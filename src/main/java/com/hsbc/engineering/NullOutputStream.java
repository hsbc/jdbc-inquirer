package com.hsbc.engineering;

import java.io.OutputStream;
import java.io.IOException;

/**
 * <p>NullOutputStream class.</p>
 *
 */
public class NullOutputStream extends OutputStream {
    /**
     * <p>write.</p>
     *
     * @param i a int
     * @throws java.io.IOException if any.
     */
    public void write(int i) throws IOException {
        //do nothing
    }

    /**
     * <p>write.</p>
     *
     * @param o a {@link java.lang.Object} object
     */
    public void write(Object o) {
        //do nothing
        o = null;
    }
}
