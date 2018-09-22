import java.util.Random;
import java.util.ArrayList;

public class Recombinator {
    public static Pair<Double[], Double[]> onePointCrossover(Individual mom, Individual dad) {

        Double[] mom_coords = mom.getCoords();
        Double[] dad_coords = dad.getCoords();

        Double child1_coords[] = new Double[10];
        Double child2_coords[] = new Double[10];

        //At least 1 gene splice
        Random rnd = new Random();
        int crossoverPoint = 1 + rnd.nextInt(8);

        for (int i=0;i<10;i++) {
            if (i < crossoverPoint){
                child1_coords[i] = mom_coords[i];
                child2_coords[i] = dad_coords[i];
            }
            else {
                child1_coords[i] = dad_coords[i];
                child2_coords[i] = mom_coords[i];
            }
        }

        Pair<Double[], Double[]> offspring_coords = new Pair<Double[], Double[]>(child1_coords, child2_coords);
        return offspring_coords;
    }


}