package com.inca.np.communicate;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-27
 * Time: 17:29:39
 * �����ַ���������
 */
public class StringCommand extends CommandBase{
	/**
	 * ���
	 */
    protected String str=null;

    /**
     * ����
     * @param commandhead ����ͷ
     */
    public StringCommand(CommandHead commandhead) {
        super(commandhead);
    }

    /**
     * ����.
     * @param s ����
     */
    public StringCommand(String s) {
        super();
        commandhead.commandtype = CommandHead.COMMANDTYPE_STRING;
        this.str=s;
    }

    /**
     * д����
     */
    protected void writeData(OutputStream out) throws Exception {
        CommandFactory.writeString(str,out);
    }

    /**
     * ��in����
     */
    protected void readData(InputStream in) throws Exception {
        str=CommandFactory.readString(in);
    }

    /**
     * �Ƚ���ͬ
     * @param other
     * @return
     */
    public boolean equal(StringCommand other){
        if(!super.equals(other))return false;
        if(!this.str.equals(other.str))return false;
        return true;
    }

    /**
     * �������
     * @return
     */
    public String getString() {
        return str;
    }
}
