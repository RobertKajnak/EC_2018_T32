public class Pair<T1,T2> implements Comparable<Pair> {

    private final T1 object1;
    private final T2 object2;
     
    public Pair(T1 object1, T2 object2) {
        this.object1= object1;
        this.object2 = object2;
    }

    public T1 first() {
        return object1;
    }

    public T2 second() {
        return object2;
    }

    public int compareTo(Pair p) {
        if (p.second() instanceof Integer) {
            if ((int) this.second() > (int)p.second()) 
                return -1;
            else if ((int) this.second() < (int)p.second())
                return 1;
            else return 0;
        }
        if (p.second() instanceof Double) {
            return ((double) this.second() >= (double)p.second()) ? -1 : 1;
        }
        else {
            return 0;
        }
    }
}