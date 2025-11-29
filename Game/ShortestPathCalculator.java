import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Calculates ShortestPath for an enemy to the player using Dijkstra's algorithm.
 */
class ShortestPathCalculator {
    // Current chunk the player is in
    TileType[][] chunk;

    // Tiles the algorithm has or hasn't visited
    ArrayList<Node> visited = new ArrayList<>();

    // Minimal height and width of chunk coordinates
    int minHeight;
    int minWidth;

    // Maximum height and width of chunk coordinates
    int maxHeight;
    int maxWidth;

    // Enemy starting tile in chunk and starting coordinates
    Point startingPoint;
    Point enemyStartingCoordinates;

    // The player instance
    Player player;

    /**
     * Initiates ShortestPathCalculator.
     * 
     * @param chunk The chunk the player is in
     */
    public ShortestPathCalculator(TileType[][] chunk, Player player) {
        this.chunk = chunk;

        // Minimum and maximum height of the chunk
        this.minHeight = 0;
        this.minWidth = 0;
        this.maxHeight = GamePanel.CHUNKHEIGHT;
        this.maxWidth = GamePanel.CHUNKWIDTH;

        this.player = player;
    }

    /**
     * Sets-up the nodes for the algorithm by calculating distance between them.
     * 
     * @return Array with the ready-to-use nodes
     */
    private Node[][] createNodes() {
        // Initialise the nodes array
        Node[][] nodes = new Node[chunk.length][chunk[0].length];

        // Create a new node for each tile
        for (int i = 0; i < chunk.length; i++) {
            for (int j = 0; j < chunk[i].length; j++) {
                // Create node instance with row and column
                nodes[i][j] = new Node(i, j);
            }
        }

        // Loop through every tile in the chunk
        for (int i = 0; i < chunk.length; i++) {
            for (int j = 0; j < chunk[i].length; j++) {
                // Wall tiles are unwalkable, so stop calculating when we arrive at one
                if (chunk[i][j].isWall) {
                    continue;
                }

                /* Check if tile above exists (isn't out of bounds) 
                / and that it isn't a wall */
                if (i - 1 >= minWidth && !(chunk[i - 1][j].isWall)) {
                    // Calculate distance of tile (or enemy) to upper neighbour
                    int distance = calculateNodeDistance(i, j, i - 1, j);

                    // Add neighbouring chunk to node
                    nodes[i][j].addAdjecentNode(nodes[i - 1][j], distance);
                }

                /* Check if tile beneath exists. Last index of chunk tile is maxWidth - 1
                 * The tile should also not be a wall */
                if (i + 1 < maxWidth && !(chunk[i + 1][j].isWall)) {
                    // Calculate distance of tile (or enemy) to lower neighbour
                    int distance = calculateNodeDistance(i, j, i + 1, j);

                    // Add neighbouring tile to the node
                    nodes[i][j].addAdjecentNode(nodes[i + 1][j], distance);
                }

                // Check if tile to the left exists and that it isn't a wall
                if (j - 1 >= minHeight && !(chunk[i][j - 1].isWall)) {
                    // Calculate distance of tile (or enemy) to left neighbour
                    int distance = calculateNodeDistance(i, j, i, j - 1);

                    // Add neighbouring tile to the node
                    nodes[i][j].addAdjecentNode(nodes[i][j - 1], distance);
                }

                // Check if tile to the right exists and that isn't a wall
                if (j + 1 < maxHeight && !(chunk[i][j + 1].isWall)) {
                    // Calculate distance of tile (or enemy) to right neighbour
                    int distance = calculateNodeDistance(i, j, i, j + 1);

                    // Add neighbouring tile to the node
                    nodes[i][j].addAdjecentNode(nodes[i][j + 1], distance);
                }
            }
        }

        return nodes;
    }

