import com.github.stefanbirkner.systemlambda.SystemLambda;
import crawler.Main;
import crawler.webpage.Webpage;
import crawler.webpage.filter.DuplicateLoadFilter;
import crawler.webpage.filter.RobotsLoadFilter;
import mocks.DummyParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private DummyParser parser;
    private ArrayList<String> urls;

    @BeforeEach
    void setDummyParser() {
        urls = new ArrayList<>();
        urls.add("https://55-words");

        parser = new DummyParser();
        Main.parser = parser;
        Main.rootPages = new ArrayList<>();
    }

    @ParameterizedTest
    @DisplayName("Test if the eXit codes are right for any combination of parse failure and help requested")
    @CsvSource({
            "false, false, 1",
            "false, true,  0",
            "true,  true,  0"
    })
    void testExitCode(boolean parseSuccess, boolean helpRequested, int expectedExitCode) throws Exception {
        parser.setParseSuccess(parseSuccess);
        parser.setHelpRequested(helpRequested);

        int code = SystemLambda.catchSystemExit(() -> {
            Main.main(new String[0]);
        });

        assertEquals(expectedExitCode, code);
    }

    @Test
    @DisplayName("Test help printing")
    void testHelpOutput() throws Exception {
        parser.setHelpRequested(true);
        parser.setHelpDialog("AHelpDialog");

        String out = SystemLambda.tapSystemOut(() -> {
            Main.checkHelp();
        });
        assertEquals("AHelpDialog\n", out);
    }

    @Test
    @DisplayName("Test filename which needs no recommendation")
    void testFilenameNoRecommendation() throws Exception {
        parser.setOutputFile("file.json");
        String out = SystemLambda.tapSystemOut(() -> {
            Main.printWarningIfFileIsntJSON();
        });
        assertEquals("", out);
    }

    @ParameterizedTest
    @DisplayName("Test that filename recommendations")
    @CsvSource({
            "file, file.json",
            "file.txt, file.json",
            "file.txt.txt, file.txt.json"
    })
    void testOutputFileRecommendations(String original,  String recommendation) throws Exception {
        parser.setOutputFile(original);
        String out = SystemLambda.tapSystemOut(() -> {
            Main.printWarningIfFileIsntJSON();
        });
        assertEquals("The output format is JSON consider using " + recommendation + " as a filename", out);
    }

    @Test
    @DisplayName("Test root pages init")
    void testRootPageInit() {
        parser.setParseSuccess(true);
        parser.setRootUrls(urls);

        Main.initializeRootPages();

        assertNotEquals(0, Main.rootPages.size());
    }

    @Test
    @DisplayName("Test if the robots gets set correctly")
    void testRobotsFilter() {
        parser.setParseSuccess(true);
        parser.setRespectRobotsTxt(true);
        parser.setRootUrls(urls);

        Main.initializeRootPages();
        Webpage page = Main.rootPages.get(0);

        assertEquals(RobotsLoadFilter.class, page.getLoadFilters().get(0).getClass());
    }

    @Test
    @DisplayName("Test if the duplicate gets set correctly")
    void testDuplicateFilter() {
        parser.setParseSuccess(true);
        parser.setOmitDuplicates(true);
        parser.setRootUrls(urls);

        Main.initializeRootPages();
        Webpage page = Main.rootPages.get(0);

        assertEquals(DuplicateLoadFilter.class, page.getLoadFilters().get(0).getClass());
    }

    @Test
    @DisplayName("Test if the no filter gets set correctly")
    void testNoFilter() {
        parser.setParseSuccess(true);
        parser.setRootUrls(urls);

        Main.initializeRootPages();
        Webpage page = Main.rootPages.get(0);

        assertEquals(0, page.getLoadFilters().size());
    }

    @Test
    @DisplayName("Test if the spoofed browser agent gets set correctly")
    void testSpoofBrowser() {
        parser.setParseSuccess(true);
        parser.setSpoofBrowser(true);
        parser.setRootUrls(urls);

        Main.initializeRootPages();

        assertEquals(Main.BROWSER_USER_AGENT, Main.rootPages.get(0).getUserAgent());
    }
}
