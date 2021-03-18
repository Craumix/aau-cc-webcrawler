package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class WebpageProcessor {

    private final String REQUEST_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";
    private static final CopyOnWriteArrayList<URI> pageUrlLog = new CopyOnWriteArrayList<>();

    private final Webpage webpage;
    private final int remainingDepth;

    public WebpageProcessor(Webpage webpage, int depth) {
        this.webpage = webpage;
        this.remainingDepth = depth - 1;
    }

    public void runOnThreadPool(ThreadPoolExecutor threadPool) {
        threadPool.execute(() -> {
            try {
                fillWebpage();
                recurse(threadPool);
            } catch (Exception e) {
                webpage.setError(e);
            }
        });
    }

    private void createChildrenFromPagelinks() throws URISyntaxException {
        for(Element link : webpage.getLinks()) {
            URI rawURI = new URI(link.attr("href"));
            //if(rawLink.equals("#") || rawLink.equals("/") || rawLink.equals("./") || rawLink.startsWith("javascript:"))
            //    continue;

            URI resolvedChildURI = webpage.getPageURI().resolve(rawURI);
            if(resolvedChildURI.equals(webpage.getPageURI()))
                continue;
            if(!(pageUrlLog.contains(resolvedChildURI) && Main.shouldOmitDuplicates())) {
                Webpage child = new Webpage(resolvedChildURI);
                pageUrlLog.add(child.getPageURI());
                webpage.addChild(child);
            }

            if (webpage.getChildren().size() >= Main.getMaxLinksPerPage())
                break;
        }
    }

    private void fillWebpage() throws IOException {
        long startTime = System.nanoTime();
        Document pageDocument = Jsoup.connect(webpage.getPageURI().toString()).userAgent(REQUEST_USER_AGENT).get();
        webpage.setLoadTimeInNanos(System.nanoTime() - startTime);

        webpage.setPageTitle(pageDocument.title());
        webpage.setLinks(pageDocument.select("a[href]"));
        webpage.setImages(pageDocument.select("img[src~=(?i)\\.(png|jpe?g|gif)]"));
        webpage.setVideos(pageDocument.select("video"));
        webpage.setWordCount(pageDocument.text().split(" ").length);
        webpage.setPageSize(pageDocument.html().getBytes(StandardCharsets.UTF_8).length);
    }

    private void recurse(ThreadPoolExecutor threadPool) throws URISyntaxException {
        if(remainingDepth > 0) {
            createChildrenFromPagelinks();
            for (Webpage child : webpage.getChildren()) {
                new WebpageProcessor(child, remainingDepth).runOnThreadPool(threadPool);
            }
        }
    }
}
