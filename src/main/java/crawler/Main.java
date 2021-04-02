package crawler;

import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    private static final int
            DEFAULT_DEPTH = 2,
            MAX_DEPTH = 10,
            DEFAULT_THREAD_COUNT = 2,
            MAX_THREAD_COUNT = 1024,
            DEFAULT_MAX_LINKS_PER_PAGE = 100;
    private static final String
            DEFAULT_USER_AGENT = "AAU CleanCode WebCrawler (https://github.com/Craumix/aau-cc-webcrawler)",
            BROWSER_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";


    private static String rootUrl, outputFile;
    private static int maxDepth, threadCount, maxLinksPerPage;
    private static boolean omitDuplicates, fakeBrowser, useRobotsTxt;

    private static Webpage rootPage;

    public static void main(String[] args) throws Exception {
        Options cliOptions = getCliOptions();
        CommandLine cmd = new DefaultParser().parse(cliOptions, args);

        if(helpRequested(cmd, cliOptions))
            System.exit(0);
        if(!parseCliOptions(cmd))
            System.exit(1);

        Webpage.setRequestUserAgent(fakeBrowser ? BROWSER_USER_AGENT : DEFAULT_USER_AGENT);
        Webpage.setMaxChildrenPerPage(maxLinksPerPage);

        CrawlerLoadFilter loadFilter = new CrawlerLoadFilter(omitDuplicates, useRobotsTxt);

        rootPage = new Webpage(rootUrl, loadFilter);
        AsyncWebpageProcessor pageProcessor = new AsyncWebpageProcessor(rootPage, maxDepth, threadCount);
        pageProcessor.loadPagesRecursively();

        printPages();
    }

    private static boolean helpRequested(CommandLine cmd, Options cliOptions) {
        if(cmd.hasOption("help") || !cmd.hasOption("url")) {
            new HelpFormatter().printHelp("Webcrawler", cliOptions, true);
            return true;
        }
        return false;
    }

    private static boolean parseCliOptions(CommandLine cmd) {
        rootUrl = cmd.getOptionValue("url");
        if(!rootUrl.contains("://")) {
            rootUrl = "http://" + rootUrl;
            System.out.printf("No URL scheme given, assuming %s%n", rootUrl);
        }
        if(!isValidHttpUrl(rootUrl)) {
            System.err.printf("\"%s\" is not a valid Http URL!", cmd.getOptionValue("url"));
            return false;
        }

        maxDepth = Integer.parseInt(cmd.getOptionValue("max-depth", DEFAULT_DEPTH + ""));
        if(maxDepth < 1 || maxDepth > MAX_DEPTH) {
            System.err.printf("%d is not a valid search depth", maxDepth);
            return false;
        }

        threadCount = Integer.parseInt(cmd.getOptionValue("threads", DEFAULT_THREAD_COUNT + ""));
        if(threadCount < 1 || threadCount > MAX_THREAD_COUNT) {
            System.err.printf("%d is not a valid Thread count", threadCount);
            return false;
        }

        maxLinksPerPage = Integer.parseInt(cmd.getOptionValue("max-links", DEFAULT_MAX_LINKS_PER_PAGE + ""));
        if(maxLinksPerPage < 1) {
            System.err.printf("Max links to follow should be > 1");
            return false;
        }

        omitDuplicates = cmd.hasOption("omit-duplicates");
        fakeBrowser = cmd.hasOption("fake-browser");
        useRobotsTxt = !cmd.hasOption("ignore-robots-txt");
        outputFile = cmd.getOptionValue("output","");

        return true;
    }

    private static Options getCliOptions() {
        Options options = new Options();
        options.addOption("t",  "threads",          true, String.format("Amount of threads to use, will increase CPU and Memory consumption. Default: %d, Range 1-1024", DEFAULT_THREAD_COUNT, MAX_THREAD_COUNT));
        options.addOption("l",  "max-links",        true, String.format("Max amount of links to follow per page. Default: %d, Range: 1-inf", DEFAULT_MAX_LINKS_PER_PAGE));
        options.addOption("d",  "max-depth",        true, String.format("Specify the recursion depth for following links. Default: %d, Range 1-%d", DEFAULT_DEPTH, MAX_DEPTH));
        options.addOption("u",  "url",              true,   "Specify the root url for the crawler");
        options.addOption("o",  "output",           true,   "Specify a Output File as alternative to stdout");
        options.addOption("s",  "omit-duplicates",  false,  "If set, omits duplicate pages");
        options.addOption("b",  "fake-browser",     false,  "If set, uses Browser UserAgent (in case some sites block the default UserAgent)");
        options.addOption("r",  "ignore-robots-txt",false,  "If set, ignores robots.txt");
        options.addOption("h",  "help",             false,  "Open the help dialog");
        return options;
    }

    private static void printPages() throws FileNotFoundException {
        PrintStream programOutput;
        if(outputFile.equals(""))
            programOutput = System.out;
        else
            programOutput = new PrintStream(new FileOutputStream(outputFile, false));

        rootPage.printWithChildren(programOutput);
    }

    public static boolean isValidHttpUrl(String url) {
        return url.matches("https?:\\/\\/?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    }
}
