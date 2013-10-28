package com.smart.platform.gui.control;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.image.IconFactory;
import com.smart.platform.util.DefaultNPParam;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-17 Time: 15:06:47
 * 细单工具条
 */
public class CMdeDtltoolbar extends CStetoolbar {

	public CMdeDtltoolbar(ActionListener l) {
		actionlistener = l;
		JButton btn = null;
		btn = new JButton("新增", IconFactory.icnew);
		btn.setToolTipText("新增一条细单．必须已有总单．热键Ctrl+I");
		btn.setActionCommand(CMdeModel.ACTION_NEWDTL);
		btn.addActionListener(l);
		add(btn);

		btn = new JButton("编辑", IconFactory.icedit);
		btn.setToolTipText("编辑一条细单．热键F3");
		btn.setActionCommand(CMdeModel.ACTION_MODIFYDTL);
		btn.addActionListener(l);
		add(btn);

		btn = new JButton("撤消", IconFactory.icundo);
		btn.setToolTipText("新增状态的记录删除．修改状态的记录恢复数据库值．删除状态的记录不删除．热键Ctrl+Shift+Z");
		btn.setActionCommand(CMdeModel.ACTION_UNDODTL);
		btn.addActionListener(l);
		add(btn);

		btn = new JButton("删除", IconFactory.icdelete);
		btn.setToolTipText("将细单记录置为删除状态，保存后在数据库中删除．热键Ctrl+Del");
		btn.setActionCommand(CMdeModel.ACTION_DELDTL);
		btn.addActionListener(l);
		add(btn);

		addSeparator();

		/*
		 * 20070802 去掉记录滚动 btn=new JButton("第一",IconFactory.icfirst);
		 * btn.setToolTipText("定位到第一条细单记录");
		 * btn.setActionCommand(CMdeModel.ACTION_FIRSTDTL);
		 * btn.addActionListener(l); add(btn);
		 * 
		 * btn=new JButton("上一",IconFactory.icprior);
		 * btn.setToolTipText("定位到上一条细单记录．热键向上键头");
		 * btn.setActionCommand(CMdeModel.ACTION_PRIORDTL);
		 * btn.addActionListener(l); add(btn);
		 * 
		 * btn=new JButton("下一",IconFactory.icnext);
		 * btn.setToolTipText("定位到下一条细单记录．热键向下键头");
		 * btn.setActionCommand(CMdeModel.ACTION_NEXTDTL);
		 * btn.addActionListener(l); add(btn);
		 * 
		 * btn=new JButton("最后",IconFactory.iclast);
		 * btn.setToolTipText("定位到最后一条细单记录");
		 * btn.setActionCommand(CMdeModel.ACTION_LASTDTL);
		 * btn.addActionListener(l); add(btn);
		 */

		this.addSeparator(new Dimension(40, 0));

		if (DefaultNPParam.develop == 1) {
			btn = new JButton("设计");
			btn.setToolTipText("界面设计");
			btn.addMouseListener(new MouseEventHandle());
			btn.addActionListener(l);
			add(btn);
		}

		Dimension size = this.getPreferredSize();
		// size.setSize(800,size.getHeight());
		size.setSize(800, 30);
		this.setPreferredSize(size);

	}

	public void setQuerybuttonText(String s) {
		if (querybtn != null) {
			querybtn.setText(s);
		}
	}

	private class MouseEventHandle implements MouseListener {

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

	public JPopupMenu createDesignMenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("设计菜单");
		JMenuItem item;
		item = new JMenuItem("细单界面设置");
		item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("保存细单界面");
		item.setActionCommand(CMdeModel.ACTION_SAVEUIDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);
		return popmenu;
	}

}
