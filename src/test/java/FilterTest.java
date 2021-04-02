import crawler.CrawlerLoadFilter;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class FilterTest {

    @Test
    void testDuplicateFilter() throws URISyntaxException {
        CrawlerLoadFilter filter = new CrawlerLoadFilter(true, false);

        assertTrue(filter.webpageShouldBeLoaded(new URI("https://google.com")));

        assertFalse(filter.webpageShouldBeLoaded(new URI("http://google.com")));

        assertFalse(filter.webpageShouldBeLoaded(new URI("https://google.com/")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("https://google.com#")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("https://google.com/#")));
    }
}
