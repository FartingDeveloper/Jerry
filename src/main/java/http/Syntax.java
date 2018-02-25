package http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Syntax {

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
