package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WebpageProcessor {

    private final Webpage rootPage;
    private final int initialDepth;
    private final ThreadPoolExecutor threadPool;

    public WebpageProcessor(Webpage rootPage, int initialDepth, int threadCount) {
        this.rootPage = rootPage;
        this.initialDepth = initialDepth;
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
    }

    public void loadPagesRecursively() throws InterruptedException {
        threadPool.execute(() -> {
            rootPage.loadPage();
            loadChildren(rootPage.getChildren(), initialDepth - 1);
        });

        while (threadPool.getActiveCount() > 0)
            Thread.sleep(50);

        threadPool.shutdown();
        threadPool.awaitTermination(30, TimeUnit.SECONDS);
    }

    private void loadChildren(ArrayList<Webpage> pages, int remainingDepth) {
        if(remainingDepth > 0) {
            for (Webpage page : pages) {
                threadPool.execute(() -> {
                    page.loadPage();
                    loadChildren(page.getChildren(), remainingDepth - 1);
                });
            }
        }
    }
}
