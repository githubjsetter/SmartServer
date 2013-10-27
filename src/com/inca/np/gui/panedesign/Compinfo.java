package com.inca.np.gui.panedesign;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.PrintWriter;

/**
 * ����ؼ���,λ�úʹ�С
 * @author user
 *
 */
public class Compinfo {
	public String compname="";
	public Component realcomp=null;
	public  Rectangle rect=new Rectangle();
	
	
	public Compinfo(String compname, Component realcomp, Rectangle rect) {
		super();
		this.compname = compname;
		this.realcomp = realcomp;
		this.rect = rect;
	}

	/**
	 * д���ļ���
	 * @param out
	 * @throws Exception
	 */
	public void write(PrintWriter out) throws Exception{
		out.print("<comp>");
		out.print(compname);
		out.print(":");
		out.print(rect.x);
		out.print(":");
		out.print(rect.y);
		out.print(":");
		out.print(rect.width);
		out.print(":");
		out.print(rect.height);
		
		if(realcomp instanceof Titleborderpane){
			out.print(":");
			out.print(((Titleborderpane)realcomp).getTitle());
		}else{
			out.print(":");
		}
		
		out.print("</comp>");
		out.println();
	}
	
}
