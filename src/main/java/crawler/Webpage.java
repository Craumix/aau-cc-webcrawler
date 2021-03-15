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

    private static CopyOnWriteArrayList<String> pageUrlLog = new CopyOnWriteArrayList<>();

    private String url;
    private int remainingDepth;

    private Document pageDocument;
    private Elements links, images, videos;
    private int wordCount;
    private long pageSize;
    private String pageTitle;
    private Exception error;

    private ArrayList<Webpage> children = new ArrayList<>();

    public Webpage(String url, int remainingDepth) {
        this.url = url;
        this.remainingDepth = remainingDepth;

        pageUrlLog.add(url);
    }

    public void runOnThreadPool(ThreadPoolExecutor threadPool) {
        threadPool.execute(() -> {
            try {
                pageDocument = Jsoup.connect(url).get();
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
        out.println(offset + "Title: \t" + pageTitle);
        out.println(offset + "# of Links: \t" + links.size());
        out.println(offset + "# of Images: \t" + images.size());
        out.println(offset + "# of Videos: \t" + videos.size());
        out.println(offset + "Wordcount: \t" + wordCount);
        out.println(offset + "Pagesize: \t" + FormattingUtil.readableFileSize(pageSize));
        for(Webpage child : children)
            child.printWithChildren(offset + "  ", out);
    }

    private void createChildrenFromPagelinks() throws MalformedURLException {
        for(int i = 0; i < (links.size()); i++) { //TODO max links
            String rawLink = links.get(i).attr("href");
            if(rawLink.equals("#") || rawLink.equals("/") || rawLink.equals("./") || rawLink.startsWith("javascript:"))
                continue;
            String childUrl = new URL(new URL(url), rawLink).toString();
            if(!(pageUrlLog.contains(childUrl) && Main.shouldOmitDuplicates())) {
                Webpage child = new Webpage(childUrl, remainingDepth - 1);
                children.add(child);
            }
        }
    }

    private void freePageDocumentMemory() {
        pageDocument = null;
        System.gc();
    }
}
