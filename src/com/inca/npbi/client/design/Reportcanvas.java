package com.inca.npbi.client.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;

import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import com.inca.np.gui.control.DBTableModel;
import com.inca.npbi.client.storer.BIReportStorage;

/**
 * 报表设计时的画布
 * 
 * @author user
 * 
 */
public class Reportcanvas extends JPanel implements ReportcalcerDatasourceIF,
		TabledefineChangedIF {
	Category logger = Category.getInstance(Reportcanvas.class);
	ReportcanvasFrame frame = null;
	int catchoffset = 3;
	Catchinfo curcatchinfo = null;
	int mousestartx, mousestarty;
	int pageno = 0;
	DBTableModel datadm = null;
	Dimension areasize = new Dimension(400, 400);
	boolean printing = false;

	double xyscale = 1.0;

	/**
	 * 水平页
	 */
	int hpagecount = 1;
	/**
	 * 可画的元件
	 */
	private Vector<ReportcanvasPlaceableIF> placeables = new Vector<ReportcanvasPlaceableIF>();
	/**
	 * 位置和大小
	 */
	Vector<Rectangle> positions = new Vector<Rectangle>();

	/**
	 * 当前激活的元件index.
	 */
	int activeindex = -1;
	private BICellCalcer calcer;

	/**
	 * 相关信息.
	 */
	Vector<Relateposinfo> relateposes = new Vector<Relateposinfo>();

	/**
	 * 分页信息
	 */
	Vector<Canvaspageinfo> canvaspageinfos = new Vector<Canvaspageinfo>();

	/**
	 * 是否在设计状态
	 */
	boolean deveoping = true;

	public Reportcanvas(ReportcanvasFrame frame) {
		this.frame = frame;
		calcer = new BICellCalcer(this);

		/**
		 * 捕获活动的元件
		 */
		addMouseListener(new Mousehandle());

		/**
		 * 哪些元件被激活,或拖动.
		 */
		addMouseMotionListener(new MousemotionHandler());
	}

	public void setDatadm(DBTableModel datadm) {
		this.datadm = datadm;
	}

	@Override
	public void paint(Graphics g) {
		frame.setWaitcursor();
		if (!printing) {
			super.paint(g);
		}
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform oldtran = g2.getTransform();
		if (!printing && xyscale != 1.0) {
			AffineTransform newtran = new AffineTransform(oldtran);
			newtran.scale(xyscale, xyscale);
			g2.setTransform(newtran);
		}

		try {
			g2.setColor(Color.WHITE);
			Dimension allsize = new Dimension(areasize.width * hpagecount,
					areasize.height);
			g2.fillRect(0, 0, allsize.width, allsize.height);

			// 画页的分隔
			if (deveoping) {
				// 开发状态画边
				Stroke oldstrock = g2.getStroke();
				float dash[] = { 3f, 3f, 3f, 3f };
				BasicStroke pagestroke = new BasicStroke(1,
						BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, dash,
						0f);
				g2.setStroke(pagestroke);
				g2.setColor(Color.LIGHT_GRAY);
				for (int i = 0; i < hpagecount; i++) {
					int x = areasize.width * (i + 1);
					g2.drawLine(x, 0, x, areasize.height);
				}
				g2.setStroke(oldstrock);
			}

			// 画页中的内容
			if (canvaspageinfos.size() == 0)
				return;
			Canvaspageinfo pageinfo = canvaspageinfos.elementAt(pageno);
			drawPagecontent(g2, pageinfo);

			if (activeindex >= 0 && isDeveoping()) {
				// 画边界
				Stroke oldstroke = g2.getStroke();
				g2.setColor(Color.blue);
				g2.setStroke(new BasicStroke(2));
				Rectangle rect = positions.elementAt(activeindex);
				g2.drawRect(rect.x, rect.y, rect.width, rect.height);
				g2.setStroke(oldstroke);
			}
		} finally {
			g2.setTransform(oldtran);
			frame.setDefaultcursor();
		}

	}

	protected void drawPagecontent(Graphics2D g2, Canvaspageinfo pageinfo) {
		Enumeration<ReportcanvasPlaceableIF> en = pageinfo.compinpagetable
				.elements();
		while (en.hasMoreElements()) {
			ReportcanvasPlaceableIF placeable = en.nextElement();
			int index = getPlaceableIndex(placeable);
			Rectangle pos = positions.elementAt(index);

			Graphics2D gtmp = (Graphics2D) g2.create(pos.x, pos.y, pos.width,
					pos.height);
			if (placeable instanceof BITableV_Render) {
				((BITableV_Render) placeable).setPrinting(isPrinting());
				placeable.draw(gtmp, pageinfo.pageno);
			} else {
				placeable.draw(gtmp, pageno);
			}
			gtmp.dispose();

		}
	}

	int getPlaceableIndex(ReportcanvasPlaceableIF p) {
		Enumeration<ReportcanvasPlaceableIF> en = placeables.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			if (en.nextElement() == p)
				return i;
		}
		return -1;
	}

	class Mousehandle implements MouseListener {

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {
			onMouseclick(e);
		}

		public void mouseReleased(MouseEvent e) {
			curcatchinfo = null;
			frame.onTabledefineChanged();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	class MousemotionHandler implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {
			onMouseDrag(e);
		}

		public void mouseMoved(MouseEvent e) {
			onMousemove(e);
		}

	}

	/**
	 * mouse滑过哪个元件?
	 * 
	 * @param mx
	 * @param my
	 * @return
	 */
	Catchinfo catchComponent(int mx, int my) {
		// 找出所有被选中的,再取面积小的
		Vector<Catchinfo> allcatched = new Vector<Catchinfo>();
		for (int i = 0; i < positions.size(); i++) {
			Rectangle pos = positions.elementAt(i);
			if (mx >= pos.x - catchoffset
					&& mx <= pos.x + pos.width + catchoffset
					&& my >= pos.y - catchoffset
					&& my < pos.y + pos.height + catchoffset) {
				// 当前的
				ReportcanvasPlaceableIF p = placeables.elementAt(i);
				if (!isIncurrentpage(p))
					continue;
				Catchinfo cinfo = new Catchinfo();
				cinfo.catchindex = i;
				allcatched.add(cinfo);
			}
		}
		if (allcatched.size() == 0) {
			return null;
		}
		Catchinfo cinfo = null;
		// System.out.println("allcatched size="+allcatched.size());
		cinfo = allcatched.elementAt(0);
		Rectangle minsize = positions.elementAt(cinfo.catchindex);
		// System.out.println("size0="+minsize.width * minsize.height);
		// 有没有面积更大的?
		for (int i = 1; i < allcatched.size(); i++) {
			Rectangle tmpsize = positions.elementAt(i);
			// System.out.println("size="+i+"="+tmpsize.width * tmpsize.height);
			if (tmpsize.width * tmpsize.height < minsize.width * minsize.height) {
				cinfo = allcatched.elementAt(i);
			}
		}

		// 是不是在边界?
		Rectangle activerect = positions.elementAt(cinfo.catchindex);
		// 左边?
		boolean leftflag = mx >= activerect.x - catchoffset
				&& mx <= activerect.x + catchoffset;
		int rightx = activerect.x + activerect.width;
		boolean rightflag = mx >= rightx - catchoffset
				&& mx <= rightx + catchoffset;
		boolean topflag = my >= activerect.y - catchoffset
				&& my <= activerect.y + catchoffset;
		int bottomy = activerect.y + activerect.height;
		boolean bottomflag = my >= bottomy - catchoffset
				&& my <= bottomy + catchoffset;

		if (leftflag || topflag) {
			cinfo.catchtype = Catchinfo.CATCH_MOVE;
		} else if (rightflag) {
			cinfo.catchtype = Catchinfo.CATCH_RIGHT;
		} else if (bottomflag) {
			cinfo.catchtype = Catchinfo.CATCH_BOTTOM;
		} else {
			cinfo.catchtype = Catchinfo.CATCH_MOVE;
		}
		return cinfo;
	}

	/**
	 * 鼠标点击,是哪个呢?
	 * 
	 * @param me
	 */
	void onMouseclick(MouseEvent me) {
		int mx = (int) ((double) me.getX() / xyscale + 0.5);
		int my = (int) ((double) me.getY() / xyscale + 0.5);
		Catchinfo cinfo = catchComponent(mx, my);
		if (cinfo == null) {
			activeindex = -1;
			if (isDeveoping()) {
				repaint();
			}
			return;
		}
		activeindex = cinfo.catchindex;
		curcatchinfo = cinfo;
		mousestartx = mx;
		mousestarty = my;

		if (me.getClickCount() <= 1) {
			// 编辑
			ReportcanvasPlaceableIF p = placeables.elementAt(activeindex);
			Rectangle pos = positions.elementAt(activeindex);
			if (p instanceof BICell) {
				if (deveoping)
					frame.editCell((BICell) p, pos);
			} else if (p instanceof BITableV_Render) {
				BITableV_Render tablevrender = (BITableV_Render) p;
				Rectangle posrect = positions.elementAt(activeindex);
				if (tablevrender
						.isMouseoverLink(mx - posrect.x, my - posrect.y)) {
					tablevrender.clickLink(mx - posrect.x, my - posrect.y,
							this, me.getX(), me.getY());
				} else {
					if (deveoping)
						frame.editTablev((BITableV_Render) p, pos);
				}
			}

			return;
		}
		if (!deveoping) {
			activeindex = -1;
			return;
		}

		ReportcanvasPlaceableIF placeable = placeables.elementAt(activeindex);
		if (placeable instanceof BICell) {
			BICell cell = (BICell) placeable;
			CellExprDlg dlg = new CellExprDlg(frame, frame.createColumntable(),
					cell.getExpr(), frame.dsdefine);
			dlg.pack();
			dlg.setVisible(true);
			if (!dlg.isOk())
				return;
			cell.setExpr(dlg.getExpr());
			repaint();

			/*
			 * }else if(placeable instanceof BITableV_Render){ BITableV_Render
			 * render=(BITableV_Render)placeable; frame.editTablev(render);
			 */
		} else if (placeable instanceof BiChartRender) {
			Rectangle pos = positions.elementAt(activeindex);
			frame.editChart((BiChartRender) placeable, pos);
		}
	}

	void onMouseDrag(MouseEvent me) {
		if (!deveoping)
			return;
		if (curcatchinfo == null) {
			/*
			 * Catchinfo cinfo = catchComponent(me.getX(), me.getY()); if (cinfo
			 * == null) { return; } curcatchinfo = cinfo;
			 * activeindex=cinfo.catchindex; mousestartx=me.getX();
			 * mousestarty=me.getY();
			 */
			return;
		}

		int offsetx = me.getX() - mousestartx;
		int offsety = me.getY() - mousestarty;
		Rectangle rect = positions.elementAt(activeindex);
		if (curcatchinfo.catchtype == Catchinfo.CATCH_MOVE) {
			// 移动
			rect.x += offsetx;
			rect.y += offsety;
		} else if (curcatchinfo.catchtype == Catchinfo.CATCH_RIGHT) {
			rect.width += offsetx;
			placeables.elementAt(activeindex).setSize(
					new Dimension(rect.width, rect.height));
		} else if (curcatchinfo.catchtype == Catchinfo.CATCH_BOTTOM) {
			rect.height += offsety;
			placeables.elementAt(activeindex).setSize(
					new Dimension(rect.width, rect.height));
		}
		mousestartx = me.getX();
		mousestarty = me.getY();

		ReportcanvasPlaceableIF p = placeables.elementAt(activeindex);
		Rectangle pos = positions.elementAt(activeindex);
		frame.recalcScrollpane(p);
		if (p instanceof BICell) {
			frame.editCell((BICell) p, pos);
		} else if (p instanceof BITableV_Render) {
			frame.editTablev((BITableV_Render) p, pos);
		} else if (p instanceof BiChartRender) {
			// 不能在这里调用
			// frame.editChart((BiChartRender) p, pos);
		}

		splitPage();
		calcHPagecount();
		repaint();

	}

	/**
	 * 
	 */
	void onMousemove(MouseEvent me) {
		int oldindex = activeindex;
		Catchinfo cinfo = catchComponent(me.getX(), me.getY());
		if (cinfo == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		} else {
			activeindex = cinfo.catchindex;
		}

		boolean islink = false;
		ReportcanvasPlaceableIF activecomp = placeables.elementAt(activeindex);
		if (activecomp instanceof BITableV_Render) {
			BITableV_Render tablevrender = (BITableV_Render) activecomp;
			Rectangle posrect = positions.elementAt(activeindex);

			int rmx = (int) ((double) me.getX() / xyscale + 0.5);
			int rmy = (int) ((double) me.getY() / xyscale + 0.5);

			if (tablevrender.isMouseoverLink(rmx - posrect.x, rmy - posrect.y)) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				islink = true;
				cinfo.catchtype = Catchinfo.CATCH_LINK;
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		if (!deveoping) {
			return;
		}

		if (oldindex != activeindex) {
			repaint();
		}

		if (cinfo == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

		if (!islink) {

			if (cinfo.catchtype == Catchinfo.CATCH_MOVE) {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			} else if (cinfo.catchtype == Catchinfo.CATCH_RIGHT) {
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (cinfo.catchtype == Catchinfo.CATCH_BOTTOM) {
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

	}

	class Catchinfo {
		static final int CATCH_MOVE = 0;
		static final int CATCH_RIGHT = 1;
		static final int CATCH_BOTTOM = 2;
		static final int CATCH_RIGHT_BOTTOM = 3;

		/**
		 * 击点链接.
		 */
		static final int CATCH_LINK = 4;

		int catchindex = 0;
		int catchtype = 0;
	}

	public int getCurrow() {
		return 0;
	}

	public DBTableModel getDbmodel() {
		// 如果有tablevrender,取tablevrender的groupdbmodel
		Enumeration<ReportcanvasPlaceableIF> en = placeables.elements();
		while (en.hasMoreElements()) {
			ReportcanvasPlaceableIF p = en.nextElement();
			if (p instanceof BITableV_Render) {
				BITableV_Render render = (BITableV_Render) p;
				return render.getGroupdatadm();
			}
		}
		return datadm;
	}

	public int getPagecount() {
		return canvaspageinfos.size();
	}

	public Splitpageinfo getPageinfo(int pageno) {
		return null;
	}

	public int getPrintingpageno() {
		return pageno;
	}

	public void addCell(BICell cell) {
		cell.setCalcer(calcer);
		addPlaceable(cell);

		Rectangle pos = new Rectangle();
		int x = 1, y = 1;
		pos.x = x;
		pos.y = y;
		pos.width = cell.getSize().width;
		pos.height = cell.getSize().height;
		positions.add(pos);
		splitPage();
	}

	public void addTablev(BITableV_Render tablevrender) {
		addPlaceable(tablevrender);

		Rectangle pos = new Rectangle();
		int x = 1, y = 50;
		pos.x = x;
		pos.y = y;
		pos.width = tablevrender.getSize().width;
		pos.height = tablevrender.getSize().height;
		positions.add(pos);

	}

	public void positionChanged(Rectangle rect) {
		for (int i = 0; i < positions.size(); i++) {
			if (positions.get(i) == rect) {
				placeables.elementAt(i).setSize(
						new Dimension(rect.width, rect.height));
				break;
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		if (!printing && xyscale != 1.0) {
			double neww = (double) areasize.width * (double) hpagecount
					* xyscale + .5;
			double newh = (double) areasize.height * xyscale + .5;
			return new Dimension((int) neww, (int) newh);
		}
		return new Dimension(areasize.width * hpagecount, areasize.height);
	}

	public void deleCell(BICell editingcell) {
		for (int i = 0; i < placeables.size(); i++) {
			if (placeables.elementAt(i) == editingcell) {
				placeables.remove(i);
				positions.remove(i);
				if (i == activeindex) {
					activeindex = -1;
				}
				break;
			}
		}
	}

	public void setPageno(int pageno) {
		this.pageno = pageno;
		this.activeindex = -1;
		curcatchinfo = null;
	}

	/*
	 * public void onTabledefineChanged(BITableV_Render render) { for (int i =
	 * 0; i < placeables.size(); i++) { if (placeables.elementAt(i) == render) {
	 * Rectangle pos = positions.elementAt(i); pos.width =
	 * render.getSize().width; pos.height = render.getSize().height; break; } }
	 * splitPage(); frame.onTabledefineChanged(); }
	 */
	public Dimension getPapersize() {
		return areasize;
	}

	/**
	 * 设置可画区域
	 * 
	 * @param areasize
	 */
	public void setAreasize(Dimension areasize) {
		this.areasize = areasize;
	}

	Dimension calcMaxsize() {
		int maxw = 0;
		int maxh = 0;

		Enumeration<Rectangle> en = positions.elements();
		while (en.hasMoreElements()) {
			Rectangle r = en.nextElement();
			int w = r.x + r.width;
			int h = r.y + r.height;
			if (w > maxw)
				maxw = w;
			if (h > maxh)
				maxh = h;
		}
		return new Dimension(maxw, maxh);
	}

	/**
	 * 算水平页数
	 */
	void calcHPagecount() {
		Dimension maxsize = calcMaxsize();
		hpagecount = maxsize.width / areasize.width;
		if (maxsize.width % areasize.width != 0) {
			hpagecount++;
		}
	}

	public Vector<ReportcanvasPlaceableIF> getPlaceables() {
		return placeables;
	}

	public void setActiveindex(int activeindex) {
		this.activeindex = activeindex;
	}

	public void deleTablev(BITableV_Render editingtablev) {
		for (int i = 0; i < placeables.size(); i++) {
			if (placeables.elementAt(i) == editingtablev) {
				placeables.remove(i);
				positions.remove(i);
				if (i == activeindex) {
					activeindex = -1;
				}
				break;
			}
		}
	}

	public int getActiveindex() {
		return activeindex;
	}

	/**
	 * 因为这些元件有相互关系,所以要重新设定页.
	 */
	public void splitPage() {
		logger.debug("reportcanvas, splitPage()");

		int drawed[] = new int[placeables.size()];
		for (int i = 0; i < drawed.length; i++) {
			drawed[i] = 0;
		}

		canvaspageinfos.clear();
		boolean drawcount = false;
		boolean needcreatenewpage = true;
		int tablevpageno = 0;
		do {
			// 开始页
			Canvaspageinfo canvaspage = null;
			if (needcreatenewpage) {
				canvaspage = new Canvaspageinfo();
				canvaspage.pageno = tablevpageno;
				canvaspageinfos.add(canvaspage);
				needcreatenewpage = false;
			}

			drawcount = false;
			int layoutstarty = 0;
			for (int i = 0; i < placeables.size(); i++, drawcount = false) {
				// 画完了
				if (drawed[i] == 1)
					continue;
				drawcount = true;

				ReportcanvasPlaceableIF p = placeables.elementAt(i);
				boolean canstartdraw = false;
				int priorcompindex = getPriorcompIndex(p);
				if (priorcompindex < 0) {
					// 没有前面的元件.画
					canstartdraw = true;
				} else {
					// 如果有前面的元件,并且画过了,也画.
					if (drawed[priorcompindex] == 1) {
						canstartdraw = true;

						if (tablevpageno == 0 && p instanceof BITableV_Render) {
							try {
								p.setLayoutstarty(layoutstarty
										- positions.elementAt(i).y + 5);
								((BITableV_Render) p).prepareData();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (!canstartdraw)
					continue;
				// 类型
				if (p instanceof BICell) {
					// 自由单元
					// 有次序吗?
					canvaspage.compinpagetable.add(p);
					if (hasBehind(p)) {
						// 有元件以它为相对位置,只画一次.
						Rectangle pos = positions.elementAt(i);
						layoutstarty = pos.y + pos.height;
						drawed[i] = 1;
						i = -1;
						continue;
					}

					if (((BICell) p).getRepeat()
							.equals(BICell.REPEAT_FIRSTPAGE)) {
						drawed[i] = 1;
						continue;
					}

				} else if (p instanceof BITableV_Render) {
					// 垂直表. 垂直表是多页的.

					BITableV_Render render = (BITableV_Render) p;
					canvaspage.compinpagetable.add(p);

					canvaspage.tablevpageno = tablevpageno;
					if (tablevpageno >= render.getPagecount() - 1) {
						// 垂直表结束了.
						drawed[i] = 1;
						// 垂直表结束后,不产生新页,而是将i置为0,重新扫一遍元件,看哪个需要画.
						// 如果没有,循环就退出了.
						i = -1;
						needcreatenewpage = false;
						continue;
					} else {
						// i = -1;
						needcreatenewpage = true;
						tablevpageno++;
					}
				} else if (p instanceof BiChartRender) {
					// 图表
					canvaspage.compinpagetable.add(p);
					// 图表是一次性的.画完了就结束了.
					drawed[i] = 1;
					if (hasBehind(p)) {
						Rectangle pos = positions.elementAt(i);
						layoutstarty = pos.y + pos.height;
						i = -1;
						continue;
					}
				}

			}
		} while (drawcount || needcreatenewpage);
	}

	boolean hasBehind(ReportcanvasPlaceableIF p) {
		Enumeration<Relateposinfo> en = relateposes.elements();
		while (en.hasMoreElements()) {
			Relateposinfo rpos = en.nextElement();
			if (rpos.priorcomp == p)
				return true;
		}
		return false;
	}

	/**
	 * 这个元件之前有元件吗?
	 * 
	 * @param p
	 * @return
	 */
	int getPriorcompIndex(ReportcanvasPlaceableIF p) {
		ReportcanvasPlaceableIF priorcomp = null;
		Enumeration<Relateposinfo> en = relateposes.elements();
		while (en.hasMoreElements()) {
			Relateposinfo rpos = en.nextElement();
			if (rpos.comp == p) {
				priorcomp = rpos.priorcomp;
				break;
			}
		}
		if (priorcomp == null)
			return -1;
		for (int i = 0; i < placeables.size(); i++) {
			if (placeables.get(i) == priorcomp)
				return i;
		}
		return -1;
	}

	/**
	 * 页信息
	 * 
	 * @author user
	 * 
	 */
	class Canvaspageinfo {
		int pageno;
		/**
		 * 本页的元件
		 */
		Vector<ReportcanvasPlaceableIF> compinpagetable = new Vector<ReportcanvasPlaceableIF>();

		/**
		 * 相对垂直表的页码
		 */
		int tablevpageno = 0;
	}

	class Relateposinfo {
		/**
		 * 前一个元件
		 */
		ReportcanvasPlaceableIF priorcomp;

		/**
		 * 后一个元件
		 */
		ReportcanvasPlaceableIF comp;

		/**
		 * 垂直距离
		 */
		int offset = 20;
	}

	boolean isIncurrentpage(ReportcanvasPlaceableIF p) {
		if (canvaspageinfos.size() == 0)
			return false;
		Canvaspageinfo pageinfo = canvaspageinfos.elementAt(pageno);
		Enumeration<ReportcanvasPlaceableIF> en = pageinfo.compinpagetable
				.elements();
		while (en.hasMoreElements()) {
			if (p == en.nextElement()) {
				return true;
			}
		}
		return false;
	}

	public void addChart(BiChartRender chart) {
		chart.setCalcer(calcer);
		addPlaceable(chart);

		Rectangle pos = new Rectangle();
		int x = 1, y = 30;
		pos.x = x;
		pos.y = y;
		pos.width = chart.getSize().width;
		pos.height = chart.getSize().height;
		positions.add(pos);

		// 为了调试,弄出一个相对位置
		// Relateposinfo relatepos = new Relateposinfo();
		// relatepos.priorcomp = chart;
		// relatepos.comp = placeables.elementAt(0);
		// relateposes.add(relatepos);
		// splitPage();
	}

	/**
	 * 存为配置文件
	 * 
	 * @param out
	 */
	public void write(PrintWriter out) {
		// 输出类型
		out.println("<placeable>");
		Enumeration<ReportcanvasPlaceableIF> en = placeables.elements();
		while (en.hasMoreElements()) {
			ReportcanvasPlaceableIF p = en.nextElement();
			if (p instanceof BICell) {
				BIReportStorage.writeFreeCell(out, (BICell) p);
			} else if (p instanceof BITableV_Render) {
				BITableV_Render render = (BITableV_Render) p;
				BIReportStorage.writeTablevdefine(out, render.getTablevdef());
			} else if (p instanceof BiChartRender) {
				BiChartRender chartrender = (BiChartRender) p;
				Chartdefine chartdefine = chartrender.getChartdefine();

				// 计算数据源是第几个数据源
				for (int i = 0; i < frame.dstable.size(); i++) {
					if (frame.dstable.elementAt(i) == chartdefine.getDsdefine()) {
						chartdefine.dsdefineindex = i;
						break;
					}
				}

				BIReportStorage.writeChart(out, chartrender.getChartdefine());
			}
		}
		out.println("</placeable>");

		out.println("<positions>");
		Enumeration<Rectangle> en1 = positions.elements();
		while (en1.hasMoreElements()) {
			Rectangle rect = en1.nextElement();
			out.print(rect.x + ":");
			out.print(rect.y + ":");
			out.print(rect.width + ":");
			out.print(rect.height);
			out.println();
		}
		out.println("</positions>");

		out.println("<relatepostions>");
		Enumeration<Relateposinfo> ren = relateposes.elements();
		while (ren.hasMoreElements()) {
			Relateposinfo rpos = ren.nextElement();
			out.print(getPlaceindex(rpos.priorcomp));
			out.print(":");
			out.print(getPlaceindex(rpos.comp));
			out.println();
		}
		out.println("</relatepostions>");

	}

	int getPlaceindex(ReportcanvasPlaceableIF p) {
		for (int i = 0; i < placeables.size(); i++) {
			if (p == placeables.elementAt(i)) {
				return i;
			}
		}
		return -1;
	}

	public void read(BufferedReader rd) throws Exception {
		placeables.clear();
		positions.clear();
		// 先读入placeable
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<placeable>")) {
				readPlaceable(rd);
			} else if (line.startsWith("<positions>")) {
				readPositions(rd);
			} else if (line.startsWith("<relatepostions>")) {
				try {
					readRelatepostions(rd);
				} catch (Exception e) {
				}
			}
		}
	}

	private void readRelatepostions(BufferedReader rd) throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</relatepostions>")) {
				break;
			}
			String ss[] = line.split(":");
			int priorindex = Integer.parseInt(ss[0]);
			int index = Integer.parseInt(ss[1]);
			addRelatepos(placeables.elementAt(priorindex), placeables
					.elementAt(index));
		}
	}

	private void readPositions(BufferedReader rd) throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</positions>")) {
				break;
			} else {
				String ss[] = line.split(":");
				Rectangle rect = new Rectangle();
				rect.x = Integer.parseInt(ss[0]);
				rect.y = Integer.parseInt(ss[1]);
				rect.width = Integer.parseInt(ss[2]);
				rect.height = Integer.parseInt(ss[3]);
				positions.add(rect);
			}
		}
		for (int i = 0; i < positions.size(); i++) {
			Rectangle r = positions.elementAt(i);
			placeables.elementAt(i).setSize(new Dimension(r.width, r.height));
		}
	}

	private void readPlaceable(BufferedReader rd) throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<freebicell>")) {
				BICell cell = BIReportStorage.readFreecell(rd);
				cell.setCalcer(calcer);
				addPlaceable(cell);
			} else if (line.startsWith("<tablev>")) {
				frame.tablevdef.reset();
				frame.tablevrender.reset();
				BIReportStorage.readTablevdefine(rd, frame.tablevdef);
				addPlaceable(frame.tablevrender);
			} else if (line.startsWith("<chart>")) {
				Chartdefine chartdefine = new Chartdefine();
				BIReportStorage.readChart(rd, chartdefine);
				chartdefine.setDsdefine(frame.dstable
						.elementAt(chartdefine.dsdefineindex));
				chartdefine.setCalcer(calcer);
				BiChartRender chartrender = new BiChartRender(chartdefine);
				chartrender.setCalcer(calcer);
				addPlaceable(chartrender);
			} else if (line.startsWith("</placeable>")) {
				break;
			}
		}
	}

	/**
	 * 清除前后次序
	 */
	public void clearRelatepos() {
		relateposes.clear();
	}

	public void addRelateposinfo(Relateposinfo posinfo) {
		relateposes.add(posinfo);
	}

	public void addRelatepos(ReportcanvasPlaceableIF prior,
			ReportcanvasPlaceableIF p) {
		Relateposinfo rpos = new Relateposinfo();
		rpos.priorcomp = prior;
		rpos.comp = p;
		relateposes.add(rpos);
	}

	public ReportcanvasPlaceableIF getPriorcomp(ReportcanvasPlaceableIF p) {
		int i = getPriorcompIndex(p);
		if (i < 0)
			return null;
		return placeables.elementAt(i);
	}

	public void addPlaceable(ReportcanvasPlaceableIF p) {
		p.setID(placeables.size());
		placeables.add(p);
	}

	public String getParameter(String pname) {
		return frame.getParameter(pname);
	}

	public void reset() {
		curcatchinfo = null;
		mousestartx = mousestarty = 0;
		pageno = 0;
		hpagecount = 1;
		placeables.clear();
		positions.clear();
		activeindex = -1;
		relateposes.clear();

	}

	public void onTabledefineChanged() {
		prepareData();
		splitPage();
		frame.onTabledefineChanged();
	}

	public boolean isDeveoping() {
		return deveoping;
	}

	public void setDeveoping(boolean deveoping) {
		this.deveoping = deveoping;
	}

	public void prepareData() {
		Enumeration<ReportcanvasPlaceableIF> en = placeables.elements();
		while (en.hasMoreElements()) {
			ReportcanvasPlaceableIF p = en.nextElement();
			if (p instanceof BiChartRender) {
				BiChartRender chartrender = (BiChartRender) p;
				int dsindex = chartrender.chartdefine.dsdefineindex;
				chartrender.setDbtablemode(frame.getDstable()
						.elementAt(dsindex).datadm);
			} else if (p instanceof BITableV_Render) {
				p.setDbtablemode(datadm);
			} else {
				p.setDbtablemode(datadm);
			}
			p.prepareData();
		}
		calcHPagecount();
	}

	public int getPageno() {
		return pageno;
	}

	public int getHpagecount() {
		return hpagecount;
	}

	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}

	public double getXyscale() {
		return xyscale;
	}

	public void setXyscale(double xyscale) {
		this.xyscale = xyscale;
	}

	public void deletePlaceableAt(int row) {
		placeables.remove(row);
		positions.remove(row);
		activeindex = -1;
		curcatchinfo = null;
	}

	public void exportExcel(File outf, String opname) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(opname);
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(outf);
		} catch (FileNotFoundException e1) {
			return;
		}
		int cellcount = 0;
		Enumeration<ReportcanvasPlaceableIF> en = placeables.elements();
		int rowcount = 0;
		List<ExcelLine> list = new ArrayList<ExcelLine>();
		while (en.hasMoreElements()) {
			ReportcanvasPlaceableIF placeable = en.nextElement();
			int index = getPlaceableIndex(placeable);
			Rectangle pos = positions.elementAt(index);
			ExcelLine l = new ExcelLine();
			l.index = index;
			l.pos = pos;
			l.placeable = placeable;
			list.add(l);

			if (placeable instanceof BITableV_Render) {
				cellcount = ((BITableV_Render) placeable).getTablevdef().colcount;
			}
		}

		ExcelLine[] lines = list.toArray(new ExcelLine[0]);
		// 从上到下，从左到右，元件排序。
		Arrays.sort(lines);

		//根据pox.y分组。划分出在一行的控件
		Map<Integer, List<ExcelLine>> map = new HashMap<Integer, List<ExcelLine>>();
		int count = 0;
		for (int i = 0; i < lines.length; i++) {
			if (i > 0) {
				if (lines[i].pos.y == lines[i - 1].pos.y) {
					ArrayList<ExcelLine> tmp = (ArrayList<ExcelLine>) map.get(count - 1);
					tmp.add(lines[i]);
				} else {
					List<ExcelLine> newarrayList = new ArrayList<ExcelLine>();
					newarrayList.add(lines[i]);
					map.put(count, newarrayList);
					count++;
				}
			} else {
				List<ExcelLine> newarrayList = new ArrayList<ExcelLine>();
				newarrayList.add(lines[i]);
				map.put(count, newarrayList);
				count++;
			}
		}
		
		//导出每一行控件
		for (int i = 0; i < count; i++) {
			ArrayList<ExcelLine>  a= (ArrayList<ExcelLine>) map.get(i);

			rowcount = exportToExcel(workbook, sheet, a, rowcount, fout,
					cellcount);
		}
		
		try {
			workbook.write(fout);
		} catch (IOException e) {
			logger.error("error",e);
		} finally {
		}

		if (fout != null) {
			try {
				fout.close();
				fout = null;
			} catch (IOException e) {
			} 
		}
	}

	private int exportToExcel(HSSFWorkbook workbook, HSSFSheet sheet,
			ArrayList<ExcelLine> excellines, int rowcount, FileOutputStream fout, int cellcount) {

		if (excellines.size() == 1) {
			ExcelLine line = (ExcelLine) excellines.get(0);
			ReportcanvasPlaceableIF placeable = line.placeable;
			if (placeable instanceof BITableV_Render) {
				try {
					//数据区导出的具体行数再赋值回来
					rowcount = ((BITableV_Render) placeable).exportExcel(
							rowcount, workbook, sheet, fout);
					
					return rowcount;
				} catch (Exception e) {
					logger.error("errr",e);
				}
			} else if (placeable instanceof BICell) {
				BICell cell = (BICell) placeable;
				HSSFRow excelrow = sheet.createRow(rowcount);
				for (int i = 0; i < cellcount; i++) {
					HSSFCell hssfcell = excelrow.createCell((short) i);
					hssfcell.setCellValue("");
				}
				HSSFCell hssfcell = excelrow.getCell((short)0);
				try {
					setCellvalue(cell.calcValue(0), workbook, hssfcell, cell
							.getAlign(), cell.getFontname(), cell
							.getFontsize(), cell.isBold(), cell
							.isItalic());
				} catch (Exception e) {
					logger.error("error", e);
				}
				//合并单元格
				sheet.addMergedRegion(new Region(rowcount, (short)0, rowcount, (short)(cellcount-1)));
			}
		} else {
			HSSFRow excelrow = sheet.createRow(rowcount);
			String value = "";
			for (int i = 0; i < cellcount; i++) {
				HSSFCell hssfcell = excelrow.createCell((short) i);
				hssfcell.setCellValue(value);
			}

			int spacecount = cellcount / excellines.size();
			for (int j = 0; j < excellines.size(); j++) {

				int index = j * spacecount ;
				int toindex = index+spacecount;
				if(j==(excellines.size()-1))
				{
					toindex = cellcount;
				}
				ExcelLine line = (ExcelLine) excellines.get(j);
				ReportcanvasPlaceableIF placeable = line.placeable;

				if (placeable instanceof BICell) {
					BICell cell = (BICell) placeable;

					HSSFCell hssfcell = excelrow.getCell((short) index);
					value = cell.calcValue(0);

					try {
						setCellvalue(value, workbook, hssfcell,
								cell.getAlign(), cell.getFontname(), cell
										.getFontsize(), cell.isBold(), cell
										.isItalic());
					} catch (Exception e) {
						logger.error("error", e);
					}
				}
				//合并单元格
				sheet.addMergedRegion(new Region(rowcount, (short)index, rowcount, (short)(toindex-1)));
			}
		}
		rowcount++;
		return rowcount;

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
}

class ExcelLine implements Comparable<ExcelLine> {
	public int index;
	public Rectangle pos;
	public ReportcanvasPlaceableIF placeable;

	// 从上到下，从左到右排序
	public int compareTo(ExcelLine o) {
		if (pos.y == o.pos.y)
			return pos.x - o.pos.x;
		return pos.y - o.pos.y;
	}
}
