package crawler;

import com.panforge.robotstxt.RobotsTxt;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CrawlerLoadFilter implements UriLoadFilter {

    private static final CopyOnWriteArrayList<URI> uriLog = new CopyOnWriteArrayList<>();
    private static final ConcurrentHashMap<String, RobotsTxt> hostRobotsMap = new ConcurrentHashMap<>();

    private final boolean omitDuplicates, useRobotsTxt;

    public CrawlerLoadFilter(boolean omitDuplicates, boolean useRobotsTxt) {
        this.omitDuplicates = omitDuplicates;
        this.useRobotsTxt = useRobotsTxt;
    }

    @Override
    public boolean uriShouldBeLoaded(URI uri) {
        if(!isHttpScheme(uri))
            return false;

        if(omitDuplicates) {
            if(uriLog.contains(uri)) {
                return false;
            }else {
                uriLog.add(uri);
            }
        }

        if(useRobotsTxt && !allowedByRobotsTxt(uri))
            return false;

        return true;
    }

    private boolean isHttpScheme(URI uri) {
        return uri.getScheme().startsWith("http");
    }

    private boolean allowedByRobotsTxt(URI uri) {
        if(!hostRobotsMap.containsKey(uri.getHost()))
            if(!loadRobotsTxtForHost(uri.getHost()))
                //Allow uri if robots.txt fails to load
                return true;

        return hostRobotsMap.get(uri.getHost()).query(null, uri.getPath());
    }

    private boolean loadRobotsTxtForHost(String host) {
        try {
            URL robotsUrl = new URL("http://" + host + "/robots.txt");
            hostRobotsMap.put(host, RobotsTxt.read(robotsUrl.openStream()));
            return true;
        }catch (IOException e) {
            return false;
        }
    }
}
