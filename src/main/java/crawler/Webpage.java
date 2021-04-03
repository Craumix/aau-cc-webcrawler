package crawler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Webpage {

    private static String userAgent = "Java/" + System.getProperty("java.version");
    private static int maxChildrenPerPage = Integer.MAX_VALUE;

    private final URI pageURI;

    private Elements links, images, videos;
    private int wordCount;
    private boolean loadAttempted = false;
    private long pageSize, loadTimeInNanos;
    private String pageTitle;
    private byte[] pageHash;

    private Exception error;

    private ArrayList<Webpage> children = new ArrayList<>();

    private final WebpageLoadFilter loadFilter;

    public Webpage(String pageURI) throws URISyntaxException {
        this(new URI(pageURI), null);
    }
    public Webpage(URI pageURI) {
        this(pageURI, null);
    }
    public Webpage(String pageURI, WebpageLoadFilter loadFilter) throws URISyntaxException {
        this(new URI(pageURI), loadFilter);
    }
    public Webpage(URI pageURI, WebpageLoadFilter loadFilter) {
        this.pageURI = pageURI;
        this.loadFilter = loadFilter;
    }

    public JSONObject asJSONObject() {
        JSONObject thisJSON = new JSONObject() {
            /**
             * https://stackoverflow.com/a/62476486
             * changes the value of JSONObject.map to a LinkedHashMap in order to maintain
             * order of keys.
             */
            @Override
            public JSONObject put(String key, Object value) throws JSONException {
                try {
                    Field map = JSONObject.class.getDeclaredField("map");
                    map.setAccessible(true);
                    Object mapValue = map.get(this);
                    if (!(mapValue instanceof LinkedHashMap)) {
                        map.set(this, new LinkedHashMap<>());
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return super.put(key, value);
            }
        };

        thisJSON.put("url", pageURI.toString());
        if(error != null) {
            thisJSON.put("error", error.getMessage());
        }else {
            thisJSON.put("title", pageTitle);
            thisJSON.put("linkCount", links.size());
            thisJSON.put("imageCount", images.size());
            thisJSON.put("videoCount", videos.size());
            thisJSON.put("wordCount", wordCount);
            thisJSON.put("pageSize", pageSize);
            thisJSON.put("nanoLoadTime", loadTimeInNanos);
            thisJSON.put("pageHash", getPageHashString());

            JSONArray childrenArr = new JSONArray();
            for(Webpage child : children) {
                if(!child.loadWasAttempted())
                    break;
                childrenArr.put(child.asJSONObject());
            }

            if(childrenArr.length() > 0)
                thisJSON.put("children", childrenArr);
        }

        return thisJSON;
    }

    public void loadPage() {
        try {
            loadAttempted = true;

            long startTime = System.nanoTime();
            Document pageDocument = Jsoup.connect(pageURI.toString()).userAgent(userAgent).get();
            loadTimeInNanos = System.nanoTime() - startTime;

            pageTitle = pageDocument.title();
            links = pageDocument.select("a[href]");
            images = pageDocument.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
            videos = pageDocument.select("video");
            wordCount = pageDocument.body().text().split(" ").length;
            pageSize = pageDocument.html().getBytes(StandardCharsets.UTF_8).length;

            MessageDigest md = MessageDigest.getInstance("MD5");
            pageHash = md.digest(pageDocument.html().getBytes(StandardCharsets.UTF_8));

            initializeChildren();
        } catch (IOException | NoSuchAlgorithmException e) {
            error = e;
        }
    }

    private void initializeChildren() {
        for(Element link : links) {
            URI rawURI;
            try {
                rawURI = new URI(link.attr("href"));
            } catch (URISyntaxException e) {
                continue;
            }

            URI resolvedChildURI = pageURI.resolve(rawURI);
            if(resolvedChildURI.equals(pageURI))
                continue;
            if(loadFilter == null || loadFilter.webpageShouldBeLoaded(resolvedChildURI)) {
                Webpage child = new Webpage(resolvedChildURI, loadFilter);
                children.add(child);
            }

            if (children.size() >= maxChildrenPerPage)
                break;
        }
    }

    public boolean loadWasAttempted() {
        return loadAttempted;
    }

    public String getPageHashString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : pageHash) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


    public static void setRequestUserAgent(String agent) {
        userAgent = agent;
    }

    public static void setMaxChildrenPerPage(int count) {
        maxChildrenPerPage = count;
    }


    public ArrayList<Webpage> getChildren() {
        return children;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public long getPageSize() {
        return pageSize;
    }

    public Elements getLinks() {
        return links;
    }

    public Elements getImages() {
        return images;
    }

    public static String getUserAgent() {
        return userAgent;
    }
}
