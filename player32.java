import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;

public class player32 implements ContestSubmission
{
	Random rnd_;
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
		System.out.printf("Num. of available evaluations: %d\n", this.evaluation.getEvaluationLimit());
		// Get evaluation properties
		//Properties props = evaluation.getProperties(); ///TODO - depr, no longer needed
        // Get evaluation limit
        // evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
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
		
		int populationSize = 100;
		double mutationRate = -0.0; // the higher, the more the chance to mutate individuals.
		double mutationStepSize = 0.022; // Used in Uniform/Gaussian Mutations to set the width/sigma of the distributions.
		double parentsRatio = 0.7;
		double parentsSurvivalRatio = 0.15; // Currently, It is not used.

        EA simplestEA = new EA(evaluation,populationSize, mutationRate, mutationStepSize, parentsRatio, parentsSurvivalRatio); 

		// calculate fitness
		try {
        	while(true) {

				simplestEA.evolve();
				Individual best = simplestEA.getBestIndividual();

				System.out.printf("Best individual after %6d evaluations = %6.4e\n", this.evaluation.getCurrentEvaluationCount(), best.getFitness());
			}
		} catch (NotEnoughEvaluationsException e) {
			//System.out.println(evaluation_.getFinalResult());
		}
	}
}
	