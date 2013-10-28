package com.smart.platform.communicate;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.Enumeration;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-26
 * Time: 15:58:43
 * 服务器响应
 */
public class ServerResponse {
	/**
	 * 消息ID
	 */
    protected String msgid="";

    Category logger=Category.getInstance(ServerResponse.class);

    /**
     * 命令向量
     */
    Vector<CommandBase> cmdtables=new Vector<CommandBase>();



    /**
     * 增加命令
     * @param cmd
     */
    public void addCommand(CommandBase cmd){
        cmdtables.add(cmd);
    }


    /**
     * 由in创建
     * @param in
     * @throws Exception
     */
    public void readData(InputStream in) throws Exception{
        msgid=CommandFactory.readString(in);
        int ct=CommandFactory.readShort(in);
        cmdtables.removeAllElements();
        for(int i=0;i<ct;i++){
            CommandBase cmd = CommandFactory.readCommand(in);
            cmdtables.add(cmd);
        }
    }

    /**
     * 写数据
     * @param out
     * @throws Exception
     */
    public void writeData(OutputStream out)throws Exception{
        CommandFactory.writeString(msgid,out);
        CommandFactory.writeShort(cmdtables.size(),out);
        Enumeration<CommandBase> en = cmdtables.elements();
        while (en.hasMoreElements()) {
            CommandBase cmd = en.nextElement();
            if(cmd==null){
            	logger.error("ServerResponse writeData,cmd=null");
            	return;
            }
            cmd.write(out);
        }
    }

    /**
     * 返回命令数量
     * @return
     */
    public int getCommandcount(){
        return cmdtables.size();
    }

    /**
     * 返回第index个命令
     * @param index
     * @return
     */
    public CommandBase commandAt(int index){
        return cmdtables.elementAt(index);
    }
    
    public String getCommand(){
    	if(cmdtables.size()==0)return null;
    	if(cmdtables.elementAt(0) instanceof StringCommand){
    		StringCommand strcmd=(StringCommand) cmdtables.elementAt(0);
    		return strcmd.getString();
    	}
    	return null;
    }
}
