package crawler.webpage.filter;

import java.net.URI;

public interface WebpageLoadFilter {
    boolean webpageShouldBeLoaded(URI webpageURI);
}
