package datastructures.concrete;

import misc.exceptions.EmptyContainerException;
import datastructures.interfaces.IList;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        Node<T> node = new Node<T>(null, item, null);
        if (node != null) {
          if (this.size == 0) {
              this.front = node;
          } else {
            this.back.next = node;
            node.prev = this.back;
          }
          this.back = node;
          this.size++;
        }
    }

    @Override
    public T remove() {
        if (this.size == 0) {
            throw new EmptyContainerException();
        }
        Node<T> lastNode = this.back;
        T value = lastNode.data;
        if (this.front.next == null) {
        	this.front = null; 
        	this.back = null; 
        } else { 
        	this.back = back.prev;
        	this.back.next = null;
        }
        this.size--;
        return value;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        if (this.front == null) {
            return null;
        }
        return halfSize(index).data;
    }

    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        if (this.front != null) {
	         if (index == 0) {
	            Node<T> node = new Node<T>(null, item, null);
	            if (this.front.next == null) {
	            	this.front.next = node; 
	            	node.prev = this.front; 
	            	this.back = node; 
	            	this.front = node; 
	            } else { 
		            this.front.next.prev = node;
		            node.next = front.next; 
		            this.front = node;
	            }
	        } else if (index == this.size -1) {
	            Node<T> node = new Node<T>(null, item, null);
	            this.back.prev.next = node;
	            node.prev = this.back.prev; 
	            this.back = node;
	        } else {
	            Node<T> curr = halfSize(index);
	            Node<T> node = new Node<T>(curr.prev, item, curr.next);
	            curr.prev.next = node;
	            curr.next.prev = node;
	        }
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= this.size + 1) {
            throw new IndexOutOfBoundsException();
        }
        if (this.front == null) {
            this.front = new Node<T>(null, item, null);
            this.back = this.front;
        } else if (index == 0) {
            Node<T> node = new Node<T>(null, item, this.front);
            this.front.prev = node;
            this.front = node;
        } else if (index == this.size) {
            Node<T> node = new Node<T>(this.back, item, null);
            this.back.next = node;
            this.back = back.next;
        } else {
        	Node<T> curr = halfSize(index);
            Node<T> node = new Node<T>(curr.prev, item, curr);
            curr.prev = node;
            curr.prev.prev.next = curr.prev;
        }
        this.size++;
    }
    
    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
        T result = null;
        if (this.front != null) {
        	if (index == 0) {
                result = this.front.data;
                 if (this.front.next == null) {
     	        	 this.front = null;
     	             this.back = null;
                 } else { 
                 	this.front = this.front.next; 
                 	this.front.prev = null; 
                 }
             } else if (index == this.size - 1) {
                 result = this.back.data;
                 this.back = this.back.prev;
                 this.back.next = null;
             } else {
                 Node<T> curr = halfSize(index);
                 result = curr.data;
                 curr.prev.next = curr.next;
                 curr.next.prev = curr.prev;
             }
             this.size--;
        }
        return result;
    }
    
    private Node<T> getFront(int index) { 
    	Node<T> curr = this.front;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr; 
    }
    
    private Node<T> getBack(int index) {
    	Node<T> curr = this.back;
    	for (int i = this.size - 1; i > index; i--) {
            curr = curr.prev;
        }
    	return curr;
    }
     
    private Node<T> halfSize(int index) {
    	int halfSize = this.size / 2; 
    	Node<T> curr = null; 
    	if (halfSize < index) {
            curr = getBack(index);
        } else {
        	curr = getFront(index);
        }
    	return curr; 
    }

    @Override
    public int indexOf(T item) {
    	return containsHelper(item);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
    	return containsHelper(other) >= 0;
    }
    
    private int containsHelper(T other) {
    	 if (this.front == null) {
             return -1;
         }
         Node<T> forward = this.front;
         int counter = 0;
         Node<T> backward = this.back;
         if (forward.next == null) {
         	if (forward.data == other || forward.data.equals(other)) {
         		return 0;
         	}
         }
         while (backward != null && (backward.next != forward || 
        		 backward.next != null && backward.next.next != forward)) {
         	if (forward.data == other || forward.data.equals(other)) {
         		return counter;
         	} else if (backward.data == other || backward.data.equals(other)) {
         		return this.size - counter - 1; 
         	} else {
         		forward = forward.next;
         		backward = backward.prev;
         		counter++;
         	}
         }
         return -1;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return this.current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                T temp = this.current.data;
                this.current = this.current.next;
                return temp;
            }
        }
    }
}
