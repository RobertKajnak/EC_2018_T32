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
    protected final Integer populationSize;
    protected final Integer offspringSize;
    protected final Double mutationRate; // percentage of offspring being mutated
    protected final Double parentsRatio; // percentage of the population that will reproduce

    // #### PARENTS SELECTION PARAMETERS ####

    // -<--- best_K_selector --->-
    // none

    // -<--- Fitness Proportional Selector --->-
    protected final String FPS_samplingMethod;

    // -<--- Ranking Selector --->- 
    protected final String mapping;
    protected final Double s;
    protected final Double base;
    protected final Double RS_factor;
    protected final String RS_samplingMethod;

    // -<--- tournament_selector --->-
    protected final Integer parents_tournamentSize;
    protected final Integer parents_RR_tournamentSize;


    // #### RECOMBINATION PARAMETERS ####

    //  -<--- Uniform Crossover --->-
    // none

    // -<--- One-point Crossover --->-
    // none

    // -<--- Multi-point Crossover --->-
    protected final Integer n_points;

    // -<--- Simple Arithmetic Crossover --->-
    protected final Double simpleCrossAlpha;

    // -<--- Single Arithmetic Crossover --->-
    protected final Double singleCrossAlpha;

    // -<--- Whole Arithmetic Crossover --->-
    protected final Double wholeCrossAlpha;

    // -<--- Blend Arithmetic Crossover --->-
    protected final Double blendCrossAlpha;



    // #### MUTATION PARAMETERS ####

    // -<--- Uniform Mutation --->-
    protected final Double uniformWidth; 

    // -<--- Gaussian Mutation --->-
    protected final Double gaussianStd;
    protected final Double gaussianAlpha;
    protected final Double gaussianBeta;
    protected final Boolean variable;

    // -<--- Uncorrelated 1 stepSize Mutation --->-
    protected final Double oneStepTau;

    // -<--- Uncorrelated N stepSizes Mutation --->-
    protected final Double tau;
    protected final Double tauPrime;
    protected final Double minStd;

    // -<--- Correlated N stepSizes Mutation --->-
    // none
    
    
    // #### SURVIVAL SELECTION PARAMETERS ####

    // -<--- mu + lambda --->-
    // none

    // -<--- mu, lambda --->-
    // none

    // -<--- round robin tournament --->-
    protected final Integer survivor_RR_tournamentSize;
    protected final Integer survivor_tournamentSize;
    

    public Island(
        Integer populationSize,
        Integer offspringSize,
        Double mutationRate,
        Double parentsRatio,
        Double gaussianStd,
        Double gaussianAlpha,
        Double gaussianBeta,
        Integer parents_tournamentSize,
        Double s,
        Double RS_factor,
        Double tau,
        Double tauPrime,
        Double minStd,
        Integer survivor_RR_tournamentSize,
        Integer survivor_tournamentSize,
        Integer parents_RR_tournamentSize
        ) {
            this.populationSize = populationSize;
            this.offspringSize = offspringSize;
            this.mutationRate = mutationRate;
            this.parentsRatio = parentsRatio;
            this.gaussianStd = gaussianStd;
            this.gaussianAlpha = gaussianAlpha;
            this.gaussianBeta = gaussianBeta;
            this.parents_tournamentSize = parents_tournamentSize;
            this.s = s;
            this.RS_factor = RS_factor;
            this.minStd = minStd;
            this.survivor_RR_tournamentSize = survivor_RR_tournamentSize;
            this.survivor_tournamentSize = survivor_tournamentSize;
            this.parents_RR_tournamentSize = parents_RR_tournamentSize;

            if (tau == null) this.tau = null;
            else this.tau = tau / Math.sqrt(2 * Math.sqrt(10));

            if (tauPrime == null) this.tauPrime = null;
            else this.tauPrime = tauPrime / Math.sqrt(2 * 10);
            
            if (tau == null) this.oneStepTau = null;
            else this.oneStepTau = tau / Math.sqrt(2 * Math.sqrt(10));

            this.FPS_samplingMethod = "SUS";
            this.mapping = "linear";
            this.base = 2.718;
            this.RS_samplingMethod = "SUS";
            this.n_points = 3;
            this.simpleCrossAlpha = 0.5;
            this.singleCrossAlpha = 0.5;
            this.wholeCrossAlpha = 0.2;
            this.blendCrossAlpha = 0.5;
            this.uniformWidth = 0.07;
            this.variable = true;
        }

    public HashMap<String, Object> getEAParams() {
        HashMap<String, Object> EAParams = new HashMap<String, Object>();

        EAParams.put("populationSize", populationSize);
        EAParams.put("mutationRate", mutationRate);	
        EAParams.put("offspringSize", offspringSize);
        EAParams.put("parentsRatio", parentsRatio);	

        return EAParams;
    }

    public HashMap<String, Object> getParentsSelectionDescriptor(String parentsSelectionOperatorName) {
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
                params.put("RS_factor", RS_factor);
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
        }

        parentsSelectionDescriptor.put("params", params);

        return parentsSelectionDescriptor;
    }

    public HashMap<String, Object> getRecombinationDescriptor(String recombinationOperatorName) {
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
        }

        recombinationDescriptor.put("params", params);

        return recombinationDescriptor;
    }

    public HashMap<String, Object> getMutationDescriptor(String mutationOperatorName) {
        HashMap<String, Object> mutationDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        // TODO add other fields to support operators which need additional parameters.
        mutationDescriptor.put("operatorName", mutationOperatorName);

        switch (mutationOperatorName) {
            case "uniform":
                params.put("width", uniformWidth);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uniform(genotype, params);}
                });
                break;
            case "gaussian":
                params.put("sigma", gaussianStd);
                params.put("alpha", gaussianAlpha);
                params.put("beta", gaussianBeta);
                params.put("variable", variable);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.gaussian(genotype, params);}
                });
                break;
            case "uncorrelated_1_stepSize":
                params.put("tau", oneStepTau);
                mutationDescriptor.put("call", new MutationFunctionInterface() {
                    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params) 
                        {return Mutator.uncorrelated_1_stepSize(genotype, params);}
                });
                break;
            case "uncorrelated_N_stepSizes":
                params.put("tau", tau);
                params.put("tauPrime", tauPrime);
                params.put("minStd", minStd);
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
        }

        mutationDescriptor.put("params", params);

        return mutationDescriptor;
    }

    public HashMap<String, Object> getSurvivorSelectionDescriptor(String survivorSelectionOperatorName) {
        HashMap<String, Object> survivorSelectionDescriptor = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("operatorName", survivorSelectionOperatorName);

        switch (survivorSelectionOperatorName) {
            case "muPlusLambda":
                survivorSelectionDescriptor.put("call", new SurvivorSelectionFunctionInterface() {
                public ArrayList<Individual> execute(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
                    return SurvivorSelector.muPlusLambda(population, offspring, params);}
                });
                break;
            case "muLambda":
                survivorSelectionDescriptor.put("call", new SurvivorSelectionFunctionInterface() {
                public ArrayList<Individual> execute(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
                    return SurvivorSelector.muLambda(population, offspring, params);}
                });
                break;
            case "round_robin_tournament":
                params.put("tournamentSize", survivor_RR_tournamentSize);
                survivorSelectionDescriptor.put("call", new SurvivorSelectionFunctionInterface() {
                public ArrayList<Individual> execute(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
                    return SurvivorSelector.round_robin_tournament(population, offspring, params);}
                });
                break;
        }

        survivorSelectionDescriptor.put("params", params);

        return survivorSelectionDescriptor;
    }

    public ArrayList<String> getIndividualDescriptor(String recombinationOperatorName, String mutationOperatorName) {
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
		}

		return individualDescriptor;
	}
}