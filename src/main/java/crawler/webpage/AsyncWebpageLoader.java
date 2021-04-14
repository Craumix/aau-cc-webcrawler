package crawler.webpage;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncWebpageLoader {

    private final Webpage rootPage;
    private final int remainingDepth;
    private final ThreadPoolExecutor threadPool;

    /**
     * @param rootPage      the first page to load
     * @param depth         to which depth to load children of the rootPage
     * @param threadCount   how many threads to use for loading
     */
    public AsyncWebpageLoader(Webpage rootPage, int depth, int threadCount) {
        this.rootPage = rootPage;
        this.remainingDepth = depth - 1;
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Starts loading the pages recursively with the parameters specified int the constructor.
     * This method blocks until the recursive loading process is finished.
     *
     * @throws InterruptedException
     */
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

    /**
     * Method for loading the Webpages children recursively.
     *
     * @param pages             list of pages to load
     * @param remainingDepth    the remaining depth for loading, will only load if > 0
     */
    private void loadChildren(ArrayList<Webpage> pages, int remainingDepth) {
        if (remainingDepth > 0) {
            for (Webpage page : pages) {
                threadPool.execute(() -> {
                    page.loadPage();
                    loadChildren(page.getChildren(), remainingDepth - 1);
                });
            }
        }
    }
}
