package crawler;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Webpage {
    private String url;
    private int remainingDepth;

    private Document pageDocument;
    private Elements links, images;
    private int wordCount;
    private String pageTitle;
    private int pageCode = 200;

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
                wordCount = pageDocument.text().split(" ").length;

                if(remainingDepth > 0)
                    createChildrenFromPagelinks();

                System.out.println(url);
                System.out.println("# of Links: " + links.size());
                System.out.println("# of Images " + images.size());
                System.out.println("Wordcount: " + wordCount);
            } catch (HttpStatusException se) {
                pageCode = se.getStatusCode();
                se.printStackTrace();
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
        pageProcessingThread.start();
    }

    private void createChildrenFromPagelinks() throws MalformedURLException {
        for(Element link : links) {
            String rawLink = link.attr("href");
            if(rawLink.equals("#") || rawLink.equals("/") || rawLink.equals("./"))
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
}
