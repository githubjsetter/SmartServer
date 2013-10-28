package com.smart.bi.client.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.smart.bi.client.design.BITableV_def.Mergeinfo;
import com.smart.bi.client.design.link.Linkinfo;
import com.smart.bi.client.design.param.BIReportparamdefine;
import com.smart.client.system.Clientframe;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.SplitGroupInfo;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.runop.Oplauncher;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.gui.ste.Steframe;

/**
 * 每页信息
 * 
 * @author user
 * 
 */
public class BIPage {
	/**
	 * 每行的信息.
	 */
	int rowtypes[] = null;

	/**
	 * 对应dbtablemodel中的行号.
	 */
	int datarowindexes[] = null;

	/**
	 * 对于垂直表中的行号.
	 */
	int definerowindexes[] = null;

	/**
	 * 数据行的实际每行行高.
	 */
	int rowheights[] = null;

	int rowcount = 0;
	BITableV_def tablevdef = null;
	DBTableModel datadm = null;
	BICellCalcer calcer;

	/**
	 * 不能画竖线的行列 rowXcol
	 */
	HashMap<String, String> cannotvlinemap = new HashMap<String, String>();

	/**
	 * 不能画横线的行列 rowXcol
	 */
	HashMap<String, String> cannothlinemap = new HashMap<String, String>();

	Category logger = Category.getInstance(BIPage.class);

	/**
	 * 每个单元格的位置.用于mousemove或mouseclick时,确定是哪个cell被点了.
	 */
	HashMap<Rectangle, Cellpositioninfo> cellpositionmap = new HashMap<Rectangle, Cellpositioninfo>();

	boolean printing = false;

	public void dump() {
		System.out.println("===========dump page=============");
		System.out.println("共" + rowtypes.length + "行");

		for (int i = 0; i < rowtypes.length; i++) {
			System.out.print(i + ":\t");
			System.out.print(getRowtypeString(rowtypes[i]));
			System.out.print("\t" + definerowindexes[i]);
			System.out.print("\t" + datarowindexes[i]);
			System.out.println("\t" + rowheights[i]);
		}
	}

	private String getRowtypeString(int rowtype) {
		if (rowtype == BITableV_def.ROWTYPE_DATA) {
			return "数据行";
		} else if (rowtype == BITableV_def.ROWTYPE_FOOT) {
			return "表尾";
		} else if (rowtype == BITableV_def.ROWTYPE_GROUP) {
			return "分组";
		} else if (rowtype == BITableV_def.ROWTYPE_HEAD) {
			return "表头";
		} else {
			return "bad rowtype " + rowtype;
		}

	}

	public int getRenderheight() {
		int h = 0;
		if (tablevdef.isDrawgrid()) {
			h += tablevdef.getGridwidth();
		}
		for (int i = 0; i < rowheights.length; i++) {
			h += rowheights[i];
			if (tablevdef.isDrawgrid()) {
				h++;
				h += tablevdef.getGridwidth() - 1;
			}
		}
		return h;
	}

	public int getRenderwidth() {
		int w = 0;
		int gridwidth = 0;
		if (tablevdef.isDrawgrid()) {
			gridwidth = tablevdef.getGridwidth();
		}
		w += gridwidth;
		for (int c = 0; c < tablevdef.getColwidths().length; c++) {
			w += tablevdef.getColwidths()[c] + gridwidth;
		}
		return w;
	}

