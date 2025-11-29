import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The class for each tile in a chunk.
 */
class Tile {
    TileType type;

    /**
     * Initiates Tile.
     * 
     * @param type Type of the tile
     */
    public Tile(TileType type) {
        // Sets tile type
        this.type = type;
    }

    /**
     * Gets the image of the tile.
     * 
     * @return The BufferedImage texture of the tile
     */
    public BufferedImage getTileImage() {
        BufferedImage image;

        try {
            image = ImageIO.read(new File(type.image));
        } catch (IOException e) {
            // Unable to load in image, return null
            System.out.println(e);
            return null;
        }

        return image;
    }
}