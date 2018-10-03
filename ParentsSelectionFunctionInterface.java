import java.util.ArrayList;
import java.util.HashMap;

public interface ParentsSelectionFunctionInterface {
    public ArrayList<Integer> execute(ArrayList<Individual> population, HashMap<String, Object> params) throws NotEnoughEvaluationsException;
}