package servlet.registration;

import javax.servlet.Registration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class JerryRegistration implements Registration.Dynamic {

    protected String name;
    protected String className;
    protected Map<String, String> initParameters;
    protected boolean async;

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
        checkInitParams(name, value);

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
        checkInitParams(initParameters.keySet(), initParameters.values());

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

    private void checkInitParams(Object... obj){

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

    public void setName(String name) {
        this.name = name;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public void setAsyncSupported(boolean isAsyncSupported) {
        async = isAsyncSupported;
    }

    public boolean isAsync() {
        return async;
    }

    public abstract void init(ServletContext context) throws InstantiationException, ClassNotFoundException, ServletException, IllegalAccessException;
}
