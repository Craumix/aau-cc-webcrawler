package crawler.webpage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncWebpageLoader {

    private final ArrayList<Webpage> rootPages;
    private final int depth;
    private final ThreadPoolExecutor threadPool;

    /**
     * @param rootPage      the first page to load
     * @param depth         to which depth to load children of the rootPages
     * @param threadCount   how many threads to use for loading
     */
    public AsyncWebpageLoader(Webpage rootPage, int depth, int threadCount) {
        this(new ArrayList<>(Collections.singletonList(rootPage)), depth, threadCount);
    }

    /**
     * @param rootPages     the first pages to load
     * @param depth         to which depth to load children of the rootPages
     * @param threadCount   how many threads to use for loading
     */
    public AsyncWebpageLoader(ArrayList<Webpage> rootPages, int depth, int threadCount) {
        this.rootPages = rootPages;
        this.depth = depth;
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Starts loading the pages recursively with the parameters specified int the constructor. <br>
     * This method blocks until the recursive loading process is finished.
     *
     * @throws InterruptedException when interrupted
     */
    public void loadPagesRecursivelyAndBlock() throws InterruptedException {
        loadPagesRecursively(rootPages, depth);

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
    private void loadPagesRecursively(ArrayList<Webpage> pages, int remainingDepth) {
        if (remainingDepth < 1)
            return;

        for (Webpage page : pages) {
            threadPool.execute(() -> {
                page.loadPage();
                loadPagesRecursively(page.getChildren(), remainingDepth - 1);
            });
        }
    }
}
