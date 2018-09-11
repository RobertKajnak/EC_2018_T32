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
	
    
	public void run() {
		// Run your algorithm here
        // System.out.println("Evaluations Limit:" + evaluations_limit_);
        int evals = 0;
        // init Evolutionary Algorithm

		int populationSize = 300;
		double mutationRate = 0.03; // the higher, the more the chance to mutate individuals.
		double mutationSwing = 0.5;
		double parentsRatio = 0.7;
		double parentsSurvivalRatio = 0.15; // It is not used currently.

		// for now, let's stick with 100. Other population sizes should be justified.
        EA simplestEA = new EA(populationSize, mutationRate, mutationSwing, parentsRatio, parentsSurvivalRatio); 
		simplestEA.initialize();

		// calculate fitness
        while(evals<evaluations_limit_){
            // Select parents
            // Apply crossover / mutation operators

			simplestEA.reproduce();
			simplestEA.evaluateFitness(evaluation_);

            double child[] = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
            // Check fitness of unknown fuction
            //Double fitness = (double) evaluation_.evaluate(child);

			Individual best = simplestEA.getBestIndividual();

			// it gives me a SecurityException regarding a class loader if I put these two sysout statements all together..
			System.out.print("Best individual in generation evals: "); 
			System.out.println(best.getFitness());
            
            evals++;
            // Select survivors
		}
		
	}

}
	