	public void paint(Graphics2D g2, BITableV_def tablevdef) {
		/*
		 * if(true){ g2.setColor(Color.RED); g2.drawLine(1, 25, 35, 25);
		 * 
		 * g2.translate(1, 1); g2.setColor(Color.black);
		 * g2.drawString("中华人民共和国", 1, 30);
		 * 
		 * return; }
		 */
		cellpositionmap.clear();

		// 开始画.
		this.tablevdef = tablevdef;
		this.datadm = datadm;
		cannotvlinemap.clear();
		cannothlinemap.clear();

		BasicStroke stroke = new BasicStroke(1);
		g2.setStroke(stroke);

		int w = tablevdef.getRenderwidth();
		int h = getRenderheight();
		Color oldc = g2.getColor();
		g2.setColor(tablevdef.getBgcolor());
		g2.fillRect(0, 0, w, h);
		int x, y;

		g2.setColor(tablevdef.getGridcolor());
		// 画数据.
		y = 0;
		if (tablevdef.isDrawgrid()) {
			// 横线
			// g2.drawLine(0, 0, w - 1, 0);
			y += tablevdef.getGridwidth();
		}

		// 画每个单元格
		for (int i = 0; i < rowtypes.length; i++) {
			BICell[] cells = tablevdef.getCells()[definerowindexes[i]];
			x = 0;
			if (tablevdef.isDrawgrid()) {
				x += tablevdef.getGridwidth();
			}
			for (int c = 0; c < tablevdef.getColwidths().length
					&& c < cells.length; c++) {
				Rectangle rect = new Rectangle(x, y,
						tablevdef.getColwidths()[c], rowheights[i]);
				int definerow = definerowindexes[i];
				int mergeindex = tablevdef.isMergecell(definerow, c);
				if (mergeindex > 0) {
					// 合并的
				} else {
					// 记录有链接的cell位置
					if (!isPrinting() && cells[c].hasLink()) {
						Cellpositioninfo cellposinfo = new Cellpositioninfo();
						cellposinfo.rect = rect;
						cellposinfo.cell = cells[c];
						cellposinfo.dmrow = datarowindexes[i];
						cellpositionmap.put(rect, cellposinfo);
					}
					cells[c].setPrinting(isPrinting());
					cells[c].setSize(new Dimension(tablevdef.getColwidths()[c],
							rowheights[i]));
					cells[c].setDbtablemode(datadm);
					cells[c].setCalcer(calcer);
					Graphics2D tmpg = (Graphics2D) g2.create(x, y, tablevdef
							.getColwidths()[c], rowheights[i]);
					cells[c].drawCell(tmpg, datarowindexes[i]);
					tmpg.dispose();
				}
				x += tablevdef.getColwidths()[c];
				if (tablevdef.isDrawgrid()) {
					// 竖线
					// g2.drawLine(x, y, x, y + rowheights[i]-1);
					if (i == 0) {
						// System.out.println("!!!!x="+x);
					}
					x += tablevdef.getGridwidth();
				}

			}

			y += rowheights[i];
			if (tablevdef.isDrawgrid()) {
				// 横线
				// System.out.println("!!!!!!!!!y="+y);
				// g2.drawLine(0, y, w - 1, y);
				y += tablevdef.getGridwidth();
			}
		}

		// 最左
		if (tablevdef.isDrawgrid()) {
			// g2.drawLine(0, 0, 0, y);
		}

		// 画组合并的组列
		drawGroupMergeColumn(g2, calcer);

		drawMergeColumn(g2, calcer);

		if (tablevdef.isDrawgrid()) {
			drawGrid(g2);
		}

		g2.setColor(oldc);
	}

