package mocks;

import crawler.webpage.fetcher.Fetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class LocalFileFetcher implements Fetcher {
    @Override
    public Document fetchDocument(String location, String userAgent) throws IOException {
        location = location.replaceFirst("https://", "");
        location = location.replaceFirst("http://", "");

        location = location.replaceFirst("\\.test","");

        if (location.endsWith(".error"))
            throw new IOException(location);

        return Jsoup.parse(new File(String.format("src/test/java/mocks/test-sites/%s.html",location)), null);
    }

}
