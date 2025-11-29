/**
 * The collecion of all possible tiles types.
 */
public enum TileType {
    Grass("assets/img/grass.png", false, "G"),
    Cobblestone("assets/img/cobblestone.jpg", false, "C"),
    Wall("assets/img/wall.jpg", true, "W");

    String image;
    boolean isWall;
    String letter;

    TileType(String image, boolean isWall, String letter) {
        this.image = image;
        this.isWall = isWall;
        this.letter = letter;
    }
}