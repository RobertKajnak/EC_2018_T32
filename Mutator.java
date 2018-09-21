import java.util.Random;

public class Mutator {
    public static double[] uniformMuatation(double[] coords, double mutationRate, double mutationSwing){
        Random rnd = new Random();
        
        for (int i=0; i<10; i++) {
            if (rnd.nextDouble() > mutationRate) {
                coords[i] = coords[i] + (rnd.nextDouble() - 0.5) * mutationSwing;
                coords[i] = Math.min(5, Math.max(-5, coords[i]));
            }
        }

        return coords;
    }
}