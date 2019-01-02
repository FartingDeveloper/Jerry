package com.rg.http.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTP {

    public static final String PARAMS_START = "?";
    public static final String PARAMS_SEPARATOR = "&";
    public static final String ELEMENT_PARAMS_SEPARATOR = ";";
    public static final String ELEMENT_SEPARATOR = ",";
    public static final String EQUALITY = "=";
    public static final String CRLF = "\r\n";
    public static final String SP = " ";
    public static final String URL_SEPARATOR = "/";

    public static int getIndex(String parameter, String character){
        Pattern pattern = Pattern.compile("[^ \\( \\[ \\{]+" + character + "[^ \\) \\] \\}]+");
        Matcher matcher = pattern.matcher(parameter);
        if (matcher.find()){
            int start = matcher.start();
            return parameter.indexOf(character, start);
        }

        return -1;
    }

}
