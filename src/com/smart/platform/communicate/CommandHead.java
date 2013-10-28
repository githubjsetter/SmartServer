package com.smart.platform.communicate;

import java.io.OutputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-26
 * Time: 17:21:05
 * 命令头
 */
public class CommandHead {
    public final static String COMMANDTYPE_STRING="string";
    public final static String COMMANDTYPE_PARAM="param";
    public final static String COMMANDTYPE_SQL="sql";
    public final static String COMMANDTYPE_DATA="data";
    public final static String COMMANDTYPE_RESULT="result";
    public final static String COMMANDTYPE_FILE="binaryfile";

    String version="";
    /**
     * 类型,请求或响应    
     */
    public String commandtype="";

    /**
     * 命令
     */
    public String command="";

    public void write(OutputStream out) throws Exception{
        CommandFactory.writeString(version,out);
        CommandFactory.writeString(commandtype,out);
        CommandFactory.writeString(command,out);
    }

    public void read(InputStream in) throws Exception{
        version = CommandFactory.readString(in);
        commandtype = CommandFactory.readString(in);
        command = CommandFactory.readString(in);
    }

    public boolean equals(CommandHead otherhead){
        if(!otherhead.version.equals(version))return false;
        if(!otherhead.commandtype.equals(commandtype))return false;
        if(!otherhead.command.equals(command))return false;
        return true;
    }

}
