package servlet.io;

import http.HttpResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JerryServletOutputStream extends ServletOutputStream {

    private OutputStream outputStream;
    private int bufferSize;

    private WriteListener writeListener;

    public JerryServletOutputStream(HttpResponse response){
        outputStream = response.getContentOutputStream();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        this.writeListener = writeListener;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void resetBuffer(){

    }
}
