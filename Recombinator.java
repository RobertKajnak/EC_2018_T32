import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;
import java.util.Collections;

public class Recombinator {
    public static Pair< HashMap<String, Object>, HashMap<String, Object> > onePointCrossover(Individual mom, Individual dad, HashMap<String, Object> params) {

        HashMap<String, Object> momGenotype = mom.getGenotype();
        HashMap<String, Object> dadGenotype = dad.getGenotype();
        HashMap<String, Object> childGenotype_1 = new HashMap<String, Object>();
        HashMap<String, Object> childGenotype_2 = new HashMap<String, Object>();

        Random rnd = new Random();

        for (String key : momGenotype.keySet()) {
            if (momGenotype.get(key) instanceof Double) {
                Double momFitness = mom.getFitness();
                Double dadFitness = dad.getFitness();
                Double alpha = momFitness / (momFitness + dadFitness);

                childGenotype_1.put(key, alpha * (Double) momGenotype.get(key));
                childGenotype_2.put(key, (1-alpha) * (Double) dadGenotype.get(key));
            }
            else if (momGenotype.get(key) instanceof Double[]) {
                Double[] momParameters = (Double[]) momGenotype.get(key);
                Double[] dadParameters = (Double[]) dadGenotype.get(key);

                int numParameters = momParameters.length;

                Double[] childParameters_1 = new Double[numParameters];
                Double[] childParameters_2 = new Double[numParameters];

                int crossoverPoint = 1 + rnd.nextInt(numParameters - 2);

                for (int i=0; i<numParameters; i++) {
                    if (i < crossoverPoint) {
                        childParameters_1[i] = momParameters[i];
                        childParameters_2[i] = dadParameters[i];
                    }
                    else {
                        childParameters_1[i] = dadParameters[i];
                        childParameters_2[i] = momParameters[i];
                    }
                }

                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
            else if (momGenotype.get(key) instanceof Double[][]) {
                // split the matrix row-wise.
                Double[][] momParameters = (Double[][]) momGenotype.get(key);
                Double[][] dadParameters = (Double[][]) dadGenotype.get(key);
    
                int numRows = momParameters.length;
    
                Double[][] childParameters_1 = new Double[numRows][numRows];
                Double[][] childParameters_2 = new Double[numRows][numRows];
    
                int crossoverPoint = 1 + rnd.nextInt(numRows - 2);
    
                for (int i=0; i<numRows; i++) {
                    if (i < crossoverPoint) {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = momParameters[i][j];
                            childParameters_2[i][j] = dadParameters[i][j];
                        }
                    }
                    else {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = dadParameters[i][j];
                            childParameters_2[i][j] = momParameters[i][j];
                        }
                    }
                }
    
                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
        }
        
        Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotypes = new Pair< HashMap<String, Object>, HashMap<String, Object> >(childGenotype_1, childGenotype_2);

        return offspringGenotypes;
    }

