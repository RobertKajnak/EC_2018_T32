import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    /// EA GLOBAL PARAMETERS ///
    private static final Integer populationSize = 100;
    private static final Double mutationRate = 0.3;
    private static final Double parentsRatio = 0.7;
    private static final Double parentsSurvivalRatio = 0.7;

    /// RECOMBINATION OPERATOR ///
    private static final String recombinationOperatorName = "wholeArithmeticCrossover";

    /// MUTATION OPERATOR ///
    private static final String mutationOperatorName = "uniform";


    /// MANUALLY TUNED PARAMETERS FOR OPERATOR

    // -<--- One-point Crossover --->-
    // none

    // -<--- Simple Arithmetic Crossover --->-
    private static final Double simpleCrossAlpha = 0.5;

    // -<--- Single Arithmetic Crossover --->-
    private static final Double singleCrossAlpha = 0.5;

    // -<--- Whole Arithmetic Crossover --->-
    private static final Double wholeCrossAlpha = 0.5;

    // -<--- Uniform Mutation --->-
    private static final Double width = 0.15; 

    // -<--- Gaussian Mutation --->-
    private static final Double sigma = 0.09;

    // -<--- Uncorrelated 1 stepSize Mutation --->-
    // none

    // -<--- Uncorrelated N stepSizes Mutation --->-
    // none

    // -<--- Correlated N stepSizes Mutation --->-
    // none

    public static HashMap<String, Object> getEAParams() {
        HashMap<String, Object> EAParams = new HashMap<String, Object>();

        EAParams.put("populationSize", populationSize);
        EAParams.put("mutationRate", mutationRate);
        EAParams.put("parentsRatio", parentsRatio);
        EAParams.put("parentsSurvivalRatio", parentsSurvivalRatio); 		

        return EAParams;
    }

    public static HashMap<String, Object> getRecombinationDescriptor() throws NotValidOperatorNameException {
        HashMap<String, Object> recombinationDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        // TODO add other fields to support operators which need additional parameters.
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
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.gaussian(genotype, params);}
                });
                break;
            case "uncorrelated_1_stepSize":
                params.put("mutationRate", mutationRate);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uncorrelated_1_stepSize(genotype, params);}
                });
                break;
            case "uncorrelated_N_stepSizes":
                params.put("mutationRate", mutationRate);
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

    public static ArrayList<String> getIndividualDescriptor() throws NotValidOperatorNameException {
        ArrayList<String> individualDescriptor = new ArrayList<String>();
        individualDescriptor.add("coords");

		switch (recombinationOperatorName) {
			case "onePointCrossover":
                break;
            case "simpleArithmeticCrossover":
                break;
            case "singleArithmeticCrossover":
                break;
            case "wholeArithmeticCrossover":
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