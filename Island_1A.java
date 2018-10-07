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

public class Island_1A extends Island{
    private static final String parentsSelectionOperatorName = "best_K_selector";
    private static final String recombinationOperatorName = "multiPointCrossover";
    private static final String mutationOperatorName = "uncorrelated_N_stepSizes";
    private static final String survivorSelectionOperatorName = "round_robin_tournament";

    public static HashMap<String, Object> getParentsSelectionDescriptor() throws NotValidOperatorNameException {
        return getParentsSelectionDescriptor(parentsSelectionOperatorName);
    }
    public static HashMap<String, Object> getRecombinationDescriptor() throws NotValidOperatorNameException {
        return getRecombinationDescriptor(recombinationOperatorName);
    }
    public static HashMap<String, Object> getMutationDescriptor() throws NotValidOperatorNameException {
        return getMutationDescriptor(mutationOperatorName);
    }
    public static HashMap<String, Object> getSurvivorSelectionDescriptor() throws NotValidOperatorNameException {
        return getSurvivorSelectionDescriptor(survivorSelectionOperatorName);
    }
    public static ArrayList<String> getIndividualDescriptor() throws NotValidOperatorNameException {
        return getIndividualDescriptor(recombinationOperatorName, mutationOperatorName);
    }
}