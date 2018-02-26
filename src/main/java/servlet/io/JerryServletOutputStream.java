package servlet.io;

import http.HttpResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.util.Arrays;

public class JerryServletOutputStream extends ServletOutputStream {

    private HttpResponse response;

    private byte[] buffer;
    private int bufferSize;
    private int bufferIndex;

    private WriteListener writeListener;

    public JerryServletOutputStream(HttpResponse response){
        this.response = response;
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
            buffer[bufferIndex++] = (byte) b;
        }
        else{
            flush();
        }
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
        for(int i = 0; i < bufferIndex; i++){
            response.getContentOutputStream().write(buffer[i]);
        }
        bufferIndex = 0;

        response.flush();
    }
}
