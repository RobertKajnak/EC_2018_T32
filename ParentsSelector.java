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

    public static ArrayList<Individual> tournament_selector(ArrayList<Individual> population, HashMap<String, Object> params) {
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Integer tournamentSize = (Integer) params.get("tournamentSize");
        Double parentsRatio = (Double) params.get("parentsRatio");
        Integer parentsSize = (int) (parentsRatio * population.size());
        Random rnd = new Random();

        ArrayList<Individual> parents = new ArrayList<Individual>();
        for (int i=0; i<parentsSize; i++) {
            ArrayList<Pair<Integer, Double>> contestants = new ArrayList<Pair<Integer, Double>>();
            for (int j=0; j<tournamentSize; j++) {
                Integer sampleInd_index = rnd.nextInt(population.size());
                contestants.add(new Pair<Integer, Double>(sampleInd_index, population.get(sampleInd_index).getFitness()));
            }
            Collections.sort(contestants);
            parents.add(population.get(contestants.get(0).first()));
        }

        return parents;
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
                probabilities.add( (1 - Math.pow(base, -rank)) );
                // System.out.printf("fitness: %e, rank: %d, prob: %e\n", population.get(i).getFitness(), rank, probabilities.get(i));
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

        // for (int i = 0; i<parentsSize; i++) {
        //     System.out.printf("%e\t%e\n", population.get(i).getFitness(), parents.get(i).getFitness());
        // }

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
        // System.out.println(cumulativeProbability.get(population.size()-1));
        if (Math.abs(cumulativeProbability.get(population.size()-1)-1) > 1e-4) {
            // System.out.println("#####################################");
            for (int i=0; i<population.size(); i++) cumulativeProbability.set(i, cumulativeProbability.get(i)/cumulativeProbability.get(population.size()-1));
        }
        
        // for (int i = 0; i < population.size(); i++) {
        //     System.out.printf("Fitness: %.6e, probability: %f, cum. probability: %f\n", population.get(i).getFitness(), probabilities.get(i), cumulativeProbability.get(i));
        // }

        ArrayList<Individual> parents = new ArrayList<Individual>();
        Individual parent;
        Double sample = rnd.nextDouble() / parentsSize;
        // System.out.printf("Sample is: %f\n", sample);
        Integer numOfGeneratedParents = 0;
        Integer i = 0;
        while (numOfGeneratedParents < parentsSize) {
            while (sample < cumulativeProbability.get(i)) {
                // System.err.printf("Sample is: %f, i: %d, cum.prob: %f\n", sample, i, cumulativeProbability.get(i));
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