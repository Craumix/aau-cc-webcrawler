package crawler.argumentparser;

import java.util.ArrayList;

public interface ArgumentParser {
     int DEFAULT_DEPTH = 2;
     int MAX_DEPTH_ALLOWED = 10;
     int DEFAULT_THREAD_COUNT = 2;
     int MAX_THREAD_COUNT = 1024;
     int DEFAULT_MAX_LINKS_PER_PAGE = 100;

    /**
     * Parses the Arguments <br>
     * errors out if: <br>
     * - u is missing <br>
     * - u, t, d, l, o are missing an argument <br>
     * - u doesn't have valid comma separated urls as argument <br>
     * - t, d, l are < 1 <br>
     * - t > {@link ArgumentParser#MAX_THREAD_COUNT} <br>
     * - d > {@link ArgumentParser#MAX_DEPTH_ALLOWED} <br>
     * - a option isn't: t, l, d, u, o, s, b, r, h <br>
     * @param args arguments to parse
     * @return false if an error occurs
     */
    boolean parseArgs(String[] args);

    /**
     * @return true if -h is set
     */
    boolean helpRequested();
    /**
     * @return true if -s is set
     */
    boolean omitDuplicates();
    /**
     * @return true if -b is set
     */
    boolean spoofBrowser();
    /**
     * @return false if -r is set
     */
    boolean respectRobotsTxt();
    /**
     * @return true if -o has an argument
     */
    boolean outputIntoFile();

    /**
     * @return - {@link ArgumentParser#DEFAULT_DEPTH} when -d isn't set <br>
     *         - the argument of -d otherwise
     */
    int getMaxDepth();
    /**
     * @return - {@link ArgumentParser#DEFAULT_THREAD_COUNT} when -t isn't set <br>
     *         - the argument of -t otherwise
     */
    int getThreadCount();
    /**
     * @return - {@link ArgumentParser#DEFAULT_MAX_LINKS_PER_PAGE} when -l isn't set <br>
     *         - the argument of -l otherwise
     */
    int getMaxLinksPerPage();
    /**
     * @return - an empty String when -o isn't set <br>
     *         - the argument of -o otherwise
     */
    String getOutputFile();
    /**
     * @return - an empty {@link ArrayList} when -u isn't set <br>
     *         - an ArrayList filled with the parsed urls otherwise
     */
    ArrayList<String> getRootUrls();

    /**
     * @return a error message
     */
    String getErrorMessage();
    /**
     * @return a help dialog with information about the arguments
     *         and their usage
     */
    String getHelpDialog();
    /**
     * @return all warnings while parsing
     */
    String getWarnings();
}
