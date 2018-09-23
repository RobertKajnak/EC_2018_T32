import java.util.HashMap;

public interface RecombinationFunctionInterface {
    public Pair< HashMap<String, Object>, HashMap<String, Object> > execute(Individual mom, Individual dad);
}