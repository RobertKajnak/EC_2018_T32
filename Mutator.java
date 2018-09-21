import java.util.Random;
import java.util.ArrayList;

public class Mutator {
    public static double[] uniformMutation(double[] coords, double mutationRate, double mutationStepSize){
        Random rnd = new Random();
        
        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() > mutationRate) { // not sure this condition should be checked - Giuseppe
                coords[i] = coords[i] + mutationStepSize * (rnd.nextDouble() - 0.5);
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        return coords;
    }

    public static double[] gaussianMutation(double[] coords, double mutationRate, double mutationStepSize) {
        Random rnd = new Random();
        
        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() > mutationRate) { // not sure this condition should be checked - Giuseppe
                coords[i] = coords[i] + mutationStepSize * rnd.nextGaussian();
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        return coords;
    }

    // In the following functions, I did not find any other method 
    // to return two values. Any other solution which requires less 
    // abstraction, less number of lines of code and still keep 
    // readability is definetly welcome - Giuseppe
    public static Pair<ArrayList<Double>, Double> uncorrelatedMutation_1_stepSize(double[] coords, double mutationRate, double mutationStepSize, double tau) {
        Random rnd = new Random();
        
        // same for all the coordinates
        mutationStepSize = mutationStepSize * Math.exp(tau * rnd.nextGaussian());
        mutationStepSize = Math.max(mutationStepSize, 0.05); // <-- it sets the minimum standard deviation

        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() > mutationRate) { // not sure this condition should be checked - Giuseppe
                coords[i] = coords[i] + rnd.nextGaussian() * mutationStepSize;
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        ArrayList<Double> Coords = new ArrayList<Double>();
        for (double d: coords) Coords.add(d);

        Double MutationStepSize = new Double(mutationStepSize);

        return new Pair<ArrayList<Double>, Double>(Coords, MutationStepSize);
    } 

    public static Pair<ArrayList<Double>, ArrayList<Double> > uncorrelatedMutation_N_stepSize(double[] coords, double mutationRate, double[] mutationStepSize, double tau, double tauPrime) {
        Random rnd = new Random();

        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() > mutationRate) { // not sure this condition should be checked - Giuseppe
                mutationStepSize[i] = mutationStepSize[i] * Math.exp(tau * rnd.nextGaussian()) * Math.exp(tauPrime * rnd.nextGaussian());
                mutationStepSize[i] = Math.max(mutationStepSize[i], 0.05); // <-- it sets the minimum standard deviation
                coords[i] = coords[i] + rnd.nextGaussian() * mutationStepSize[i];
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        ArrayList<Double> Coords = new ArrayList<Double>();
        for (double d: coords) Coords.add(d);

        ArrayList<Double> MutationStepSize = new ArrayList<Double>();
        for (double d: mutationStepSize) MutationStepSize.add(d);

        return new Pair<ArrayList<Double>, ArrayList<Double> >(Coords, MutationStepSize);
    } 
}