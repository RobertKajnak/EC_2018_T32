import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class player32 implements ContestSubmission
{
	Random rnd_;
	CompetitionCustomPack evaluation;
	ContestEvaluation evaluation_;
	String[] islands_names;

	HashMap<String, Object> paramVector = new HashMap<String, Object>();
	HashMap<String, EA> EAs = new HashMap<String, EA>();
	HashMap<String, HashMap<String, Object>> EAParams = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, HashMap<String, Object>> parentsSelectionDescriptor = new HashMap<String, HashMap<String, Object>>();;
	HashMap<String, HashMap<String, Object>> recombinationDescriptor = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, HashMap<String, Object>> mutationDescriptor = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, HashMap<String, Object>> survivorSelectionDescriptor = new HashMap<String, HashMap<String, Object>>();
	HashMap<String, ArrayList<String>> individualDescriptor = new HashMap<String, ArrayList<String>>();
	HashMap<String, EAHandler> manager = new HashMap<String, EAHandler>();

	boolean isMultimodal;
	boolean hasStructure;
	boolean isSeparable;
	
	public player32() {
		this.rnd_ = new Random();
	}
	
	public void setSeed(long seed) {
		// Set seed of algortihms random process
		this.rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation) {
		// Set evaluation problem used in the run
		this.evaluation = new CompetitionCustomPack(evaluation);
		Properties props = evaluation.getProperties();
	
        this.isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        this.hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        this.isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));
		
		if (!this.isMultimodal && !this.hasStructure && !this.isSeparable) {
			System.out.println("Bent Cigar Function");
			paramVector.put("populationSize", 100);
			paramVector.put("offspringSize", 2);
			paramVector.put("mutationRate", 0.15);
			paramVector.put("parentsRatio", 0.15);
			paramVector.put("gaussianStd", 0.095139908);
			paramVector.put("gaussianAlpha", 2.145378);
			paramVector.put("gaussianBeta", 1.711925);
			paramVector.put("parents_tournamentSize", null);
			paramVector.put("s", null);
			paramVector.put("RS_factor", null);
			paramVector.put("tau", null);
			paramVector.put("tauPrime", null);
			paramVector.put("minStd", null);
			paramVector.put("survivor_RR_tournamentSize", null);
			paramVector.put("survivor_tournamentSize", null);
			paramVector.put("parents_RR_tournamentSize", null);
		}
		if (this.isMultimodal && this.hasStructure && !this.isSeparable) {
			// System.out.println("Schaffers Function");
			paramVector.put("populationSize", 110);
			paramVector.put("offspringSize", 10); // was 10
			paramVector.put("mutationRate", null);
			paramVector.put("parentsRatio", 0.49);
			paramVector.put("gaussianStd", null);
			paramVector.put("gaussianAlpha", null);
			paramVector.put("gaussianBeta", null);
			paramVector.put("parents_tournamentSize", 7);
			paramVector.put("parents_RR_tournamentSize", null);
			paramVector.put("s", 1.5);
			paramVector.put("RS_factor", null);
			paramVector.put("tau", 0.686);
			paramVector.put("tauPrime", 0.99);
			paramVector.put("minStd", 0.0005);
			paramVector.put("survivor_RR_tournamentSize", null);
			paramVector.put("survivor_tournamentSize", null);
		}
		if (this.isMultimodal && !this.hasStructure && !this.isSeparable) {
            // System.out.println("Katsuura Function"); 
			paramVector.put("populationSize", 100);
			paramVector.put("offspringSize", Integer.parseInt(System.getProperty("offspringSize"))); 
			paramVector.put("mutationRate", null);
			paramVector.put("parentsRatio", Double.parseDouble(System.getProperty("parentsRatio")));
			paramVector.put("gaussianStd", null);
			paramVector.put("gaussianAlpha", null);
			paramVector.put("gaussianBeta", null);
			paramVector.put("parents_tournamentSize", Integer.parseInt(System.getProperty("tournamentSize")));
			paramVector.put("parents_RR_tournamentSize", null);
			paramVector.put("s", Double.parseDouble(System.getProperty("s")));
			paramVector.put("RS_factor", null);
			paramVector.put("tau", Double.parseDouble(System.getProperty("tau")));
			paramVector.put("tauPrime", Double.parseDouble(System.getProperty("tauPrime")));
			paramVector.put("minStd", Double.parseDouble(System.getProperty("minStd")));
			paramVector.put("survivor_RR_tournamentSize", null);
			paramVector.put("survivor_tournamentSize", null);
		}

		this.getIslandsDescriptors();
    }
    
	public void run() {
		Integer generation = 1;
		HashMap<String, Double> islandDiversity = new HashMap<String, Double>();
		String bestIsland = "";
		double bestPerformance = 0.;

		if (!this.isMultimodal) {
			String island_name = "Island_Bent_Cigar";
			this.EAs.put(island_name, new EA (
				this.evaluation, 
				this.EAParams.get(island_name), 
				this.parentsSelectionDescriptor.get(island_name),
				this.recombinationDescriptor.get(island_name), 
				this.mutationDescriptor.get(island_name), 
				this.survivorSelectionDescriptor.get(island_name),
				this.individualDescriptor.get(island_name)
			));
			try {
				while (true) {
					this.EAs.get(island_name).evolve();
					// System.out.printf("Best individual after %6d evaluations (Gen. %d): %6.8e\n", 
						//this.evaluation.getCurrentEvaluationCount(), generation, this.EAs.get(island_name).getBestIndividual().getFitness());
					generation++;
				}
			} catch (NotEnoughEvaluationsException e) {}
		}
		else {
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

			Integer num_of_stopped_EA = 0;

			while(true) {

				for (String island_name : this.islands_names) {
					if (this.manager.get(island_name).allow_evolution()) {
						try {
							this.EAs.get(island_name).evolve();
							// if (this.evaluation.getCurrentEvaluationCount() > 100000)
							// 	throw new java.lang.ArrayIndexOutOfBoundsException();
						} catch (NotEnoughEvaluationsException e) {}
						this.manager.get(island_name).update(this.EAs.get(island_name));
					}
					else num_of_stopped_EA += 1;
				}

				// get the best performing island
				for (String island_name : this.islands_names) {
					double islandPerformance = this.EAs.get(island_name).getBestIndividual().getFitness();
					if ( islandPerformance - bestPerformance > 1e-5) {
						bestPerformance = islandPerformance;
						bestIsland = island_name;
					}
					islandDiversity.put(island_name, this.EAs.get(island_name).computeDiversity());
				}

				if (num_of_stopped_EA > 2) {
					num_of_stopped_EA = 0;
					// pick the best from each subpopulation and move them into the next subpopulations
					ArrayList<Individual>[] immigrants = new ArrayList[6];
					for (int i=0; i<islands_names.length; i++) {
						String dest_island = islands_names[(i+1) % islands_names.length];
						immigrants[i] = this.EAs.get(islands_names[i]).getImmigrants(5, this.EAs.get(dest_island).getPopulation());
					}
					for (int i=0; i<islands_names.length; i++) {
						String dest_island = islands_names[(i+1) % islands_names.length];
						this.EAs.get(dest_island).host(immigrants[i]);
						// dest_island = islands_names[(i-1)>=0 ? (i-1) : islands_names.length-1 ];
						// this.EAs.get(dest_island).host(immigrants[i]);
						
						// allow evolution
						this.manager.get(islands_names[i]).reset();
					}
				}
				
				System.out.printf("Best individual after %6d evaluations (Gen. %d):\n", this.evaluation.getCurrentEvaluationCount(), generation);
				for (String island_name : this.islands_names) {
					System.out.printf("\t%s - %6.8f, diversity = %6.8f, evaluation = %d, best = %s\n", 
						island_name, 
						this.EAs.get(island_name).getBestIndividual().getFitness(),
						islandDiversity.get(island_name), 
						this.EAs.get(island_name).getNumOfEvaluation(),
						(island_name == bestIsland ? "yes" : ""));
				}

				generation++;
			}
		}
	}	

	public void getIslandsDescriptors() {

		// The only Island for the Bent Cigar function
		// Island Bent Cigar
		Island_Bent_Cigar island_bc = new Island_Bent_Cigar(this.paramVector);
		this.EAParams.put("Island_Bent_Cigar", island_bc.getEAParams());
		this.parentsSelectionDescriptor.put("Island_Bent_Cigar", island_bc.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_Bent_Cigar", island_bc.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_Bent_Cigar", island_bc.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_Bent_Cigar", island_bc.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_Bent_Cigar", island_bc.getIndividualDescriptor());

		// Island_1A
		Island_1A island_1a = new Island_1A(this.paramVector);
		this.EAParams.put("Island_1A", island_1a.getEAParams());
		this.parentsSelectionDescriptor.put("Island_1A", island_1a.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_1A", island_1a.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_1A", island_1a.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_1A", island_1a.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_1A", island_1a.getIndividualDescriptor());

		// Island_1B
		Island_1B island_1b = new Island_1B(this.paramVector);
		this.EAParams.put("Island_1B", island_1b.getEAParams());
		this.parentsSelectionDescriptor.put("Island_1B", island_1b.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_1B", island_1b.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_1B", island_1b.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_1B", island_1b.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_1B", island_1b.getIndividualDescriptor());

		// Island_2A
		Island_2A island_2a = new Island_2A(this.paramVector);
		this.EAParams.put("Island_2A", island_2a.getEAParams());
		this.parentsSelectionDescriptor.put("Island_2A", island_2a.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_2A", island_2a.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_2A", island_2a.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_2A", island_2a.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_2A", island_2a.getIndividualDescriptor());

		// Island_2B
		Island_2B island_2b = new Island_2B(this.paramVector);
		this.EAParams.put("Island_2B", island_2b.getEAParams());
		this.parentsSelectionDescriptor.put("Island_2B", island_2b.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_2B", island_2b.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_2B", island_2b.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_2B", island_2b.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_2B", island_2b.getIndividualDescriptor());

		// Island_3A
		Island_3A island_3a = new Island_3A(this.paramVector);
		this.EAParams.put("Island_3A", island_3a.getEAParams());
		this.parentsSelectionDescriptor.put("Island_3A", island_3a.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_3A", island_3a.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_3A", island_3a.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_3A", island_3a.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_3A", island_3a.getIndividualDescriptor());

		// Island_3B
		Island_3B island_3b = new Island_3B(this.paramVector);
		this.EAParams.put("Island_3B", island_3b.getEAParams());
		this.parentsSelectionDescriptor.put("Island_3B", island_3b.getParentsSelectionDescriptor());
		this.recombinationDescriptor.put("Island_3B", island_3b.getRecombinationDescriptor());
		this.mutationDescriptor.put("Island_3B", island_3b.getMutationDescriptor());
		this.survivorSelectionDescriptor.put("Island_3B", island_3b.getSurvivorSelectionDescriptor());
		this.individualDescriptor.put("Island_3B", island_3b.getIndividualDescriptor());
	}
}