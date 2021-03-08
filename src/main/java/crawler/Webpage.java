package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Webpage {
    private String url;
    private int remainingDepth;

    private Document pageDocument;
    private Elements links;

    private boolean doneProcessingWebpage;

    public Webpage(String url, int remainingDepth) {
        this.url = url;
        this.remainingDepth = remainingDepth;

        new Thread(() -> {
            doneProcessingWebpage = false;

            try {
                fetchWebpage();
                extractLinks();
            } catch (IOException e) {
                e.printStackTrace();
            }

            doneProcessingWebpage = true;
        }).start();
    }

    public void fetchWebpage() throws IOException {
        pageDocument = Jsoup.connect(url).get();
    }

    public void extractLinks() {
        links = pageDocument.select("a[href]");
    }

    public boolean doneProcessingWebpage() {
        return doneProcessingWebpage;
    }
}
