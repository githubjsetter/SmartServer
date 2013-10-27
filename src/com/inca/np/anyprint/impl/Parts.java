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
	 * ��ͷ��ӡһ��
	 */
	boolean tableheadonce = false;
	/**
	 * 0 ҳͷ 1 ��ͷ 2 ���� 3 ��β 4 ҳβ
	 */
	Vector<Partbase> parttable = new Vector<Partbase>();

	Vector<Splitpageinfo> splitpages = new Vector<Splitpageinfo>();

	// Vector<Horizontalplitpageinfo> hsplitpages = new
	// Vector<Horizontalplitpageinfo>();

	/**
	 * ֽ����ӦΪA0-A10 B0-B10���Զ���
	 */
	String papername = "A4";

	/**
	 * �Ƿ�����ӡ��?
	 */
	boolean landscape = false;

	/**
	 * ֽ�ſ�� mm
	 */
	float paperwidth = 210f;

	/**
	 * ֽ�Ÿ߶� mm
	 */
	float paperheight = 297f;
	private int horizontalpagect;

	/**
	 * ���±߽磬������
	 */
	float vmargin = 0;

	/**
	 * ���ұ߽� MM
	 */
	float hmargin = 10;

	/**
	 * �Ƿ��ӡ�����
	 */
	boolean printline = false;

	Printplan plan = null;

	Category logger = Category.getInstance(Parts.class);

	Vector<PartschangListener> changedlisteners = new Vector<PartschangListener>();

	/**
	 * ��ֹ�ٴ�ӡ
	 */
	boolean forbidreprint = true;

	/**
	 * �Ƿ����ʹ�ӡ��?
	 */
	boolean isprinting = false;

	/**
	 * ���datadirty==true,Ҫ���·�ҳ������prepareData()��datadirty=false
	 */
	private boolean datadirty = true;

	/**
	 * ��ݵڼ���
	 */
	int printcopy = 1;

	/**
	 * ��ӡ�ܷ���
	 */
	int printcopys = 1;

	/**
	 * ��Ҫÿҳ����ӡ��β.
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
		 * TextCell testcell = new TextCell("\"����\"", Cellbase.CELLTYPE_EXPR);
		 * head.addCell(testcell); testcell.setRect(new Rectangle(10, 10, 40,
		 * 20));
		 * 
		 * testcell = new TextCell("\"����1\"", Cellbase.CELLTYPE_EXPR);
		 * head.addCell(testcell); testcell.setRect(new Rectangle(20, 20, 40,
		 * 20));
		 */
		Tableheadpart tablehead = new Tableheadpart();
		tablehead.setHeight(20);
		parttable.setElementAt(tablehead, 1);

		/**
		 * body�ĸ���ָһ�����ݵĸ�
		 */
		Bodypart body = new Bodypart();
		parttable.setElementAt(body, 2);
		body.setHeight(40);
		/*
		 * testcell = new TextCell("\"����body\"", Cellbase.CELLTYPE_EXPR); //
		 * body.addCell(testcell); testcell.setRect(new Rectangle(10, 10, 40,
		 * 20));
		 */
		Tablefootpart tablefoot = new Tablefootpart();
		tablefoot.setHeight(20);
		parttable.setElementAt(tablefoot, 3);

		// !!!!!!!!!!!!!!!!!!Ϊ�˵���,�Ӹ���
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
		 * testcell = new TextCell("\"����foot\"", Cellbase.CELLTYPE_EXPR);
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
	 * �����ؼ���
	 * 
	 * @param table
	 *            ��Ϊcolname��title ,ѡ�еļ�
	 * @param addtype
	 *            "������������ֵ","����������","����ֵ"
	 * @param addpos
	 *            ҳͷ ���� ҳ��
	 */
	public void addColumns(CTable table, String addtype, String addpos) {
		if (plantype.indexOf("���") >= 0 && addpos.equals("����")) {
			// ���ʽ�����ӵ�����
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
			if (addpos.equals("ҳͷ")) {
				targetpart = this.getHead();
			} else if (addpos.equals("ҳ��")) {
				targetpart = this.getFoot();
			} else if (addpos.equals("��ͷ")) {
				targetpart = getTablehead();
			} else if (addpos.equals("��β")) {
				targetpart = getTablefoot();
			} else if (addpos.equals("����")) {
				targetpart = getBody();
			}
			// �ֱ������ı�����ֶο�
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			int x = targetpart.getMaxwidth();
			int rows[] = table.getSelectedRows();
			for (int i = 0; i < rows.length; i++) {
				int row = rows[i];
				String colname = dbmodel.getItemValue(row, "colname");
				String title = dbmodel.getItemValue(row, "title");

				if (addtype.indexOf("����") >= 0) {
					TextCell tcell = new TextCell("\"" + title + "\"",
							Cellbase.CELLTYPE_EXPR);
					tcell.setRect(new Rectangle(x, 1, TextCell.DEFAULT_WIDTH,
							TextCell.DEFAULT_HEIGHT));
					targetpart.addCell(tcell);
					x += tcell.getRect().getWidth();
				}

				if (addtype.indexOf("��ֵ") >= 0) {
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
	 * ����Ǳ��ģʽ������
	 */
	public void alignTable() {
		Bodypart body = this.getBody();
		Tableheadpart tablehead = getTablehead();
		Font headfont = null;
		if (tablehead.getCells().size() > 0) {
			// ȡ��һ����font
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
			// ��tablehead�м�һ��
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
	 * ��ӡһҳ
	 * 
	 * @param g2
	 * @param width
	 *            ֽ��pixel
	 * @param height
	 *            ֽ��pixel
	 * @param pageno
	 *            ҳ��
	 * @param hpageno
	 *            ˮƽҳ��
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
			// ֻ�����һҳ��.
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
		// �ټ����ұ߽�
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

		// ���ñ߿��Ƿ��ӡ
		if (plantype.indexOf("���") >= 0) {
			alignTable();
		}

		Splitpageinfo splitpageinfo = null;
		if (pageno >= splitpages.size()) {
			return Printable.NO_SUCH_PAGE;
		} else {
			splitpageinfo = splitpages.elementAt(pageno);
		}

		// ���ô�ӡ����
		if (isprinting) {
			try {
				fillPrintno(splitpageinfo.startrow, splitpageinfo.endrow);
			} catch (Exception e) {
				if (e.getMessage().indexOf("�Ѵ�ӡ.û���ٴ�ӡ��Ȩ,���ܴ�") >= 0) {
					Window w = KeyboardFocusManager
							.getCurrentKeyboardFocusManager().getActiveWindow();
					JOptionPane.showMessageDialog(w, "�Ѵ�ӡ.û���ٴ�ӡ��Ȩ,���ܴ�ӡ!");
					return Printable.NO_SUCH_PAGE;
				}
				logger.error("���ô�ӡ���Ŵ���", e);
			}
		}

		Bodypart body = this.getBody();
		Tableheadpart tablehead = getTablehead();
		Headpart head = this.getHead();
		Footpart foot = this.getFoot();
		Tablefootpart tablefoot = getTablefoot();
		int y = 0;
		// ��ӡҳͷ
		int maxwidth = getMaxwidth();

		Graphics2D headg2 = null;
		headg2 = (Graphics2D) g2.create(0, y, maxwidth, head.getHeight());
		head.print(headg2, dbmodel, splitpageinfo.startrow, 1, width, head
				.getHeight(), calcer);

		y = head.getHeight();
		// ��ӡ��ͷ
		if (!isTableheadonce() || pageno == 0) {
			Graphics2D tableheadg2 = null;
			tableheadg2 = (Graphics2D) g2.create(0, y, maxwidth, tablehead
					.getHeight());
			tablehead.print(tableheadg2, dbmodel, splitpageinfo.startrow, 1,
					width, head.getHeight(), calcer);

			// ��ӡ������

			y += tablehead.getHeight() - 1;
		}
		boolean lastpage = false;
		for (int r = splitpageinfo.startrow; r <= splitpageinfo.endrow; r++, y += body
				.getHeight()) {
			Graphics2D bodyg2 = null;
			bodyg2 = (Graphics2D) g2.create(0, y, maxwidth, body.getHeight());
			body.print(bodyg2, dbmodel, r, 1, width, head.getHeight(), calcer);
			// ����Ӧ���д�ӡ��β���ж�
			// if (r == dbmodel.getRowCount() - 1) {
			// 2008.7.13 ÿҳ��Ҫ��ӡ��β
			if (r == splitpageinfo.endrow && printtabletail) {

				// �����ֹÿҳ�����β, ֻ�����һҳ��.
				// ��β
				y += body.getHeight();
				Graphics2D tablefootg2 = null;
				tablefootg2 = (Graphics2D) g2.create(0, y, maxwidth, tablefoot
						.getHeight());
				tablefoot.print(tablefootg2, dbmodel, splitpageinfo.startrow,
						1, maxwidth, head.getHeight(), calcer);
				lastpage = true;

			}
		}

		// ��ӡҳ��
		if (isLandscape()) {
			y = width - foot.getHeight();
		} else {
			y = height - foot.getHeight();
		}
		Graphics2D footg2 = null;
		footg2 = (Graphics2D) g2.create(0, y, maxwidth, foot.getHeight());
		foot.print(footg2, dbmodel, splitpageinfo.startrow, 1, width, head
				.getHeight(), calcer);

		if (printline && plantype.indexOf("���") >= 0) {
			drawGridline(g2, splitpageinfo, pageno, lastpage);
		}

		g2.setTransform(oldtran);
		return Printable.PAGE_EXISTS;

	}

	void drawGridline(Graphics2D g2, Splitpageinfo splitpageinfo, int pageno,
			boolean lastpage) {
		// ����Сx
		int x = getBody().getMinx();
		int maxx = getBody().getMaxwidth();
		int y = getHead().getHeight();

		// ���
		int h = 0;

		if (!tableheadonce || pageno == 0) {
			h = getTablehead().getHeight();
		}

		h += getBody().getHeight() * splitpageinfo.getRowcount();
		if (lastpage) {
			h += getTablefoot().getHeight();
		}

		g2.drawRect(x, y, maxx - x, h);

		// ����ͷ��������
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

		// ÿ�е�����
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
	 * ��ӡһҳ
	 * 
	 * @param g2
	 * @param dbmodel
	 *            public void printPreview(Graphics2D g2, DBTableModel dbmodel,
	 *            int startrow, int width, int height) { Bodypart body =
	 *            this.getBody(); Tableheadpart tablehead = getTablehead();
	 *            Headpart head = this.getHead(); Footpart foot =
	 *            this.getFoot(); Tablefootpart tablefoot = getTablefoot();
	 *            this.dbmodel = dbmodel; currow = startrow; // ��ͷ // ����߶ȣ� int
	 *            bodyheight = height - head.getHeight() - foot.getHeight(); int
	 *            rowsofpage = bodyheight / body.getHeight();
	 * 
	 *            int y = 0; Graphics2D headg2 = (Graphics2D) g2.create(0, y,
	 *            width, head .getHeight()); head.print(headg2, dbmodel,
	 *            startrow, rowsofpage, width, head .getHeight(), calcer);
	 * 
	 *            y = height - foot.getHeight(); // ��β Graphics2D footg2 =
	 *            (Graphics2D) g2.create(0, y, width, foot .getHeight());
	 *            foot.print(footg2, dbmodel, startrow, rowsofpage, width, head
	 *            .getHeight(), calcer);
	 * 
	 *            y = head.getHeight(); // ����ͷ Graphics2D tableheadg2 =
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
		if (plantype.indexOf("���") >= 0) {
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
	 * ��ҳ
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

			// /���һ��
			boolean blastrow = false;
			if (r == dbmodel.getRowCount() - 1) {
				blastrow = true;
/*				if (thisbodyheight + body.getHeight()
						+ getTablefoot().getHeight() > bodymaxheight) {
					Splitpageinfo page = new Splitpageinfo();
					splitpages.add(page);
					page.startrow = startrow;
					page.endrow = r - 1; // ���һ��ת��ҳ
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

			// 2008.7.13 ��Ϊÿҳ����ʾ��β�ϼơ�
			// 2009.7.22 ��Ϊcheckbox�趨�Ƿ�ÿҳ�����β.
			int tmph = 0;
			if (isForbidtabletaileverypage()) {
				//ֻ�����һҳ�Ŵ��β.
				if (blastrow) {
					tmph = thisbodyheight + body.getHeight()
							+ getTablefoot().getHeight();
				} else {
					tmph = thisbodyheight + body.getHeight();

				}
			} else {
				//ÿҳ�����β.
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
					// ���һ��ת��ҳ
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
		if (plantype.indexOf("���") >= 0) {
			alignTable();
		}
	}

	/**
	 * �зֵ�ҳ����Ϣ
	 * 
	 * @author Administrator
	 * 
	 */
	class Splitpageinfo {
		/**
		 * ���ݿ�ʼ��
		 */
		public int startrow;
		/**
		 * ���ݽ�����
		 */
		public int endrow;

		public int getRowcount() {
			return endrow - startrow + 1;
		}
	}

	/**
	 * ˮƽ��ҳ��Ϣ
	 * 
	 * @author Administrator
	 * 
	 */
	class Horizontalplitpageinfo {
		/**
		 * ���ݿ�ʼ��
		 */
		public int startcol;
		/**
		 * ���ݽ�����
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
	 * ֽ������
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
	 * ����
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
	 * ����printno����������������
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
		// ��鱾�������Ƿ��Ѿ���д���ˡ�
		for (int r = startrow; r <= endrow; r++) {
			String fillv = dbmodel.getItemValue(r, fillcolname);
			if (fillv == null || fillv.length() == 0) {
				alreadyfilled = false;
				break;
			}
		}

		if (alreadyfilled)
			return;

		ClientRequest req = new ClientRequest("npclient:��д��ӡ����");
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

		// ȡ����ҳ���е�id,��Ҫ�ظ�
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
		// ɾ�����һ��ð��
		if (sb.toString().endsWith(":"))
			sb.deleteCharAt(sb.length() - 1);
		pcmd.addParam("dbmodelvalues", sb.toString());

		// ��ӡ��־�ֶζ�Ӧ����
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
		// ɾ�����һ��ð��
		if (sb.toString().endsWith(":"))
			sb.deleteCharAt(sb.length() - 1);
		pcmd.addParam("dbmodelvalues1", sb.toString());

		// ���͵�������ִ��
		ServerResponse resp = SendHelper.sendRequest(req);
		String respcmd = resp.getCommand();
		if (!respcmd.startsWith("+OK")) {
			throw new Exception(respcmd);
		}
		ParamCommand resppcmd = (ParamCommand) resp.commandAt(1);
		String serialno = resppcmd.getValue("serialno");
		// ����serialno
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
