import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

/**
 * The JPanel for the game. Handles game loop as well.
 */
class GamePanel extends JPanel implements Runnable {
    protected static final int TILESIZE = 50; // Tile size in pixels
    protected static final int CHUNKHEIGHT = 8; // The height in tiles of one chunk
    protected static final int CHUNKWIDTH = 8; // The height in tiles of one chunk

    private KeyHandler keyHandler;
    private Player player;
    private Thread gameThread;
    private TileType[][] chunk;

    private Point chunkCoordinates; // The current chunk the player is in, in x and y
    private String world; // The name of the 'world' the player is in

    // Initiate the chunk loader
    private ChunkLoader chunkLoader = new ChunkLoader();

    private ShortestPathCalculator pathCalculator;

    private boolean gameOver = false;

    // Map for cached tile images
    HashMap<TileType, BufferedImage> tileImages = new HashMap<TileType, BufferedImage>();

    // Enemies in chunk
    ArrayList<Enemy> enemies;

    /**
     * Initiates GamePanel and adds a Player.
     */
    public GamePanel() {
        // Create keyHandler used to receive input from user
        this.keyHandler = new KeyHandler();

        // Initiate player with created KeyHandler and starting position
        this.player = new Player(188, 188, keyHandler);

        // Add KeyListener event to panel
        this.addKeyListener(keyHandler);

        // Set panel to focusable in order to receive input
        this.setFocusable(true);

        // Set player chunk location
        this.world = "OW";
        this.chunkCoordinates = new Point(0, 0);

        // Load starting chunk
        initialiseChunk();
    }

    /**
     * Creates and starts the game loop.
     */
    public void startThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Loads in the chunk a player has moved and sets chunk variable accordingly.
     * If the chunk failed to load, initialiseChunk reverts to the previous chunk and returns false.
     */
    private boolean initialiseChunk() {
        // Get chunk name using world and chunk location of player
        String chunkName = world + "_"
            + (int) chunkCoordinates.getX() + "_" + (int) chunkCoordinates.getY();

        // Load the chunk using the chunk's name
        TileType[][] newChunk = chunkLoader.loadChunk(chunkName);

        // If the chunk failed to load, return false
        if (newChunk == null) {
            return false;
        }

        // Set the chunk instance variable to the new chunk
        this.chunk = newChunk;

        // Loop through every tile in the chunk
        for (int i = 0; i < chunk.length; i++) {
            for (TileType type : chunk[i]) {
                /* Check for each tiletype if the image has been cached already,
                 * if not, load the image and put it in the designated map */
                if (!tileImages.containsKey(type)) {
                    Tile tile = new Tile(type);
                    tileImages.put(type, tile.getTileImage());
                }
            }
        }

        enemies = chunkLoader.loadEnemies(chunkName);

        pathCalculator = new ShortestPathCalculator(newChunk, player);

        return true;
    }

    /**
     * Draws the game.
     */
    public void paint(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        if (gameOver) {
            g2D.setFont(new Font("Arial", Font.BOLD, 40));
            g2D.drawString("Game Over", 100, 200);
            return;
        }
        
        /* Draws tiles and entities in the order of 
         * walkable tiles -> player -> enemies -> wall tiles */
        drawWalkableTiles(g2D);

        drawPlayer(g2D);
        drawEnemies(g2D);

        drawWallTiles(g2D);

        // Shows player hp in the top left corner
        g2D.drawString("Player HP: " + player.getHP(), 10, 10);
    }

    /**
     * Draws the tiles the player can walk on.
     * 
     * @param g2D The graphics2D component to draw with 
     */
    private void drawWalkableTiles(Graphics2D g2D) {
        Tile tile;

        for (int i = 0; i < chunk.length; i++) {
            for (int j = 0; j < chunk[i].length; j++) {
                tile = new Tile(chunk[i][j]);

                if (!tile.type.isWall) {
                    // Draw the image of the tile on the coordinates
                    drawTile(g2D, tileImages.get(tile.type), j * TILESIZE, i * TILESIZE);
                }
            }
        }
    }

