package servlet.io;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JerryServletInputStream extends ServletInputStream {

    private static final byte SP = 0x20;
    private final static byte CR  = 0x0D;
    private final static byte LF  = 0x0A;


    private InputStream inputStream;

    public JerryServletInputStream(HttpRequest request) {
        List<Byte> bytes = new ArrayList<>();

        byte[] method = request.getRequestLine().getMethod().getBytes();
        byte[] uri = request.getRequestLine().getUri().getBytes();
        byte[] protocolVersion = request.getRequestLine().getProtocolVersion().getProtocol().getBytes();

        byte[] headers = null;

        for (Header header : request.getAllHeaders()){
            header.getName().getBytes();
        }

        inputStream = new ByteArrayInputStream(null);
    }

    private void addBytesToList(List<Byte> list, byte... arr){
        for (byte b : arr){
            list.add(b);
        }
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
