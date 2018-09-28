import java.util.ArrayList;

public class Selector {
    public static ArrayList<Individual> selectFirst(ArrayList<Individual> groupIndividuals, int max_size) {
        	return new ArrayList<Individual>(groupIndividuals.subList(0, max_size));
    }
}