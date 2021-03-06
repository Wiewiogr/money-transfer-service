package pl.tw.integration;

import com.google.gson.Gson;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.UUID;

public class HttpUtils {

    private static Gson gson = new Gson();

    public static <T> T get(String url, Class<T> clazz) throws IOException {
        String result = Request.Get(url).execute().returnContent().asString();
        return gson.fromJson(result, clazz);
    }

    public static UUID post(String url, Object body) throws IOException {
        String result = Request.Post(url)
                .bodyString(gson.toJson(body), ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString();
        return gson.fromJson(result, UUID.class);
    }
}
