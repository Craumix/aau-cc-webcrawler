package connector;

import crawler.webpage.connector.Connector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class MockedJsoupConnector implements Connector {
    @Override
    public Document getDocument(String location) throws IOException {
        location = location.replaceFirst("https://", "");
        location = location.replaceFirst("http://", "");

        return Jsoup.parse(new File(String.format("src/test/java/connector/test-sites/%s.html",location)), null);
    }

    @Override
    public void setUserAgent(String userAgent) { }

    @Override
    public String getUserAgent() { return null; }

}
