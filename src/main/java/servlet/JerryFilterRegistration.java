package servlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.*;

public class JerryFilterRegistration extends JerryRegistration implements FilterRegistration {

    private Set<String> mappings;
    private Set<String> servletNameMappings;
    private Map<Set<String>, EnumSet<DispatcherType>> dispatcherTypes;

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

    private void add(Set<String> set, EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String... urls){
        check(urls);

        Set<String> tmp = new HashSet<>();
        for (String servletName : urls){
            tmp.add(servletName);
        }

        set.addAll(tmp);
        this.dispatcherTypes.put(set, dispatcherTypes);
    }
}
