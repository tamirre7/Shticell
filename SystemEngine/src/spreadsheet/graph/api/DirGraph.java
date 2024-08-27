package spreadsheet.graph.api;

import java.util.List;
import java.util.Set;

public interface DirGraph<T> {
    void addNode(T node);
    void addEdge(T from, T to);
    List<T> topologicalSort();
    }
