package servlet.registration;

import servlet.JerryEnumeration;

import javax.servlet.*;
import java.util.*;

public class JerryFilterRegistration extends JerryRegistration implements FilterRegistration.Dynamic {

    private Filter filter;
    private Set<String> mappings;
    private Set<String> servletNameMappings;
    private Map<Set<String>, EnumSet<DispatcherType>> dispatcherTypes;

    public JerryFilterRegistration(String name, String className) {
        super(name, className);


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

    private void add(Set<String> set, EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urls){
        check(urls);

        Set<String> tmp = new HashSet<>();
        for (String servletName : urls){
            tmp.add(servletName);
        }

        set.addAll(tmp);
        this.dispatcherTypes.put(set, dispatcherTypes);
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void init(ServletContext context) throws InstantiationException, ClassNotFoundException, ServletException, IllegalAccessException {
        if(initialized){
            throw new IllegalStateException();
        }

        initialized = true;

        if(filter == null){
            Class<Filter> clazz = (Class<Filter>) context.getClassLoader().loadClass(className);
            filter = clazz.newInstance();
        }

        filter.init(new JerryFilterConfig(context));
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
            return new JerryEnumeration<>(initParameters.keySet().iterator());
        }
    }
}
