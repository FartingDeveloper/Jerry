package servlet;

import javax.servlet.Registration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JerryRegistration implements Registration {

    protected String name;
    protected String className;
    protected Map<String, String> initParameters;

    public JerryRegistration(String name, String className){
        this.name = name;
        this.className = className;
        initParameters = new HashMap<>();
    }

    public JerryRegistration(String name, String className, Map<String, String> initParameters){
        this(name, className);
        this.initParameters = initParameters;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getClassName() {
        return className;
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
