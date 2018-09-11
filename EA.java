import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class EA {
    private static int populationSize;
    private ArrayList<Individual> population;
    private double mutationRate;
    private double mutationSwing;
    private double parentsRatio; // percentage of individual that becomes a parent
    private double parentsSurvivalRatio; // percentage of parents that survive after replacement
    private Random RNG;
    
    // helper, to remove - we need high performance!
    private Boolean sorted;

    public EA(int populationSize, double mutationRate, double mutatationSwing, double parentsRatio, double parentsSurvivalRatio) {
        this.populationSize = populationSize;
        this.population = new ArrayList<Individual>(populationSize);
        this.mutationRate = mutationRate;
        this.mutationSwing = mutationSwing;
        this.parentsRatio = parentsRatio > 1 ? 1 : parentsRatio < 0 ? 0 : parentsSurvivalRatio;
        this.parentsSurvivalRatio = parentsSurvivalRatio > 1 ? 1 : parentsSurvivalRatio < 0 ? 0 : parentsSurvivalRatio;

        this.RNG = new Random();
        this.sorted = false;
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

    // for each individual, randomly sets its coordinates.
    public ArrayList<Individual> initialize() {
        for (int i=0; i<this.populationSize; i++) {

            double[] rndCoords = new double[10];
            for (int j=0; j<10; j++) 
                rndCoords[j] = this.RNG.nextDouble()*10 - 5;

            this.population.add(new Individual(rndCoords));
        }

        return this.population;
    }

    public void evaluateFitness(ContestEvaluation evaluationOperator) {
        for (Individual individual: this.population) {
            double fitness = (double) evaluationOperator.evaluate(individual.getCoords());
            individual.setFitness(fitness);
        }
    }

    ///creates a new mutated individual, based on itself. Rates are taken from the host population
    private ArrayList<Individual> applyMutation(ArrayList<Individual> parents) {

        for (int i=0; i<parents.size(); i++) {

            double[] coords = parents.get(i).getCoords();
            double[] mutatedCoords = new double[10]; 

            for (int j=0;j<10;j++) {
                if (this.RNG.nextDouble() < this.mutationRate) {
                    mutatedCoords[j] = coords[j] + this.RNG.nextDouble() * this.mutationSwing;
                }
                else {
                    mutatedCoords[j] = coords[j];
                }
            }

            parents.set(i, new Individual(mutatedCoords));
        }

        return parents;
    }

    ///Creates two new children based on two individuals given, using crossover
    private Pair<Individual,Individual> applyCrossover(Individual parent_1, Individual parent_2) {

        this.RNG.nextInt(10); // What is this line for?

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

        Individual child_1 = new Individual(childCoords_1);
        Individual child_2 = new Individual(childCoords_2);

        Pair<Individual, Individual> offspring = new Pair<Individual, Individual>(child_1, child_2);
        
        return offspring;
    }

    private ArrayList<Individual> selectParents(int numParents) {

        if (!sorted) sortByFitness();
        System.out.println(this.population.toString());

        ArrayList<Individual> parents = new ArrayList<Individual>(this.population.subList(0, numParents));

        return parents;
    }

    private void applyReplacement() {
        // Sort again :( the population --> !!! PERFORMANCE !!!
        this.sortByFitness();
        // Keep constant the number of Individual inside the population

        this.population = new ArrayList<Individual>(this.population.subList(0, this.populationSize));
    }

    ///Make fittest individuals reproduce and keep best parents. Any excess inidividuals are killed, in order of fitness.
    ///NOTE: NEEDS to sorts the population twice, if the nr_parents+nr_survivors>pop_size. 
    ////TODO Do something about the double sort, for both cases. Actually, since it doesn't invoke evaluate, it's not  that horrible
    public void reproduce() {

        int numParents = (int) (this.populationSize * this.parentsRatio);
        ArrayList<Individual> parents = this.selectParents(numParents);
        
        // mutate parents
        parents = this.applyMutation(parents);
        
        // Since parents are sorted by fitness, 
        // coupling them sequencially means to couple
        // the best one with the second-best and the 
        // third-best with the fourth and so on. 
        // Therefore, I shuffle the parents' array to 
        // break this simmetry.

        Collections.shuffle(parents);

        // Then, the approach of Robert is fine, provided that mod(numParents, 2) = 0.
        // If mod(numParents, 2) != 0, then a parent does not contribute to reproduction.
        for (int i=0; i<numParents-1; i++) {
            Individual parent_1 = parents.get(i);
            Individual parent_2 = parents.get(i+1);
            Pair offspring = applyCrossover(parent_1, parent_2);
            this.population.add((Individual) offspring.first());
            this.population.add((Individual) offspring.second());
        }

        this.applyReplacement();
    }

    /// !!! PERFORMANCE HERE DROPS DOWN !!! ///
    public Individual getBestIndividual() {
        if (!this.sorted)
            this.sortByFitness();

        return this.population.get(0);
    }

    ///Wrapper function for sorting
    private void sortByFitness() {
        Collections.sort(this.population);
        this.sorted = true;
    }

}