package com.inca.np.anyprint.impl;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.control.SplitGroupInfo.Datacolumn;

/**
 * 表头表身表尾的基类
 * 
 * @author Administrator
 * 
 */
public class Partbase {
	int height;
	Vector<Cellbase> cells = new Vector<Cellbase>();
	
	/**
	 * 横线
	 */
	Vector<DrawableLine> lines = new Vector<DrawableLine>();
	String plantype = "";

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getPlantype() {
		return plantype;
	}

	public void setPlantype(String plantype) {
		this.plantype = plantype;
	}

	public void addCell(Cellbase cell) {
		cells.add(cell);
	}

	public void draw(Graphics2D g2) {
		// 每个元件draw
		Enumeration<Cellbase> en = cells.elements();
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			cell.draw(g2);
		}
		Enumeration<DrawableLine> en1 = lines.elements();
		while (en1.hasMoreElements()) {
			DrawableLine line = en1.nextElement();
			line.draw(g2);
		}

	}

	public Vector<Cellbase> getCells() {
		return cells;
	}

	public int getMaxwidth() {
		// 最宽到哪里？
		int maxx = 0;
		// 每个元件draw
		Enumeration<Cellbase> en = cells.elements();
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			maxx = Math.max(maxx, cell.getRect().x + cell.getRect().width);
		}
		return maxx;

	}

	public int getMinx() {
		// 最宽到哪里？
		int minx = Integer.MAX_VALUE;
		// 每个元件draw
		Enumeration<Cellbase> en = cells.elements();
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			minx = Math.min(minx, cell.getRect().x);
		}
		return minx;

	}

	public void print(Graphics2D g2, DBTableModel dbmodel, int row,
			int rowsofpage, int width, int height, PrintCalcer calcer) {
		// 每个元件draw
		Enumeration<Cellbase> en = cells.elements();
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			cell.print(g2, dbmodel, row, rowsofpage, calcer);
		}

		Enumeration<DrawableLine> en1 = lines.elements();
		while (en1.hasMoreElements()) {
			DrawableLine line = en1.nextElement();
			line.draw(g2);
		}
}

	public void removeAllcell() {
		cells.clear();
	}

	/**
	 * 得到第一个数据列
	 * 
	 * @return
	 */
	public Columncell getFirstcolumncell() {
		Columncell cell = null;
		Enumeration<Cellbase> en = cells.elements();
		while (en.hasMoreElements()) {
			Cellbase tmpc = en.nextElement();
			if (tmpc instanceof Columncell) {
				cell = (Columncell) tmpc;
				break;
			}
		}
		return cell;
	}

	/**
	 * 取左边的
	 * @param p
	 * @return
	 */
	public Cellbase getLeftcell(Cellbase p) {
		int i=-1;
		for(i=0;i<cells.size();i++){
			if(cells.elementAt(i)==p){
				if(i==0)return null;
				return cells.elementAt(i-1);
			}
		}
		return null;
	}

	public int getCellindex(Cellbase c) {
		Enumeration<Cellbase> en = cells.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			Cellbase tmpc = en.nextElement();
			if (c == tmpc)
				return i;
		}
		return -1;
	}

	public void write(PrintWriter out) throws Exception {
		out.println("<part>");
		out.println("<plantype>" + plantype + "</plantype>");
		out.println("<height>" + height + "</height>");
		Enumeration<Cellbase> en = cells.elements();
		while (en.hasMoreElements()) {
			en.nextElement().write(out);
		}
		Enumeration<DrawableLine> en1 = lines.elements();
		while (en1.hasMoreElements()) {
			en1.nextElement().write(out);
		}
		
		out.println("</part>");
	}

	public void read(BufferedReader rd) throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</part>")) {
				break;
			}

			if (line.startsWith("<plantype>")) {
				setPlantype(getXmlvalue(line));
			} else if (line.startsWith("<height>")) {
				setHeight(Integer.parseInt(getXmlvalue(line)));
			} else if(line.startsWith("<cell>")){
				line=rd.readLine();
				String celltype=getXmlvalue(line);
				Cellbase cellbase=null;
				if(celltype.equals(Cellbase.CELLTYPE_DATA)){
					cellbase=new Columncell("","");
					cellbase.read(rd);
				}else if(celltype.equals(Cellbase.CELLTYPE_EXPR)){
					cellbase=new TextCell("");
					cellbase.read(rd);
				}
				addCell(cellbase);
			} else if(line.startsWith("<drawableline>")){
				DrawableLine drawline=new DrawableLine();
				drawline.read(rd);
				lines.add(drawline);
			}
		}
	}

	String getXmlvalue(String line) {
		int p = line.indexOf(">");
		int p1 = line.indexOf("<", p);
		return line.substring(p + 1, p1);
	}
	
	public void addDrawableline(DrawableLine line){
		lines.add(line);
	}

	public Vector<DrawableLine> getLines() {
		return lines;
	}

}
