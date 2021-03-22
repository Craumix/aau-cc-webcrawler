package crawler;

import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class Main {
    private static final int DEFAULT_MAX_DEPTH = 2, DEFAULT_THREAD_COUNT = 2, DEFAULT_MAX_LINKS_PER_PAGE = 100;

    private static String rootUrl, outputFile;
    private static int maxDepth, threadCount, maxLinksPerPage;
    private static boolean omitDuplicates, fakeBrowser, respectRobotsTXT;

    private static Webpage rootPage;

    public static void main(String[] args) throws Exception {
        Options cliOptions = getCliOptions();
        CommandLine cmd = new DefaultParser().parse(cliOptions, args);

        if(helpRequested(cmd, cliOptions))
            System.exit(0);
        if(!parseCliOptions(cmd))
            System.exit(1);

        rootPage = new Webpage(rootUrl);
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
            System.out.println("No URL scheme given, assuming http...");
            rootUrl = "http://" + rootUrl;
        }
        if(!isValidHttpUrl(rootUrl)) {
            System.err.printf("\"%s\" is not a valid Http URL!", cmd.getOptionValue("url"));
            return false;
        }

        maxDepth = Integer.parseInt(cmd.getOptionValue("max-depth", DEFAULT_MAX_DEPTH + ""));
        if(maxDepth < 1 || maxDepth > 10) {
            System.err.printf("%d is not a valid search depth", maxDepth);
            return false;
        }

        threadCount = Integer.parseInt(cmd.getOptionValue("threads", DEFAULT_THREAD_COUNT + ""));
        if(threadCount < 1 || threadCount > 1024) {
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
        respectRobotsTXT = !cmd.hasOption("ignore-robots-txt");
        outputFile = cmd.getOptionValue("output","");

        return true;
    }

    private static Options getCliOptions() {
        Options options = new Options();
        options.addOption("d","max-depth",true, String.format("Specify the recursion depth for following links. Default: %d, Range 1-10", DEFAULT_MAX_DEPTH));
        options.addOption("s","omit-duplicates",false, "If set omits duplicate pages");
        options.addOption("b","fake-browser",false, "Use fake Browser UserAgent (in case some sites block the regular one)");
        options.addOption("r","ignore-robots-txt",false, "Ignores robots.txt");
        options.addOption("u", "url", true, "Specify the root url for the crawler");
        options.addOption("o", "output", true, "Output file can be specified as alternative to stdout");
        options.addOption("t", "threads", true, String.format("How many threads to use, will increase CPU and Memory consumption. Default: %d, Range 1-1024", DEFAULT_THREAD_COUNT));
        options.addOption("l", "max-links", true, String.format("Max links to follow per page. Default: %d, Range: 1-inf", DEFAULT_MAX_LINKS_PER_PAGE));
        options.addOption("h", "help", false, "Open the help dialog");
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

    public static boolean shouldOmitDuplicates() {
        return omitDuplicates;
    }

    public static int getMaxLinksPerPage() {
        return maxLinksPerPage;
    }

    public static boolean useBrowserUserAgent() {
        return fakeBrowser;
    }
}
