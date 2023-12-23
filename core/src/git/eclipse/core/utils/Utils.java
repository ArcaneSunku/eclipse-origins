package git.eclipse.core.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * <p>A static class meant to be used mostly for Reading and Writing to files.</p>
 */
public class Utils {

    public static final String NIL_STRING = "niL";

    public static String readFile(String filePath) {
        String str;

        try {
            str = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("Failed to read file: " + filePath);
        }

        return str;
    }

    /**
     * <p>Reads the contents of a specified, internal file and gives them to us as a String</p>
     *
     * @param filePath the internal location of our File we'll read.
     * @return the contents of the file as a String.
     */
    public static String StringFromFile(String filePath) {
        return StringFromFile(filePath, true);
    }

    /**
     * <p>Reads the contents of a specified file and gives them to us as a String</p>
     *
     * @param filePath the location of our File we'll read.
     * @param internal whether we are reading from within the jar or not.
     * @return the contents of the file as a String.
     */
    public static String StringFromFile(String filePath, boolean internal) {
        String result;
        try {
            if (!internal) {
                    File file = new File(filePath);
                    if (!file.exists())
                        throw new IOException("Failed to load file: " + filePath);

                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder builder = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        builder.append(line).append("\n");
                    }
                    result = builder.toString();

                    br.close();
            } else {
                InputStream is = new BufferedInputStream(Objects.requireNonNull(Utils.class.getResourceAsStream("/" + filePath)));
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                result = builder.toString();

                br.close();
                is.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return NIL_STRING;
        }

        return result;
    }

}
