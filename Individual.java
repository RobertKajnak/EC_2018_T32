import org.vu.contest.ContestEvaluation;

///I made it immutable for the time being for simplicity. Also, it's a nice thing to have :P
///On a more serious note, if the fitness and coord are final, it means that it is not possible 
///to accidentally change one, while the other remains unchanged
class Individual implements Comparable<Individual>{
    private final double coords[];
    private double fitness;
    private boolean isSet=false;
    private ContestEvaluation evaluation;
    
    // Constructor. Initialize individual with provided coords.
    // Random coords are provided by the EA either during initialization
    // or during recombination.
    public Individual(ContestEvaluation evaluation, double[] coords) {
    	this.evaluation = evaluation;
        this.coords = coords;
        this.isSet = false;
    }

    public double[] getCoords() {
        return this.coords;
    }

    public double getFitness() {
    	if (this.isSet)
    		return this.fitness;
    	else {
    		isSet = true;
    		this.fitness = (double)evaluation.evaluate(this.coords);
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