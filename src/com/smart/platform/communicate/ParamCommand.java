package com.smart.platform.communicate;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-2-27
 * Time: 17:51:40
 * 传输字符串形式名字值对
 */
public class ParamCommand  extends CommandBase{
    protected Vector <NVPair> nvpairs=new Vector<NVPair>();

    public ParamCommand(CommandHead commandhead) {
        super(commandhead);
    }

    public ParamCommand() {
        super();
        commandhead.commandtype=CommandHead.COMMANDTYPE_PARAM;
    }


    /**
     * 发送序列
     * 名字值对数量
     * 名字 值[名字 值]
     * @param out
     * @throws Exception
     */
    protected void writeData(OutputStream out) throws Exception {
        //写数量
        CommandFactory.writeShort(nvpairs.size(),out);


        Enumeration<NVPair> en = nvpairs.elements();
        while (en.hasMoreElements()) {
            NVPair nvPair = en.nextElement();
            CommandFactory.writeString(nvPair.name,out);
            CommandFactory.writeString(nvPair.value,out);
        }

    }

    protected void readData(InputStream in) throws Exception {
        int ct=CommandFactory.readShort(in);
        //逐个发送名字值对
        nvpairs.removeAllElements();
        for(int i=0;i<ct;i++){
            String name=CommandFactory.readString(in);
            String value=CommandFactory.readString(in);
            nvpairs.add(new NVPair(name,value));
        }
    }

    public void addParam(String name,String value){
    	Enumeration<NVPair> en=nvpairs.elements();
    	while(en.hasMoreElements()){
    		NVPair nv=en.nextElement();
    		if(nv.name.equals(name)){
    			nv.value=value;
    			return;
    		}
    	}
        nvpairs.add(new NVPair(name,value));
    }

    public Vector<NVPair> getNvpairs() {
        return nvpairs;
    }

    public boolean equals(ParamCommand other){
        if(!super.equals(other))return false;

        if(nvpairs.size() != other.nvpairs.size()){
            return false;
        }

        Enumeration<NVPair> en1 = nvpairs.elements();
        Enumeration<NVPair> en2 = other.nvpairs.elements();
        while(en1.hasMoreElements()){
            NVPair nv1=en1.nextElement();
            NVPair nv2=en2.nextElement();
            if(!nv1.name.equals(nv2.name))return false;
            if(!nv1.value.equals(nv2.value))return false;
        }
        return true;
    }

    public String getValue(String key){
        Enumeration<NVPair> en = nvpairs.elements();
        while (en.hasMoreElements()) {
            NVPair nvPair = en.nextElement();
            if(nvPair.getName().equals(key)){
                return nvPair.getValue();
            }
        }
        return null;
    }

}