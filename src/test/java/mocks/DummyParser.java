package mocks;

import crawler.argumentparser.ArgumentParser;

import java.util.ArrayList;

public class DummyParser implements ArgumentParser {

    private boolean parseSuccess = false;
    private boolean helpRequested = false;
    private boolean omitDuplicates = false;
    private boolean spoofBrowser = false;
    private boolean respectRobotsTxt = false;
    private boolean outputIntoFile = false;
    
    private int maxDepth = 0;
    private int threadCount = 0;
    private int maxLinksPerPage = 0;

    private String outputFile = "";
    private String errorMessage = "";
    private String helpDialog = "";
    private String warnings = "";

    private ArrayList<String> rootUrls = new ArrayList<>();

    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    public void setHelpRequested(boolean helpRequested) {
        this.helpRequested = helpRequested;
    }

    public void setOmitDuplicates(boolean omitDuplicates) {
        this.omitDuplicates = omitDuplicates;
    }

    public void setSpoofBrowser(boolean spoofBrowser) {
        this.spoofBrowser = spoofBrowser;
    }

    public void setRespectRobotsTxt(boolean respectRobotsTxt) {
        this.respectRobotsTxt = respectRobotsTxt;
    }

    public void setOutputIntoFile(boolean outputIntoFile) {
        this.outputIntoFile = outputIntoFile;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void setMaxLinksPerPage(int maxLinksPerPage) {
        this.maxLinksPerPage = maxLinksPerPage;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setHelpDialog(String helpDialog) {
        this.helpDialog = helpDialog;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public void setRootUrls(ArrayList<String> rootUrls) {
        this.rootUrls = rootUrls;
    }

    @Override
    public boolean parseArgs(String[] args) {
        return parseSuccess;
    }

    @Override
    public boolean helpRequested() {
        return helpRequested;
    }

    @Override
    public boolean omitDuplicates() {
        return omitDuplicates;
    }

    @Override
    public boolean spoofBrowser() {
        return spoofBrowser;
    }

    @Override
    public boolean respectRobotsTxt() {
        return respectRobotsTxt;
    }

    @Override
    public boolean outputIntoFile() {
        return outputIntoFile;
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
    public String getOutputFile() {
        return outputFile;
    }

    @Override
    public ArrayList<String> getRootUrls() {
        return rootUrls;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getHelpDialog() {
        return helpDialog;
    }

    @Override
    public String getWarnings() {
        return warnings;
    }
}
