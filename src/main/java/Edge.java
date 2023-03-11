// @author joas47

public class Edge<T> implements Comparable<Edge<T>> {

    private T destination;
    private int cost;

    public Edge(T destination, int cost) {
        this.destination = destination;
        this.cost = cost;
    }

    public T getDestination() {
        return destination;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Error: Cost can't be negative!");
        }
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "to " + destination + " by " + "'edge'" + " costs " + cost;
    }

    @Override
    public int compareTo(Edge o) {
        return Integer.compare(this.cost, o.cost);
    }
}
