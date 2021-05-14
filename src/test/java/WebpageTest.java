import connector.LocalFileFetcher;
import crawler.webpage.Webpage;
import crawler.webpage.filter.WebpageLoadFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebpageTest {

    @BeforeEach
    void setup() {
        Webpage.setConnector(new LocalFileFetcher());
        Webpage.setMaxChildrenPerPage(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("Test whether the generation of the page-hash works correctly")
    void testPageHashString() throws URISyntaxException {
        Webpage webpage = new Webpage("55-words");

        webpage.loadPage();

        assertEquals("FF78D1AB741350018A5290629C7AB3FE", webpage.getPageHashString());
    }

    @Test
    @DisplayName("Test if maxChildren can be set")
    void testMaxChildrenPerPage() {
        int expectedResult = 58;

        Webpage.setMaxChildrenPerPage(expectedResult);

        assertEquals(expectedResult, Webpage.getMaxChildrenPerPage());
    }

    @Test
    @DisplayName("Test if a user agent can be set")
    void testUserAgent() {
        String expectedResult = "abc";

        Webpage.setRequestUserAgent(expectedResult);

        assertEquals(expectedResult, Webpage.getUserAgent());
    }

    @Test
    @DisplayName("Test if image links in a page get loaded correctly")
    void testImages() throws URISyntaxException {
        Webpage webpage = new Webpage("4-images");

        webpage.loadPage();

        String actualResult = webpage.getImages().toString();
        String expectedResult =
                "<img src=\"image.png\">\n" +
                "<img src=\"anotherImage.jpeg\">\n" +
                "<img src=\"wowAnImage.gif\">\n" +
                "<img src=\"justAnImage.svg\">";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test if links in a page get loaded correctly")
    void testLinks() throws URISyntaxException {
        Webpage webpage = new Webpage("4-links");

        webpage.loadPage();

        String actualResult = webpage.getLinks().toString();
        String expectedResult =
                "<a href=\"http://link1.com\">link1</a>\n" +
                "<a href=\"http://link2.at\">link2</a>\n" +
                "<a href=\"https://links.link3.de\">link3</a>\n" +
                "<a href=\"https://link4.net\">link4</a>";

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test if the page size gets calculated correctly")
    void testPageSize() throws URISyntaxException {
        Webpage webpage = new Webpage("big-pagesize");

        webpage.loadPage();

        assertEquals(50002, webpage.getPageSize());
    }

    @Test
    @DisplayName("Test if the page title gets set correctly")
    void testPageTitle() throws URISyntaxException {
        Webpage webpage = new Webpage("55-words");

        webpage.loadPage();

        assertEquals("55 Words", webpage.getPageTitle());
    }

    @Test
    @DisplayName("Test an empty page title gets set correctly")
    void testPageTitleEmpty() throws URISyntaxException {
        Webpage webpage = new Webpage("no-pagetitle");

        webpage.loadPage();

        assertEquals("", webpage.getPageTitle());
    }

    @Test
    @DisplayName("Test if a missing page title gets set correctly")
    void testPageTitleMissing() throws URISyntaxException {
        Webpage webpage = new Webpage("pagetitle-missing");

        webpage.loadPage();

        assertEquals("", webpage.getPageTitle());
    }

    @Test
    @DisplayName("Test if it counts words correctly")
    void testWordCount() throws URISyntaxException {
        Webpage webpage = new Webpage("55-words");

        webpage.loadPage();

        assertEquals(55, webpage.getWordCount());
    }

    @Test
    @DisplayName("Test if it counts words correctly with hyphens involved")
    void testWordCountWordWithHyphen() throws URISyntaxException {
        Webpage webpage = new Webpage("55-words-hyphen");

        webpage.loadPage();

        assertEquals(55, webpage.getWordCount());
    }

    @Test
    @DisplayName("Test if it counts words correctly with numbers involved")
    void testWordCountNumber() throws URISyntaxException {
        Webpage webpage = new Webpage("55-words-numbers");

        webpage.loadPage();

        assertEquals(55, webpage.getWordCount());
    }

    @Test
    @DisplayName("Test if it counts words correctly with symbols involved")
    void testWordCountSymbols() throws URISyntaxException {
        Webpage webpage = new Webpage("10-symbols");

        webpage.loadPage();

        assertEquals(10, webpage.getWordCount());
    }

    @Test
    @DisplayName("Test if children stop being added when the limit is hit")
    void testMaxLinksPerPage() throws URISyntaxException {
        Webpage webpage = new Webpage("4-links");

        Webpage.setMaxChildrenPerPage(2);
        webpage.loadPage();

        assertEquals(2, webpage.getChildren().size());
    }

    @Test
    @DisplayName("Test if the JSONObject derived from a Webpage without links is as expected (excluding nanoLoadTime)")
    void testJSONtoStringWithoutChildren() throws URISyntaxException {
        Webpage webpage = new Webpage("55-words");

        webpage.loadPage();

        String expectedResult = "{\"url\":\"55-words\",\"title\":\"55 Words\",\"linkCount\":0,\"imageCount\":0,\"videoCount\":0,\"wordCount\":55,\"pageSize\":497,\"pageHash\":\"FF78D1AB741350018A5290629C7AB3FE\"}";

        assertEquals(expectedResult, removeNanoLoadTimes(webpage));
    }

    @Test
    @DisplayName("Test if the JSONObject derived from a Webpage is as expected (excluding nanoLoadTime)")
    void testJSONtoString() throws URISyntaxException {
        Webpage webpage = new Webpage("3-children");

        webpage.loadPage();
        for (Webpage child : webpage.getChildren())
            child.loadPage();

        String expectedResult = "{\"url\":\"3-children\",\"title\":\"3 Children\",\"linkCount\":3,\"imageCount\":0,\"videoCount\":0,\"wordCount\":6,\"pageSize\":267,\"pageHash\":\"DFA0C256847AEB2582B67E7862CD35A7\",\"children\":[{\"url\":\"https://55-words.test\",\"title\":\"55 Words\",\"linkCount\":0,\"imageCount\":0,\"videoCount\":0,\"wordCount\":55,\"pageSize\":497,\"pageHash\":\"FF78D1AB741350018A5290629C7AB3FE\"},{\"url\":\"https://4-images.test\",\"title\":\"4 Images\",\"linkCount\":0,\"imageCount\":4,\"videoCount\":0,\"wordCount\":1,\"pageSize\":279,\"pageHash\":\"CA08A90CAE6845180C4BFEA6DD0D2AB1\"},{\"url\":\"https://4-links.test\",\"title\":\"4 Links\",\"linkCount\":4,\"imageCount\":0,\"videoCount\":0,\"wordCount\":4,\"pageSize\":285,\"pageHash\":\"8C7615B9B21B588359A8F0909BC469E0\"}]}";

        assertEquals(expectedResult, removeNanoLoadTimes(webpage));
    }

    @Test
    @DisplayName("Test if the JSONObject correctly errors out when the website doesn't exist")
    void testJSONtoStringError() throws URISyntaxException {
        Webpage webpage = new Webpage("https://a.error");

        webpage.loadPage();

        String expectedResult = "{\"url\":\"https://a.error\",\"error\":\"a.error\"}";

        assertEquals(expectedResult, removeNanoLoadTimes(webpage));
    }

    @Test
    @DisplayName("Test if the children don't get added when the load filter return false")
    void testLoadFilter() throws URISyntaxException {
        class DummyFilter implements WebpageLoadFilter {
            @Override
            public boolean webpageShouldBeLoaded(URI webpageURI) {
                try {
                    return webpageURI.equals(new URI("3-children")) || webpageURI.equals(new URI("https://4-links.test"));
                } catch (Exception e) {
                    return false;
                }
            }
        }

        ArrayList<WebpageLoadFilter> dummyFilterList = new ArrayList<>();
        dummyFilterList.add(new DummyFilter());

        Webpage webpage = new Webpage("3-children", dummyFilterList);

        webpage.loadPage();
        for (Webpage child : webpage.getChildren())
            child.loadPage();

        String expectedResult = "{\"url\":\"3-children\",\"title\":\"3 Children\",\"linkCount\":3,\"imageCount\":0,\"videoCount\":0,\"wordCount\":6,\"pageSize\":267,\"pageHash\":\"DFA0C256847AEB2582B67E7862CD35A7\",\"children\":[{\"url\":\"https://4-links.test\",\"title\":\"4 Links\",\"linkCount\":4,\"imageCount\":0,\"videoCount\":0,\"wordCount\":4,\"pageSize\":285,\"pageHash\":\"8C7615B9B21B588359A8F0909BC469E0\"}]}";

        assertEquals(expectedResult, removeNanoLoadTimes(webpage));
    }


    /**
     * Removes nanoLoadTime from a webpage and its children
     * @param webpage Webpage to remove thr load times from
     * @return the webpage without nanoLoadTime as a String
     */
    private String removeNanoLoadTimes(Webpage webpage) {
        JSONObject resultingJSON = webpage.asJSONObject();
        resultingJSON.remove("nanoLoadTime");
        try {
            JSONArray resultingJSONArray = resultingJSON.getJSONArray("children");
            for (int i=0; i<resultingJSONArray.length(); i++)
                resultingJSONArray.getJSONObject(i).remove("nanoLoadTime");
        } catch (JSONException ignored) { }

        return resultingJSON.toString();
    }

}
