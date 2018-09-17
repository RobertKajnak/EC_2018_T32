import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class EA {
    private int populationSize; ///@Guiseppe: Why did you make this single attribute static?
    private ArrayList<Individual> population;
    private double mutationRate;
    private double mutationSwing;
    private double parentsRatio; // percentage of individual that becomes a parent
    private double parentsSurvivalRatio; // percentage of parents that survive after replacement
    private Random RNG;
    CompetitionCustomPack evaluation;
    private Individual previousBest;
    // helper, to remove - we need high performance! -- umm we don't really, the time limit is 12000ms (I double-checked) 
    // and this does it under 150ms. "Premature optimization is the root of all evil" - Robert. I mean the note, I am not quoting myself :P
    // Also, you already made a mistake because of this in select parents -- you weren't setting this flag
    //private Boolean isSorted;

    public EA(CompetitionCustomPack evaluation,int populationSize, double mutationRate, double mutationSwing, double parentsRatio, double parentsSurvivalRatio) {
        this.evaluation = evaluation;
    	this.populationSize = populationSize;
        this.population = new ArrayList<Individual>(populationSize);
        this.mutationRate = mutationRate;
        this.mutationSwing = mutationSwing;
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

    public void setMutationSwing(double value) {
        this.mutationRate = value;
    }

    public double getMutationSwing() {
        return this.mutationSwing;
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

    ///creates a new mutated individual, based on itself. Rates are taken from the host population
    private Individual applyMutation(Individual child) {

        double[] coords = child.getCoords();
        double[] mutatedCoords = new double[10]; 

        for (int j=0;j<10;j++) { 
            if (this.RNG.nextDouble() < this.mutationRate) {
                mutatedCoords[j] = coords[j] + (this.RNG.nextDouble() - 0.5) * this.mutationSwing;
                mutatedCoords[j] = Math.min(5,Math.max(-5,mutatedCoords[j]));
            }
            else {
                mutatedCoords[j] = coords[j];
            }
        }

        Individual mutatedChild = new Individual(evaluation,mutatedCoords);
        
        return mutatedChild;
    }

    ///Creates two new children based on two individuals given, using crossover
    private Pair<Individual,Individual> applyCrossover(Individual parent_1, Individual parent_2) {

        double childCoords_1[] = new double[10];
        double childCoords_2[] = new double[10];

        //At least 1 gene splice
        int crossoverPoint = 1 + this.RNG.nextInt(8);

        double[] coords1 = parent_1.getCoords();
        double[] coords2 = parent_2.getCoords();

        for (int i=0;i<10;i++) {
            if (i < crossoverPoint){
                childCoords_1[i] = coords1[i];
                childCoords_2[i] = coords2[i];
            }
            else {
                childCoords_1[i] = coords2[i];
                childCoords_2[i] = coords1[i];
            }
        }

        Individual child_1 = new Individual(evaluation,childCoords_1);
        Individual child_2 = new Individual(evaluation,childCoords_2);

        Pair<Individual, Individual> offspring = new Pair<Individual, Individual>(child_1, child_2);
        
        return offspring;
    }

    ///TODO rename this based on the generation method
    @SuppressWarnings("unused")
    private Pair<Individual, Individual> genOffspring(Individual parent_1, Individual parent_2) {
        //why are you torturing us with C style indexing? :(( even_with_autocomplete_it_s_annoying
    	if (evaluation.evaluationsRemaining()<2)
    		return null;
        double fitness_1 = parent_1.getFitness();
        double fitness_2 = parent_2.getFitness(); 
        
        double weight_1 = (fitness_1 + 1e-6) / (2e-6 + fitness_1 + fitness_2);
        double weight_2 = (fitness_2 + 1e-6)/ (2e-6 + fitness_1 + fitness_2);

        // weighted average
        double[] parentCoords_1 = parent_1.getCoords();
        double[] parentCoords_2 = parent_2.getCoords();
        double[] childCoords_1 = new double[10];
        double[] childCoords_2 = new double[10];
        double weigth_mutation;

        for (int i=0; i<10; i++) {
            weigth_mutation = 1 + (-1 + 2*RNG.nextDouble()) * 0.10;
            childCoords_1[i] = parentCoords_1[i]*weight_1*weigth_mutation + parentCoords_2[i]*weight_2*weigth_mutation;
        }
        for (int i=0; i<10; i++) {
            weigth_mutation = 1 + (-1 + 2*RNG.nextDouble()) * 0.1;
            childCoords_2[i] = parentCoords_1[i]*weight_1*weigth_mutation + parentCoords_2[i]*weight_2*weigth_mutation;
        }

        Individual child_1 = new Individual(evaluation,childCoords_1);
        Individual child_2 = new Individual(evaluation,childCoords_2);

        Pair<Individual, Individual> offspring = new Pair<Individual, Individual>(child_1, child_2);
        
        return offspring;
    }

    private ArrayList<Individual> selectParents(int numParents) throws NotEnoughEvaluationsException {

        //if (!isSorted) 
        //{
        	sortByFitness();
        //	isSorted = true;
        //}
        	

        ArrayList<Individual> parents = new ArrayList<Individual>(this.population.subList(0, numParents));

        return parents;
    }

    ///Make fitest individuals reproduce and keep best parents. Any excess inidividuals are killed, in order of fitness.
    ///NOTE: NEEDS to sorts the population twice, if the nr_parents+nr_survivors>pop_size. 
    ////TODO Do something about the double sort, for both cases. Actually, since it doesn't invoke evaluate, it's not that horrible
    public void reproduce() throws NotEnoughEvaluationsException {

        //Visualizer viz = new Visualizer(); ///TODO why is this in reproduce?

        int numParents = (int) (this.populationSize * this.parentsRatio);
        
        ArrayList<Individual> parents = this.selectParents(numParents);
        
        // Since parents are sorted by fitness, 
        // coupling them sequencially means to couple
        // the best one with the second-best and the 
        // third-best with the fourth and so on. 
        // Therefore, I shuffle the parents' array to 
        // break this simmetry.

        Collections.shuffle(parents);

        // Then, the approach of Robert is fine, provided that mod(numParents, 2) = 0. 
        // If mod(numParents, 2) != 0, then a parent does not contribute to reproduction.
        // +/-1  parent doesn't matter. Popsize is kept in check separately anyway - Robert
        // System.out.println("=============================================================");

        ArrayList<Individual> childList = new ArrayList<Individual>();
        for (int i=0; i<numParents-1; i+=2) {
            Individual parent_1 = parents.get(i);
            Individual parent_2 = parents.get(i+1);

            // Pair offspring = applyCrossover(parent_1, parent_2);
            Pair<Individual, Individual> offspring = applyCrossover(parent_1, parent_2);
            //Pair<Individual, Individual> offspring = genOffspring(parent_1, parent_2);

            Individual child_1 = (Individual) offspring.first();
            Individual child_2 = (Individual) offspring.second();

            //childList.add(child_1);
            //childList.add(child_2);
            // viz.printCoords(childList);

            // mutate
            child_1 = applyMutation(child_1);
            child_2 = applyMutation(child_2);

            childList.add(child_1);
            childList.add(child_2);
        }
        
        sortByFitness();
        ///Add first the surviving parents;
        this.population = new ArrayList<Individual>(this.population.subList(0, (int)(this.parentsSurvivalRatio*this.populationSize)));
        
        ///Add the new children
        this.population.addAll(childList);
        
        ///If there are too many ppl cut the worst performing of
        if (this.population.size()>populationSize){
        	sortByFitness();
        	this.population = new ArrayList<Individual>(this.population.subList(0,this.populationSize));
        }
        ///Otherwise fill the population with random new ppl
        else if (this.population.size()<populationSize) {
        	fillEmptyIndividualSlots();
		}
    }

    /// !!! PERFORMANCE HERE DROPS DOWN !!! /// -- still not relevant
    public Individual getBestIndividual() {
        //if (!this.isSorted)
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