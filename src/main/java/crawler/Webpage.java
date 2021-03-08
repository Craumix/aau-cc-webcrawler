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

    private boolean doneProcessingWebpage;

    private ArrayList<Webpage> children = new ArrayList<>();

    public Webpage(String url, int remainingDepth) {
        this.url = url;
        this.remainingDepth = remainingDepth;

        new Thread(() -> {
            doneProcessingWebpage = false;

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

            doneProcessingWebpage = true;
        }).start();
    }

    private void createChildrenFromPagelinks() throws MalformedURLException {
        for(Element link : links) {
            String rawLink = link.attr("href");
            if(rawLink.equals("#") || rawLink.equals("/") || rawLink.equals("./"))
                continue;
            String childUrl = new URL(new URL(url), link.attr("href")).toString();
            Webpage child = new Webpage(childUrl, remainingDepth - 1);
            children.add(child);
        }

        while (true) {
            boolean areDone = true;
            for (Webpage child : children)
                if(!child.doneProcessingWebpage)
                    areDone = false;
            if(areDone)
                break;
        }
    }

    public boolean doneProcessingWebpage() {
        return doneProcessingWebpage;
    }
}
