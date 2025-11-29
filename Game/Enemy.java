import java.awt.Point;
import java.util.LinkedList;

/**
 * The Enemy class. Supports multiple different enemy types.
 */
abstract class Enemy extends Entity {
    // Distance of enemy to player
    private double distanceToPlayer;

    private int attackCooldown = 0;

    // The path the enemy has to take to reach the player
    private LinkedList<Node> path; 

    // Maximum movement speed
    private int maxVelocity;

    /**
     * Initiate an enemy and loads in the image of the enemy.
     * @param x x coordinate of the enemy
     * @param y y coordinate of the enemy
     */
    public Enemy(int x, int y, int hp, int damage, int attackRange, int maxVelocity) {
        super(x, y, hp, damage, attackRange);

        this.maxVelocity = maxVelocity;
    }

    /**
     * Makes an enemy attack.
     * 
     * @return True if the attack was succesful, false if not.
     */
    public boolean attack() {
        if (attackCooldown > 0) {
            attackCooldown--;
            return false;
        }

        // If the player is not in range for the enemy to 'reach' the player, return
        if (this.distanceToPlayer > this.getAttackRange()) {
            return false;
        }
        
        attackCooldown = 60;

        this.switchSprite("attacking", attackCooldown);

        return true;
    }

    public double getDistanceToPlayer() {
        return distanceToPlayer;
    }

    /**
     * Calculates distance of the player to the enemy.
     * 
     * @param playerCentreX The X coordinate of the centre of the player
     * @param playerCentreY The Y coordinate of the centre of the player
     */
    public void calculateDistancePlayer(int playerCentreX, int playerCentreY) {
        // Calculates the distance from the centre of the enemy to the centre of the player
        this.distanceToPlayer = Math.sqrt(
            Math.pow(playerCentreX - (this.getCentreX()), 2)
            + Math.pow(playerCentreY - (this.getCentreY()), 2)
            );
    }

    /**
     * Sets the path to the player.
     * 
     * @param path LinkedList of Node with path to player
     */
    public void setPath(LinkedList<Node> path) {
        this.path = path;
    }

    /**
     * Moves to the player using the calculated path.
     * 
     * @param player Player instance
     */
    public void moveToPlayer(Player player) {
        // Get own tile
        Point selfTile = this.getTile();

        // The x and y the enemy should move to
        int targetX;
        int targetY;

        // The velocity of the enemy after determining where to move
        int newVelocityX;
        int newVelocityY;

        // Determine where to move to
        Point target = getMovementTarget(player, selfTile);

        targetX = (int) target.getX();
        targetY = (int) target.getY();

        /* Check if enemy is on the left or on the right of the target 
         * and set X velocity accordingly */
        if (this.getX() < target.getX()) {
            /* The enemy should not move further than the current target
             * therefore cap the velocity at either the maximum enemy speed 
             * or the distance left between the target and the enemy */
            newVelocityX = Math.min(targetX - this.getX(), maxVelocity);

        } else {
            newVelocityX = Math.max(targetX - this.getX(), -maxVelocity);
        }
        this.setVelocityX(newVelocityX);

        // Check if enemy is above or below of the target and set Y velocity accordingly
        if (this.getY() < targetY) {
            newVelocityY = Math.min(targetY - this.getY(), maxVelocity);
            
        } else {
            newVelocityY = Math.max(targetY - this.getY(), -maxVelocity);
        }
        this.setVelocityY(newVelocityY);

        // Set new X and Y
        this.setX(this.getVelocityX() + this.getX());
        this.setY(this.getVelocityY() + this.getY());
    }

    /**
     * Determines where to enemy should move to.
     * 
     * @param player Player instance
     * @param selfTile The tile the enemy is on
     * @return Point with x and y coordinates of where to move to
     */
    private Point getMovementTarget(Player player, Point selfTile) {
        // Get the player tile
        Point playerTile = player.getCentreTile();
        
        // Check if enemy is in the player's tile
        if (selfTile.getX() == playerTile.getX() && selfTile.getY() == playerTile.getY()) {
            return new Point(player.getX(), player.getY());
        }

        /* If the enemy reached the player tile (and it was thus reached) 
         * before a new calculation occured */
        if (path.size() == 0) { 
            // Stop moving as we can't be sure of the path anymore (fail-safe)
            return new Point(this.getX(), this.getY());
        }
        
        Node targetNode = path.getFirst();
        
        int targetX = targetNode.getColumn() * GamePanel.TILESIZE;
        int targetY = targetNode.getRow() * GamePanel.TILESIZE;

        // Check if enemy has reached the target during last move
        if (targetX == this.getX() && targetY == this.getY()) {
            // Remove tile from path
            path.removeFirst();

            if (path.size() > 0) {
                // Get new target x and y
                targetNode = path.getFirst();
                targetX = targetNode.getColumn() * GamePanel.TILESIZE;
                targetY = targetNode.getRow() * GamePanel.TILESIZE;

            // Player tile reached
            } else {
                targetX = player.getX();
                targetY = player.getY();
            }
        }

        return new Point(targetX, targetY);
    }
}