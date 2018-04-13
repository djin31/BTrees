package col106.a3;

import java.util.Vector;

public class Node<Key extends Comparable<Key>,Value> {
    //private static int max_node_size;
    //private static int min_node_size;
    protected boolean isLeafNode;
    protected int size;
    protected Vector<Key> keys;
    protected Vector<Value> values;
    protected Vector<Node<Key,Value>> children;

    public Node(int max_node_size,int min_node_size) {
      //  this.max_node_size=max_node_size;
      //  this.min_node_size=min_node_size;
        keys= new Vector<Key>();
        values= new Vector<Value>();
        children=new Vector<Node<Key,Value>>();
        isLeafNode=true;
        size=0;
    }
    public Vector<Key> getKeys(int i){
        return keys;
    }
    public Vector<Value> getValues(int i){
        return values;
    }
    public int getSize() {
        return size;
    }

    public StringBuilder stringmaker() {
        StringBuilder s= new StringBuilder("[");
        if (!isLeafNode){
            int i=0;
            int j=0;
            s.append(children.get(i).stringmaker());
            while (i<keys.size()){
                s.append(", ");
                s.append(keys.get(i).toString());
                s.append("=");
                s.append(values.get(i).toString());
                s.append(", ");
                s.append(children.get(++i).stringmaker());
            }
            s.append("]");
        }
        else{
            int i=0;
            while (i<keys.size()){
                s.append(keys.get(i).toString());
                s.append("=");
                s.append(values.get(i++).toString());
                s.append(", ");
            }
            s.delete(s.length()-2,s.length());
            s.append("]");
        }
        return s;
    }

    public Vector<Value> finder(Key k){
        Vector<Value> v =new Vector<Value>();
        if (!isLeafNode){
            int i=0;
            while ((i<keys.size()) && (keys.get(i).compareTo(k)<0)){
                i++;
            }
            int j=i;
            while ((j<keys.size()) && (k.equals(keys.get(j)))) {
                v.add(values.get(j));
                j++;
            }
            for(int t=Math.max(i-1,0);t<=j;t++){
                v.addAll(children.get(t).finder(k));
            }
            
        }
        else{
            int i=0;
            while ((i<keys.size())&&(keys.get(i).compareTo(k)<0))
                i++;
            while((i<keys.size())&&(keys.get(i).compareTo(k)==0)){
                v.add(values.get(i));
                i++;
            }
        }
        return v;
    }

}
