package spreadsheet.graph.impl;


import spreadsheet.graph.api.DirGraph;

import java.util.*;

public class DirGraphImpl<T> implements DirGraph<T> {
    private final Map<T, List<T>> adjacencyList = new HashMap<>();
    private final Map<T, Integer> inDegree = new HashMap<>();

    @Override
    public void addNode(T node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        inDegree.putIfAbsent(node, 0);
    }

    @Override
    public void addEdge(T from, T to) {
        addNode(from);
        addNode(to);
        adjacencyList.get(from).add(to);
        inDegree.put(to, inDegree.get(to) + 1);
    }

    public List<T> topologicalSort() {
        List<T> sortedList = new ArrayList<>();
        Queue<T> queue = new LinkedList<>();
        Map<T, Integer> localInDegree = new HashMap<>(inDegree);


        for (Map.Entry<T, Integer> entry : localInDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            T node = queue.poll();
            sortedList.add(node);

            for (T neighbor : adjacencyList.get(node)) {
                localInDegree.put(neighbor, localInDegree.get(neighbor) - 1);
                if (localInDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }


        if (sortedList.size() != adjacencyList.size()) {
            throw new IllegalStateException("Cycle Detected!");
        }

        return sortedList;
    }
}
