package crawler.util;

import java.net.URI;
import java.text.DecimalFormat;

public class Util {
    /**
     * Formats a given amount of bytes as a more readable string.
     *
     * @param size  amount of bytes
     * @return      formatted string
     */
    public static String readableFileSize(double size) {
        return readableFileSize((long) size);
    }

    /**
     * Formats a given amount of bytes as a more readable string.
     *
     * @param size  amount of bytes
     * @return      formatted string
     */
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Checks the provided URI against a regex for http urls.
     *
     * @param uri   the URI to compare to the regex
     * @return      if the URI matches the regex
     */
    public static boolean isValidHttpUrl(URI uri) {
        return isValidHttpUrl(uri.toString());
    }

    /**
     * Checks the provided URI against a regex for http urls.
     *
     * @param uri   the URI to compare to the regex
     * @return      if the URI matches the regex
     */
    public static boolean isValidHttpUrl(String uri) {
        return uri.matches("https?:\\/\\/?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    }
}
