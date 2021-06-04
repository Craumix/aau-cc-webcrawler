package crawler.webpage;

import crawler.util.Util;
import crawler.webpage.fetcher.Fetcher;
import crawler.webpage.fetcher.JsoupFetcher;
import crawler.webpage.filter.WebpageLoadFilter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;

public class Webpage {
    private static String userAgent = "Java/" + System.getProperty("java.version");
    private static int maxChildrenPerPage = Integer.MAX_VALUE;

    private static Fetcher fetcher = new JsoupFetcher();

    private final URI pageURI;

    private Elements links, images, videos;
    private int wordCount;
    private boolean loadAttempted, loadPreventedByFilter;
    private long pageSize, loadTimeInNanos;
    private String pageTitle;
    private byte[] pageHash;

    private Exception error;

    private final ArrayList<Webpage> children = new ArrayList<>();
    private final ArrayList<WebpageLoadFilter> loadFilters ;

    /**
     * String will be parsed to URI throws an exception if string is not a valid URI
     *
     * @param pageURI   the URI of the Website to load
     * @throws URISyntaxException
     */
    public Webpage(String pageURI) throws URISyntaxException {
        this(new URI(pageURI));
    }

    /**
     * @param pageURI   the URI of the Website to load
     */
    public Webpage(URI pageURI) {
        this(pageURI, new ArrayList<>());
    }

    /**
     * String will be parsed to URI throws an exception if string is not a valid URI
     *
     * @param pageURI   the URI of the Website to load
     * @param loadFilters   a list of filters to check before loading
     * @throws URISyntaxException
     */
    public Webpage(String pageURI, ArrayList<WebpageLoadFilter> loadFilters) throws URISyntaxException {
        this(new URI(pageURI), loadFilters);
    }

    /**
     * @param pageURI       the URI of the Website to load
     * @param loadFilters   a list of filters to check before loading
     */
    public Webpage(URI pageURI, ArrayList<WebpageLoadFilter> loadFilters) {
        this.pageURI = pageURI;
        this.loadFilters = loadFilters;
    }

    /**
     * Returns a JSONObject that contains:
     * URL, Title, Link count, Image count, Video count, Word count, Page Size in bytes,
     * Load time in nanoseconds, Page hash (MD5 using the whole page).
     *
     * If and Exception occurred while loading the page:
     * URL, Error
     *
     * @return  a JSONObject representing the Information gathered from the Website
     * @see     JSONObject
     */
    public JSONObject asJSONObject() {
        JSONObject thisAsJSON = Util.makeJSONObjectWithOrderedKeys();

        fillJSONObjectWithPageContent(thisAsJSON);

        if (!thisAsJSON.has("error"))
            fillJSONObjectWithChildren(thisAsJSON);

        return thisAsJSON;
    }

    /**
     * Fills the given JSONObject. <br>
     * with error <br>
     * - url, error <br>
     * without error <br>
     * - url, title, lnkCount, imageCount, videoCount, wordCount, pageSize, nanoLoadTime, pageHash
     *
     * @param jsonToFill Webpage as JSONObject to be filled
     */
    private void fillJSONObjectWithPageContent(JSONObject jsonToFill) {
        jsonToFill.put("url", pageURI.toString());

        if (error != null) {
            jsonToFill.put("error", error.getMessage());
            return;
        }

        jsonToFill.put("title", pageTitle);
        jsonToFill.put("linkCount", links.size());
        jsonToFill.put("imageCount", images.size());
        jsonToFill.put("videoCount", videos.size());
        jsonToFill.put("wordCount", wordCount);
        jsonToFill.put("pageSize", pageSize);
        jsonToFill.put("nanoLoadTime", loadTimeInNanos);
        jsonToFill.put("pageHash", getPageHashString());
    }

