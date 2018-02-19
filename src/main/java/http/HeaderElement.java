package http;

import java.util.HashMap;
import java.util.Map;

//      header  = [ element ] *( "," [ element ] )
//      element = name [ "=" [ value ] ] *( ";" [ param ] )
//      param   = name [ "=" [ value ] ]

public class HeaderElement {

    private static final String SEPARATOR = ";";
    private static final String EQUALITY = "=";

    private String name;
    private String value;
    private Map<String, String> parameters = new HashMap<>();

    public HeaderElement(String element){
        int index = element.indexOf(SEPARATOR);

        if(index != -1){
            collectParams(element.substring(index + 1, element.length()));
        }
        else{
            element = element.substring(0, element.length());
        }

        index = element.indexOf(EQUALITY);
        this.name = element.substring(0, index);
        this.value = element.substring(index + 1, element.length());
    }

    private void collectParams(String parameter){
        int separatorIndex = parameter.indexOf(SEPARATOR);

        if(separatorIndex != -1){
            collectParams(parameter.substring(separatorIndex + 1, parameter.length()));
        }
        else{
            separatorIndex = parameter.length();
        }

        int equalityIndex = parameter.indexOf(EQUALITY);
        String parameterName = parameter.substring(0, equalityIndex);
        String parameterValue = parameter.substring(equalityIndex + 1, separatorIndex);
        parameters.put(parameterName, parameterValue);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameterByName(String name){
        return parameters.get(name);
    }
}
