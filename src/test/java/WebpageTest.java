import crawler.Webpage;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Element;
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
    void testImages() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/links/image_links";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();

        String actualResult = webpage.getImages().toString();
        String expectedResult =
                "<img src=\"/image_link.png\" alt=\"Image alt tag that is not empty\">\n" +
                "<img src=\"/image_link.png\" alt=\"\">\n" +
                "<img src=\"/image_link.png\">\n" +
                "<img src=\"/image_link.png\">";

        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testLinks() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/links/page_with_external_links";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();

        String actualResult = webpage.getLinks().toString();
        String expectedResult =
                "<a href=\"/\" id=\"logo\">Crawler Test <span class=\"neon-effect\">two point oh!</span></a>\n" +
                "<a href=\"http://robotto.org\">External Link 1</a>\n" +
                "<a href=\"http://semetrical.com\">External Link 2</a>\n" +
                "<a href=\"http://deepcrawl.co.uk\">External Link 3</a>\n" +
                "<a href=\"http://robotto.org\">Repeated External Link</a>\n" +
                "<a href=\"http://robotto.org\">Repeated External Link</a>";

        assertEquals(expectedResult, actualResult);
    }


    @Test
    void testPageSize() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/content/page_html_size/5";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals(webpage.getPageSize(), 9057);

        testWebsite = "https://crawler-test.com/content/page_html_size/3080";
        webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals(webpage.getPageSize(), 3048962);
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
