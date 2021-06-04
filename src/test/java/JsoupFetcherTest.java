import crawler.webpage.fetcher.JsoupFetcher;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class JsoupFetcherTest {

    JsoupFetcher fetcher;

    @BeforeEach
    void setup() {
        fetcher = new JsoupFetcher();
    }

    @Test
    @DisplayName("Test if a website can be fetched")
    void testFetchDocument() throws IOException {
        Document doc = fetcher.fetchDocument("https://example.com", "Crawler");

        assertEquals("Example Domain", doc.title());
    }
}
