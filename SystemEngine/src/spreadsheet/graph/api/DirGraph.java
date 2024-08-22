package spreadsheet.graph.api;

public interface DirGraph<T> {
    void addNode(T node);
    void addEdge(T from, T to);
}
