import crawler.webpage.AsyncWebpageLoader;
import crawler.webpage.Webpage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class PageProcessorTest {

    @Test
    @DisplayName("Tests the AsyncPageProcessor by loading google.com with a depth of 2")
    void testPageProcessorWithValidInputs() throws URISyntaxException, InterruptedException {
        Webpage rootPage = new Webpage("https://google.com");
        AsyncWebpageLoader webpageProcessor = new AsyncWebpageLoader(rootPage, 2, 1);
        webpageProcessor.loadPagesRecursively();

        assertFalse(rootPage.getPageTitle().equals(""));
        assertFalse(rootPage.getChildren().get(0).getPageTitle().equals(""));
    }
}
