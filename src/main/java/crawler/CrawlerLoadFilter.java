package crawler;

import com.panforge.robotstxt.RobotsTxt;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CrawlerLoadFilter implements WebpageLoadFilter {

    private static final CopyOnWriteArrayList<String> urlLog = new CopyOnWriteArrayList<>();
    private static final ConcurrentHashMap<String, RobotsTxt> hostRobotsMap = new ConcurrentHashMap<>();

    private final boolean omitDuplicates, useRobotsTxt;

    public CrawlerLoadFilter(boolean omitDuplicates, boolean useRobotsTxt) {
        this.omitDuplicates = omitDuplicates;
        this.useRobotsTxt = useRobotsTxt;
    }

    @Override
    public boolean webpageShouldBeLoaded(URI uri) {
        if(!Util.isValidHttpUrl(uri))
            return false;

        String dupeCompUri = asCompareUriString(uri);
        if(omitDuplicates)
            if(urlLog.contains(dupeCompUri))
                return false;
            else
                urlLog.add(dupeCompUri);

        if(useRobotsTxt && !allowedByRobotsTxt(uri))
            return false;

        return true;
    }

    private String asCompareUriString(URI uri) {
        StringBuilder sb = new StringBuilder(uri.toString());

        //Strip URI Scheme
        sb.delete(0, uri.getScheme().length() + 3);

        //Remove trailing "#", "/", or "/#"
        if(sb.charAt(sb.length() - 1) == '#')
            sb.deleteCharAt(sb.length() - 1);
        if(sb.charAt(sb.length() - 1) == '/')
            sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
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
