public class EAHandler {
    private Double best_fitness;
    private Double last_fitness;
    private Double[] best_coords;
    private Integer num_of_generations_without_improvement;

    public EAHandler() {
        this.best_fitness = 0.;
        this.last_fitness = 0.;
        this.best_coords = new Double[10];
        this.num_of_generations_without_improvement = 0;
    }

    public void update(EA EA_) {
        Individual best_individual = EA_.getBestIndividual();
        this.best_fitness = best_individual.getFitness();
        this.best_coords = (Double[]) best_individual.getGenotype().get("coords");

        // there has been an improvement
        if (best_fitness - last_fitness > 1e-4) {
            this.last_fitness = this.best_fitness;
        }
        else {
            this.num_of_generations_without_improvement += 1;
        }
    }

    public Boolean allow_evolution() {
        return this.num_of_generations_without_improvement <= 25;
    }

    public void reset() {
        this.num_of_generations_without_improvement = 0;
    }
}