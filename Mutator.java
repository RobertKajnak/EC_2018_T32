import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;

public class Mutator {
    public static HashMap<String, Object> uniform(HashMap<String, Object> genotype, HashMap<String, Object> params){
        Random rnd = new Random();

        Double mutationRate = (Double) params.get("mutationRate");
        Double width = (Double) params.get("width");
        
        Double[] coords = (Double[]) genotype.get("coords"); 
        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() < mutationRate) { // not sure this condition should be checked - Giuseppe
                coords[i] = coords[i] + width * (rnd.nextDouble() - 0.5);
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        genotype.put("coords", coords);
        return genotype;
    }

    public static HashMap<String, Object> gaussian(HashMap<String, Object> genotype, HashMap<String, Object> params) {
        Random rnd = new Random();
        
        Double mutationRate = (Double) params.get("mutationRate");
        Double sigma = (Double) params.get("sigma"); 
        Double alpha = (Double) params.get("alpha");
        Double beta = (Double) params.get("beta");

        if ( params.containsKey("variable") && (Boolean) params.get("variable")) {
            CompetitionCustomPack evaluation = (CompetitionCustomPack) params.get("evaluation");
            sigma = sigma * Math.exp(-Math.pow(beta * ( (double) evaluation.getEvaluationLimit() - evaluation.evaluationsRemaining())/evaluation.getEvaluationLimit(), alpha));
        }
        
        Double[] coords = (Double[]) genotype.get("coords");
        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() < mutationRate) { // not sure this condition should be checked - Giuseppe
                coords[i] = coords[i] + sigma * rnd.nextGaussian();
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        genotype.put("coords", coords);
        return genotype;
    }

    public static HashMap<String, Object> uncorrelated_1_stepSize(HashMap<String, Object> genotype, HashMap<String, Object> params) {
        Random rnd = new Random();
        
        // same for all the coordinates
        Double tau = (Double) params.get("tau");

        Double stepSize = (Double) genotype.get("stepSize");
        stepSize = stepSize * Math.exp(tau * rnd.nextGaussian());
        stepSize = Math.max(stepSize, 0.05); // <-- it sets the minimum standard deviation
        
        Double[] coords = (Double[]) genotype.get("coords");
        for (int i=0; i<10; i++) {
            coords[i] = coords[i] + stepSize * rnd.nextGaussian();
            coords[i] = Math.min(5, Math.max(-5, coords[i]));
        }

        genotype.put("coords", coords);
        genotype.put("stepSize", stepSize);
        return genotype;
    } 

    public static HashMap<String, Object> uncorrelated_N_stepSizes(HashMap<String, Object> genotype, HashMap<String, Object> params) {
        Random rnd = new Random();

        Double min_std = (Double) params.get("minStd");
        Double tau = (Double) params.get("tau");
        Double tauPrime = (Double) params.get("tauPrime");
        Double commonDistribution = tauPrime * rnd.nextGaussian();

        Double[] stepSizes = (Double[]) genotype.get("stepSizes");
        Double[] coords = (Double[]) genotype.get("coords");
        for (int i=0; i<10; i++) {
            stepSizes[i] = stepSizes[i] * Math.exp(commonDistribution + tau * rnd.nextGaussian());
            stepSizes[i] = Math.max(stepSizes[i], min_std);
            coords[i] = coords[i] + stepSizes[i] * rnd.nextGaussian();
            coords[i] = Math.min(5, Math.max(-5, coords[i]));
        }

        genotype.put("coords", coords);
        genotype.put("stepSizes", stepSizes);
        return genotype;
    } 

    public static HashMap<String, Object> correlated_N_stepSizes(HashMap<String, Object> genotype, HashMap<String, Object> params) {
        /*
            Note: I don't use the mutationRate parameter since here all the coordinates are correlated
                  and it does not make sense to me to update only few of them. - Giuseppe
        */

        Random rnd = new Random();
        
        Double beta = 0.087; // 5° = 0.087 randians
        Double tau = 1.0 / Math.sqrt(2 * Math.sqrt(10));
        Double tauPrime = 1.0 / Math.sqrt(2 * 10);
        Double commonDistribution = tauPrime * rnd.nextGaussian();

        Double[] coords = (Double[]) genotype.get("coords");
        Double[] stepSizes = (Double[]) genotype.get("stepSizes");
        Double[][] alphas = (Double[][]) genotype.get("alphas");

        for (int i=0; i<10; i++) {
            stepSizes[i] = stepSizes[i] * Math.exp(commonDistribution + tau * rnd.nextGaussian());
            stepSizes[i] = Math.max(stepSizes[i], 0.05); // <-- it sets the minimum standard deviation
            for (int j=0; j<10; j++) {
                alphas[i][j] = alphas[i][j] + beta * rnd.nextGaussian();
                if (Math.abs(alphas[i][j]) > Math.PI) {
                    alphas[i][j] = alphas[i][j] - 2 * Math.PI * Math.signum(alphas[i][j]);
                }
            }
        }

        // compute covariance matrix
        double[][] cov = new double[10][10];
        for (int p=0; p<10; p++) {
            for (int q=0; q<10; q++) {
                if (p == q) cov[p][q] = 0.;
                else {
                    cov[p][q] = 0.5 * (Math.pow(stepSizes[p], 2) - Math.pow(stepSizes[q], 2)) * Math.tan(2 * alphas[p][q]);
                }
            }
        }

        MultivariateNormalDistribution mnd = new MultivariateNormalDistribution(ArrayUtils.toPrimitive(coords), cov);
        double[] deltaCoords = mnd.sample();
        
        for (int i=0; i<10; i++) 
            coords[i] = coords[i] + deltaCoords[i];

        genotype.put("coords", coords);
        genotype.put("stepSizes", stepSizes);
        genotype.put("alphas", alphas);
        return genotype;
    }
}