import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

public class HttpHandler {

    private final OkHttpClient client = new OkHttpClient();

    // Constructor (optional, if you need configuration)
    public HttpHandler() {
        // You can initialize anything if needed here
    }

    /**
     * Sends JSON data to a specified URL using a POST request.
     * @param url The server URL to send the data to.
     * @param jsonData A JSONObject containing the data to send.
     */
    public void sendData(String url, JSONObject jsonData) {
        // Create request body
        RequestBody body = RequestBody.create(
                jsonData.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // Create the request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                System.err.println("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle successful response
                    System.out.println("Response: " + response.body().string());
                } else {
                    // Handle error response
                    System.err.println("Error: " + response.code() + ", " + response.message());
                }
            }
        });
    }
}