	/**
	 * 画合并列
	 * 
	 * @param g2
	 * @param calcer
	 */
	void drawMergeColumn(Graphics2D g2, BICellCalcer calcer) {
		int x, y = 0;
		if (tablevdef.isDrawgrid()) {
			// 横线
			y += tablevdef.getGridwidth();
		}

		for (int i = 0; i < rowtypes.length; i++) {
			BICell[] cells = tablevdef.getCells()[definerowindexes[i]];
			x = 0;
			if (tablevdef.isDrawgrid()) {
				x += tablevdef.getGridwidth();
			}

			for (int c = 0; c < tablevdef.getColwidths().length; c++) {
				int definerow = definerowindexes[i];
				int mergeindex = tablevdef.isMergecell(definerow, c);
				if (mergeindex == 0) {

				} else if (mergeindex == 2) {

				} else {
					// 取合并信息
					Mergeinfo minfo = tablevdef.getMergeinfo(definerow, c);
					// 求宽
					int rectwidth = 0;
					for (int k = 0; k < minfo.columncount; k++) {
						if (c + k >= tablevdef.getColwidths().length)
							break;
						rectwidth += tablevdef.getColwidths()[c + k];
						if (k > 0 && tablevdef.isDrawgrid())
							rectwidth += tablevdef.getGridwidth();
						for (int j = 0; j < minfo.rowcount - 1; j++) {
							// 在中间的列都不能有右边的竖线
							cannothlinemap.put(String.valueOf(i) + "X"
									+ String.valueOf(c + k), "");
						}
					}

					// 求高
					int rectheight = 0;
					for (int k = 0; k < minfo.rowcount; k++) {
						int tmprow = i + k;
						if (tmprow >= rowtypes.length)
							break;
						rectheight += rowheights[i + k];
						if (k > 0 && tablevdef.isDrawgrid())
							rectheight += tablevdef.getGridwidth();
						for (int j = 0; j < minfo.columncount - 1; j++) {
							// 在中间的列都不能有右边的竖线
							cannotvlinemap.put(String.valueOf(tmprow) + "X"
									+ String.valueOf(c + j), "");
						}
					}

					// 区域
					Rectangle newr = new Rectangle();
					newr.x = x;
					newr.y = y;
					newr.width = rectwidth;
					newr.height = rectheight;

					// g2.fillRect(x, y, rectwidth, rectheight);
					// g2.drawLine(x, 25, x+rectwidth-1,25);
					// System.out.println("fill
					// rect,x="+x+",y="+y+",rect="+rectwidth+"x"+rectheight);

					cells[c].setSize(new Dimension(rectwidth, rectheight));
					cells[c].setDbtablemode(datadm);
					cells[c].setCalcer(calcer);

					Graphics2D tmpg = (Graphics2D) g2.create(x, y, rectwidth,
							rectheight);
					cells[c].drawCell(tmpg, datarowindexes[i]);
					// tmpg.setColor(Color.WHITE);
					// tmpg.fillRect(0, 0, rectwidth-1, rectheight);

					// g2.setColor(Color.WHITE);
					// g2.fillRect(0, 0, rectwidth-1, rectheight);
					// g2.setColor(Color.red);
					// g2.drawLine(0, 25, 35, 25);
				}

				x += tablevdef.getColwidths()[c];
				if (tablevdef.isDrawgrid()) {
					x += tablevdef.getGridwidth();
				}

			}
			y += rowheights[i];
			if (tablevdef.isDrawgrid()) {
				y += tablevdef.getGridwidth();
			}

		}
	}

	/**
	 * 画列的合并
	 * 
	 * @param g2
	 */
	void drawGroupMergeColumn(Graphics2D g2, BICellCalcer calcer) {
		Enumeration<SplitGroupInfo> en = tablevdef.getGroupinfos().elements();
		while (en.hasMoreElements()) {
			SplitGroupInfo ginfo = en.nextElement();
			String groupcol = ginfo.getGroupcolumn();
			mergeGroupColumn(g2, groupcol, ginfo.getLevel(), calcer);
		}
	}

