import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;

public class player32 implements ContestSubmission
{
	Random rnd_;
	//ContestEvaluation evaluation_;
	CompetitionCustomPack evaluation;
	ContestEvaluation evaluation_;
	
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
		//evaluation_ = evaluation;
		this.evaluation = new CompetitionCustomPack(evaluation);
		// Get evaluation properties
		//Properties props = evaluation.getProperties(); ///TODO - depr, no longer needed
        // Get evaluation limit
        //evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        
        ///just to make the warnings go away
        /*boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }*/
    }

    
	public void run() {

		
		// Run your algorithm here
        // init Evolutionary Algorithm
		int populationSize = 100;
		double mutationRate = 0.02; // the higher, the more the chance to mutate individuals.
		double mutationSwing = 0.1;
		double parentsRatio = 0.7;
		double parentsSurvivalRatio = 0.15; // It is not used currently.

		// for now, let's stick with 100. Other population sizes should be justified.
		
        EA simplestEA = new EA(evaluation,populationSize, mutationRate, mutationSwing, parentsRatio, parentsSurvivalRatio); 
		//Visualizer viz = new Visualizer();

		// calculate fitness
        while(true) {
            // Select parents
            // Apply crossover / mutation operators

			try {
				simplestEA.reproduce();
			} catch (NotEnoughEvaluationsException e) {
				//System.out.println(evaluation_.getFinalResult());
				break;
			}

            // double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
            // Check fitness of unknown fuction
            //Double fitness = (double) evaluation_.evaluate(child);

			Individual best = simplestEA.getBestIndividual();

			// it gives me a SecurityException regarding a class loader if I put these two sysout statements all together.
			// I did not get anything like that. Can anyone else confirm? - Robert
			System.out.printf("Best individual in generation: Y = %6.4e\n", best.getFitness());

			// This could be useful for debugging purposes.
			// viz.printCoords(simplestEA.getPopulation());
			// System.out.println("\n-----------------------------------------------------------------------------\n");

            // Select survivors
		}
	}
}
	