    /**
     * Calculates distance between tile and tile.
     * or between tile and enemy/player if enemy/player is in tile neighbouring the tile.
     * 
     * @param row row of tile
     * @param column column of tile
     * @param newRow row of new (the neighbouring) tile
     * @param newColumn column of new (the neighbouring) tile
     * @return The calculated distance as described
     */
    private int calculateNodeDistance(int row, int column, int newRow, int newColumn) {
        int distance;
        int distanceX;
        int distanceY;

        /* When the tile to calculate the distance between the neighbours of
         *   is the tile the enemy is in,
         * calculate the distance between the enemy and the middle of the neighbouring tile(s) */
        if (column == startingPoint.getX() && row == startingPoint.getY()) {
            // Calculate x and y distance between tile and enemy
            distanceX = (int) Math.abs(
                enemyStartingCoordinates.getX() - GamePanel.TILESIZE * newColumn);
            distanceY = (int) Math.abs(
                enemyStartingCoordinates.getY() - GamePanel.TILESIZE * newRow);

        // Check if the tile is the player tile
        } else if (
            newColumn == player.getCentreTile().getX() && newRow == player.getCentreTile().getY()) {

            // Set distance x and y to the distance between the centre of the old tile to the player
            distanceX = (int) Math.abs(player.getX() - GamePanel.TILESIZE * column);
            distanceY = (int) Math.abs(player.getY() - GamePanel.TILESIZE * row);

        } else {
            // Return the distance between the middle of 2 tiles (in total the Tilesize)
            return GamePanel.TILESIZE;
        }

        // Calculate distance between enemy and neighbouring tile using the distance formula
        distance = Math.round((long) Math.sqrt(distanceX * distanceX + distanceY * distanceY));
        return distance;
    }

    /**
     * Calculates the shortest path from the enemy to the player tile.
     * 
     * @param enemy Instance of enemy
     * @return LinkedList with the path to follow
     */
    public LinkedList<Node> calculateShortestPath(Enemy enemy) {
        // Create comparator for the priority queue, based on distance to starting tile
        Comparator<Node> nodeComparator = Comparator.comparing(Node::getDistance);

        // Initialise priority queue used for calculating shortest path
        PriorityQueue<Node> pq = new PriorityQueue<>(nodeComparator);

        /* As attacking is based on the centre of the entity,
         * it is best to calculate distance from the centre as well */
        startingPoint = enemy.getCentreTile();
        enemyStartingCoordinates = new Point(enemy.getCentreX(), enemy.getCentreY());

        Point playerTile = player.getCentreTile();

        // Create nodes for the calculation
        Node[][] nodes;
        nodes = createNodes();

        // Set distance to node of enemy to 0
        (nodes[(int) startingPoint.getY()][(int) startingPoint.getX()]).setDistance(0);

        // Add starting node to the priority queue
        pq.add(nodes[(int) startingPoint.getY()][(int) startingPoint.getX()]);

        while (!pq.isEmpty()) {
            // Get the node with the shortest distance to the tile
            Node currentNode = pq.poll();

            if (visited.contains(currentNode)) {
                /* The distances for this node have already been determined,
                 * continue to the next queue entry */
                continue;
            }

            // Add this node to the visited array list
            visited.add(currentNode);

            // Loop over each neighbour of node
            for (Map.Entry<Node, Integer> neighbourInfo : currentNode.adjecentNodes.entrySet()) {
                // Calculate new distance between tile and starting tile
                int newDistance = neighbourInfo.getValue() + currentNode.getDistance();

                /* If the distance between the new tile is shorter than 
                 * the known distance to the tile */
                if (newDistance < (neighbourInfo.getKey().getDistance())) {
                    // Set the distance of the tile to the newly calculated distance
                    neighbourInfo.getKey().setDistance(newDistance);;

                    // Set the new path to get to the node
                    LinkedList<Node> newPath = currentNode.getPath();
                    newPath.add(currentNode);

                    // Set shortest path to neighbour tile
                    neighbourInfo.getKey().setPath(newPath);
                }

                // Check if the neighbour tile is the tile that the player is on
                if (neighbourInfo.getKey().column == playerTile.getX() 
                    && neighbourInfo.getKey().row == playerTile.getY()) {
                    /* The shortest path to the player has been found,
                     * there's no need to continue calculating anymore */
                    break;
                }

                // Add neighbour tile to priority queue
                pq.add(neighbourInfo.getKey());
            }
        }

        // Get the path to the node the player is on (Y = row, X = column, so [y][x])
        LinkedList<Node> neededPath = (nodes[(int) player.getCentreTile().getY()]
            [(int) player.getCentreTile().getX()]).getPath();

        // Add player node to path
        neededPath.add(nodes[(int) player.getCentreTile().getY()]
            [(int) player.getCentreTile().getX()]);

        // Remove first tile as the enemy is already on it
        neededPath.removeFirst();
        
        return neededPath;
    }
}