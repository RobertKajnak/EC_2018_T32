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

public class Island_2A extends Island {

    private final String parentsSelectionOperatorName = "tournament_selector";
    private final String recombinationOperatorName = "multiPointCrossover";
    private final String mutationOperatorName = "uncorrelated_N_stepSizes";
    private final String survivorSelectionOperatorName = "muPlusLambda";

    public Island_2A(HashMap<String, Object> paramVector) {
        super(
            (Integer) paramVector.get("populationSize"),
            (Integer) paramVector.get("offspringSize"),
            (Double) paramVector.get("mutationRate"),
            (Double) paramVector.get("parentsRatio"),
            (Double) paramVector.get("gaussianStd"),
            (Double) paramVector.get("gaussianAlpha"),
            (Double) paramVector.get("gaussianBeta"),
            (Integer) paramVector.get("parents_tournamentSize"),
            (Double) paramVector.get("s"),
            (Double) paramVector.get("RS_factor"),
            (Double) paramVector.get("tau"),
            (Double) paramVector.get("tauPrime"),
            (Double) paramVector.get("minStd"),
            (Integer) paramVector.get("survivor_RR_tournamentSize"),
            (Integer) paramVector.get("survivor_tournamentSize"),
            (Integer) paramVector.get("parents_tournamentSize")
        );
    }   

    public HashMap<String, Object> getParentsSelectionDescriptor() {
        return getParentsSelectionDescriptor(parentsSelectionOperatorName);
    }
    public HashMap<String, Object> getRecombinationDescriptor() {
        return getRecombinationDescriptor(recombinationOperatorName);
    }
    public HashMap<String, Object> getMutationDescriptor() {
        return getMutationDescriptor(mutationOperatorName);
    }
    public HashMap<String, Object> getSurvivorSelectionDescriptor() {
        return getSurvivorSelectionDescriptor(survivorSelectionOperatorName);
    }
    public ArrayList<String> getIndividualDescriptor() {
        return getIndividualDescriptor(recombinationOperatorName, mutationOperatorName);
    }
}