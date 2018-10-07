/*
 * Available operators' name:
 * 
 *  Parents selector operators' names:
 *   - best_K_selector
 *   - fitness_proportional_selector
 *   - ranking_selector
 *   - tournament_selector
 * 
 *  Recombination operators' names;
 *   - onePointCrossover
 *   - multiPointCrossover
 *   - simpleArithmeticCrossover
 *   - singleArithmeticCrossover
 *   - wholeArithmeticCrossover
 *   - blendCrossover
 *  
 *  Mutation operators' names:
 *   - uniform
 *   - gaussian
 *   - uncorrelated_1_stepSize
 *   - uncorrelated_N_stepSizes
 *   - correlated_N_stepSizes
 * 
 *  Survival selection operators' names:
 *   - mu_plus_lambda
 *   - mu_lambda
 *   - round_robin_tournament 
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;

public class Island {

    /// EA GLOBAL PARAMETERS ///
    protected static final Integer populationSize = Integer.parseInt(System.getProperty("populationSize"));;
    protected static final Integer offspringSize = Integer.parseInt(System.getProperty("offspringSize"));;
    protected static final Double mutationRate = Double.parseDouble(System.getProperty("mutationRate")); // percentage of offspring being mutated
    protected static final Double parentsRatio = Double.parseDouble(System.getProperty("parentsRatio")); // percentage of the population that will reproduce
    protected static final Boolean apply_crowding = true;

    // /// Actual values for the following parameter are defined in each sub-class (island_1A, island_1B...)
    // /// PARENTS SELECTION OPERATOR ///
    // protected String parentsSelectionOperatorName = "";

    // /// RECOMBINATION OPERATOR ///
    // protected String recombinationOperatorName = "";

    // /// MUTATION OPERATOR ///
    // protected String mutationOperatorName = "";

    // /// SURVIVOR SELECTION OPERATOR ///
    // protected String survivorSelectionOperatorName = "";


    // #### PARENTS SELECTION PARAMETERS ####

    // -<--- best_K_selector --->-
    // none

    // -<--- Fitness Proportional Selector --->-
    protected static final String FPS_samplingMethod = "SUS";

    // -<--- Ranking Selector --->- 
    protected static final String mapping = "linear";
    protected static final Double s = Double.parseDouble(System.getProperty("s"));
    protected static final Double base = 2.718;
    protected static final Double ranking_scaling_factor = Double.parseDouble(System.getProperty("RSscalingFactor"));
    protected static final String RS_samplingMethod = "SUS";

    // -<--- tournament_selector --->-
    protected static final Integer parents_tournamentSize =  Integer.parseInt(System.getProperty("tournamentSize"));

    // #### RECOMBINATION PARAMETERS ####

    //  -<--- Uniform Crossover --->-
    // none

    // -<--- One-point Crossover --->-
    // none

    // -<--- Multi-point Crossover --->-
    protected static final Integer n_points = 3;

    // -<--- Simple Arithmetic Crossover --->-
    protected static final Double simpleCrossAlpha = 0.5;

    // -<--- Single Arithmetic Crossover --->-
    protected static final Double singleCrossAlpha = 0.5;

    // -<--- Whole Arithmetic Crossover --->-
    protected static final Double wholeCrossAlpha = 0.2;

    // -<--- Blend Arithmetic Crossover --->-
    protected static final Double blendCrossAlpha = 0.5;



    // #### MUTATION PARAMETERS ####

    // -<--- Uniform Mutation --->-
    protected static final Double width = 0.07; 

    // -<--- Gaussian Mutation --->-
    protected static final Double sigma = 0.09;
    protected static final Boolean variable = true;

    // -<--- Uncorrelated 1 stepSize Mutation --->-
    protected static final Double one_step_tau = 1.0 / Math.sqrt(2 * Math.sqrt(10));

    // -<--- Uncorrelated N stepSizes Mutation --->-
    protected static final Double N_steps_tau = Double.parseDouble(System.getProperty("tau")) / Math.sqrt(2 * Math.sqrt(10));
    protected static final Double tauPrime = Double.parseDouble(System.getProperty("tauPrime")) / Math.sqrt(2 * 10);
    protected static final Double min_std =  Double.parseDouble(System.getProperty("stdMin"));

    // -<--- Correlated N stepSizes Mutation --->-
    // none

    
    
    // #### SURVIVAL SELECTION PARAMETERS ####

    // -<--- mu + lambda --->-
    // none

    // -<--- mu, lambda --->-
    // none

    // -<--- round robin tournament --->-
    protected static final Integer survivor_tournamentSize = Integer.parseInt(System.getProperty("RRtournamentSize"));;

    public static HashMap<String, Object> getEAParams() {
        HashMap<String, Object> EAParams = new HashMap<String, Object>();

        EAParams.put("populationSize", populationSize);
        EAParams.put("mutationRate", mutationRate);	
        EAParams.put("offspringSize", offspringSize);
        EAParams.put("parentsRatio", parentsRatio);	
        EAParams.put("apply_crowding", apply_crowding);

        return EAParams;
    }

    public static HashMap<String, Object> getParentsSelectionDescriptor(String parentsSelectionOperatorName) throws NotValidOperatorNameException {
        HashMap<String, Object> parentsSelectionDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        parentsSelectionDescriptor.put("operatorName", parentsSelectionOperatorName);

        switch (parentsSelectionOperatorName) {
            case "best_K_selector":
                parentsSelectionDescriptor.put("call", new ParentsSelectionFunctionInterface() {
                    public ArrayList<Integer> execute(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
                        return ParentsSelector.best_K_selector(population, params);}
                });
                break;
            case "fitness_proportional_selector":
                params.put("samplingMethod", FPS_samplingMethod);
                parentsSelectionDescriptor.put("call", new ParentsSelectionFunctionInterface() {
                    public ArrayList<Integer> execute(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException{
                        return ParentsSelector.fitness_proportional_selector(population, params);}
                });
                break;
            case "ranking_selector":
                params.put("mapping", mapping);
                params.put("s", s);
                params.put("base", base);
                params.put("samplingMethod", RS_samplingMethod);
                params.put("ranking_scaling_factor", ranking_scaling_factor);
                parentsSelectionDescriptor.put("call", new ParentsSelectionFunctionInterface() {
                    public ArrayList<Integer> execute(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException{
                        return ParentsSelector.ranking_selector(population, params);}
                });
                break;
            case "tournament_selector":
                params.put("tournamentSize", parents_tournamentSize);
                parentsSelectionDescriptor.put("call", new ParentsSelectionFunctionInterface() {
                    public ArrayList<Integer> execute(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException{
                        return ParentsSelector.tournament_selector(population, params);}
                });
                break;
            default:
                throw new NotValidOperatorNameException("You did not provide a valid name for the parent selection operator.");
        }

        parentsSelectionDescriptor.put("params", params);

        return parentsSelectionDescriptor;
    }

    public static HashMap<String, Object> getRecombinationDescriptor(String recombinationOperatorName) throws NotValidOperatorNameException {
        HashMap<String, Object> recombinationDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        recombinationDescriptor.put("operatorName", recombinationOperatorName);

        switch (recombinationOperatorName) {
            case "uniformCrossover":
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params) 
                        {return Recombinator.uniformCrossover(mom, dad, params);}
                });
                break;
            case "onePointCrossover":
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params) 
                        {return Recombinator.onePointCrossover(mom, dad, params);}
                });
                break;
            case "multiPointCrossover":
                params.put("n_points", n_points);
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params) 
                        {return Recombinator.multiPointCrossover(mom, dad, params);}
                });
                break;
            case "simpleArithmeticCrossover":
                params.put("simpleCrossAlpha", simpleCrossAlpha);
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params)
                        {return Recombinator.simpleArithmeticCrossover(mom, dad, params);}
                });
                break;
            case "singleArithmeticCrossover":
                params.put("singleCrossAlpha", singleCrossAlpha);
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params)
                        {return Recombinator.singleArithmeticCrossover(mom, dad, params);}
                });
                break;
            case "wholeArithmeticCrossover":
                params.put("wholeCrossAlpha", wholeCrossAlpha);
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params)
                        {return Recombinator.wholeArithmeticCrossover(mom, dad, params);}
                });
                break;
            case "blendCrossover":
                params.put("blendCrossAlpha", blendCrossAlpha);
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params)
                        {return Recombinator.blendCrossover(mom, dad, params);}
                });
                break;
            default:
                throw new NotValidOperatorNameException("You did not provide a valid name for the recombination operator.");
        }

        recombinationDescriptor.put("params", params);

        return recombinationDescriptor;
    }

    public static HashMap<String, Object> getMutationDescriptor(String mutationOperatorName) throws NotValidOperatorNameException {
        HashMap<String, Object> mutationDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        // TODO add other fields to support operators which need additional parameters.
        mutationDescriptor.put("operatorName", mutationOperatorName);

        switch (mutationOperatorName) {
            case "uniform":
                params.put("width", width);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uniform(genotype, params);}
                });
                break;
            case "gaussian":
                params.put("sigma", sigma);
                params.put("variable", variable);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.gaussian(genotype, params);}
                });
                break;
            case "uncorrelated_1_stepSize":
                params.put("tau", one_step_tau);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uncorrelated_1_stepSize(genotype, params);}
                });
                break;
            case "uncorrelated_N_stepSizes":
                params.put("tau", N_steps_tau);
                params.put("tauPrime", tauPrime);
                params.put("min_std", min_std);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uncorrelated_N_stepSizes(genotype, params);}
                });
                break;
            case "correlated_N_stepSizes":
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.correlated_N_stepSizes(genotype, params);}
                });
                break;
            default:
                throw new NotValidOperatorNameException("You did not provide a valid name for the mutation operator.");
        }

        mutationDescriptor.put("params", params);

        return mutationDescriptor;
    }

    public static HashMap<String, Object> getSurvivorSelectionDescriptor(String survivorSelectionOperatorName) throws NotValidOperatorNameException {
        HashMap<String, Object> survivorSelectionDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("operatorName", survivorSelectionOperatorName);

        switch (survivorSelectionOperatorName) {
            case "mu_plus_lambda":
                survivorSelectionDescriptor.put("call", new SurvivorSelectionFunctionInterface() {
                public ArrayList<Individual> execute(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
                    return SurvivorSelector.mu_plus_lambda(population, offspring, params);}
                });
                break;
            case "mu_lambda":
                survivorSelectionDescriptor.put("call", new SurvivorSelectionFunctionInterface() {
                public ArrayList<Individual> execute(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
                    return SurvivorSelector.mu_lambda(population, offspring, params);}
                });
                break;
            case "round_robin_tournament":
                params.put("tournamentSize", survivor_tournamentSize);
                survivorSelectionDescriptor.put("call", new SurvivorSelectionFunctionInterface() {
                public ArrayList<Individual> execute(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
                    return SurvivorSelector.round_robin_tournament(population, offspring, params);}
                });
                break;
            default:
                throw new NotValidOperatorNameException("You did not provide a valid name for the survivor selection operator.");
        }

        survivorSelectionDescriptor.put("params", params);

        return survivorSelectionDescriptor;
    }

    public static ArrayList<String> getIndividualDescriptor(String recombinationOperatorName, String mutationOperatorName) throws NotValidOperatorNameException {
        ArrayList<String> individualDescriptor = new ArrayList<String>();
        individualDescriptor.add("coords");

        // Giuseppe: here the switches for the selection operators are not needed 
        //           since they operate independently form the specific representation

		switch (recombinationOperatorName) {
			case "onePointCrossover":
                break;
            case "multiPointCrossover":
                break;
            case "simpleArithmeticCrossover":
                break;
            case "singleArithmeticCrossover":
                break;
            case "wholeArithmeticCrossover":
                break;
            case "blendCrossover":
                break;
			default:
				throw new NotValidOperatorNameException("You did not provide a valid name for the recombination operator.");
		}

		switch (mutationOperatorName) {
			case "uniform":
				break;
			case "gaussian":
				break;
			case "uncorrelated_1_stepSize":
                individualDescriptor.add("stepSize");
                break;
			case "uncorrelated_N_stepSizes":
                individualDescriptor.add("stepSizes");
                break;
			case "correlated_N_stepSizes":
				individualDescriptor.add("stepSizes");
                individualDescriptor.add("alphas");
                break;
			default:
				throw new NotValidOperatorNameException("You did not provide a valid name for the mutation operator.");
		}

		return individualDescriptor;
	}
}