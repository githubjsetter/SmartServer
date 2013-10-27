package com.inca.np.gui.panedesign;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 用于进行设计的panel
 * 
 * @author user
 * 
 */
public class DPanedesignPane extends JPanel {
	DPanel dpane = null;
	Vector<AdjsizeComp> adjcomps = new Vector<AdjsizeComp>();
	/**
	 * 选中的元件
	 */
	Vector<AdjsizeComp> selectedcomp = new Vector<AdjsizeComp>();

	/**
	 * 调整个dpane大小的控件
	 */
	AdjsizeComp dpaneresizecomp = null;

	Point lastclickp = null;
	int lastselectflag = -1;
	/**
	 * 调整panel
	 */
	boolean dpaneselected = false;

	/**
	 * 以n为点阵.
	 */
	int gridoffset = 5;

	boolean batchselect = false;
	Rectangle batchselectrect = null;

	public DPanedesignPane(DPanel dpane) {
		super();
		this.dpane = dpane;
		Dimension d1 = dpane.getPreferredSize();
		// System.out.println("!!! d1 " + d1.width);
		Rectangle d2 = dpane.getBounds();
		// System.out.println("!!! d2 " + d2.width);
		Compinfo resizecomp = new Compinfo("_dpanel", dpane, new Rectangle(0,
				0, dpane.getPreferredSize().width,
				dpane.getPreferredSize().height));
		dpaneresizecomp = new AdjsizeComp(resizecomp);
		dpaneresizecomp.setSelected(true);

		// 根据dpane中的控件情况,画出不同的comp调整框
		Enumeration<Compinfo> en = dpane.getCompinfos();
		while (en.hasMoreElements()) {
			Compinfo compinfo = en.nextElement();
			AdjsizeComp adjcomp = new AdjsizeComp(compinfo);
			adjcomps.add(adjcomp);
		}

		MouseHandler mousehandle = new MouseHandler();
		addMouseListener(mousehandle);
		addMouseMotionListener(mousehandle);
	}

