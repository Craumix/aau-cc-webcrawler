package crawler;

import org.apache.commons.cli.*;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    private static final int
            DEFAULT_DEPTH = 2,
            MAX_DEPTH = 10,
            DEFAULT_THREAD_COUNT = 2,
            MAX_THREAD_COUNT = 1024,
            DEFAULT_MAX_LINKS_PER_PAGE = 100;
    private static final String
            DEFAULT_USER_AGENT = "AAU CleanCode WebCrawler (https://github.com/Craumix/aau-cc-webcrawler)",
            BROWSER_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36";


    private static String rootUrl, outputFile;
    private static int maxDepth, threadCount, maxLinksPerPage;
    private static boolean omitDuplicates, spoofBrowser, respectRobotsTxt;

    private static Options cliOptions;
    private static CommandLine cmdLine;

    private static Webpage rootPage;

    public static void main(String[] args) throws Exception {
        initializeCliOptions();
        cmdLine = new DefaultParser().parse(cliOptions, args);

        if (helpRequested())
            System.exit(0);
        if (!parseCliOptions())
            System.exit(1);

        initializeRootPage();

        AsyncWebpageProcessor pageProcessor = new AsyncWebpageProcessor(rootPage, maxDepth, threadCount);
        pageProcessor.loadPagesRecursively();

        printPages();
    }

    private static boolean helpRequested() {
        if (cmdLine.hasOption("help") || !cmdLine.hasOption("url")) {
            new HelpFormatter().printHelp("Webcrawler", cliOptions, true);
            return true;
        }
        return false;
    }

    private static boolean parseCliOptions() {
        // returns false when an error occurs
        rootUrl = cmdLine.getOptionValue("url");
        if (!rootUrl.contains("://")) {
            rootUrl = "http://" + rootUrl;
            System.out.printf("No URL scheme given, assuming %s%n", rootUrl);
        }
        if (!Util.isValidHttpUrl(rootUrl)) {
            System.err.printf("\"%s\" is not a valid Http URL!", cmdLine.getOptionValue("url"));
            return false;
        }

        maxDepth = Integer.parseInt(cmdLine.getOptionValue("max-depth", DEFAULT_DEPTH + ""));
        if (maxDepth < 1 || maxDepth > MAX_DEPTH) {
            System.err.printf("%d is not a valid search depth", maxDepth);
            return false;
        }

        threadCount = Integer.parseInt(cmdLine.getOptionValue("threads", DEFAULT_THREAD_COUNT + ""));
        if (threadCount < 1 || threadCount > MAX_THREAD_COUNT) {
            System.err.printf("%d is not a valid Thread count", threadCount);
            return false;
        }

        maxLinksPerPage = Integer.parseInt(cmdLine.getOptionValue("max-links", DEFAULT_MAX_LINKS_PER_PAGE + ""));
        if (maxLinksPerPage < 1) {
            System.err.print("Max links to follow should be > 1");
            return false;
        }

        omitDuplicates = cmdLine.hasOption("omit-duplicates");
        spoofBrowser = cmdLine.hasOption("fake-browser");
        respectRobotsTxt = !cmdLine.hasOption("ignore-robots-txt");
        outputFile = cmdLine.getOptionValue("output","");

        return true;
    }

    private static void initializeCliOptions() {
        Options options = new Options();
        options.addOption("t",  "threads",          true, String.format("Amount of threads to use, will increase CPU and Memory consumption. Default: %d, Range 1-%d", DEFAULT_THREAD_COUNT, MAX_THREAD_COUNT));
        options.addOption("l",  "max-links",        true, String.format("Max amount of links to follow per page. Default: %d, Range: 1-inf", DEFAULT_MAX_LINKS_PER_PAGE));
        options.addOption("d",  "max-depth",        true, String.format("Specify the recursion depth for following links. Default: %d, Range 1-%d", DEFAULT_DEPTH, MAX_DEPTH));
        options.addOption("u",  "url",              true,   "Specify the root url for the crawler");
        options.addOption("o",  "output",           true,   "Specify a Output File as alternative to stdout");
        options.addOption("s",  "omit-duplicates",  false,  "If set, omits duplicate pages");
        options.addOption("b",  "spoof-browser",    false,  "If set, spoofs the UserAgent (in case some sites block the default UserAgent)");
        options.addOption("r",  "ignore-robots-txt",false,  "If set, ignores robots.txt");
        options.addOption("h",  "help",             false,  "Open the help dialog");
        cliOptions = options;
    }

    private static void initializeRootPage() throws URISyntaxException {
        Webpage.setRequestUserAgent(spoofBrowser ? BROWSER_USER_AGENT : DEFAULT_USER_AGENT);
        Webpage.setMaxChildrenPerPage(maxLinksPerPage);

        ArrayList<WebpageLoadFilter> loadFilters = new ArrayList<>();
        if (omitDuplicates)
            loadFilters.add(new DuplicateLoadFilter());
        if (respectRobotsTxt)
            loadFilters.add(new RobotLoadFilter());

        rootPage = new Webpage(rootUrl, loadFilters);
    }

    private static void printPages() {
        String jsonString = rootPage.asJSONObject().toString(2);

        if (!outputFile.equals("")) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(outputFile, false);
                fw.write(jsonString);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println(jsonString);
        }
    }
}
