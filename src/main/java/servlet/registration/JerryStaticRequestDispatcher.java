package servlet.registration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

public class JerryStaticRequestDispatcher implements RequestDispatcher {

    private URL resource;

    public JerryStaticRequestDispatcher(URL resource){
        this.resource = resource;
    }

    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        include(request, response);
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        InputStream in = null;
        try {
            OutputStream out = response.getOutputStream();
            in = new FileInputStream(new File(resource.toURI()));
            BufferedInputStream bufferedInputStream = new BufferedInputStream(in, 1024);

            while (bufferedInputStream.available() > 0) {
                out.write(bufferedInputStream.read());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
    }
}
