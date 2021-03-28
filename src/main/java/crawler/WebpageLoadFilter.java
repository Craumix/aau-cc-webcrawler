package crawler;

import java.net.URI;

public interface WebpageLoadFilter {
    boolean webpageShouldBeLoaded(URI webpageURI);
}
