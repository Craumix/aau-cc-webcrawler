import crawler.CrawlerLoadFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FilterTest {

    @Test
    void testDuplicateFilter() {
        CrawlerLoadFilter filter = new CrawlerLoadFilter(true, false);
    }
}
