import crawler.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlRegexTest {

    @Test
    @DisplayName("Test to make sure the regex detects valid urls")
    void testValidURLs() {
        assertTrue(Util.isValidHttpUrl("http://google.com"));
        assertTrue(Util.isValidHttpUrl("http://www.google.com"));

        assertTrue(Util.isValidHttpUrl("https://google.com"));
        assertTrue(Util.isValidHttpUrl("https://www.google.com"));
    }

    @Test
    @DisplayName("Test to make sure the regex detects invalid urls")
    void testInvalidURLs() {
        //No Scheme
        assertFalse(Util.isValidHttpUrl("google.com"));
        assertFalse(Util.isValidHttpUrl("www.google.com"));

        //Invalid Scheme
        assertFalse(Util.isValidHttpUrl("abc://google.com"));
        assertFalse(Util.isValidHttpUrl("abc://www.google.com"));

        //Just a hostname
        assertFalse(Util.isValidHttpUrl("http://hostname"));
        assertFalse(Util.isValidHttpUrl("http://hostname"));
    }
}
