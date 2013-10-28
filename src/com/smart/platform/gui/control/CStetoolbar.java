package com.smart.platform.gui.control;

import com.smart.platform.auth.RunopManager;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.tbar.TBar;
import com.smart.platform.gui.tbar.TButton;
import com.smart.platform.image.CIcon;
import com.smart.platform.image.IconFactory;
import com.smart.platform.util.DefaultNPParam;

import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-29 Time: 11:26:06
 * To change this template use File | Settings | File Templates.
 */
public class CStetoolbar extends TBar {
	protected TButton querybtn;
	protected String[] runopnames = null;

	public CStetoolbar() {
	}

	protected ActionListener actionlistener = null;

	public void setRunopnames(String[] runopnames) {
		this.runopnames = runopnames;
	}

	protected TButton addButton(String title, String tips, String action) {
		TButton btn = new TButton(title);
		btn.setToolTipText(tips);
		btn.setActionCommand(action);
		btn.addActionListener(actionlistener);
		btn.setFocusable(false);
		btn.setPreferredSize(new Dimension((int) btn.getPreferredSize()
				.getWidth(), 27));
		add(btn);
		return btn;
	}

	protected TButton addButton(String title, CIcon icon, String tips,
			String action) {
		TButton btn = new TButton(title, icon);
		btn.setToolTipText(tips);
		btn.setActionCommand(action);
		btn.addActionListener(actionlistener);
		btn.setFocusable(false);
		add(btn);
		btn.setPreferredSize(new Dimension((int) btn.getPreferredSize()
				.getWidth(), 27));
		return btn;
	}

	protected TButton addButton(CIcon icon, String tips, String action) {
		TButton btn = new TButton(icon);
		btn.setToolTipText(tips);
		btn.setActionCommand(action);
		btn.addActionListener(actionlistener);
		btn.setFocusable(false);
		btn.setPreferredSize(new Dimension((int) btn.getPreferredSize()
				.getWidth(), 27));
		add(btn);
		return btn;
	}

	public CStetoolbar(ActionListener l) {
		super();
		actionlistener = l;
		if (isUsebutton(CSteModel.ACTION_NEW)) {
			addButton("新增", IconFactory.icnew, "新增一条记录．热键Ctrl+N Insert",
					CSteModel.ACTION_NEW);
		}

		if (isUsebutton(CSteModel.ACTION_MODIFY)) {
			addButton("编辑", IconFactory.icedit, "打开编辑窗口编辑记录．热键F2",
					CSteModel.ACTION_MODIFY);
		}

		if (isUsebutton(CSteModel.ACTION_UNDO)) {
			addButton("撤消", IconFactory.icundo,
					"新增状态的记录删除．修改状态的记录恢复数据库值．删除状态的记录不删除．热键Ctrl+Z",
					CSteModel.ACTION_UNDO);
		}

		if (isUsebutton(CSteModel.ACTION_DEL)) {
			addButton("删除", IconFactory.icdelete,
					"将记录置为删除状态，保存后在数据库中删除．热键Ctrl+D", CSteModel.ACTION_DEL);
		}

		if (isUsebutton(CSteModel.ACTION_QUERY)) {
			querybtn = addButton("查询", IconFactory.icquery,
					"输入条件，从数据中查询记录．热键F8.刷新热键F5", CSteModel.ACTION_QUERY);
		}

		createOtherButton(l);
		addSeparator();

		/*
		 * 20070802 去掉上一条下一条 if (isUsebutton(CSteModel.ACTION_FIRST)) {
		 * addButton(IconFactory.icfirst, "定位到第一条记录", CSteModel.ACTION_FIRST); }
		 * 
		 * if (isUsebutton(CSteModel.ACTION_PRIOR)) {
		 * addButton(IconFactory.icprior, "定位到上一条记录．热键向上键头",
		 * CSteModel.ACTION_PRIOR); }
		 * 
		 * if (isUsebutton(CSteModel.ACTION_NEXT)) {
		 * addButton(IconFactory.icnext, "定位到下一条记录．热键向下键头",
		 * CSteModel.ACTION_NEXT); }
		 * 
		 * if (isUsebutton(CSteModel.ACTION_LAST)) {
		 * addButton(IconFactory.iclast, "定位到最后一条记录", CSteModel.ACTION_LAST); }
		 * 
		 * 
		 * addSeparator();
		 */

		if (isUsebutton(CSteModel.ACTION_SAVE)) {
			addButton("保存", IconFactory.icsave, "将变化的数据保存到服务器 F9",
					CSteModel.ACTION_SAVE);
		}

		TButton btn = null;
		if (isUsebutton("打印")) {
			btn = addButton("打印", IconFactory.icprint, "将数据发送到发打印机打印", "print");
			btn.addMouseListener(new PrintMouseEventHandle());
		}

		this.addSeparator(new Dimension(40, 0));

		if (isUsebutton(CSteModel.ACTION_SELECTOP)) {
			//btn = addButton("功能", "选择其它功能", CSteModel.ACTION_SELECTOP);
		}

		btn = addButton("界面", IconFactory.icprint, "保存和使用界面设置", "print");
		btn.addMouseListener(new FaceEventHandle());

		if (isUsebutton(CSteModel.ACTION_EXIT)) {
			addButton("关闭", "关闭功能 Alt+X", CSteModel.ACTION_EXIT);
		}

		if (DefaultNPParam.develop == 1 && isUsebutton("设计")) {
			btn = addButton("设计", "界面设计", "");
			btn.addMouseListener(new MouseEventHandle());
		}

		Dimension size = this.getPreferredSize();
		// size.setSize(800,size.getHeight());
		//size.setSize(800, 30);
		//this.setPreferredSize(size);

	}

