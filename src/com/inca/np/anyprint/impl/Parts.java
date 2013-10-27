package com.inca.np.anyprint.impl;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.inca.np.anyprint.Printplan;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SendHelper;

public class Parts {

	DBTableModel dbmodel = null;
	int currow = 0;
	int printingpageno = 0;
	PrintCalcer calcer = null;
	String plantype;

	PartsMouseHandle mousehandle = null;
	/**
	 * 表头打印一次
	 */
	boolean tableheadonce = false;
	/**
	 * 0 页头 1 表头 2 表身 3 表尾 4 页尾
	 */
	Vector<Partbase> parttable = new Vector<Partbase>();

	Vector<Splitpageinfo> splitpages = new Vector<Splitpageinfo>();

	// Vector<Horizontalplitpageinfo> hsplitpages = new
	// Vector<Horizontalplitpageinfo>();

	/**
	 * 纸名，应为A0-A10 B0-B10或自定义
	 */
	String papername = "A4";

	/**
	 * 是否横向打印呢?
	 */
	boolean landscape = false;

	/**
	 * 纸张宽度 mm
	 */
	float paperwidth = 210f;

	/**
	 * 纸张高度 mm
	 */
	float paperheight = 297f;
	private int horizontalpagect;

	/**
	 * 上下边界，暂无用
	 */
	float vmargin = 0;

	/**
	 * 左右边界 MM
	 */
	float hmargin = 10;

	/**
	 * 是否打印表格线
	 */
	boolean printline = false;

	Printplan plan = null;

	Category logger = Category.getInstance(Parts.class);

	Vector<PartschangListener> changedlisteners = new Vector<PartschangListener>();

	/**
	 * 禁止再打印
	 */
	boolean forbidreprint = true;

	/**
	 * 是否是送打印机?
	 */
	boolean isprinting = false;

	/**
	 * 如果datadirty==true,要重新分页。调用prepareData()后，datadirty=false
	 */
	private boolean datadirty = true;

	/**
	 * 打份第几份
	 */
	int printcopy = 1;

	/**
	 * 打印总份数
	 */
	int printcopys = 1;

	/**
	 * 不要每页都打印表尾.
	 */
	boolean forbidtabletaileverypage = false;

	public boolean isIsprinting() {
		return isprinting;
	}

	public void setIsprinting(boolean isprinting) {
		this.isprinting = isprinting;
	}

	public Parts(Printplan plan) {
		super();
		this.plan = plan;
		calcer = new PrintCalcer(this);
		mousehandle = new PartsMouseHandle(this);
		parttable.setSize(5);
		createDefaults();

	}

	public int getPartcount() {
		return parttable.size();
	}

	public Partbase getPart(int index) {
		return parttable.elementAt(index);
	}

	public String getPlantype() {
		return plantype;
	}

	public void setPlantype(String plantype) {
		this.plantype = plantype;
		for (int i = 0; i < parttable.size(); i++) {
			parttable.get(i).setPlantype(plantype);
		}
	}

	void createDefaults() {
		Headpart head = new Headpart();
		parttable.setElementAt(head, 0);
		head.setHeight(100);

		/*
		 * TextCell testcell = new TextCell("\"测试\"", Cellbase.CELLTYPE_EXPR);
		 * head.addCell(testcell); testcell.setRect(new Rectangle(10, 10, 40,
		 * 20));
		 * 
		 * testcell = new TextCell("\"测试1\"", Cellbase.CELLTYPE_EXPR);
		 * head.addCell(testcell); testcell.setRect(new Rectangle(20, 20, 40,
		 * 20));
		 */
		Tableheadpart tablehead = new Tableheadpart();
		tablehead.setHeight(20);
		parttable.setElementAt(tablehead, 1);

		/**
		 * body的高是指一行数据的高
		 */
		Bodypart body = new Bodypart();
		parttable.setElementAt(body, 2);
		body.setHeight(40);
		/*
		 * testcell = new TextCell("\"测试body\"", Cellbase.CELLTYPE_EXPR); //
		 * body.addCell(testcell); testcell.setRect(new Rectangle(10, 10, 40,
		 * 20));
		 */
		Tablefootpart tablefoot = new Tablefootpart();
		tablefoot.setHeight(20);
		parttable.setElementAt(tablefoot, 3);

		// !!!!!!!!!!!!!!!!!!为了调试,加根线
		/*
		 * DrawableLine line=new DrawableLine(); line.p1=new Point(0,10);
		 * line.p2=new Point(100,10); tablefoot.addDrawableline(line);
		 * 
		 * line=new DrawableLine(DrawableLine.LINETYPE_VERTICAL); line.p1=new
		 * Point(50,10); line.p2=new Point(50,100);
		 * tablefoot.addDrawableline(line);
		 */

		Footpart foot = new Footpart();
		foot.setHeight(100);
		parttable.setElementAt(foot, 4);
		/*
		 * testcell = new TextCell("\"测试foot\"", Cellbase.CELLTYPE_EXPR);
		 * foot.addCell(testcell); testcell.setRect(new Rectangle(10, 10, 40,
		 * 20));
		 */}

