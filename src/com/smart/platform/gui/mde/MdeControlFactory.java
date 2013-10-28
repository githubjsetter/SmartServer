package com.smart.platform.gui.mde;

import com.smart.platform.gui.control.CMdeDtltoolbar;
import com.smart.platform.gui.control.CMdetoolbar;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.SteActionListener;
import com.smart.platform.util.DefaultNPParam;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-14 Time: 16:02:59
 * To change this template use File | Settings | File Templates.
 */
public class MdeControlFactory {
	public static CMdetoolbar createMdetoolbar(ActionListener l) {
		CMdetoolbar toolbar = new CMdetoolbar(l);
		toolbar.removeDefaultKey();
		return toolbar;
	}

	public static CMdeDtltoolbar createMdedtltoolbar(ActionListener l) {
		CMdeDtltoolbar toolbar = new CMdeDtltoolbar(l);
		toolbar.removeDefaultKey();
		return toolbar;
	}

	public static JPopupMenu createPopupmenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("�˵�");
		JMenuItem item;
		item = new JMenuItem("���� Ctrl+N");
		item.setActionCommand(CMdeModel.ACTION_NEW);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("�޸� F2");
		item.setActionCommand(CMdeModel.ACTION_MODIFY);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("�����޸� Ctrl+Z");
		item.setActionCommand(CMdeModel.ACTION_UNDO);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("ɾ�� Ctrl+D");
		item.setActionCommand(CMdeModel.ACTION_DEL);
		item.addActionListener(actionListener);
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("����ϸ�� Ctrl+I");
		item.setActionCommand(CMdeModel.ACTION_NEWDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("����ϸ���޸� Ctrl+Shift+Z");
		item.setActionCommand(CMdeModel.ACTION_UNDODTL);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("ɾ��ϸ�� Ctrl+D");
		item.setActionCommand(CMdeModel.ACTION_DELDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("��ѯ F8");
		item.setActionCommand(CMdeModel.ACTION_QUERY);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		item = new JMenuItem("��һ��");
		item.setActionCommand(CMdeModel.ACTION_FIRST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ǰһ��");
		item.setActionCommand(CMdeModel.ACTION_PRIOR);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("��һ��");
		item.setActionCommand(CMdeModel.ACTION_NEXT);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ĩһ��");
		item.setActionCommand(CMdeModel.ACTION_LAST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("����");
		item.setActionCommand(CMdeModel.ACTION_SAVE);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		item = new JMenuItem("����");
		item.setActionCommand(CMdeModel.ACTION_EXPORT);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("����Ϊ");
		item.setActionCommand(CMdeModel.ACTION_EXPORTAS);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("�ܵ�����Ϊ");
		item.setActionCommand(CMdeModel.ACTION_EXPORTMASTERAS);
		item.addActionListener(actionListener);
		popmenu.add(item);

		item = new JMenuItem("ϸ������Ϊ");
		item.setActionCommand(CMdeModel.ACTION_EXPORTDETAILAS);
		item.addActionListener(actionListener);
		popmenu.add(item);
		
		popmenu.addSeparator();

		if (DefaultNPParam.develop == 1) {
			item = new JMenuItem("����ܵ�");
			item.setActionCommand(CMdeModel.ACTION_SETUPUI);
			item.addActionListener(actionListener);
			popmenu.add(item);

			item = new JMenuItem("���ϸ��");
			item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
			item.addActionListener(actionListener);
			popmenu.add(item);
		}

		return popmenu;
	}

	public static JPopupMenu createQueryPopupmenu(ActionListener actionListener) {
		JPopupMenu popmenu = new JPopupMenu("�˵�");
		JMenuItem item;
		item = new JMenuItem("��ѯ F8");
		item.setActionCommand(CMdeModel.ACTION_QUERY);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		item = new JMenuItem("��һ��");
		item.setActionCommand(CMdeModel.ACTION_FIRST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ǰһ��");
		item.setActionCommand(CMdeModel.ACTION_PRIOR);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("��һ��");
		item.setActionCommand(CMdeModel.ACTION_NEXT);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ĩһ��");
		item.setActionCommand(CMdeModel.ACTION_LAST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("����");
		item.setActionCommand(CMdeModel.ACTION_EXPORT);
		item.addActionListener(actionListener);
		popmenu.add(item);
		item = new JMenuItem("����Ϊ");
		item.setActionCommand(CMdeModel.ACTION_EXPORTAS);
		item.addActionListener(actionListener);
		popmenu.add(item);

		popmenu.addSeparator();

		if (DefaultNPParam.develop == 1) {
			item = new JMenuItem("����ܵ�");
			item.setActionCommand(CMdeModel.ACTION_SETUPUI);
			item.addActionListener(actionListener);
			popmenu.add(item);

			item = new JMenuItem("���ϸ��");
			item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
			item.addActionListener(actionListener);
			popmenu.add(item);
		}

		return popmenu;
	}

	public static void setHotkey(JComponent compcp, ActionListener listener) {

		// F8 ��ѯ
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf8,
				CMdeModel.ACTION_QUERY);
		compcp.getActionMap().put(CMdeModel.ACTION_QUERY,
				new SteActionListener(CMdeModel.ACTION_QUERY, listener));
		KeyStroke vkf5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf5,
				CMdeModel.ACTION_REFRESH);
		compcp.getActionMap().put(CMdeModel.ACTION_REFRESH,
				new SteActionListener(CMdeModel.ACTION_REFRESH, listener));

		// Ctrl+N ����
		KeyStroke vkctrln = KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrln,
				CMdeModel.ACTION_NEW);
		compcp.getActionMap().put(CMdeModel.ACTION_NEW,
				new SteActionListener(CMdeModel.ACTION_NEW, listener));

		// ɾ��
		KeyStroke vkctrld = KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrld,
				CMdeModel.ACTION_DEL);
		compcp.getActionMap().put(CMdeModel.ACTION_DEL,
				new SteActionListener(CMdeModel.ACTION_DEL, listener));

		// F9 ����
		KeyStroke vkf9 = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf9,
				CMdeModel.ACTION_SAVE);
		compcp.getActionMap().put(CMdeModel.ACTION_SAVE,
				new SteActionListener(CMdeModel.ACTION_SAVE, listener));

		// f2 �༭
		KeyStroke vkf2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf2,
				CMdeModel.ACTION_MODIFY);
		compcp.getActionMap().put(CMdeModel.ACTION_MODIFY,
				new SteActionListener(CMdeModel.ACTION_MODIFY, listener));

