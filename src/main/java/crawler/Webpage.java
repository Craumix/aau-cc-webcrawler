package crawler;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Webpage {
    private String url;
    private int remainingDepth;

    private Document pageDocument;
    private Elements links, images, videos;
    private int wordCount;
    private long pageSize;
    private String pageTitle;
    private Exception error;

    private Thread pageProcessingThread;

    private ArrayList<Webpage> children = new ArrayList<>();

    public Webpage(String url, int remainingDepth) {
        this.url = url;
        this.remainingDepth = remainingDepth;

        pageProcessingThread = new Thread(() -> {
            try {
                pageDocument = Jsoup.connect(url).get();
                pageTitle = pageDocument.title();
                links = pageDocument.select("a[href]");
                images = pageDocument.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
                videos = pageDocument.select("video");
                wordCount = pageDocument.text().split(" ").length;
                pageSize = pageDocument.html().getBytes(StandardCharsets.UTF_8).length;
                if(remainingDepth > 0)
                    createChildrenFromPagelinks();
            } catch (Exception e) {
                error = e;
            }
        });
        pageProcessingThread.start();
    }

    public void printWithChildren() {
        printWithChildren("");
    }
    public void printWithChildren(String offset) {
        System.out.println(offset + url + " - " + pageTitle);
        if(error != null) {
            System.out.println(offset + "Error: " + error.getMessage());
            return;
        }
        System.out.println(offset + "# of Links: \t" + links.size());
        System.out.println(offset + "# of Images: \t" + images.size());
        System.out.println(offset + "# of Videos: \t" + videos.size());
        System.out.println(offset + "Wordcount: \t" + wordCount);
        System.out.println(offset + "Pagesize: \t" + readableFileSize(pageSize));
        for(Webpage child : children)
            child.printWithChildren(offset + "  ");
    }

    private void createChildrenFromPagelinks() throws MalformedURLException {
        for(Element link : links) {
            String rawLink = link.attr("href");
            if(rawLink.equals("#") || rawLink.equals("/") || rawLink.equals("./") || rawLink.startsWith("javascript:"))
                continue;
            String childUrl = new URL(new URL(url), rawLink).toString();
            Webpage child = new Webpage(childUrl, remainingDepth - 1);
            children.add(child);
        }

        for (Webpage child : children)
            child.waitForProcessing();
    }

    public void waitForProcessing() {
        try {
            pageProcessingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
