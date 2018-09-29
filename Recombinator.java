import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;

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

}