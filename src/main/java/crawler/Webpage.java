package crawler;

import org.jsoup.select.Elements;

import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Webpage {

    private final URI pageURI;

    private Elements links, images, videos;
    private int wordCount;
    private long pageSize, loadTimeInNanos;
    private String pageTitle;

    private Exception error;

    private ArrayList<Webpage> children = new ArrayList<>();

    public Webpage(String pageURI) throws URISyntaxException {
        this(new URI(pageURI));
    }

    public Webpage(URI pageURI) {
        this.pageURI = pageURI;
    }

    public void printWithChildren(PrintStream out) {
        printWithChildren("", out);
    }

    public void printWithChildren(String offset, PrintStream out) {
        out.println(offset + pageURI.toString());
        if(error != null) {
            out.println(offset + "Error: " + error.getMessage());
            return;
        }

        double bytesPerSecond = pageSize / (loadTimeInNanos / 1e9);

        out.println(offset + "Title: \t\t" + pageTitle);
        out.println(offset + "Links: \t\t" + links.size());
        out.println(offset + "Images: \t" + images.size());
        out.println(offset + "Videos: \t" + videos.size());
        out.println(offset + "Word count: " + wordCount);
        out.println(offset + String.format(
                "Loading: \t%s in %.2fms ( @ %s/s)",
                FormattingUtil.readableFileSize(pageSize),
                (loadTimeInNanos / 1e6), FormattingUtil.readableFileSize(bytesPerSecond)
        ));

        for(Webpage child : children)
            child.printWithChildren(offset + "\t", out);
    }



    public void setLinks(Elements links) {
        this.links = links;
    }

    public void setImages(Elements images) {
        this.images = images;
    }

    public void setVideos(Elements videos) {
        this.videos = videos;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public void setLoadTimeInNanos(long loadTimeInNanos) {
        this.loadTimeInNanos = loadTimeInNanos;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    public void addChild(Webpage child) {
        this.children.add(child);
    }



    public Elements getLinks() {
        return links;
    }

    public URI getPageURI() {
        return pageURI;
    }

    public ArrayList<Webpage> getChildren() {
        return children;
    }
}
