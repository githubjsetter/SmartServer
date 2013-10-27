package com.inca.np.communicate;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-27
 * Time: 17:29:39
 * 传输字符串的命令
 */
public class StringCommand extends CommandBase{
	/**
	 * 命令串
	 */
    protected String str=null;

    /**
     * 构造
     * @param commandhead 命令头
     */
    public StringCommand(CommandHead commandhead) {
        super(commandhead);
    }

    /**
     * 构造.
     * @param s 命令
     */
    public StringCommand(String s) {
        super();
        commandhead.commandtype = CommandHead.COMMANDTYPE_STRING;
        this.str=s;
    }

    /**
     * 写数据
     */
    protected void writeData(OutputStream out) throws Exception {
        CommandFactory.writeString(str,out);
    }

    /**
     * 由in创建
     */
    protected void readData(InputStream in) throws Exception {
        str=CommandFactory.readString(in);
    }

    /**
     * 比较相同
     * @param other
     * @return
     */
    public boolean equal(StringCommand other){
        if(!super.equals(other))return false;
        if(!this.str.equals(other.str))return false;
        return true;
    }

    /**
     * 返回命令串
     * @return
     */
    public String getString() {
        return str;
    }
}
