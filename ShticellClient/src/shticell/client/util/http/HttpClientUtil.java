package shticell.client.util.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpClientUtil {
    private final static CookieManager CookieManager = new CookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(CookieManager)
                    .followRedirects(false)
                    .build();

    // Method to remove cookies for a specified domain
    public static void removeCookiesOf(String domain) {
        CookieManager.removeCookiesOf(domain);
    }

    // Asynchronously execute an HTTP request using a callback
    public static void runAsync(Request request, Callback callback) {
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    // Overloaded method to create a request from a URL and execute it
    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    // Extracts a SheetDto object from a JSON response
    public static SheetDto extractSheetFromResponseBody(String response) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(response, SheetDto.class);
        } catch (JsonSyntaxException e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }

    // Shuts down the HTTP client and cleans up resources
    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }

    // Displays an error alert in the UI
    public static void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setMinHeight(Region.USE_PREF_SIZE);
            dialogPane.setMinWidth(Region.USE_PREF_SIZE);

            alert.showAndWait();
        });
    }

    // Displays an informational alert in the UI
    public static void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }
}
