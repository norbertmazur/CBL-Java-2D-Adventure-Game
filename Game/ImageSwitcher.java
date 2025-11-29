/**
 * ImageSwitcher helps swapping an Entity sprite using Thread.
 */
class ImageSwitcher implements Runnable {
    String oldImage;
    String newImage;
    
    int duration;

    Entity entity; // Entity to switch image of

    private Thread switcherThread;

    /**
     * Switches image of entity and reverts switch after set duration.
     * 
     * @param oldImage Name of the image before swapping
     * @param newImage Name of image to swap to
     * @param duration Duration of swap
     * @param entity The entity to swap the image of
     */
    public ImageSwitcher(String oldImage, String newImage, int duration, Entity entity) {
        this.oldImage = oldImage;
        this.newImage = newImage;
        this.duration = duration;

        this.entity = entity;
    }

    /**
     * Creates and starts thread to swap image.
     */
    public void switchImage() {
        switcherThread = new Thread(this);
        switcherThread.start();
    }

    public void run() {
        // Sets new image
        entity.setImage(newImage);

        try {
            // Sleep the duration
            Thread.sleep((long) (duration * 1.0 / 60 * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Revert change
        entity.setImage(oldImage);

        // Unblock swapping of image of sprite
        entity.finishedSwitchingSprite();
        
    }
}