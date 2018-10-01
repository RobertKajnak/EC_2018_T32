import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

public class player32 implements ContestSubmission
{
	Random rnd_;
	CompetitionCustomPack evaluation;
	ContestEvaluation evaluation_;

	HashMap<String, Object> EAParams;
	HashMap<String, Object> parentsSelectionDescriptor;
	HashMap<String, Object> recombinationDescriptor;
	HashMap<String, Object> mutationDescriptor;
	HashMap<String, Object> survivorSelectionDescriptor;
	ArrayList<String> individualDescriptor;
	EA optimizer;
	
	public player32() throws NotValidOperatorNameException {
		rnd_ = new Random();
		
		this.EAParams = Config.getEAParams();
		this.parentsSelectionDescriptor = Config.getParentsSelectionDescriptor();
		this.recombinationDescriptor = Config.getRecombinationDescriptor();
		this.mutationDescriptor = Config.getMutationDescriptor();
		this.survivorSelectionDescriptor = Config.getSurvivorSelectionDescriptor();
		this.individualDescriptor = Config.getIndividualDescriptor(); 
	}
	
	public void setSeed(long seed) {
		// Set seed of algortihms random process
		rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation) {
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

		this.optimizer = new EA(
			this.evaluation, 
			this.EAParams, 
			this.parentsSelectionDescriptor,
			this.recombinationDescriptor, 
			this.mutationDescriptor, 
			this.survivorSelectionDescriptor,
			this.individualDescriptor
		);

		try {
        	while(true) {
				this.optimizer.evolve();
				System.out.printf("Best individual after %6d evaluations = %6.4e\n", this.evaluation.getCurrentEvaluationCount(), this.optimizer.getBestIndividual().getFitness());
			}
		} catch (NotEnoughEvaluationsException e) {
			// System.out.println(evaluation_.getFinalResult());
		}
	}	
}