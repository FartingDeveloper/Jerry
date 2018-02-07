package servlet;

import javax.servlet.FilterRegistration;
import java.util.Map;
import java.util.Set;

public class JerryFilterRegistrationDynamic extends JerryFilterRegistration implements FilterRegistration.Dynamic {

    private boolean asyncSupport;

    public JerryFilterRegistrationDynamic(String name, String className) {
        super(name, className);
    }

    public JerryFilterRegistrationDynamic(String name, String className, Map<String, String> initParameters) {
        super(name, className, initParameters);
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        this.asyncSupport = asyncSupport;
    }

    public boolean isAsyncSupport() {
        return asyncSupport;
    }
}