    /**
     * Draws all the (living) enemies in the chunk.
     * 
     * @param g2D Graphics2D component to draw with
     */
    private void drawEnemies(Graphics2D g2D) {
        for (Enemy enemy : enemies) {
            g2D.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), null);
        }
    }

    /**
     * Draws the player.
     * 
     * @param g2D The graphics2D component to draw with 
     */
    private void drawPlayer(Graphics2D g2D) {
        g2D.drawImage(player.getImage(), player.getX(), player.getY(), null);
    }

    /**
     * Draws the wall tiles.
     * 
     * @param g2D The graphics2D component to draw with 
     */
    private void drawWallTiles(Graphics2D g2D) {
        Tile tile;

        for (int i = 0; i < chunk.length; i++) {
            for (int j = 0; j < chunk[i].length; j++) {
                tile = new Tile(chunk[i][j]);

                if (tile.type.isWall) {
                    // Draw the image of the tile on the coordinates
                    drawTile(g2D, tileImages.get(tile.type), j * TILESIZE, i * TILESIZE);
                }
            }
        }
    }

    /**
     * Draws a specific tile at a given location.
     * 
     * @param g2D The graphics2D component to draw with
     * @param image The image of the tile to draw
     * @param x The x coordinate of the tile
     * @param y The y coordinate of the tile
     */
    private void drawTile(Graphics2D g2D, BufferedImage image, int x, int y) {
        g2D.drawImage(image, x, y, null);
    }

    /**
     * Checks if player is near a chunk border and switches chunk accordingly.
     */
    private boolean switchChunk(int playerX, int playerY) {
        int newChunkX = (int) chunkCoordinates.getX();
        int newChunkY = (int) chunkCoordinates.getY();

        // Check if player is near the left border of the chunk
        if (playerX < 5) {
            // Set new chunk X coordinate
            newChunkX--;

            // Set the new player location. Player enters from the right.
            playerX = CHUNKWIDTH * TILESIZE - player.getWidth() - 10;

        // Check if player is near the right border of the chunk
        } else if (playerX > TILESIZE * CHUNKWIDTH - player.getWidth() - 5) {
            // Set new chunk X coordinate
            newChunkX++;

            // Set the new player location. Player enters from the left.
            playerX = 10;

        // Check if player is near the top border of the chunk
        } else if (playerY < 5) {
            // Set new chunk Y coordinate
            newChunkY++;

            // Set the new player location. Player enters from the bottom.
            playerY = CHUNKHEIGHT * TILESIZE - player.getHeight() - 10;

        // Check if player is near the bottom border of the chunk
        } else if (playerY > TILESIZE * CHUNKHEIGHT - player.getHeight() - 5) {
            // Set new chunk Y coordinate
            newChunkY--;
            
            // Set the new player location. The player enters from the top.
            playerY = 10;

        // If the player wasn't near a chunk border, return false
        } else {
            return false;
        }

        enterChunk(world, newChunkX, newChunkY, playerX, playerY);

        return true;
    }

    /**
     * Enters a new chunk. Has a fail-safe measure in case chunk fails to load.
     * 
     * @param world The world the new chunk is in
     * @param newChunkX The X coordinate of the new chunk
     * @param newChunkY The Y coordinate of the new chunk
     * @param newPlayerX The X coordinate the player is placed at after entering new chunk
     * @param newPlayerY The Y coordinate the player is placed at after entering new chunk
     */
    private void enterChunk(
        String world, int newChunkX, int newChunkY, int newPlayerX, int newPlayerY) {

        // Create a copy of the old chunk coordinates and player coords, in case the new one fails to load 
        Point oldChunkCoordinates = new Point(chunkCoordinates.getLocation());
        int oldPlayerX = player.getX();
        int oldPlayerY = player.getY();

        chunkCoordinates.setLocation(newChunkX, newChunkY);

        // Try to load in the new chunk.
        if (initialiseChunk()) {
            /* Set player coordinates accordingly when the chunk was succesfully loaded.
             * If the chunk wasn't loaded in, we don't change the player's location. */
            player.setCoordinates(newPlayerX, newPlayerY);

            // If the new player position collides (possible when entering from the edge),
            // attempt to nudge the player to a nearby valid position before reverting.
            CollisionCheck cc = new CollisionCheck();
            if (!cc.canMove(player, player.getX(), player.getY(), this.chunk)) {
                boolean found = false;
                // Try small offsets (in pixels) around the target position
                int maxOffset = 30;
                int step = 4;
                for (int r = step; r <= maxOffset && !found; r += step) {
                    for (int dx = -r; dx <= r && !found; dx += step) {
                        for (int dy = -r; dy <= r && !found; dy += step) {
                            int tryX = newPlayerX + dx;
                            int tryY = newPlayerY + dy;
                            if (tryX < 0 || tryY < 0) continue;
                            if (cc.canMove(player, tryX, tryY, this.chunk)) {
                                player.setCoordinates(tryX, tryY);
                                found = true;
                            }
                        }
                    }
                }

                // If no valid nearby position found, revert chunk and player coords
                if (!found) {
                    this.chunkCoordinates = oldChunkCoordinates;
                    player.setCoordinates(oldPlayerX, oldPlayerY);
                }
            }
        } else {
            // Revert the changes made to chunkCoordinates if loading failed as a fail-safe measure
            this.chunkCoordinates = oldChunkCoordinates;
        }
    }

    /**
     * Determines whether player can attack an enemy and attacks enemy if possible.
     */
    private void attackEnemies() {
        ArrayList<Enemy> enemiesKilled = new ArrayList<Enemy>();
        // If the player is unable to attack, return
        if (!player.canAttack()) {
            return;
        }

        for (Enemy enemy : enemies) {
            // If player attack wasn't successful, continue with next enemy
            if (!player.attack(enemy)) {
                break;
            }

            // If the player defeated the enemy, remove it from the ArrayList
            if (enemy.takeDamage(player.getDamage())) {
                // Add killed enemy to enemiesKilled List in order to be removed after for loop
                enemiesKilled.add(enemy);
            }
        }

        // Remove killed enemies
        enemies.removeAll(enemiesKilled);
    }

    /**
     * The game loop. Repeats every one 60th of a second.
     */
    public void run() {
        // Initiate cooldowns to prevent quickly entering and leaving chunks and reduce CPU load
        int chunkEnteringCooldown = 0;
        int pathCalculatorCooldown = 0;

        int playerX;
        int playerY;

        while (!gameOver) {
            // Redraw the screen
            repaint();

            // Check for movement and change player position
            player.move(chunk);

            playerX = player.getX();
            playerY = player.getY();

            /* Check if player has recently switched chunk
             *   and prevent them from entering a new chunk for a small time if that is the case. */
            if (chunkEnteringCooldown == 0) {
                /* Check if player is near a chunk border. If the player has switched chunks,
                 *   or if there was an attempt to load a new chunk,
                 *   start the chunk switchting cooldown period. */
                if (switchChunk(playerX, playerY)) {
                    chunkEnteringCooldown = 30;

                    // Set path calculator cooldown to 0
                    pathCalculatorCooldown = 0;

                    // As the player is switching chunk, the rest of the loop can be skipped
                    continue;
                }
            } else {
                chunkEnteringCooldown--;
            }

            for (Enemy enemy : enemies) {
                /* Calculate the shortest path to the player
                 * The enemy will not move for one update while calculating */
                if (pathCalculatorCooldown == 0) {
                    // Calculate path
                    enemy.setPath(pathCalculator.calculateShortestPath(enemy));
                } else {
                    // Go to the player following the calculated path
                    enemy.moveToPlayer(player);
                }

                enemy.calculateDistancePlayer(
                    player.getCentreX(), player.getCentreY());

                // If the enemy attacked succesfully
                if (enemy.attack()) {

                    // Deals damage to the player. If this returned true, the player was defeated.
                    if (player.takeDamage(enemy.getDamage())) {
                        gameOver = true;
                    }
                }
            }

            // Have player attack the enemies
            attackEnemies();

            // Set a cooldown for calculating the paths to reduce CPU load
            if (pathCalculatorCooldown == 0) {
                pathCalculatorCooldown = 30;
            } else {
                pathCalculatorCooldown--;
            }

            try {
                // Sleep every 60th of a second
                Thread.sleep((long) (1.0 / 60 * 1000));
            } catch (InterruptedException e) {
                System.out.println("The game was interrupted");
                System.out.println(e.getMessage());
                System.exit(0);
            }
        }

        // Once the player is defeated, the game loop stops

        // Repaint the screen one last time to show that the game is over
        repaint();
    }
}