package spreadsheet.graph.impl;

import spreadsheet.graph.api.DirGraph;
import java.util.*;

// Implementation of a directed graph using adjacency list representation
// Supports topological sorting and cycle detection
// Type parameter T represents the type of nodes in the graph
public class DirGraphImpl<T> implements DirGraph<T> {
    private final Map<T, List<T>> adjacencyList = new HashMap<>();
    private final Map<T, Integer> inDegree = new HashMap<>();


    // Adds a new node to the graph if it doesn't exist
    // Initializes empty adjacency list and sets inDegree to 0
    @Override
    public void addNode(T node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        inDegree.putIfAbsent(node, 0);
    }

    // Adds a directed edge from 'from' node to 'to' node
    // Creates nodes if they don't exist
    // Updates inDegree count for the destination node
    @Override
    public void addEdge(T from, T to) {
        addNode(from);
        addNode(to);
        adjacencyList.get(from).add(to);
        inDegree.put(to, inDegree.get(to) + 1);
    }

    // Performs topological sort using Kahn's algorithm
    // Returns sorted list of nodes where for each edge u->v, u comes before v
    // Throws IllegalStateException if graph contains a cycle
    @Override
    public List<T> topologicalSort() {
        // List to store the topologically sorted nodes
        List<T> sortedList = new ArrayList<>();

        // Queue for processing nodes with no incoming edges
        Queue<T> queue = new LinkedList<>();

        // Create local copy of inDegree to track remaining edges
        Map<T, Integer> localInDegree = new HashMap<>(inDegree);

        // Add all nodes with no incoming edges to the queue
        for (Map.Entry<T, Integer> entry : localInDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Process nodes until queue is empty
        while (!queue.isEmpty()) {
            T node = queue.poll();
            sortedList.add(node);

            // Reduce inDegree of all neighbors
            // Add neighbors to queue when their inDegree becomes 0
            for (T neighbor : adjacencyList.get(node)) {
                localInDegree.put(neighbor, localInDegree.get(neighbor) - 1);
                if (localInDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }
        // If not all nodes are processed, graph has a cycle
        if (sortedList.size() != adjacencyList.size()) {
            throw new IllegalStateException("Cycle Detected!");
        }
        return sortedList;
    }
}