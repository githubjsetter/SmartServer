package com.smart.platform.anyprint.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;

import org.apache.log4j.Category;

import com.smart.platform.anyprint.BarcodeCreator;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 文本
 * 
 * @author Administrator
 * 
 */
public class TextCell extends Cellbase {
	// 垂直对齐.
	public static final int ALIGN_NORTH = JLabel.NORTH;
	public static final int ALIGN_SOUTH = JLabel.SOUTH;

	Font font = new Font("宋体", Font.PLAIN, 10);
	String expr = "";
	/**
	 * 垂真对齐
	 */
	protected int valign = ALIGN_NORTH;

	Category logger = Category.getInstance(TextCell.class);

	public static int DEFAULT_WIDTH = 80;
	public static int DEFAULT_HEIGHT = 16;

	protected static final int ALIGN_LEFT = JLabel.LEFT;
	protected static final int ALIGN_CENTER = JLabel.CENTER;
	protected static final int ALIGN_RIGHT = JLabel.RIGHT;

	protected int align = ALIGN_LEFT;
	private boolean bold = false;
	private boolean italic = false;
	String format = "";
	int linepadding = 1;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public TextCell(String expr, String celltype) {
		this.expr = expr;
		this.celltype = celltype;
	}

	public TextCell(String expr) {
		this.expr = expr;
		this.celltype = Cellbase.CELLTYPE_EXPR;
	}

