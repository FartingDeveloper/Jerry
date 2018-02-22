package http.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ByteOutputStream extends OutputStream{

    private List<Byte> bytes;

    public ByteOutputStream(){
        bytes = new ArrayList<>();
    }

    @Override
    public void write(int b) throws IOException {
        bytes.add((byte) b);
    }

    public Byte[] getBytes(){
        return bytes.toArray(new Byte[bytes.size()]);
    }

}