    public static Pair< HashMap<String, Object>, HashMap<String, Object> > multiPointCrossover(Individual mom, Individual dad, HashMap<String, Object> params) {

        HashMap<String, Object> momGenotype = mom.getGenotype();
        HashMap<String, Object> dadGenotype = dad.getGenotype();
        HashMap<String, Object> childGenotype_1 = new HashMap<String, Object>();
        HashMap<String, Object> childGenotype_2 = new HashMap<String, Object>();

        Random rnd = new Random();

        for (String key : momGenotype.keySet()) {
            if (momGenotype.get(key) instanceof Double) {
                Double momFitness = mom.getFitness();
                Double dadFitness = dad.getFitness();
                Double alpha = momFitness / (momFitness + dadFitness);

                childGenotype_1.put(key, alpha * (Double) momGenotype.get(key));
                childGenotype_2.put(key, (1-alpha) * (Double) dadGenotype.get(key));
            }
            else if (momGenotype.get(key) instanceof Double[]) {
                Double[] momParameters = (Double[]) momGenotype.get(key);
                Double[] dadParameters = (Double[]) dadGenotype.get(key);

                int numParameters = momParameters.length;

                Double[] childParameters_1 = new Double[numParameters];
                Double[] childParameters_2 = new Double[numParameters];

                ArrayList<Integer> crossoverPoints = new ArrayList<Integer>();
                Integer crossoverPoint;
                Integer n_points = rnd.nextInt(3) + 1; // from 1 up to 3 crossover points
                for (int p=0; p<n_points; p++) {
                    do {
                        crossoverPoint = 1 + rnd.nextInt(numParameters - 2); 
                    }
                    while (crossoverPoints.contains(crossoverPoint));
                    crossoverPoints.add(crossoverPoint);
                }

                Collections.sort(crossoverPoints);
                
                // System.out.println(n_points);
                // System.out.printf("Crossover points: ");
                // for (int i=0; i<n_points; i++)
                //     System.out.printf("%d ", crossoverPoints.get(i));
                // System.out.println();
                
                Integer currentCrossoverPointId = 0;
                for (int i=0; i<numParameters; i++) {
                    if (currentCrossoverPointId % 2 == 0) {
                        childParameters_1[i] = momParameters[i];
                        childParameters_2[i] = dadParameters[i];
                    }
                    else {
                        childParameters_1[i] = dadParameters[i];
                        childParameters_2[i] = momParameters[i];
                    }
                    if (currentCrossoverPointId < crossoverPoints.size() && i >= crossoverPoints.get(currentCrossoverPointId))
                        currentCrossoverPointId++;
                }

                // System.out.printf("Mom parameters: ");
                // for (int i=0; i<numParameters; i++)
                //     System.out.printf("%f", momParameters[i]);
                // System.out.println();
                // System.out.printf("Dad parameters: ");
                // for (int i=0; i<numParameters; i++)
                //     System.out.printf("%f", dadParameters[i]);
                // System.out.println();
                // System.out.printf("Child_1 parameters: ");
                // for (int i=0; i<numParameters; i++)
                //     System.out.printf("%f", childParameters_1[i]);
                // System.out.println();
                // System.out.printf("Child_2 parameters: ");
                // for (int i=0; i<numParameters; i++)
                //    System.out.printf("%f", childParameters_2[i]);
                // System.out.println();
                // System.exit(0);
                
                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
            else if (momGenotype.get(key) instanceof Double[][]) {
                // split the matrix row-wise.
                Double[][] momParameters = (Double[][]) momGenotype.get(key);
                Double[][] dadParameters = (Double[][]) dadGenotype.get(key);
    
                int numRows = momParameters.length;
    
                Double[][] childParameters_1 = new Double[numRows][numRows];
                Double[][] childParameters_2 = new Double[numRows][numRows];

                ArrayList<Integer> crossoverPoints = new ArrayList<Integer>();
                Integer crossoverPoint;
                Integer n_points = rnd.nextInt(3) + 1; // from 1 up to 3 crossover points
                for (int p=0; p<n_points; p++) {
                    do {
                        crossoverPoint = 1 + rnd.nextInt(numRows - 2); 
                    }
                    while (crossoverPoints.contains(crossoverPoint));
                    crossoverPoints.add(crossoverPoint);
                }

                Collections.sort(crossoverPoints);
                
                // System.out.println(n_points);
                // System.out.printf("Crossover points: ");
                // for (int i=0; i<n_points; i++)
                //     System.out.printf("%d ", crossoverPoints.get(i));
                // System.out.println();
                
                Integer currentCrossoverPointId = 0;
                for (int i=0; i<numRows; i++) {
                    if (currentCrossoverPointId % 2 == 0) {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = momParameters[i][j];
                            childParameters_2[i][j] = dadParameters[i][j];
                        }
                    }
                    else {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = dadParameters[i][j];
                            childParameters_2[i][j] = momParameters[i][j];
                        }
                    }
                    if (currentCrossoverPointId < crossoverPoints.size() && i >= crossoverPoints.get(currentCrossoverPointId))
                        currentCrossoverPointId++;
                }
    
                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
        }
        
        Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotypes = new Pair< HashMap<String, Object>, HashMap<String, Object> >(childGenotype_1, childGenotype_2);

        return offspringGenotypes;
    }

