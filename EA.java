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

    private ArrayList<Individual> population;
    private ArrayList<Individual> parents;
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
                genotype.put("stepSize", new Double(0.));

            if (individualDescriptor.contains("stepSizes")) {
                Double[] stepSizes = new Double[10];
                for (int j=0; j<10; j++) 
                    stepSizes[j] = new Double(0.);
                genotype.put("stepSizes", stepSizes);
            }

            if (individualDescriptor.contains("alphas")) {
                Double[][] alphas = new Double[10][10];
                for (int j=0; j<10; j++) 
                    for (int k=0; k<10; k++)
                        alphas[j][k] = new Double(0.);
                genotype.put("alphas", alphas);
            }
            
            this.population.add(new Individual(this.evaluation, genotype));
        }
    }

    public void evolve() throws NotEnoughEvaluationsException {
        this.parents    = this.selectParents();
        this.offspring  = this.recombine(this.parents);
        this.offspring  = this.mutate(this.offspring);
        this.population = this.selectSurvivors(this.population, this.offspring);
    }

    private ArrayList<Individual> selectParents() throws NotEnoughEvaluationsException {
        
        HashMap<String, Object> params = (HashMap<String, Object>) this.parentsSelectionDescriptor.get("params");
        params.put("evaluation", this.evaluation);
        ArrayList<Individual> parents = ((ParentsSelectionFunctionInterface) this.parentsSelectionDescriptor.get("call")).execute(this.population, params);

        return parents; 
    }

    private ArrayList<Individual> recombine(ArrayList<Individual> parents) {
        offspring = new ArrayList<Individual>();
        
        for (int i=0; i<this.offspringSize; i++) {
            Individual mom = parents.get(this.RNG.nextInt(parents.size()));
            Individual dad = parents.get(this.RNG.nextInt(parents.size()));
            
            @SuppressWarnings("unchecked")
            HashMap<String, Object> params = (HashMap<String, Object>) this.recombinationDescriptor.get("params");
            params.put("evaluation", this.evaluation);
            Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotype = ((RecombinationFunctionInterface)this.recombinationDescriptor.get("call")).execute(mom, dad, params);
            // Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotype = Recombinator.onePointCrossover(mom, dad);

            offspring.add(new Individual(this.evaluation, offspringGenotype.first()));
            offspring.add(new Individual(this.evaluation, offspringGenotype.second()));
        }

        return offspring;
    }

    private ArrayList<Individual> mutate(ArrayList<Individual> offspring) {
        for (int i=0; i<offspring.size(); i++) {
            HashMap<String, Object> genotype = offspring.get(i).getGenotype();

            @SuppressWarnings("unchecked")
            HashMap<String, Object> params = (HashMap<String, Object>) this.mutationDescriptor.get("params");
            params.put("evaluation", this.evaluation);
            HashMap<String, Object> mutatedGenotype = ((MutationFunctionInterface)this.mutationDescriptor.get("call")).execute(genotype, params);
            // HashMap<String, Object> mutatedGenotype = Mutator.gaussian(genotype);
            Individual mutant = new Individual(this.evaluation, mutatedGenotype);
            offspring.set(i, mutant);
        }
        
        return offspring;
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
}