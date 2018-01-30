package servlet;

import javax.servlet.FilterRegistration;
import java.util.Map;
import java.util.Set;

public class JerryFilterRegistrationDynamic extends JerryFilterRegistration implements FilterRegistration.Dynamic {

    boolean asyncSupport;

    public JerryFilterRegistrationDynamic(String name, String className) {
        super(name, className);
    }

    public JerryFilterRegistrationDynamic(String name, String className, Map<String, String> initParameters) {
        super(name, className, initParameters);
    }


    public JerryFilterRegistrationDynamic(String servletName, String servletClassName, Map<String, String> initParameters, Set<String> mappings, Set<String> servletNameMappings) {
        super(servletName, servletClassName, initParameters, mappings, servletNameMappings);
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        this.asyncSupport = asyncSupport;
    }
}
