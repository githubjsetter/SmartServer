package com.inca.np.communicate;



import com.inca.np.client.RemoteConnector;
import com.inca.np.util.DefaultNPParam;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-26
 * Time: 15:40:06
 * �ͻ������󡣰���������
 */
public class ClientRequest {
    /**
     * ��֤��
     */
    protected String authstring="";

    /**
     * ��ϢID
     */
    //protected String msgid="";
    
    protected String activeopid="";

    /**
     * ������IP
     */
    protected String remoteip="";
    
    /**
     * web�������ϵ�Web context name
     */
    protected String contextname="";

    /**
     * ��������
     */
    Vector<CommandBase> cmdtables=new Vector<CommandBase>();

    protected String version="version2";
    
    /**
     * request�ı���
     */
    HashMap<String, String> contextmap=new HashMap<String, String>();
    /**
     * ����
     */
    public ClientRequest() {
        authstring=RemoteConnector.getAuthstring();
        activeopid=RemoteConnector.getActiveopid();
    }

    /**
     * ����.������һ��StringCommand,���Ϊcommand
     * @param command ���
     */
    public ClientRequest(String command) {
        authstring=RemoteConnector.getAuthstring();
        addCommand(new StringCommand(command));
        activeopid=RemoteConnector.getActiveopid();
    }
    
    /**
     * ������ϢID
     * @deprecated
     */
    protected void createMsgid(){

    }

    /**
     * ���ط�����IP
     * @return
     */
    public String getRemoteip() {
		return remoteip;
	}

    /**
     * ���÷�����IP
     * @param remoteip
     */
	public void setRemoteip(String remoteip) {
		this.remoteip = remoteip;
	}
	
	/**
	 * 
	 * @return
	 */
	
	public String getActiveopid() {
		return activeopid;
	}

	/**
	 * ������ϢID
	 * @param msgid
	 */
	public void setActiveopid(String activeopid) {
		this.activeopid = activeopid;
		this.putContextvalue("activeopid",activeopid);
	}

	/**
	 * ȡweb context name
	 * @return
	 */
	public String getContextname() {
		return contextname;
	}

	/**
	 * ����web context name
	 * @param contextname
	 */
	public void setContextname(String contextname) {
		this.contextname = contextname;
	}

	/**
	 * ��������
	 * @param cmd
	 */
	public void addCommand(CommandBase cmd){
        cmdtables.add(cmd);
    }

	/**
	 * ��in����
	 * @param in
	 * @throws Exception
	 */
    public void readData(InputStream in) throws Exception{
    	String firstword=CommandFactory.readString(in);
    	if(firstword.startsWith("version")){
    		version=firstword;
    		readDataVersion2(in);
    	}else{
    		activeopid=firstword;
    		readDataVersion1(in);
    	}
    }
    
    void readDataVersion1(InputStream in) throws Exception{
        authstring = CommandFactory.readString(in);
        int ct=CommandFactory.readShort(in);
        cmdtables.removeAllElements();
        for(int i=0;i<ct;i++){
            CommandBase cmd = CommandFactory.readCommand(in);
            cmdtables.add(cmd);
        }
    }

    void readDataVersion2(InputStream in) throws Exception{
        authstring = CommandFactory.readString(in);
        contextmap.clear();
        ParamCommand contextparamcmd = (ParamCommand) CommandFactory.readCommand(in);
        Enumeration<NVPair> en=contextparamcmd.getNvpairs().elements();
        while(en.hasMoreElements()){
        	NVPair nv=en.nextElement();
        	contextmap.put(nv.getName(), nv.getValue());
        }
        activeopid=contextmap.get("activeopid");
        
        int ct=CommandFactory.readShort(in);
        cmdtables.removeAllElements();
        for(int i=0;i<ct;i++){
            CommandBase cmd = CommandFactory.readCommand(in);
            cmdtables.add(cmd);
        }
    }


    /**
     * д���ݵ�out
     * @param out
     * @throws Exception
     */
    public void writeData(OutputStream out)throws Exception{
        CommandFactory.writeString(version,out);
        CommandFactory.writeString(authstring,out);
        
        ParamCommand contextpcmd=new ParamCommand();
        contextpcmd.addParam("activeopid", activeopid);
        Iterator<String> it=contextmap.keySet().iterator();
        while(it.hasNext()){
        	String name=it.next();
        	String value=contextmap.get(name);
        	contextpcmd.addParam(name, value);
        }
        contextpcmd.write(out);
        
        CommandFactory.writeShort(cmdtables.size(),out);
        Enumeration<CommandBase> en = cmdtables.elements();
        while (en.hasMoreElements()) {
            CommandBase cmd = en.nextElement();
            cmd.write(out);
        }
    }

    /**
     * ȡ��������
     * @return
     */
    public int getCommandcount(){
        return cmdtables.size();
    }

    /**
     * ������֤��
     * @param authstring
     */
    public void setAuthstring(String authstring) {
        this.authstring = authstring;
    }

    /**
     * ȡ��֤��
     * @return
     */
    public String getAuthstring() {
        return authstring;
    }

    /**
     * ȡ��index������
     * @param index
     * @return
     */
    public CommandBase commandAt(int index){
        return  cmdtables.elementAt(index);
    }

    public String getCommand(){
    	if(cmdtables.size()==0)return null;
    	Object o1=cmdtables.elementAt(0);
    	if(!(o1 instanceof StringCommand))return null;
    	return ((StringCommand)o1).getString();
    }
    
    /**
     * �������󱳾���ֵ
     * @param key
     * @param value
     */
    public void putContextvalue(String key,String value){
    	contextmap.put(key,value);
    }

    /**
     * ȡ���󱳾���ֵ
     * @param key
     * @return
     */
    public String getContextvalue(String key){
    	return contextmap.get(key);
    }
}
