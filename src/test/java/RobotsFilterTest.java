import crawler.webpage.filter.RobotsLoadFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RobotsFilterTest {

    RobotsLoadFilter filter;

    @BeforeEach
    void setup() {
        filter = new RobotsLoadFilter();
    }

    @ParameterizedTest
    @DisplayName("Test RobotsTxt")
    @ValueSource(strings = {
            "https://crawler-test.com",
            "https://crawler-test.com/robots_protocol/page_allowed_with_robots"
    })
    void testRobotsAllowed(String url) throws URISyntaxException {
        assertTrue(filter.webpageShouldBeLoaded(new URI(url)));
    }

    @ParameterizedTest
    @DisplayName("Test RobotsTxt")
    @ValueSource(strings = {
            "https://crawler-test.com/infinite",
            "https://crawler-test.com/speed_test"
    })
    void testRobotsTxtDisallowed(String url) throws URISyntaxException {
        assertFalse(filter.webpageShouldBeLoaded(new URI(url)));
    }

    @Test
    @DisplayName("Missing RobotsTxt")
    void testMissingRobotsTxt() throws URISyntaxException {
        /*
         * This Website doesn't have a /robots.txt file and this test depends on it not having one.
         * This is obviously not a good idea but probably the best for now..
         */

        assertTrue(filter.webpageShouldBeLoaded(new URI("http://www.simplecpudesign.com")));
    }
}
