package crawler;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadingUtil {
    public static void waitUntilPoolEmptyAndTerminated(ThreadPoolExecutor threadPool) throws InterruptedException {
        while (threadPool.getActiveCount() > 0)
            Thread.sleep(50);

        threadPool.shutdown();
        threadPool.awaitTermination(30, TimeUnit.SECONDS);
    }
}
