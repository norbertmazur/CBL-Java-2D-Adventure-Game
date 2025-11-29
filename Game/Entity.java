import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Entity class used for enemies and player.
 */
abstract class Entity {
    // x and y coordinates of entity
    private int x;
    private int y;

    // The speed in x and y direction of entity
    private int velocityX;
    private int velocityY;
    
    // Height and width of the entity
    private int width;
    private int height;

    private BufferedImage image;
    protected Map<String, BufferedImage> cachedImages;

    private int hp;

    private boolean switchingSprite = false;


    // The damage the entity deals with an attack
    private int damage;

    // The maximum amount of distance between entities to be able to attack each other
    private int attackRange;

    /**
     * Initiates an entity.
     * 
     * @param x Starting X coordinate
     * @param y Starting Y coordinate
     * @param hp Starting Hit Points
     * @param damage Damage entity deals with an attack
     * @param attackRange Maximum amount of pixels entity should be away from another to attack
     */
    public Entity(int x, int y, int hp, int damage, int attackRange) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.damage = damage;
        this.attackRange = attackRange;

        // Load sprite images
        this.cachedImages = loadEntityImages();

        // Set default image
        this.image = cachedImages.get("idle");

        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected int getVelocityX() {
        return velocityX;
    }

    protected int getVelocityY() {
        return velocityY;
    }

    public int getCentreX() {
        return x + this.width / 2;
    }

    public int getCentreY() {
        return y + this.height / 2;
    }

    public int getHP() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    protected void setVelocityX(int newVelocityX) {
        this.velocityX = newVelocityX;
    }

    protected void setVelocityY(int newVelocityY) {
        this.velocityY = newVelocityY;
    }

    /**
     * Gets the tile the entity is standing on determined from the top left.
     * @return Point with tile coordinates
     */
    public Point getTile() {
        return new Point(x / GamePanel.TILESIZE, y / GamePanel.TILESIZE);
    }

    /**
     * Gets the tile the entity is standing on determined from the middle.
     * @return Point with tile coordinates
     */
    public Point getCentreTile() {
        return new Point(this.getCentreX() / GamePanel.TILESIZE,
            this.getCentreY() / GamePanel.TILESIZE);
    }

    /**
     * Makes the entity take damage. 
     * If the attack was powerful enough to kill the entity, returns true. Returns false otherwise.
     * 
     * @param damage The amount of damage the attack dealt
     * @return true if the attack was powerful enough to kill the entity, else false
     */
    public boolean takeDamage(int damage) {
        // Switch to damaged sprite for 30 updates
        switchSprite("damaged", 30);

        // Decrease hp. HP minimum is 0
        this.hp = Math.max(0, hp - damage);

        // If the attack was fatal
        if (this.hp <= 0) {
            return true;
        }

        return false;
    }

    public void setImage(String imageName) {
        this.image = cachedImages.get(imageName);
    }

    /**
     * Loads the images of an entity.
     * 
     * @return Map with keys in String and values in BufferedImage
     */
    private Map<String, BufferedImage> loadEntityImages() {
        // Create map with loaded images
        Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();

        // Get the directory path where the images are stored in
        String dirPath = "assets/sprites/" + this.getClass().getSimpleName();

        // Set directory (resolve in case the working directory is different)
        File dir = FileUtils.resolveExistingFile(dirPath);

        // Guard: listFiles returns null when the path doesn't exist or isn't a directory
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            System.err.println("Sprite directory missing or empty: " + dir.getAbsolutePath());
            // Provide a tiny transparent placeholder so callers don't NPE when accessing dimensions
            BufferedImage placeholder = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            images.put("idle", placeholder);
            return images;
        }

        // Loop through images in the directory
        for (File img : files) {
            if (!img.isFile()) continue;

            // Get file name
            String imgName = img.getName();

            // Get location of extension
            int extensionIndex = imgName.lastIndexOf('.');
            if (extensionIndex <= 0) continue;

            // Initiate loaded image
            BufferedImage bufferedImage = null;

            try {
                // Read image directly from the file
                bufferedImage = ImageIO.read(img);
            } catch (IOException e) {
                bufferedImage = null;
            }

            // Put image in map under the file name without extension
            images.put(imgName.substring(0, extensionIndex), bufferedImage);
        }

        // Ensure there's always an "idle" image available
        if (!images.containsKey("idle")) {
            if (!images.isEmpty()) {
                String firstKey = images.keySet().iterator().next();
                images.put("idle", images.get(firstKey));
            } else {
                BufferedImage placeholder = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                images.put("idle", placeholder);
            }
        }

        return images;
    }

    /**
     * Switches the currently used image of an entity for a duration of time.
     * 
     * @param newImageName The name of the image that should be swapped in
     * @param duration The duration the image should be active
     */
    protected void switchSprite(String newImageName, int duration) {
        // Check if the image isn't already being switched
        if (!this.switchingSprite) {
            // Create instance to prepare to switch image
            ImageSwitcher imageSwitcher = new ImageSwitcher("idle", newImageName, duration, this);
        
            imageSwitcher.switchImage();

            /* Stop image switching in order to prevent player confusion
             * Image switching is enabled again by ImageSwitcher after the swapping has finished */
            this.switchingSprite = true;
        }
    }

    /**
     * Sets switchingSprite back to false. Should only be called by ImageSwitcher.
     */
    protected void finishedSwitchingSprite() {
        this.switchingSprite = false;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public int getAttackRange() {
        return this.attackRange;
    }
}