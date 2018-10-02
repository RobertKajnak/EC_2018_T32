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
        String samplingMethod = (String) params.get("samplingMethod");
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
        
        ArrayList<Individual> parents;
        if (samplingMethod == "rouletteWheel") {
            parents = ParentsSelector.rouletteWheel(population, probabilities, parentsSize);
        }
        else if (samplingMethod == "SUS") {
            parents = ParentsSelector.stochastic_universal_sampling(population, probabilities, parentsSize);
        }
        else {
            throw new IllegalArgumentException("The sampling method specified does not exists.");
        }

        return parents;
    }

    public static ArrayList<Individual> ranking_selector(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Double parentsRatio = (Double) params.get("parentsRatio");
        Double s = (Double) params.get("s");
        Double base = (Double) params.get("base"); // It makes sense only when sampling method == exponential.
        String mapping = (String) params.get("mapping");
        String samplingMethod = (String) params.get("samplingMethod");
        Integer parentsSize = (int) (parentsRatio * population.size());

        population = ParentsSelector.sortByFitness(evaluation, population);

        Integer populationSize = population.size();
        ArrayList<Double> probabilities = new ArrayList<Double>();
        
        if (mapping == "linear") {
            for (int i=0; i<populationSize; i++) {
                Integer rank = populationSize -i -1;            
                probabilities.add( (2.0-s)/(double)populationSize + (2*rank*(s-1))/(populationSize*(populationSize-1)) );
            }
        }
        else if (mapping == "exponential") {
            for (int i=0; i<populationSize; i++) {
                Integer rank = populationSize -i -1;            
                // Giuseppe: The normalization factor should be correct. Anyway, check it.
                probabilities.add( (1 - Math.pow(base, -rank)) / (populationSize - (1. - Math.pow(base, -(populationSize))) / (1-Math.pow(base, -1))) );
            }
        }
        else {
            throw new IllegalArgumentException("The mapping specified does not exists.");
        }

        // build the mating pool
        ArrayList<Individual> parents;
        if (samplingMethod == "rouletteWheel") {
            parents = ParentsSelector.rouletteWheel(population, probabilities, parentsSize);
        }
        else if (samplingMethod == "SUS") {
            parents = ParentsSelector.stochastic_universal_sampling(population, probabilities, parentsSize);
        }
        else {
            throw new IllegalArgumentException("The sampling method specified does not exists.");
        }

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