package servlet.registration;

import javax.servlet.*;
import java.io.IOException;

public class JerryDynamicRequestDispatcher implements RequestDispatcher {

    private Servlet servlet;

    public JerryDynamicRequestDispatcher(Servlet servlet){
        this.servlet = servlet;
    }

    @Override
    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        servlet.service(request, response);
    }

    @Override
    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        response.flushBuffer();
        servlet.service(request, response);
    }
}