	int xpadding = 1;
	int ypadding = 1;

	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawRect(g2, expr);
	}

	protected void drawRect(Graphics2D pg2, String value) {
		if (value == null || value.length() == 0)
			return;

		Graphics2D g2 = (Graphics2D) pg2.create(rect.x, rect.y, rect.width,
				rect.height);

		// tmpg.drawString("test string", 0,13);

		Font oldf = g2.getFont();
		Font newfont = getFont();
		g2.setFont(newfont);
		int hmaxwidth = rect.width - 2 * xpadding;
		FontMetrics fm = g2.getFontMetrics();
		// int stringwidth=fm.stringWidth(value);
		StringsplitInfo ssinfo = splitText(value);

		// 现在计算上下区域的大小.
		int starty = 0;
		int endy = ssinfo.parts.size() * (ssinfo.fontheight + linepadding);
		int blockheight = endy - starty + 1;

		// 计算出最大

		int offsety = 0;
		if (valign == ALIGN_NORTH) {
			// 上对齐.
			offsety = ypadding;
		} else if (valign == ALIGN_CENTER) {
			// 垂直居中
			offsety = (rect.height - 2 * ypadding - blockheight) / 2;
		} else if (valign == ALIGN_SOUTH) {
			offsety = rect.height - ypadding - fm.getHeight();
		}

		int y = offsety;
		Enumeration<String> en = ssinfo.parts.elements();
		while (en.hasMoreElements()) {
			String text = en.nextElement();
			drawHString(g2, text, 0, y + ssinfo.fontasc);
			y += ssinfo.fontheight + linepadding;
		}

		g2.setFont(oldf);
		g2.dispose();
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int align) {
		this.align = align;
	}

	@Override
	public void read(BufferedReader rd) throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</cell>")) {
				break;
			}

			if (line.startsWith("<printable>")) {
				setPrintable(getXmlvalue(line).equals("true"));
			} else if (line.startsWith("<rect>")) {
				readRect(getXmlvalue(line));
			} else if (line.startsWith("<font>")) {
				readFont(getXmlvalue(line));
			} else if (line.startsWith("<align>")) {
				setAlign(Integer.parseInt(getXmlvalue(line)));
			} else if (line.startsWith("<format>")) {
				setFormat(getXmlvalue(line));
			} else if (line.startsWith("<barcodetype>")) {
				setBarcodetype(getXmlvalue(line));
			} else if (line.startsWith("<bold>")) {
				setBold(getXmlvalue(line).equals("true"));
			} else if (line.startsWith("<italic>")) {
				setItalic(getXmlvalue(line).equals("true"));
			} else if (line.startsWith("<expr>")) {
				readExpr(rd);
			}

		}
	}

	protected void readExpr(BufferedReader rd) throws Exception {
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</expr>")) {
				break;
			}
			sb.append(line + "\n");
		}
		setExpr(sb.toString());

	}

	protected void readFont(String line) {
		String ss[] = line.split(":");
		String facename = ss[0];
		int fontsize = Integer.parseInt(ss[1]);
		int fontdecro = Integer.parseInt(ss[2]);
		Font font = new Font(facename, fontdecro, fontsize);
		setFont(font);

	}

	protected void readRect(String line) {
		String ss[] = line.split(":");
		rect = new Rectangle();
		rect.x = Integer.parseInt(ss[0]);
		rect.y = Integer.parseInt(ss[1]);
		rect.width = Integer.parseInt(ss[2]);
		rect.height = Integer.parseInt(ss[3]);
	}

	protected String getXmlvalue(String line) {
		int p = line.indexOf(">");
		int p1 = line.indexOf("<", p);
		return line.substring(p + 1, p1);
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
		int style = Font.PLAIN;
		if (bold)
			style |= Font.BOLD;
		if (italic)
			style |= Font.ITALIC;
		font = new Font(font.getName(), style, font.getSize());
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
		int style = Font.PLAIN;
		if (bold)
			style |= Font.BOLD;
		if (italic)
			style |= Font.ITALIC;
		font = new Font(font.getName(), style, font.getSize());
	}

	/**
	 * 输出到文件 printable rect expr font align
	 */
	@Override
	public void write(PrintWriter out) throws Exception {
		out.println("<cell>");
		out.println("<celltype>" + celltype + "</celltype>");
		out.println("<printable>" + (printable ? "true" : "false")
				+ "</printable>");
		out.println("<rect>" + rect.x + ":" + rect.y + ":" + rect.width + ":"
				+ rect.height + "</rect>");
		int fontdecro = Font.PLAIN;
		out.println("<font>" + font.getName() + ":" + font.getSize() + ":"
				+ fontdecro + "</font>");
		out.println("<align>" + getAlign() + "</align>");
		out.println("<bold>" + (isBold() ? "true" : "false") + "</bold>");
		out.println("<italic>" + (isItalic() ? "true" : "false") + "</italic>");
		out.println("<format>" + format + "</format>");
		out.println("<barcodetype>" + barcodetype + "</barcodetype>");
		out.println("<expr>");
		out.println(expr);
		out.println("</expr>");
		out.println("</cell>");
	}

	public void print(Graphics2D g2, DBTableModel dbmodel, int row,
			int rowsofpage, PrintCalcer calcer) {
		this.row = row;
		String value = "";

		try {
			value = calcer.calc(row, expr);
		} catch (Exception e) {
			logger.error("error", e);
			value = e.getMessage();
		}

		if (format.length() > 0) {
			value = doFormat(value);
		}

		if (barcodetype != null && barcodetype.equals(BARCODE_EAN13)) {
			drawRectEan13(g2, value);
		} else if (barcodetype != null && barcodetype.equals(BARCODE_CODE128)) {
			drawCode128(g2, value);
		//} else if (barcodetype != null && barcodetype.equals(BARCODE_DATAMATRIX)) {
		//	drawDatamatrix(g2,value);
		} else if (barcodetype != null && barcodetype.equals(BARCODE_PDF417)) {
			drawPdf417(g2, value);
		} else if (barcodetype != null && barcodetype.equals(BARCODE_EANUCC128)) {
			drawEANUCC128(g2, value);
		} else {
			drawRect(g2, value);
		}

		if (printborder) {
			g2.setColor(Color.black);
			// 因为drawrect实际是画 x到x+width，所以要减一
			g2.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
		}

	}

	protected void drawRectEan13(Graphics2D g2, String value) {
		try {
			if (value == null)
				return;
			if (value.length() != 12 && value.length() != 13)
				return;
			BarcodeCreator.drawEan13bar(g2, rect, value);
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	protected void drawCode128(Graphics2D g2, String value) {
		try {
			if (value == null)
				return;
			BarcodeCreator.drawCode128(g2, rect, value);
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	protected void drawDatamatrix(Graphics2D g2, String value) {
		try {
			if (value == null)
				return;
			BarcodeCreator.drawDataMatrix(g2, rect, value);
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	protected void drawPdf417(Graphics2D g2, String value) {
		try {
			if (value == null)
				return;
			BarcodeCreator.drawPdf417(g2, rect, value);
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	protected void drawEANUCC128(Graphics2D g2, String value) {
		try {
			if (value == null)
				return;
			BarcodeCreator.drawEANUCC128(g2, rect, value);
		} catch (Exception e) {
			logger.error("Error", e);
		}
	}

	/**
	 * 按number进行format
	 * 
	 * @param value
	 * @return
	 */
	private String doFormat(String value) {
		double v = 0;
		try {
			v = Double.parseDouble(value);
			DecimalFormat dfmt = new DecimalFormat(format);
			return dfmt.format(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	class StringsplitInfo {
		int fontheight = 0;
		int fontasc = 0;
		Vector<String> parts = new Vector<String>();
	}

	StringsplitInfo splitText(String value) {

		Font newfont = getFont();
		int width = rect.width - 2 * xpadding;
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(newfont);
		int fontheight = fm.getAscent() + fm.getDescent();

		StringsplitInfo ssinfo = new StringsplitInfo();
		int tmpw = 0;
		ssinfo.fontheight = fontheight;
		ssinfo.fontasc = fm.getAscent();

		// 先分解

		do {
			boolean sped = false;
			int i = 1;
			for (i = 1; i <= value.length(); i++) {
				int charw = fm.charsWidth(value.toCharArray(), 0, i);
				if (charw > width) {
					if (i == 1) {
						i = 2;// 强行分配一个.
					}
					ssinfo.parts.add(value.substring(0, i - 1));
					value = value.substring(i - 1);
					sped = true;
					break;
				}
			}

			if (!sped) {
				ssinfo.parts.add(value);
				break;
			}
		} while (value.length() > 0);
		return ssinfo;
	}

	void drawHString(Graphics2D g2, String str, int x, int y) {
		FontMetrics fm = g2.getFontMetrics();
		int strwidth = fm.charsWidth(str.toCharArray(), 0, str.length());

		int xoffset = 0;
		if (align == ALIGN_LEFT) {
			xoffset = xpadding;
		} else if (align == ALIGN_CENTER) {
			xoffset = (rect.width - strwidth) / 2;
		} else if (align == ALIGN_RIGHT) {
			xoffset = rect.width - xpadding - strwidth;
		}
		g2.drawString(str, x + xoffset, y);
	}

}
