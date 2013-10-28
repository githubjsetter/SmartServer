package com.smart.platform.anyprint.impl;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Êý¾ÝÁÐ
 * @author Administrator
 *
 */
public class Columncell extends TextCell{
	protected String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Columncell(String expr, String title) {
		super(expr, Cellbase.CELLTYPE_DATA);
		this.title=title;
	}

	@Override
	public void write(PrintWriter out) throws Exception {
		out.println("<cell>");
		out.println("<celltype>"+celltype+"</celltype>");
		out.println("<printable>"+(printable?"true":"false")+"</printable>");
		out.println("<rect>"+rect.x+":"+rect.y+":"+rect.width+":"+rect.height+"</rect>");
		int fontdecro=Font.PLAIN;
		out.println("<font>"+font.getName()+":"+font.getSize()+":"+fontdecro+"</font>");
		out.println("<align>"+getAlign()+"</align>");
		out.println("<bold>"+(isBold()?"true":"false")+"</bold>");
		out.println("<italic>"+(isItalic()?"true":"false")+"</italic>");
		out.println("<format>"+format+"</format>");
		out.println("<barcodetype>"+barcodetype+"</barcodetype>");
		out.println("<title>"+getTitle()+"</title>");
		out.println("<expr>");
		out.println(expr);
		out.println("</expr>");
		out.println("</cell>");
	}
	
	public void read(BufferedReader rd) throws Exception {
		String line;
		while((line=rd.readLine())!=null){
			if(line.startsWith("</cell>")){
				break;
			}
			
			if(line.startsWith("<printable>")){
				setPrintable(getXmlvalue(line).equals("true"));
			}else if(line.startsWith("<rect>")){
				readRect(getXmlvalue(line));
			}else if(line.startsWith("<font>")){
				readFont(getXmlvalue(line));
			}else if(line.startsWith("<align>")){
				setAlign(Integer.parseInt(getXmlvalue(line)));
			}else if(line.startsWith("<title>")){
				setTitle(getXmlvalue(line));
			}else if(line.startsWith("<format>")){
				setFormat(getXmlvalue(line));
			}else if(line.startsWith("<barcodetype>")){
				setBarcodetype(getXmlvalue(line));
			}else if(line.startsWith("<bold>")){
				setBold(getXmlvalue(line).equals("true"));
			}else if(line.startsWith("<italic>")){
				setItalic(getXmlvalue(line).equals("true"));
			}else if(line.startsWith("<expr>")){
				readExpr(rd);
			}
			
		}
	}

}
