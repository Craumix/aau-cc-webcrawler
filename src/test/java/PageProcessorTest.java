import connector.LocalFileFetcher;
import crawler.webpage.AsyncWebpageLoader;
import crawler.webpage.Webpage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class PageProcessorTest {

    @Test
    @DisplayName("Tests the AsyncPageProcessor by loading a site with a depth of 2")
    void testPageProcessorWithValidInputs() throws URISyntaxException, InterruptedException {
        Webpage.setConnector(new LocalFileFetcher());
        Webpage rootPage = new Webpage("3-children");
        AsyncWebpageLoader webpageProcessor = new AsyncWebpageLoader(rootPage, 2, 1);
        webpageProcessor.loadPagesRecursively();

        assertNotEquals("", rootPage.getPageTitle());
        assertNotEquals("", rootPage.getChildren().get(0).getPageTitle());
    }
}
