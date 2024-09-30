package shticell.client.util.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dto.SheetDto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.function.Consumer;

public class HttpClientUtil {
    private final static CookieManager CookieManager = new CookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(CookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        CookieManager.setLogData(logConsumer);
    }

    public static void removeCookiesOf(String domain) {
        CookieManager.removeCookiesOf(domain);
    }

    public static void runAsync(Request request, Callback callback) {
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static SheetDto extractSheetFromResponse(String response) {
        Gson gson = new Gson();
        try {
            // Parse the JSON response into a SheetDto object
            return gson.fromJson(response, SheetDto.class);
        } catch (JsonSyntaxException e) {
            // Handle the case where the JSON is not valid
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
