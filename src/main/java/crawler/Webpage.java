package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class Webpage {

    private static final String REQUEST_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";

    private static CopyOnWriteArrayList<URI> pageUrlLog = new CopyOnWriteArrayList<>();

    private URI pageURI;
    private int remainingDepth;

    private Document pageDocument;
    private Elements links, images, videos;
    private int wordCount;
    private long pageSize, loadTimeInNanos;
    private String pageTitle;
    private Exception error;

    private ArrayList<Webpage> children = new ArrayList<>();

    public Webpage(String pageURI, int depth) throws URISyntaxException {
        this(new URI(pageURI), depth);
    }
    public Webpage(URI pageURI, int depth) {
        this.pageURI = pageURI;
        this.remainingDepth = depth - 1;

        pageUrlLog.add(pageURI);
    }

    public void runOnThreadPool(ThreadPoolExecutor threadPool) {
        threadPool.execute(() -> {
            try {
                long startTime = System.nanoTime();
                pageDocument = Jsoup.connect(pageURI.toString()).userAgent(REQUEST_USER_AGENT).get();
                loadTimeInNanos = System.nanoTime() - startTime;

                pageTitle = pageDocument.title();
                links = pageDocument.select("a[href]");
                images = pageDocument.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
                videos = pageDocument.select("video");
                wordCount = pageDocument.text().split(" ").length;
                pageSize = pageDocument.html().getBytes(StandardCharsets.UTF_8).length;

                freePageDocumentMemory();

                if(remainingDepth > 0) {
                    createChildrenFromPagelinks();
                    for (Webpage child : children)
                        child.runOnThreadPool(threadPool);
                }

            } catch (Exception e) {
                error = e;
            }
        });
    }

    public void printWithChildren(PrintStream out) {
        printWithChildren("", out);
    }
    public void printWithChildren(String offset, PrintStream out) {
        out.println(offset + pageURI.toString());
        if(error != null) {
            System.out.println(offset + "Error: " + error.getMessage());
            return;
        }

        double bytesPerSecond = pageSize / (loadTimeInNanos / 1e9);

        out.println(offset + "Title: \t\t" + pageTitle);
        out.println(offset + "Links: \t\t" + links.size());
        out.println(offset + "Images: \t" + images.size());
        out.println(offset + "Videos: \t" + videos.size());
        out.println(offset + "Wordcount: \t" + wordCount);
        out.println(offset + String.format("Loading: \t%s in %.2fms ( @ %s/s)", FormattingUtil.readableFileSize(pageSize), (loadTimeInNanos / 1e6), FormattingUtil.readableFileSize(bytesPerSecond)));
        for(Webpage child : children)
            child.printWithChildren(offset + "\t", out);
    }

    private void createChildrenFromPagelinks() throws URISyntaxException {
        for(Element link : links) {
            URI rawURI = new URI(link.attr("href"));
            //if(rawLink.equals("#") || rawLink.equals("/") || rawLink.equals("./") || rawLink.startsWith("javascript:"))
            //    continue;

            URI resolvedChildURI = pageURI.resolve(rawURI);
            if(resolvedChildURI.equals(pageURI))
                continue;
            if(!(pageUrlLog.contains(resolvedChildURI) && Main.shouldOmitDuplicates())) {
                Webpage child = new Webpage(resolvedChildURI, remainingDepth);
                children.add(child);
            }

            if (children.size() >= Main.getMaxLinksPerPage())
                break;
        }
    }

    private void freePageDocumentMemory() {
        pageDocument = null;
    }
}