		// undo
		KeyStroke vkctrlz = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlz,
				CMdeModel.ACTION_UNDO);
		compcp.getActionMap().put(CMdeModel.ACTION_UNDO,
				new SteActionListener(CMdeModel.ACTION_UNDO, listener));

		// undodtl
		KeyStroke vkctrlshitz = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlshitz,
				CMdeModel.ACTION_UNDODTL);
		compcp.getActionMap().put(CMdeModel.ACTION_UNDODTL,
				new SteActionListener(CMdeModel.ACTION_UNDODTL, listener));
		
		//�˳�
		KeyStroke altx = KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.ALT_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(altx,
				CMdeModel.ACTION_EXIT);
		compcp.getActionMap().put(CSteModel.ACTION_EXIT,
				new SteActionListener(CMdeModel.ACTION_EXIT, listener));


		// esc ���ر༭��
/*		KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkesc,
				CMdeModel.ACTION_HIDEFORM);
		compcp.getActionMap().put(CMdeModel.ACTION_HIDEFORM,
				new SteActionListener(CMdeModel.ACTION_HIDEFORM, listener));
*/
		// /////////////////ϸ��
		KeyStroke vkctrli = KeyStroke.getKeyStroke(KeyEvent.VK_I,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrli,
				CMdeModel.ACTION_NEWDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_NEWDTL,
				new SteActionListener(CMdeModel.ACTION_NEWDTL, listener));

		KeyStroke vkins = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT,
				0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkins,
				CMdeModel.ACTION_NEWDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_NEWDTL,
				new SteActionListener(CMdeModel.ACTION_NEWDTL, listener));

		// ɾ��
		KeyStroke vkctrldel = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrldel,
				CMdeModel.ACTION_DELDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_DELDTL,
				new SteActionListener(CMdeModel.ACTION_DELDTL, listener));

		// �޸�ϸ��F3
		KeyStroke vkf3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf3,
				CMdeModel.ACTION_MODIFYDTL);
		compcp.getActionMap().put(CMdeModel.ACTION_MODIFYDTL,
				new SteActionListener(CMdeModel.ACTION_MODIFYDTL, listener));

		// /////////////////����
		KeyStroke vkctrlw = KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlw,
				CMdeModel.ACTION_HIDEFORM);
		compcp.getActionMap().put(CMdeModel.ACTION_HIDEFORM,
				new SteActionListener(CMdeModel.ACTION_HIDEFORM, listener));

		// ��ӡ
		KeyStroke vkctrlp = KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlp,
				CMdeModel.ACTION_PRINT);
		compcp.getActionMap().put(CMdeModel.ACTION_PRINT,
				new SteActionListener(CMdeModel.ACTION_PRINT, listener));

	}

	public static void setQueryHotkey(JComponent compcp, ActionListener listener) {

		// F8 ��ѯ
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf8,
				CMdeModel.ACTION_QUERY);
		compcp.getActionMap().put(CMdeModel.ACTION_QUERY,
				new SteActionListener(CMdeModel.ACTION_QUERY, listener));
		// esc ���ر༭��
		KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkesc,
				CMdeModel.ACTION_HIDEFORM);
		compcp.getActionMap().put(CMdeModel.ACTION_HIDEFORM,
				new SteActionListener(CMdeModel.ACTION_HIDEFORM, listener));

		// /////////////////����
		KeyStroke vkctrlw = KeyStroke.getKeyStroke(KeyEvent.VK_W,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlw,
				CMdeModel.ACTION_HIDEFORM);
		compcp.getActionMap().put(CMdeModel.ACTION_HIDEFORM,
				new SteActionListener(CMdeModel.ACTION_HIDEFORM, listener));

		// ��ӡ
		KeyStroke vkctrlp = KeyStroke.getKeyStroke(KeyEvent.VK_P,
				InputEvent.CTRL_MASK, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkctrlp,
				CMdeModel.ACTION_PRINT);
		compcp.getActionMap().put(CMdeModel.ACTION_PRINT,
				new SteActionListener(CMdeModel.ACTION_PRINT, listener));

	}
}
