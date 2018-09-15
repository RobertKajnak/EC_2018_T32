import java.util.ArrayList;

public class Visualizer {
    public void printCoords(ArrayList<Individual> individuals) {
        for (Individual I : individuals) {
            double[] coords = I.getCoords();
            for (double coord : coords) {
                System.out.printf("%6.2f", coord);
            }
            System.out.printf(" --> %6.6e", I.getFitness());
            System.out.println();
        }
    }
}