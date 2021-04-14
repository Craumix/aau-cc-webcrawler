package crawler.webpage.filter;

import crawler.util.Util;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

public class DuplicateLoadFilter implements WebpageLoadFilter {

    private static final CopyOnWriteArrayList<String> urlLog = new CopyOnWriteArrayList<>();

    @Override
    public boolean webpageShouldBeLoaded(URI uri) {
        if(!Util.isValidHttpUrl(uri))
            return false;

        String dupeCompUri = asCompareUriString(uri);
        if(urlLog.contains(dupeCompUri))
            return false;
        else
            urlLog.add(dupeCompUri);

        return true;

    }

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
