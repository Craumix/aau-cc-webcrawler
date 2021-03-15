package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class Webpage {

    private static final String REQUEST_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";

    private static CopyOnWriteArrayList<String> pageUrlLog = new CopyOnWriteArrayList<>();

    private String url;
    private int remainingDepth;

    private Document pageDocument;
    private Elements links, images, videos;
    private int wordCount;
    private long pageSize, loadTimeInNanos;
    private String pageTitle;
    private Exception error;

    private ArrayList<Webpage> children = new ArrayList<>();

    public Webpage(String url, int depth) {
        this.url = url;
        this.remainingDepth = depth - 1;

        pageUrlLog.add(url);
    }

    public void runOnThreadPool(ThreadPoolExecutor threadPool) {
        threadPool.execute(() -> {
            try {
                long startTime = System.nanoTime();
                pageDocument = Jsoup.connect(url).userAgent(REQUEST_USER_AGENT).get();
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
        out.println(offset + url);
        if(error != null) {
            System.out.println(offset + "Error: " + error.getMessage());
            return;
        }

        double bytesPerSecond = pageSize / (loadTimeInNanos / 1e9);

        out.println(offset + "Title: \t" + pageTitle);
        out.println(offset + "Links: \t\t" + links.size());
        out.println(offset + "Images: \t" + images.size());
        out.println(offset + "Videos: \t" + videos.size());
        out.println(offset + "Wordcount: \t" + wordCount);
        out.println(offset + String.format("Loading: \t%s in %.2fms ( @ %s/s)", FormattingUtil.readableFileSize(pageSize), (loadTimeInNanos / 1e6), FormattingUtil.readableFileSize(bytesPerSecond)));
        for(Webpage child : children)
            child.printWithChildren(offset + "\t", out);
    }

    private void createChildrenFromPagelinks() throws MalformedURLException {
        for(Element link : links) {
            String rawLink = link.attr("href");
            if(rawLink.equals("#") || rawLink.equals("/") || rawLink.equals("./") || rawLink.startsWith("javascript:"))
                continue;

            String childUrl = new URL(new URL(url), rawLink).toString();
            if(!(pageUrlLog.contains(childUrl) && Main.shouldOmitDuplicates())) {
                Webpage child = new Webpage(childUrl, remainingDepth);
                children.add(child);
            }

            if (children.size() >= Main.getMaxLinksPerPage())
                break;
        }
    }

    private void freePageDocumentMemory() {
        pageDocument = null;
        System.gc();
    }
}
