package col106.a3;
import java.util.*;

public class BTree<Key extends Comparable<Key>,Value> implements DuplicateBTree<Key,Value> {
    private static int max_node_size;
    private static int min_node_size;
    private Node<Key,Value> root;
    private int size;
    private int height;
    private Node<Key,Value> intermediate_node;

    public BTree(int b) throws bNotEvenException {  /* Initializes an empty b-tree. Assume b is even. */
        if (b%2!=0)
            throw new bNotEvenException();
        min_node_size=b/2-1;
        max_node_size=b-1;
        size=0;
        height=0;
        root=new Node(max_node_size,min_node_size);
    }

    @Override
    public boolean isEmpty() {
        if (size==0)
            return true;
        else
            return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int height() {
        if (size==0)
            return -1;
        return height;
    }

    @Override
    public List<Value> search(Key key) throws IllegalKeyException {
        Vector<Value> v= root.finder(key);
        if (v.size()==0){
            System.out.println(this);
            throw new IllegalKeyException();
        }
        else{
            return v;
        }
    }

    @Override
    public String toString(){
        if (size==0)
            return "[]";
        StringBuilder s = root.stringmaker();
        return s.toString();
    }

    @Override
    public void insert(Key key, Value val) {
        ++size;
        if (size==1){
            root.keys.add(key);
            root.values.add(val);
            root.size++;
        }
        else{
            if(root.getSize()==max_node_size){
                Node<Key,Value> new_root = new Node<>(max_node_size,max_node_size);
                new_root.isLeafNode=false;
                new_root.children.add(root);
                root=new_root;
                splitNode(root,0,(Node)root.children.get(0));
                height++;
            }
            insertKey(root,key,val);
            //System.out.println(this);
        }
    }

    private void splitNode(Node parent, int index, Node child){
        Node<Key,Value> new_node = new Node<Key,Value>(max_node_size,min_node_size);
        new_node.isLeafNode= child.isLeafNode;
        new_node.size=min_node_size;
        for (int i=0;i<min_node_size;i++){
            new_node.keys.add((Key) child.keys.get(1+min_node_size));
            new_node.values.add((Value) child.values.get(1+min_node_size));
            child.keys.remove(1+min_node_size);
            child.values.remove(1+min_node_size);
        }
        if (!child.isLeafNode){
            for (int i=0;i<=min_node_size;i++){
                new_node.children.add((Node<Key, Value>) child.children.get(1+min_node_size));
                child.children.remove(1+min_node_size);
            }
        }
        child.size=min_node_size;
        parent.children.add(index+1,new_node);
        parent.keys.add(index,child.keys.lastElement());
        parent.values.add(index,child.values.lastElement());
        child.keys.remove(min_node_size);
        child.values.remove(min_node_size);
        parent.size++;
    }

    private void insertKey(Node<Key,Value> node,Key k,Value v){

        if (node.isLeafNode){
            int i=0;
            while ((i<node.size)&&(node.keys.get(i).compareTo(k)<=0)) {
                i++;
            }
            if (i==node.size){
                node.keys.add(k);
                node.values.add(v);
            }
            else {
                node.keys.add(i, k);
                node.values.add(i, v);
            }
            ++node.size;
        }
        else{
            int i=0;
            while ((i<node.size)&&(node.keys.get(i).compareTo(k)<=0))
                i++;

            if (node.children.get(i).size==max_node_size){
                splitNode(node,i,node.children.get(i));
                insertKey(node,k,v);
            }
            else
                insertKey(node.children.get(i),k,v);
        }
    }

    @Override
    public void delete(Key key) throws IllegalKeyException {
        if (size==0)
            throw new IllegalKeyException();
        Vector<Value> del_v = (Vector<Value>)this.search(key);
        for (int i=0;i<del_v.size();i++){
            intermediate_node=null;
            kill(null,root,key,0);
            size--;
        }
    }

    private void kill(Node<Key,Value> parent,Node<Key,Value> child,Key key,int node_index) {
        int curr_index;
        if (child.isLeafNode) {
            curr_index = child.keys.indexOf(key);
            if (curr_index < 0) {
                size++;
                return;
            }
            if ((child.size > min_node_size) || (parent == null)) {     //sufficient size or root node
                child.keys.remove(curr_index);
                child.values.remove(curr_index);
                child.size--;
                if (child.size == 0)
                    root = new Node<>(max_node_size, min_node_size);
                return;
            }

            if ((node_index > 0) && (parent.children.get(node_index - 1).size > min_node_size)) {
                Node<Key, Value> donor = parent.children.get(node_index - 1);
                child.keys.remove(curr_index);
                child.values.remove(curr_index);
                child.keys.add(0, parent.keys.get(node_index - 1));
                parent.keys.remove(node_index - 1);
                parent.keys.add(node_index-1, donor.keys.get(donor.size - 1));
                child.values.add(0, parent.values.get(node_index - 1));
                parent.values.remove(node_index - 1);
                parent.values.add(node_index-1, donor.values.get(donor.size - 1));
                donor.keys.remove(donor.size - 1);
                donor.values.remove(donor.size - 1);
                donor.size--;
                return;
            } else if ((node_index < parent.size) && (parent.children.get(node_index + 1).size > min_node_size)) {
                Node<Key, Value> donor = parent.children.get(node_index + 1);
                child.keys.remove(curr_index);
                child.values.remove(curr_index);
                child.keys.add(parent.keys.get(node_index));
                parent.keys.remove(node_index);
                parent.keys.add(node_index, donor.keys.get(0));
                child.values.add(parent.values.get(node_index));
                parent.values.remove(node_index);
                parent.values.add(node_index, donor.values.get(0));
                donor.keys.remove(0);
                donor.values.remove(0);
                donor.size--;
                return;
            } else if (node_index > 0) {
                Node<Key, Value> donor = parent.children.get(node_index - 1);
                child.keys.remove(curr_index);
                child.values.remove(curr_index);
                donor.keys.add(parent.keys.get(node_index - 1));
                donor.keys.addAll(child.keys);
                donor.values.add(parent.values.get(node_index - 1));
                donor.values.addAll(child.values);
                donor.size = donor.keys.size();
                parent.keys.remove(node_index - 1);
                parent.values.remove(node_index - 1);
                parent.children.remove(node_index);
                parent.size=parent.keys.size();
                return;
            } else {
                Node<Key, Value> donor = parent.children.get(node_index + 1);
                child.keys.remove(curr_index);
                child.values.remove(curr_index);
                child.keys.add(parent.keys.get(node_index));
                child.keys.addAll(donor.keys);
                child.values.add(parent.values.get(node_index));
                child.values.addAll(donor.values);
                child.size = child.keys.size();
                parent.keys.remove(node_index);
                parent.values.remove(node_index);
                parent.children.remove(node_index + 1);
                parent.size=parent.keys.size();

                return;
            }
        } else if ((parent == null) && (child.size == 1) && (child.children.get(0).size == min_node_size) && (child.children.get(1).size == min_node_size)) {                            //root node
            Node<Key, Value> new_root = new Node<Key, Value>(max_node_size, min_node_size);
            new_root.keys.addAll(root.children.get(0).keys);
            new_root.keys.addAll(root.keys);
            new_root.keys.addAll(root.children.get(1).keys);
            new_root.values.addAll(root.children.get(0).values);
            new_root.values.addAll(root.values);
            new_root.values.addAll(root.children.get(1).values);
            new_root.children.addAll(root.children.get(0).children);
            new_root.children.addAll(root.children.get(1).children);
            new_root.isLeafNode = root.children.get(0).isLeafNode;
            new_root.size=new_root.keys.size();
            root = new_root;
            height--;
            kill(null, root, key, 0);
            return;
        } else {
            if ((child.size==min_node_size)&&(parent!=null)){
                if (node_index>0){
                    if (parent.children.get(node_index-1).size>min_node_size){
                        rightRotate(parent,child,node_index);
                        kill(parent,parent.children.get(node_index),key,node_index);
                    }
                    else{
                        leftSiblingMerge(parent,child,node_index);
                        kill(parent,parent.children.get(node_index-1),key,node_index-1);
                    }
                }
                else{
                    if (parent.children.get(node_index+1).size>min_node_size){
                        leftRotate(parent,child,node_index);
                        kill(parent,parent.children.get(node_index),key,node_index);
                    }
                    else{
                        rightSiblingMerge(parent,child,node_index);
                        kill(parent,parent.children.get(node_index),key,node_index);
                    }

                }

            }
            else{
                curr_index = child.keys.indexOf(key);
                if (curr_index >= 0) {
                    if (child.children.get(curr_index).size>min_node_size){

                        Node<Key,Value> predecessor = findPredecessor(child,curr_index);
                        child.keys.add(curr_index,predecessor.keys.lastElement());
                        child.values.add(curr_index,predecessor.values.lastElement());
                        child.keys.remove(curr_index+1);
                        child.values.remove(curr_index+1);
                        predecessor.keys.remove(predecessor.size-1);
                        predecessor.values.remove(predecessor.size-1);
                        predecessor.size--;

                    }
                    else if (child.children.get(curr_index+1).size>min_node_size){
                        Node<Key,Value> successor = findSuccessor(child,curr_index);
                        child.keys.add(curr_index,successor.keys.firstElement());
                        child.values.add(curr_index,successor.values.firstElement());
                        child.keys.remove(curr_index+1);
                        child.values.remove(curr_index+1);
                        successor.keys.remove(0);
                        successor.values.remove(0);
                        successor.size--;
                    }
                    else{
                        rightSiblingMerge(child,child.children.get(curr_index),curr_index);
                        kill(child,child.children.get(curr_index),key,curr_index);
                    }
                }
                else{
                    int j=0;
                    while((j<child.size)&&(child.keys.get(j).compareTo(key)<0))
                        j++;
                    kill(child,child.children.get(j),key,j);
                }
            }
        }
    }


    private void leftRotate(Node<Key,Value> parent,Node<Key,Value> child,int node_index){
        Node<Key,Value> donor = parent.children.get(node_index+1);
        child.keys.add(parent.keys.get(node_index));
        parent.keys.add(node_index,donor.keys.get(0));
        parent.keys.remove(node_index+1);
        child.values.add(parent.values.get(node_index));
        parent.values.add(node_index,donor.values.get(0));
        parent.values.remove(node_index+1);     
        if (!child.isLeafNode) {
            child.children.add(donor.children.get(0));
            donor.children.remove(0);
        }
        donor.keys.remove(0);
        donor.values.remove(0);
        donor.size--;
        child.size++;
    }

    private void rightRotate(Node<Key,Value> parent,Node<Key,Value> child,int node_index){
        Node<Key,Value> donor = parent.children.get(node_index-1);
        child.keys.add(0,parent.keys.get(node_index-1));
        parent.keys.remove(node_index-1);
        parent.keys.add(node_index-1,donor.keys.get(donor.size-1));
        child.values.add(0,parent.values.get(node_index-1));
        parent.values.remove(node_index-1);
        parent.values.add(node_index-1,donor.values.get(donor.size-1));
        if (!child.isLeafNode) {
            child.children.add(0,donor.children.get(donor.size));
            donor.children.remove(donor.size);
        }
        donor.keys.remove(donor.size-1);
        donor.values.remove(donor.size-1);
        donor.size--;
        child.size++;
    }

    private void leftSiblingMerge(Node<Key,Value> parent,Node<Key,Value> child,int node_index){
        Node<Key,Value> donor = parent.children.get(node_index-1);
        donor.keys.add(parent.keys.get(node_index-1));
        donor.keys.addAll(child.keys);
        donor.values.add(parent.values.get(node_index-1));
        donor.values.addAll(child.values);
        donor.size=donor.keys.size();
        donor.children.addAll(child.children);
        parent.keys.remove(node_index-1);
        parent.values.remove(node_index-1);
        parent.children.remove(node_index);
        parent.size--;
    }

    private void rightSiblingMerge(Node<Key,Value> parent,Node<Key,Value> child,int node_index){
        Node<Key,Value> donor = parent.children.get(node_index+1);
        child.keys.add(parent.keys.get(node_index));
        child.keys.addAll(donor.keys);
        child.values.add(parent.values.get(node_index));
        child.values.addAll(donor.values);
        child.children.addAll(donor.children);
        child.size=child.keys.size();
        parent.keys.remove(node_index);
        parent.values.remove(node_index);
        parent.children.remove(node_index+1);
        parent.size--;
    }

    private Node<Key,Value> findPredecessor(Node<Key,Value> node,int node_index){
        intermediate_node=node;
        Node<Key,Value> child=node.children.get(node_index);
        while(!child.isLeafNode){
            if (child.size>min_node_size){
                intermediate_node=child;
                child=intermediate_node.children.lastElement();
            }
            else if(intermediate_node.children.get(intermediate_node.size-1).size>min_node_size) {
                rightRotate(intermediate_node,child,intermediate_node.size);
                child=intermediate_node.children.lastElement();
            }
            else{
                leftSiblingMerge(intermediate_node,child,intermediate_node.size);
                child=intermediate_node.children.lastElement();
            }

        }
        if (child.size==min_node_size){
            if(intermediate_node.children.get(intermediate_node.size-1).size>min_node_size) {
                rightRotate(intermediate_node,child,intermediate_node.size);
                child=intermediate_node.children.lastElement();

            }
            else{
                leftSiblingMerge(intermediate_node,child,intermediate_node.size);
                child=intermediate_node.children.lastElement();
            }
        }
        return child;
    }

    private Node<Key,Value> findSuccessor(Node<Key,Value> node,int node_index){
        intermediate_node=node;
        Node<Key,Value> child=node.children.get(node_index+1);
        while(!child.isLeafNode){
            if (child.size>min_node_size){
                intermediate_node=child;
                child=intermediate_node.children.firstElement();
            }
            else if(intermediate_node.children.get(1).size>min_node_size) {
                leftRotate(intermediate_node,child,0);
                child=intermediate_node.children.firstElement();
            }
            else{
                rightSiblingMerge(intermediate_node,child,0);
                child=intermediate_node.children.firstElement();
            }
        }
        if (child.size==min_node_size){
            if(intermediate_node.children.get(1).size>min_node_size) {
                leftRotate(intermediate_node,child,0);
                child=intermediate_node.children.firstElement();
            }
            else{
                rightSiblingMerge(intermediate_node,child,0);
                child=intermediate_node.children.firstElement();
            }
        }
        return child;
    }

}
