package servlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import java.util.*;

public class JerryFilterRegistration extends JerryRegistration implements FilterRegistration {

    private Set<String> mappings;
    private Set<String> servletNameMappings;
    private Map<Set<String>, EnumSet<DispatcherType>> dispatcherTypes;
    private JerryFilterRegistration.JerryFilterConfig config;

    public JerryFilterRegistration(String name, String className) {
        super(name, className);
        mappings = new LinkedHashSet<>();
        servletNameMappings = new LinkedHashSet<>();
        dispatcherTypes = new LinkedHashMap<>();
    }

    public JerryFilterRegistration(String name, String className, Map<String, String> initParameters) {
        super(name, className, initParameters);
        mappings = new LinkedHashSet<>();
        servletNameMappings = new LinkedHashSet<>();
        dispatcherTypes = new LinkedHashMap<>();
    }

    @Override
    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... servletNames) {
        add(servletNameMappings, dispatcherTypes, false, servletNames);
    }

    @Override
    public Collection<String> getServletNameMappings() {
        return servletNameMappings;
    }

    @Override
    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urlPatterns) {
        add(mappings, dispatcherTypes, false, urlPatterns);
    }

    @Override
    public Collection<String> getUrlPatternMappings() {
        return mappings;
    }

    public EnumSet<DispatcherType> getDispatcherTypes(String mapping){
        for(Set<String> set : dispatcherTypes.keySet()){
            if(set.contains(mapping)){
                return dispatcherTypes.get(set);
            }
        }
        return null;
    }

    public EnumSet<DispatcherType> getDispatcherTypes(Set<String> mappings){
        for (Set<String> set : dispatcherTypes.keySet()){
            if(set.containsAll(mappings)){
                return dispatcherTypes.get(set);
            }
        }
        return null;
    }

    public JerryFilterRegistration.JerryFilterConfig getFilterConfig(ServletContext context){
        if(config == null){
            config =  new JerryFilterRegistration.JerryFilterConfig(context);
        }
        return config;
    }

    private void add(Set<String> set, EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urls){
        check(urls);

        Set<String> tmp = new HashSet<>();
        for (String servletName : urls){
            tmp.add(servletName);
        }

        set.addAll(tmp);
        this.dispatcherTypes.put(set, dispatcherTypes);
    }

    private class JerryFilterConfig implements FilterConfig {

        private ServletContext context;

        public JerryFilterConfig(ServletContext context){
            this.context = context;
        }

        @Override
        public String getFilterName() {
            return name;
        }

        @Override
        public ServletContext getServletContext() {
            return context;
        }

        @Override
        public String getInitParameter(String name) {
            return initParameters.get(name);
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return new Enumeration<String>() {

                private Iterator<String> iterator = initParameters.keySet().iterator();

                @Override
                public boolean hasMoreElements() {
                    return iterator.hasNext();
                }

                @Override
                public String nextElement() {
                    return iterator.next();
                }
            };
        }
    }
}
