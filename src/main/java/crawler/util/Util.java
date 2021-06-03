package crawler.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;

public class Util {

    private Util() {}
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
        if (size <= 0)
            return "0";
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
        return uri.matches("https?://?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)");
    }

    /**
     * Generates a JSONObject with a LinkedHashMap instead of a Hashmap in order to maintain order of keys. <br>
     * https://stackoverflow.com/a/62476486
     *
     * @return JSONObject with ordered keys
     */
    public static JSONObject makeJSONObjectWithOrderedKeys() {
        return new JSONObject() {
            @Override
            public JSONObject put(String key, Object value) throws JSONException {
                try {
                    Field map = JSONObject.class.getDeclaredField("map");
                    map.setAccessible(true);
                    Object mapValue = map.get(this);
                    if (!(mapValue instanceof LinkedHashMap)) {
                        map.set(this, new LinkedHashMap<>());
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return super.put(key, value);
            }
        };
    }
}
