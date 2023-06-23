package com.hsbc.engineering;

import java.io.OutputStream;
import java.io.IOException;

public class NullOutputStream extends OutputStream {
    public void write(int i) throws IOException {
        //do nothing
    }

    public void write(Object o) {
        //do nothing
        o = null;
    }
}