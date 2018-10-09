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
	String[] islands_names;

	HashMap<String, EA> EAs = new HashMap<String, EA>();
	HashMap<String, HashMap<String, Object>> EAParams = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, HashMap<String, Object>> parentsSelectionDescriptor = new HashMap<String, HashMap<String, Object>>();;
	HashMap<String, HashMap<String, Object>> recombinationDescriptor = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, HashMap<String, Object>> mutationDescriptor = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, HashMap<String, Object>> survivorSelectionDescriptor = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, ArrayList<String>> individualDescriptor = new HashMap<String, ArrayList<String>>();
	HashMap<String, EAHandler> manager = new HashMap<String, EAHandler>();
	
	public player32() throws NotValidOperatorNameException {
		this.rnd_ = new Random();
		this.getIslandsDescriptors();
	}
	
	public void setSeed(long seed) {
		// Set seed of algortihms random process
		this.rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation) {
		// Set evaluation problem used in the run
		//evaluation_ = evaluation;
		this.evaluation = new CompetitionCustomPack(evaluation);
		System.out.printf("Num. of available evaluations: %d\n", this.evaluation.getEvaluationLimit());
		// Get evaluation properties
		// Properties props = this.evaluation.getProperties(); ///TODO - depr, no longer needed
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

		// define the EAs for each island
		this.islands_names = new String[] {"Island_1A", "Island_1B", "Island_2A", "Island_2B", "Island_3A", "Island_3B"};
		for (String island_name : islands_names) {
			this.EAs.put(island_name, new EA(
				this.evaluation, 
				this.EAParams.get(island_name), 
				this.parentsSelectionDescriptor.get(island_name),
				this.recombinationDescriptor.get(island_name), 
				this.mutationDescriptor.get(island_name), 
				this.survivorSelectionDescriptor.get(island_name),
				this.individualDescriptor.get(island_name)
			));
			this.manager.put(island_name, new EAHandler());
		}

		try {

			Integer num_of_stopped_EA = 0;
			Integer generation = 1;

        	while(true) {
				for (String island_name : this.islands_names) {
					if (this.manager.get(island_name).allow_evolution()) {
						this.EAs.get(island_name).evolve();
						this.manager.get(island_name).update(this.EAs.get(island_name));
					}
					else num_of_stopped_EA += 1;
				}

				if (num_of_stopped_EA == 6) {
					throw new NotEnoughEvaluationsException();
				}

				if (num_of_stopped_EA > 3) {
					num_of_stopped_EA = 0;
					// pick the best from each subpopulation and move them into the next subpopulations
					ArrayList<Individual>[] immigrants = new ArrayList[6];
					for (int i=0; i<islands_names.length; i++) {
						immigrants[i] = this.EAs.get(islands_names[i]).getImmigrants(5);
					}
					for (int i=0; i<islands_names.length; i++) {
						String dest_island = islands_names[(i+1) % islands_names.length];
						this.EAs.get(dest_island).host(immigrants[i]);
						
						// allow evolution
						this.manager.get(islands_names[i]).reset();
					}
				}

				// Output
				System.out.printf("Best individual after %6d evaluations (Gen. %d):\n", this.evaluation.getCurrentEvaluationCount(), generation);
				for (String island_name : this.islands_names) {
					System.out.printf("\t%s - %6.8f\n", island_name, this.EAs.get(island_name).getBestIndividual().getFitness());
				}

				generation++;
			}
		} catch (NotEnoughEvaluationsException e) {
			// System.out.println(evaluation_.getFinalResult());
		}
	}	

	public void getIslandsDescriptors()  throws NotValidOperatorNameException {

		// Island_1A
		this.EAParams.put("Island_1A", Island_1A.getEAParams());
		this.parentsSelectionDescriptor.put("Island_1A", Island_1A.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_1A", Island_1A.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_1A", Island_1A.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_1A", Island_1A.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_1A", Island_1A.getIndividualDescriptor());

		// Island_1B
		this.EAParams.put("Island_1B", Island_1B.getEAParams());
		this.parentsSelectionDescriptor.put("Island_1B", Island_1B.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_1B", Island_1B.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_1B", Island_1B.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_1B", Island_1B.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_1B", Island_1B.getIndividualDescriptor());

		// Island_2A
		this.EAParams.put("Island_2A", Island_2A.getEAParams());
		this.parentsSelectionDescriptor.put("Island_2A", Island_2A.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_2A", Island_2A.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_2A", Island_2A.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_2A", Island_2A.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_2A", Island_2A.getIndividualDescriptor());

		// Island_2B
		this.EAParams.put("Island_2B", Island_2B.getEAParams());
		this.parentsSelectionDescriptor.put("Island_2B", Island_2B.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_2B", Island_2B.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_2B", Island_2B.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_2B", Island_2B.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_2B", Island_2B.getIndividualDescriptor());

		// Island_3A
		this.EAParams.put("Island_3A", Island_3A.getEAParams());
		this.parentsSelectionDescriptor.put("Island_3A", Island_3A.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_3A", Island_3A.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_3A", Island_3A.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_3A", Island_3A.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_3A", Island_3A.getIndividualDescriptor());

		// Island_3B
		this.EAParams.put("Island_3B", Island_3B.getEAParams());
		this.parentsSelectionDescriptor.put("Island_3B", Island_3B.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_3B", Island_3B.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_3B", Island_3B.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_3B", Island_3B.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_3B", Island_3B.getIndividualDescriptor());
	}
}