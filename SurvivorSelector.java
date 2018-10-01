import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class SurvivorSelector {

    public static ArrayList<Individual> mu_plus_lambda(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params)  throws NotEnoughEvaluationsException {
        
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Integer populationSize = (Integer) population.size();

        population.addAll(offspring);
        population = SurvivorSelector.sortByFitness(evaluation, population);

        return new ArrayList<Individual>(population.subList(0, populationSize));
    }

    private static ArrayList<Individual> sortByFitness(CompetitionCustomPack evaluation, ArrayList<Individual> population) throws NotEnoughEvaluationsException {
        int cost=0;
        for (Individual ind : population) {
            if (!ind.isEvaluated()) {
                cost++;
            }
        }
        if (cost>evaluation.evaluationsRemaining())
            throw new NotEnoughEvaluationsException();
        Collections.sort(population);

        return population;
    }

}