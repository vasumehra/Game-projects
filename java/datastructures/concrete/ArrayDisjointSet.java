package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<T, Integer> newSet;
    private int size;
    

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {

        pointers = new int[10];
        newSet = new ChainedHashDictionary<T, Integer>();
        size = 0;
    }
    
    private void resize() {
        int[] temp = new int[pointers.length * 2];
        for (int i = 0; i < pointers.length; i++) {
            temp[i] = pointers[i];
        }
        this.pointers = temp;
    }

    @Override
    public void makeSet(T item) {
        if (newSet.size() == pointers.length) {
            resize();
        }
        if (newSet.containsKey(item)) {
            throw new IllegalArgumentException();
        } else {
            int nodeNum = this.size;
            newSet.put(item, nodeNum);
            this.pointers[nodeNum] =  -1;
            this.size++;
        }
    }

    @Override
    public int findSet(T item) {
        if (!newSet.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int val = newSet.get(item);
        return findSetHelp(val);
    }
    
    private int findSetHelp(int id) {
        int num = pointers[id];
        if (num < 0) {
            return id;
        }
        return findSetHelp(num);
    }

    @Override
    public void union(T item1, T item2) {
        if (!newSet.containsKey(item1) || !newSet.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        int val1 = findSet(item1);
        int val2 = findSet(item2);        
        int rank1 = Math.abs(pointers[val1]);
        int rank2 = Math.abs(pointers[val2]);
        if (val1 == val2) {
            throw new IllegalArgumentException();
        }
        if (rank1 == rank2) {
            rank1++;
        } 
        if (rank1 > rank2) {
            pointers[val2] = val1;
            pointers[val1] = -rank1;
        } else {
            pointers[val1] = val2;
            pointers[val2] = -rank2;
        }

    }
}
