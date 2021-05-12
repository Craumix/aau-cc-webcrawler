package crawler.webpage.connector;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface Connector {
    Document getDocument(String location) throws IOException;

    void setUserAgent(String userAgent);
    String getUserAgent();
}
