import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Node class to be used with ShortestPathCalculator. Has column and row of Node.
 */
public class Node {
    // Column and row of the Node on the map
    int column;
    int row;

    private LinkedList<Node> shortestPath = new LinkedList<>(); // The shortest path to the node

    // Nodes adjectented to the node
    Map<Node, Integer> adjecentNodes = new HashMap<Node, Integer>();

    // Set distance of node to the starting location as maximal integer value to represent infinity.
    int distanceToStart = Integer.MAX_VALUE;

    /**
     * Initiates node. Takes in the row and column on the chunk of the node.
     * 
     * @param row The row of the Node on the chunk
     * @param column The column of the Node on the chunk
     */
    public Node(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Adds a node to the Map with adjecent nodes.
     * 
     * @param node The adjecent node
     * @param distanceNode The distance between the adjecent node and the node/enemy/player
     */
    public void addAdjecentNode(Node node, int distanceNode) {
        adjecentNodes.put(node, distanceNode);
    }

    /**
     * Creates a copy of the shortest path to the node and returns it.
     * 
     * @return LinkedList containing the shortest path the the node
     */
    public LinkedList<Node> getPath() {
        LinkedList<Node> pathCopy = new LinkedList<>();

        for (Node node : shortestPath) {
            pathCopy.add(node);
        }

        return pathCopy;
    }

    public void setPath(LinkedList<Node> path) {
        this.shortestPath = path;
    }

    public int getDistance() {
        return distanceToStart;
    }

    public void setDistance(int distance) {
        this.distanceToStart = distance;
    }
}