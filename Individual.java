
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.HashMap;

///I made it immutable for the time being for simplicity. Also, it's a nice thing to have :P
///On a more serious note, if the fitness and coord are final, it means that it is not possible 
///to accidentally change one, while the other remains unchanged
@SuppressWarnings("overrides")
class Individual implements Comparable<Individual>{
    private final HashMap<String, Object> genotype; 
    private double fitness;
    private boolean isSet=false;
    private CompetitionCustomPack evaluation;

    public Individual(CompetitionCustomPack evaluation, HashMap<String, Object> genotype) {
        this.evaluation = evaluation;
        this.genotype = genotype;
        this.isSet = false;
    }

    public HashMap<String, Object> getGenotype() {
        return this.genotype;
    }

/*
    DEPERCATED

    //
         TODO: too many constructors. Moreover, when new recombination and mutations
               will be added, new constructors will be required with combination of all
               necessary parameters for the couple (recomb. operator, mutation operator).
               
               I suggest to create a class, such as EAConfiguration, which holds all the  
               parameters and pass this class to the individual. Based on which parameters 
               are set, the Individual updates its genotype. - Giuseppe
    //  
    public Individual(CompetitionCustomPack evaluation, Map<String, Object> genotype) {
        this.evaluation = evaluation;
        this.genotype = genotype;
        this.isSet = false;
    }

    public Individual(CompetitionCustomPack evaluation, Double[] coords) {
        this.setup(evaluation);

    	genotype = new HashMap<String, Object>();
        genotype.put("coords", coords);
    }

    public Individual(CompetitionCustomPack evaluation, Double[] coords, Double stepSize) {
        this.setup(evaluation);
        
        genotype = new HashMap<String, Object>();
        genotype.put("coords", coords);
        genotype.put("stepSize", stepSize);
    }

    public Individual(CompetitionCustomPack evaluation, Double[] coords, Double[] stepSizes) {
        this.setup(evaluation);
        
        genotype = new HashMap<String, Object>();
        genotype.put("coords", coords);
        genotype.put("stepSizes", stepSizes);
    }

    public Individual(CompetitionCustomPack evaluation, Double[] coords, Double[] stepSizes, Double[] alphas) {
        this.setup(evaluation);

        genotype = new HashMap<String, Object>();
        genotype.put("coords", coords);
        genotype.put("stepSizes", stepSizes);
        genotype.put("alphas", alphas);
    }

    private void setup(CompetitionCustomPack evaluation) {

    }

    public Double[] getCoords() {
        return (Double[]) this.genotype.get("coords");
    }

    public Double getStepSize() {
        System.out.println(this.genotype.get("stepSize"));
        return 0.1;
        // return new Double(this.genotype.get("stepSize").toString());
    }
    
    public Double[] getStepSizes() {
        return (Double[]) this.genotype.get("stepSizes");
    }

    public Double[] getAlphas() {
        return (Double[]) this.genotype.get("alphas");
    }

*/
    
    public boolean isEvaluated() {
    	return isSet;
    }

    public Double getFitness() {
    	if (this.isSet)
    		return this.fitness;
    	else {
            isSet = true;
            Double[] coords = (Double[]) this.genotype.get("coords");
    		this.fitness = (double) evaluation.evaluate(ArrayUtils.toPrimitive(coords));
    		return this.fitness;
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
        // TODO: check that this works. I'm still confused by the way Java handle exceptions - Giuseppe
        if (obj instanceof Individual) {    
            Double[] objCoords = (Double[]) ((Individual)obj).getGenotype().get("coords");
            Double[] coords = (Double[]) this.genotype.get("coords");
            for (int i=0;i<10;i++) {
                if ( objCoords[i] != coords[i])
                    return false;
            }
            return true;
        }
        else {
            throw new IllegalArgumentException("The argument passed to equals(Object obj) method is not of type `Individual`.");
        }
    }

    @Override
    /// both the same fitness=>0
    /// i.fitness<this.fitness => -1
    /// i.fitness>this.fitness => 1
    public int compareTo(Individual i) {
    	double fitness = this.getFitness();
        return fitness==i.fitness?0:i.getFitness()>fitness?1:-1;
    }

}