package crawler;

import org.apache.commons.cli.*;

public class Main {
    private static String rootUrl;
    private static int maxDepth;
    private static boolean omitDuplicates;

    public static void main(String[] args) throws Exception {
        Options cliOptions = getCliOptions();
        CommandLine cmd = new DefaultParser().parse(cliOptions, args);

        if(cmd.hasOption("help") || !cmd.hasOption("url")) {
            new HelpFormatter().printHelp("Webcrawler", cliOptions, true);
            System.exit(0);
        }

        parseCliOptions(cmd);

        Webpage rootPage = new Webpage(rootUrl, maxDepth - 1);
    }

    private static void parseCliOptions(CommandLine cmd) {
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

        omitDuplicates = Boolean.parseBoolean(cmd.getOptionValue("omit-duplicates", "false"));
    }

    private static Options getCliOptions() {
        Options options = new Options();
        options.addOption("d","max-depth",true, "Specify the recursion depth for following links [1-10]");
        options.addOption("o","omit-duplicates",true, "Omit duplicate pages");
        options.addOption("u", "url", true, "Specify the root url for the crawler");
        options.addOption("h", "help", false, "Open the help dialog");
        return options;
    }

    public static boolean isValidHttpUrl(String url) {
        return url.matches("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    }
}
