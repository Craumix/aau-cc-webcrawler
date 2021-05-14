package crawler.argumentparser;

import crawler.util.Util;
import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class OptionsArgumentParser implements ArgumentParser{
    private static final int
            DEFAULT_DEPTH = 2,
            MAX_DEPTH_ALLOWED = 10,
            DEFAULT_THREAD_COUNT = 2,
            MAX_THREAD_COUNT = 1024,
            DEFAULT_MAX_LINKS_PER_PAGE = 100;

    private final Options cliOptions = initializeCliOptions();
    private CommandLine cmdLine;

    private String errorMessage;
    private int maxDepth, maxLinksPerPage, threadCount;
    private final ArrayList<String> rootUrls = new ArrayList<>();
    private final ArrayList<String> warnings = new ArrayList<>();

    @Override
    public boolean parseArgs(String[] args) {
        try {
            cmdLine = new DefaultParser().parse(cliOptions, args);

            parseRootUrls();
            parseMaxDepth();
            parseMaxLinksPerPage();
            parseThreadCount();

        } catch (ParseException e) {
            errorMessage = e.getMessage();
            return false;
        }

        return true;
    }

    private void parseRootUrls() throws ParseException {
        String urls = cmdLine.getOptionValue("urls", null);

        if (urls == null)
            throw new ParseException("Missing argument: u");

        String[] rawRootUrls = urls.split(",");

        for (String rawRootUrl : rawRootUrls) {
            if (!rawRootUrl.contains("://")) {
                rawRootUrl = "https://" + rawRootUrl;
                warnings.add(String.format("No URL scheme given, assuming %s", rawRootUrl));
            }
            if (!Util.isValidHttpUrl(rawRootUrl)) {
                throw new ParseException(String.format("\"%s\" is not a valid Http URL!", rawRootUrl));
            }
            rootUrls.add(rawRootUrl);
        }

    }

    private void parseMaxDepth() throws ParseException {
        maxDepth = Integer.parseInt(cmdLine.getOptionValue("max-depth", DEFAULT_DEPTH + ""));
        if (maxDepth < 1 || maxDepth > MAX_DEPTH_ALLOWED)
            throw new ParseException(String.format("%d is not a valid search depth", maxDepth));
    }

    private void parseMaxLinksPerPage() throws ParseException {
        maxLinksPerPage = Integer.parseInt(cmdLine.getOptionValue("max-links", DEFAULT_MAX_LINKS_PER_PAGE + ""));
        if (maxLinksPerPage < 1)
            throw new ParseException("Max links to follow should be > 0");
    }

    private void parseThreadCount() throws ParseException {
        threadCount = Integer.parseInt(cmdLine.getOptionValue("thread-count", DEFAULT_THREAD_COUNT + ""));
        if (threadCount < 1 || threadCount > MAX_THREAD_COUNT)
            throw new ParseException(String.format("%d is not a valid number of threads", threadCount));
    }

    /**
     * Generates a help dialog from the parsed arguments <br>
     * https://stackoverflow.com/questions/44426626/how-do-i-get-help-string-from-commons-cli-instead-of-print
     * @return A help dialog as a String
     */
    @Override
    public String getHelpDialog() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                writer,
                formatter.getWidth(),
                "Webcrawler",
                "",
                cliOptions,
                formatter.getLeftPadding(),
                formatter.getDescPadding(),
                "",
                true
        );

        writer.flush();

        return out.toString();
    }

    private static Options initializeCliOptions() {
        Options options = new Options();
        options.addOption("t",  "thread-count",     true, String.format("Amount of threads to use, will increase CPU and Memory consumption. Default: %d, Range 1-%d", DEFAULT_THREAD_COUNT, MAX_THREAD_COUNT));
        options.addOption("l",  "max-links",        true, String.format("Max amount of links to follow per page. Default: %d, Range: 1-inf", DEFAULT_MAX_LINKS_PER_PAGE));
        options.addOption("d",  "max-depth",        true, String.format("Specify the recursion depth for following links. Default: %d, Range 1-%d", DEFAULT_DEPTH, MAX_DEPTH_ALLOWED));
        options.addOption("u",  "urls",             true,   "Specify the root urls for the crawler. Multiple urls must be comma separated");
        options.addOption("o",  "output",           true,   "Specify a Output File as alternative to stdout");
        options.addOption("s",  "omit-duplicates",  false,  "If set, omits duplicate pages");
        options.addOption("b",  "spoof-browser",    false,  "If set, spoofs the UserAgent (in case some sites block the default UserAgent)");
        options.addOption("r",  "ignore-robots-txt",false,  "If set, ignores robots.txt");
        options.addOption("h",  "help",             false,  "Open the help dialog");
        return options;
    }

    @Override
    public boolean helpRequested() {
        return cmdLine != null && cmdLine.hasOption("help");
    }

    @Override
    public boolean omitDuplicates() {
        return cmdLine != null && cmdLine.hasOption("omit-duplicates");
    }

    @Override
    public boolean spoofBrowser() {
        return cmdLine != null && cmdLine.hasOption("spoof-browser");
    }

    @Override
    public boolean respectRobotsTxt() {
        return cmdLine == null || !cmdLine.hasOption("ignore-robots-txt");
    }

    @Override
    public boolean outputIntoFile() {
        return cmdLine != null && !(cmdLine.getOptionValue("output") == null);
    }

    @Override
    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public int getMaxLinksPerPage() {
        return maxLinksPerPage;
    }

    @Override
    public ArrayList<String> getRootUrls() {
        return rootUrls;
    }

    @Override
    public String getWarnings() {
        StringBuilder warningsAsString = new StringBuilder();
        for (String warning : warnings)
            warningsAsString.append(warning).append("\n");
        return warningsAsString.toString();
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getOutputFile() {
        return cmdLine.getOptionValue("output", "");
    }
}