	void mergeGroupColumn(Graphics2D g2, String groupcolname,
			int targetgrouplevel, BICellCalcer calcer) {
		// 找到这列的位置
		int targetcol = -1;
		for (int i = 0; i < tablevdef.getRowcount(); i++) {
			if (tablevdef.getRowtypes()[i] != BITableV_def.ROWTYPE_DATA)
				continue;
			for (int c = 0; c < tablevdef.getColcount(); c++) {
				if (tablevdef.getCells()[i][c].getExpr().equals(
						"{" + groupcolname + "}")) {
					targetcol = c;
					break;
				}
			}
		}
		if (targetcol < 0) {
			return;
		}

		// 计算targetcol一个组值的占用的区域大小
		int groupcolstartx, y;
		groupcolstartx = 0;
		y = 0;
		if (tablevdef.isDrawgrid()) {
			groupcolstartx += tablevdef.getGridwidth();
			y += tablevdef.getGridwidth();
		}

		int groupcolstarty = -1;
		int groupcolendy = 0;

		for (int c = 0; c < targetcol; c++) {
			groupcolstartx += tablevdef.getColwidths()[c];
			if (tablevdef.isDrawgrid()) {
				groupcolstartx += tablevdef.getGridwidth();
			}
		}

		BICell groupcell = null;
		int grouprow = -1;
		for (int i = 0; i < rowtypes.length; i++) {

			if (rowtypes[i] == BITableV_def.ROWTYPE_DATA) {
				if (groupcolstarty < 0) {
					// 刚开始
					groupcolstarty = y;
					groupcell = tablevdef.getCells()[definerowindexes[i]][targetcol];
					grouprow = i;
				}
				groupcolendy = y + rowheights[i];
				// 不要横线了.
				cannothlinemap.put(String.valueOf(i) + "X"
						+ String.valueOf(targetcol), "");

			} else if (groupcolstarty >= 0
					&& rowtypes[i] == BITableV_def.ROWTYPE_GROUP) {
				// 换组了吗?
				int dmrow = datarowindexes[i];
				if (datadm.getRecordThunk(dmrow).getGrouplevel() == targetgrouplevel) {
					// 换组了
					int rheight = groupcolendy - groupcolstarty;
					Rectangle rect = new Rectangle(groupcolstartx,
							groupcolstarty,
							tablevdef.getColwidths()[targetcol], rheight);

					Color tmpc = g2.getColor();
					g2.setColor(tablevdef.getBgcolor());
					g2.fillRect(rect.x, rect.y, rect.width, rect.height);
					g2.setColor(tmpc);
					// 显示值
					Graphics2D tmpg = (Graphics2D) g2.create(rect.x, rect.y,
							rect.width, rect.height);
					groupcell.setSize(new Dimension(rect.width, rect.height));
					groupcell.drawCell(tmpg, datarowindexes[grouprow]);
					tmpg.dispose();

					groupcolstarty = -1;
					groupcolendy = 0;

					// 上行需要画横线
					cannothlinemap.remove(String.valueOf(i - 1) + "X"
							+ String.valueOf(targetcol));
				} else {
					groupcolendy = y + rowheights[i];
					// 不要横线了.
					cannothlinemap.put(String.valueOf(i) + "X"
							+ String.valueOf(targetcol), "");

				}

			}
			y += rowheights[i];
			if (tablevdef.isDrawgrid()) {
				y += tablevdef.getGridwidth();
			}
		}
		// 最后一行肯定要横线的
		cannothlinemap.remove(String.valueOf(rowtypes.length - 1) + "X"
				+ String.valueOf(targetcol));

		if (groupcolstarty >= 0) {
			Color tmpc = g2.getColor();
			g2.setColor(tablevdef.getBgcolor());
			// g2.setColor(Color.red);
			int rheight = groupcolendy - groupcolstarty;
			Rectangle rect = new Rectangle(groupcolstartx, groupcolstarty,
					tablevdef.getColwidths()[targetcol], rheight);
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);
			g2.setColor(tmpc);
			Graphics2D tmpg = (Graphics2D) g2.create(rect.x, rect.y,
					rect.width, rect.height);
			groupcell.drawCell(tmpg, datarowindexes[grouprow]);
			tmpg.dispose();
		}

	}

	public void setCalcer(BICellCalcer calcer) {
		this.calcer = calcer;
	}

	public void setDbtablemode(DBTableModel dm) {
		this.datadm = dm;
	}

	/**
	 * 必须统一画线
	 * 
	 * @param g2
	 */
	void drawGrid(Graphics2D g2) {
		// 先画竖线
		int totalh = getRenderheight() - 1;
		// 左边
		g2.setColor(tablevdef.getGridcolor());
		g2.drawLine(0, 0, 0, totalh);
		int gridwidth = tablevdef.getGridwidth();
		int x = gridwidth;
		int y = gridwidth;
		for (int c = 0; c < tablevdef.colcount; c++) {
			y = gridwidth;
			x += tablevdef.getColwidths()[c];
			int starty = tablevdef.getGridwidth();
			int endy = tablevdef.getGridwidth();
			for (int r = 0; r < rowtypes.length; r++) {
				if (cannotvlinemap.get(String.valueOf(r) + "X"
						+ String.valueOf(c)) == null) {
					// 说明可以画
					if (starty == -1) {
						starty = y;
						endy = starty + rowheights[r] - 1;
					} else {
						endy += rowheights[r] + tablevdef.getGridwidth();
					}
				} else {
					// 说明断了,先画原来的
					if (starty >= 0 && endy > starty) {
						g2.drawLine(x, starty, x, endy - 1);
					}
					starty = -1;

				}
				y += rowheights[r] + gridwidth;
			}
			if (starty >= 0 && endy > starty) {
				g2.drawLine(x, starty, x, endy - 1);
			}
			x += gridwidth;
		}

		// 画横线
		int totalw = getRenderwidth() - 1;
		g2.drawLine(0, 0, totalw, 0);

		y = gridwidth;
		for (int r = 0; r < rowtypes.length; r++) {
			int startx = gridwidth;
			int endx = gridwidth;
			y += rowheights[r];
			x = gridwidth;
			for (int c = 0; c < tablevdef.getColwidths().length; c++) {
				if (cannothlinemap.get(String.valueOf(r) + "X"
						+ String.valueOf(c)) == null) {
					if (startx == -1) {
						startx = x;
						endx = startx + tablevdef.getColwidths()[c];
					} else {
						endx += tablevdef.getColwidths()[c] + gridwidth;
					}
				} else {
					if (startx >= 0 && endx > startx) {
						g2.drawLine(startx, y, endx - 1, y);
					}
					startx = -1;
				}
				x += tablevdef.getColwidths()[c];
				x += gridwidth;
			}
			if (startx >= 0 && endx > startx) {
				g2.drawLine(startx, y, endx - 1, y);
			}
			y += gridwidth;
		}

	}

	public static void main(String[] args) {
		BufferedImage img = new BufferedImage(320, 200,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setColor(Color.white);
		g2.fillRect(0, 0, 640, 480);

		Graphics2D tmpg1 = (Graphics2D) g2.create(10, 10, 640, 480);
		tmpg1.rotate(-90 / 360.0 * 2 * Math.PI);
		tmpg1.translate(-320, 0);
		tmpg1.setColor(Color.red);
		tmpg1.drawRect(0, 0, 320, 200);

		tmpg1.setColor(Color.black);
		Font font = new Font("宋体", Font.PLAIN, 30);
		tmpg1.setFont(font);

		tmpg1.drawString("测试文字测试文字测试文字", 10, 50);

		try {
			ImageIO.write(img, "png", new File("font.png"));
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setTablevdef(BITableV_def tablevdef) {
		this.tablevdef = tablevdef;
	}

	/**
	 * 导出excel
	 * 
	 * @param sheet
	 * @param excelr
	 * @return 生成了几行?
	 */
	public int exportExcel(HSSFWorkbook workbook, HSSFSheet sheet, int excelr,
			boolean withhead, boolean withfoot) {
		//
		HSSFFont titlefont = workbook.createFont();
		titlefont.setFontName("宋体");
		titlefont.setBoldweight((short) 700);

		// 画每个单元格
		int newrowct = 0;
		for (int i = 0; i < rowtypes.length; i++) {
			int definerow = definerowindexes[i];
			int rowtype = tablevdef.getRowtypes()[definerow];
			if (!withhead && rowtype == BITableV_def.ROWTYPE_HEAD) {
				continue;
			}
			if (!withfoot && rowtype == BITableV_def.ROWTYPE_FOOT) {
				continue;
			}
			HSSFRow excelrow = sheet.createRow(excelr + newrowct);
			newrowct++;

			BICell[] cells = tablevdef.getCells()[definerow];
			for (int c = 0; c < tablevdef.getColwidths().length
					&& c < cells.length; c++) {
				String value = "";
				int mergeindex = tablevdef.isMergecell(definerow, c);
				if (mergeindex > 0) {
					// 合并的
				} else {
					cells[c].setDbtablemode(datadm);
					cells[c].setCalcer(calcer);
					value = cells[c].calcValue(datarowindexes[i]);
				}
				HSSFCell hssfcell = excelrow.createCell((short) c);
				try {
					setCellvalue(value, workbook, hssfcell,
							cells[c].getAlign(), cells[c].getFontname(),
							cells[c].getFontsize(), cells[c].isBold(), cells[c]
									.isItalic());
				} catch (Exception e) {
					logger.error("error", e);
				}
			}
		}
		return newrowct;
	}

	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	HSSFCellStyle cellstyleleft = null;
	HSSFCellStyle cellstyleright = null;
	HSSFCellStyle cellstylecenter = null;

	void setCellvalue(String v, HSSFWorkbook workbook, HSSFCell hssfcell,
			int align, String fontname, int fontsize, boolean bold,
			boolean italic) throws Exception {

		if (cellstyleleft == null) {
			cellstyleleft = workbook.createCellStyle();
			cellstyleleft.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		}
		if (cellstyleright == null) {
			cellstyleright = workbook.createCellStyle();
			cellstyleright.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		}
		if (cellstylecenter == null) {
			cellstylecenter = workbook.createCellStyle();
			cellstylecenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		}

		HSSFCellStyle cellstyle = null;
		if (align == BICell.ALIGN_LEFT) {
			cellstyle = cellstyleleft;
		} else if (align == BICell.ALIGN_RIGHT) {
			cellstyle = cellstyleright;
		} else {
			cellstyle = cellstylecenter;
		}

		HSSFFont font = workbook.createFont();
		font.setFontName(fontname);
		font.setFontHeightInPoints((short) fontsize);
		// font.setFontHeight((short) fontsize);

		if (bold) {
			font.setBoldweight((short) 700);
		}
		if (italic) {
			font.setItalic(true);
		}

		cellstyle.setFont(font);
		hssfcell.setCellStyle(cellstyle);

		// 日期
		if (v.length() == 10 && v.charAt(4) == '-' && v.charAt(7) == '-') {
			Calendar cal = Calendar.getInstance();
			cal.setTime(df1.parse(v));
			hssfcell.setCellValue(cal);
			cellstyle.setDataFormat((short) 0x16);
			return;
		}

		if (v.length() == 19 && v.charAt(4) == '-' && v.charAt(7) == '-'
				&& v.charAt(13) == ':' && v.charAt(16) == ':') {
			Calendar cal = Calendar.getInstance();
			cal.setTime(df2.parse(v));
			hssfcell.setCellValue(cal);
			cellstyle.setDataFormat((short) 0x16);
			return;
		}

		// 是数字?
		boolean isnumber = false;
		BigDecimal dec = null;
		try {
			dec = new BigDecimal(v);
			// 是数字
			String vs = dec.toPlainString();
			if (vs.startsWith("0") && !vs.startsWith("0.")) {
				isnumber = false;
			} else {
				isnumber = true;
			}
		} catch (Exception e) {
			isnumber = false;
		}

		if (isnumber) {
			double dblv = Double.parseDouble(v);
			hssfcell.setCellValue(dblv);
		} else {
			hssfcell.setCellValue(new HSSFRichTextString(v));
		}
	}

	public boolean isMouseoverlink(int mx, int my) {
		Iterator<Rectangle> it = cellpositionmap.keySet().iterator();
		while (it.hasNext()) {
			Rectangle r = it.next();
			// System.out.println("mx,my="+mx+","+my+",r="+r.x+","+r.y+","+r.width+","+r.height);
			if (r.contains(mx, my)) {
				return true;
			}
		}
		return false;
	}

	public boolean clickLink(int mx, int my, Component parentComp, int compx,
			int compy, BIReportdsDefine dsdefine) {
		Iterator<Rectangle> it = cellpositionmap.keySet().iterator();
		boolean found = false;
		Cellpositioninfo linkposinfo = null;
		while (it.hasNext()) {
			Rectangle r = it.next();
			if (r.contains(mx, my)) {
				linkposinfo = cellpositionmap.get(r);
				// System.out.println("cell="+linkposinfo.cell+",数据行="+linkposinfo.dmrow);
				found = true;
				break;
			}
		}
		if (!found) {
			return false;
		}

		JPopupMenu popmenu = new JPopupMenu();
		Enumeration<Linkinfo> en = linkposinfo.cell.getLinkinfos().elements();
		while (en.hasMoreElements()) {
			Linkinfo linkinfo = en.nextElement();
			JMenuItem item = new JMenuItem(linkinfo.getLinkname());
			popmenu.add(item);
			item.setAction(new LinkMenuAction(linkinfo.getLinkname(),
					linkposinfo, linkinfo, dsdefine));
		}
		popmenu.show(parentComp, compx, compy);
		return true;
	}

	class LinkMenuAction extends AbstractAction {
		Cellpositioninfo cellposinfo;
		Linkinfo linkinfo;
		BIReportdsDefine dsdefine;

		public LinkMenuAction(String name, Cellpositioninfo cellposinfo,
				Linkinfo linkinfo, BIReportdsDefine dsdefine) {
			super(name);
			this.cellposinfo = cellposinfo;
			this.linkinfo = linkinfo;
			this.dsdefine = dsdefine;
			putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println(linkinfo);
			String callopid = linkinfo.getCallopid();
			String callcond = linkinfo.getCallcond();
			BICell cell = cellposinfo.cell;
			int dmrow = cellposinfo.dmrow;

			// 替换数据列
			Enumeration<DBColumnDisplayInfo> en = datadm
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo col = en.nextElement();
				String dbvalue = "";
				if (dmrow >= 0) {
					dbvalue = datadm.getItemValue(dmrow, col.getColname());
				}
				callcond = BIReportdsDefine.replaceParam(callcond, col
						.getColname(), dbvalue);
			}
			// 替换参数
			for (int i = 0; i < dsdefine.params.size(); i++) {
				BIReportparamdefine p = dsdefine.params.elementAt(i);
				if (p.getInputvalue().length() == 0) {
					continue;
				}
				String inputvalue = p.getInputvalue();
				if (inputvalue.length() == 0) {
					if (p.paramtype.equals("number")) {
						inputvalue = "to_number(null)";
					} else if (p.paramtype.equals("varchar")) {
						inputvalue = "to_char(null)";
					} else if (p.paramtype.equals("datetime")) {
						inputvalue = "to_date(null)";
					}
				} else {
					if (p.paramtype.equals("number")) {
					} else if (p.paramtype.equals("varchar")) {
						inputvalue = inputvalue;
					} else if (p.paramtype.equals("datetime")) {
						inputvalue = inputvalue;
					}
				}
				callcond = BIReportdsDefine.replaceParam(callcond, p.paramname,
						inputvalue);
			}
			logger.debug("要调用功能" + callopid + "的查询条件是" + callcond);

			String sql = "select classname from np_op where opid=" + callopid;
			RemotesqlHelper sh = new RemotesqlHelper();
			String classname = "";
			try {
				DBTableModel dm = sh.doSelect(sql, 0, 1);
				if (dm.getRowCount() == 0) {
					return;
				}
				classname = dm.getItemValue(0, "classname");
			} catch (Exception eeee) {
				logger.error("error", eeee);
				return;
			}

			if (classname.equals("bireport")) {
				BIReportFrame birptfrm = null;
				Clientframe clientframe = Clientframe.getClientframe();
				if (clientframe != null) {
					birptfrm = (BIReportFrame) clientframe.runBIreport(
							callopid, false, false);

				} else {
					birptfrm = new BIReportFrame();
					birptfrm.setAutoquery(false);
					birptfrm.pack();
					birptfrm.setOpid(callopid);
				}

				birptfrm.setAutoquery(false);
				birptfrm.doQuery(callcond);
			} else {
				//将callcond换为wheres.
				try {
					COpframe frm;
					if (Clientframe.getClientframe() != null) {
						frm = Clientframe.getClientframe().runOp(callopid,
								false);
					} else {
						frm = Oplauncher.loadOp(callopid);
					}
					if (frm instanceof Steframe) {
						Steframe stefrm = (Steframe) frm;
						Querycond cond=stefrm.getCreatedStemodel().getCreatedquerycond();
						String wheres = buildwheres(cond,callcond);
						stefrm.getCreatedStemodel().doQuery(wheres);
					} else if (frm instanceof MdeFrame) {
						MdeFrame mdefrm = (MdeFrame) frm;
						Querycond cond=mdefrm.getCreatedMdemodel().getMasterModel().getCreatedquerycond();
						String wheres = buildwheres(cond,callcond);
						mdefrm.getCreatedMdemodel().getMasterModel().doQuery(wheres);
					} else {
						JOptionPane.showMessageDialog((Frame)null, "被调用功能ID"
								+ callopid + "不是能处理的ste和mde类型");
						return;
					}
				} catch (Exception ee1) {
					logger.error("error",ee1);
					JOptionPane.showMessageDialog((Frame)null, "下载被调用功能ID"
							+ callopid+ "失败:" + ee1.getMessage());
					return;
				}

			}
		}

		private String buildwheres(Querycond cond, String callcond) {
			String ss[]=callcond.split(":");
			String wheres="";
			//设置值
			for(int j=0;j<cond.size();j++){
				Querycondline ql=cond.get(j);
				ql.getCbUse().setSelected(false);
			}

			for(int i=0;i<ss.length;i++){
				String line=ss[i];
				int p=line.indexOf("=");
				if(p<0)continue;
				String name=line.substring(0,p);
				String value=line.substring(p+1);
				
				for(int j=0;j<cond.size();j++){
					Querycondline ql=cond.get(j);
					if(ql.getColname().equals(name)){
						ql.setValue(value);
						ql.getCbUse().setSelected(true);
					}
				}
			}

			wheres =cond.getWheres();
			return wheres;
		}
	}

	public class Cellpositioninfo {
		BICell cell;
		Rectangle rect;
		/**
		 * 对应数据行.
		 */
		int dmrow = 0;
	}

	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}

}
