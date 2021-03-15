package crawler;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.*;

public class Main {
    private static final int DEFAULT_MAX_DEPTH = 2, DEFAULT_THREAD_COUNT = 2, DEFAULT_MAX_LINKS_PER_PAGE = 100;

    private static String rootUrl, outputFile;
    private static int maxDepth, threadCount, maxLinksPerPage;
    private static boolean omitDuplicates;

    public static void main(String[] args) throws Exception {
        Options cliOptions = getCliOptions();
        CommandLine cmd = new DefaultParser().parse(cliOptions, args);
        parseCliOptions(cmd, cliOptions);

        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        Webpage rootPage = new Webpage(rootUrl, maxDepth);
        rootPage.runOnThreadPool(threadPool);
        ThreadingUtil.waitUntilPoolEmptyAndTerminated(threadPool);

        PrintStream programOutput;
        if(outputFile.equals(""))
            programOutput = System.out;
        else
            programOutput = new PrintStream(new FileOutputStream(new File(outputFile), false));

        rootPage.printWithChildren(programOutput);
    }

    private static void parseCliOptions(CommandLine cmd, Options cliOptions) {
        if(cmd.hasOption("help") || !cmd.hasOption("url")) {
            new HelpFormatter().printHelp("Webcrawler", cliOptions, true);
            System.exit(0);
        }

        rootUrl = cmd.getOptionValue("url");
        if(!rootUrl.contains("://")) {
            System.out.println("No URL scheme given, assuming http...");
            rootUrl = "http://" + rootUrl;
        }
        if(!isValidHttpUrl(rootUrl)) {
            System.err.printf("\"%s\" is not a valid Http URL!", rootUrl);
            System.exit(1);
        }

        maxDepth = Integer.parseInt(cmd.getOptionValue("max-depth", DEFAULT_MAX_DEPTH + ""));
        if(maxDepth < 1 || maxDepth > 10) {
            System.err.printf("%d is not a valid search depth", maxDepth);
            System.exit(1);
        }

        threadCount = Integer.parseInt(cmd.getOptionValue("threads", DEFAULT_THREAD_COUNT + ""));
        if(threadCount < 1 || threadCount > 1024) {
            System.err.printf("%d is not a valid Thread count", threadCount);
            System.exit(1);
        }

        maxLinksPerPage = Integer.parseInt(cmd.getOptionValue("max-links", DEFAULT_MAX_LINKS_PER_PAGE + ""));
        if(maxLinksPerPage < 1) {
            System.err.printf("Max links to follow should be > 1");
            System.exit(1);
        }

        omitDuplicates = cmd.hasOption("omit-duplicates");
        outputFile = cmd.getOptionValue("output","");
    }

    private static Options getCliOptions() {
        Options options = new Options();
        options.addOption("d","max-depth",true, String.format("Specify the recursion depth for following links. Default: %d, Range 1-10", DEFAULT_MAX_DEPTH));
        options.addOption("s","omit-duplicates",false, "If set omits duplicate pages");
        options.addOption("u", "url", true, "Specify the root url for the crawler");
        options.addOption("o", "output", true, "Output file can be specified as alternative to stdout");
        options.addOption("t", "threads", true, String.format("How many threads to use, will increase CPU and Memory consumption. Default: %d, Range 1-1024", DEFAULT_THREAD_COUNT));
        options.addOption("l", "max-links", true, String.format("Max links to follow per page. Default: %d, Range: 1-inf", DEFAULT_MAX_LINKS_PER_PAGE));
        options.addOption("h", "help", false, "Open the help dialog");
        return options;
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
}
