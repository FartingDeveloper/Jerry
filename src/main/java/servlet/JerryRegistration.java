package servlet;

import javax.servlet.Registration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JerryRegistration implements Registration {

    protected String name;
    protected String className;
    protected Map<String, String> initParameters;

    protected boolean initialized;

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
        check(name, value);

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

        for (String name : initParameters.keySet()){
            check(name, initParameters.get(name));
        }

        this.initParameters.putAll(initParameters);
        return initParameters.keySet();
    }

    @Override
    public Map<String, String> getInitParameters() {
        return initParameters;
    }

    public void setInitialized(boolean init){
        initialized = init;
    }

    private void check(Object... obj){

        for (int i = 0; i < obj.length; i++){
            if(obj[i] == null) throw new IllegalArgumentException();
        }

        if(initialized) throw new IllegalStateException();
    }

    protected void check(String... str) {
        for (int i = 0; i < str.length; i++){
            if(str[i] == null) throw new IllegalArgumentException();
            if(str[i].isEmpty()) throw new IllegalArgumentException();
        }

        if(initialized) throw new IllegalStateException();
    }
}
