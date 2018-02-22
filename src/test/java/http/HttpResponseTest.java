package http;

public class HttpResponseTest extends HttpMessageTest{

    private HttpResponse response;

    public void init(){
        super.init();
        response = new HttpResponse(null);
    }

}
