import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class EA {
    private CompetitionCustomPack evaluation;

    private HashMap<String, Object> EAParams;
    private HashMap<String, Object> parentsSelectionDescriptor;
    private HashMap<String, Object> recombinationDescriptor;
    private HashMap<String, Object> mutationDescriptor;
    private HashMap<String, Object> survivorSelectionDescriptor;
    private ArrayList<String> individualDescriptor;

    private Integer populationSize;      // number of individuals
    private Integer offspringSize;       // number of children
    private Double mutationRate;         // percentage of mutants
    private Double parentsRatio;         // percentage of parents
    private Boolean apply_crowding;

    private ArrayList<Individual> population;
    private ArrayList<Integer> parents_ids;
    private ArrayList<Individual> offspring;
    
    private Random RNG;
    private Individual previousBest;

    public EA(
        CompetitionCustomPack evaluation, 
        HashMap<String, Object> EAParams, 
        HashMap<String, Object> parentsSelectionDescriptor,
        HashMap<String, Object> recombinationDescriptor, 
        HashMap<String, Object> mutationDescriptor, 
        HashMap<String, Object> survivorSelectionDescriptor,
        ArrayList<String> individualDescriptor) {

        this.evaluation = evaluation;

        // Global EA parameters
        this.EAParams = EAParams;
        this.populationSize = (Integer) EAParams.get("populationSize");
        this.offspringSize = (Integer) EAParams.get("offspringSize");
        this.mutationRate = (Double) EAParams.get("mutationRate");
        this.parentsRatio = (Double) EAParams.get("parentsRatio");
        this.apply_crowding = (Boolean) EAParams.get("apply_crowding");
        
        this.parentsSelectionDescriptor = parentsSelectionDescriptor;
        this.recombinationDescriptor = recombinationDescriptor;
        this.mutationDescriptor = mutationDescriptor;
        this.survivorSelectionDescriptor = survivorSelectionDescriptor;
        this.individualDescriptor = individualDescriptor; 

        // helpers
        this.previousBest = null;
        this.RNG = new Random();

        // initialize population
        this.population = new ArrayList<Individual>(this.populationSize);
        this.fillEmptyIndividualSlots(individualDescriptor);
    }

    public ArrayList<Individual> getPopulation() {
        return this.population;
    }

    // fills in empty slots in the population with individuals
    private void fillEmptyIndividualSlots(ArrayList<String> individualDescriptor) {
        for (int i=this.population.size(); i<this.populationSize; i++) {

            HashMap<String, Object> genotype = new HashMap<String, Object>();

            // did not found a more scalable way - Giuseppe
            if (individualDescriptor.contains("coords")) {
                Double[] coords = new Double[10];
                for (int j=0; j<10; j++) 
                    coords[j] = this.RNG.nextDouble()*10 - 5;
                genotype.put("coords", coords);
            }

            if (individualDescriptor.contains("stepSize"))
                genotype.put("stepSize", new Double(this.RNG.nextDouble()));

            if (individualDescriptor.contains("stepSizes")) {
                Double[] stepSizes = new Double[10];
                for (int j=0; j<10; j++) 
                    stepSizes[j] = new Double(this.RNG.nextDouble());
                genotype.put("stepSizes", stepSizes);
            }

            if (individualDescriptor.contains("alphas")) {
                Double[][] alphas = new Double[10][10];
                for (int j=0; j<10; j++) 
                    for (int k=0; k<10; k++)
                        alphas[j][k] = new Double(this.RNG.nextDouble());
                genotype.put("alphas", alphas);
            }
            
            this.population.add(new Individual(this.evaluation, genotype));
        }

        // DEBUG
        // System.out.printf("Number of Individuals: %d\nGenotype: ", this.population.size());
        // System.out.println(this.population.get(this.RNG.nextInt(this.population.size())).getGenotype());
        // System.out.println(java.util.Arrays.toString((Double[])this.population.get(this.RNG.nextInt(this.population.size())).getGenotype().get("coords")));
        // System.exit(0);
    }

    public void evolve() throws NotEnoughEvaluationsException {
        this.parents_ids  = this.selectParents_ids();
        this.offspring    = this.reproduce(this.parents_ids);
        this.population   = this.selectSurvivors(this.population, this.offspring);
    }

    private ArrayList<Integer> selectParents_ids() throws NotEnoughEvaluationsException {
        
        HashMap<String, Object> params = (HashMap<String, Object>) this.parentsSelectionDescriptor.get("params");
        params.put("evaluation", this.evaluation);
        params.put("parentsRatio", this.parentsRatio);
        ArrayList<Integer> parents_ids = ((ParentsSelectionFunctionInterface) this.parentsSelectionDescriptor.get("call")).execute(this.population, params);

        return parents_ids; 
    }

    private ArrayList<Individual> reproduce(ArrayList<Integer> parents_ids) throws NotEnoughEvaluationsException {

        @SuppressWarnings("unchecked")
        HashMap<String, Object> recombination_params = (HashMap<String, Object>) this.recombinationDescriptor.get("params");
        @SuppressWarnings("unchecked")
        HashMap<String, Object> mutation_params = (HashMap<String, Object>) this.mutationDescriptor.get("params");

        this.offspring = new ArrayList<Individual>();
        Pair< HashMap<String, Object>, HashMap<String, Object> > children_genotype;

        for (int i=0; i<this.offspringSize; i++) {
            Integer mom_id = this.RNG.nextInt(parents_ids.size());
            Integer dad_id = this.RNG.nextInt(parents_ids.size());
            Individual mom = this.population.get(parents_ids.get(mom_id));
            Individual dad = this.population.get(parents_ids.get(dad_id));
            
            // recombination
            children_genotype = this.recombine(mom, dad, recombination_params);
            
            // mutation
            HashMap<String, Object> child_1_genotype = this.mutate(children_genotype.first(), mutation_params);
            HashMap<String, Object> child_2_genotype = this.mutate(children_genotype.second(), mutation_params);

            Individual child_1 = new Individual(this.evaluation, child_1_genotype);
            Individual child_2 = new Individual(this.evaluation, child_2_genotype);

            offspring.add(child_1);
            offspring.add(child_2);
        }

        return offspring;
    }

    private Pair< HashMap<String, Object>, HashMap<String, Object> > recombine(Individual mom, Individual dad, HashMap<String, Object> params) {
        params.put("evaluation", this.evaluation);
        return ((RecombinationFunctionInterface)this.recombinationDescriptor.get("call")).execute(mom, dad, params);
    }

    private HashMap<String, Object> mutate(HashMap<String, Object> genotype, HashMap<String, Object> params) {
        params.put("evaluation", this.evaluation);
        params.put("mutationRate", this.mutationRate);
        return ((MutationFunctionInterface)this.mutationDescriptor.get("call")).execute(genotype, params);
    }

    private ArrayList<Individual> selectSurvivors(ArrayList<Individual> population, ArrayList<Individual> offspring) throws NotEnoughEvaluationsException {
        HashMap<String, Object> params = (HashMap<String, Object>) this.survivorSelectionDescriptor.get("params");
        params.put("evaluation", this.evaluation);
        this.population = ((SurvivorSelectionFunctionInterface) this.survivorSelectionDescriptor.get("call")).execute(population, offspring, params);
        return this.population;
    }

    public Individual getBestIndividual() {
        try {
			this.sortByFitness();        
			previousBest = this.population.get(0);
	        return previousBest;
		} catch (NotEnoughEvaluationsException e) {
			return previousBest;
		}
    }

    public ArrayList<Individual> getImmigrants(Integer num_of_immigrant) {
        try {
			this.sortByFitness();        
		} catch (NotEnoughEvaluationsException e) {
            ;
        }
        // ArrayList<Individual> migrants = new ArrayList<Individual>();
        // for (int i=0; i<num_of_immigrant; i++) {
        //     migrants.add(this.population.get(this.RNG.nextInt((int) (0.25 * this.population.size()))));
        // }
        return new ArrayList<Individual>(this.population.subList(0, num_of_immigrant));
        // return migrants;
    }

    public void host(ArrayList<Individual> immigrants) {
        try {
            this.sortByFitness();
            for (int i=1; i<=immigrants.size(); i++)         
			    this.population.set(this.population.size()-i, immigrants.get(i-1));
		} catch (NotEnoughEvaluationsException e) {
            ;
		}
    }

    private void sortByFitness() throws NotEnoughEvaluationsException {
        int cost=0;
        for (Individual ind : this.population) {
            if (!ind.isEvaluated()) {
                cost++;
            }
        }
        if (cost>this.evaluation.evaluationsRemaining())
            throw new NotEnoughEvaluationsException();
        Collections.sort(this.population);
    }

    private Double compute_distance(Double[] coords_1, Double[] coords_2) {
        Double dist = 0.; 
        for (int i=0; i<10; i++) dist += Math.sqrt(Math.pow(coords_1[i] - coords_2[i], 2));
        return dist;
    }

    public double computeDiversity() {
        // use euclidean distance. Here performance are not a problem.
        double diversity = 0;
        for (Individual I_1 : this.population) {
            for (Individual I_2 : this.population) {
                diversity += this.compute_distance((Double[])I_1.getGenotype().get("coords"), (Double[])I_2.getGenotype().get("coords"));
            }
        }
        return diversity;
    }
}