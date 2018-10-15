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

        // DEBUG
        // System.out.printf("ParentsRatio: %f\n", parentsRatio);
        // System.out.printf("ParentsSize: %d\n", parentsSize);
        // System.out.printf("PopulationSize: %d\n", population.size());
        // System.out.printf("Fitness first individual: %e\n", population.get(0).getFitness());
        // System.out.printf("Fitness last individual: %e\n", population.get(population.size()-1).getFitness());
        // System.out.println("Parents_ids and relative fitness:");
        // for (Integer i : parents_ids) System.out.printf("%d %e\n", i, population.get(i).getFitness());
        // System.out.println();
        // System.exit(0);
        
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
            // System.out.println("Contestants and relative fitness, after sorting by fitness:");
            // for (int k=0; k<tournamentSize; k++) System.out.printf("%d %e\n", contestants.get(k).first(), contestants.get(k).second());
        }

        // DEBUG
        // System.out.printf("ParentsRatio: %f\n", parentsRatio);
        // System.out.printf("ParentsSize: %d\n", parentsSize);
        // System.out.printf("PopulationSize: %d\n", population.size());
        // System.out.println("Parents_ids and relative fitness:");
        // for (Integer i : parents_ids) System.out.printf("%d %e\n", i, population.get(i).getFitness());
        // System.out.println();
        // System.exit(0);

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

        // System.err.printf("Total fitness: %e\n", total_fitness);

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

        // DEBUG
        // System.out.printf("ParentsRatio: %f\n", parentsRatio);
        // System.out.printf("ParentsSize: %d\n", parentsSize);
        // System.out.printf("PopulationSize: %d\n", population.size());
        // System.out.printf("samplingMethod: %s\n", samplingMethod);
        // System.out.println("Parents_ids and relative fitness:");
        // for (Integer i : parents_ids) System.out.printf("%d %e\n", i, population.get(i).getFitness());
        // System.out.println();
        // System.exit(0);

        return parents_ids;
    }

    public static ArrayList<Integer> ranking_selector(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException {
        CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
        Double parentsRatio = (Double) params.get("parentsRatio");
        Double s = (Double) params.get("s");
        Double base = (Double) params.get("base"); // It makes sense only when sampling method == exponential.
        String mapping = (String) params.get("mapping");
        String samplingMethod = (String) params.get("samplingMethod");
        Double ranking_scaling_factor = (Double) params.get("RS_factor"); 
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
                //  divided the rank by 10. To see why, try to plot the exponential function with and without 10 for popSize = 100        
                probabilities.add( (1 - Math.pow(base, -rank/ranking_scaling_factor)) );
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

        // DEBUG
        // System.out.printf("ParentsRatio: %f\n", parentsRatio);
        // System.out.printf("ParentsSize: %d\n", parentsSize);
        // System.out.printf("PopulationSize: %d\n", population.size());
        // System.out.printf("samplingMethod: %s\n", samplingMethod);
        // System.out.println("Parents_ids and relative fitness:");
        // for (Integer i : parents_ids) System.out.printf("%d %e\n", i, population.get(i).getFitness());
        // System.out.println();
        // System.exit(0);

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

        // System.out.printf("Fitness, prob. and cum. prob: \n");
        // for (int i=0; i<population.size(); i++) {
        //     System.out.printf("%e %f %f\n", population.get(i).getFitness(), probabilities.get(i), cumulativeProbability.get(i));
        // }

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