package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Webpage {

    private static String userAgent = "Java/" + System.getProperty("java.version");
    private static int maxChildrenPerPage = Integer.MAX_VALUE;

    private final URI pageURI;

    private Elements links, images, videos;
    private int wordCount;
    private long pageSize, loadTimeInNanos;
    private String pageTitle;

    private Exception error;

    private ArrayList<Webpage> children = new ArrayList<>();

    private final WebpageLoadFilter loadFilter;

    public Webpage(String pageURI) throws URISyntaxException {
        this(new URI(pageURI), null);
    }

    public Webpage(URI pageURI) {
        this(pageURI, null);
    }

    public Webpage(String pageURI, WebpageLoadFilter loadFilter) throws URISyntaxException {
        this(new URI(pageURI), loadFilter);
    }

    public Webpage(URI pageURI, WebpageLoadFilter loadFilter) {
        this.pageURI = pageURI;
        this.loadFilter = loadFilter;
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
        out.println(offset + "Word count:\t" + wordCount);
        out.println(offset + String.format(
                "Loading: \t%s in %.2fms ( @ %s/s)",
                Util.readableFileSize(pageSize),
                (loadTimeInNanos / 1e6), Util.readableFileSize(bytesPerSecond)
        ));

        for(Webpage child : children)
            if(child.wasLoaded())
                child.printWithChildren(offset + "\t", out);
    }

    public void loadPage() {
        try {
            long startTime = System.nanoTime();
            Document pageDocument = Jsoup.connect(pageURI.toString()).userAgent(userAgent).get();
            loadTimeInNanos = System.nanoTime() - startTime;

            pageTitle = pageDocument.title();
            links = pageDocument.select("a[href]");
            images = pageDocument.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
            videos = pageDocument.select("video");
            wordCount = pageDocument.body().text().split(" ").length;
            pageSize = pageDocument.html().getBytes(StandardCharsets.UTF_8).length;

            initializeChildren();
        } catch (IOException e) {
            error = e;
        }
    }

    private void initializeChildren() {
        for(Element link : links) {
            URI rawURI = null;
            try {
                rawURI = new URI(link.attr("href"));
            } catch (URISyntaxException e) {
                continue;
            }

            URI resolvedChildURI = pageURI.resolve(rawURI);
            if(resolvedChildURI.equals(pageURI))
                continue;
            if(loadFilter == null || loadFilter.webpageShouldBeLoaded(resolvedChildURI)) {
                Webpage child = new Webpage(resolvedChildURI, loadFilter);
                children.add(child);
            }

            if (children.size() >= maxChildrenPerPage)
                break;
        }
    }

    public ArrayList<Webpage> getChildren() {
        return children;
    }

    public boolean wasLoaded() {
        return loadTimeInNanos > 0;
    }

    public static void setRequestUserAgent(String agent) {
        userAgent = agent;
    }

    public static void setMaxChildrenPerPage(int count) {
        maxChildrenPerPage = count;
    }


    public int getWordCount() {
        return wordCount;
    }

    public String getPageTitle() {
        return pageTitle;
    }
}
