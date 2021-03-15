package crawler;

import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.*;

public class Main {
    private static String rootUrl, outputFile;
    private static int maxDepth, threadCount, maxLinksPerPage;
    private static boolean omitDuplicates;

    public static void main(String[] args) throws Exception {
        Options cliOptions = getCliOptions();
        CommandLine cmd = new DefaultParser().parse(cliOptions, args);

        parseCliOptions(cmd, cliOptions);

        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);

        Webpage rootPage = new Webpage(rootUrl, maxDepth - 1);
        rootPage.runOnThreadPool(threadPool);

        while (threadPool.getActiveCount() > 0)
            Thread.sleep(50);

        threadPool.shutdown();
        threadPool.awaitTermination(30, TimeUnit.SECONDS);

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
        if(!isValidHttpUrl(rootUrl)) {
            System.err.printf("\"%s\" is not a valid Http URL!", rootUrl);
            System.exit(1);
        }

        maxDepth = Integer.parseInt(cmd.getOptionValue("max-depth", "2"));
        if(maxDepth < 1 || maxDepth > 10) {
            System.err.printf("%d is not a valid search depth", maxDepth);
            System.exit(1);
        }

        threadCount = Integer.parseInt(cmd.getOptionValue("threads", "2"));
        if(threadCount < 1 || threadCount > 1024) {
            System.err.printf("%d is not a valid Thread count", threadCount);
            System.exit(1);
        }

        maxLinksPerPage = Integer.parseInt(cmd.getOptionValue("max-links", "1000"));
        if(maxLinksPerPage < 1) {
            System.err.printf("Max links to follow should be > 1");
            System.exit(1);
        }

        omitDuplicates = cmd.hasOption("omit-duplicates");
        outputFile = cmd.getOptionValue("output","");
    }

    private static Options getCliOptions() {
        Options options = new Options();
        options.addOption("d","max-depth",true, "Specify the recursion depth for following links. Default: 2, Range 1-10");
        options.addOption("s","omit-duplicates",false, "If set omits duplicate pages");
        options.addOption("u", "url", true, "Specify the root url for the crawler");
        options.addOption("o", "output", true, "Output file can be specified as alternative to stdout");
        options.addOption("t", "threads", true, "How many threads to use, will increase CPU and Memory consumption. Default: 2, Range 1-1024");
        options.addOption("l", "max-links", true, "Max links to follow per page. Default: 1000, Range: 1-2147483647");
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
