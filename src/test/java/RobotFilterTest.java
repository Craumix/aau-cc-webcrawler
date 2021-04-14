import crawler.DuplicateLoadFilter;
import crawler.RobotLoadFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotFilterTest {

    @Test
    @DisplayName("Test RobotsTxt")
    void testRobotsTxt() throws URISyntaxException {
        RobotLoadFilter filter = new RobotLoadFilter();

        assertTrue(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com")));
        assertTrue(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com/robots_protocol/page_allowed_with_robots")));

        assertFalse(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com/infinite")));
        assertFalse(filter.webpageShouldBeLoaded(new URI("https://crawler-test.com/speed_test")));
    }

    @Test
    @DisplayName("Missing RobotsTxt")
    void testMissingRobotsTxt() throws URISyntaxException {
        RobotLoadFilter filter = new RobotLoadFilter();

        /**
         * This Website doesn't have a /robots.txt file and this test depends on it not having one.
         * This is obviously not a good idea but probably the best for now..
         */

        assertTrue(filter.webpageShouldBeLoaded(new URI("http://www.simplecpudesign.com")));
    }
}
