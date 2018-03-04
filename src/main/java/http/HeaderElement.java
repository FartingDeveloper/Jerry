package http;

import java.util.HashMap;
import java.util.Map;

//      header  = [ element ] *( "," [ element ] )
//      element = name [ "=" [ value ] ] *( ";" [ param ] )
//      param   = name [ "=" [ value ] ]

public class HeaderElement {

    private String name;
    private String value;
    private Map<String, String> parameters = new HashMap<>();

    public HeaderElement(String element){
        int index = Syntax.getIndex(element, Syntax.ELEMENT_PARAMS_SEPARATOR);
        if(index != -1){
            collectParams(element.substring(index + 1, element.length()));
            element = element.substring(0, index);
        }

        int equalIndex = element.indexOf(Syntax.EQUALITY);
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
        int separatorIndex = Syntax.getIndex(parameter, Syntax.ELEMENT_PARAMS_SEPARATOR);

        if(separatorIndex != -1){
            collectParams(parameter.substring(separatorIndex + 1, parameter.length()));
        }
        else{
            separatorIndex = parameter.length();
        }

        int equalityIndex = parameter.indexOf(Syntax.EQUALITY);
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        if (value == null) {
            result.append(name);
        } else {
            result.append(name + Syntax.EQUALITY + value);
        }

        if (parameters.size() != 0) {
            for (String str : parameters.keySet()) {
                result.append(Syntax.ELEMENT_PARAMS_SEPARATOR + str + "=" + parameters.get(str));
            }
        }
        return result.toString();
    }
}
