
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