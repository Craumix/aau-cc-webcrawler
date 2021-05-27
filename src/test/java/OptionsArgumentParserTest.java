import crawler.argumentparser.OptionsArgumentParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OptionsArgumentParserTest {

    OptionsArgumentParser parser;
    ArrayList<String> defaultArgs, customArgs;

    @BeforeEach
    void setup() {
        parser = new OptionsArgumentParser();

        defaultArgs = new ArrayList<>();
        defaultArgs.add("-u");
        defaultArgs.add("https://website.com");

        customArgs = new ArrayList<>();
    }

    @ParameterizedTest
    @DisplayName("Test if the error message for missing arguments is correct")
    @ValueSource(strings={"t", "d", "l", "o"})
    void testErrorMessagesMissingArgumentForOption(String option) {
        defaultArgs.add("-" + option);

        parser.parseArgs(toArray(defaultArgs));

        assertEquals("Missing argument for option: " + option, parser.getErrorMessage());
    }

    @ParameterizedTest
    @DisplayName("Test if parseArgs() returns false when no argument is given")
    @ValueSource(strings={"t", "d", "l", "o"})
    void testNoArgumentForOption(String option) {
        defaultArgs.add("-" + option);

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }
    @ParameterizedTest
    @DisplayName("Test if parseArgs() returns false when the argument is too big")
    @CsvSource({
            "t, 10000",
            "d, 50"
    })

    void testBiggerThanMaxForOption(String option, String arg) {
        defaultArgs.add("-" + option);
        defaultArgs.add(arg);

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @ParameterizedTest
    @DisplayName("Test if parseArgs() returns false when the argument is negative")
    @ValueSource(strings={"t", "d", "l"})
    void testNegativeArgumentForOption(String option) {
        defaultArgs.add("-" + option);
        defaultArgs.add("-10");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @ParameterizedTest
    @DisplayName("Test if parseArgs() returns false when the argument is 0")
    @ValueSource(strings={"t", "d", "l"})
    void testZeroForOption(String option) {
        defaultArgs.add("-" + option);
        defaultArgs.add("0");

        assertFalse(parser.parseArgs(toArray(defaultArgs)));
    }

    @ParameterizedTest
    @DisplayName("Test if setting a argument for an option works")
    @CsvSource({
            "t, 10, getThreadCount",
            "d, 5,  getMaxDepth",
            "l, 5,  getMaxLinksPerPage"
    })
    void testSettingArgumentForOption(String option, int arg, String method) throws Exception {
        defaultArgs.add("-" + option);
        defaultArgs.add(String.valueOf(arg));

        parser.parseArgs(toArray(defaultArgs));

        Method actualMethod = Class.forName(parser.getClass().getName()).getDeclaredMethod(method);
        int actualResult = (int) actualMethod.invoke(parser);

        assertEquals(arg, actualResult);
    }

    @ParameterizedTest
    @DisplayName("Test if boolean methods have the correct default value")
    @CsvSource({
            "helpRequested,    false",
            "spoofBrowser,     false",
            "respectRobotsTxt, true",
            "omitDuplicates,   false",
            "outputIntoFile,   false"
    })
    void testDefaultValueBooleanMethods(String method, boolean expectedResult) throws Exception {

        Method actualMethod = Class.forName(parser.getClass().getName()).getDeclaredMethod(method);
        boolean actualResult = (boolean) actualMethod.invoke(parser);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @DisplayName("Test if boolean methods have the correct value when not set but parsed")
    @CsvSource({
            "helpRequested,    false",
            "spoofBrowser,     false",
            "respectRobotsTxt, true",
            "omitDuplicates,   false",
            "outputIntoFile,   false"
    })
    void testDefaultValueAfterParsingBooleanMethods(String method, boolean expectedResult) throws Exception {
        parser.parseArgs(toArray(defaultArgs));

        Method actualMethod = Class.forName(parser.getClass().getName()).getDeclaredMethod(method);
        boolean actualResult = (boolean) actualMethod.invoke(parser);

        assertEquals(expectedResult, actualResult);
    }

    @ParameterizedTest
    @DisplayName("Test if boolean methods have the correct value when their respective flag is set")
    @CsvSource({
            "h, helpRequested,    true",
            "b, spoofBrowser,     true",
            "r, respectRobotsTxt, false",
            "s, omitDuplicates,   true"
    })
    void testSetBooleanMethods(String option, String method, boolean expectedResult) throws Exception {
        defaultArgs.add("-" + option);

        parser.parseArgs(toArray(defaultArgs));

        Method actualMethod = Class.forName(parser.getClass().getName()).getDeclaredMethod(method);
        boolean actualResult = (boolean) actualMethod.invoke(parser);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    @DisplayName("Test if setting a single url works")
    void testSingleUrl() {
        customArgs.add("-u");
        customArgs.add("https://website.com");

        parser.parseArgs(toArray(customArgs));

        customArgs.remove("-u");

        assertEquals(customArgs, parser.getRootUrls());
    }

    @Test
    @DisplayName("Test if setting multiple urls works")
    void testMultipleUrls() {
        customArgs.add("-u");
        customArgs.add("https://website.com,https://internet.com");

        parser.parseArgs(toArray(customArgs));

        customArgs = new ArrayList<>();
        customArgs.add("https://website.com");
        customArgs.add("https://internet.com");

        assertEquals(customArgs, parser.getRootUrls());
    }

    @Test
    @DisplayName("Test if the https:// scheme gets added when no scheme is found")
    void testUrlSchemaAdding() {
        customArgs.add("-u");
        customArgs.add("website.com");

        parser.parseArgs(toArray(customArgs));

        customArgs = new ArrayList<>();
        customArgs.add("https://website.com");

        assertEquals(customArgs, parser.getRootUrls());
    }

    @Test
    @DisplayName("Test if a url gets rejected when is not valid")
    void testUrlRejection() {
        customArgs.add("-u");
        customArgs.add("https://website");

        assertFalse(parser.parseArgs(toArray(customArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when no url is given")
    void testNoUrl() {
        assertFalse(parser.parseArgs(toArray(customArgs)));
    }

    @Test
    @DisplayName("Test if parseArgs() returns false when urls has no argument")
    void testUrlNoArgument() {
        customArgs.add("-u");

        assertFalse(parser.parseArgs(toArray(customArgs)));
    }

    @Test
    @DisplayName("Test if the error message is correct when urls has no argument")
    void testUrlNoArgumentErrorMessage() {
        customArgs.add("-u");

        assertFalse(parser.parseArgs(toArray(customArgs)));

        assertEquals("Missing argument for option: u", parser.getErrorMessage());
    }

    @Test
    @DisplayName("Test if the scheme adding warning gets added")
    void testUrlWarning() {
        customArgs.add("-u");
        customArgs.add("website.com");

        parser.parseArgs(toArray(customArgs));

        String expectedResult = "No URL scheme given, assuming https://website.com\n";

        assertEquals(expectedResult, parser.getWarnings());
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
    @DisplayName("Test if outputIntoFile() is true when an output is set")
    void testOutputIntoFile() {
        defaultArgs.add("-o");
        defaultArgs.add("file.txt");

        assertTrue(parser.parseArgs(toArray(defaultArgs)));

        assertTrue(parser.outputIntoFile());
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
        String[] result = new String[list.size()];

        for (int i=0; i<result.length; i++)
            result[i] = list.get(i);

        return result;
    }

}
