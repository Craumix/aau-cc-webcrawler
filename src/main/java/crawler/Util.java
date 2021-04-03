package crawler;

import java.net.URI;
import java.text.DecimalFormat;

public class Util {
    public static String readableFileSize(double size) {
        return readableFileSize((long) size);
    }
    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static boolean isValidHttpUrl(URI url) {
        return isValidHttpUrl(url.toString());
    }
    public static boolean isValidHttpUrl(String url) {
        return url.matches("https?:\\/\\/?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    }
}
