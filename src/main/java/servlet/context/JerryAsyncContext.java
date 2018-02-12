package servlet.context;

import servlet.request.JerryServletRequest;
import servlet.response.JerryServletResponse;

import javax.servlet.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JerryAsyncContext implements AsyncContext {

    private JerryServletRequest servletRequest;
    private JerryServletResponse servletResponse;

    private boolean originalRequestAndResponse;

    private boolean completed;

    private long timeout;

    private Map<AsyncListener, RequestResponsePair> listeners = new LinkedHashMap<>();

    public JerryAsyncContext(JerryServletRequest request, JerryServletResponse response){
        servletRequest = request;
        servletResponse = response;
    }

    public JerryAsyncContext(JerryServletRequest request, JerryServletResponse response, boolean originalRequestAndResponse){
        servletRequest = request;
        servletResponse = response;
        this.originalRequestAndResponse = originalRequestAndResponse;
    }

    @Override
    public ServletRequest getRequest() {
        return servletRequest;
    }

    @Override
    public ServletResponse getResponse() {
        return servletResponse;
    }

    @Override
    public boolean hasOriginalRequestAndResponse() {
        return originalRequestAndResponse;
    }

    public void setOriginalRequestAndResponse(boolean originalRequestAndResponse) {
        this.originalRequestAndResponse = originalRequestAndResponse;
    }

    @Override
    public void dispatch() {
        try {
            servletRequest.getRequestDispatcher(servletRequest.getPath()).forward(servletRequest, servletResponse);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatch(String path) {
        try {
            servletRequest.getRequestDispatcher(path).forward(servletRequest, servletResponse);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatch(ServletContext context, String path) {
        try {
            context.getRequestDispatcher(path).forward(servletRequest, servletResponse);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        for (AsyncListener listener : listeners.keySet()){
            RequestResponsePair pair = listeners.get(listener);
            try {
                listener.onComplete(new AsyncEvent(this, pair.servletRequest, pair.servletResponse));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        completed = true;
    }

    @Override
    public void start(Runnable run) {
        new Thread(run).start();
    }

    @Override
    public void addListener(AsyncListener listener) {
        listeners.put(listener, new RequestResponsePair(servletRequest, servletResponse));
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
        listeners.put(listener, new RequestResponsePair(servletRequest, servletResponse));
    }

    @Override
    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        T result = null;
        try {
            result = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    private class RequestResponsePair {

        private ServletRequest servletRequest;
        private ServletResponse servletResponse;

        private RequestResponsePair(ServletRequest servletRequest, ServletResponse servletResponse) {
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
        }


        public ServletRequest getServletRequest() {
            return servletRequest;
        }

        public ServletResponse getServletResponse() {
            return servletResponse;
        }
    }

}
