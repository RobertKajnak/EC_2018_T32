import java.util.ArrayList;
import java.util.HashMap;

public interface SurvivorSelectionFunctionInterface {
    public ArrayList<Individual> execute(ArrayList<Individual> population, ArrayList<Individual> offspring, HashMap<String, Object> params)  throws NotEnoughEvaluationsException;
}