	public int getHeight() {
		int h = 0;
		for (int i = 0; i < getPartcount(); i++) {
			h += getPart(i).getHeight();
		}
		return h;
	}

	public Headpart getHead() {
		return (Headpart) getPart(0);
	}

	public Bodypart getBody() {
		return (Bodypart) getPart(2);
	}

	public Tableheadpart getTablehead() {
		return (Tableheadpart) getPart(1);
	}

	public Tablefootpart getTablefoot() {
		return (Tablefootpart) getPart(3);
	}

	public Footpart getFoot() {
		return (Footpart) getPart(4);
	}

	/**
	 * 成批地加列
	 * 
	 * @param table
	 *            列为colname和title ,选中的加
	 * @param addtype
	 *            "中文列名和列值","仅中文列名","仅列值"
	 * @param addpos
	 *            页头 表身 页脚
	 */
	public void addColumns(CTable table, String addtype, String addpos) {
		if (plantype.indexOf("表格") >= 0 && addpos.equals("表身")) {
			// 表格方式，并加到表身。
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			Bodypart body = this.getBody();
			Headpart head = this.getHead();
			int x = body.getMaxwidth();
			int rows[] = table.getSelectedRows();
			for (int i = 0; i < rows.length; i++) {
				int row = rows[i];
				String colname = dbmodel.getItemValue(row, "colname");
				String title = dbmodel.getItemValue(row, "title");
				Columncell textcell = new Columncell("{" + colname + "}", title);
				textcell.setRect(new Rectangle(x, 1, TextCell.DEFAULT_WIDTH,
						TextCell.DEFAULT_HEIGHT));
				body.addCell(textcell);

				x += TextCell.DEFAULT_WIDTH + 1;

			}
			alignTable();
		} else {
			Partbase targetpart = this.getBody();
			if (addpos.equals("页头")) {
				targetpart = this.getHead();
			} else if (addpos.equals("页脚")) {
				targetpart = this.getFoot();
			} else if (addpos.equals("表头")) {
				targetpart = getTablehead();
			} else if (addpos.equals("表尾")) {
				targetpart = getTablefoot();
			} else if (addpos.equals("表身")) {
				targetpart = getBody();
			}
			// 分别增加文本框和字段框
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			int x = targetpart.getMaxwidth();
			int rows[] = table.getSelectedRows();
			for (int i = 0; i < rows.length; i++) {
				int row = rows[i];
				String colname = dbmodel.getItemValue(row, "colname");
				String title = dbmodel.getItemValue(row, "title");

				if (addtype.indexOf("中文") >= 0) {
					TextCell tcell = new TextCell("\"" + title + "\"",
							Cellbase.CELLTYPE_EXPR);
					tcell.setRect(new Rectangle(x, 1, TextCell.DEFAULT_WIDTH,
							TextCell.DEFAULT_HEIGHT));
					targetpart.addCell(tcell);
					x += tcell.getRect().getWidth();
				}

				if (addtype.indexOf("列值") >= 0) {
					Columncell textcell = new Columncell("{" + colname + "}",
							title);
					textcell.setRect(new Rectangle(x, 1,
							TextCell.DEFAULT_WIDTH, TextCell.DEFAULT_HEIGHT));
					targetpart.addCell(textcell);

					x += TextCell.DEFAULT_WIDTH + 1;
				}
			}

		}
	}

