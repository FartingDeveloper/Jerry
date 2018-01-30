package servlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.Registration;
import java.util.*;

public class JerryFilterRegistration extends JerryRegistration implements FilterRegistration {

    private Set<String> mappings;
    private Set<String> servletNameMappings;

    public JerryFilterRegistration(String name, String className) {
        super(name, className);
        mappings = new HashSet<>();
    }

    public JerryFilterRegistration(String name, String className, Map<String, String> initParameters) {
        super(name, className, initParameters);
        mappings = new HashSet<>();
        servletNameMappings = new HashSet<>();
    }

    public JerryFilterRegistration(String servletName, String servletClassName, Map<String, String> initParameters, Set<String> mappings, Set<String> servletNameMappings){
        this(servletName, servletClassName, initParameters);
        this.mappings = mappings;
        this.servletNameMappings = servletNameMappings;
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {

    }

    @Override
    public Collection<String> getServletNameMappings() {
        return servletNameMappings;
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {

    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return mappings;
    }

}
