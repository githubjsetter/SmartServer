package com.inca.npbi.client.design;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import org.apache.log4j.Category;

import sun.font.FontLineMetrics;
import sun.security.krb5.internal.PAData;

import com.inca.np.gui.control.DBTableModel;
import com.inca.npbi.client.design.link.Linkinfo;

/**
 * 单元格.
 * 
 * @author user
 * 
 */
public class BICell implements ReportcanvasPlaceableIF {
	String expr = "";
	Category logger = Category.getInstance(BICell.class);

	public static final int ALIGN_LEFT = JLabel.LEFT;
	public static final int ALIGN_CENTER = JLabel.CENTER;
	public static final int ALIGN_RIGHT = JLabel.RIGHT;

	// 垂直对齐.
	public static final int ALIGN_NORTH = JLabel.NORTH;
	public static final int ALIGN_SOUTH = JLabel.SOUTH;

	//重复
	public static final String REPEAT_FIRSTPAGE = "firstpage";
	public static final String REPEAT_ALWAYS = "always";

	protected int align = ALIGN_CENTER;

	/**
	 * 垂真对齐
	 */
	protected int valign = ALIGN_NORTH;

	private boolean bold = false;
	private boolean italic = false;
	String format = "";
	String fontname = "宋体";
	int fontsize = 11;

	Color color = Color.BLACK;
	int xpadding = 1;
	int ypadding = 1;
	/**
	 * 行距
	 */
	int linepadding = 1;
	int id;

	Dimension size = new Dimension(100, 27);

	DBTableModel dm = null;
	int dmrow = 0;
	BICellCalcer calcer = null;
	
	String repeat=REPEAT_ALWAYS;
	
	/**
	 * 是否送打印机?
	 */
	boolean printing=false;
	
	/**
	 * 调用链接
	 */
	Vector<Linkinfo> linkinfos=new Vector<Linkinfo>();

	public void drawCell(Graphics2D g2, int dmrow) {
		this.dmrow = dmrow;
		Color oldc = g2.getColor();
		g2.setColor(color);

		String value = expr;
		try {
			value = calcer.calc(dmrow, expr);
		} catch (Exception e) {
			logger.error("error", e);
			value = e.getMessage();
		}

		if (format.length() > 0) {
			double dv = 0;
			try {
				dv = Double.parseDouble(value);
				DecimalFormat df = new DecimalFormat(format);
				value = df.format(dv);
			} catch (Exception e) {

			}

		}

		drawRect(g2, value);

		// 从左上角到右下角,显示cell的区域,调试用.
		// g2.drawLine(0, 0, (int)rect.getWidth(),(int)rect.getHeight());
		g2.setColor(oldc);
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Font getFont() {
		int flag = Font.PLAIN;
		if (bold) {
			flag |= Font.BOLD;
		}
		if (italic) {
			flag |= Font.ITALIC;
		}
		Font font = new Font(fontname, flag, fontsize);
		return font;
	}

	class StringsplitInfo {
		int fontheight = 0;
		int fontasc = 0;
		Vector<String> parts = new Vector<String>();
	}

	protected void drawRect(Graphics2D g2, String value) {
		if (value == null || value.length() == 0)
			return;

		// tmpg.drawString("test string", 0,13);

		Font oldf = g2.getFont();
		Font newfont = getFont();
		g2.setFont(newfont);
		int hmaxwidth = size.width - 2 * xpadding;
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
			offsety = (size.height - 2 * ypadding - blockheight) / 2;
		} else if (valign == ALIGN_SOUTH) {
			offsety = size.height - ypadding - fm.getHeight();
		}

		int y = offsety;
		Enumeration<String> en = ssinfo.parts.elements();
		while (en.hasMoreElements()) {
			String text = en.nextElement();
			drawHString(g2, text, 0, y + ssinfo.fontasc );
			y += ssinfo.fontheight + linepadding;
		}

		g2.setFont(oldf);
	}
	
	public String calcValue(int dmrow){
		this.dmrow=dmrow;
		String value = expr;
		try {
			if (calcer != null)
				value = calcer.calc(dmrow, expr);
		} catch (Exception e) {
			logger.error("error", e);
			value = e.getMessage();
		}

		if (format.length() > 0) {
			double dv = 0;
			try {
				dv = Double.parseDouble(value);
				DecimalFormat df = new DecimalFormat(format);
				value = df.format(dv);
			} catch (Exception e) {

			}

		}
		return value;
	}

