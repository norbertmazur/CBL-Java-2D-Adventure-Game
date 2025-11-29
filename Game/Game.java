/**
 * CBL Game Project for Programming 2IP90.
 * 
 * @author Lars Wouters
 * @author Norbert Mazur
 */
class Game {
    GameRenderer gameRenderer;
    Player player;

    /**
     * Initiates the game. Creates JFrame object with GameRenderer and starts the game loop.
     */
    public Game() {
        // Create new game window and start the game loop
        gameRenderer = new GameRenderer();
        ((GamePanel) gameRenderer.panel).startThread();
    }

    public static void main(String[] args) {
        new Game();
    }
}   