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

public class Config {

    /// EA GLOBAL PARAMETERS ///
    private static final Integer populationSize = 100;
    private static final Integer offspringSize = 2;
    private static final Double mutationRate = 0.15; // percentage of offspring being mutated
    private static final Double parentsRatio = 0.15; // percentage of the population that will reproduce
    private static final Boolean apply_crowding = true;

    /// PARENTS SELECTION OPERATOR ///
    private static final String parentsSelectionOperatorName = "ranking_selector";

    /// RECOMBINATION OPERATOR ///
    private static final String recombinationOperatorName = "onePointCrossover";

    /// MUTATION OPERATOR ///
    private static final String mutationOperatorName = "gaussian";

    /// SURVIVOR SELECTION OPERATOR ///
    private static final String survivorSelectionOperatorName = "round_robin_tournament";


    // #### PARENTS SELECTION PARAMETERS ####

    // -<--- best_K_selector --->-
    // none

    // -<--- Fitness Proportional Selector --->-
    private static final String FPS_samplingMethod = "SUS";

    // -<--- Ranking Selector --->- 
    private static final String mapping = "linear";
    private static final Double s = 1.01;
    private static final Double base = 2.;
    private static final String RS_samplingMethod = "SUS";

    // -<--- tournament_selector --->-
    private static final Integer parents_tournamentSize = 15;

    // #### RECOMBINATION PARAMETERS ####

    // -<--- One-point Crossover --->-
    // none

    // -<--- Multi-point Crossover --->-
    // none

    // -<--- Simple Arithmetic Crossover --->-
    private static final Double simpleCrossAlpha = 0.5;

    // -<--- Single Arithmetic Crossover --->-
    private static final Double singleCrossAlpha = 0.5;

    // -<--- Whole Arithmetic Crossover --->-
    private static final Double wholeCrossAlpha = 0.2;

    // -<--- Blend Arithmetic Crossover --->-
    private static final Double blendCrossAlpha = 0.5;



    // #### MUTATION PARAMETERS ####

    // -<--- Uniform Mutation --->-
    private static final Double width = 0.07; 

    // -<--- Gaussian Mutation --->-
    private static final Double sigma = 0.09;
    private static final Boolean variable = true;

    // -<--- Uncorrelated 1 stepSize Mutation --->-
    // none

    // -<--- Uncorrelated N stepSizes Mutation --->-
    // none

    // -<--- Correlated N stepSizes Mutation --->-
    // none

    
    
    // #### SURVIVAL SELECTION PARAMETERS ####

    // -<--- mu + lambda --->-
    // none

    // -<--- mu, lambda --->-
    // none

    // -<--- round robin tournament --->-
    private static final Integer survivor_tournamentSize = 30;

    public static HashMap<String, Object> getEAParams() {
        HashMap<String, Object> EAParams = new HashMap<String, Object>();

        EAParams.put("populationSize", populationSize);
        EAParams.put("mutationRate", mutationRate);	
        EAParams.put("offspringSize", offspringSize);
        EAParams.put("parentsRatio", parentsRatio);	
        EAParams.put("apply_crowding", apply_crowding);

        return EAParams;
    }

    public static HashMap<String, Object> getParentsSelectionDescriptor() throws NotValidOperatorNameException {
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

    public static HashMap<String, Object> getRecombinationDescriptor() throws NotValidOperatorNameException {
        HashMap<String, Object> recombinationDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        recombinationDescriptor.put("operatorName", recombinationOperatorName);

        switch (recombinationOperatorName) {
            case "onePointCrossover":
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad, HashMap<String, Object> params) 
                        {return Recombinator.onePointCrossover(mom, dad, params);}
                });
                break;
            case "multiPointCrossover":
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

    public static HashMap<String, Object> getMutationDescriptor() throws NotValidOperatorNameException {
        HashMap<String, Object> mutationDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        // TODO add other fields to support operators which need additional parameters.
        mutationDescriptor.put("operatorName", mutationOperatorName);

        switch (mutationOperatorName) {
            case "uniform":
                params.put("mutationRate", mutationRate);
                params.put("width", width);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uniform(genotype, params);}
                });
                break;
            case "gaussian":
                params.put("mutationRate", mutationRate);
                params.put("sigma", sigma);
                params.put("variable", variable);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.gaussian(genotype, params);}
                });
                break;
            case "uncorrelated_1_stepSize":
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uncorrelated_1_stepSize(genotype, params);}
                });
                break;
            case "uncorrelated_N_stepSizes":
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

    public static HashMap<String, Object> getSurvivorSelectionDescriptor() throws NotValidOperatorNameException {
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

    public static ArrayList<String> getIndividualDescriptor() throws NotValidOperatorNameException {
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