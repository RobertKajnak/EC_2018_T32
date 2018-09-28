import java.util.HashMap;

public interface MutationFunctionInterface {
    public HashMap<String, Object> execute(HashMap<String, Object> genotype, HashMap<String, Object> params);
}