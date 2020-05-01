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

3. Farmer-Fox-Chicken-Grain problem/Game
Implemented the famous game of Famer fox chicken grain where each of them have to cross a river but on special conditions. 
Farmer can carry one thing at a time, Fox-Chicken cannot be left alone, Chicken-Grain cannot be left alone at all times.

4. Baroque chess
Implemented a baroque chess game. It has completly different rules and moves from a normal chess game, the rules for baroque chess are way more hard and complex than a normal chess game rules. This was implemented by a 2 person team, I and my partner. I worked on zobrist hashing and various Artificial intelligence algorithms like IDDFS. The main file with the implementation is :
--> Baroque chess vasu master

5. Search Engine
I implemented and mimicked google's search engine while implementing two algorithms, TF-IDF , Page Rank 
I used common data structures from the ones I implemented in Maze-Solvers, these are, ArrayDictionary, ChainedHashDictionary, ChainedHashSet, DoubleLinkedList, ArrayHeap.
I implemented a method called topKSort, this method is responsible for returning the top k elements from a list containing n comparable elements. Then, I implemented SearchEngine.search.analyzers.TfIdfAnalyzer.java which is the TF-IDF algorithm.
Then, I implemented Page Rank algorithm in SearchEngine.search.analyzers.PageRankAnalyzer.java



