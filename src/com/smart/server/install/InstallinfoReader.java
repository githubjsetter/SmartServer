package com.smart.server.install;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * ��ȡ��װ��Ϣ
 * @author Administrator
 *
 */
public class InstallinfoReader {

	public Installinfo read(BufferedReader reader)throws Exception{
		Installinfo installinfo=new Installinfo();
		String line;
		while((line=reader.readLine())!=null){
			if(line.indexOf("<ģ����>")>=0){
				installinfo.setModulename(getValue(line));
			}else if(line.indexOf("<Ӣ����>")>=0){
				installinfo.setModuleengname(getValue(line));
			}else if(line.indexOf("<�汾>")>=0){
				installinfo.setVersion(getValue(line));
			}else if(line.indexOf("<��Ʒ��>")>=0){
				installinfo.setProdname(getValue(line));
			}else if(line.indexOf("<�����嵥>")>=0){
				readOplist(reader,installinfo);
			}else if(line.indexOf("<�����嵥>")>=0){
				readServicelist(reader,installinfo);
			}else if(line.indexOf("<HOV�嵥>")>=0){
				readHovlist(reader,installinfo);
			}
		}
		return installinfo;
	}
	
	
	/**
	 * ���ļ���ȡ��װ��Ϣ
	 * @param f
	 * @throws Exception
	 */
	public Installinfo read(File f)throws Exception{
		BufferedReader reader=null;
		try{
			reader=new BufferedReader(new InputStreamReader(new FileInputStream(f),"gbk"));
			return read(reader);
		}finally{
			if(reader!=null)
			reader.close();
		}
	}
	
	/**
	 * lineΪ<ֵ>value</ֵ>��ʽ,ȡvalue
	 * @param line
	 * @return
	 */
	static String getValue(String line){
		int p1=line.indexOf(">");
		if(p1<0)return "";
		
		int p2=line.indexOf("</");
		if(p2<0)return "";
		
		return line.substring(p1+1,p2);
	}
	
	/**
	 * �������嵥
	 * @param reader
	 * @param installinfo
	 * @throws Exception
	 */
	static void readOplist(BufferedReader reader,Installinfo installinfo)throws Exception{
		String line;
		while((line=reader.readLine())!=null){
			//�ֽ�. opid:opcode:opname:classname
			if(line.indexOf("</")>=0)return;
			String ss[]=line.split(":");
			if(ss.length<5){
				continue;
			}
			if(ss==null || ss.length<5)continue;
			Installinfo.Opinfo opinfo=new Installinfo.Opinfo(); 
			opinfo.opid=ss[0];
			opinfo.opcode=ss[1];
			opinfo.opname=ss[2];
			opinfo.classname=ss[3];
			opinfo.groupname=ss[4];
			if(ss.length>=6){
				opinfo.sortno=ss[5];
			}
			installinfo.addOpinfo(opinfo);
		}
	}

	static void readHovlist(BufferedReader reader,Installinfo installinfo)throws Exception{
		String line;
		while((line=reader.readLine())!=null){
			//�ֽ�. hovname,classname
			if(line.indexOf("</")>=0)return;
			String ss[]=line.split(":");
			if(ss==null || ss.length<2)continue;
			Installinfo.Hovinfo hovinfo=new Installinfo.Hovinfo(); 
			hovinfo.hovname=ss[0];
			hovinfo.classname=ss[1];
			installinfo.addHovinfo(hovinfo);
		}
	}

	static void readServicelist(BufferedReader reader,Installinfo installinfo)throws Exception{
		String line;
		while((line=reader.readLine())!=null){
			//�ֽ�. command,classname
			if(line.indexOf("</")>=0)return;
			int p=line.lastIndexOf(":");
			if(p<0)continue;
			Installinfo.Serviceinfo serviceinfo=new Installinfo.Serviceinfo(); 
			serviceinfo.command=line.substring(0,p);
			serviceinfo.classname=line.substring(p+1);
			installinfo.addServiceinfo(serviceinfo);
		}
	}
}