	/**
	 * 如果是表格模式，排齐
	 */
	public void alignTable() {
		Bodypart body = this.getBody();
		Tableheadpart tablehead = getTablehead();
		Font headfont = null;
		if (tablehead.getCells().size() > 0) {
			// 取第一个的font
			TextCell tc = (TextCell) tablehead.getCells().firstElement();
			headfont = tc.getFont();
		}
		tablehead.removeAllcell();

		Enumeration<Cellbase> en = body.getCells().elements();
		int x = -1;
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			if (!cell.getCelltype().equals(Cellbase.CELLTYPE_DATA))
				continue;
			if (headfont == null) {
				headfont = ((TextCell) cell).getFont();
			}
			if (x < 0) {
				if (cell.getRect().x < 3) {
					cell.getRect().x = 3;
				}
				x = cell.getRect().x + cell.getRect().width;
				cell.getRect().height = body.getHeight();
			} else {
				cell.getRect().x = x;
				x += cell.getRect().width;
				cell.getRect().height = body.getHeight();
			}
			cell.getRect().y = 0;
			Columncell datacell = (Columncell) cell;
			// 在tablehead中加一个
			TextCell tcell = new TextCell("\"" + datacell.getTitle() + "\"",
					Cellbase.CELLTYPE_EXPR);
			tcell.setFont(headfont);
			tcell.setAlign(TextCell.ALIGN_CENTER);
			Rectangle r = new Rectangle();
			r.x = cell.getRect().x;
			r.y = 0;
			r.width = cell.getRect().width;
			r.height = tablehead.getHeight();
			tcell.setRect(r);
			tablehead.addCell(tcell);
		}
	}

	/**
	 * 打印一页
	 * 
	 * @param g2
	 * @param width
	 *            纸宽pixel
	 * @param height
	 *            纸高pixel
	 * @param pageno
	 *            页号
	 * @param hpageno
	 *            水平页号
	 */
	public int printPage(Graphics2D g2, int width, int height, int pageno,
			int hpageno) {
		// [ x'] = [ m00 m01 m02 ] [ x ] [ m00x + m01y + m02 ]
		// * [ y'] = [ m10 m11 m12 ] [ y ] = [ m10x + m11y + m12 ]
		// * [ 1 ]= [ 0 0 1 ] [ 1 ] [ 1 ]

		// 1 0 offset
		// 0 1 0
		// g2.drawString("----->head line 1<------", 30, 13);
		boolean printtabletail = false;
		if (!isForbidtabletaileverypage()) {
			printtabletail = true;
		} else {
			// 只有最后一页打.
			printtabletail = pageno == getPagecount() - 1;
		}
		printingpageno = pageno;
		try {
			prepareData();
		} catch (Exception e) {
			logger.error("error", e);
			return Printable.NO_SUCH_PAGE;
		}

		g2.setClip(getHmarginPixel(), getVmarginPixel(), getPaperwidthPixel()
				- 2 * getHmarginPixel(), getPaperheightPixel() - 2
				* getVmarginPixel());

		AffineTransform oldtran = g2.getTransform();
		float xoffset = -(float) hpageno * (float) getPaperwidthPixel();
		// 再加左右边界
		xoffset += 2 * getHmarginPixel() * hpageno;

		if (isLandscape()) {
			xoffset = (float) hpageno * (float) getPaperheightPixel();
			xoffset -= 2 * getVmarginPixel() * hpageno;
		}

		if (isLandscape()) {
			xoffset *= oldtran.getScaleY();
		} else {
			xoffset *= oldtran.getScaleX();
		}
		if (isLandscape()) {
			float oldxoffset = (float) oldtran.getTranslateY();
			xoffset -= oldxoffset;
		} else {
			float oldxoffset = (float) oldtran.getTranslateX();
			xoffset += oldxoffset;
		}

		AffineTransform transform = null;
		if (isLandscape()) {
			/*
			 * transform = new AffineTransform(oldtran.getScaleX(), 0f, 0f,
			 * oldtran.getScaleY(), oldtran.getTranslateY(),-xoffset);
			 */
			/*
			 * transform = new AffineTransform(0, -oldtran.getScaleY(), oldtran
			 * .getScaleY(), 0, xoffset, oldtran.getTranslateY());
			 */
			transform = new AffineTransform(0, -oldtran.getScaleY(), oldtran
					.getScaleY(), 0, oldtran.getTranslateX(),
					getPaperheightPixel() * oldtran.getScaleY() + xoffset);
		} else {
			transform = new AffineTransform(oldtran.getScaleX(), 0f, 0f,
					oldtran.getScaleY(), xoffset, oldtran.getTranslateY());
		}

		/*
		 * if (isLandscape()) { transform.rotate(-0.5 * Math.PI);
		 * transform.translate(-getPaperheightPixel(), 0); }
		 */
		g2.setTransform(transform);
		// g2.drawLine(0, 0, getPaperheightPixel(), getPaperwidthPixel());

		// 设置边框是否打印
		if (plantype.indexOf("表格") >= 0) {
			alignTable();
		}

		Splitpageinfo splitpageinfo = null;
		if (pageno >= splitpages.size()) {
			return Printable.NO_SUCH_PAGE;
		} else {
			splitpageinfo = splitpages.elementAt(pageno);
		}

		// 设置打印单号
		if (isprinting) {
			try {
				fillPrintno(splitpageinfo.startrow, splitpageinfo.endrow);
			} catch (Exception e) {
				if (e.getMessage().indexOf("已打印.没有再打印授权,不能打") >= 0) {
					Window w = KeyboardFocusManager
							.getCurrentKeyboardFocusManager().getActiveWindow();
					JOptionPane.showMessageDialog(w, "已打印.没有再打印授权,不能打印!");
					return Printable.NO_SUCH_PAGE;
				}
				logger.error("设置打印单号错误", e);
			}
		}

		Bodypart body = this.getBody();
		Tableheadpart tablehead = getTablehead();
		Headpart head = this.getHead();
		Footpart foot = this.getFoot();
		Tablefootpart tablefoot = getTablefoot();
		int y = 0;
		// 打印页头
		int maxwidth = getMaxwidth();

		Graphics2D headg2 = null;
		headg2 = (Graphics2D) g2.create(0, y, maxwidth, head.getHeight());
		head.print(headg2, dbmodel, splitpageinfo.startrow, 1, width, head
				.getHeight(), calcer);

		y = head.getHeight();
		// 打印表头
		if (!isTableheadonce() || pageno == 0) {
			Graphics2D tableheadg2 = null;
			tableheadg2 = (Graphics2D) g2.create(0, y, maxwidth, tablehead
					.getHeight());
			tablehead.print(tableheadg2, dbmodel, splitpageinfo.startrow, 1,
					width, head.getHeight(), calcer);

			// 打印数据行

			y += tablehead.getHeight() - 1;
		}
		boolean lastpage = false;
		for (int r = splitpageinfo.startrow; r <= splitpageinfo.endrow; r++, y += body
				.getHeight()) {
			Graphics2D bodyg2 = null;
			bodyg2 = (Graphics2D) g2.create(0, y, maxwidth, body.getHeight());
			body.print(bodyg2, dbmodel, r, 1, width, head.getHeight(), calcer);
			// 这里应该有打印表尾的判断
			// if (r == dbmodel.getRowCount() - 1) {
			// 2008.7.13 每页都要打印表尾
			if (r == splitpageinfo.endrow && printtabletail) {

				// 如果禁止每页都打表尾, 只有最后一页打.
				// 表尾
				y += body.getHeight();
				Graphics2D tablefootg2 = null;
				tablefootg2 = (Graphics2D) g2.create(0, y, maxwidth, tablefoot
						.getHeight());
				tablefoot.print(tablefootg2, dbmodel, splitpageinfo.startrow,
						1, maxwidth, head.getHeight(), calcer);
				lastpage = true;

			}
		}

		// 打印页脚
		if (isLandscape()) {
			y = width - foot.getHeight();
		} else {
			y = height - foot.getHeight();
		}
		Graphics2D footg2 = null;
		footg2 = (Graphics2D) g2.create(0, y, maxwidth, foot.getHeight());
		foot.print(footg2, dbmodel, splitpageinfo.startrow, 1, width, head
				.getHeight(), calcer);

		if (printline && plantype.indexOf("表格") >= 0) {
			drawGridline(g2, splitpageinfo, pageno, lastpage);
		}

		g2.setTransform(oldtran);
		return Printable.PAGE_EXISTS;

	}

	void drawGridline(Graphics2D g2, Splitpageinfo splitpageinfo, int pageno,
			boolean lastpage) {
		// 求最小x
		int x = getBody().getMinx();
		int maxx = getBody().getMaxwidth();
		int y = getHead().getHeight();

		// 求高
		int h = 0;

		if (!tableheadonce || pageno == 0) {
			h = getTablehead().getHeight();
		}

		h += getBody().getHeight() * splitpageinfo.getRowcount();
		if (lastpage) {
			h += getTablefoot().getHeight();
		}

		g2.drawRect(x, y, maxx - x, h);

		// 画表头结束横线
		int tmpy = 0;
		if (!tableheadonce || pageno == 0) {
			tmpy = y + getTablehead().getHeight();
			g2.drawLine(x, tmpy, maxx, tmpy);
		} else {
			tmpy = y;
		}
		for (int r = 0; r < splitpageinfo.getRowcount(); r++) {
			tmpy += getBody().getHeight();
			g2.drawLine(x, tmpy, maxx, tmpy);
		}

		// 每列的竖线
		Enumeration<Cellbase> en = getBody().getCells().elements();
		while (en.hasMoreElements()) {
			Cellbase cell = en.nextElement();
			int tmpx = cell.getRect().x + cell.getRect().width;
			int tmph = y + h;
			if (lastpage) {
				tmph -= getTablefoot().getHeight();
			}
			g2.drawLine(tmpx, y, tmpx, tmph);
		}

	}

	/**
	 * 打印一页
	 * 
	 * @param g2
	 * @param dbmodel
	 *            public void printPreview(Graphics2D g2, DBTableModel dbmodel,
	 *            int startrow, int width, int height) { Bodypart body =
	 *            this.getBody(); Tableheadpart tablehead = getTablehead();
	 *            Headpart head = this.getHead(); Footpart foot =
	 *            this.getFoot(); Tablefootpart tablefoot = getTablefoot();
	 *            this.dbmodel = dbmodel; currow = startrow; // 表头 // 表身高度？ int
	 *            bodyheight = height - head.getHeight() - foot.getHeight(); int
	 *            rowsofpage = bodyheight / body.getHeight();
	 * 
	 *            int y = 0; Graphics2D headg2 = (Graphics2D) g2.create(0, y,
	 *            width, head .getHeight()); head.print(headg2, dbmodel,
	 *            startrow, rowsofpage, width, head .getHeight(), calcer);
	 * 
	 *            y = height - foot.getHeight(); // 表尾 Graphics2D footg2 =
	 *            (Graphics2D) g2.create(0, y, width, foot .getHeight());
	 *            foot.print(footg2, dbmodel, startrow, rowsofpage, width, head
	 *            .getHeight(), calcer);
	 * 
	 *            y = head.getHeight(); // 画表头 Graphics2D tableheadg2 =
	 *            (Graphics2D) g2.create(0, y, width, tablehead .getHeight());
	 *            tablehead.print(tableheadg2, dbmodel, 0, rowsofpage, width,
	 *            height, calcer); y += tablehead.getHeight(); for (int i = 0; i
	 *            < rowsofpage; i++, y += body.getHeight()) { int row = startrow
	 *            + i; if (row >= dbmodel.getRowCount()) break; currow = row;
	 *            Graphics2D bodyg2 = (Graphics2D) g2.create(0, y, width,
	 *            bodyheight); body.print(bodyg2, dbmodel, startrow, rowsofpage,
	 *            width, head .getHeight(), calcer); } }
	 */

	public DBTableModel getDbmodel() {
		return dbmodel;
	}

	public int getCurrow() {
		return currow;
	}

	public int getPrintingpageno() {
		return printingpageno;
	}

	public int getPagecount() {
		return splitpages.size();
	}

	public void onMousemove(Point p) {
		mousehandle.onMousemove(p);
	}

	public void onDrag(Point p) {
		mousehandle.onDrag(p);
	}

	public void onMousePressed(Point p) {
		mousehandle.onMousePressed(p);
	}

	public void onMouseReleased(Point point) {
		mousehandle.onMouseReleased(point);
	}

	public void onCellsizechanged() {
		if (plantype.indexOf("表格") >= 0) {
			alignTable();
		}
	}

	public boolean needRepaint() {
		return mousehandle.needRepaint();
	}

	public void prepareData() throws Exception {
		if (!datadirty)
			return;
		calcPagecount(plan.getDbmodel(), plan.getSplitcolumns());
		calcHorizontalpage();
		notifyChangelistener();
		datadirty = false;
	}

	void calcHorizontalpage() {
		int maxw = this.getMaxwidth();
		double dblmargin = hmargin / 25.4 * 72.0;
		int printablew;
		if (isLandscape()) {
			printablew = this.getPaperheightPixel() - 2 * (int) dblmargin;
		} else {
			printablew = this.getPaperwidthPixel() - 2 * (int) dblmargin;
		}
		horizontalpagect = maxw / printablew;
		if (maxw % printablew != 0)
			horizontalpagect++;
	}

	/**
	 * 分页
	 */
	private void calcPagecount(DBTableModel dbmodel, String splitpagecolumns[]) {
		splitpages.clear();
		Bodypart body = this.getBody();
		Tableheadpart tablehead = getTablehead();
		Headpart head = this.getHead();
		Footpart foot = this.getFoot();
		Tablefootpart tablefoot = getTablefoot();
		this.dbmodel = dbmodel;
		String splitkeyword = "";

		int totalheight = this.getPaperheightPixel();
		if (isLandscape()) {
			totalheight = getPaperwidthPixel();
		}
		int bodymaxheight = totalheight - head.getHeight() - foot.getHeight();

		int thisbodyheight = 0;
		int startrow = 0;
		int r = 0;
		for (r = 0; r < dbmodel.getRowCount(); r++) {
			StringBuffer sb = new StringBuffer();
			for (int k = 0; k < splitpagecolumns.length; k++) {
				sb.append("<" + dbmodel.getItemValue(r, splitpagecolumns[k])
						+ ">");
			}
			boolean splitpageflag = false;
			if (r > 0) {
				if (!splitkeyword.equals(sb.toString())) {
					splitpageflag = true;
				}
			}
			splitkeyword = sb.toString();

			if (r == 0) {
				startrow = r;
				thisbodyheight = this.getTablehead().getHeight();
			}

			// /最后一行
			boolean blastrow = false;
			if (r == dbmodel.getRowCount() - 1) {
				blastrow = true;
/*				if (thisbodyheight + body.getHeight()
						+ getTablefoot().getHeight() > bodymaxheight) {
					Splitpageinfo page = new Splitpageinfo();
					splitpages.add(page);
					page.startrow = startrow;
					page.endrow = r - 1; // 最后一行转下页
					page = new Splitpageinfo();
					splitpages.add(page);
					page.startrow = r;
					page.endrow = r;
				} else {
					Splitpageinfo page = new Splitpageinfo();
					splitpages.add(page);
					page.startrow = startrow;
					page.endrow = r;
				}
*/
				}

			// 2008.7.13 改为每页都显示表尾合计。
			// 2009.7.22 改为checkbox设定是否每页都打表尾.
			int tmph = 0;
			if (isForbidtabletaileverypage()) {
				//只在最后一页才打表尾.
				if (blastrow) {
					tmph = thisbodyheight + body.getHeight()
							+ getTablefoot().getHeight();
				} else {
					tmph = thisbodyheight + body.getHeight();

				}
			} else {
				//每页都打表尾.
				tmph = thisbodyheight + body.getHeight()
						+ getTablefoot().getHeight();
			}

			if (splitpageflag || tmph > bodymaxheight) {
				Splitpageinfo page = new Splitpageinfo();
				splitpages.add(page);
				page.startrow = startrow;
				page.endrow = r - 1;
				startrow = r;
				if (tableheadonce) {
					thisbodyheight = 0;
				} else {
					thisbodyheight = this.getTablehead().getHeight();
				}
			}
			if (r == dbmodel.getRowCount() - 1) {
				if (thisbodyheight + body.getHeight()
						+ getTablefoot().getHeight() > bodymaxheight) {
					Splitpageinfo page = new Splitpageinfo();
					splitpages.add(page);
					page.startrow = startrow;
					page.endrow = r - 1;
					// 最后一行转下页
					page = new Splitpageinfo();
					splitpages.add(page);
					page.startrow = r;
					page.endrow = r;
				} else {
					Splitpageinfo page = new Splitpageinfo();
					splitpages.add(page);
					page.startrow = startrow;
					page.endrow = r;
				}
			} else {
				thisbodyheight += body.getHeight();
			}
		}
		int m;
		m = 3;
	}

	public void delActivecell() {
		mousehandle.delActivecell();
		if (plantype.indexOf("表格") >= 0) {
			alignTable();
		}
	}

	/**
	 * 切分的页面信息
	 * 
	 * @author Administrator
	 * 
	 */
	class Splitpageinfo {
		/**
		 * 数据开始行
		 */
		public int startrow;
		/**
		 * 数据结束行
		 */
		public int endrow;

		public int getRowcount() {
			return endrow - startrow + 1;
		}
	}

	/**
	 * 水平分页信息
	 * 
	 * @author Administrator
	 * 
	 */
	class Horizontalplitpageinfo {
		/**
		 * 数据开始列
		 */
		public int startcol;
		/**
		 * 数据结束列
		 */
		public int endcol;
	}

	public Partbase getActivepart() {
		return mousehandle.getActivepart();
	}

	public Cellbase getActivecell() {
		return mousehandle.getActivecell();
	}

	public String getPapername() {
		return papername;
	}

	public void setPapername(String papername) {
		this.papername = papername;
	}

	public float getPaperwidth() {
		return paperwidth;
	}

	public void setPaperwidth(float paperwidth) {
		this.paperwidth = paperwidth;
	}

	public float getPaperheight() {
		return paperheight;
	}

	public void setPaperheight(float paperheight) {
		this.paperheight = paperheight;
	}

	/**
	 * 纸宽，点阵
	 * 
	 * @return
	 */
	public int getPaperwidthPixel() {
		return (int) (paperwidth / 25.4f * 72f);
	}

	public int getPaperheightPixel() {
		return (int) (paperheight / 25.4f * 72f);
	}

	public void moveLeft() {
		mousehandle.moveLeft();
	}

	public void moveRight() {
		mousehandle.moveRight();
	}

	public void moveDown() {
		mousehandle.moveDown();
	}

	public void moveUp() {
		mousehandle.moveUp();
	}

	public void write(PrintWriter out) throws Exception {
		out.println("<parts>");
		out.println("<plantype>" + plantype + "</plantype>");
		out.println("<papername>" + getPapername() + "</papername>");
		out.println("<paperwidth>" + getPaperwidth() + "</paperwidth>");
		out.println("<paperheight>" + getPaperheight() + "</paperheight>");
		out.println("<landscape>" + (isLandscape() ? "true" : "false")
				+ "</landscape>");
		out.println("<hmargin>" + getHmargin() + "</hmargin>");
		out.println("<vmargin>" + getVmargin() + "</vmargin>");
		out.println("<printline>" + (isPrintline() ? "true" : "false")
				+ "</printline>");
		out.println("<tableheadonce>" + (isTableheadonce() ? "true" : "false")
				+ "</tableheadonce>");
		out.println("<forbidtabletaileverypage>"
				+ (isForbidtabletaileverypage() ? "true" : "false")
				+ "</forbidtabletaileverypage>");
		for (int i = 0; i < parttable.size(); i++) {
			parttable.get(i).write(out);
		}
		out.println("</parts>");
	}

	public void read(BufferedReader rd) throws Exception {
		String line;
		int partindex = 0;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</parts>")) {
				break;
			}

			if (line.startsWith("<plantype>")) {
				setPlantype(getXmlvalue(line));
			} else if (line.startsWith("<papername>")) {
				setPapername(getXmlvalue(line));
			} else if (line.startsWith("<paperwidth>")) {
				setPaperwidth(Float.parseFloat(getXmlvalue(line)));
			} else if (line.startsWith("<paperheight>")) {
				setPaperheight(Float.parseFloat(getXmlvalue(line)));
			} else if (line.startsWith("<hmargin>")) {
				setHmargin(Float.parseFloat(getXmlvalue(line)));
			} else if (line.startsWith("<vmargin>")) {
				setVmargin(Float.parseFloat(getXmlvalue(line)));
			} else if (line.startsWith("<printline>")) {
				setPrintline(getXmlvalue(line).equals("true"));
			} else if (line.startsWith("<tableheadonce>")) {
				setTableheadonce(getXmlvalue(line).equals("true"));
			} else if (line.startsWith("<part>")) {
				parttable.get(partindex++).read(rd);
			} else if (line.startsWith("<landscape>")) {
				setLandscape(getXmlvalue(line).equals("true"));
			} else if (line.startsWith("<forbidtabletaileverypage>")) {
				setForbidtabletaileverypage(getXmlvalue(line).equals("true"));
			}
		}
	}

	public boolean isForbidtabletaileverypage() {
		return forbidtabletaileverypage;
	}

	public void setForbidtabletaileverypage(boolean forbidtabletaileverypage) {
		this.forbidtabletaileverypage = forbidtabletaileverypage;
	}

	protected String getXmlvalue(String line) {
		int p = line.indexOf(">");
		int p1 = line.indexOf("<", p);
		return line.substring(p + 1, p1);
	}

	protected int getMaxwidth() {
		int max = 0;
		for (int i = 0; i < getPartcount(); i++) {
			max = Math.max(max, getPart(i).getMaxwidth());
		}
		return max;
	}

	public int getHorizontalPagecount() {
		return horizontalpagect;
	}

	public boolean isPrintline() {
		return printline;
	}

	public void setPrintline(boolean printline) {
		this.printline = printline;
	}

	public void setDatadirty(boolean datadirty) {
		this.datadirty = datadirty;
	}

	public void addChangelistener(PartschangListener cl) {
		changedlisteners.add(cl);
	}

	void notifyChangelistener() {
		Enumeration<PartschangListener> en = changedlisteners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().dataChanged();
		}
	}

	public int getHmarginPixel() {
		return (int) (hmargin / 25.4 * 72.0);
	}

	public int getVmarginPixel() {
		return (int) (vmargin / 25.4 * 72.0);
	}

	public float getVmargin() {
		return vmargin;
	}

	public void setVmargin(float vmargin) {
		this.vmargin = vmargin;
	}

	public float getHmargin() {
		return hmargin;
	}

	public void setHmargin(float hmargin) {
		this.hmargin = hmargin;
	}

	public Splitpageinfo getPageinfo(int pageno) {
		return this.splitpages.elementAt(pageno);
	}

	/**
	 * 返填
	 */
	protected void fillPrintno(int startrow, int endrow) throws Exception {
		Enumeration<DataprocRule> en = plan.getProcrule().elements();
		while (en.hasMoreElements()) {
			DataprocRule dataproc = en.nextElement();
			if (dataproc.getRuletype().equals(
					FillprintnoRule.RULETYPE_FILLPRINTNO)) {
				doFillprintno(startrow, endrow, dataproc.getExpr());
			}
		}
	}

	/**
	 * 返填printno，交给服务器处理
	 * 
	 * @param dataproc
	 * @throws Exception
	 */
	protected void doFillprintno(int startrow, int endrow, String expr)
			throws Exception {
		String ss[] = expr.split(":");
		String serialnoid = ss[0];
		String tablename = ss[1];
		String fillcolname = ss[2];
		String pkcolname = ss[3];
		String dbmodelcolname = ss[4];
		String printflagcolname = "";
		String printmanidcolname = "";
		String printdatecolname = "";
		String tablename1 = "";
		String pkcolname1 = "";
		String dbmodelcolname1 = "";

		if (ss.length > 5) {
			printflagcolname = ss[5];
		}
		if (ss.length > 6) {
			printmanidcolname = ss[6];
		}
		if (ss.length > 7) {
			printdatecolname = ss[7];
		}
		if (ss.length > 8) {
			tablename1 = ss[8];
		}
		if (ss.length > 9) {
			pkcolname1 = ss[9];
		}
		if (ss.length > 10) {
			dbmodelcolname1 = ss[10];
		}

		boolean alreadyfilled = true;
		// 检查本地数据是否已经填写过了。
		for (int r = startrow; r <= endrow; r++) {
			String fillv = dbmodel.getItemValue(r, fillcolname);
			if (fillv == null || fillv.length() == 0) {
				alreadyfilled = false;
				break;
			}
		}

		if (alreadyfilled)
			return;

		ClientRequest req = new ClientRequest("npclient:填写打印单号");
		ParamCommand pcmd = new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("serialnoid", serialnoid);
		pcmd.addParam("tablename", tablename);
		pcmd.addParam("fillcolname", fillcolname);
		pcmd.addParam("pkcolname", pkcolname);
		pcmd.addParam("printflagcolname", printflagcolname);
		pcmd.addParam("printmanidcolname", printmanidcolname);
		pcmd.addParam("printdatecolname", printdatecolname);
		pcmd.addParam("tablename1", tablename1);
		pcmd.addParam("pkcolname1", pkcolname1);
		pcmd.addParam("dbmodelcolname1", dbmodelcolname1);
		pcmd.addParam("forbidreprint", forbidreprint ? "true" : "false");

		// 取出本页所有的id,不要重复
		HashMap<String, String> tmpmap = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		for (int r = startrow; r <= endrow; r++) {
			String v = dbmodel.getItemValue(r, dbmodelcolname);
			if (tmpmap.get(v) != null)
				continue;
			tmpmap.put(v, v);
			sb.append(v);
			sb.append(":");
		}
		// 删除最后一个冒号
		if (sb.toString().endsWith(":"))
			sb.deleteCharAt(sb.length() - 1);
		pcmd.addParam("dbmodelvalues", sb.toString());

		// 打印标志字段对应的列
		tmpmap = new HashMap<String, String>();
		sb = new StringBuffer();
		for (int r = startrow; dbmodelcolname1.length() > 0 && r <= endrow; r++) {
			String v = dbmodel.getItemValue(r, dbmodelcolname1);
			if (tmpmap.get(v) != null)
				continue;
			tmpmap.put(v, v);
			sb.append(v);
			sb.append(":");
		}
		// 删除最后一个冒号
		if (sb.toString().endsWith(":"))
			sb.deleteCharAt(sb.length() - 1);
		pcmd.addParam("dbmodelvalues1", sb.toString());

		// 发送到服务器执行
		ServerResponse resp = SendHelper.sendRequest(req);
		String respcmd = resp.getCommand();
		if (!respcmd.startsWith("+OK")) {
			throw new Exception(respcmd);
		}
		ParamCommand resppcmd = (ParamCommand) resp.commandAt(1);
		String serialno = resppcmd.getValue("serialno");
		// 设置serialno
		for (int r = startrow; r <= endrow; r++) {
			dbmodel.setItemValue(r, fillcolname, serialno);
		}

		return;

	}

	public boolean isLandscape() {
		return landscape;
	}

	public void setLandscape(boolean landscape) {
		this.landscape = landscape;
	}

	public boolean isTableheadonce() {
		return tableheadonce;
	}

	public void setTableheadonce(boolean tableheadonce) {
		this.tableheadonce = tableheadonce;
	}

	public boolean isForbidreprint() {
		return forbidreprint;
	}

	public void setForbidreprint(boolean forbidreprint) {
		this.forbidreprint = forbidreprint;
	}

	public int getPrintcopy() {
		return printcopy;
	}

	public void setPrintcopy(int printcopy) {
		this.printcopy = printcopy;
	}

	public int getPrintcopys() {
		return printcopys;
	}

	public void setPrintcopys(int printcopys) {
		this.printcopys = printcopys;
	}

	public static void main(String[] args) {
		BufferedImage img = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_BGR);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		AffineTransform tran = g2.getTransform();
		System.out.println(tran);
		g2.rotate(-0.5 * Math.PI);
		tran = g2.getTransform();
		System.out.println(tran);

	}
}
