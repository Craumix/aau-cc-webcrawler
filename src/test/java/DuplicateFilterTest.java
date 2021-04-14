import crawler.webpage.filter.DuplicateLoadFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class DuplicateFilterTest {


    @Test
    @DisplayName("Test CrawlerLoadFilter duplicate detection")
    void testDuplicateFilter() throws URISyntaxException {
        DuplicateLoadFilter filter = new DuplicateLoadFilter();

        filter.webpageShouldBeLoaded(new URI("https://google.com"));

        assertFalse(filter.webpageShouldBeLoaded(new URI("https://google.com")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("https://google.com/")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("https://google.com#")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("https://google.com/#")));

        assertFalse(filter.webpageShouldBeLoaded(new URI("http://google.com")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("http://google.com/")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("http://google.com#")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("http://google.com/#")));
    }

    /*
    @Test
    @DisplayName("Test CrawlerLoadFilter for invalid urls")
    void testFilterInvalidUrlDetection() throws URISyntaxException {
        DuplicateLoadFilter filter = new DuplicateLoadFilter();

        assertTrue(filter.webpageShouldBeLoaded(new URI("https://google.com")));

        assertFalse(filter.webpageShouldBeLoaded(new URI("abc://domain.com")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("http://domain")));
    }
    */
}
