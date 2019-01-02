package com.rg.http;

import com.rg.http.core.Header;
import com.rg.http.core.HeaderElement;
import com.rg.http.io.HttpRequest;
import com.rg.http.io.HttpResponse;
import com.rg.servlet.JerryHttpSession;
import com.rg.servlet.context.JerryServletContext;
import com.rg.servlet.registration.JerryFilterChain;
import com.rg.servlet.request.JerryHttpServletRequest;
import com.rg.servlet.response.JerryHttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

public class HttpRequestHandler implements RequestHandler {

    private Logger LOG = LogManager.getLogger(HttpRequestHandler.class);

    private JerryServletContext servletContext;
    private Servlet servlet;
    private List<Filter> filters;

    public HttpRequestHandler(JerryServletContext servletContext, Servlet servlet, List<Filter> filters) {
        this.servletContext = servletContext;
        this.servlet = servlet;
        this.filters = filters;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        Thread.currentThread().setContextClassLoader(servletContext.getClassLoader());

        String sessionId = null;
        Header cookie = request.getHeader("Cookie");
        if (cookie != null) {
            for (HeaderElement element : request.getHeader("Cookie").getElements()) {
                if (element.getName().equals("JSESSIONID")) {
                    sessionId = element.getValue();
                }
            }
        }

        JerryHttpSession session;
        if (sessionId == null) {
            session = servletContext.createSession(response);
        } else {
            session = servletContext.getSession(sessionId);
        }

        if (session == null) {
            session = servletContext.createSession(response);
        }

        session.setLastAccessedTime(LocalTime.now().toNanoOfDay());

        JerryHttpServletResponse servletResponse = new JerryHttpServletResponse(response, servletContext);
        JerryHttpServletRequest servletRequest = new JerryHttpServletRequest(request, servletResponse, servletContext);

        servletRequest.setSession(session);

        for (ServletRequestListener listener : servletContext.getRequestListeners()) {
            listener.requestInitialized(new ServletRequestEvent(servletContext, servletRequest));
        }

        try {
            FilterChain filterChain = new JerryFilterChain(servlet, filters);
            filterChain.doFilter(servletRequest, servletResponse);

            servletResponse.flushBuffer();
        } catch (ServletException e) {
            LOG.error("Can't create response", e);
            throw new IllegalStateException(e.getRootCause());
        } catch (IOException e) {
            LOG.error("Can't create response", e);
            throw new IllegalStateException(e.getCause());
        }

        for (ServletRequestListener listener : servletContext.getRequestListeners()) {
            listener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
        }
    }
}
