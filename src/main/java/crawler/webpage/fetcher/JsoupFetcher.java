package crawler.webpage.fetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupFetcher implements Fetcher {

    @Override
    public Document fetchDocument(String pageURI, String userAgent) throws IOException {
        return Jsoup.connect(pageURI).userAgent(userAgent).get();
    }

}
