package com.rg.http.core;

import java.util.ArrayList;
import java.util.List;

//      header  = [ element ] *( "," [ element ] )"," [ element ] )
//      element = name [ "=" [ value ] ] *( ";" [ param ] )
//      param   = name [ "=" [ value ] ]

public class Header {

    private String name;
    private String value;

    private List<HeaderElement> elements = new ArrayList<>();

    public Header(String name, String value){
        this.name = name;
        this.value = value;

        collectElements(value);
    }

    private void collectElements(String value){
        int separatorIndex = HTTP.getIndex(value, HTTP.ELEMENT_SEPARATOR);

        if(separatorIndex != -1){
            collectElements(value.substring(separatorIndex + 1, value.length()));
        }
        else{
            separatorIndex = value.length();
        }

        elements.add(new HeaderElement(value.substring(0, separatorIndex)));
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<HeaderElement> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
