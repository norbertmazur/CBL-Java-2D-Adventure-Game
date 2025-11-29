/**
 * Player class handles the movement of the player.
 */
class Player extends Entity {
    KeyHandler keyHandler;

    private int attackCooldown = 0;

    private CollisionCheck collisionChecker = new CollisionCheck();

    /**
     * Initiates player.
     * 
     * @param keyHandler The key handler to use
     */
    public Player(int x, int y, KeyHandler keyHandler) {
        super(x, y, 10, 5, 50);

        this.keyHandler = keyHandler;
    }

    /**
     * Determines if a player can attack.
     * 
     * @return True if the player can attack, else false
     */
    public boolean canAttack() {
        // Check if the attack cooldown is still active
        if (attackCooldown > 0) {
            attackCooldown--;
            return false;
        }

        // Check if isn't spacebar is pressed and thus player isn't attacking
        if (!keyHandler.space) {
            return false;
        }

        attackCooldown = 60;

        this.switchSprite("attacking", attackCooldown);

        return true;
    }

    /**
     * Lets player attack an enemy.
     *  
     * @param enemy The enemy to attack
     * @return True if the attack was succesful, false if not
     */
    public boolean attack(Enemy enemy) {
        // If the player is not in range for the player to 'reach' the enemy, return false
        if (enemy.getDistanceToPlayer() > this.getAttackRange()) {
            return false;
        }
        
        return true;
    }

    /**
     * Sets the coordinates of the player to a new location and sets velocity to 0.
     */
    public void setCoordinates(int newX, int newY) {
        this.setX(newX);
        this.setY(newY);

        this.setVelocityX(0);
        this.setVelocityY(0);
    }

    /**
     * Determine whether the user holds down a key used for movement and set velocity accordingly.
     * If there are no keys being hold in either the X or Y direction, 
     *  the player slows down in that direction.
     * 
     * @param chunk The chunk the player is in
     */
    public void move(TileType[][] chunk) {
        // The x and y coordinate of the player after moving
        int newX;
        int newY;

        // Current velocity of player
        int velocityX = this.getVelocityX();
        int velocityY = this.getVelocityY();

        if (keyHandler.up) {
            changeVelocityY(-2);
        } else if (keyHandler.down) {
            changeVelocityY(2);
        } else {
            // Gradually bring the player to a stop when not moving
            if (velocityY < 0) {
                changeVelocityY(Math.min(2, 2 - velocityY));
            } else if (velocityY > 0) {
                changeVelocityY(Math.max(-2, -velocityY - 2));
            }
        }

        if (keyHandler.left) {
            changeVelocityX(-2);
        } else if (keyHandler.right) {
            changeVelocityX(2);
        } else {
            // Gradually bring the player to a stop when not moving
            if (velocityX < 0) {
                changeVelocityX(Math.min(2, 2 - velocityX));
            } else if (velocityX > 0) {
                changeVelocityX(Math.max(-2, -velocityX - 2));
            }
        }

        // Get velocity after the changes
        velocityX = this.getVelocityX();
        velocityY = this.getVelocityY();

        newX = this.getX() + velocityX;
        newY = this.getY() + velocityY;

        // Check if movement isn't illegal
        if (collisionChecker.canMove(this, newX, newY, chunk)) {
            this.setX(newX);
            this.setY(newY);
        } else {
            // Set velocity in both directions to 0 in order to prevent illegal movement
            this.setVelocityX(0);
            this.setVelocityY(0);
        }
    }

    /**
     * Changes the x velocity of the player. Caps at (-)10.
     * 
     * @param deltaVelocityX The amount to change the x velocity by
     */
    private void changeVelocityX(int deltaVelocityX) {
        // Set velocity to a maximum
        if (deltaVelocityX >= 0) {
            this.setVelocityX(Math.min(this.getVelocityX() + deltaVelocityX, 10));
        } else {
            this.setVelocityX(Math.max(this.getVelocityX() + deltaVelocityX, -10));
        }
    }

    /**
     * Changes the x velocity of the player. Caps at (-)10.
     * 
     * @param deltaVelocityY The amount to change the y velocity by
     */
    private void changeVelocityY(int deltaVelocityY) {
        // Set velocity to a maximum
        if (deltaVelocityY >= 0) {
            this.setVelocityY(Math.min(this.getVelocityY() + deltaVelocityY, 10));
        } else {
            this.setVelocityY(Math.max(this.getVelocityY() + deltaVelocityY, -10));
        }
    }
}