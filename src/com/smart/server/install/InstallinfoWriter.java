package com.smart.server.install;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Enumeration;

import com.smart.server.install.Installinfo.Hovinfo;
import com.smart.server.install.Installinfo.Opinfo;
import com.smart.server.install.Installinfo.Serviceinfo;

public class InstallinfoWriter {
	public void write(Installinfo iinfo,File file)throws Exception{
		PrintWriter out=null;
		try {
			out=new PrintWriter(new FileWriter(file));
			write(iinfo,out);
		}finally{
			if(out!=null)
			out.close();
		}
	}
	
	
	public void write(Installinfo iinfo,PrintWriter out){
		writeTag("产品名",iinfo.prodname,out);
		writeTag("模块名",iinfo.modulename,out);
		writeTag("英文名",iinfo.moduleengname,out);
		writeTag("版本",iinfo.version,out);
		
		out.println("<功能清单>");
		writeOps(iinfo,out);
		out.println("</功能清单>");
		
		out.println("<服务清单>");
		writeService(iinfo,out);
		out.println("</服务清单>");

		out.println("<HOV清单>");
		writeHov(iinfo,out);
		out.println("</HOV清单>");
		
		out.flush();
	}

	void writeHov(Installinfo iinfo, PrintWriter out) {
		Enumeration<Hovinfo> en=iinfo.getHovinfos().elements();
		while(en.hasMoreElements()){
			Hovinfo hovinfo=en.nextElement();
			out.print(hovinfo.hovname);
			out.println(":"+hovinfo.classname);
		}
	}
	
	void writeService(Installinfo iinfo, PrintWriter out) {
		Enumeration<Serviceinfo> en=iinfo.getServiceinfos().elements();
		while(en.hasMoreElements()){
			Serviceinfo serviceinfo=en.nextElement();
			out.print(serviceinfo.command);
			out.println(":"+serviceinfo.classname);
		}
	}
	
	void writeOps(Installinfo iinfo, PrintWriter out) {
		Enumeration<Opinfo> en=iinfo.getOpinfos().elements();
		while(en.hasMoreElements()){
			Opinfo opinfo=en.nextElement();
			out.print(opinfo.opid);
			out.print(":"+opinfo.opcode);
			out.print(":"+opinfo.opname);
			out.print(":"+opinfo.classname);
			out.print(":"+opinfo.groupname);
			out.println();
		}
	}

	void writeTag(String tag,String value,PrintWriter out){
		out.println("<"+tag+">"+value+"</"+tag+">");
	}
	
}
