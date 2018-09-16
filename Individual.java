///I made it immutable for the time being for simplicity. Also, it's a nice thing to have :P
///On a more serious note, if the fitness and coord are final, it means that it is not possible 
///to accidentally change one, while the other remains unchanged
class Individual implements Comparable<Individual>{
    public final double coords[];
    public double fitness;
    
    // Constructor. Initialize individual with provided coords.
    // Random coords are provided by the EA either during initialization
    // or during recombination.
    public Individual(double[] coords) {
        this.coords = coords;
    }

    public double[] getCoords() {
        return this.coords;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return this.fitness;
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
        return this.fitness==i.fitness?0:i.fitness>this.fitness?1:-1;
    }

}