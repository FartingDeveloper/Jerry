package servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletRegistration;
import java.util.*;

public class JerryServletRegistration implements ServletRegistration {

    private String servletName;
    private String servletClassName;
    private Map<String, String> initParameters;
    private Set<String> mappings;

    public JerryServletRegistration(String servletName, String servletClassName){
        this.servletName = servletName;
        this.servletClassName = servletClassName;
        initParameters = new HashMap<>();
        mappings = new HashSet<>();
    }

    public JerryServletRegistration(String servletName, String servletClassName, Map<String, String> initParameters){
        this(servletName, servletClassName);
        this.initParameters = initParameters;
    }

    public JerryServletRegistration(String servletName, String servletClassName, Set<String> mappings){
        this(servletName, servletClassName);
        this.mappings = mappings;
    }

    public JerryServletRegistration(String servletName, String servletClassName, Map<String, String> initParameters, Set<String> mappings){
        this(servletName, servletClassName);
        this.initParameters = initParameters;
        this.mappings = mappings;
    }



    @Override
    public Set<String> addMapping(String... urlPatterns) {
        for (String url : urlPatterns){
            mappings.add(url);
        }
        return mappings;
    }

    @Override
    public Collection<String> getMappings() {
        return mappings;
    }

    @Override
    public String getRunAsRole() {
        return null;
    }

    @Override
    public String getName() {
        return servletName;
    }

    @Override
    public String getClassName() {
        return servletClassName;
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        boolean result = initParameters.containsKey(name);
        initParameters.put(name, value);
        return result;
    }

    @Override
    public String getInitParameter(String name) {
        return initParameters.get(name);
    }

    @Override
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        initParameters.putAll(initParameters);
        return initParameters.keySet();
    }

    @Override
    public Map<String, String> getInitParameters() {
        return initParameters;
    }

}
