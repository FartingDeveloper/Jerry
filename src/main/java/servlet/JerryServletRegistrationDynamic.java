package servlet;

import javax.servlet.*;
import java.io.IOException;
import java.util.*;

public class JerryServletRegistrationDynamic extends JerryServletRegistration implements ServletRegistration.Dynamic{

    private int loadOnStartup = -1;
    private Servlet servlet;

    public JerryServletRegistrationDynamic(String servletName, String servletClassName){
        super(servletName, servletClassName);
    }

    public JerryServletRegistrationDynamic(String servletName, Servlet servlet){
        super(servletName, servlet.getClass().getName());
        this.servlet = servlet;
    }


    public JerryServletConfig getJerryServletConfig(ServletContext context){
        if(cache.get(context) == null){
            cache.put(context, new JerryServletConfigDynamic(context));
        }
        return cache.get(context);
    }

    public JerryServletConfig.JerryRequestDispatcher getJerryRequestDispatcher(ServletContext context){
        JerryServletConfigDynamic config = (JerryServletConfigDynamic) getJerryServletConfig(context);
        JerryServletConfig.JerryRequestDispatcher requestDispatcher = null;
        try {
            if(servlet != null){
                requestDispatcher = config.getJerryRequestDispatcher(servlet);
            }else{
                requestDispatcher = config.getJerryRequestDispatcher();
            }
        } catch (ClassNotFoundException e) {
            logger.error("Class isn't found: " + className);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return requestDispatcher;
    }

    @Override
    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    @Override
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        return null;
    }

    @Override
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {

    }

    @Override
    public void setRunAsRole(String roleName) {

    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {

    }

    protected class JerryServletConfigDynamic extends JerryServletRegistration.JerryServletConfig{

        private JerryServletRegistration.JerryServletConfig.JerryRequestDispatcher requestDispatcher;

        public JerryServletConfigDynamic(ServletContext context) {
            super(context);
        }

        public JerryServletRegistration.JerryServletConfig.JerryRequestDispatcher getJerryRequestDispatcher(Servlet servlet) throws ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
            if(requestDispatcher == null){
                requestDispatcher = new JerryServletRegistration.JerryServletConfig.JerryRequestDispatcher(servlet);
            }
            return requestDispatcher;
        }
    }
}
