package com.inca.np.communicate;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-29
 * Time: 13:38:24
 * To change this template use File | Settings | File Templates.
 */
public class InputStreamWrapper extends BufferedInputStream{
    int totalreadedsize=0;
    public InputStreamWrapper(InputStream in) {
        super(in);
    }

    public InputStreamWrapper(InputStream in, int size) {
        super(in, size);
    }

    public synchronized int read() throws IOException {
        int rdct = super.read();    //To change body of overridden methods use File | Settings | File Templates.
        if (rdct > 0) {
            totalreadedsize += rdct;
        }
        return rdct;
    }

    public synchronized int read(byte b[], int off, int len) throws IOException {
        int rdct = super.read(b, off, len);
        if(rdct>0){
            totalreadedsize+=rdct;
        }
        return rdct;
    }

    public int read(byte b[]) throws IOException {
        int rdct = super.read(b);
        if(rdct>0){
            totalreadedsize+=rdct;
        }
        return rdct;
    }

    public int getTotalreadedsize() {
        return totalreadedsize;
    }
}