    public static Pair< HashMap<String, Object>, HashMap<String, Object> > simpleArithmeticCrossover(Individual mom, Individual dad, HashMap<String, Object> params) {

        HashMap<String, Object> momGenotype = mom.getGenotype();
        HashMap<String, Object> dadGenotype = dad.getGenotype();
        HashMap<String, Object> childGenotype_1 = new HashMap<String, Object>();
        HashMap<String, Object> childGenotype_2 = new HashMap<String, Object>();

        Random rnd = new Random();

        Double alpha = (Double) params.get("simpleCrossAlpha");
        
        for (String key : momGenotype.keySet()) {
            if (momGenotype.get(key) instanceof Double) {
                childGenotype_1.put(key, alpha * (Double) momGenotype.get(key));
                childGenotype_2.put(key, (1-alpha) * (Double) dadGenotype.get(key));
            }
            else if (momGenotype.get(key) instanceof Double[]) {
                Double[] momParameters = (Double[]) momGenotype.get(key);
                Double[] dadParameters = (Double[]) dadGenotype.get(key);

                int numParameters = momParameters.length;

                Double[] childParameters_1 = new Double[numParameters];
                Double[] childParameters_2 = new Double[numParameters];

                int crossoverPoint = 1 + rnd.nextInt(numParameters - 2);

                for (int i=0; i<numParameters; i++) {
                    if (i < crossoverPoint) {
                        childParameters_1[i] = momParameters[i];
                        childParameters_2[i] = dadParameters[i];
                    }
                    else {
                        childParameters_1[i] = alpha * dadParameters[i] + (1-alpha) * momParameters[i];
                        childParameters_2[i] = alpha * momParameters[i] + (1-alpha) * dadParameters[i];
                    }
                }

                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
            else if (momGenotype.get(key) instanceof Double[][]) {
                // split the matrix row-wise.
                Double[][] momParameters = (Double[][]) momGenotype.get(key);
                Double[][] dadParameters = (Double[][]) dadGenotype.get(key);
    
                int numRows = momParameters.length;
    
                Double[][] childParameters_1 = new Double[numRows][numRows];
                Double[][] childParameters_2 = new Double[numRows][numRows];
    
                int crossoverPoint = 1 + rnd.nextInt(numRows - 2);
    
                for (int i=0; i<numRows; i++) {
                    if (i < crossoverPoint) {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = momParameters[i][j];
                            childParameters_2[i][j] = dadParameters[i][j];
                        }
                    }
                    else {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = alpha * dadParameters[i][j] + (1-alpha) * momParameters[i][j];
                            childParameters_2[i][j] = alpha * momParameters[i][j] + (1-alpha) * dadParameters[i][j];
                        }
                    }
                }
    
                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
        }
        
        Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotypes = new Pair< HashMap<String, Object>, HashMap<String, Object> >(childGenotype_1, childGenotype_2);

        return offspringGenotypes;
    }

