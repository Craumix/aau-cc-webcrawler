package crawler;

import crawler.argumentparser.OptionsArgumentParser;
import crawler.webpage.filter.DuplicateLoadFilter;
import crawler.webpage.filter.RobotsLoadFilter;
import crawler.webpage.filter.WebpageLoadFilter;
import crawler.webpage.AsyncWebpageLoader;
import crawler.webpage.Webpage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {
    private static final String
            DEFAULT_USER_AGENT = "AAU CleanCode WebCrawler (https://github.com/Craumix/aau-cc-webcrawler)",
            BROWSER_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36";

    private static final OptionsArgumentParser parser = new OptionsArgumentParser();

    private static final ArrayList<Webpage> rootPages = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        if (!parser.parseArgs(args) && !parser.helpRequested()) {
            System.out.println(parser.getWarnings());
            System.err.println(parser.getErrorMessage());
            System.exit(1);
        }

        if (parser.helpRequested()) {
            System.out.println(parser.getHelpDialog());
            System.exit(0);
        }

        System.out.println(parser.getWarnings());

        if (parser.outputIntoFile())
            printWarningIfFileIsntJSON();

        initializeRootPage();

        startLoadingPagesAsynchronously();

        printPages();
    }

    /**
     * Prints a warning if the specified output file isn't a .json file.
     */
    private static void printWarningIfFileIsntJSON() {
        if (!parser.getOutputFile().endsWith(".json")) {
            String filename = parser.getOutputFile();
            if (filename.contains("."))
                filename = filename.substring(0, filename.lastIndexOf("."));

            System.out.printf("The output format is JSON consider using %s as a filename", filename + ".json");
        }
    }

    /**
     * Loads the root page with the specified filters, user agent and links per page.
     * @throws URISyntaxException If the given string violates RFC 2396
     */
    private static void initializeRootPage() throws URISyntaxException {
        Webpage.setRequestUserAgent(parser.spoofBrowser() ? BROWSER_USER_AGENT : DEFAULT_USER_AGENT);
        Webpage.setMaxChildrenPerPage(parser.getMaxLinksPerPage());

        ArrayList<WebpageLoadFilter> loadFilters = new ArrayList<>();
        if (parser.omitDuplicates())
            loadFilters.add(new DuplicateLoadFilter());
        if (parser.respectRobotsTxt())
            loadFilters.add(new RobotsLoadFilter());

        for (String rootUrl : parser.getRootUrls())
            rootPages.add(new Webpage(rootUrl, loadFilters));
    }

    /**
     * Prints the root page and its children either into a file if one is specified
     * or into System.out.
     */
    private static void printPages() {

        String rootPagesAsJSONString = getRootPagesAsJsonString();

        if (parser.outputIntoFile()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(parser.getOutputFile(), false);
                fileWriter.write(rootPagesAsJSONString);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println(rootPagesAsJSONString);
        }
    }

    /**
     * Starts an {@link AsyncWebpageLoader} for every rootPage
     * @throws InterruptedException
     */
    private static void startLoadingPagesAsynchronously() throws InterruptedException {
        for (Webpage rootPage : rootPages) {
            AsyncWebpageLoader pageProcessor = new AsyncWebpageLoader(rootPage, parser.getMaxDepth(), parser.getThreadCount());
            pageProcessor.loadPagesRecursively();
        }
    }

    /**
     * Generates a JSON with all of the JSON representations of the rootPages in the field urls
     * @return A String of the generated JSONObject
     */
    private static String getRootPagesAsJsonString() {
        JSONArray rootPagesJSONArray = new JSONArray();
        for (Webpage rootPage : rootPages) {
            rootPagesJSONArray.put(rootPage.asJSONObject());
        }

        JSONObject rootPagesAsJSON = new JSONObject();
        rootPagesAsJSON.put("webpages", rootPagesJSONArray);

        return rootPagesAsJSON.toString(2);
    }
}
