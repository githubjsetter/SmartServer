package com.smart.platform.print.drawable;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.io.PrintWriter;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-5-9 Time: 17:58:54
 * һ��������.
 */
public class PDataline {
	/**
	 * ��������ֽ�ϵĿ�ʼλ��.
	 */
	int x = 10;
	int height = 27;
	int titleheight = 40;

	Vector<PColumnCell> columns = new Vector<PColumnCell>();

	boolean withborder = true;
	int borderwidth = 1;

	/**
	 * ������ʽ ����:asc|desc[����:asc|desc]
	 */
	String sortexpr = "";

	public String getSortexpr() {
		return sortexpr;
	}

	public void setSortexpr(String sortexpr) {
		this.sortexpr = sortexpr;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void addColumn(PColumnCell cell) {
		columns.add(cell);
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWithborder(boolean withborder) {
		this.withborder = withborder;
	}

	public void setBorderwidth(int borderwidth) {
		this.borderwidth = borderwidth;
	}

	public int getX() {
		return x;
	}

	public int getHeight() {
		return height;
	}

	public Vector<PColumnCell> getColumns() {
		return columns;
	}

	public int getTitleheight() {
		return titleheight;
	}

	public void setTitleheight(int titleheight) {
		this.titleheight = titleheight;
	}

	public void paint(Graphics2D g2, DBTableModel dbmodel,
			DBTableModel assistantdbmodel, int startrow, int endrow,
			String columns[]) {
		// ������
		int y = 0;
		if (dbmodel.isCrosstable()) {
			paintCrosstitle(g2, dbmodel, 0, y, columns);
		} else {
			paintTitle(g2, dbmodel, 0, y, columns);
		}
		y += titleheight;
		for (int row = startrow; row <= endrow; row++) {
			paint(g2, dbmodel, row, y, columns, row == endrow);
			y += height;
			if (withborder) {
				y += borderwidth;
			}
		}
	}

	private PColumnCell getColumn(String name) {
		Enumeration<PColumnCell> en = columns.elements();
		while (en.hasMoreElements()) {
			PColumnCell columncell = en.nextElement();
			if (columncell.getColname().equals(name)) {
				return columncell;
			}
		}
		return null;
	}

	private void paintCrosstitle(Graphics2D g2, DBTableModel dbmodel, int row,
			int y, String[] pagecolumns) {
		Vector<DBColumnDisplayInfo> colinfos = dbmodel.getDisplaycolumninfos();
		int x = this.x;
		// ������
		Stroke oldstroke = g2.getStroke();
		BasicStroke stroke = new BasicStroke(borderwidth);
		g2.setStroke(stroke);

		g2.setColor(Color.BLACK);

		String crosstitle = "";
		int crossstartx=-1;
		PColumnCell columncell=null;
		for (int i = 0; i < pagecolumns.length; i++) {
			columncell = getColumn(pagecolumns[i]);
			String colname = columncell.getColname();
			DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(colname);
			if(colinfo==null){
				System.err.println("�Ҳ�����"+colname);
				continue;
			}

			if (withborder && !colinfo.isCrossdata()) {
				if(colinfo!=null && colinfo.isCrossdata()){
					//nothing
				}else{
					// ��ߵ�����
					g2.drawLine(x, y, x, y + titleheight);
				}
			}

			if (colinfo.isCrossdata()) {
				// �����²�
				columncell.paintCrosstitleBottom(g2, dbmodel, row, x, y,
						titleheight);

				// ��С����
				g2.drawLine(x, y + titleheight / 2, x + columncell.getWidth(),
						y + titleheight / 2);

				String title = colinfo.getTitle();
				int p = title.indexOf(":");
				if (p >= 0) {
					title = title.substring(0, p);
				}

				if (!title.equals(crosstitle)) {
					// �������
					if(crossstartx<0){
						crossstartx=x;
					}else{
						//��crosstitle
						int twidth= x - crossstartx;
						columncell.paintCrosstitle(g2,crosstitle,crossstartx,y,twidth,titleheight/2);
						crossstartx=x;
					}

					g2.drawLine(x, y, x, y + titleheight);
					crosstitle = title;
				}else{
	        		//���С����
	                g2.drawLine(x, y+titleheight/2, x, y + titleheight);
				}
				

			} else {
				// �����м�
				columncell.paintTitle(g2, dbmodel, row, x, y, titleheight);
			}

			x += borderwidth;
			x += columncell.getWidth();

			// ���һ������
			if (withborder && i == pagecolumns.length - 1) {
				g2.drawLine(x, y, x, y + titleheight);
			}
		}

		if(crossstartx>0){
			int twidth= x - crossstartx;
			columncell.paintCrosstitle(g2,crosstitle,crossstartx,y,twidth,titleheight/2);
			crossstartx=x;
		}

		// ����ĺ���
		if (withborder) {
			g2.drawLine(this.x, y, x, y);
		}
		g2.setStroke(oldstroke);
	}

	private void paintTitle(Graphics2D g2, DBTableModel dbmodel, int row,
			int y, String[] pagecolumns) {
		int x = this.x;
		// ������
		Stroke oldstroke = g2.getStroke();
		BasicStroke stroke = new BasicStroke(borderwidth);
		g2.setStroke(stroke);

		g2.setColor(Color.BLACK);

		for (int i = 0; i < pagecolumns.length; i++) {
			PColumnCell columncell = getColumn(pagecolumns[i]);
			if (withborder) {
				g2.drawLine(x, y, x, y + titleheight);
				x += borderwidth;
			}

			columncell.paintTitle(g2, dbmodel, row, x, y, titleheight);
			x += columncell.getWidth();

			// ���һ������
			if (withborder && i == pagecolumns.length - 1) {
				g2.drawLine(x, y, x, y + titleheight);
			}
		}
		
		
		// ����ĺ���
		if (withborder) {
			g2.drawLine(this.x, y, x, y);
		}
		g2.setStroke(oldstroke);
	}

	private void paint(Graphics2D g2, DBTableModel dbmodel, int row, int y,
			String[] pagecolumns, boolean islastrow) {
		int x = this.x;
		// ������
		Stroke oldstroke = g2.getStroke();
		BasicStroke stroke = new BasicStroke(borderwidth);
		g2.setStroke(stroke);

		g2.setColor(Color.BLACK);

		for (int i = 0; i < pagecolumns.length; i++) {
			PColumnCell columncell = null;

			Enumeration<PColumnCell> en = columns.elements();
			for (int c = 0; en.hasMoreElements(); c++) {
				columncell = en.nextElement();
				if (columncell.getColname().equals(pagecolumns[i])) {
					columncell.setColindex(c);
					break;
				}
			}

			if (!columncell.isVisible())
				continue;
			// ��ʼ������
			if (withborder) {
				g2.drawLine(x, y, x, y + height);
				x += borderwidth;
			}
			columncell.paint(g2, dbmodel, row, x, y, height);
			x += columncell.getWidth();

			// ���һ������
			if (withborder && i == pagecolumns.length - 1) {
				g2.drawLine(x, y, x, y + height);
			}
		}

		// ����ĺ���
		if (withborder) {
			g2.drawLine(this.x, y, x, y);
			if (islastrow) {
				// ���ĺ���
				g2.drawLine(this.x, y + height, x, y + height);
			}
		}

		g2.setStroke(oldstroke);
	}

	public boolean isWithborder() {
		return withborder;
	}

	public int getBorderwidth() {
		return borderwidth;
	}

	public void setColumns(Vector<PColumnCell> columns) {
		this.columns = columns;
	}

	public int calcBesttitleHeight(Graphics2D g2) {
		int max = 0;
		Enumeration<PColumnCell> en = columns.elements();
		while (en.hasMoreElements()) {
			PColumnCell columnCell = en.nextElement();
			if (!columnCell.isVisible() || columnCell.getWidth() < 10)
				continue;
			int h = columnCell.calcBesttitleHeight(g2);
			if (h > max) {
				max = h;
			}
		}
		return max;
	}

	public void writeReport(PrintWriter out) {
		out.println("<dataline>");
		out.println("x=" + x);
		out.println("height=" + height);
		out.println("titleheight=" + titleheight);
		out.println("withborder=" + (withborder ? "true" : "false"));
		out.println("borderwidth=" + borderwidth);
		out.println("sortexpr=" + sortexpr);

		Enumeration<PColumnCell> en = columns.elements();
		while (en.hasMoreElements()) {
			PColumnCell cell = en.nextElement();
			cell.writeReport(out);
		}
		out.println("</dataline>");
	}

	public void readReport(BufferedReader in) throws Exception {
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("</dataline>")) {
				break;
			}
			if (line.startsWith("x=")) {
				x = Integer.parseInt(line.substring("x=".length()));
			} else if (line.startsWith("height=")) {
				height = Integer.parseInt(line.substring("height=".length()));
			} else if (line.startsWith("titleheight=")) {
				titleheight = Integer.parseInt(line.substring("titleheight="
						.length()));
			} else if (line.startsWith("withborder=")) {
				withborder = line.substring("withborder=".length()).equals(
						"true");
			} else if (line.startsWith("borderwidth=")) {
				borderwidth = Integer.parseInt(line.substring("borderwidth="
						.length()));
			} else if (line.startsWith("sortexpr=")) {
				sortexpr = line.substring("sortexpr=".length());
			} else if (line.startsWith("<cell")) {
				PColumnCell cell = new PColumnCell("");
				addColumn(cell);
				cell.read(in);
			}
		}
	}
}
