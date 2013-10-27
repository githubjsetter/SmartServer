package com.inca.np.communicate;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-8-2
 * Time: 18:23:45
 * To change this template use File | Settings | File Templates.
 */
public class BinfileCommand extends CommandBase {
    protected byte[] bindata = null;

    public BinfileCommand(CommandHead commandhead) {
        super(commandhead);
    }

    public BinfileCommand(byte[] data) {
        super();
        commandhead.commandtype = CommandHead.COMMANDTYPE_FILE;
        this.bindata = data;
    }

    public BinfileCommand(byte[] data, int offset, int len) {
        super();
        commandhead.commandtype = CommandHead.COMMANDTYPE_FILE;
        bindata = new byte[len];
        System.arraycopy(data, 0, bindata, offset, len);
    }

    protected void writeData(OutputStream out) throws Exception {
        CommandFactory.writeShort(bindata.length, out);
        out.write(bindata);
    }

    protected void readData(InputStream in) throws Exception {
        int len = CommandFactory.readShort(in);
        bindata = new byte[len];

        int wanted=len;
        int readed = 0;
        while (wanted>0) {
            int rd = 0;
            rd = in.read(bindata, readed, wanted);
            readed += rd;
            wanted -= rd;
        }
    }

    public boolean equal(BinfileCommand other) {
        if (!super.equals(other)) return false;
        return true;
    }


    public byte[] getBindata() {
        return bindata;
    }

    public String getString() {
        return "bindata";
    }
}
