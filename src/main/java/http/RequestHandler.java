package http;

public interface RequestHandler {

    void handle(HttpRequest request, HttpResponse response);

}
