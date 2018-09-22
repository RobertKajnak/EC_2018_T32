import java.util.Random;
import java.util.ArrayList;

public class Recombinator {
    public static Pair<double[], double[]> onePointCrossover(Individual mom, Individual dad) {

        double[] mom_coords = mom.getCoords();
        double[] dad_coords = dad.getCoords();

        double child1_coords[] = new double[10];
        double child2_coords[] = new double[10];

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

        Pair<double[], double[]> offspring_coords = new Pair<double[], double[]>(child1_coords, child2_coords);
        return offspring_coords;
    }


}