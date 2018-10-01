import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;

public class ParentsSelector {

    private static Random rnd = new Random();

    public static ArrayList<Individual> best_K_selector(ArrayList<Individual> population, HashMap<String, Object> params) {
        Double parentsRatio = (Double) params.get("parentsRatio");
        Integer parentsSize = (int) (parentsRatio * population.size());

        return new ArrayList<Individual>(population.subList(0, parentsSize));
    }

    public static ArrayList<Individual> fitness_proportional_selector(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Double parentsRatio = (Double) params.get("parentsRatio");
        Integer parentsSize = (int) (parentsRatio * population.size());

        population = ParentsSelector.sortByFitness(evaluation, population);
        
        Double total_fitness = 0.;
        for (Individual ind : population) {
            total_fitness += ind.getFitness();
        }

        ArrayList<Double> probabilities = new ArrayList<Double>();
        for (Individual ind : population) {
            probabilities.add(ind.getFitness() / total_fitness);
        }

        ArrayList<Individual> parents = ParentsSelector.stochastic_universal_sampling(population, probabilities, parentsSize);

        return parents;

    }

    private static ArrayList<Individual> rouletteWheel(ArrayList<Individual> population, ArrayList<Double> probabilities, Integer parentsSize) {

        // compute the cumulative probability distribution
        ArrayList<Double> cumulativeProbability = new ArrayList<Double>();
        Double cumProb = 0.0;
        for (Double prob : probabilities) {
            cumProb += prob;
            cumulativeProbability.add(cumProb);
        }
        
        ArrayList<Individual> parents = new ArrayList<Individual>();
        Individual parent;
        for (int i=0; i<parentsSize; i++) {
            Double sample = rnd.nextDouble();
            Integer idx = 0;
            while (sample > cumulativeProbability.get(idx)) {
                idx += 1;
            }
            parent = population.get(idx);
            parents.add(parent);
        }

        return parents;
    }

    private static ArrayList<Individual> stochastic_universal_sampling(ArrayList<Individual> population, ArrayList<Double> probabilities, Integer parentsSize) {

        // compute the cumulative probability distribution
        ArrayList<Double> cumulativeProbability = new ArrayList<Double>();
        Double cumProb = 0.0;
        for (Double prob : probabilities) {
            cumProb += prob;
            cumulativeProbability.add(cumProb);
        }

        ArrayList<Individual> parents = new ArrayList<Individual>();
        Individual parent;
        Double sample = rnd.nextDouble() / parentsSize;
        Integer numOfGeneratedParents = 0;
        Integer i = 0;
        while (numOfGeneratedParents < parentsSize) {
            while (sample < cumulativeProbability.get(i)) {
                parent = population.get(i);
                parents.add(parent);
                sample += 1. / parentsSize;
                numOfGeneratedParents += 1;
            }
            i += 1;
        }

        return parents;
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