package com.inca.np.anyprint.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.PrintWriter;

import com.inca.np.gui.control.DBTableModel;

/**
 * ÿһ��ɻ���Ԫ��
 * @author Administrator
 *
 */
public abstract class Cellbase {
	/**
	 * ������
	 */
	public static String CELLTYPE_DATA="������";
	
	/**
	 * ���ɱ��ʽ��
	 */
	public static String CELLTYPE_EXPR="���ʽ��";

	public static String BARCODE_EAN13="EAN13";
	public static String BARCODE_CODE128="CODE128";
	//public static String BARCODE_DATAMATRIX="DATAMATRIX";
	public static String BARCODE_PDF417="PDF417";

	public static String BARCODE_EANUCC128="EANUCC128";
/**
	 * ������Part�е�λ��
	 */
	protected Rectangle rect;
	
	boolean active;
	Color lightgray=new Color(220,220,220);
	
	//protected PrintCalcer calcer = null;
	protected int row=0;
	protected boolean printable=true;
	boolean printborder=false;
	
	/**
	 * cell������
	 */
	protected String celltype="";
	
	protected String barcodetype="";

	public void draw(Graphics2D g2){
		java.awt.Color oldc=g2.getColor();
		g2.setColor(Color.white);
		g2.fillRect(rect.x, rect.y, rect.width, rect.height);
		if(active){
			g2.setColor(Color.black);
			g2.drawRect(rect.x, rect.y, rect.width, rect.height);
		}else{
			g2.setColor(lightgray);
			g2.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
		g2.setColor(oldc);
	}

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void print(Graphics2D g2, DBTableModel dbmodel, int row,
			int rowsofpage, PrintCalcer calcer) {
		
	}

	public String getCelltype() {
		return celltype;
	}

	public void setCelltype(String celltype) {
		this.celltype = celltype;
	}

	
	public boolean isPrintable() {
		return printable;
	}

	public void setPrintable(boolean printable) {
		this.printable = printable;
	}

	/**
	 * д���ļ���
	 * @param out
	 */
	public abstract void write(PrintWriter out) throws Exception;

	public abstract void read(BufferedReader rd) throws Exception;

	public boolean isPrintborder() {
		return printborder;
	}

	public void setPrintborder(boolean printborder) {
		this.printborder = printborder;
	}

	public String getBarcodetype() {
		return barcodetype;
	}

	public void setBarcodetype(String barcodetype) {
		this.barcodetype = barcodetype;
	}

	
}
