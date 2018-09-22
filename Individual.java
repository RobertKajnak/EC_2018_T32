
import org.apache.commons.lang3.ArrayUtils;

///I made it immutable for the time being for simplicity. Also, it's a nice thing to have :P
///On a more serious note, if the fitness and coord are final, it means that it is not possible 
///to accidentally change one, while the other remains unchanged
class Individual implements Comparable<Individual>{
    private final Double coords[];
    private double fitness;
    private boolean isSet=false;
    private CompetitionCustomPack evaluation;
    
    // Constructor. Initialize individual with provided coords.
    // Random coords are provided by the EA either during initialization
    // or during recombination.
    public Individual(CompetitionCustomPack evaluation, Double[] coords) {
    	this.evaluation = evaluation;
        this.coords = coords;
        this.isSet = false;
    }

    public Double[] getCoords() {
        return this.coords;
    }
    
    public boolean isEvaluated() {
    	return isSet;
    }

    public Double getFitness() {
    	if (this.isSet)
    		return this.fitness;
    	else {
            isSet = true;
    		this.fitness = (double) evaluation.evaluate(ArrayUtils.toPrimitive(this.coords));
    		return this.fitness;
    	}
    }
    
    @Override
    public boolean equals(Object obj) {
        ///TODO add class check
        for (int i=0;i<10;i++) {
            if (((Individual)obj).coords[i]!=this.coords[i])
                return false;
        }
        return true;
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