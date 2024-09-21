package spreadsheet.graph.api;

import java.util.List;

public interface DirGraph<T> {
    void addNode(T node);
    void addEdge(T from, T to);
    List<T> topologicalSort();
    }
