package col106.a3;

public class KeyValuePair<Key extends Comparable<Key>,Value> {
    Key k;
    Value v;

    public KeyValuePair(Key k,Value v){
        this.k=k;
        this.v=v;
    }
}
