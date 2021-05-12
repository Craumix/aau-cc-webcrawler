package crawler.webpage.fetcher;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface Fetcher {
    Document fetchDocument(String location, String userAgent) throws IOException;
}
