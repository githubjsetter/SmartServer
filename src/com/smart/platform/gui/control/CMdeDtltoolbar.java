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
 * ϸ��������
 */
public class CMdeDtltoolbar extends CStetoolbar {

	public CMdeDtltoolbar(ActionListener l) {
		actionlistener = l;
		JButton btn = null;
		btn = new JButton("����", IconFactory.icnew);
		btn.setToolTipText("����һ��ϸ�������������ܵ����ȼ�Ctrl+I");
		btn.setActionCommand(CMdeModel.ACTION_NEWDTL);
		btn.addActionListener(l);
		add(btn);

		btn = new JButton("�༭", IconFactory.icedit);
		btn.setToolTipText("�༭һ��ϸ�����ȼ�F3");
		btn.setActionCommand(CMdeModel.ACTION_MODIFYDTL);
		btn.addActionListener(l);
		add(btn);

		btn = new JButton("����", IconFactory.icundo);
		btn.setToolTipText("����״̬�ļ�¼ɾ�����޸�״̬�ļ�¼�ָ����ݿ�ֵ��ɾ��״̬�ļ�¼��ɾ�����ȼ�Ctrl+Shift+Z");
		btn.setActionCommand(CMdeModel.ACTION_UNDODTL);
		btn.addActionListener(l);
		add(btn);

		btn = new JButton("ɾ��", IconFactory.icdelete);
		btn.setToolTipText("��ϸ����¼��Ϊɾ��״̬������������ݿ���ɾ�����ȼ�Ctrl+Del");
		btn.setActionCommand(CMdeModel.ACTION_DELDTL);
		btn.addActionListener(l);
		add(btn);

		addSeparator();

		/*
		 * 20070802 ȥ����¼���� btn=new JButton("��һ",IconFactory.icfirst);
		 * btn.setToolTipText("��λ����һ��ϸ����¼");
		 * btn.setActionCommand(CMdeModel.ACTION_FIRSTDTL);
		 * btn.addActionListener(l); add(btn);
		 * 
		 * btn=new JButton("��һ",IconFactory.icprior);
		 * btn.setToolTipText("��λ����һ��ϸ����¼���ȼ����ϼ�ͷ");
		 * btn.setActionCommand(CMdeModel.ACTION_PRIORDTL);
		 * btn.addActionListener(l); add(btn);
		 * 
		 * btn=new JButton("��һ",IconFactory.icnext);
		 * btn.setToolTipText("��λ����һ��ϸ����¼���ȼ����¼�ͷ");
		 * btn.setActionCommand(CMdeModel.ACTION_NEXTDTL);
		 * btn.addActionListener(l); add(btn);
		 * 
		 * btn=new JButton("���",IconFactory.iclast);
		 * btn.setToolTipText("��λ�����һ��ϸ����¼");
		 * btn.setActionCommand(CMdeModel.ACTION_LASTDTL);
		 * btn.addActionListener(l); add(btn);
		 */

		this.addSeparator(new Dimension(40, 0));

		if (DefaultNPParam.develop == 1) {
			btn = new JButton("���");
			btn.setToolTipText("�������");
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
		JPopupMenu popmenu = new JPopupMenu("��Ʋ˵�");
		JMenuItem item;
		item = new JMenuItem("ϸ����������");
		item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("����ϸ������");
		item.setActionCommand(CMdeModel.ACTION_SAVEUIDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);
		return popmenu;
	}

}