    /**
     * Takes the children of the Webpage and puts them as a JSON into jsonToFill
     *
     * @param jsonToFill Webpage as JSONObject to be filled
     */
    private void fillJSONObjectWithChildren(JSONObject jsonToFill) {
        JSONArray childrenArr = new JSONArray();
        for (Webpage child : children)
            if (child.loadingWasAttempted() && !child.loadingWasPreventedByFilter())
                childrenArr.put(child.asJSONObject());

        if (childrenArr.length() > 0)
            jsonToFill.put("children", childrenArr);
    }

    /**
     * Downloads the Website specified in the constructor
     * and performs various analyses on it.
     * This method may block for an extended amount of time.
     */
    public void loadPage() {
        loadAttempted = true;

        for (WebpageLoadFilter filter : loadFilters) {
            if (!filter.webpageShouldBeLoaded(pageURI)) {
                loadPreventedByFilter = true;
                return;
            }
        }

        loadWebpageValuesIntoVariables();

        if (error == null)
            initializeChildren();
    }

    /**
     * Queries Values from the url of this Webpage and loads them into the corresponding variables
     */
    private void loadWebpageValuesIntoVariables(){
        try {
            long startTime = System.nanoTime();
            Document pageDocument = fetcher.fetchDocument(pageURI.toString(), userAgent);
            loadTimeInNanos = System.nanoTime() - startTime;

            pageTitle = pageDocument.title();
            links = pageDocument.select("a[href]");
            images = pageDocument.select("img[src~=(?i)\\.(png|jpe?g|gif|svg)]");
            videos = pageDocument.select("video");
            wordCount = pageDocument.body().text().split(" ").length;
            pageSize = pageDocument.html().getBytes(StandardCharsets.UTF_8).length;

            MessageDigest md = MessageDigest.getInstance("MD5");
            pageHash = md.digest(pageDocument.html().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            error = e;
        }

    }

    /**
     * Initializes a child for every link in the Webpage,
     * given that they don't have the same URL as this Webpage and are a valid http Url.
     * Only creates a certain amount of children if setMaxChildrenPerPage() was used.
     */
    private void initializeChildren() {
        for (Element link : links) {
            URI rawURI;
            try {
                rawURI = new URI(link.attr("href"));
            } catch (URISyntaxException e) {
                continue;
            }

            URI resolvedChildURI = pageURI.resolve(rawURI);
            if (resolvedChildURI.equals(pageURI))
                continue;
            if (!Util.isValidHttpUrl(resolvedChildURI))
                continue;

            Webpage child = new Webpage(resolvedChildURI, loadFilters);
            children.add(child);

            if (children.size() >= maxChildrenPerPage)
                break;
        }
    }

    /**
     * Returns true if the loadPage() method of this object was called.
     *
     * @return  true if loadPage() was called
     */
    public boolean loadingWasAttempted() {
        return loadAttempted;
    }

    /**
     * Returns true if loadPage() was called
     * and was then aborted because the Webpage was rejected by a filter.
     *
     * @return  true if load was aborted by filter
     */
    public boolean loadingWasPreventedByFilter() {
        return loadPreventedByFilter;
    }

    /**
     * Returns the Hex encoded MD5 hash of this page.
     *
     * @return  Hex MD5 of this page
     */
    public String getPageHashString() {
        StringBuilder sb = new StringBuilder();
        for (byte b : pageHash)
            sb.append(String.format("%02X", b));

        return sb.toString();
    }

    /**
     * Sets the UserAgent header for all further requests in loadPage()
     *
     * @param agent String to send in the UserAgent header
     */
    public static void setRequestUserAgent(String agent) {
        Webpage.userAgent = agent;
    }

    /**
     * Sets the maximal amount of children the initializeChildren() method should create
     *
     * @param count max amount of children
     */
    public static void setMaxChildrenPerPage(int count) {
        maxChildrenPerPage = count;
    }

    public static void setFetcher(Fetcher newFetcher) {
        Webpage.fetcher = newFetcher;
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
        return Webpage.userAgent;
    }

    public static int getMaxChildrenPerPage() {
        return maxChildrenPerPage;
    }

    public ArrayList<WebpageLoadFilter> getLoadFilters() {
        return loadFilters;
    }


}
