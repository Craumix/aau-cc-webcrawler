package crawler.webpage.filter;

import com.panforge.robotstxt.RobotsTxt;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class RobotsLoadFilter implements WebpageLoadFilter{

    private static final ConcurrentHashMap<String, RobotsTxt> hostRobotsMap = new ConcurrentHashMap<>();

    /**
     * Checks if the URI should be loaded according to the severs /robots.txt file.
     * Tries to used a cached version, if none is available tries to load /robots.txt from server.
     * If the /robots.txt is not found and can't be loaded, this will return true.
     *
     * @param uri   the URI to checked against /robots.txt
     * @return      weather the URI is allowed by the /robots.txt
     */
    @Override
    public boolean webpageShouldBeLoaded(URI uri) {
        if (!hostRobotsMap.containsKey(uri.getHost()))
            if (!loadRobotsTxtForHost(uri.getHost()))
                //Allow uri if robots.txt fails to load
                return true;

        return hostRobotsMap.get(uri.getHost()).query(null, uri.getPath());
    }

    /**
     * Load the /robots.txt from a specified host into the cache.
     * Returns true if /robots.txt was loaded.
     *
     * @param host  the host to load the /robots.txt from
     * @return      true if /robots.txt could be loaded
     */
    private boolean loadRobotsTxtForHost(String host) {
        try {
            URL robotsUrl = new URL("http://" + host + "/robots.txt");
            hostRobotsMap.put(host, RobotsTxt.read(robotsUrl.openStream()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
