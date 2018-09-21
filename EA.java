import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class EA {
    private int populationSize;
    private ArrayList<Individual> population;
    private ArrayList<Individual> parents;
    private ArrayList<Individual> offspring;
    private double mutationRate;
    private double mutationStepSize;
    private double parentsRatio; // percentage of individual that becomes a parent
    private double parentsSurvivalRatio; // percentage of parents that survive after replacement
    private Random RNG;
    private     CompetitionCustomPack evaluation; //
    private Individual previousBest;

    public EA(CompetitionCustomPack evaluation,int populationSize, double mutationRate, double mutationStepSize, double parentsRatio, double parentsSurvivalRatio) {
        this.evaluation = evaluation;
    	this.populationSize = populationSize;
        this.population = new ArrayList<Individual>(populationSize);
        this.mutationRate = mutationRate;
        this.mutationStepSize = mutationStepSize;
        this.parentsRatio = parentsRatio > 1 ? 1 : parentsRatio < 0 ? 0 : parentsRatio;
        this.parentsSurvivalRatio = parentsSurvivalRatio > 1 ? 1 : parentsSurvivalRatio < 0 ? 0 : parentsSurvivalRatio;
        previousBest = null;
        
        this.RNG = new Random();
        //this.isSorted = false;

        this.fillEmptyIndividualSlots();
    }

    public void setMutationRate(double value) {
        this.mutationRate = value;
    }

    public double getMutationRate() {
        return this.mutationRate;
    }

    public void setMutationStepSize(double value) {
        this.mutationRate = value;
    }

    public double getMutationStepSize() {
        return this.mutationStepSize;
    }

    public ArrayList<Individual> getPopulation() {
        return this.population;
    }

    // fills in empty slots in the population with individuals
    private void fillEmptyIndividualSlots() {
        for (int i=this.population.size(); i<this.populationSize; i++) {

            double[] rndCoords = new double[10];
            for (int j=0; j<10; j++) 
                rndCoords[j] = this.RNG.nextDouble()*10 - 5;

            this.population.add(new Individual(evaluation,rndCoords));
        }
    }

    public void evolve() throws NotEnoughEvaluationsException {
        this.parents    = this.selectParents();
        this.offspring  = this.recombine(this.parents);
        this.offspring  = this.mutate(this.offspring);
        this.population = this.selectSurvivors(this.parents, this.offspring);
    }

    private ArrayList<Individual> selectParents() throws NotEnoughEvaluationsException {
        /*
            Parents = the first K best individuals.

            Available selection operators in Selector class
        */

        this.sortByFitness();
        int numParents = (int) (this.populationSize * this.parentsRatio);
        ArrayList<Individual> parents = new ArrayList<Individual>(this.population.subList(0, numParents));

        return parents; 
    }

    private ArrayList<Individual> recombine(ArrayList<Individual> parents) {
        /*
            ### OLD VERSION ###
            Since parents are sorted by fitness, 
            coupling them sequencially means to couple
            the best one with the second-best and the 
            third-best with the fourth and so on. 
            Therefore, I shuffle the parents' array to 
            break this simmetry.

            Collections.shuffle(parents);

            ### NEW VERSION ###
            parents are picked randomly for each child.
            The number of child is equal to the number of parents.

            Available crossover operators in Recombinator class
        */

        offspring = new ArrayList<Individual>();
        
        int numChildren = (int) (this.populationSize * this.parentsRatio);
        for (int i=0; i<numChildren; i++) {
            Individual mom = parents.get(this.RNG.nextInt(numChildren));
            Individual dad = parents.get(this.RNG.nextInt(numChildren));

            Pair<double[], double[]> offspring_coords = Recombinator.onePointCrossover(mom, dad);

            offspring.add(new Individual(this.evaluation, offspring_coords.first()));
            offspring.add(new Individual(this.evaluation, offspring_coords.second()));
        }

        return offspring;
    }

    private ArrayList<Individual> mutate(ArrayList<Individual> offspring) {

        /*
            Available mutation oparators in Mutator class
        */

        for (int i=0; i<offspring.size(); i++) {
            double[] indCoords = offspring.get(i).getCoords();
            double[] mutatedIndCoords = Mutator.gaussianMutation(indCoords, this.mutationRate, this.mutationStepSize);
            Individual mutatedInd = new Individual(this.evaluation, mutatedIndCoords);

            offspring.set(i, mutatedInd);
        }
        
        return offspring;
    }

    private ArrayList<Individual> selectSurvivors(ArrayList<Individual> parents, ArrayList<Individual> offspring) throws NotEnoughEvaluationsException {
        /*
            New population consists of the best K among parents+offspring,
            where K = population size.

            Available selection operators in Selector class
        */

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
            fillEmptyIndividualSlots();
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