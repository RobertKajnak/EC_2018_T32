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
                genotype.put("stepSize", new Double(1.));

            if (individualDescriptor.contains("stepSizes")) {
                Double[] stepSizes = new Double[10];
                for (int j=0; j<10; j++) 
                    stepSizes[j] = new Double(1.);
                genotype.put("stepSizes", stepSizes);
            }

            if (individualDescriptor.contains("alphas")) {
                Double[][] alphas = new Double[10][10];
                for (int j=0; j<10; j++) 
                    for (int k=0; k<10; k++)
                        alphas[j][k] = new Double(0.0);
                genotype.put("alphas", alphas);
            }
            
            this.population.add(new Individual(this.evaluation, genotype));
        }
    }

    public void evolve() throws NotEnoughEvaluationsException {
        this.parents_ids    = this.selectParents_ids();
        this.offspring  = this.reproduce(this.parents_ids);
        // this.offspring  = this.recombine(this.parents);
        // this.offspring  = this.mutate(this.offspring);
        this.population = this.selectSurvivors(this.population, this.offspring);
    }

    private ArrayList<Integer> selectParents_ids() throws NotEnoughEvaluationsException {
        
        HashMap<String, Object> params = (HashMap<String, Object>) this.parentsSelectionDescriptor.get("params");
        params.put("evaluation", this.evaluation);
        params.put("parentsRatio", this.parentsRatio);
        ArrayList<Integer> parents_ids = ((ParentsSelectionFunctionInterface) this.parentsSelectionDescriptor.get("call")).execute(this.population, params);

        return parents_ids; 
    }

    private ArrayList<Individual> reproduce(ArrayList<Integer> parents_ids) {

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
            
            // recobination
            children_genotype = this.recombine(mom, dad, recombination_params);
            
            // mutation
            HashMap<String, Object> child_1_genotype = this.mutate(children_genotype.first(), mutation_params);
            HashMap<String, Object> child_2_genotype = this.mutate(children_genotype.second(), mutation_params);

            Individual child_1 = new Individual(this.evaluation, child_1_genotype);
            Individual child_2 = new Individual(this.evaluation, child_2_genotype);

            if (this.apply_crowding) {
                // compute distances
                Double mom_c1 = this.compute_distance((Double[])mom.getGenotype().get("coords"), (Double[])child_1_genotype.get("coords"));
                Double mom_c2 = this.compute_distance((Double[])mom.getGenotype().get("coords"), (Double[])child_2_genotype.get("coords"));
                Double dad_c1 = this.compute_distance((Double[])dad.getGenotype().get("coords"), (Double[])child_1_genotype.get("coords"));
                Double dad_c2 = this.compute_distance((Double[])dad.getGenotype().get("coords"), (Double[])child_2_genotype.get("coords"));

                if (mom_c1 + dad_c2 <= mom_c2 + dad_c1) {
                    if (child_1.getFitness() > mom.getFitness()) {
                        this.offspring.add(child_1);
                        this.population.remove(mom_id);
                    }
                    if (child_2.getFitness() > dad.getFitness()) {
                        this.offspring.add(child_2);
                        this.population.remove(dad_id);
                    }
                }
                else {
                    if (child_1.getFitness() > dad.getFitness()) {
                        this.offspring.add(child_1);
                        this.population.remove(dad_id);
                    }
                    if (child_2.getFitness() > mom.getFitness()) {
                        this.offspring.add(child_2);
                        this.population.remove(mom_id);
                    }
                }
            }
            else {
                offspring.add(child_1);
                offspring.add(child_2);
            }
        }

        return offspring;
    }

    private Pair< HashMap<String, Object>, HashMap<String, Object> > recombine(Individual mom, Individual dad, HashMap<String, Object> params) {
        params.put("evaluation", this.evaluation);
        return ((RecombinationFunctionInterface)this.recombinationDescriptor.get("call")).execute(mom, dad, params);
    }

    private HashMap<String, Object> mutate(HashMap<String, Object> genotype, HashMap<String, Object> params) {
        params.put("evaluation", this.evaluation);
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

        // I do not compute the sqrt, since it is a monotonically crescent function.
        for (int i=0; i<10; i++) dist += Math.pow(coords_1[i] - coords_2[i], 2);

        return dist;
    }
}