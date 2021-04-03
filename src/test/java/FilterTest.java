import crawler.CrawlerLoadFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class FilterTest {

    @Test
    @DisplayName("Test CrawlerLoadFilter duplicate detection")
    void testDuplicateFilter() throws URISyntaxException {
        CrawlerLoadFilter filter = new CrawlerLoadFilter(true, false);

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

    @Test
    @DisplayName("Test CrawlerLoadFilter for invalid urls")
    void testFilterInvalidUrlDetection() throws URISyntaxException {
        CrawlerLoadFilter filter = new CrawlerLoadFilter(false, false);

        assertTrue(filter.webpageShouldBeLoaded(new URI("https://google.com")));

        assertFalse(filter.webpageShouldBeLoaded(new URI("abc://domain.com")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("http://domain")));
    }

    @Test
    @DisplayName("Test RobotsTxt")
    void testRobotsTxt() throws URISyntaxException {
        CrawlerLoadFilter filter = new CrawlerLoadFilter(false, true);

        assertTrue(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com")));
        assertTrue(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com/robots_protocol/page_allowed_with_robots")));

        assertFalse(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com/infinite")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com/speed_test")));
    }
}
