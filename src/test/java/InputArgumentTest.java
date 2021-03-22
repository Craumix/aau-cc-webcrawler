import crawler.Main;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InputArgumentTest {

    @Test
    @DisplayName("Test to make sure the regex detects valid urls")
    void testValidURLs() {
        assertTrue(Main.isValidHttpUrl("http://google.com"));
        assertTrue(Main.isValidHttpUrl("http://www.google.com"));

        assertTrue(Main.isValidHttpUrl("https://google.com"));
        assertTrue(Main.isValidHttpUrl("https://www.google.com"));
    }

    @Test
    @DisplayName("Test to make sure the regex detects invalid urls")
    void testInvalidURLs() {
        //No Scheme
        assertFalse(Main.isValidHttpUrl("google.com"));
        assertFalse(Main.isValidHttpUrl("www.google.com"));

        //Invalid Scheme
        assertFalse(Main.isValidHttpUrl("abc://google.com"));
        assertFalse(Main.isValidHttpUrl("abc://www.google.com"));

        //Just a hostname
        assertFalse(Main.isValidHttpUrl("http://hostname"));
        assertFalse(Main.isValidHttpUrl("http://hostname"));
    }
}
