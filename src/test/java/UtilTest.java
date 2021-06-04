import crawler.util.Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UtilTest {

    @ParameterizedTest
    @DisplayName("Test to make sure the regex detects valid urls")
    @ValueSource(strings = {
            "http://google.com",
            "http://www.google.com",
            "https://google.com",
            "https://www.google.com"
    })
    void testValidURLs(String url) {
        assertTrue(Util.isValidHttpUrl(url));
    }

    @ParameterizedTest
    @DisplayName("Test to make sure the regex detects urls missing a scheme")
    @ValueSource(strings = {
            "google.com",
            "www.google.com"
    })
    void testInvalidURLsNoScheme(String url) {
        assertFalse(Util.isValidHttpUrl(url));
    }

    @ParameterizedTest
    @DisplayName("Test to make sure the regex detects urls with an invalid scheme")
    @ValueSource(strings = {
            "abc://google.com",
            "abc://www.google.com"
    })
    void testInvalidURLsInvalidSchemes(String url) {
        assertFalse(Util.isValidHttpUrl(url));
    }

    @ParameterizedTest
    @DisplayName("Test to make sure the regex detects urls with just a hostname")
    @ValueSource(strings = {
            "http://hostname",
            "https://hostname"
    })
    void testInvalidURLs(String url) {
        assertFalse(Util.isValidHttpUrl(url));
    }

    @ParameterizedTest
    @DisplayName("Tests invalid double Values (<= 0) for readable Byte-Formatting")
    @ValueSource(doubles = {0.0, -1.0})
    void testInvalidDoubleInputsForByteFormatting(double input) {
        assertEquals("0", Util.readableFileSize(input));
    }

    @ParameterizedTest
    @DisplayName("Tests invalid long Values (<= 0) for readable Byte-Formatting")
    @ValueSource(longs = {0L, -1L})
    void testInvalidLongInputsForByteFormatting(long input) {
        assertEquals("0", Util.readableFileSize(input));
    }

    @ParameterizedTest
    @DisplayName("Tests valid long values for readable Byte-Formatting")
    @CsvSource({
            "8,        8 B",
            "2048,     2 kB",
            "52428800, 50 MB"
    })
    void testValidLongInputsForByteFormatting(long input, String expected) {
        assertEquals(expected, Util.readableFileSize(input));
    }

    @ParameterizedTest
    @DisplayName("Tests valid double values for readable Byte-Formatting")
    @CsvSource({
            "8,        8 B",
            "2048,     2 kB",
            "52428800, 50 MB"
    })
    void testValidDoubleInputsForByteFormatting(double input, String expected) {
        assertEquals(expected, Util.readableFileSize(input));
    }
}
