// @author joas47

import java.util.*;

public class MyUndirectedGraph<T> implements UndirectedGraph<T> {

    private Map<T, Set<Edge<T>>> adjList = new HashMap<>();

    private int numberOfEdges;

    /**
     * Antalet noder i grafen.
     *
     * @return antalet noder i grafen.
     */
    @Override
    public int getNumberOfNodes() {
        return adjList.size();
    }

    /**
     * Antalet bågar i grafen.
     *
     * @return antalet bågar i grafen.
     */
    @Override
    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    /**
     * Lägger till en ny nod i grafen.
     *
     * @param newNode datat för den nya noden som ska läggas till i grafen.
     * @return false om noden redan finns.
     */
    @Override
    public boolean add(T newNode) {
        if (!adjList.containsKey(newNode)) {
            adjList.put(newNode, new HashSet<>());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Kopplar samman två noder i grafen. Eftersom grafen är oriktad så spelar
     * det ingen roll vilken av noderna som står först. Det är också
     * fullständigt okej att koppla ihop en nod med sig själv. Däremot tillåts
     * inte multigrafer. Om två noder kopplas ihop som redan är ihopkopplade
     * uppdateras bara deras kostnadsfunktion.
     *
     * @param from den ena noden.
     * @param to   den andra noden.
     * @param cost kostnaden för att ta sig mellan noderna. Denna måste vara >0
     *             för att noderna ska kunna kopplas ihop.
     * @return true om bägge noderna finns i grafen och kan kopplas ihop.
     */
    @Override
    public boolean connect(T from, T to, int cost) {
        if (cost > 0) {
            if (this.adjList.containsKey(from) && this.adjList.containsKey(to)) {
                if (!isConnected(from, to)) {
                    return connectHelper(from, to, cost);
                } else {
                    return updateCost(from, to, cost);
                }
            } else {
                // TODO: Throw new NSEE?
                return false;
            }
        } else {
            // TODO: throw new IAE?
            return false;
        }
    }

    private boolean connectHelper(T from, T to, int cost) {
        Set<Edge<T>> fromsEdges = adjList.get(from);
        fromsEdges.add(new Edge<>(to, cost));
        if (!from.equals(to)) {
            Set<Edge<T>> tosEdges = adjList.get(to);
            tosEdges.add(new Edge<>(from, cost));
        }
        numberOfEdges++;
        return true;
    }

    private boolean updateCost(T from, T to, int cost) {
        Edge<T> edgeFromTo = getEdgeBetween(from, to);
        edgeFromTo.setCost(cost);
        if (!from.equals(to)) {
            Edge<T> edgeToFrom = getEdgeBetween(to, from);
            edgeToFrom.setCost(cost);
        }
        return true;
    }

    /**
     * Berättar om två noder är sammanbundna av en båge eller inte.
     *
     * @param from den ena noden.
     * @param to   den andra noden.
     * @return om noderna är sammanbundna eller inte.
     */
    @Override
    public boolean isConnected(T from, T to) {
        Edge<T> edge = getEdgeBetween(from, to);
        return edge != null;
    }

    private Edge<T> getEdgeBetween(T from, T to) {
        // Return edge between nodes.
        // If either node is missing return NULL
        // If no edge between nodes, RETURN NULL
        Set<Edge<T>> fromsEdges = getEdgesFrom(from);
        // Don't remove. throws necessary exception.
        Set<Edge<T>> tosEdges = getEdgesFrom(to);
        if (fromsEdges != null && tosEdges != null) {
            for (Edge<T> edge : fromsEdges) {
                if (edge.getDestination().equals(to)) {
                    return edge;
                }
            }
        }
        return null;
    }

    private Set<Edge<T>> getEdgesFrom(T t) {
        // Return a COPY of set of all edges from this node.
        // If node is missing, return NULL
        Set<Edge<T>> set = adjList.get(t);
        if (set != null) {
            return Set.copyOf(set);
        } else {
            return null;
        }
    }

    /**
     * Returnerar kostnaden för att ta sig mellan två noder.
     *
     * @param to   den ena noden.
     * @param from den andra noden.
     * @return kostnaden för att ta sig mellan noderna eller -1 om noderna inte
     * är kopplade.
     */
    @Override
    // TODO: kostnad för att ta sig mellan två noder som inte är kopplade direkt?
    //  Just nu bara två direktkopplade noder
    public int getCost(T to, T from) {
        if (isConnected(to, from)) {
            return getEdgeBetween(to, from).getCost();
        } else {
            return -1;
        }
    }

    /**
     * Gär en djupet-först-sökning efter en väg mellan två noder.
     * <p>
     * Observera att denna metod inte använder sig av viktinformationen.
     *
     * @param start startnoden.
     * @param end   slutnoden.
     * @return en lista över alla noder på vägen mellan start- och slutnoden. Om
     * ingen väg finns är listan tom.
     */
    @Override
    public List<T> depthFirstSearch(T start, T end) {
        List<T> list = new LinkedList<>();
        if (adjList.containsKey(start) && adjList.containsKey(end)) {
            list = getAPath(start, end);
            if (list != null) {
                list.add(0, start);
                return list;
            }
            return new LinkedList<>();
        }
        return list;
    }

    private List<T> getAPath(T from, T to) {
        Set<T> visited = new HashSet<>();
        Map<T, T> via = new HashMap<>();
        depthFirstSearcher(from, null, visited, via);
        if (!visited.contains(to)) {
            return null;
        }
        return gatherPath(from, to, via);
    }

    private void depthFirstSearcher(T where, T whereFrom, Set<T> visited, Map<T, T> via) {
        visited.add(where);
        via.put(where, whereFrom);
        for (Edge<T> e : adjList.get(where)) {
            if (!visited.contains(e.getDestination())) {
                depthFirstSearcher(e.getDestination(), where, visited, via);
            }
        }
    }

    private List<T> gatherPath(T from, T to, Map<T, T> via) {
        List<Edge<T>> path = new ArrayList<>();
        T where = to;
        while (!where.equals(from)) {
            T node = via.get(where);
            Edge<T> e = getEdgeBetween(node, where);
            path.add(e);
            where = node;
        }
        Collections.reverse(path);
        List<T> nodePath = new LinkedList<>();
        for (Edge<T> e : path) {
            nodePath.add(e.getDestination());
        }
        return nodePath;
    }

    /**
     * Gär en bredden-först-sökning efter en väg mellan två noder.
     * <p>
     * Observera att denna metod inte använder sig av viktinformationen. Ni ska
     * alltså inte implementera Dijkstra eller A*.
     *
     * @param start startnoden.
     * @param end   slutnoden.
     * @return en lista över alla noder på vägen mellan start- och slutnoden. Om
     * ingen väg finns är listan tom.
     */
    @Override
    public List<T> breadthFirstSearch(T start, T end) {
        List<T> list = new LinkedList<>();
        if (adjList.containsKey(start) && adjList.containsKey(end)) {
            list = breadthFirstSearcher(start, end);
            if (list != null) {
                list.add(0, start);
                return list;
            }
            return new LinkedList<>();
        }
        return list;
    }

    private List<T> breadthFirstSearcher(T from, T to) {
        LinkedList<T> queue = new LinkedList<>();
        Set<T> visited = new HashSet<>();
        Map<T, T> via = new HashMap<>();
        visited.add(from);
        queue.addLast(from);
        while (!queue.isEmpty()) {
            T node = queue.pollFirst();
            for (Edge<T> e : adjList.get(node)) {
                T dest = e.getDestination();
                if (!visited.contains(dest)) {
                    visited.add(dest);
                    queue.add(dest);
                    via.put(dest, node);
                }
            }
        }
        if (!visited.contains(to)) {
            return null;
        }
        return gatherPath(from, to, via);
    }

    /**
     * Returnerar en ny graf som utgör ett minimalt spännande träd till grafen.
     * Ni kan förutsätta att alla noder ingår i samma graf.
     *
     * @return en graf som representerar ett minimalt spännande träd.
     */
    @Override
    public UndirectedGraph<T> minimumSpanningTree() {
        UndirectedGraph<T> mst = new MyUndirectedGraph<>();
        // Workaround for getting an element out of a Set.
        ArrayList<T> workaround = new ArrayList<>();
        Set<T> nodes = adjList.keySet();
        for (T node : nodes) {
            mst.add(node);
            workaround.add(node);
        }
        int numberOfNodes = mst.getNumberOfNodes();
        int edgesInMST = numberOfNodes - 1;
        PriorityQueue<Edge<T>> priorityQueue = new PriorityQueue<>(Edge::compareTo);
        Set<T> visited = new HashSet<>();
        primsAlgo(workaround.get(0), mst, priorityQueue, visited, edgesInMST);
        return mst;
    }

    private void primsAlgo(T node, UndirectedGraph<T> mst, PriorityQueue<Edge<T>> priorityQueue, Set<T> visited, int edgesInMST) {
        Set<Edge<T>> edges = getEdgesFrom(node);
        for (Edge<T> edge : edges) {
            if (!edge.getDestination().equals(node) && !visited.contains(edge.getDestination())) {
                priorityQueue.add(edge);
            }
        }
        visited.add(node);
        while (!priorityQueue.isEmpty() && mst.getNumberOfEdges() <= edgesInMST) {
            Edge<T> minEdge = priorityQueue.poll();
            if (!visited.contains(minEdge.getDestination())) {
                mst.connect(node, minEdge.getDestination(), minEdge.getCost());
                primsAlgo(minEdge.getDestination(), mst, priorityQueue, visited, edgesInMST);
            }
        }
    }
}
