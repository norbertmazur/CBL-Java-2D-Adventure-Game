import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ChunkLoader loads in chunks and enemies.
 */
class ChunkLoader {
    /**
     * Loads a chunk by reading a text file containing 8 x 8 single letters.
     * Returns null if the chunk couldn't be loaded.
     * 
     * @param chunkName The name of the chunk to read
     * @return A array with the tiletypes of each tile in the chunk.
     */
    public TileType[][] loadChunk(String chunkName) {
        String tileLetter;

        // The file with the data for the chunk
        File chunkFile = FileUtils.resolveExistingFile("chunks/" + chunkName + "/tiles.txt");

        // Initialise the chunk
        TileType[][] chunk = new TileType[GamePanel.CHUNKHEIGHT][GamePanel.CHUNKWIDTH];

        try {
            // Use scanner to read the file
            Scanner scanner = new Scanner(chunkFile);

            // Loop over each tile in a chunk
            for (int i = 0; i < GamePanel.CHUNKHEIGHT; i++) {
                for (int j = 0; j < GamePanel.CHUNKWIDTH; j++) {
                    // Get the letter in the chunk file associated with the tile type
                    tileLetter = scanner.next();

                    for (TileType type : TileType.values()) {
                        // Check which type the tile is and set the tile in the chunk accordingly
                        if (type.letter.equals(tileLetter)) {
                            chunk[i][j] = type;
                        }
                    }
                }
            }

            // Close the scanner
            scanner.close();
        } catch (FileNotFoundException e) {
            // Unable to load chunk, return null
            return null;
        }

        return chunk;
    }

    /**
     * Loads in enemies in a chunk. 
     * 
     * @param chunkName The name of the chunk the player is entering
     * @return ArrayList of enemies
     */
    public ArrayList<Enemy> loadEnemies(String chunkName) {
        // Initialise array
        ArrayList<Enemy> enemies = new ArrayList<Enemy>();

        // Create the parameter types that are used for initialising the enemy class
        Class<?>[] paramTypes = {int.class, int.class};

        // The file with the data for the enemies in a chunk
        File enemiesFile = FileUtils.resolveExistingFile("chunks/" + chunkName + "/enemies.txt");

        try {
            // Use scanner to read the file
            Scanner scanner = new Scanner(enemiesFile);

            // Iterate over lines in the file.
            while (scanner.hasNextLine()) {
                // Get the type name of the enemy
                String enemyName = scanner.next();

                // Get x and y coordinates of the enemy
                int x = scanner.nextInt();
                int y = scanner.nextInt();

                // Initiate enemy class variable
                Class<?> enemyClass;
                try {
                    // Get the class of the enemy type by name
                    enemyClass = Class.forName(enemyName);

                } catch (ClassNotFoundException e) {
                    System.out.println("Enemy type does not exist!");
                    e.printStackTrace();
                    continue; // Continue with while loop
                }
                
                // Initiate enemy variable
                Enemy enemy;
                try {
                    // Create new instance of enemy
                    enemy = (Enemy) enemyClass
                        .getDeclaredConstructor(paramTypes).newInstance(x, y);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException e) {

                    System.out.println("Unable to load in enemy \"" + enemyName 
                        + "\" located on (" + x + ", " + y + ").");
                    e.printStackTrace();
                    continue; // Continue with while loop
                }
                    
                // Add newly intiated enemy to enemies array
                enemies.add(enemy);                          
            }

            // Close the scanner
            scanner.close();
        } catch (FileNotFoundException e) {
            /* If there is no enemies.txt file in the chunk folder, the chunk has no enemies.
             * Therefore, return the empty enemies array list. */
            return enemies;
        }

        return enemies;
    }
}