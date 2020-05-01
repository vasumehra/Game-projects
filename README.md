# Game-projects
Following repository contains some games I programmed

1. Maze-Solver

Folder with name "java" is a Maze-Solver game that randomly generates a maze and finds the shortest distance from starting point to end point. I have implemented Kruskal's algorithm and Dijkstra's algorithm to help me both generate and solve mazes.
I have implemented following Data Structures for Maze-Solver :
1. ChainedHashDictionary - (Path)--> java.datastructures.concrete.dictionaries.ChainedHashDictionary.java
2. ArrayDictionary - (Path)--> java.datastructures.concrete.dictionaries.ArrayDictionary.java
3. ArrayHeap - (Path)--> java.datastructures.concrete.ArrayHeap.java
4. ChainedHashSet - (Path)--> java.datastructures.concrete.ChainedHashSet.java
5. DoubleLinkedList - (Path)--> java.datastructures.concrete.DoubleLinkedList.java
6. ArrayDisjointSet - (Path)--> java.datastructures.concrete.ArrayDisjointSet.java
Imlementation of Graph structures:
(Path)--> java.misc.graphs.Graph.java
Here, I implemented shortest path between two vertices using Dijkstra's algorithm. 
Implementation of Kruskal's and Prims algorithm:
(Path)--> java.mazes.generators.maze.KruskalMazeCarver.java
Here, I implemented prims and kruskal's algorithm to generate random mazes with maximum complexity and reduced redundancy.
Other Miscellenious java files implemented: 
--> java.misc.Searcher.java
--> java.datastructures.interfaces.IDisjointSet.java
--> java.misc.graphs.Edge.java
--> java.mazes.entities.Wall.java
--> java.mazes.entities.Room.java
--> java.mazes.entities.Maze.java
--> java.mazes.gui.MainWindow.java

2. ChatBot
Implemented a small chatbot. After running it against another chatbot, it creates an interesting chat with another bot. One dialogue per bot and repeats it alterntively and so on, this creates an interetsing/funny converstaion between bots.



