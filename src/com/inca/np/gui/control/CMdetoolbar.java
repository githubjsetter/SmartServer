package com.inca.np.gui.control;

import com.inca.np.gui.control.CStetoolbar.Menuiteminfo;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.image.IconFactory;
import com.inca.np.util.DefaultNPParam;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-14
 * Time: 16:03:35
 * To change this template use File | Settings | File Templates.
 */
public class CMdetoolbar extends CStetoolbar {
    public CMdetoolbar(ActionListener l) {
        actionlistener = l;

        if (isUsebutton(CMdeModel.ACTION_NEW)) {
            addButton("����", IconFactory.icnew,
                    "����һ���ܵ���¼���ȼ�Ctrl+N",
                    CMdeModel.ACTION_NEW);
        }

        if (isUsebutton(CMdeModel.ACTION_MODIFY)) {
            addButton("�༭", IconFactory.icedit,
                    "�򿪱༭���ڱ༭��¼���ȼ�F2",
                    CMdeModel.ACTION_MODIFY);
        }

        if (isUsebutton(CMdeModel.ACTION_UNDO)) {
            addButton("����", IconFactory.icundo,
                    "����״̬�ļ�¼ɾ�����޸�״̬�ļ�¼�ָ����ݿ�ֵ��ɾ��״̬�ļ�¼��ɾ�����ȼ�Ctrl+Z",
                    CMdeModel.ACTION_UNDO);
        }

        if (isUsebutton(CMdeModel.ACTION_DEL)) {
            addButton("ɾ��", IconFactory.icdelete,
                    "����¼��Ϊɾ��״̬������������ݿ���ɾ�����ȼ�Ctrl+D",
                    CMdeModel.ACTION_DEL);
        }

        if (isUsebutton(CMdeModel.ACTION_QUERY)) {
            querybtn = addButton("��ѯ", IconFactory.icquery,
                    "�����������������в�ѯ��¼���ȼ�F8.ˢ���ȼ�F5",
                    CMdeModel.ACTION_QUERY);
        }
        addSeparator();
        createOtherButton(l);

        /** 20070802 ȥ������ť

        if (isUsebutton(CMdeModel.ACTION_FIRST)) {
            addButton(IconFactory.icfirst,
                    "��λ����һ����¼",
                    CMdeModel.ACTION_FIRST);
        }

        if (isUsebutton(CMdeModel.ACTION_PRIOR)) {
            addButton(IconFactory.icprior,
                    "��λ����һ����¼���ȼ����ϼ�ͷ",
                    CMdeModel.ACTION_PRIOR);
        }

        if (isUsebutton(CMdeModel.ACTION_NEXT)) {
            addButton(IconFactory.icnext,
                    "��λ����һ����¼���ȼ����¼�ͷ",
                    CMdeModel.ACTION_NEXT);
        }


        if (isUsebutton(CMdeModel.ACTION_LAST)) {
            addButton(IconFactory.iclast,
                    "��λ�����һ����¼",
                    CMdeModel.ACTION_LAST);
        }
         */

        this.addSeparator(new Dimension(10,0));
 
        if (isUsebutton(CMdeModel.ACTION_NEWDTL)) {
            addButton("����ϸ��", IconFactory.icnew,
                    "����һ��ϸ�������������ܵ����ȼ�Ctrl+I Insert",
                    CMdeModel.ACTION_NEWDTL);
        }

        if (isUsebutton(CMdeModel.ACTION_MODIFYDTL)) {
            addButton("�༭ϸ��", IconFactory.icedit,
                    "�༭һ��ϸ�����ȼ�F3",
                    CMdeModel.ACTION_MODIFYDTL);
        }
        
        if (isUsebutton(CMdeModel.ACTION_UNDODTL)) {
            addButton("����ϸ��", IconFactory.icundo,
                    "����״̬�ļ�¼ɾ�����޸�״̬�ļ�¼�ָ����ݿ�ֵ��ɾ��״̬�ļ�¼��ɾ�����ȼ�Ctrl+Shift+Z",
                    CMdeModel.ACTION_UNDODTL);
        }

        if (isUsebutton(CMdeModel.ACTION_DELDTL)) {
            addButton("ɾ��ϸ��", IconFactory.icdelete,
                    "��ϸ����¼��Ϊɾ��״̬������������ݿ���ɾ�����ȼ�Ctrl+Del",
                    CMdeModel.ACTION_DELDTL);
        }

        this.addSeparator(new Dimension(10, 0));


        JButton btn = null;
        if (isUsebutton("��ӡ")) {
            btn = addButton("��ӡ", IconFactory.icprint,
                    "�����ݷ��͵�����ӡ����ӡ",
                    "print");
            btn.addMouseListener(new PrintMouseEventHandle());
        }

        addSeparator();


        if (isUsebutton(CMdeModel.ACTION_SAVE)) {
            addButton("����", IconFactory.icsave,
                    "���仯�����ݱ��浽������ F9",
                    CMdeModel.ACTION_SAVE);
        }

        this.addSeparator(new Dimension(40, 0));

        if (isUsebutton(CMdeModel.ACTION_SELECTOP)) {
            //btn = addButton("����",
            //        "ѡ����������",
            //        CMdeModel.ACTION_SELECTOP);
        }

        if (isUsebutton(CMdeModel.ACTION_EXIT)) {
            btn = addButton("�ر�",
                    "�رչ��� Alt+X",
                    CMdeModel.ACTION_EXIT);
        }

        if (DefaultNPParam.develop==1 && isUsebutton("���")) {
            btn = addButton("���",
                    "�������",
                    "");
            btn.addMouseListener(new MouseEventHandle());
        }


        Dimension size = this.getPreferredSize();
        this.setPreferredSize(size);


    }

    public void setQuerybuttonText(String s) {
        querybtn.setText(s);
    }

    private class MouseEventHandle implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mousePressed(MouseEvent e) {
            JPopupMenu menu = createDesignMenu(actionlistener);
            menu.show((JComponent) e.getSource(), e.getX(), e.getY());
        }

        public void mouseReleased(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseEntered(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseExited(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private class PrintMouseEventHandle implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
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
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseEntered(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void mouseExited(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public JPopupMenu createDesignMenu(ActionListener actionListener) {
        JPopupMenu popmenu = new JPopupMenu("��Ʋ˵�");
        JMenuItem item;
        item = new JMenuItem("�ԡ���������");
        item.setActionCommand(CMdeModel.ACTION_SELFCHECK);
        item.addActionListener(actionListener);
        popmenu.add(item);

        item = new JMenuItem("�ܵ���������");
        item.setActionCommand(CMdeModel.ACTION_SETUPUI);
        item.addActionListener(actionListener);
        popmenu.add(item);


		item = new JMenuItem("ϸ����������");
		item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);

        
        item = new JMenuItem("��������");
        item.setActionCommand(CMdeModel.ACTION_SETUPRULE);
        item.addActionListener(actionListener);
        popmenu.add(item);
        
        
        return popmenu;
    }


    public static JPopupMenu createPrintMenu(ActionListener actionListener) {
        JPopupMenu popmenu = new JPopupMenu("��ӡ");
        JMenuItem item;
        item = new JMenuItem("��ӡ����");
        item.setActionCommand(CMdeModel.ACTION_PRINTSETUP);
        item.addActionListener(actionListener);
        popmenu.add(item);


        item = new JMenuItem("��ӡ CTRL+P");
        item.setActionCommand(CMdeModel.ACTION_PRINT);
        item.addActionListener(actionListener);
        popmenu.add(item);
        return popmenu;
    }
}
