package crawler.webpage.filter;

import crawler.util.Util;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

public class DuplicateLoadFilter implements WebpageLoadFilter {

    private static final CopyOnWriteArrayList<String> urlLog = new CopyOnWriteArrayList<>();

    /**
     * Checks if a this URI was already checked by this filter.
     * The first check for each URI returns true every check after that returns false.
     * URI don't have to match exactly, ignored is the protocol and trailing "/", "#" and "/#".
     *
     * @param uri   the URI to check against the list
     * @return      returns true only on the first check for each URI
     */
    @Override
    public boolean webpageShouldBeLoaded(URI uri) {
        String dupeCompUri = asCompareUriString(uri);
        if(urlLog.contains(dupeCompUri))
            return false;
        else
            urlLog.add(dupeCompUri);

        return true;

    }

    /**
     * Strips the protocol and trailing "/", "#" and "/#" from URI.
     *
     * @param uri   the URI to strip
     * @return      the striped URI
     */
    private String asCompareUriString(URI uri) {
        StringBuilder sb = new StringBuilder(uri.toString());

        //Strip URI Scheme
        sb.delete(0, uri.getScheme().length() + 3);

        //Remove trailing "#", "/", or "/#"
        if(sb.charAt(sb.length() - 1) == '#')
            sb.deleteCharAt(sb.length() - 1);
        if(sb.charAt(sb.length() - 1) == '/')
            sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }


}
