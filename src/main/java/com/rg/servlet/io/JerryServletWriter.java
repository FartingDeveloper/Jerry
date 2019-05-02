package com.rg.servlet.io;

import java.io.IOException;
import java.io.Writer;

public class JerryServletWriter extends Writer {

    private JerryServletOutputStream out;

    public JerryServletWriter(JerryServletOutputStream out) {
        this.out = out;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (; off < len; off++) {
            out.write(cbuf[off]);
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
