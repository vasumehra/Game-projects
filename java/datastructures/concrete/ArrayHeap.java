package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;


/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int elementCount;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        
        heap = makeArrayOfT(10);
        elementCount = 0; 
    }
    
    public ArrayHeap(int size) {
        heap = makeArrayOfT(size);  
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        if (elementCount == 0) {
            throw new EmptyContainerException();
        }
        
        if (elementCount == 1) {
            T val = heap[0];
            elementCount--;
            return val;
        } else {
            elementCount--;
            T minItem = heap[0];
            heap[0] = heap[elementCount];
            heap[elementCount] = null;
            //
            sortHeap1(0, NUM_CHILDREN);
            //sortHeap();
            //
            return minItem;
        }   
                
    }
    
    private void sortHeap1(int parent, int NUM_CHILDREN) {
        int childIndex = NUM_CHILDREN * parent + 1;
        if(heap[childIndex] != null) {
            T smallest = heap[childIndex];
            for(int i = 2; i <= NUM_CHILDREN; i++) {
                int index = NUM_CHILDREN * parent + i;
                if(heap[index] != null) {
                    T nextIndex = heap[index];
                    if(smallest.compareTo(nextIndex) > 0) {
                        smallest = nextIndex;
                        childIndex = index;
                    }
                }
            }
            if(heap[parent].compareTo(smallest) > 0) {
                T temp = heap[parent];
                heap[parent] = smallest;
                heap[childIndex] = temp;
                if((NUM_CHILDREN * childIndex + 1) < heap.length - 4) {
                    sortHeap1(childIndex, NUM_CHILDREN);
                }
            }
        }
    }
    
    private void swap(int index1, int index2) {
        T temp = heap[index1];
        T temp2 = heap[index2];
        heap[index1] = temp2;
        heap[index2] = temp;    
    }

    @Override
    public T peekMin() { 
        
        if (elementCount == 0) {
            throw new EmptyContainerException();
        }
        return this.heap[0];        
    }

    @Override
    public void insert(T item) {
        
        if (item == null) {
            throw new IllegalArgumentException();   
        }
        if (heap.length == (elementCount)) {            
            resize();
        }
        heap[elementCount] = item;
        elementCount++;
        if (elementCount > 1) {
            //sortHeap();
            percolateUp(elementCount - 1);
        }
    }

     
     /*private int getChild(int index, int child) {
         
         return ((index*NUM_CHILDREN) + child);
     }*/
     
     private int getParent(int index) {
         return (index - 1)/NUM_CHILDREN;
     }
    
    private void resize() {
    
        T[] newHeap = makeArrayOfT(2*elementCount);
        for (int i = 0; i < elementCount; i++) {
            newHeap[i] = heap[i];
        }
        heap = newHeap; 
    }
    
    /*private void sortHeap() {
        
        for (int i = 0; i <= ((elementCount -1)/4); i++) {
            for (int j = 1; j <= NUM_CHILDREN; j++) {
                if (i*NUM_CHILDREN + j < elementCount){
                    if (heap[i].compareTo(heap[i*NUM_CHILDREN+ j]) > 0) {
                        swap(i, i*NUM_CHILDREN + j);
                    }
                } 
            }       
        }         
    }*/  
     
    private void percolateUp(int index) {
        
        int parentIndex = getParent(index);
        
        if (index > 0 && heap[parentIndex].compareTo(heap[index]) > 0) {
            swap(parentIndex, index);
            percolateUp(parentIndex);
        } 
    }

    @Override
    public int size() {
        return elementCount;
    }
}
