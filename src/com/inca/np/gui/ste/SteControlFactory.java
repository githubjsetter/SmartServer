package com.inca.np.gui.ste;

import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.util.DefaultNPParam;

import javax.swing.*;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-28 Time: 13:53:31
 * ����༭�˵�
 */
public class SteControlFactory {
	/**
	 * ����ȱʡ��STE�˵�
	 * 
	 * @return
	 */
	/*
	 * public static JMenuBar createJMenu(ActionListener actionListener) {
	 * JMenuBar menubar=new JMenuBar(); JMenu menu=null;
	 * 
	 * ///////////////////////////////////////���ݲ˵�/////////////////////////////////////
	 * menu=new JMenu("D����"); menu.setMnemonic(KeyEvent.VK_D);
	 * 
	 * JMenuItem menuitem=new JMenuItem("���� Ctrl+N");
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setActionCommand(CSteModel.ACTION_NEW); menu.add(menuitem);
	 * 
	 * menuitem=new JMenuItem("�����޸� Ctrl+Z");
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setActionCommand(CSteModel.ACTION_UNDO); menu.add(menuitem);
	 * 
	 * menuitem=new JMenuItem("ɾ�� Ctrl+D");
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setActionCommand(CSteModel.ACTION_DEL); menu.add(menuitem);
	 * 
	 * menuitem=new JMenuItem("��ѯ F8");
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setActionCommand(CSteModel.ACTION_QUERY); menu.add(menuitem);
	 * 
	 * 
	 * menu.addSeparator();
	 * 
	 * menuitem=new JMenuItem("���� F9"); menuitem.setMnemonic(KeyEvent.VK_S);
	 * menuitem.setActionCommand(CSteModel.ACTION_SAVE); menuitem.setName("����");
	 * menuitem.addActionListener(actionListener); menu.add(menuitem);
	 * 
	 * 
	 * menubar.add(menu);
	 * 
	 * ///////////////////////////////////////�鿴�˵�/////////////////////////////////////
	 * menu=new JMenu("V�鿴"); menu.setMnemonic(KeyEvent.VK_V);
	 * menubar.add(menu);
	 * 
	 * menuitem=new JMenuItem("F��һ����¼"); menuitem.setMnemonic(KeyEvent.VK_F);
	 * menuitem.setActionCommand(CSteModel.ACTION_FIRST);
	 * menuitem.addActionListener(actionListener); menu.add(menuitem);
	 * menuitem=new JMenuItem("P��һ����¼"); menuitem.setMnemonic(KeyEvent.VK_P);
	 * menuitem.setActionCommand(CSteModel.ACTION_PRIOR);
	 * menuitem.addActionListener(actionListener); menu.add(menuitem);
	 * menuitem=new JMenuItem("N��һ����¼");
	 * menuitem.setActionCommand(CSteModel.ACTION_NEXT);
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setMnemonic(KeyEvent.VK_N); menu.add(menuitem); menuitem=new
	 * JMenuItem("L�������¼"); menuitem.setActionCommand(CSteModel.ACTION_LAST);
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setMnemonic(KeyEvent.VK_L); menu.add(menuitem);
	 * 
	 * 
	 * 
	 * menu=new JMenu("H����"); menu.setMnemonic(KeyEvent.VK_H);
	 * menubar.add(menu);
	 * 
	 * menuitem=new JMenuItem("H����"); menuitem.setName("����");
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setMnemonic(KeyEvent.VK_H); menu.add(menuitem);
	 * 
	 * menu.addSeparator();
	 * 
	 * menuitem=new JMenuItem("V�汾"); menuitem.setName("�汾");
	 * menuitem.addActionListener(actionListener);
	 * menuitem.setMnemonic(KeyEvent.VK_V); menu.add(menuitem);
	 * 
	 * return menubar; }
	 * 
	 */
	public static CStetoolbar createStetoolbar(ActionListener l) {
		CStetoolbar toolbar = new CStetoolbar(l);
		return toolbar;
	}

	/**
	 * @deprecated
	 * @param actionListener
	 * @return
	 */
	public static JPopupMenu createPopupmenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("�˵�");
		JMenuItem item;
/*		
		item = new JMenuItem("���� Ctrl+N");
		item.setActionCommand(CSteModel.ACTION_NEW);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("�޸� F2");
		item.setActionCommand(CSteModel.ACTION_MODIFY);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("�����޸� Ctrl+Z");
		item.setActionCommand(CSteModel.ACTION_MODIFY);
		item.addActionListener(actionListener);
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("ɾ�� Ctrl+D");
		item.setActionCommand(CSteModel.ACTION_DEL);
		item.addActionListener(actionListener);
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("��ѯ F8");
		item.setActionCommand(CSteModel.ACTION_QUERY);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
*/
		//ȡ���ٵ���.
		
		
		
