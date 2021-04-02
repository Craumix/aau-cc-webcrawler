import crawler.CrawlerLoadFilter;
import crawler.Webpage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebpageTest {

    CrawlerLoadFilter loadFilterFalseFalse;

    @BeforeEach
    void setup() {
        loadFilterFalseFalse = new CrawlerLoadFilter(false,false);
    }

    @Test
    void testLoadChildren() {

    }

    @Test
    void testPrintWithChildren() {

    }

    @Test
    void testWordCount() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_100_words";
        Webpage webpage = new Webpage(testWebsite, loadFilterFalseFalse);

        webpage.loadPage();
        assertEquals(webpage.getWordCount(), 100);
    }

    @Test
    void testWordCountWordWithHyphen() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_hyphenated";
        Webpage webpage = new Webpage(testWebsite, loadFilterFalseFalse);

        webpage.loadPage();
        assertEquals(webpage.getWordCount(), 1);
    }

    @Test
    void testWordCountNumber() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_number";
        Webpage webpage = new Webpage(testWebsite, loadFilterFalseFalse);

        webpage.loadPage();
        assertEquals(webpage.getWordCount(), 1);
    }

    @Test
    void testWordCountSymbols() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_symbols";
        Webpage webpage = new Webpage(testWebsite, loadFilterFalseFalse);

        webpage.loadPage();
        assertEquals(webpage.getWordCount(), 8);
    }
}
