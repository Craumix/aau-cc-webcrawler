import crawler.argumentparser.OptionsArgumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OptionsArgumentParserTest {

    OptionsArgumentParser parser;
    ArrayList<String> defaultArgs;

    @BeforeEach
    void setup() {
        parser = new OptionsArgumentParser();

        defaultArgs = new ArrayList<>();
        defaultArgs.add("-u");
        defaultArgs.add("https://website.com");
    }

    @Test
    @DisplayName("Test if setting a thread count works")
    void testThreadCount() {
        defaultArgs.add("-t");
        defaultArgs.add("10");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertEquals(10, parser.getThreadCount());
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when the thread count is set too big")
    void testThreadCountBiggerThanMax() {
        defaultArgs.add("-t");
        defaultArgs.add("10000");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when the thread count is set to a negative number")
    void testThreadCountNegative() {
        defaultArgs.add("-t");
        defaultArgs.add("-10");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when the thread count is set to 0")
    void testThreadCountZero() {
        defaultArgs.add("-t");
        defaultArgs.add("0");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when thread count has no argument")
    void testThreadCountNoArgument() {
        defaultArgs.add("-t");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if the error message is correct when thread count has no argument")
    void testThreadCountNoArgumentErrorMessage() {
        defaultArgs.add("-t");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));

        assertEquals("Missing argument for option: t", parser.getErrorMessage());
    }

    @Test
    @DisplayName("Test if setting max depth works")
    void testMaxDepth() {
        defaultArgs.add("-d");
        defaultArgs.add("5");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertEquals(5, parser.getMaxDepth());
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when max depth is set too big")
    void testMaxDepthBiggerThanMax() {
        defaultArgs.add("-d");
        defaultArgs.add("50");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when max depth is set to a negative number")
    void testMaxDepthNegative() {
        defaultArgs.add("-d");
        defaultArgs.add("-50");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when max depth is set to 0")
    void testMaxDepthZero() {
        defaultArgs.add("-d");
        defaultArgs.add("0");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when max depth has no argument")
    void testMaxDepthNoArgument() {
        defaultArgs.add("-d");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if the error message is correct when max depth has no argument")
    void testMaxDepthNoArgumentErrorMessage() {
        defaultArgs.add("-d");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));

        assertEquals("Missing argument for option: d", parser.getErrorMessage());
    }

    @Test
    @DisplayName("Test if setting max links works")
    void testMaxLinks() {
        defaultArgs.add("-l");
        defaultArgs.add("5");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertEquals(5, parser.getMaxLinksPerPage());
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when max links is set to a negative number")
    void testMaxLinksNegative() {
        defaultArgs.add("-l");
        defaultArgs.add("-5");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when max links is set to 0")
    void testMaxLinksZero() {
        defaultArgs.add("-l");
        defaultArgs.add("0");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when max links has no argument")
    void testMaxLinksNoArgument() {
        defaultArgs.add("-l");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if the error message is correct when max links has no argument")
    void testMaxLinksNoArgumentErrorMessage() {
        defaultArgs.add("-l");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));

        assertEquals("Missing argument for option: l", parser.getErrorMessage());
    }

    @Test
    @DisplayName("Test if setting a single url works")
    void testSingleUrl() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-u");
        args.add("https://website.com");

        parser.parseArgs(toArray(args));

        args.remove("-u");

        assertEquals(args, parser.getRootUrls());
    }

    @Test
    @DisplayName("Test if setting multiple urls works")
    void testMultipleUrls() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-u");
        args.add("https://website.com,https://internet.com");

        parser.parseArgs(toArray(args));

        args = new ArrayList<>();
        args.add("https://website.com");
        args.add("https://internet.com");

        assertEquals(args, parser.getRootUrls());
    }

    @Test
    @DisplayName("Test if the https:// scheme gets added when no scheme is found")
    void testUrlSchemaAdding() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-u");
        args.add("website.com");

        parser.parseArgs(toArray(args));

        args = new ArrayList<>();
        args.add("https://website.com");

        assertEquals(args, parser.getRootUrls());
    }

    @Test
    @DisplayName("Test if a url gets rejected when is not valid")
    void testUrlRejection() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-u");
        args.add("https://website");

        assertFalse(parser.parseArgs(toArray(args)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when no url is given")
    void testNoUrl() {
        ArrayList<String> args = new ArrayList<>();

        assertFalse(parser.parseArgs(toArray(args)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when urls has no argument")
    void testUrlNoArgument() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-u");

        assertFalse(parser.parseArgs(toArray(args)));
    }

    @Test
    @DisplayName("Test if the error message is correct when urls has no argument")
    void testUrlNoArgumentErrorMessage() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-u");

        assertFalse(parser.parseArgs(toArray(args)));

        assertEquals("Missing argument for option: u", parser.getErrorMessage());
    }

    @Test
    @DisplayName("Test if the scheme adding warning gets added")
    void testUrlWarning() {
        ArrayList<String> args = new ArrayList<>();
        args.add("-u");
        args.add("website.com");

        parser.parseArgs(toArray(args));

        String expectedResult = "No URL scheme given, assuming https://website.com\n";

        assertEquals(expectedResult, parser.getWarnings());
    }

    @Test
    @DisplayName("Test if the help flag gets set correctly")
    void testHelp() {
        defaultArgs.add("-h");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertTrue(parser.helpRequested());
    }

    @Test
    @DisplayName("Test if the helpRequested() is false when the help flag is not set")
    void testHelpNotSet() {
        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertFalse(parser.helpRequested());
    }

    @Test
    @DisplayName("Test if the helpRequested() is false when the arguments weren't parsed yet")
    void testHelpWithoutParsing() {
        assertFalse(parser.helpRequested());
    }

    @Test
    @DisplayName("Test if the omit-duplicates flag gets set correctly")
    void testOmitDuplicates() {
        defaultArgs.add("-s");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertTrue(parser.omitDuplicates());
    }

    @Test
    @DisplayName("Test if the omitDuplicates() is false when the omit-duplicates flag is not set")
    void testOmitDuplicatesNotSet() {
        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertFalse(parser.omitDuplicates());
    }

    @Test
    @DisplayName("Test if the omitDuplicates() is false when the arguments weren't parsed yet")
    void testOmitDuplicatesWithoutParsing() {
        assertFalse(parser.helpRequested());
    }

    @Test
    @DisplayName("Test if the spoof browser flag gets set correctly")
    void testSpoofBrowser() {
        defaultArgs.add("-b");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertTrue(parser.spoofBrowser());
    }

    @Test
    @DisplayName("Test if the spoofBrowser() is false when the spoof browser flag is not set")
    void testSpoofBrowseNotSet() {
        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertFalse(parser.spoofBrowser());
    }

    @Test
    @DisplayName("Test if the spoofBrowser() is false when the arguments weren't parsed yet")
    void testSpoofBrowseWithoutParsing() {
        assertFalse(parser.spoofBrowser());
    }

    @Test
    @DisplayName("Test if the ignore robots flag gets set correctly")
    void testIgnoreRobots() {
        defaultArgs.add("-r");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertFalse(parser.respectRobotsTxt());
    }

    @Test
    @DisplayName("Test if the respectRobotsTxt() is true when the ignore robots flag is not set")
    void testIgnoreRobotsNotSet() {
        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertTrue(parser.respectRobotsTxt());
    }

    @Test
    @DisplayName("Test if the respectRobotsTxt() is true when the arguments weren't parsed yet")
    void testIgnoreRobotsWithoutParsing() {
        assertTrue(parser.respectRobotsTxt());
    }

    @Test
    @DisplayName("Test if setting a output file works")
    void testOutputFile() {
        defaultArgs.add("-o");
        defaultArgs.add("file.txt");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertEquals("file.txt", parser.getOutputFile());
    }

    @Test
    @DisplayName("Test if an empty String gets returned when the output file is not set")
    void testOutputFileNotSet() {
        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertEquals("", parser.getOutputFile());
    }

    @Test
    @DisplayName("Test if parseArgs() returns false output file doesn't have an argument")
    void testOutputFileNoArgument() {
        defaultArgs.add("-o");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @Test
    @DisplayName("Test if the error message is correct when output file has no argument")
    void testOutputFileNoArgumentErrorMessage() {
        defaultArgs.add("-o");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));

        assertEquals("Missing argument for option: o", parser.getErrorMessage());
    }

    @Test
    @DisplayName("Test if outputIntoFile() is true when an output is set")
    void testOutputIntoFile() {
        defaultArgs.add("-o");
        defaultArgs.add("file.txt");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertTrue(parser.outputIntoFile());
    }

    @Test
    @DisplayName("Test if the outputIntoFile() is false when the omit-duplicates flag is not set")
    void testOutputIntoFileNotSet() {
        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertFalse(parser.outputIntoFile());
    }

    @Test
    @DisplayName("Test if the outputIntoFile() is false when the arguments weren't parsed yet")
    void testOutputIntoFileWithoutParsing() {
        assertFalse(parser.spoofBrowser());
    }
    
    @Test
    @DisplayName("Test if errorMessage is an empty String when no error has happened")
    void testErrorMessageNoError() {
        assertEquals("", parser.getErrorMessage());
    }




    @Test
    @DisplayName("Test if the help dialog is correct")
    void testHelpDialog() {
        String expectedResult =
                """
                        usage: Webcrawler [-b] [-d <arg>] [-h] [-l <arg>] [-o <arg>] [-r] [-s] [-t
                               <arg>] [-u <arg>]
                         -b,--spoof-browser        If set, spoofs the UserAgent (in case some
                                                   sites block the default UserAgent)
                         -d,--max-depth <arg>      Specify the recursion depth for following
                                                   links. Default: 2, Range 1-10
                         -h,--help                 Open the help dialog
                         -l,--max-links <arg>      Max amount of links to follow per page.
                                                   Default: 100, Range: 1-inf
                         -o,--output <arg>         Specify a Output File as alternative to stdout
                         -r,--ignore-robots-txt    If set, ignores robots.txt
                         -s,--omit-duplicates      If set, omits duplicate pages
                         -t,--thread-count <arg>   Amount of threads to use, will increase CPU and
                                                   Memory consumption. Default: 2, Range 1-1024
                         -u,--urls <arg>           Specify the root urls for the crawler. Multiple
                                                   urls must be comma separated
                        """;

        assertEquals(expectedResult, parser.getHelpDialog());
    }


    String[] toArray(ArrayList<String> list) {
        int i=0;
        String[] result = new String[list.size()];
        for (String string : list)
            result[i++] = string;

        return result;
    }

}
