import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Uses KeyListener to determine the keys the user is pressing.
 */
public class KeyHandler implements KeyListener {
    public boolean up = false;
    public boolean left = false;
    public boolean right = false;
    public boolean down = false;
    public boolean space = false;
    public boolean debugInfo = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // If the W key or up arrow key is pressed 
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
            up = true;
        }

        // If the S key or down arrow key is pressed
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
            down = true;
        }

        // If the A key or left arrow key is pressed
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            left = true;
        }

        // If the D key or right arrow key is pressed
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            right = true;
        }

        // If the Spacebar is pressed
        if (key == KeyEvent.VK_SPACE) {
            space = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // If the W key or up arrow key was released 
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
            up = false;
        }

        // If the S key or down arrow key was released
        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
            down = false;
        }

        // If the A key or left arrow key was released
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            left = false;
        }

        // If the D key or right arrow key was released
        if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            right = false;
        }

        // If the Spacebar was released
        if (key == KeyEvent.VK_SPACE) {
            space = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}