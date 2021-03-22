package crawler;

import java.net.URI;

public interface UriLoadFilter {
    boolean uriShouldBeLoaded(URI uri);
}
