package servlet.io;

import http.HttpResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class JerryServletOutputStream extends ServletOutputStream {

    private OutputStream outputStream;
    private OutputStream contentOutputStream;

    private byte[] buffer;
    private int bufferSize;
    private int bufferIndex;

    private boolean flushed;

    private WriteListener writeListener;

    public JerryServletOutputStream(HttpResponse response){
        contentOutputStream = response.getContentOutputStream();
        bufferSize = 1024;
        bufferIndex = 0;
        buffer = new byte[1024];
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
        if(bufferIndex < bufferSize){
            writeListener.onWritePossible();
        }
        contentOutputStream.write(b);
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = Arrays.copyOf(buffer, bufferSize);
    }

    public void resetBuffer(){
        bufferIndex = 0;
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        flushed = true;
    }

    public boolean isFlushed() {
        return flushed;
    }
}
