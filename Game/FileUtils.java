import java.io.File;

/**
 * Small utility to resolve relative file paths by searching upward
 * through parent directories. This helps when the program is started
 * from a different working directory (IDE vs terminal).
 */
public class FileUtils {
    /**
     * Attempts to find an existing file by checking the given relative path
     * in the current working directory and up through several parent directories.
     * If no existing file is found, returns a File pointing to the original relative path.
     *
     * @param relativePath Relative path to resolve
     * @return File pointing to an existing file/directory if found, otherwise File(relativePath)
     */
    public static File resolveExistingFile(String relativePath) {
        File current = new File(System.getProperty("user.dir"));
        // Check current directory and up to 6 parents
        File start = current;
        for (int i = 0; i < 6 && current != null; i++) {
            File candidate = new File(current, relativePath);
            if (candidate.exists()) {
                return candidate;
            }
            current = current.getParentFile();
        }

        // If not found by walking up, try searching one level down in the original start directory
        File[] children = start.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    File candidate = new File(child, relativePath);
                    if (candidate.exists()) {
                        return candidate;
                    }
                }
            }
        }

        // Fall back to using the relative path as-is (may not exist)
        return new File(relativePath);
    }
}
