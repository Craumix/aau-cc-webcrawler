package crawler;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncWebpageProcessor {

    private final Webpage rootPage;
    private final int remainingDepth;
    private final ThreadPoolExecutor threadPool;

    public AsyncWebpageProcessor(Webpage rootPage, int depth, int threadCount) {
        this.rootPage = rootPage;
        this.remainingDepth = depth - 1;
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
    }

    public void loadPagesRecursively() throws InterruptedException {
        threadPool.execute(() -> {
            rootPage.loadPage();
            loadChildren(rootPage.getChildren(), remainingDepth);
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