    public static Pair< HashMap<String, Object>, HashMap<String, Object> > singleArithmeticCrossover(Individual mom, Individual dad, HashMap<String, Object> params) {

        HashMap<String, Object> momGenotype = mom.getGenotype();
        HashMap<String, Object> dadGenotype = dad.getGenotype();
        HashMap<String, Object> childGenotype_1 = new HashMap<String, Object>();
        HashMap<String, Object> childGenotype_2 = new HashMap<String, Object>();

        Random rnd = new Random();

        Double alpha = (Double) params.get("singleCrossAlpha");
        
        for (String key : momGenotype.keySet()) {
            if (momGenotype.get(key) instanceof Double) {
                childGenotype_1.put(key, alpha * (Double) momGenotype.get(key));
                childGenotype_2.put(key, (1-alpha) * (Double) dadGenotype.get(key));
            }
            else if (momGenotype.get(key) instanceof Double[]) {
                Double[] momParameters = (Double[]) momGenotype.get(key);
                Double[] dadParameters = (Double[]) dadGenotype.get(key);

                int numParameters = momParameters.length;

                Double[] childParameters_1 = new Double[numParameters];
                Double[] childParameters_2 = new Double[numParameters];

                int pos = rnd.nextInt(numParameters);

                for (int i=0; i<numParameters; i++) {
                    if (i != pos) {
                        childParameters_1[i] = momParameters[i];
                        childParameters_2[i] = dadParameters[i];
                    }
                    else {
                        childParameters_1[i] = alpha * dadParameters[i] + (1-alpha) * momParameters[i];
                        childParameters_2[i] = alpha * momParameters[i] + (1-alpha) * dadParameters[i];
                    }
                }

                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
            else if (momGenotype.get(key) instanceof Double[][]) {
                // split the matrix row-wise.
                Double[][] momParameters = (Double[][]) momGenotype.get(key);
                Double[][] dadParameters = (Double[][]) dadGenotype.get(key);
    
                int numRows = momParameters.length;
    
                Double[][] childParameters_1 = new Double[numRows][numRows];
                Double[][] childParameters_2 = new Double[numRows][numRows];
    
                int pos = rnd.nextInt(numRows);
    
                for (int i=0; i<numRows; i++) {
                    if (i != pos) {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = momParameters[i][j];
                            childParameters_2[i][j] = dadParameters[i][j];
                        }
                    }
                    else {
                        for (int j=0; j<numRows; j++) {
                            childParameters_1[i][j] = alpha * dadParameters[i][j] + (1-alpha) * momParameters[i][j];
                            childParameters_2[i][j] = alpha * momParameters[i][j] + (1-alpha) * dadParameters[i][j];
                        }
                    }
                }
    
                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
        }
        
        Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotypes = new Pair< HashMap<String, Object>, HashMap<String, Object> >(childGenotype_1, childGenotype_2);

        return offspringGenotypes;
    }

    public static Pair< HashMap<String, Object>, HashMap<String, Object> > wholeArithmeticCrossover(Individual mom, Individual dad, HashMap<String, Object> params) {

        HashMap<String, Object> momGenotype = mom.getGenotype();
        HashMap<String, Object> dadGenotype = dad.getGenotype();
        HashMap<String, Object> childGenotype_1 = new HashMap<String, Object>();
        HashMap<String, Object> childGenotype_2 = new HashMap<String, Object>();

        Double alpha = (Double) params.get("wholeCrossAlpha");
        
        for (String key : momGenotype.keySet()) {
            if (momGenotype.get(key) instanceof Double) {
                childGenotype_1.put(key, alpha * (Double) momGenotype.get(key));
                childGenotype_2.put(key, (1-alpha) * (Double) dadGenotype.get(key));
            }
            else if (momGenotype.get(key) instanceof Double[]) {
                Double[] momParameters = (Double[]) momGenotype.get(key);
                Double[] dadParameters = (Double[]) dadGenotype.get(key);

                int numParameters = momParameters.length;

                Double[] childParameters_1 = new Double[numParameters];
                Double[] childParameters_2 = new Double[numParameters];

                for (int i=0; i<numParameters; i++) {
                    childParameters_1[i] = alpha * dadParameters[i] + (1-alpha) * momParameters[i];
                    childParameters_2[i] = alpha * momParameters[i] + (1-alpha) * dadParameters[i];
                }

                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
            else if (momGenotype.get(key) instanceof Double[][]) {
                // split the matrix row-wise.
                Double[][] momParameters = (Double[][]) momGenotype.get(key);
                Double[][] dadParameters = (Double[][]) dadGenotype.get(key);
    
                int numRows = momParameters.length;
    
                Double[][] childParameters_1 = new Double[numRows][numRows];
                Double[][] childParameters_2 = new Double[numRows][numRows];
    
                for (int i=0; i<numRows; i++) {
                    for (int j=0; j<numRows; j++) {
                        childParameters_1[i][j] = alpha * dadParameters[i][j] + (1-alpha) * momParameters[i][j];
                        childParameters_2[i][j] = alpha * momParameters[i][j] + (1-alpha) * dadParameters[i][j];
                    }
                }
    
                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
        }
        
        Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotypes = new Pair< HashMap<String, Object>, HashMap<String, Object> >(childGenotype_1, childGenotype_2);

        return offspringGenotypes;
    }

    public static Pair< HashMap<String, Object>, HashMap<String, Object> > blendCrossover(Individual mom, Individual dad, HashMap<String, Object> params) {

        HashMap<String, Object> momGenotype = mom.getGenotype();
        HashMap<String, Object> dadGenotype = dad.getGenotype();
        HashMap<String, Object> childGenotype_1 = new HashMap<String, Object>();
        HashMap<String, Object> childGenotype_2 = new HashMap<String, Object>();

        Random rnd = new Random();
        
        Double alpha = (Double) params.get("blendCrossAlpha");
        
        for (String key : momGenotype.keySet()) {
            if (momGenotype.get(key) instanceof Double) {
                Double momParameters = (Double) momGenotype.get(key);
                Double dadParameters = (Double) dadGenotype.get(key);

                Double max = Math.max(momParameters, dadParameters);
                Double min = Math.min(momParameters, dadParameters);
                Double gamma_1 = (1 - 2*alpha) * rnd.nextDouble() - alpha;
                Double gamma_2 = (1 - 2*alpha) * rnd.nextDouble() - alpha;

                childGenotype_1.put(key, (1-gamma_1) * momParameters + gamma_1*dadParameters);
                childGenotype_2.put(key, (1-gamma_2) * momParameters + gamma_2*dadParameters);
            }
            else if (momGenotype.get(key) instanceof Double[]) {
                Double[] momParameters = (Double[]) momGenotype.get(key);
                Double[] dadParameters = (Double[]) dadGenotype.get(key);

                int numParameters = momParameters.length;

                Double[] childParameters_1 = new Double[numParameters];
                Double[] childParameters_2 = new Double[numParameters];

                for (int i=0; i<numParameters; i++) {
                    Double max = Math.max(momParameters[i], dadParameters[i]);
                    Double min = Math.min(momParameters[i], dadParameters[i]);
                    Double gamma_1 = (1 - 2*alpha) * rnd.nextDouble() - alpha;
                    Double gamma_2 = (1 - 2*alpha) * rnd.nextDouble() - alpha;

                    childParameters_1[i] = (1-gamma_1) * momParameters[i] + gamma_1*dadParameters[i];
                    childParameters_2[i] = (1-gamma_2) * momParameters[i] + gamma_2*dadParameters[i];
                }

                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
            else if (momGenotype.get(key) instanceof Double[][]) {
                // split the matrix row-wise.
                Double[][] momParameters = (Double[][]) momGenotype.get(key);
                Double[][] dadParameters = (Double[][]) dadGenotype.get(key);
    
                int numRows = momParameters.length;
    
                Double[][] childParameters_1 = new Double[numRows][numRows];
                Double[][] childParameters_2 = new Double[numRows][numRows];
    
                for (int i=0; i<numRows; i++) {
                    for (int j=0; j<numRows; j++) {
                        Double max = Math.max(momParameters[i][j], dadParameters[i][j]);
                        Double min = Math.min(momParameters[i][j], dadParameters[i][j]);
                        Double gamma_1 = (1 - 2*alpha) * rnd.nextDouble() - alpha;
                        Double gamma_2 = (1 - 2*alpha) * rnd.nextDouble() - alpha;

                        childParameters_1[i][j] = (1-gamma_1) * momParameters[i][j] + gamma_1*dadParameters[i][j];
                        childParameters_2[i][j] = (1-gamma_2) * momParameters[i][j] + gamma_2*dadParameters[i][j];
                    }
                }
    
                childGenotype_1.put(key, childParameters_1);
                childGenotype_2.put(key, childParameters_2);
            }
        }

        Pair< HashMap<String, Object>, HashMap<String, Object> > offspringGenotypes = new Pair< HashMap<String, Object>, HashMap<String, Object> >(childGenotype_1, childGenotype_2);

        return offspringGenotypes;
    }
}