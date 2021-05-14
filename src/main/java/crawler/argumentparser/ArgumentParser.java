package crawler.argumentparser;

import java.util.ArrayList;

public interface ArgumentParser {
    /**
     * Parses the Arguments
     * @param args Arguments to parse
     * @return false if an error occurs
     */
    boolean parseArgs(String[] args);


    boolean helpRequested();
    boolean omitDuplicates();
    boolean spoofBrowser();
    boolean respectRobotsTxt();
    boolean outputIntoFile();

    int getMaxDepth();
    int getThreadCount();
    int getMaxLinksPerPage();

    String getOutputFile();

    ArrayList<String> getRootUrls();


    String getErrorMessage();
    String getHelpDialog();
    String getWarnings();
}
