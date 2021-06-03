import com.github.stefanbirkner.systemlambda.SystemLambda;
import crawler.Main;
import crawler.argumentparser.ArgumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private DummyParser parser;

    @BeforeEach
    void setDummyParser() {
        parser = new DummyParser();
        Main.parser = parser;
    }

    @Test
    @DisplayName("Test main exit code without params")
    void testNoParamsExitCode() throws Exception {
        parser.setParseSuccess(false);

        int code = SystemLambda.catchSystemExit(() -> {
            Main.main(new String[0]);
        });

        assertEquals(1, code);
    }

    @Test
    @DisplayName("Test main exit code for help")
    void testHelpExitCode() throws Exception {
        parser.setParseSuccess(true);
        parser.setHelpRequested(true);

        int code = SystemLambda.catchSystemExit(() -> {
            Main.main(new String[0]);
        });

        assertEquals(0, code);
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

        parser.setHelpRequested(false);
        out = SystemLambda.tapSystemOut(() -> {
            Main.checkHelp();
        });
        assertEquals("", out);
    }

    @Test
    @DisplayName("Test that filename recommendations")
    void testOutputFileRecommendations() throws Exception {
        parser.setOutputFile("file.json");
        String out = SystemLambda.tapSystemOut(() -> {
            Main.printWarningIfFileIsntJSON();
        });
        assertEquals("", out);

        parser.setOutputFile("file");
        out = SystemLambda.tapSystemOut(() -> {
            Main.printWarningIfFileIsntJSON();
        });
        assertEquals("The output format is JSON consider using file.json as a filename", out);

        parser.setOutputFile("file.txt");
        out = SystemLambda.tapSystemOut(() -> {
            Main.printWarningIfFileIsntJSON();
        });
        assertEquals("The output format is JSON consider using file.json as a filename", out);

        parser.setOutputFile("file.txt.txt");
        out = SystemLambda.tapSystemOut(() -> {
            Main.printWarningIfFileIsntJSON();
        });
        assertEquals("The output format is JSON consider using file.txt.json as a filename", out);
    }

    @Test
    @DisplayName("Test root pages init")
    void testRootPageInit() {
        parser.setSpoofBrowser(true);
    }
}
