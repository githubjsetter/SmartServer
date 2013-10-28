package com.smart.platform.communicate;

import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-29
 * Time: 13:20:41
 * 为了统计下载的数据量
 */
public class InflatInputStreamWrapper extends InflaterInputStream {
    int totalreadedsize = 0;

    public InflatInputStreamWrapper(InputStream in, Inflater inf, int size) {
        super(in, inf, size);
    }

    public InflatInputStreamWrapper(InputStream in, Inflater inf) {
        super(in, inf);
    }

    public InflatInputStreamWrapper(InputStream in) {
        super(in);
    }

    public int read() throws IOException {
        int rdct = super.read();    //To change body of overridden methods use File | Settings | File Templates.
        if (rdct > 0) {
            totalreadedsize += rdct;
        }
        return rdct;
    }

    public int read(byte b[]) throws IOException {
        int rdct = super.read(b);
        if (rdct > 0) {
            totalreadedsize += rdct;
        }
        return rdct;
    }

    public int getTotalreadedsize() {
        return totalreadedsize;
    }
}