		item = new JMenuItem("��һ��");
		item.setActionCommand(CSteModel.ACTION_FIRST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ǰһ��");
		item.setActionCommand(CSteModel.ACTION_PRIOR);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("��һ��");
		item.setActionCommand(CSteModel.ACTION_NEXT);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ĩһ��");
		item.setActionCommand(CSteModel.ACTION_LAST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		popmenu.addSeparator();
/*
		item = new JMenuItem("����");
		item.setActionCommand(CSteModel.ACTION_SAVE);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
*/
		item = new JMenuItem("����");
		item.setActionCommand(CSteModel.ACTION_EXPORT);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("����Ϊ");
		item.setActionCommand(CSteModel.ACTION_EXPORTAS);
		item.addActionListener(actionListener);
		popmenu.add(item);
		popmenu.addSeparator();

		if (DefaultNPParam.develop == 1) {
			item = new JMenuItem("�������");
			item.setActionCommand(CSteModel.ACTION_SETUPUI);
			item.addActionListener(actionListener);
			popmenu.add(item);
		}

		return popmenu;
	}

	public static JPopupMenu createQueryPopupmenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("�˵�");
		JMenuItem item;

		item = new JMenuItem("��ѯ F8");
		item.setActionCommand(CSteModel.ACTION_QUERY);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		popmenu.addSeparator();

		item = new JMenuItem("��һ��");
		item.setActionCommand(CSteModel.ACTION_FIRST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ǰһ��");
		item.setActionCommand(CSteModel.ACTION_PRIOR);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("��һ��");
		item.setActionCommand(CSteModel.ACTION_NEXT);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ĩһ��");
		item.setActionCommand(CSteModel.ACTION_LAST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("����");
		item.setActionCommand(CSteModel.ACTION_EXPORT);
		item.addActionListener(actionListener);
		popmenu.add(item);
		item = new JMenuItem("����Ϊ");
		item.setActionCommand(CSteModel.ACTION_EXPORTAS);
		item.addActionListener(actionListener);
		popmenu.add(item);

		popmenu.addSeparator();

		if (DefaultNPParam.develop == 1) {
			item = new JMenuItem("�������");
			item.setActionCommand(CSteModel.ACTION_SETUPUI);
			item.addActionListener(actionListener);
			popmenu.add(item);
		}

		return popmenu;
	}

	public static void setHotkey(JComponent compcp, ActionListener listener) {
		//Ctrl+W �ؿ�Ƭ
		KeyStroke vkctrlw = KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				vkctrlw, CSteModel.ACTION_HIDEFORM);
		compcp.getActionMap().put(CSteModel.ACTION_HIDEFORM,
				new SteActionListener(CSteModel.ACTION_HIDEFORM,listener));

		
		// F8 ��ѯ
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf8,
				CSteModel.ACTION_QUERY);
		compcp.getActionMap().put(CSteModel.ACTION_QUERY,
				new SteActionListener(CSteModel.ACTION_QUERY, listener));
		// F5 ˢ��
		KeyStroke vkf5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf5,
				CSteModel.ACTION_REFRESH);
		compcp.getActionMap().put(CSteModel.ACTION_REFRESH,
				new SteActionListener(CSteModel.ACTION_REFRESH, listener));

		// Ctrl+N ����
		KeyStroke vkctrln = KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrln,
				CSteModel.ACTION_NEW);
		compcp.getActionMap().put(CSteModel.ACTION_NEW,
				new SteActionListener(CSteModel.ACTION_NEW, listener));
		
		KeyStroke vkins = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,
				0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkins,
				CSteModel.ACTION_NEW);
		compcp.getActionMap().put(CSteModel.ACTION_NEW,
				new SteActionListener(CSteModel.ACTION_NEW, listener));


		// Ctrl+Z UNDO
		KeyStroke vkctrlz = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlz,
				CSteModel.ACTION_UNDO);
		compcp.getActionMap().put(CSteModel.ACTION_UNDO,
				new SteActionListener(CSteModel.ACTION_UNDO, listener));

		// ɾ��
		KeyStroke vkctrld = KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrld,
				CSteModel.ACTION_DEL);
		compcp.getActionMap().put(CSteModel.ACTION_DEL,
				new SteActionListener(CSteModel.ACTION_DEL, listener));

		// F9 ����
		KeyStroke vkf9 = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf9,
				CSteModel.ACTION_SAVE);
		compcp.getActionMap().put(CSteModel.ACTION_SAVE,
				new SteActionListener(CSteModel.ACTION_SAVE, listener));

		// f2 �༭
		KeyStroke vkf2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf2,
				CSteModel.ACTION_MODIFY);
		compcp.getActionMap().put(CSteModel.ACTION_MODIFY,
				new SteActionListener(CSteModel.ACTION_MODIFY, listener));
		
		


/*		// esc ���ر༭��
		KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkesc,
				CSteModel.ACTION_HIDEFORM);
		compcp.getActionMap().put(CSteModel.ACTION_HIDEFORM,
				new SteActionListener(CSteModel.ACTION_HIDEFORM, listener));
*/
		// ��ӡ
		KeyStroke vkctrlp = KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlp,
				CSteModel.ACTION_PRINT);
		compcp.getActionMap().put(CSteModel.ACTION_PRINT,
				new SteActionListener(CSteModel.ACTION_PRINT, listener));
		
		
		//�˳�
		KeyStroke altx = KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.ALT_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altx,
				CSteModel.ACTION_EXIT);
		compcp.getActionMap().put(CSteModel.ACTION_EXIT,
				new SteActionListener(CSteModel.ACTION_EXIT, listener));
		

	}

	public static void setQueryHotkey(JComponent compcp, ActionListener listener) {

		// F8 ��ѯ
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf8,
				CSteModel.ACTION_QUERY);
		compcp.getActionMap().put(CSteModel.ACTION_QUERY,
				new SteActionListener(CSteModel.ACTION_QUERY, listener));

		// ��ӡ
		KeyStroke vkctrlp = KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlp,
				CSteModel.ACTION_PRINT);
		compcp.getActionMap().put(CSteModel.ACTION_PRINT,
				new SteActionListener(CSteModel.ACTION_PRINT, listener));

	}
}
