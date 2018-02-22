package http.io;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream{

    private StringBuilder builder;

    public StringOutputStream(){
        builder = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {
        builder.append((char) b);
    }

    public String getString(){
        return builder.toString();
    }

}
