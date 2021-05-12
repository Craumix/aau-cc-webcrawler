package crawler.webpage.connector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupConnector implements Connector {

    private String userAgent = "Java/" + System.getProperty("java.version");

    @Override
    public Document getDocument(String pageURI) throws IOException {
        return Jsoup.connect(pageURI).userAgent(userAgent).get();
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
