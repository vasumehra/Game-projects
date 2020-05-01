package mazes.generators.maze;


import datastructures.concrete.ChainedHashSet;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import misc.graphs.Graph;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @SuppressWarnings("rawtypes")

    public ISet<Wall> returnWallsToRemove(Maze maze) {
    	
    	ISet<Wall> weightedWalls = new ChainedHashSet<Wall>(); 	

    	for (Wall w : maze.getWalls()) {
    		w.setDistance(Math.random() + 10);
    		weightedWalls.add(w);
    	}         	
		Graph<Room, Wall> g = new Graph<>(maze.getRooms(), weightedWalls);    	
		ISet<Wall> removeWalls = g.findMinimumSpanningTree();		
    	return removeWalls;
    }
}
