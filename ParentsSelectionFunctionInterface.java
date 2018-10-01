import java.util.ArrayList;
import java.util.HashMap;

public interface ParentsSelectionFunctionInterface {
    public ArrayList<Individual> execute(ArrayList<Individual> population, HashMap<String, Object> params);
}