	protected void createOtherButton(ActionListener listener) {

	}

	public void setQuerybuttonText(String s) {
		if (querybtn != null)
			querybtn.setText(s);
	}

	protected class MouseEventHandle implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mousePressed(MouseEvent e) {
			JPopupMenu menu = createDesignMenu(actionlistener);
			menu.show((JComponent) e.getSource(), e.getX(), e.getY());
		}

		public void mouseReleased(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseEntered(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseExited(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
	}

	protected class PrintMouseEventHandle implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mousePressed(MouseEvent e) {
			JPopupMenu menu = createPrintMenu(actionlistener);
			Enumeration<Menuiteminfo> en=docprintmenus.elements();
			while(en.hasMoreElements()){
				Menuiteminfo menuitem=en.nextElement();
				JMenuItem item;
				item = new JMenuItem(menuitem.title);
				item.setActionCommand(menuitem.command);
				item.addActionListener(actionlistener);
				menu.add(item);
	
			}
			
			menu.show((JComponent) e.getSource(), e.getX(), e.getY());
		}

		public void mouseReleased(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseEntered(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseExited(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
	}

	/**
	 * 弹出界面菜单
	 * @author user
	 *
	 */
	protected class FaceEventHandle implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mousePressed(MouseEvent e) {
			JPopupMenu menu = createFaceMenu(actionlistener);
			menu.show((JComponent) e.getSource(), e.getX(), e.getY());
		}

		public void mouseReleased(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseEntered(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseExited(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
	}

	protected class SelectopMouseListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
			RunopManager.mousex = e.getX();
			RunopManager.mousey = e.getY();
		}
	}

	public JPopupMenu createDesignMenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("设计菜单");
		JMenuItem item;
		item = new JMenuItem("自　　检");
		item.setActionCommand(CSteModel.ACTION_SELFCHECK);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("界面设置");
		item.setActionCommand(CSteModel.ACTION_SETUPUI);
		item.addActionListener(actionListener);
		popmenu.add(item);
		/*
		 * item = new JMenuItem("保存界面");
		 * item.setActionCommand(CSteModel.ACTION_SAVEUI);
		 * item.addActionListener(actionListener); popmenu.add(item);
		 */
		item = new JMenuItem("规则设置");
		item.setActionCommand(CSteModel.ACTION_SETUPRULE);
		item.addActionListener(actionListener);
		popmenu.add(item);

		/*
		 * item = new JMenuItem("保存列序(专项)");
		 * item.setActionCommand(CSteModel.ACTION_SAVETABLECOLUMN_ZX);
		 * item.addActionListener(actionListener); popmenu.add(item);
		 */
		return popmenu;
	}

	protected boolean isUsebutton(String actionname) {
		return true;
	}

	public static JPopupMenu createPrintMenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("打印");
		JMenuItem item;
		item = new JMenuItem("打印设置");
		item.setActionCommand(CSteModel.ACTION_PRINTSETUP);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("打印 CTRL+P");
		item.setActionCommand(CSteModel.ACTION_PRINT);
		item.addActionListener(actionListener);
		popmenu.add(item);
		return popmenu;
	}

	public static JPopupMenu createFaceMenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("界面");
		JMenuItem item;
		item = new JMenuItem("排序");
		item.setActionCommand(CSteModel.ACTION_FACESORT);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("保存界面");
		item.setActionCommand(CSteModel.ACTION_SAVEFACE);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("另存界面");
		item.setActionCommand(CSteModel.ACTION_SAVEASFACE);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("使用界面");
		item.setActionCommand(CSteModel.ACTION_USEFACE);
		item.addActionListener(actionListener);
		popmenu.add(item);
		return popmenu;
	}

	public TButton placeButton(String title, String tips, String action) {
		TButton btn = new TButton(title);
		btn.setToolTipText(tips);
		btn.setActionCommand(action);
		btn.addActionListener(actionlistener);
		btn.setFocusable(false);
		add(btn);
		btn.setPreferredSize(new Dimension((int) btn.getPreferredSize()
				.getWidth(), 27));
		return btn;
	}
	
	Vector<Menuiteminfo> docprintmenus=new Vector<Menuiteminfo>();
	
	/**
	 * 扩展单据打印
	 * @param title
	 * @param command
	 */
	public void addPrintmenu(String title,String command){
		docprintmenus.add(new Menuiteminfo(title,command));
	}


	class Menuiteminfo{
		String title;
		String command;
		public Menuiteminfo(String title, String command) {
			super();
			this.title = title;
			this.command = command;
		}
	}

}
