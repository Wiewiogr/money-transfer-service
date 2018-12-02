package pl.tw;

public class HttpUtils {

    public static String errorResponse(String error) {
        return "{\"error\":\"" + error + "\"}";
    }

    public static String idResponse(String id) {
        return "{\"id\":\"" + id + "\"}";
    }
}
