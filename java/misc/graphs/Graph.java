package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;
/**
 * 
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    private int graphSize;
    private int edgeSize;
    private IDictionary<V, IList<E>> edgesVertex;
    private IList<E> edge;
    
    public Graph(IList<V> vertices, IList<E> edges) {
        edgesVertex = new ChainedHashDictionary<V, IList<E>>();
        edge = edges;
        for (E edg : edges) {
            if (edg.getWeight() < 0.0) {
                throw new IllegalArgumentException();
            }
            if (!vertices.contains(edg.getVertex1()) || !vertices.contains(edg.getVertex2())) {
                throw new IllegalArgumentException();
            }
        }
        for (V vertex : vertices) {
            IList<E> edgesWithVer = new DoubleLinkedList<E>();
            for (E edg : edges) {
                if (edg.getVertex1().equals(vertex) || edg.getVertex2().equals(vertex)) {
                    edgesWithVer.add(edg);
                }
            }
            edgesVertex.put(vertex, edgesWithVer);
        }
        graphSize = vertices.size();
        edgeSize = edges.size();
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.graphSize;
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return edgeSize;
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        IDisjointSet<V> vertexSet = new ArrayDisjointSet<V>();
        ISet<E> mst = new ChainedHashSet<E>();
        
        //here
        IList<E> edgeSort = Searcher.topKSort(edgeSize, this.edge);
        //
        for (KVPair<V, IList<E>> graphComps : this.edgesVertex) {
            vertexSet.makeSet(graphComps.getKey());
            
        }
        for (E e : edgeSort) {
            if (vertexSet.findSet(e.getVertex1()) != vertexSet.findSet(e.getVertex2())) {
                vertexSet.union(e.getVertex1(), e.getVertex2());
                mst.add(e);
            }
            
        }
        return mst;
    }
    
    public class Vertice implements Comparable<Vertice> {

    	private V room;
    	private double dist;
    	private E edge;
    	
    	public Vertice(V room, E edge, double dist) {
    		this.room = room;
    		this.edge = edge;
    		this.dist = dist;
    	}
    	
		@Override
		public int compareTo(Vertice other) {			
			return Double.compare(this.dist, other.dist);
		}
    	
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
    	
    	IList<E> shortestPath = new DoubleLinkedList<E>();
    	IDictionary<V, Double> dictDistance = new ChainedHashDictionary<V, Double>();
    	IDictionary<V, E> previous = new ChainedHashDictionary<V, E>();
    	ISet<V> setProcessed = new ChainedHashSet<V>();
    	IPriorityQueue<Vertice> heap = new ArrayHeap<Vertice>();  
    	    	
    	if (start == end) {
    		return shortestPath;
    	}
    	
    	if (!edgesVertex.containsKey(start) && edgesVertex.get(start) == null) {
    		throw new NoPathExistsException();
    	}
    	
        for (KVPair<V, IList<E>> graphComps : this.edgesVertex) {            
            dictDistance.put(graphComps.getKey(), Double.POSITIVE_INFINITY);
         }
    	
        dictDistance.put(start, 0.0);      
        
    	for (E edges : edgesVertex.get(start)) {
    		Vertice temp = new Vertice(edges.getOtherVertex(start), edges, edges.getWeight());
    		heap.insert(temp);
    	}
    	
    	Vertice currVert;
    	V vertex;
    	
    	vertex = start;
    	
    	double distance = 0.0;
    	
    while (heap.size() > 0) {
    		
    		currVert = heap.removeMin();
    		vertex = currVert.room;

    		if (setProcessed.contains(vertex)) {
    			continue;
    		}
    		
    		if (!setProcessed.contains(vertex)) {
    			setProcessed.add(vertex);
    			dictDistance.put(vertex, (currVert.dist));
    			previous.put(vertex, currVert.edge);
    		}    		
    		else
    		{
    			distance = currVert.dist;
    			if (distance < dictDistance.get(vertex)) {
					dictDistance.put(vertex, distance);
					previous.put(vertex, currVert.edge);
				}  
    		}
    			
            	for (E edges : edgesVertex.get(vertex)) {
            		Vertice temp = new Vertice(edges.getOtherVertex(vertex), 
            				edges, edges.getWeight() + dictDistance.get(vertex));
            		heap.insert(temp);
            	}        	
    }
    
	V found = end;
	E e;
	
	while (found != start) {

		if (previous.containsKey(found)) {
		e = previous.get(found);
		} else {
			throw new NoPathExistsException();
		}
		found = e.getOtherVertex(found);
		shortestPath.insert(0, e);
		}
    return shortestPath;
}
    
}
