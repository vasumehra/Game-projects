package datastructures.concrete.dictionaries;

import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using     our private tests.
    private Pair<K, V>[] pairs;
    private int size;
    // You're encouraged to add extra fields (and helper methods) though!

    public ArrayDictionary() {
        this.size = 0;
        this.pairs =  makeArrayOfPairs(10);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }

    @Override
    public V get(K key) {
        int index = indexOf(key);
        if (index == -1) {
            throw new NoSuchKeyException();
        }
        return this.pairs[index].value;
    }
    
    
    @Override
    public void put(K key, V value) {
        if (this.size == pairs.length) {
            Pair<K, V>[] newArray = makeArrayOfPairs(this.size * 2);
            for (int i = 0; i < this.size; i++) {
                newArray[i] = this.pairs[i];
            }
            this.pairs = newArray;
        }
        
        int index = indexOf(key);
        
        if (index != -1) {
            this.pairs[index].value = value;
        } else {
            this.pairs[size] = new Pair<K, V>(key, value);
            this.size++; 
        }   
    }

    @Override
    public V remove(K key) {
        int index = indexOf(key);
        
        if (index == -1) {
            throw new NoSuchKeyException();
        }
        V value = this.pairs[index].value;
        this.pairs[index] = this.pairs[this.size - 1];
        this.size--;
        return value;
    }

    @Override
    public boolean containsKey(K key) {
        return indexOf(key) != -1;
    } 

    @Override
    public int size() {
        return this.size;
    }
    
    private int indexOf(K key) {
        for (int i = 0; i < this.size; i++) {

             if ((key == null && pairs[i].key == key) 
                    || (key != null && key.equals(pairs[i].key))) {
               return i;
           }
        }
        return -1;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
    
    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        
        private Pair<K, V>[] pair;
        private int index = 0;
        private int size = 0;
        
        public ArrayDictionaryIterator(Pair<K, V>[] pair) {
            this.pair = pair;
            this.size = pair.length;
            index = 0;
        }
        
        @Override
        public boolean hasNext() {  
            
            return (index < size && pair[index] != null);      	        	
        }

        @Override
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                Pair<K, V> temp = pair[index]; 
                K key = temp.key;
                V val = temp.value;
                index++;
                return new KVPair<K, V>(key, val);
            }
        }
    }
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<K, V>(this.pairs);
    }
}

