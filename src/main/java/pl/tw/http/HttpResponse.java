package pl.tw.http;

public class HttpResponse<T> {

    T object;
    int status;
    String error;

    private HttpResponse(T object, int status, String error) {

        this.object = object;
        this.status = status;
        this.error = error;
    }

    public T getObject() {
        return object;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public boolean isError() {
        return status != 200;
    }

    public static <T> HttpResponse<T> ok(T object) {
        return new HttpResponse<>(object, 200, "");
    }

    public static <T> HttpResponse<T> error(int status, String error) {
        return new HttpResponse<>(null, status, error);
    }
}
