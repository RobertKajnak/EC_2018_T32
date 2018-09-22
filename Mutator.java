import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Mutator {
    public static Map<String, Object> uniformMutation(Double[] coords, double mutationRate, Double mutationStepSize){
        Random rnd = new Random();
        
        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() > mutationRate) { // not sure this condition should be checked - Giuseppe
                coords[i] = coords[i] + mutationStepSize * (rnd.nextDouble() - 0.5);
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        Map<String, Object> newState = new HashMap<String, Object>();
        newState.put("coords", coords); 

        return newState;
    }

    public static Map<String, Object> gaussianMutation(Double[] coords, double mutationRate, Double mutationStepSize) {
        Random rnd = new Random();
        
        for (int i=0; i<10; i++) {Double[] coordsObj = new Double[10];
            if (rnd.nextDouble() > mutationRate) { // not sure this condition should be checked - Giuseppe
                coords[i] = coords[i] + mutationStepSize * rnd.nextGaussian();
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        Map<String, Object> newState = new HashMap<String, Object>();
        newState.put("coords", coords); 

        return newState;
    }

    // In the following functions, I did not find any other method 
    // to return two values. Any other solution which requires less 
    // abstraction, less number of lines of code and still keep 
    // readability is definetly welcome - Giuseppe
    public static Map<String, Object> uncorrelatedMutation_1_stepSize(Double[] coords, double mutationRate, Double mutationStepSize, double tau) {
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

        Map<String, Object> newState = new HashMap<String, Object>();
        newState.put("coords", coords); 
        newState.put("mutation stepsize", mutationStepSize);

        return newState;
    } 

    public static Map<String, Object> uncorrelatedMutation_N_stepSize(Double[] coords, double mutationRate, Double[] mutationStepSizes, double tau, double tauPrime) {
        Random rnd = new Random();

        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() > mutationRate) { // not sure this condition should be checked - Giuseppe
                mutationStepSizes[i] = mutationStepSizes[i] * Math.exp(tau * rnd.nextGaussian()) * Math.exp(tauPrime * rnd.nextGaussian());
                mutationStepSizes[i] = Math.max(mutationStepSizes[i], 0.05); // <-- it sets the minimum standard deviation
                coords[i] = coords[i] + rnd.nextGaussian() * mutationStepSizes[i];
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        Map<String, Object> newState = new HashMap<String, Object>();
        newState.put("coords", coords); 
        newState.put("mutation stepsizes", mutationStepSizes);

        return newState;
    } 

    public static Map<String, Object> correlatedMutation(Double[] coords, Double[] mutationStepSizes, Double[][] alphas, double tau, double tauPrime) {
        /*
            Note: I deleted the mutationRate parameter since here all the coordinates are correlated
                  and it does not make sense to me to update only few of them. - Giuseppe
        */

        Random rnd = new Random();

        for (int i=0; i<10; i++) {
            mutationStepSizes[i] = mutationStepSizes[i] * Math.exp(tau * rnd.nextGaussian()) * Math.exp(tauPrime * rnd.nextGaussian());
            mutationStepSizes[i] = Math.max(mutationStepSizes[i], 0.05); // <-- it sets the minimum standard deviation
            for (int j=0; j<10; j++) {
                alphas[i][j] = alphas[i][j] + 0.087 * rnd.nextGaussian(); // here 0.087 = Beta = 5° (in radians)
            }
        }

        // compute covariance matrix
        double[][] cov = new double[10][10];
        for (int p=0; p<10; p++) {
            for (int q=0; q<10; q++) {
                if (p == q) cov[p][q] = 0.;
                else {
                    cov[p][q] = 0.5 * (Math.pow(mutationStepSizes[p], 2) - Math.pow(mutationStepSizes[q], 2)) * Math.tan(2 * alphas[p][q]);
                }
            }
        }

        MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(ArrayUtils.toPrimitive(coords), cov);
        double[] deltaCoords = mnd.sample();
        
        for (int i=0; i<10; i++) 
            coords[i] = coords[i] + deltaCoords[i];

        Map<String, Object> newState = new HashMap<String, Object>();
        newState.put("coords", coords); 
        newState.put("mutation stepsizes", mutationStepSizes);
        newState.put("mutation alphas", alphas);

        return newState;
    }
}