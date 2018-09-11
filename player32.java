import org.vu.contest.ContestSubmission;

import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class player32 implements ContestSubmission
{
	Random rnd_;
	ContestEvaluation evaluation_;
    private int evaluations_limit_;
	
	public player32()
	{
		rnd_ = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		evaluation_ = evaluation;
		
		// Get evaluation properties
		Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }
	
    
	public void run()
	{
		// Run your algorithm here
        System.out.println("Evaluations Limit:" + evaluations_limit_);
        int evals = 0;
        // init population
        Population population = new Population(300, 0.03, 0.5,.7,.15);
        // calculate fitness
        while(evals<evaluations_limit_){
            // Select parents
            // Apply crossover / mutation operators
        	population.mutate();
        	population.reproduce();
            double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
            // Check fitness of unknown fuction
            //Double fitness = (double) evaluation_.evaluate(child);
            System.out.println("Best individual in generation evals: " +population.getPrimeIndividual().fitness);
            
            evals++;
            // Select survivors
        }

	}
	class Pair<T1,T2>{
		public final T1 object1;
		public final T2 object2;
		public Pair(T1 object1, T2 object2) {
			this.object1= object1;
			this.object2 = object2;
		}
	}
	
	class Population{
		Random RNG;

		private double mutationRate;//double representing the chance[0,1]
		private double mutationSwing;//the amount of change that can happen
		private int populationSize;
		private double populationPercentageThatReproduces;
		private double percentageOfParentsThatSurvive;
		boolean sorted;///TODO this screams for someone to screw this up. Need better approach, without consnatly sorting
		
		ArrayList<Individual> individuals;
		
		public Population(int populationSize,double mutationRate,double mutationSwing, double populationPercentageThatReproduces,double percentageOfParentsThatSurvive) {
			RNG = new Random();
			individuals = new ArrayList<Individual>();
			for (int i=0;i<populationSize;i++) {
				individuals.add(new Individual(this));
			}
			this.sorted = false;
			
			this.mutationRate = mutationRate;
			this.mutationSwing = mutationSwing;
			//this is just to fuck with you, if you don't have autocomplete :P
			this.populationSize = populationSize;
			this.populationPercentageThatReproduces 
				=populationPercentageThatReproduces>1?1:populationPercentageThatReproduces<0?0:populationPercentageThatReproduces;
			this.percentageOfParentsThatSurvive=
					percentageOfParentsThatSurvive>1?1:percentageOfParentsThatSurvive<0?0:percentageOfParentsThatSurvive;
		}
		
		public Individual getPrimeIndividual() {
			if (!sorted)
				sortByFitness();

			return individuals.get(0);
		}
		
		///Wrapper function for sorting
		private void sortByFitness() {
			Collections.sort(individuals);
		}
		
		///Make all individuals roll the dice of mutation
		///not the most elegant, but keeps the immutability fromt he individual. For reasoons, read that comment
		public void mutate() {
			ArrayList<Individual> toRemove = new ArrayList<Individual>();
			ArrayList<Individual> toAdd = new ArrayList<Individual>();
			
			
			///Goes through all individuals, if there is a mutation, it  adds it to a list
			for (Individual ind : individuals) {
				Individual newInd = ind.mutate();
				if (!newInd.equals(ind)) {
					toRemove.remove(ind);
					toAdd.add(newInd);
				}
			}
			
			///Tackling lists from previous cycle
			for (Individual ind : toRemove) {
				individuals.remove(ind);
			}
			for (Individual ind : toAdd) {
				individuals.add(ind);
			}
			sorted = false;
		}
		
		///Make fittest individuals reproduce and keep best parents. Any excess inidividuals are killed, in order of fitness.
		///NOTE: NEEDS to sorts the population twice, if the nr_parents+nr_survivors>pop_size. 
		////TODO Do something about the double sort, for both cases. Actually, since it doesn't invoke evaluate, it's not  that horrible
		public void reproduce() {
			if (!sorted)
				sortByFitness();
			ArrayList<Individual> newPopulation= new ArrayList<Individual>();
			
			int goodParent = (int) (populationSize*populationPercentageThatReproduces);
			int goodAncestors = (int)(populationSize * percentageOfParentsThatSurvive);
			
			for (int i=0;i<goodAncestors;i++) {
				newPopulation.add(individuals.get(i));
			}
			
			///TODO for now the individuals reproduce in pairs. A better pairing technique would be better
			Pair<Individual,Individual> pair;
			for (int i=0;i<goodParent;i+=2) {
				pair = individuals.get(i).mate(individuals.get(i+1));
				newPopulation.add(pair.object1);
				newPopulation.add(pair.object2);
			}
			
			int newPopSize = newPopulation.size();
			if (newPopSize>populationSize) {
				Collections.sort(individuals);
				for(int i=0;i<newPopSize-populationSize;i++) {
					newPopulation.remove(newPopSize-i-1);
				}
			}
			else {
				///Edge case taken care by for stop condition
				for (int i=0;i<populationSize;i++) {
					newPopulation.add(new Individual(this));
				}
				sortByFitness();
			}
			sorted = true;
		}
		
		///I made it immutable for the time being for simplicity. Also, it's a nice thing to have :P
		///On a more serious note, if the fitness and coord are final, it means that it is not possible 
		///to accidentally change one, while the other remains unchanged
		class Individual implements Comparable<Individual>{
			public final double coords[];
			public final double fitness;
			///Used to read relevant config such as mutation rate
			private final Population population;
			
			///Initializes a random individual
			public Individual(Population hostPopulation) {
				coords = new double[10];
				for (int i=0;i<10;i++) {
					coords[i] = RNG.nextDouble()*10-5;
				}
				fitness = (double) evaluation_.evaluate(coords);
				
				this.population = hostPopulation;
			}
			
			///Initializes individual using the provided coordinates
			public Individual(Population hostPopulation, double[] coords) {
				this.coords =coords;
				fitness = (double) evaluation_.evaluate(coords);
				this.population = hostPopulation;
			}
			
			///Creates two new children based on two individuals given, using crossover
			public Pair<Individual,Individual> mate(Individual individual) {
				RNG.nextInt(10);
				double offspring1[] = new double[10];
				double offspring2[] = new double[10];

				//At least 1 gene splice
				int crossoverPoint = 1+RNG.nextInt(8);
				for (int i=0;i<10;i++) {
					if (i<crossoverPoint){
						offspring1[i]= this.coords[i];
						offspring2[i]= individual.coords[i];
					}
					else {
						offspring1[i]= individual.coords[i];
						offspring2[i]= this.coords[i];
					}
				}
				
				return new Pair<Individual, Individual>(new Individual(population,offspring1), new Individual(population,offspring2));
			}
			
			///creates a new mutated individual, based on itself. Rates are taken from the host population
			public Individual mutate() {
				double newMe[] = new double[10];
				for (int i=0;i<10;i++) {
					if (RNG.nextDouble()<population.mutationRate) {
						newMe[i] = coords[i] + RNG.nextDouble()*population.mutationSwing;
					}
					else {
						newMe[i]=coords[i];
					}
				}
				
				if (Arrays.equals(newMe,coords))
					return this;
				else
					return new Individual(population,newMe);
			}
			
			@Override
			public boolean equals(Object obj) {
				///TODO add class check
				for (int i=0;i<10;i++) {
					if (((Individual)obj).coords[i]!=this.coords[i])
						return false;
				}
				return true;
			}

			
			@Override
			/// both the same fitness=>0
			/// i.fitness<this.fitness => -1
			/// i.fitness>this.fitness => 1
			public int compareTo(Individual i) {
				return this.fitness==i.fitness?0:i.fitness>this.fitness?1:-1;
			}
		
			
		}
		
		
	
	}
}
