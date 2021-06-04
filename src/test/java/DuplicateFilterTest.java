import crawler.webpage.filter.DuplicateLoadFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class DuplicateFilterTest {

    DuplicateLoadFilter filter;

    @BeforeEach
    void setup() {
        filter = new DuplicateLoadFilter();
    }

    @ParameterizedTest
    @DisplayName("Test if the duplicate load filter detects duplicates")
    @ValueSource(strings = {
            "https://google.com",
            "https://google.com/",
            "https://google.com#",
            "https://google.com/#",
            "http://google.com",
            "http://google.com/",
            "http://google.com#",
            "http://google.com/#"
    })
    void testDuplicateFilter(String url) throws URISyntaxException {
        filter.webpageShouldBeLoaded(new URI("https://google.com"));

        assertFalse(filter.webpageShouldBeLoaded(new URI(url)));
    }

    @ParameterizedTest
    @DisplayName("Test if the duplicate load filter lets non duplicates through")
    @ValueSource(strings = {
            "http://webpage.com",
            "https://google.com/a",
            "https://google.de"
    })
    void testFilterInvalidUrlDetection(String url) throws URISyntaxException {
        filter.webpageShouldBeLoaded(new URI("https://google.com"));

        assertTrue(filter.webpageShouldBeLoaded(new URI(url)));
    }

}
