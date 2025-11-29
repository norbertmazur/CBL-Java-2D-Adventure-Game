import java.awt.*;
import javax.swing.*;

/** 
 * GameRenderer creates a new JFrame and a new JPanel and sets screen dimension. */
public class GameRenderer {
    JFrame frame;
    JPanel panel;
    
    protected static final int SCREENWIDTH = 400;
    protected static final int SCREENHEIGHT = 400;

    /**
     * Creates a new JFrame and sets up the rendering of the game.
     */
    public GameRenderer() {
        frame = new JFrame("Game");

        panel = new GamePanel();

        // Set screen size
        frame.setSize(SCREENWIDTH, SCREENHEIGHT);
        panel.setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));

        frame.add(panel);

        // Set window to middle of screen
        frame.setLocationRelativeTo(null);

        // Stops the program when JFrame is closed by the user
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set all components to preferred size
        frame.pack();
    
        frame.setVisible(true);
        frame.repaint();
    } 
}
