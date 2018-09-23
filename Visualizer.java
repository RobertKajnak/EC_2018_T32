import java.util.ArrayList;

public class Visualizer {
    public void printCoords(ArrayList<Individual> individuals) {
        for (Individual I : individuals) {
            Double[] coords = (Double[]) I.getGenotype().get("coords");
            for (Double coord : coords) {
                System.out.printf("%6.2f", coord);
            }
            System.out.printf(" --> %6.6e", I.getFitness());
            System.out.println();
        }
    }
}