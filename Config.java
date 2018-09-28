import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    /// EA GLOBAL PARAMETERS ///
    private static final Integer populationSize = 100;
    private static final Double mutationRate = 0.0;
    private static final Double parentsRatio = 0.7;
    private static final Double parentsSurvivalRatio = 0.7;

    /// RECOMBINATION OPERATOR ///
    private static final String recombinationOperatorName = "onePointCrossover";

    /// MUTATION OPERATOR ///
    private static final String mutationOperatorName = "uncorrelated_N_stepSizes";


    /// MANUALLY TUNED PARAMETERS FOR OPERATOR

    // -<--- Uniform Mutation --->-
    private static final Double width = 0.15; 

    // -<--- Gaussian Mutation --->-
    private static final Double sigma = 0.15;

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

        // TODO add other fields to support operators which need additional parameters.
        recombinationDescriptor.put("operatorName", recombinationOperatorName);

        switch (recombinationOperatorName) {
            case "onePointCrossover":
                recombinationDescriptor.put("call", new RecombinationFunctionInterface() {
                    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad) 
                        {return Recombinator.onePointCrossover(mom, dad);}
                });
                // recombinationDescriptor.put("call", (mom, dad) -> Recombinator.onePointCrossover(mom, dad));
                break;
            default:
                throw new NotValidOperatorNameException("You did not provide a valid name for the recombination operator.");
        }

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

		switch (recombinationOperatorName) {
			case "onePointCrossover":
				break;
			default:
				throw new NotValidOperatorNameException("You did not provide a valid name for the recombination operator.");
		}

		switch (mutationOperatorName) {
			case "uniform":
				individualDescriptor.add("coords");
				break;
			case "gaussian":
				individualDescriptor.add("coords");
				break;
			case "uncorrelated_1_stepSize":
				individualDescriptor.add("coords");
                individualDescriptor.add("stepSize");
                break;
			case "uncorrelated_N_stepSizes":
				individualDescriptor.add("coords");
                individualDescriptor.add("stepSizes");
                break;
			case "correlated_N_stepSizes":
				individualDescriptor.add("coords");
				individualDescriptor.add("stepSizes");
                individualDescriptor.add("alphas");
                break;
			default:
				throw new NotValidOperatorNameException("You did not provide a valid name for the mutation operator.");
		}

		return individualDescriptor;
	}
}