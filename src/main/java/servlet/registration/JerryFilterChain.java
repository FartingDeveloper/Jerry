package servlet.registration;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;

public class JerryFilterChain implements FilterChain {

    private Servlet servlet;
    private List<Filter> filters;
    private int index;

    public JerryFilterChain(Servlet servlet, List<Filter> filters){
        this.servlet = servlet;
        this.filters = filters;
        index = 0;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if(index < filters.size()){
            filters.get(index++).doFilter(request, response, this);
        }
        else if(index == filters.size()){
            servlet.service(request, response);
        }
        else{
            throw new ServletException();
        }
    }

}
