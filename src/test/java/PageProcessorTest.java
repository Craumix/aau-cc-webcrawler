import mocks.LocalFileFetcher;
import crawler.webpage.AsyncWebpageLoader;
import crawler.webpage.Webpage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class PageProcessorTest {

    Webpage rootPage;

    @BeforeAll
    static void setupBefore() {
        Webpage.setFetcher(new LocalFileFetcher());
    }

    @BeforeEach
    void setupBeforeEach() throws URISyntaxException {
        rootPage = new Webpage("3-children");
    }

    @ParameterizedTest
    @DisplayName("Test if the root page is loaded correctly")
    @ValueSource(ints = {1, 50, 100})
    void testLoadingRootPage(int threadCount) throws InterruptedException {
        AsyncWebpageLoader webpageProcessor = new AsyncWebpageLoader(rootPage, 1, threadCount);
        webpageProcessor.loadPagesRecursively();

        assertNotNull(rootPage.getPageTitle());
    }

    @ParameterizedTest
    @DisplayName("Tests if children get loaded with the root page")
    @ValueSource(ints = {1, 50, 100})
    void testLoadingChildren(int threadCount) throws InterruptedException {
        AsyncWebpageLoader webpageProcessor = new AsyncWebpageLoader(rootPage, 10, threadCount);
        webpageProcessor.loadPagesRecursively();

        for (int index=0; index<3; index++)
            assertNotNull(rootPage.getChildren().get(index).getPageTitle());
    }

    @ParameterizedTest
    @DisplayName("Test if pages stop being loaded when the depth is reached")
    @ValueSource(ints = {1, 50, 100})
    void testDepth(int threadCount) throws InterruptedException {
        int[] depthsToTest = {1, 2};

        for (int depth : depthsToTest) {
            AsyncWebpageLoader webpageProcessor = new AsyncWebpageLoader(rootPage, depth, threadCount);
            webpageProcessor.loadPagesRecursively();

            // get 4-links.html
            Webpage childPage = rootPage.getChildren().get(2);
            for (int j=1; j < depth; j++)
                childPage = childPage.getChildren().get(0);

            assertNull(childPage.getPageTitle());
        }
    }

    @Test
    @DisplayName("Test if the root page doesn't get loaded with a depth of zero")
    void testRootPageNotLoading() throws InterruptedException {
        AsyncWebpageLoader webpageProcessor = new AsyncWebpageLoader(rootPage, 0, 1);
        webpageProcessor.loadPagesRecursively();

        assertNull(rootPage.getPageTitle());
    }
}
