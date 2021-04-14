package crawler;

import com.panforge.robotstxt.RobotsTxt;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class RobotLoadFilter implements WebpageLoadFilter{

    private static final ConcurrentHashMap<String, RobotsTxt> hostRobotsMap = new ConcurrentHashMap<>();

    @Override
    public boolean webpageShouldBeLoaded(URI uri) {
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
