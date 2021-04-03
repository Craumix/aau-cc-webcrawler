import crawler.CrawlerLoadFilter;
import crawler.Webpage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebpageTest {

    @Test
    void testLoadChildren() {

    }

    @Test
    void testPrintWithChildren() {

    }

    @Test
    void testPageTitle() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_100_words";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals("Word Count - 100 Words", webpage.getPageTitle());
    }

    @Test
    void testPageTitleEmpty() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/titles/empty_title";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals("", webpage.getPageTitle());
    }

    @Test
    void testPageTitleMissing() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/titles/missing_title";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals("", webpage.getPageTitle());
    }


    @Test
    void testWordCount() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_100_words";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals(100, webpage.getWordCount());
    }

    @Test
    void testWordCountWordWithHyphen() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_hyphenated";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals(1, webpage.getWordCount());
    }

    @Test
    void testWordCountNumber() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_number";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals(1, webpage.getWordCount());
    }

    @Test
    void testWordCountSymbols() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/word_count_symbols";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals(8, webpage.getWordCount());
    }
}
