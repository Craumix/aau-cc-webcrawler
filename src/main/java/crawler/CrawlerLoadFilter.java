package crawler;

import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;

public class CrawlerLoadFilter implements UriLoadFilter {

    private static final CopyOnWriteArrayList<URI> uriLog = new CopyOnWriteArrayList<>();

    private final boolean omitDuplicates;

    public CrawlerLoadFilter(boolean omitDuplicates) {
        this.omitDuplicates = omitDuplicates;
    }

    @Override
    public boolean uriShouldBeLoaded(URI uri) {
        if(omitDuplicates) {
            if(uriLog.contains(uri)) {
                return false;
            }else {
                uriLog.add(uri);
                return true;
            }
        }

        return true;
    }
}
