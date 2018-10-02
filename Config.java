import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    /// EA GLOBAL PARAMETERS ///
    private static final Integer populationSize = 100;
    private static final Integer offspringSize = 2;
    private static final Double mutationRate = 0.5; // percentage of offspring being mutated

    /// PARENTS SELECTION OPERATOR ///
    private static final String parentsSelectionOperatorName = "ranking_selector";

    /// RECOMBINATION OPERATOR ///
    private static final String recombinationOperatorName = "onePointCrossover";

    /// MUTATION OPERATOR ///
    private static final String mutationOperatorName = "gaussian";

    /// SURVIVOR SELECTION OPERATOR ///
    private static final String survivorSelectionOperatorName = "mu_plus_lambda";


    // #### PARENTS SELECTION PARAMETERS ####

    // -<--- best_K_selector --->-
    private static final Double bestK_parentsRatio = 0.15;

    // -<--- Fitness Proportional Selector --->-
    private static final Double FPS_parentsRatio = 0.15;
    private static final String FPS_mapping = "linear";
    private static final Double FPS_s = 1.5; // must be 1 < s <= 2. Makes sense when using linear mapping.
    private static final Double FPS_base = 2.718; // it makes sense when exponential mapping is used.
    private static final String FPS_samplingMethod = "SUS";

    // -<--- Ranking Selector --->-
    private static final Double RS_parentsRatio = 0.15;
    private static final String RS_mapping = "exponential";
    private static final Double RS_s = 1.5;
    private static final Double RS_base = 2.718;
    private static final String RS_samplingMethod = "SUS";


    // #### RECOMBINATION PARAMETERS ####

    // -<--- One-point Crossover --->-
    // none

    // -<--- Simple Arithmetic Crossover --->-
    private static final Double simpleCrossAlpha = 0.5;

    // -<--- Single Arithmetic Crossover --->-
    private static final Double singleCrossAlpha = 0.5;

    // -<--- Whole Arithmetic Crossover --->-
    private static final Double wholeCrossAlpha = 0.5;

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

    // -<--- mu + lambda --->- // TODO
    // none

    // -<--- mu lambda --->-   // TODO
    // none

    // -<--- round robin tournament --->- // TODO
    private static final Integer tournamentSize = 10;

    public static HashMap<String, Object> getEAParams() {
        HashMap<String, Object> EAParams = new HashMap<String, Object>();

        EAParams.put("populationSize", populationSize);
        EAParams.put("mutationRate", mutationRate);	
        EAParams.put("offspringSize", offspringSize);	

        return EAParams;
    }

    public static HashMap<String, Object> getParentsSelectionDescriptor() throws NotValidOperatorNameException {
        HashMap<String, Object> parentsSelectionDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        parentsSelectionDescriptor.put("operatorName", parentsSelectionOperatorName);

        switch (parentsSelectionOperatorName) {
            case "best_K_selector":
                params.put("parentsRatio", bestK_parentsRatio);
                parentsSelectionDescriptor.put("call", new ParentsSelectionFunctionInterface() {
                    public ArrayList<Individual> execute(ArrayList<Individual> population, HashMap<String, Object> params) {
                        return ParentsSelector.best_K_selector(population, params);}
                });
                break;
            case "fitness_proportional_selector":
                params.put("parentsRatio", FPS_parentsRatio);
                params.put("mapping", FPS_mapping);
                params.put("s", FPS_s);
                params.put("base", FPS_base);
                params.put("samplingMethod", FPS_samplingMethod);
                parentsSelectionDescriptor.put("call", new ParentsSelectionFunctionInterface() {
                    public ArrayList<Individual> execute(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException{
                        return ParentsSelector.fitness_proportional_selector(population, params);}
                });
                break;
            case "ranking_selector":
                params.put("parentsRatio", RS_parentsRatio);
                params.put("mapping", RS_mapping);
                params.put("s", RS_s);
                params.put("base", RS_base);
                params.put("samplingMethod", RS_samplingMethod);
                parentsSelectionDescriptor.put("call", new ParentsSelectionFunctionInterface() {
                    public ArrayList<Individual> execute(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException{
                        return ParentsSelector.ranking_selector(population, params);}
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