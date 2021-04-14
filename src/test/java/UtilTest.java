import crawler.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {

    @Test
    @DisplayName("This is a dummy test to achieve 100% Code Coverage by instantiating the Util class")
    void dummyUtilInstance() {
        new Util();
    }

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

    @Test
    @DisplayName("Tests invalid Values (<= 0) for readable Byte-Formatting")
    void testInvalidInputsForByteFormatting() {
        assertEquals(Util.readableFileSize(0l), "0");
        assertEquals(Util.readableFileSize(0.0), "0");

        assertEquals(Util.readableFileSize(-1l), "0");
        assertEquals(Util.readableFileSize(-1.0), "0");
    }

    @Test
    @DisplayName("Tests valid Value values for readable Byte-Formatting")
    void testValidInputsForByteFormatting() {
        assertEquals(Util.readableFileSize(8L), "8 B");
        assertEquals(Util.readableFileSize(8.0), "8 B");

        assertEquals(Util.readableFileSize(2048L), "2 kB");
        assertEquals(Util.readableFileSize(2048.0), "2 kB");

        assertEquals(Util.readableFileSize(1024L * 1024L * 50L), "50 MB");

        assertEquals(Util.readableFileSize(1024.0 * 1024.0 * 4.5), "4.5 MB");
    }
}
