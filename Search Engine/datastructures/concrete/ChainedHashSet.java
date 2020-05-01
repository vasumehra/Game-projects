package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See ISet for more details on what each method is supposed to do.
 */
public class ChainedHashSet<T> implements ISet<T> {
    // This should be the only field you need
    private IDictionary<T, Boolean> map;

    public ChainedHashSet() {
        // No need to change this method
        this.map = new ChainedHashDictionary<>();
    }

    @Override
    public void add(T item) {
        //throw new NotYetImplementedException();
        if (!map.containsKey(item)) {
            map.put(item, true);
        }
    }

    @Override
    public void remove(T item) {
        //throw new NotYetImplementedException();
        if (!map.containsKey(item)) {
            throw new NoSuchElementException();
        } else {
            map.remove(item);
        }
        
    }

    @Override
    public boolean contains(T item) {
        //throw new NotYetImplementedException();
        return map.containsKey(item);
    }

    @Override
    public int size() {
        //throw new NotYetImplementedException();
        return map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return new SetIterator<>(this.map.iterator());
    }

    private static class SetIterator<T> implements Iterator<T> {
        // This should be the only field you need
        private Iterator<KVPair<T, Boolean>> iter;

        public SetIterator(Iterator<KVPair<T, Boolean>> iter) {
            // No need to change this method.
            this.iter = iter;
        }

        @Override
        public boolean hasNext() {
            //throw new NotYetImplementedException();
            return iter.hasNext();
        }

        @Override
        public T next() {
            //throw new NotYetImplementedException();
            if (iter.hasNext()) {
                T item = iter.next().getKey();
                return item;
            }
            throw new NoSuchElementException();
        }
    }
}
