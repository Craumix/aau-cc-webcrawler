import crawler.Webpage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebpageTest {

    @Test
    void testLoadChildren() {

    }

    @Test
    void testPageHashString() throws URISyntaxException {
        String testWebsite = "http://example.org/";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        assertEquals("05D8617380C30F4A70CE0D4088D54D2B", webpage.getPageHashString());
    }

    @Test
    void testMaxChildrenPerPage() {
        int expectedResult = 58;
        Webpage.setMaxChildrenPerPage(expectedResult);

        assertEquals(expectedResult, Webpage.getMaxChildrenPerPage());
    }

    @Test
    void testUserAgent() {
        String expectedResult = "abc";
        Webpage.setRequestUserAgent(expectedResult);

        assertEquals(expectedResult, Webpage.getUserAgent());
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

    @Test
    @DisplayName("Test if children stop being added when the limit is hit")
    void testMaxLinksPerPage() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/links/page_with_external_links";
        Webpage webpage = new Webpage(testWebsite);
        Webpage.setMaxChildrenPerPage(1);
        webpage.loadPage();
        for (Webpage child : webpage.getChildren())
            child.loadPage();

        JSONObject resultingJSON = webpage.asJSONObject();
        resultingJSON.remove("nanoLoadTime");
        resultingJSON.remove("pageHash");
        resultingJSON.remove("pageSize");

        JSONArray resultingJSONArray = resultingJSON.getJSONArray("children");
        for (int i=0; i<resultingJSONArray.length(); i++) {
            resultingJSONArray.getJSONObject(i).remove("nanoLoadTime");
            resultingJSONArray.getJSONObject(i).remove("pageHash");
            resultingJSONArray.getJSONObject(i).remove("pageSize");
        }

        String actualResult = resultingJSON.toString();
        String expectedResult = "{\"url\":\"https://crawler-test.com/links/page_with_external_links\",\"title\":\"Page with External Links\",\"linkCount\":6,\"imageCount\":0,\"videoCount\":0,\"wordCount\":25,\"children\":[{\"url\":\"https://crawler-test.com/\",\"title\":\"Crawler Test Site\",\"linkCount\":413,\"imageCount\":0,\"videoCount\":0,\"wordCount\":1539}]}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test if the JSONObject derived from a Webpage without links is as expected (excluding nanoLoadTime)")
    void testJSONtoStringWithoutChildren() throws URISyntaxException {
        String testWebsite = "https://example.org";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        JSONObject resultingJSON = webpage.asJSONObject();
        resultingJSON.remove("nanoLoadTime");

        String actualResult = resultingJSON.toString();
        String expectedResult = "{\"url\":\"https://example.org\",\"title\":\"Example Domain\",\"linkCount\":1,\"imageCount\":0,\"videoCount\":0,\"wordCount\":28,\"pageSize\":1249,\"pageHash\":\"05D8617380C30F4A70CE0D4088D54D2B\"}";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test if the JSONObject derived from a Webpage is as expected (excluding nanoLoadTime)")
    void testJSONtoString() throws URISyntaxException {
        String testWebsite = "https://crawler-test.com/links/page_with_external_links";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        for (Webpage child : webpage.getChildren())
            child.loadPage();

        JSONObject resultingJSON = webpage.asJSONObject();
        resultingJSON.remove("nanoLoadTime");
        resultingJSON.remove("pageHash");
        resultingJSON.remove("pageSize");

        JSONArray resultingJSONArray = resultingJSON.getJSONArray("children");
        for (int i=0; i<resultingJSONArray.length(); i++) {
            resultingJSONArray.getJSONObject(i).remove("nanoLoadTime");
            resultingJSONArray.getJSONObject(i).remove("pageHash");
            resultingJSONArray.getJSONObject(i).remove("pageSize");
        }


        String actualResult = resultingJSON.toString();
        String expectedResult = "{\"url\":\"https://crawler-test.com/links/page_with_external_links\",\"title\":\"Page with External Links\",\"linkCount\":6,\"imageCount\":0,\"videoCount\":0,\"wordCount\":25,\"children\":[{\"url\":\"https://crawler-test.com/\",\"title\":\"Crawler Test Site\",\"linkCount\":413,\"imageCount\":0,\"videoCount\":0,\"wordCount\":1539},{\"url\":\"http://robotto.org\",\"title\":\"Robotto | Domain Monitoring, SEO Alerts & Portfolio Domain Management\",\"linkCount\":17,\"imageCount\":1,\"videoCount\":0,\"wordCount\":402},{\"url\":\"http://semetrical.com\",\"title\":\"Digital Marketing Agency London | Global Solutions | Semetrical\",\"linkCount\":121,\"imageCount\":27,\"videoCount\":0,\"wordCount\":650},{\"url\":\"http://deepcrawl.co.uk\",\"title\":\"DeepCrawl | The #1 Technical SEO Platform\",\"linkCount\":64,\"imageCount\":54,\"videoCount\":0,\"wordCount\":817},{\"url\":\"http://robotto.org\",\"title\":\"Robotto | Domain Monitoring, SEO Alerts & Portfolio Domain Management\",\"linkCount\":17,\"imageCount\":1,\"videoCount\":0,\"wordCount\":402},{\"url\":\"http://robotto.org\",\"title\":\"Robotto | Domain Monitoring, SEO Alerts & Portfolio Domain Management\",\"linkCount\":17,\"imageCount\":1,\"videoCount\":0,\"wordCount\":402}]}";
        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test if the JSONObject correctly errors out when the website doesn't exist")
    void testJSONtoStringError() throws URISyntaxException {
        String testWebsite = "https://e.a";
        Webpage webpage = new Webpage(testWebsite);

        webpage.loadPage();
        JSONObject resultingJSON = webpage.asJSONObject();
        resultingJSON.remove("nanoLoadTime");

        String actualResult = resultingJSON.toString();
        String expectedResult = "{\"url\":\"https://e.a\",\"error\":\"e.a\"}";

        assertEquals(expectedResult, actualResult);
    }

    @AfterEach
    void cleanUp() {
        Webpage.setMaxChildrenPerPage(Integer.MAX_VALUE);
    }
}
