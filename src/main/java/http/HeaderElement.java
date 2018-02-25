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
        int index = Syntax.getIndex(element, SEPARATOR);
        if(index != -1){
            collectParams(element.substring(index + 1, element.length()));
            element = element.substring(0, index);
        }

        int equalIndex = element.indexOf(EQUALITY);
        if(equalIndex == -1){
            this.name = element;
            this.value = null;
        }
        else{
            this.name = element.substring(0, equalIndex);
            this.value = element.substring(equalIndex + 1, element.length());
        }
    }

    private void collectParams(String parameter){
        int separatorIndex = Syntax.getIndex(parameter, SEPARATOR);

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
