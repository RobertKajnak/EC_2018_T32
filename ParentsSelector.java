import java.util.ArrayList;
import java.util.HashMap;

public class ParentsSelector {

    public static ArrayList<Individual> best_N_selector(ArrayList<Individual> population, HashMap<String, Object> params) {
        Double parentsRatio = (Double) params.get("parentsRatio");
        Integer parentsSize = (int) (parentsRatio * population.size());

        return new ArrayList<Individual>(population.subList(0, parentsSize));
    }
}