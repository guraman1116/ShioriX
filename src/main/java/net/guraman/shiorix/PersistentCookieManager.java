package net.guraman.shiorix;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public class PersistentCookieManager {

    private static final String COOKIE_FILE = "cookies.dat";
    private final CookieManager cookieManager;

    public PersistentCookieManager() {
        this.cookieManager = new CookieManager();
        CookieHandler.setDefault(this.cookieManager);
    }

    @SuppressWarnings("unchecked")
    public void load() {
        File cookieFile = new File(COOKIE_FILE);
        if (!cookieFile.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookieFile))) {
            CookieStore cookieStore = cookieManager.getCookieStore();
            cookieStore.removeAll(); // Clear existing cookies
            List<SerializableHttpCookie> cookies = (List<SerializableHttpCookie>) ois.readObject();
            for (SerializableHttpCookie cookie : cookies) {
                cookieStore.add(URI.create(cookie.getDomain()), cookie.toHttpCookie());
            }
            System.out.println("Loaded " + cookies.size() + " cookies.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Could not load cookies: " + e.getMessage());
        }
    }

    public void save() {
        File cookieFile = new File(COOKIE_FILE);
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cookieFile))) {
            List<SerializableHttpCookie> serializableCookies = cookies.stream()
                    .map(SerializableHttpCookie::new)
                    .toList();
            oos.writeObject(serializableCookies);
            System.out.println("Saved " + serializableCookies.size() + " cookies.");
        } catch (IOException e) {
            System.err.println("Could not save cookies: " + e.getMessage());
        }
    }
}

// HttpCookie is not serializable, so we need a wrapper
class SerializableHttpCookie implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient HttpCookie cookie;

    // Fields to serialize
    private String name;
    private String value;
    private String comment;
    private String commentURL;
    private boolean toDiscard;
    private String domain;
    private long maxAge;
    private String path;
    private String portlist;
    private boolean secure;
    private boolean httpOnly;
    private int version;

    public SerializableHttpCookie(HttpCookie cookie) {
        this.cookie = cookie;
        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.comment = cookie.getComment();
        this.commentURL = cookie.getCommentURL();
        this.toDiscard = cookie.getDiscard();
        this.domain = cookie.getDomain();
        this.maxAge = cookie.getMaxAge();
        this.path = cookie.getPath();
        this.portlist = cookie.getPortlist();
        this.secure = cookie.getSecure();
        this.httpOnly = cookie.isHttpOnly();
        this.version = cookie.getVersion();
    }

    public HttpCookie toHttpCookie() {
        if (cookie != null) {
            return cookie;
        }
        HttpCookie newCookie = new HttpCookie(name, value);
        newCookie.setComment(comment);
        newCookie.setCommentURL(commentURL);
        newCookie.setDiscard(toDiscard);
        newCookie.setDomain(domain);
        newCookie.setMaxAge(maxAge);
        newCookie.setPath(path);
        newCookie.setPortlist(portlist);
        newCookie.setSecure(secure);
        newCookie.setHttpOnly(httpOnly);
        newCookie.setVersion(version);
        return newCookie;
    }

    public String getDomain() {
        return domain;
    }
}
