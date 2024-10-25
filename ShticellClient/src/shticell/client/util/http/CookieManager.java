package shticell.client.util.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * CookieManager manages HTTP cookies for OkHttpClient.
 */
public class CookieManager implements CookieJar {
    private final static String CACHE_MANAGER_PREFIX = "    [Cookie Manager] ---> ";
    private Map<String, Map<String, Cookie>> cookies = new HashMap<>();
    private Consumer<String> logData = System.out::println; // For logging cookie operations

    // Loads cookies for a given URL.
    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        String host = httpUrl.host();
        StringBuilder sb = new StringBuilder();
        sb.append(CACHE_MANAGER_PREFIX).append("Fetching cookies for domain: [").append(host).append("]...");
        List<Cookie> cookiesPerDomain = Collections.emptyList();
        synchronized (this) {
            if (cookies.containsKey(host)) {
                cookiesPerDomain = new ArrayList<>(cookies.get(host).values());
            }
        }
        sb.append(" Total of ").append(cookiesPerDomain.size()).append(" cookie(s) will be loaded !");
        logData.accept(sb.toString());
        return cookiesPerDomain;
    }

    //Saves cookies from an HTTP response.
    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> responseCookies) {
        String host = httpUrl.host();
        synchronized (this) {
            Map<String, Cookie> cookiesMap = cookies.computeIfAbsent(host, key -> new HashMap<>());
            responseCookies
                    .stream()
                    .filter(cookie -> !cookiesMap.containsKey(cookie.name())) // Avoid duplicates
                    .forEach(cookie -> {
                        logData.accept(CACHE_MANAGER_PREFIX + "Storing cookie [" + cookie.name() + "] --> [" + cookie.value() + "]");
                        cookiesMap.put(cookie.name(), cookie);
                    });
        }
    }

    // Removes cookies associated with a domain.
    public void removeCookiesOf(String domain) {
        synchronized (this) {
            cookies.remove(domain);
        }
    }
}
