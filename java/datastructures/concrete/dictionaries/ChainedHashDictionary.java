package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;


import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    
    private int tableSize;
    private int count;
    
    public ChainedHashDictionary() {
        tableSize = 10;
        chains = makeArrayOfChains(tableSize);
        count = 0;
            
    }

    public ChainedHashDictionary(IDictionary<K, V>[] chains) {

        this.chains = chains;
        this.tableSize = chains.length;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        
        int index = hashCode(key);
        
        if (chains[index] != null && chains[index].containsKey(key)) {
            return chains[index].get(key);
        
        } else {
      
            throw new NoSuchKeyException();
        }
    }

    @Override
    public void put(K key, V value) {
        
    	
    	if ((count / tableSize) >= 1) {
    		resize();
    	}
    	
        int index = hashCode(key);

        
        if (chains[index] == null) {
            
            chains[index] = new ArrayDictionary<K, V>();
            chains[index].put(key, value);
            count++;
       
                
        } else {
        	
            if (!chains[index].containsKey(key)) {
                count++;
            }
            
            chains[index].put(key, value);
        }
        
        
      
    }

    @Override
    public V remove(K key) {
       
        int index = hashCode(key);
        
        if (chains[index] == null || !chains[index].containsKey(key)) {
            throw new NoSuchKeyException();
        }         
        V val = chains[index].get(key);
        chains[index].remove(key);        
        count--;
        
        return val;
    }

    @Override
    public boolean containsKey(K key) {
        int index = hashCode(key);
        
        if (chains[index] == null) {
            return false;
        }
        else {
            return chains[index].containsKey(key);
        }
    }

    @Override
    public int size() {
        return count;
    }
    
    private int hashCode(K key) {
        
        if (key == null) { 
            return 0;
        }
        
        int index = Math.abs(key.hashCode() % tableSize);
        return index;
    }
    
    private void resize() {
     
            count = 0;
            IDictionary<K, V>[] oldChains = chains; 
            
            this.tableSize*=2;
            this.chains = makeArrayOfChains(tableSize);         
               
            for (IDictionary<K, V> arrayDict : oldChains) { 	
            	if (arrayDict != null) {
            		for (KVPair<K, V> pair : arrayDict) {            			
            			put(pair.getKey(), pair.getValue());
            		}            					
            	}
            }
        }
            
    
    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }
    
    

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private Iterator<KVPair<K, V>> bucket;
        private int chainIndex = 0;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;          
        }


        @Override
        public boolean hasNext() {
            
        	if (chains.length == 0 || chainIndex > (chains.length)) {
        		
        		return false;
        	}
        	
            if (bucket == null || !bucket.hasNext()) {               
                nextBucket();                
            }
            
            if (bucket == null) {
            	return false;
            } else {
            
            	return bucket.hasNext();
        	}
 
        }

        @Override
        public KVPair<K, V> next() {
            
        	if (hasNext()) {
        		return bucket.next();
        	}
        	
        	else {
        		throw new NoSuchElementException();
        	}           
         
        }
        
        private void nextBucket() {
            
        	while (chainIndex < chains.length) {
        		if (chains[chainIndex] == null || chains[chainIndex].isEmpty()) {
        			chainIndex++;
        		}
        		else {
        			bucket = chains[chainIndex].iterator();
        			chainIndex++;
        			break;        				
        		}
        	}            
        }
    }
}
