package com.inca.np.anyprint.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * 画的线.
 * 
 * @author user
 * 
 */
public class DrawableLine {
	public static String LINETYPE_HORIZONTAL = "horizontal";
	public static String LINETYPE_VERTICAL = "vertical";
	public static String LINETYPE_FREE = "free";

	/**
	 * 线类型.横线竖线或任意
	 */
	public String linetype = LINETYPE_HORIZONTAL;
	public int width = 1;
	public Point p1;
	public Point p2;

	int captureoffset = 3;
	int capturemode=0;

	
	public DrawableLine() {
		
	}
	
	public DrawableLine(String linetype) {
		super();
		this.linetype = linetype;
	}

	public void draw(Graphics2D g2) {
		Stroke oldstroke = g2.getStroke();
		BasicStroke stroke = new BasicStroke(width);
		Color oldc = g2.getColor();
		g2.setStroke(stroke);
		g2.setColor(Color.BLACK);
		g2.drawLine(p1.x, p1.y, p2.x, p2.y);
		g2.setStroke(oldstroke);
		g2.setColor(oldc);
	}

	/**
	 * 是否鼠标进入? 现在使用矩形判断,只能处理横线或竖线
	 * 
	 * @param p
	 * @return capturemode
	 * 0-没有选中
	 * 1 -第1顶点
	 * 2 -第2顶点
	 * 3 - 中间
	 */
	public int isMouseenter(Point p) {
		Rectangle r = null;

		if (linetype.equals(LINETYPE_HORIZONTAL)) {
			r = new Rectangle(p1.x-captureoffset, p1.y - captureoffset, p2.x - p1.x+2*captureoffset, p2.y
					- p1.y + 2 * captureoffset);
		} else if (linetype.equals(LINETYPE_VERTICAL)) {
			r = new Rectangle(p1.x-captureoffset, p1.y-captureoffset, p2.x - p1.x + 2 * captureoffset, p2.y
					- p1.y + 2*captureoffset);
		} else {
			// 斜线
			return capturemode=0;
		}
		if(!r.contains(p)){
			//System.out.println("not contains");
			return 0;
		}
		if(enterPoint(p1, p) ){
			capturemode=1;
		}else if(enterPoint(p2, p)){
			capturemode=2;
		}else{
			capturemode=3;
		}
		//System.out.println("capturemode="+capturemode);
		return capturemode;
	}
	
	boolean enterPoint(Point p,Point mousep){
		Rectangle r = new Rectangle(p.x-captureoffset,p.y-captureoffset,2*captureoffset,2*captureoffset);
		return r.contains(mousep);
	}

	public void write(PrintWriter out) {
		out.println("<drawableline>");
		out.println("<linetype>"+linetype+"</linetype>");
		out.println("<width>"+width+"</width>");
		out.println("<x1>"+p1.x+"</x1>");
		out.println("<y1>"+p1.y+"</y1>");
		out.println("<x2>"+p2.x+"</x2>");
		out.println("<y2>"+p2.y+"</y2>");
		out.println("</drawableline>");
	}

	public void read(BufferedReader rd)throws Exception {
		String line=rd.readLine();
		linetype=getXmlvalue(line);
		
		line=rd.readLine();
		width=Integer.parseInt(getXmlvalue(line));

		p1=new Point();
		line=rd.readLine();
		p1.x=Integer.parseInt(getXmlvalue(line));
		line=rd.readLine();
		p1.y=Integer.parseInt(getXmlvalue(line));
		
		p2=new Point();
		line=rd.readLine();
		p2.x=Integer.parseInt(getXmlvalue(line));
		line=rd.readLine();
		p2.y=Integer.parseInt(getXmlvalue(line));

		//读进最后一行</drawableline>
		line=rd.readLine();
}
	
	String getXmlvalue(String line) {
		int p = line.indexOf(">");
		int p1 = line.indexOf("<", p);
		return line.substring(p + 1, p1);
	}

	
}
