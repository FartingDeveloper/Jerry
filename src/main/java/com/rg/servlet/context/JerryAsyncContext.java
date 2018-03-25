package com.rg.servlet.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.rg.servlet.request.JerryServletRequest;
import com.rg.servlet.response.JerryServletResponse;

import javax.servlet.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class JerryAsyncContext implements AsyncContext {

    @Autowired
    @Qualifier("asyncContextThreadPool")
    private static ScheduledExecutorService threadPool;

    private JerryServletRequest servletRequest;
    private JerryServletResponse servletResponse;

    private String path;

    private boolean originalRequestAndResponse;

    private boolean completed;
    private boolean dispatched;
    private boolean initialized;

    private long timeout;

    private Map<AsyncListener, RequestResponsePair> listeners = new LinkedHashMap<>();

    public JerryAsyncContext(){
        timeout = 30000;
    }

    public void init(JerryServletRequest request, JerryServletResponse response, String path){
        this.servletRequest = request;
        this.servletResponse = response;
        this.path = path;
        initialized = true;
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
        dispatch(path);
    }

    @Override
    public void dispatch(String path) {
        dispatch(servletRequest.getServletContext(), path);
    }

    @Override
    public void dispatch(ServletContext context, String path) {
        try {
            if(completed || (!dispatched && initialized)){
                throw new IllegalStateException();
            }

            dispatched = true;
            context.getRequestDispatcher(path).forward(servletRequest, servletResponse);
            dispatched = false;
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void complete() {
        if(! dispatched && ! completed){
            for (AsyncListener listener : listeners.keySet()){
                RequestResponsePair pair = listeners.get(listener);
                try {
                    listener.onComplete(new AsyncEvent(this, pair.servletRequest, pair.servletResponse));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        servletResponse.getResponse().commit();

        completed = true;
    }

    @Override
    public void start(Runnable run) {
        ScheduledFuture future = threadPool.schedule(run, timeout, TimeUnit.MILLISECONDS);
        threadPool.submit(()->{
            if(future.isCancelled()){
                try{
                    for (AsyncListener listener : listeners.keySet()){
                        RequestResponsePair pair = listeners.get(listener);
                        listener.onTimeout(new AsyncEvent(this, pair.servletRequest, pair.servletResponse));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void addListener(AsyncListener listener) {
        addListener(listener, servletRequest, servletResponse);
    }

    @Override
    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
        if(dispatched){
            throw new IllegalStateException();
        }
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
        if(dispatched){
            throw new IllegalStateException();
        }
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
