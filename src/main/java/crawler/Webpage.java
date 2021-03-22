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
import java.util.concurrent.CopyOnWriteArrayList;

public class Webpage {

    private static final String REQUEST_USER_AGENT = "AAU CleanCode WebCrawler (https://github.com/Craumix/aau-cc-webcrawler)";
    private static final String BROWSER_REQUEST_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";
    private static final CopyOnWriteArrayList<URI> pageUrlLog = new CopyOnWriteArrayList<>();

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
            if(child.wasLoaded())
                child.printWithChildren(offset + "\t", out);
    }

    public void loadPage() {
        try {
            long startTime = System.nanoTime();
            Document pageDocument = Jsoup.connect(pageURI.toString()).userAgent(Main.useBrowserUserAgent() ? BROWSER_REQUEST_USER_AGENT : REQUEST_USER_AGENT).get();
            loadTimeInNanos = System.nanoTime() - startTime;

            pageTitle = pageDocument.title();
            links = pageDocument.select("a[href]");
            images = pageDocument.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
            videos = pageDocument.select("video");
            wordCount = pageDocument.text().split(" ").length;
            pageSize = pageDocument.html().getBytes(StandardCharsets.UTF_8).length;

            initializeChildren();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeChildren() {
        for(Element link : links) {
            URI rawURI = null;
            try {
                rawURI = new URI(link.attr("href"));
            } catch (URISyntaxException e) {
                e.printStackTrace();
                continue;
            }
            if(rawURI.toString().equals("#") ||
                    rawURI.toString().equals("/") ||
                    rawURI.toString().equals("./") ||
                    rawURI.toString().startsWith("javascript:"))
                continue;

            URI resolvedChildURI = pageURI.resolve(rawURI);
            if(resolvedChildURI.equals(pageURI))
                continue;
            if(!(pageUrlLog.contains(resolvedChildURI) && Main.shouldOmitDuplicates())) {
                Webpage child = new Webpage(resolvedChildURI);
                pageUrlLog.add(pageURI);
                children.add(child);
            }

            if (children.size() >= Main.getMaxLinksPerPage())
                break;
        }
    }

    public ArrayList<Webpage> getChildren() {
        return children;
    }

    public boolean wasLoaded() {
        return loadTimeInNanos > 0;
    }
}