	/**
	 * 求当前值最大可能显示的框的大小
	 * 
	 * @return
	 */
	public Dimension getMaxsize() {

		String value = expr;
		try {
			if (calcer != null)
				value = calcer.calc(dmrow, expr);
		} catch (Exception e) {
			logger.error("error", e);
			value = e.getMessage();
		}

		if (format.length() > 0) {
			double dv = 0;
			try {
				dv = Double.parseDouble(value);
				DecimalFormat df = new DecimalFormat(format);
				value = df.format(dv);
			} catch (Exception e) {

			}

		}

		StringsplitInfo ssinfo = splitText(value);

		// 现在计算上下区域的大小.
		int starty = 0;
		int endy = ssinfo.parts.size() * (ssinfo.fontheight + linepadding);
		int blockheight = endy - starty + 1;

		// 计算出最大
		Dimension maxsize = new Dimension(1, 1);
		maxsize.width = size.width;
		maxsize.height = blockheight + 2 * ypadding;
		return maxsize;
	}

	StringsplitInfo splitText(String value) {

		Font newfont = getFont();
		int width = size.width - 2 * xpadding;
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(newfont);
		int fontheight = fm.getAscent() + fm.getDescent();

		StringsplitInfo ssinfo = new StringsplitInfo();
		int tmpw = 0;
		ssinfo.fontheight = fontheight;
		ssinfo.fontasc = fm.getAscent();

		// 先分解
		
		do{
			boolean sped=false;
			int i=1;
			for(i=1;i<=value.length();i++){
				int charw=fm.charsWidth(value.toCharArray(), 0, i);
				if(charw>width){
					if(i==1){
						i=2;//强行分配一个.
					}
					ssinfo.parts.add(value.substring(0,i-1));
					value=value.substring(i-1);
					sped=true;
					break;
				}
			}
			
			if(!sped){
				ssinfo.parts.add(value);
				break;
			}
		}while(value.length()>0);
		return ssinfo;
	}

	void drawHString(Graphics2D g2, String str, int x, int y) {
		FontMetrics fm = g2.getFontMetrics();
		int strwidth = 	fm.charsWidth(str.toCharArray(), 0, str.length());
		

		int xoffset = 0;
		if (align == ALIGN_LEFT) {
			xoffset = xpadding;
		} else if (align == ALIGN_CENTER) {
			xoffset = (size.width - strwidth) / 2;
		} else if (align == ALIGN_RIGHT) {
			xoffset = size.width - xpadding - strwidth;
		}
		g2.drawString(str, x + xoffset, y);
		
		//如果有link,画下划线
		if(!isPrinting() && linkinfos.size()>0){
			g2.drawLine(x+ xoffset, y, + xoffset+strwidth, y);
		}
	}

	public int getAlign() {
		return align;
	}

	public void setAlign(int align) {
		this.align = align;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getXpadding() {
		return xpadding;
	}

	public void setXpadding(int xpadding) {
		this.xpadding = xpadding;
	}

	public int getYpadding() {
		return ypadding;
	}

	public void setYpadding(int ypadding) {
		this.ypadding = ypadding;
	}

	public String getFontname() {
		return fontname;
	}

	public void setFontname(String fontname) {
		this.fontname = fontname;
	}

	public int getFontsize() {
		return fontsize;
	}

	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

	public int getValign() {
		return valign;
	}

	public void setValign(int valign) {
		this.valign = valign;
	}

	public void draw(Graphics2D g2, int pageno) {
		drawCell(g2, 0);
	}

	public Dimension getSize() {
		return size;
	}

	public String getType() {
		return id + ":自由单元格";
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public void setCalcer(BICellCalcer calcer) {
		this.calcer = calcer;
	}

	public void setDbtablemode(DBTableModel dm) {
		this.dm = dm;
	}

	public int getPagecount() {
		return 0;
	}

	public void setLayoutstarty(int layoutstarty) {
		// TODO Auto-generated method stub

	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public boolean prepareData() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public static void main(String[] args) {
		BICell cell = new BICell();
		cell.setFontname("宋体");
		cell.setFontsize(14);
		BufferedImage img = new BufferedImage(480, 320,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, 480, 320);
		g2.setColor(Color.black);
		cell.setSize(new Dimension(100, 27));
		String expr="美林(布洛花混悬夜)(儿童退热)";
		cell.setExpr(expr);
		cell.drawRect(g2, expr);
		Dimension maxsize = cell.getMaxsize();
		g2.drawRect(0, 0, maxsize.width, maxsize.height);
		try {
			ImageIO.write(img, "png", new File("font.png"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addLink(String linkname, String callopid, String callopname,
			String cond) {
		Linkinfo linkinfo=new Linkinfo(linkname,callopid,callopname,cond);
		linkinfos.add(linkinfo);
	}

	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}
	
	/**
	 * 有链接吗?
	 * @return
	 */
	public boolean hasLink(){
		return linkinfos.size()>0;
	}

	public Vector<Linkinfo> getLinkinfos() {
		return linkinfos;
	}

	public void addLinkinfo(Linkinfo linkinfo) {
		linkinfos.add(linkinfo);
	}
	
}
