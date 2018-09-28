import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class EA {
    private CompetitionCustomPack evaluation;

    private HashMap<String, Object> EAParams;
    private HashMap<String, Object> recombinationDescriptor;
    private HashMap<String, Object> mutationDescriptor;
    private ArrayList<String> individualDescriptor;

    private Integer populationSize;      // number of individuals
    private Double mutationRate;         // percentage of mutants
    private Double parentsRatio;         // percentage of individual that becomes a parent
    private Double parentsSurvivalRatio; // percentage of parents that survive after replacement

    private ArrayList<Individual> population;
    private ArrayList<Individual> parents;
    private ArrayList<Individual> offspring;
    
    private Random RNG;
    private Individual previousBest;

    public EA(CompetitionCustomPack evaluation, HashMap<String, Object> EAParams, HashMap<String, Object> recombinationDescriptor, HashMap<String, Object> mutationDescriptor, ArrayList<String> individualDescriptor) {
        this.evaluation = evaluation;

        // Global EA parameters
        this.EAParams = EAParams;
    	this.populationSize = (Integer) EAParams.get("populationSize");
        this.mutationRate = (Double) EAParams.get("mutationRate");
        this.parentsRatio = (Double) EAParams.get("parentsRatio");
        this.parentsSurvivalRatio = (Double) EAParams.get("parentsSurvivalRatio");

        this.recombinationDescriptor = recombinationDescriptor;
        this.mutationDescriptor = mutationDescriptor;
        this.individualDescriptor = individualDescriptor; 
        
        // checks
        this.parentsRatio = this.parentsRatio > 1 ? 1 : this.parentsRatio < 0 ? 0 : this.parentsRatio;
        this.parentsSurvivalRatio = this.parentsSurvivalRatio > 1 ? 1 : this.parentsSurvivalRatio < 0 ? 0 : this.parentsSurvivalRatio;

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
        this.population = this.selectSurvivors(this.parents, this.offspring);
    }

    private ArrayList<Individual> selectParents() throws NotEnoughEvaluationsException {
        this.sortByFitness();
        int numParents = (int) (this.populationSize * this.parentsRatio);
        ArrayList<Individual> parents = new ArrayList<Individual>(this.population.subList(0, numParents));

        return parents; 
    }

    /*
    *  Operators implemented:
    *      - One point crossover
    */
    private ArrayList<Individual> recombine(ArrayList<Individual> parents) {
        offspring = new ArrayList<Individual>();
        
        int numChildren = (int) (this.populationSize * this.parentsRatio);
        for (int i=0; i<numChildren; i++) {
            Individual mom = parents.get(this.RNG.nextInt(numChildren));
            Individual dad = parents.get(this.RNG.nextInt(numChildren));
            
            Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotype = ((RecombinationFunctionInterface)this.recombinationDescriptor.get("call")).execute(mom, dad);
            // Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotype = Recombinator.onePointCrossover(mom, dad);

            offspring.add(new Individual(this.evaluation, offspringGenotype.first()));
            offspring.add(new Individual(this.evaluation, offspringGenotype.second()));
        }

        return offspring;
    }

    /*
    *  Operators implemented:
    *      - Uniform Mutation
    *      - Gaussina Mutation
    *      - Uncorrelated 1 step size Mutation
    *      - Uncorrelated N step sizes Mutation
    *      - Correlated N step sizes Mutation - Not working due to a bug (SecurityException: Attempting to create a class loader!)
    */
    private ArrayList<Individual> mutate(ArrayList<Individual> offspring) {
        for (int i=0; i<offspring.size(); i++) {
            HashMap<String, Object> genotype = offspring.get(i).getGenotype();
            HashMap<String, Object> params = (HashMap<String, Object>) this.mutationDescriptor.get("params");
            HashMap<String, Object> mutatedGenotype = ((MutationFunctionInterface)this.mutationDescriptor.get("call")).execute(genotype, params);
            // HashMap<String, Object> mutatedGenotype = Mutator.gaussian(genotype);
            Individual mutant = new Individual(this.evaluation, mutatedGenotype);
            offspring.set(i, mutant);
        }
        
        return offspring;
    }

    private ArrayList<Individual> selectSurvivors(ArrayList<Individual> parents, ArrayList<Individual> offspring) throws NotEnoughEvaluationsException {
        // sortByFitness(); // there's no need to sort it again. You already sorted it when you selected parents. - Giuseppe
        this.population = new ArrayList<Individual>(parents);
        this.population.addAll(offspring);
        
        ///If there are too many ppl cut the worst performing of
        if (this.population.size() > this.populationSize){
        	this.sortByFitness();
            this.population = Selector.selectFirst(this.population, this.populationSize);
        }
        else if (this.population.size() < populationSize) {
            /// Otherwise fill the population with random new ppl
            fillEmptyIndividualSlots(this.individualDescriptor);
        }

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

    ///Wrapper function for sorting
    private void sortByFitness() throws NotEnoughEvaluationsException {
    	///I tried going through the list and checking if it is already sorted, but it only increases execution time
    	int cost=0;
    	for (Individual ind : population) {
    		if (!ind.isEvaluated()) {
    			cost++;
    		}
    	}
    	if (cost>evaluation.evaluationsRemaining())
    		throw new NotEnoughEvaluationsException();
        Collections.sort(this.population);
    }
}