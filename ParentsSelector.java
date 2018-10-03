import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;

public class ParentsSelector {

    private static Random rnd = new Random();

    public static ArrayList<Integer> best_K_selector(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Double parentsRatio = (Double) params.get("parentsRatio");
        Integer parentsSize = (int) (parentsRatio * population.size());
        
        population = ParentsSelector.sortByFitness(evaluation, population);

        ArrayList<Integer> parents_ids = new ArrayList<Integer>();
        for (int i=0; i<parentsSize; i++) parents_ids.add(i);
        
        return parents_ids;
    }

    public static ArrayList<Integer> tournament_selector(ArrayList<Individual> population, HashMap<String, Object> params) {
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Integer tournamentSize = (Integer) params.get("tournamentSize");
        Double parentsRatio = (Double) params.get("parentsRatio");
        Integer parentsSize = (int) (parentsRatio * population.size());
        Random rnd = new Random();

        ArrayList<Integer> parents_ids = new ArrayList<Integer>();
        for (int i=0; i<parentsSize; i++) {
            ArrayList<Pair<Integer, Double>> contestants = new ArrayList<Pair<Integer, Double>>();
            for (int j=0; j<tournamentSize; j++) {
                Integer sampleInd_index = rnd.nextInt(population.size());
                contestants.add(new Pair<Integer, Double>(sampleInd_index, population.get(sampleInd_index).getFitness()));
            }
            Collections.sort(contestants);
            parents_ids.add(contestants.get(0).first());
        }

        return parents_ids;
    }

    public static ArrayList<Integer> fitness_proportional_selector(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
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
        
        ArrayList<Integer> parents_ids;
        if (samplingMethod == "rouletteWheel") {
            parents_ids = ParentsSelector.rouletteWheel(population, probabilities, parentsSize);
        }
        else if (samplingMethod == "SUS") {
            parents_ids = ParentsSelector.stochastic_universal_sampling(population, probabilities, parentsSize);
        }
        else {
            throw new IllegalArgumentException("The sampling method specified does not exists.");
        }

        return parents_ids;
    }

    public static ArrayList<Integer> ranking_selector(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
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
                probabilities.add( (1 - Math.pow(base, -rank)) );
            }
        }
        else {
            throw new IllegalArgumentException("The mapping specified does not exists.");
        }

        // build the mating pool
        ArrayList<Integer> parents_ids;
        if (samplingMethod == "rouletteWheel") {
            parents_ids = ParentsSelector.rouletteWheel(population, probabilities, parentsSize);
        }
        else if (samplingMethod == "SUS") {
            parents_ids = ParentsSelector.stochastic_universal_sampling(population, probabilities, parentsSize);
        }
        else {
            throw new IllegalArgumentException("The sampling method specified does not exists.");
        }

        return parents_ids;
    }

    private static ArrayList<Integer> rouletteWheel(ArrayList<Individual> population, ArrayList<Double> probabilities, Integer parentsSize) {

        // compute the cumulative probability distribution
        ArrayList<Double> cumulativeProbability = new ArrayList<Double>();
        Double cumProb = 0.0;
        for (Double prob : probabilities) {
            cumProb += prob;
            cumulativeProbability.add(cumProb);
        }
        
        ArrayList<Integer> parents_ids = new ArrayList<Integer>();
        for (int i=0; i<parentsSize; i++) {
            Double sample = rnd.nextDouble();
            Integer idx = 0;
            while (sample > cumulativeProbability.get(idx)) {
                idx += 1;
            }
            parents_ids.add(idx);
        }

        return parents_ids;
    }

    private static ArrayList<Integer> stochastic_universal_sampling(ArrayList<Individual> population, ArrayList<Double> probabilities, Integer parentsSize) {

        // compute the cumulative probability distribution
        ArrayList<Double> cumulativeProbability = new ArrayList<Double>();
        Double cumProb = 0.0;
        for (Double prob : probabilities) {
            cumProb += prob;
            cumulativeProbability.add(cumProb);
        }

        if (Math.abs(cumulativeProbability.get(population.size()-1)-1) > 1e-4) {
            for (int i=0; i<population.size(); i++) cumulativeProbability.set(i, cumulativeProbability.get(i)/cumulativeProbability.get(population.size()-1));
        }

        ArrayList<Integer> parents_ids = new ArrayList<Integer>();
        Double sample = rnd.nextDouble() / parentsSize;
        Integer numOfGeneratedParents = 0;
        Integer idx = 0;
        while (numOfGeneratedParents < parentsSize) {
            while (sample < cumulativeProbability.get(idx)) {
                parents_ids.add(idx);
                sample += 1. / parentsSize;
                numOfGeneratedParents += 1;
            }
            idx += 1;
        }

        return parents_ids;
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