	@Override
	public void paint(Graphics g) {
		// 先画白布,再画出pane的大小位置
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.white);
		g2.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);

		// 画要设计的dpanel的位置
		g2.setColor(dpane.getBackground());
		/*
		 * System.out.println("dp size ="+dpane.getPreferredSize().width+","+dpane
		 * .getPreferredSize().height);
		 */
		g2.fillRect(0, 0, dpaneresizecomp.rect.width,
				dpaneresizecomp.rect.height);

		// 画点阵
		int x = 0;
		int y = 0;

		g2.setColor(Color.LIGHT_GRAY);
		for (x = 0; x < getPreferredSize().width; x += gridoffset) {
			for (y = 0; y < getPreferredSize().height; y += gridoffset) {
				g2.fillRect(x, y, 1, 1);
			}
		}


		Enumeration<AdjsizeComp> en = adjcomps.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp adjcomp = en.nextElement();
			adjcomp.paint(g2);
		}
		
		dpaneresizecomp.paint(g2);


		if (batchselect) {
			g2.setColor(Color.black);
			g2.drawRect(batchselectrect.x, batchselectrect.y,
					batchselectrect.width, batchselectrect.height);

		}
}

	/**
	 * 画布的大小
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1680, 2048);
	}

	class MouseHandler implements MouseListener, MouseMotionListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				doubleClicked(e.getPoint());
			} else {
				//onClick(e.getPoint(), e.isControlDown());
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			onPressed(e.getPoint(),e.isControlDown());
		}

		public void mouseReleased(MouseEvent e) {
			onReleased(e.getPoint());
		}

		public void mouseDragged(MouseEvent e) {
			onDrag(e.getPoint());
		}

		public void mouseMoved(MouseEvent e) {
			onMousemove(e.getPoint());
		}
	}

	void onReleased(Point p) {
		// 是否是批量选择?
		if (batchselect) {
			clearSelect();

			Enumeration<AdjsizeComp> en = adjcomps.elements();
			while (en.hasMoreElements()) {
				AdjsizeComp adjcomp = en.nextElement();
				if(adjcomp.compinfo.realcomp instanceof Titleborderpane){
					if(!batchselectrect.contains(adjcomp.compinfo.rect)){
						continue;
					}
					
				}
				if (adjcomp.isIntersects(batchselectrect)) {
					addSelected(adjcomp, true);
				}
			}
		}
		batchselect = false;

		// 规整
		Enumeration<AdjsizeComp> en = adjcomps.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp adjcomp = en.nextElement();
			if (adjcomp.isSelected()) {
				adjcomp.rect.x = (int) ((double) adjcomp.rect.x
						/ (double) gridoffset + 0.5)
						* gridoffset;
				adjcomp.rect.y = (int) ((double) adjcomp.rect.y
						/ (double) gridoffset + 0.5)
						* gridoffset;
				adjcomp.rect.width = (int) ((double) adjcomp.rect.width
						/ (double) gridoffset + 0.5)
						* gridoffset;
				adjcomp.rect.height = (int) ((double) adjcomp.rect.height
						/ (double) gridoffset + 0.5)
						* gridoffset;
			}
		}
		invalidate();
		repaint();

	}

	void onPressed(Point p,boolean toggle) {
		int flag = dpaneresizecomp.onMousemove(p);
		if (flag == AdjsizeComp.MOUSE_RIGHT) {
			lastselectflag = flag;
			lastclickp = p;
			dpaneselected = true;
			return;
		} else if (flag == AdjsizeComp.MOUSE_BOTTOM) {
			lastselectflag = flag;
			lastclickp = p;
			dpaneselected = true;
			return;
		} else if (flag == AdjsizeComp.MOUSE_RIGHTBOTTOM) {
			lastselectflag = flag;
			lastclickp = p;
			dpaneselected = true;
			return;
		}
		dpaneselected = false;

		lastclickp = p;
		Pointselectinfo psinfo = doSelect(p);
		lastselectflag = psinfo.flag;
		if (psinfo.flag < 0) {
			clearSelect();
			return;
		}

		if(psinfo.curadjsizecomp.isSelected()){
		}else{
			if (psinfo.flag == AdjsizeComp.MOUSE_ENTER) {
				addSelected(psinfo.curadjsizecomp, toggle);
				return;
			}			
		}
		

		return;

	}

	void onDrag(Point p) {
		// 如果没有被选中的,说明是画框批量选
		// System.out.println("lastselectflag="+lastselectflag);
		// System.out.println("lastclickp="+lastclickp.x+","+lastclickp.y);
		if (lastselectflag == -1) {
			batchselect = true;
			batchselectrect = new Rectangle();
			if (lastclickp.x < p.x) {
				batchselectrect.x = lastclickp.x;
				batchselectrect.width = p.x - lastclickp.x + 1;
			} else {
				batchselectrect.x = p.x;
				batchselectrect.width = lastclickp.x - p.x + 1;
			}

			if (lastclickp.y < p.y) {
				batchselectrect.y = lastclickp.y;
				batchselectrect.height = p.y - lastclickp.y + 1;
			} else {
				batchselectrect.y = p.y;
				batchselectrect.height = lastclickp.y - p.y + 1;
			}
			invalidate();
			repaint();
			return;
		}

		batchselect = false;
		batchselectrect = null;

		if (dpaneselected) {
			// 调面板大小
			int offsetx = p.x - lastclickp.x;
			int offsety = p.y - lastclickp.y;

			if (lastselectflag == AdjsizeComp.MOUSE_RIGHT) {
				dpaneresizecomp.rect.width += offsetx;
			} else if (lastselectflag == AdjsizeComp.MOUSE_BOTTOM) {
				dpaneresizecomp.rect.height += offsety;
			} else if (lastselectflag == AdjsizeComp.MOUSE_RIGHTBOTTOM) {
				dpaneresizecomp.rect.width += offsetx;
				dpaneresizecomp.rect.height += offsety;
			}
			dpane.setPreferredSize(new Dimension(dpaneresizecomp.rect.width,
					dpaneresizecomp.rect.height));
			lastclickp = p;
			invalidate();
			repaint();
			return;
		}

		AdjsizeComp adjcomp = null;
		Enumeration<AdjsizeComp> en = adjcomps.elements();
		while (en.hasMoreElements()) {
			adjcomp = en.nextElement();
			if (adjcomp.isSelected()) {
				if (lastselectflag < 0) {
					return;
				}
				int offsetx = p.x - lastclickp.x;
				int offsety = p.y - lastclickp.y;

				// System.out.println("!!lastselectflag="+lastselectflag);
				if (lastselectflag == AdjsizeComp.MOUSE_ENTER) {
					adjcomp.rect.x += offsetx;
					adjcomp.rect.y += offsety;
					// break;
				} else if (lastselectflag == AdjsizeComp.MOUSE_RIGHT) {
					adjcomp.rect.width += offsetx;
					// break;
				} else if (lastselectflag == AdjsizeComp.MOUSE_BOTTOM) {
					adjcomp.rect.height += offsety;
					// break;
				} else if (lastselectflag == AdjsizeComp.MOUSE_RIGHTBOTTOM) {
					adjcomp.rect.width += offsetx;
					adjcomp.rect.height += offsety;
					// break;
				}
			}
		}

		// 规整
		lastclickp = p;
		invalidate();
		repaint();

	}


	void doubleClicked(Point p) {
		Pointselectinfo psinfo = doSelect(p);
		if (psinfo.flag < 0) {
			return;
		}
		if (psinfo.curadjsizecomp.compinfo.realcomp instanceof Titleborderpane) {
			Titleborderpane tbpane = (Titleborderpane) psinfo.curadjsizecomp.compinfo.realcomp;
			String title = tbpane.getTitle();
			String s = JOptionPane.showInputDialog((Frame) null, "请输入分组标题",
					title);
			if (s != null) {
				tbpane.setTitle(s);
				tbpane.invalidate();
				tbpane.repaint();
			}
		}

	}

	/**
	 * 增加选中
	 * 
	 * @param adjcomp
	 */
	private void addSelected(AdjsizeComp adjcomp, boolean toggle) {
		if (!toggle) {
			// 先清掉现在的
			clearSelect();
		}
		selectedcomp.add(adjcomp);
		adjcomp.setSelected(true);
		invalidate();
		repaint();
	}

	private void clearSelect() {
		// 先清掉现在的
		while (!selectedcomp.isEmpty()) {
			selectedcomp.remove(0).setSelected(false);
		}
		invalidate();
		repaint();
	}

	void onMousemove(Point p) {
		//System.out.println("mouse move ");
		// 检查是否有选中的?
		int flag = dpaneresizecomp.onMousemove(p);
		//System.out.println("dpaneresizecomp flag=" + flag);
		if (flag == AdjsizeComp.MOUSE_RIGHT) {
			setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			return;
		} else if (flag == AdjsizeComp.MOUSE_BOTTOM) {
			setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			return;
		} else if (flag == AdjsizeComp.MOUSE_RIGHTBOTTOM) {
			setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			return;
		}


		Pointselectinfo psinfo = doSelect(p);
		if (psinfo.flag < 0) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}

		if(psinfo.curadjsizecomp.isSelected()){
			if (psinfo.flag == AdjsizeComp.MOUSE_ENTER) {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				return;
			} else if (psinfo.flag == AdjsizeComp.MOUSE_RIGHT) {
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				return;
			} else if (psinfo.flag == AdjsizeComp.MOUSE_BOTTOM) {
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				return;
			} else if (psinfo.flag == AdjsizeComp.MOUSE_RIGHTBOTTOM) {
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				return;
			}
		}else{
			if (psinfo.flag == AdjsizeComp.MOUSE_ENTER) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				return;
			}			
		}
		
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * 水平上对齐. 对最左边的元件为准
	 */
	public void alignHorizontaltop() {
		if (selectedcomp.size() == 0)
			return;

		// 找最左边的
		AdjsizeComp leftcomp = selectedcomp.elementAt(0);
		for (int i = 1; i < selectedcomp.size(); i++) {
			if (selectedcomp.elementAt(i).rect.x < leftcomp.rect.x) {
				leftcomp = selectedcomp.elementAt(i);
			}
		}

		for (int i = 0; i < selectedcomp.size(); i++) {
			selectedcomp.elementAt(i).rect.y = leftcomp.rect.y;
		}
		invalidate();
		repaint();
	}

	/**
	 * 水平下对齐
	 */
	public void alignHorizontalbottom() {
		if (selectedcomp.size() == 0)
			return;

		// 找最左边的
		AdjsizeComp leftcomp = getLefttop();

		int targety = leftcomp.rect.y + leftcomp.rect.height;
		for (int i = 0; i < selectedcomp.size(); i++) {
			selectedcomp.elementAt(i).rect.y = targety
					- selectedcomp.elementAt(i).rect.height;
		}
		invalidate();
		repaint();
	}

	/**
	 * 垂直左对齐
	 */
	public void alignVerticalleft() {
		if (selectedcomp.size() == 0)
			return;
		// 找最上边的
		AdjsizeComp topcomp = getLefttop();

		for (int i = 0; i < selectedcomp.size(); i++) {
			selectedcomp.elementAt(i).rect.x = topcomp.rect.x;
			// selectedcomp.elementAt(i).rect.height=leftcomp.rect.height;
		}
		invalidate();
		repaint();
	}

	/**
	 * 垂直右对齐
	 */
	public void alignVerticalright() {
		if (selectedcomp.size() == 0)
			return;
		// 找最上边的
		AdjsizeComp topcomp = getLefttop();
		int startx = topcomp.rect.x + topcomp.rect.width;

		for (int i = 0; i < selectedcomp.size(); i++) {
			selectedcomp.elementAt(i).rect.x = startx
					- selectedcomp.elementAt(i).rect.width;
			// selectedcomp.elementAt(i).rect.height=leftcomp.rect.height;
		}
		invalidate();
		repaint();
	}

	/**
	 * 设同高
	 */
	public void sameHeight() {
		if (selectedcomp.size() == 0)
			return;
		AdjsizeComp topcomp = getLefttop();

		for (int i = 0; i < selectedcomp.size(); i++) {
			selectedcomp.elementAt(i).rect.height = topcomp.rect.height;
		}
		invalidate();
		repaint();
	}

	/**
	 * 设同宽
	 */
	public void sameWidth() {
		if (selectedcomp.size() == 0)
			return;
		// 找最上边的
		AdjsizeComp topcomp = getLefttop();

		for (int i = 0; i < selectedcomp.size(); i++) {
			selectedcomp.elementAt(i).rect.width = topcomp.rect.width;
		}
		invalidate();
		repaint();
	}

	/**
	 * 取左上角的
	 * 
	 * @return
	 */
	AdjsizeComp getLefttop() {
		AdjsizeComp topcomp = selectedcomp.elementAt(0);
		for (int i = 1; i < selectedcomp.size(); i++) {
			if (selectedcomp.elementAt(i).rect.y < topcomp.rect.y) {
				topcomp = selectedcomp.elementAt(i);
			} else if (selectedcomp.elementAt(i).rect.y == topcomp.rect.y) {
				if (selectedcomp.elementAt(i).rect.x < topcomp.rect.x) {
					topcomp = selectedcomp.elementAt(i);
				}
			}
		}
		return topcomp;

	}

	public void selectAll() {
		clearSelect();

		for (int i = 0; i < adjcomps.size(); i++) {
			AdjsizeComp adjcomp = adjcomps.elementAt(i);
			selectedcomp.add(adjcomp);
			adjcomp.setSelected(true);
		}
		invalidate();
		repaint();

	}

	public void batchMoveleft() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.x -= gridoffset;
		}
		invalidate();
		repaint();
	}

	public void batchMoveright() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.x += gridoffset;
		}
		invalidate();
		repaint();
	}

	public void batchMoveup() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.y -= gridoffset;
		}
		invalidate();
		repaint();
	}

	public void batchMovedown() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.y += gridoffset;
		}
		invalidate();
		repaint();
	}

	public void batchMoveleft1() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.width -= gridoffset;
		}
		invalidate();
		repaint();
	}

	public void batchMoveright1() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.width += gridoffset;
		}
		invalidate();
		repaint();
	}

	public void batchMoveup1() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.height -= gridoffset;
		}
		invalidate();
		repaint();
	}

	public void batchMovedown1() {
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			c.rect.height += gridoffset;
		}
		invalidate();
		repaint();
	}

	public void addTitleborder(Titleborderpane titleborderpane) {

		int ct = 0;
		Enumeration<AdjsizeComp> en = adjcomps.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp c = en.nextElement();
			if (c.compinfo.realcomp instanceof Titleborderpane) {
				ct++;
			}
		}
		ct++;
		String newname = "titleborder" + ct;
		titleborderpane.setName(newname);
		titleborderpane.setBounds(0, 0,
				titleborderpane.getPreferredSize().width, titleborderpane
						.getPreferredSize().height);
		dpane.add(titleborderpane);

		Compinfo cpinfo = dpane.getCompinfo(newname);
		AdjsizeComp adjsizecomp = new AdjsizeComp(cpinfo);
		adjcomps.add(adjsizecomp);
		addSelected(adjsizecomp, false);

	}

	class Pointselectinfo {
		AdjsizeComp curadjsizecomp;
		int flag = -1;
	}

	/**
	 * 由当前位置判断选中了哪个,如果有多个,选面积最小的一个
	 * 
	 * @param point
	 * @return
	 */
	private Pointselectinfo doSelect(Point p) {

		Vector<AdjsizeComp> allselected = new Vector<AdjsizeComp>();
		//先看选中的
		Enumeration<AdjsizeComp> en = selectedcomp.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp adjcomp = en.nextElement();
			int flag = adjcomp.onMousemove(p);
			if (flag >= 0) {
				allselected.add(adjcomp);
			}
		}
		if(allselected.size()>0){
			return createPsinfo(allselected,p);
		}
		
		
		en = adjcomps.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp adjcomp = en.nextElement();
			int flag = adjcomp.onMousemove(p);
			if (flag >= 0) {
				allselected.add(adjcomp);
			}
		}

		return createPsinfo(allselected,p);
	}
	
	Pointselectinfo createPsinfo(Vector<AdjsizeComp> allselected,Point p){
		// 找面积最小
		AdjsizeComp minadjcomp = null;

		Enumeration<AdjsizeComp> en = allselected.elements();
		while (en.hasMoreElements()) {
			AdjsizeComp adjcomp = en.nextElement();
			if (minadjcomp == null) {
				minadjcomp = adjcomp;
			} else {
				int a1 = minadjcomp.rect.width * minadjcomp.rect.height;
				int a2 = adjcomp.rect.width * adjcomp.rect.height;
				if (a2 < a1) {
					minadjcomp = adjcomp;
				}
			}
		}

		// 反回最小的
		Pointselectinfo psinfo = new Pointselectinfo();
		if (minadjcomp != null) {
			psinfo.curadjsizecomp = minadjcomp;
			psinfo.flag = minadjcomp.onMousemove(p);
		}
		return psinfo;
	}

}
