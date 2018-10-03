import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Random;

public class SurvivorSelector {

    public static ArrayList<Individual> mu_plus_lambda(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params)  throws NotEnoughEvaluationsException {
        assert offspring.size() >= population.size();

        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Integer populationSize = (Integer) population.size();

        population = SurvivorSelector.sortByFitness(evaluation, offspring);

        return new ArrayList<Individual>(population.subList(0, populationSize));
    }

    public static ArrayList<Individual> mu_lambda(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params)  throws NotEnoughEvaluationsException {
        
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Integer populationSize = (Integer) population.size();

        population.addAll(offspring);
        population = SurvivorSelector.sortByFitness(evaluation, population);

        return new ArrayList<Individual>(population.subList(0, populationSize));
    }

    public static ArrayList<Individual> round_robin_tournament(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params)  throws NotEnoughEvaluationsException {
        
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Integer q = (Integer) params.get("tournamentSize");
        Integer populationSize = (Integer) population.size();
        Random rnd = new Random();
        ArrayList<Pair<Integer, Integer>> wins = new ArrayList<Pair<Integer, Integer>>();

        population.addAll(offspring);
        population = SurvivorSelector.sortByFitness(evaluation, population);

        for (int i=0; i<population.size(); i++) {
            Individual ind = population.get(i);
            Double ind_fitness = ind.getFitness();
            Integer win = 0;
            // System.out.printf("ind_fitness: %e, opp_fitness: ", ind_fitness);
            for (int j=0; j<q; j++) {
                // select opponent
                Individual opponent = population.get(rnd.nextInt(population.size()));
                Double opp_fitness = opponent.getFitness();
                // System.out.printf("%e, ", opp_fitness);
                // battle
                if (ind_fitness >= opp_fitness) {
                    win++;
                }
            }
            wins.add(new Pair<Integer, Integer>(i, win));
            // System.out.printf("win: %d\n", win);
        }
        // System.exit(0);

        Collections.sort(wins);

        ArrayList<Individual> newPopulation = new ArrayList<Individual>();
        for (int i=0; i<populationSize; i++) {
            newPopulation.add(population.get(wins.get(i).first()));
        }

        return newPopulation;
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