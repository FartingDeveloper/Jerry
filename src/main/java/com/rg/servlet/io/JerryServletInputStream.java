package com.rg.servlet.io;

import com.rg.http.HttpRequest;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JerryServletInputStream extends ServletInputStream {

    private InputStream inputStream;
    private boolean finished;

    private ReadListener readListener;

    public JerryServletInputStream(HttpRequest request) {
        inputStream = request.getContentInputStream();
    }

    public int readLine(byte[] b, int off, int len) throws IOException {
        int result = super.readLine(b, off, len);
        if(result == -1){
            finished = true;
            if(readListener != null){
                readListener.onAllDataRead();
            }
        }
        return result;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public boolean isReady() {
        try{
            if(inputStream.available() > 0){
                if(readListener != null){
                    readListener.onDataAvailable();
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        this.readListener = readListener;
    }

    @Override
    public int read() throws IOException {
        if(readListener != null){
            readListener.onDataAvailable();
        }
        return inputStream.read();
    }

}
