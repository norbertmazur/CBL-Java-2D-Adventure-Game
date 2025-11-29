/**
 * CollsionCheck determines if the player can move to the new location.
 */
public class CollisionCheck {
    // Number of pixels to inset the collision bounding box from left/right and top/bottom.
    // These are tuned to make the collision box more centered inside the player sprite.
    private static final int COLLISION_INSET_X = 12; // horizontal inset
    private static final int COLLISION_INSET_Y = 10; // vertical inset
    /**
     * Checks if the player can move to the new location.
     * 
     * @param player Player instance
     * @param newX New player x coordinate
     * @param newY New player y coordinate
     * @param chunk Chunk of the player
     * @return True if the player can move, false if not
     */
    public boolean canMove(Player player, int newX, int newY, TileType[][] chunk) {
        // First check if the player will go out of bounds (avoid array index errors)
        if (checkOutOfBounds(player, newX, newY)) {
            return false;
        }

        // Then check if the player's bounding box would overlap any wall tiles
        if (checkForWalls(player, newX, newY, chunk)) {
            return false;
        }

        // Movement is valid
        return true;
    }

    /**
     * Checks if the new location is a wall tile.
     * 
     * @param player Player instance
     * @param newX New player x coordinate
     * @param newY New player y coordinate
     * @param chunk Chunk of the player
     * @return True if the new location is a wall tile, false if not
     */
    private boolean checkForWalls(Player player, int newX, int newY, TileType[][] chunk) {
        // If chunk is null, treat as blocked
        if (chunk == null) return true;

        // Compute player's bounding box after moving, inset to allow slight overlap for smoother movement
        int left = newX + COLLISION_INSET_X;
        int top = newY + COLLISION_INSET_Y;
        int right = newX + player.getWidth() - 1 - COLLISION_INSET_X;
        int bottom = newY + player.getHeight() - 1 - COLLISION_INSET_Y;

        // If inset is larger than half the size, fall back to full box
        if (right < left) {
            left = newX;
            right = newX + player.getWidth() - 1;
        }
        if (bottom < top) {
            top = newY;
            bottom = newY + player.getHeight() - 1;
        }

        // Convert pixel bounds to tile indices
        int tileLeft = left / GamePanel.TILESIZE;
        int tileTop = top / GamePanel.TILESIZE;
        int tileRight = right / GamePanel.TILESIZE;
        int tileBottom = bottom / GamePanel.TILESIZE;

        // Clamp tile indices to chunk bounds to be safe
        tileLeft = Math.max(0, tileLeft);
        tileTop = Math.max(0, tileTop);
        tileRight = Math.min(GamePanel.CHUNKWIDTH - 1, tileRight);
        tileBottom = Math.min(GamePanel.CHUNKHEIGHT - 1, tileBottom);

        // Check every tile overlapped by the player's bounding box
        for (int ty = tileTop; ty <= tileBottom; ty++) {
            for (int tx = tileLeft; tx <= tileRight; tx++) {
                TileType t = chunk[ty][tx];
                if (t != null && t.isWall) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a player goes out of bounds when moving.
     * 
     * @param player Player instance
     * @param newX New player x coordinate
     * @param newY New player y coordinate
     * @return True if the player goes out of bounds, false if not
     */
    private boolean checkOutOfBounds(Player player, int newX, int newY) {
        if (newX < 0) {
            return true;
        } else if (newX + player.getWidth() > GameRenderer.SCREENWIDTH) {
            return true;
        }

        if (newY < 0) {
            return true;
        } else if (newY + player.getHeight() > GameRenderer.SCREENHEIGHT) {
            return true;
        }

        return false;
    }
}
