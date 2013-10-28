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
			addButton("����", IconFactory.icnew, "����һ����¼���ȼ�Ctrl+N Insert",
					CSteModel.ACTION_NEW);
		}

		if (isUsebutton(CSteModel.ACTION_MODIFY)) {
			addButton("�༭", IconFactory.icedit, "�򿪱༭���ڱ༭��¼���ȼ�F2",
					CSteModel.ACTION_MODIFY);
		}

		if (isUsebutton(CSteModel.ACTION_UNDO)) {
			addButton("����", IconFactory.icundo,
					"����״̬�ļ�¼ɾ�����޸�״̬�ļ�¼�ָ����ݿ�ֵ��ɾ��״̬�ļ�¼��ɾ�����ȼ�Ctrl+Z",
					CSteModel.ACTION_UNDO);
		}

		if (isUsebutton(CSteModel.ACTION_DEL)) {
			addButton("ɾ��", IconFactory.icdelete,
					"����¼��Ϊɾ��״̬������������ݿ���ɾ�����ȼ�Ctrl+D", CSteModel.ACTION_DEL);
		}

		if (isUsebutton(CSteModel.ACTION_QUERY)) {
			querybtn = addButton("��ѯ", IconFactory.icquery,
					"�����������������в�ѯ��¼���ȼ�F8.ˢ���ȼ�F5", CSteModel.ACTION_QUERY);
		}

		createOtherButton(l);
		addSeparator();

		/*
		 * 20070802 ȥ����һ����һ�� if (isUsebutton(CSteModel.ACTION_FIRST)) {
		 * addButton(IconFactory.icfirst, "��λ����һ����¼", CSteModel.ACTION_FIRST); }
		 * 
		 * if (isUsebutton(CSteModel.ACTION_PRIOR)) {
		 * addButton(IconFactory.icprior, "��λ����һ����¼���ȼ����ϼ�ͷ",
		 * CSteModel.ACTION_PRIOR); }
		 * 
		 * if (isUsebutton(CSteModel.ACTION_NEXT)) {
		 * addButton(IconFactory.icnext, "��λ����һ����¼���ȼ����¼�ͷ",
		 * CSteModel.ACTION_NEXT); }
		 * 
		 * if (isUsebutton(CSteModel.ACTION_LAST)) {
		 * addButton(IconFactory.iclast, "��λ�����һ����¼", CSteModel.ACTION_LAST); }
		 * 
		 * 
		 * addSeparator();
		 */

		if (isUsebutton(CSteModel.ACTION_SAVE)) {
			addButton("����", IconFactory.icsave, "���仯�����ݱ��浽������ F9",
					CSteModel.ACTION_SAVE);
		}

		TButton btn = null;
		if (isUsebutton("��ӡ")) {
			btn = addButton("��ӡ", IconFactory.icprint, "�����ݷ��͵�����ӡ����ӡ", "print");
			btn.addMouseListener(new PrintMouseEventHandle());
		}

		this.addSeparator(new Dimension(40, 0));

		if (isUsebutton(CSteModel.ACTION_SELECTOP)) {
			//btn = addButton("����", "ѡ����������", CSteModel.ACTION_SELECTOP);
		}

		btn = addButton("����", IconFactory.icprint, "�����ʹ�ý�������", "print");
		btn.addMouseListener(new FaceEventHandle());

		if (isUsebutton(CSteModel.ACTION_EXIT)) {
			addButton("�ر�", "�رչ��� Alt+X", CSteModel.ACTION_EXIT);
		}

		if (DefaultNPParam.develop == 1 && isUsebutton("���")) {
			btn = addButton("���", "�������", "");
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
	 * ��������˵�
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
		JPopupMenu popmenu = new JPopupMenu("��Ʋ˵�");
		JMenuItem item;
		item = new JMenuItem("�ԡ�����");
		item.setActionCommand(CSteModel.ACTION_SELFCHECK);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("��������");
		item.setActionCommand(CSteModel.ACTION_SETUPUI);
		item.addActionListener(actionListener);
		popmenu.add(item);
		/*
		 * item = new JMenuItem("�������");
		 * item.setActionCommand(CSteModel.ACTION_SAVEUI);
		 * item.addActionListener(actionListener); popmenu.add(item);
		 */
		item = new JMenuItem("��������");
		item.setActionCommand(CSteModel.ACTION_SETUPRULE);
		item.addActionListener(actionListener);
		popmenu.add(item);

		/*
		 * item = new JMenuItem("��������(ר��)");
		 * item.setActionCommand(CSteModel.ACTION_SAVETABLECOLUMN_ZX);
		 * item.addActionListener(actionListener); popmenu.add(item);
		 */
		return popmenu;
	}

	protected boolean isUsebutton(String actionname) {
		return true;
	}

	public static JPopupMenu createPrintMenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("��ӡ");
		JMenuItem item;
		item = new JMenuItem("��ӡ����");
		item.setActionCommand(CSteModel.ACTION_PRINTSETUP);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("��ӡ CTRL+P");
		item.setActionCommand(CSteModel.ACTION_PRINT);
		item.addActionListener(actionListener);
		popmenu.add(item);
		return popmenu;
	}

	public static JPopupMenu createFaceMenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("����");
		JMenuItem item;
		item = new JMenuItem("����");
		item.setActionCommand(CSteModel.ACTION_FACESORT);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("�������");
		item.setActionCommand(CSteModel.ACTION_SAVEFACE);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("������");
		item.setActionCommand(CSteModel.ACTION_SAVEASFACE);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("ʹ�ý���");
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
	 * ��չ���ݴ�ӡ
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
