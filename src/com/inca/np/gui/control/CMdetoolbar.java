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
            addButton("新增", IconFactory.icnew,
                    "新增一条总单记录．热键Ctrl+N",
                    CMdeModel.ACTION_NEW);
        }

        if (isUsebutton(CMdeModel.ACTION_MODIFY)) {
            addButton("编辑", IconFactory.icedit,
                    "打开编辑窗口编辑记录．热键F2",
                    CMdeModel.ACTION_MODIFY);
        }

        if (isUsebutton(CMdeModel.ACTION_UNDO)) {
            addButton("撤消", IconFactory.icundo,
                    "新增状态的记录删除．修改状态的记录恢复数据库值．删除状态的记录不删除．热键Ctrl+Z",
                    CMdeModel.ACTION_UNDO);
        }

        if (isUsebutton(CMdeModel.ACTION_DEL)) {
            addButton("删除", IconFactory.icdelete,
                    "将记录置为删除状态，保存后在数据库中删除．热键Ctrl+D",
                    CMdeModel.ACTION_DEL);
        }

        if (isUsebutton(CMdeModel.ACTION_QUERY)) {
            querybtn = addButton("查询", IconFactory.icquery,
                    "输入条件，从数据中查询记录．热键F8.刷新热键F5",
                    CMdeModel.ACTION_QUERY);
        }
        addSeparator();
        createOtherButton(l);

        /** 20070802 去掉滚动钮

        if (isUsebutton(CMdeModel.ACTION_FIRST)) {
            addButton(IconFactory.icfirst,
                    "定位到第一条记录",
                    CMdeModel.ACTION_FIRST);
        }

        if (isUsebutton(CMdeModel.ACTION_PRIOR)) {
            addButton(IconFactory.icprior,
                    "定位到上一条记录．热键向上键头",
                    CMdeModel.ACTION_PRIOR);
        }

        if (isUsebutton(CMdeModel.ACTION_NEXT)) {
            addButton(IconFactory.icnext,
                    "定位到下一条记录．热键向下键头",
                    CMdeModel.ACTION_NEXT);
        }


        if (isUsebutton(CMdeModel.ACTION_LAST)) {
            addButton(IconFactory.iclast,
                    "定位到最后一条记录",
                    CMdeModel.ACTION_LAST);
        }
         */

        this.addSeparator(new Dimension(10,0));
 
        if (isUsebutton(CMdeModel.ACTION_NEWDTL)) {
            addButton("新增细单", IconFactory.icnew,
                    "新增一条细单．必须已有总单．热键Ctrl+I Insert",
                    CMdeModel.ACTION_NEWDTL);
        }

        if (isUsebutton(CMdeModel.ACTION_MODIFYDTL)) {
            addButton("编辑细单", IconFactory.icedit,
                    "编辑一条细单．热键F3",
                    CMdeModel.ACTION_MODIFYDTL);
        }
        
        if (isUsebutton(CMdeModel.ACTION_UNDODTL)) {
            addButton("撤消细单", IconFactory.icundo,
                    "新增状态的记录删除．修改状态的记录恢复数据库值．删除状态的记录不删除．热键Ctrl+Shift+Z",
                    CMdeModel.ACTION_UNDODTL);
        }

        if (isUsebutton(CMdeModel.ACTION_DELDTL)) {
            addButton("删除细单", IconFactory.icdelete,
                    "将细单记录置为删除状态，保存后在数据库中删除．热键Ctrl+Del",
                    CMdeModel.ACTION_DELDTL);
        }

        this.addSeparator(new Dimension(10, 0));


        JButton btn = null;
        if (isUsebutton("打印")) {
            btn = addButton("打印", IconFactory.icprint,
                    "将数据发送到发打印机打印",
                    "print");
            btn.addMouseListener(new PrintMouseEventHandle());
        }

        addSeparator();


        if (isUsebutton(CMdeModel.ACTION_SAVE)) {
            addButton("保存", IconFactory.icsave,
                    "将变化的数据保存到服务器 F9",
                    CMdeModel.ACTION_SAVE);
        }

        this.addSeparator(new Dimension(40, 0));

        if (isUsebutton(CMdeModel.ACTION_SELECTOP)) {
            //btn = addButton("功能",
            //        "选择其它功能",
            //        CMdeModel.ACTION_SELECTOP);
        }

        if (isUsebutton(CMdeModel.ACTION_EXIT)) {
            btn = addButton("关闭",
                    "关闭功能 Alt+X",
                    CMdeModel.ACTION_EXIT);
        }

        if (DefaultNPParam.develop==1 && isUsebutton("设计")) {
            btn = addButton("设计",
                    "界面设计",
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
        JPopupMenu popmenu = new JPopupMenu("设计菜单");
        JMenuItem item;
        item = new JMenuItem("自　　　　检");
        item.setActionCommand(CMdeModel.ACTION_SELFCHECK);
        item.addActionListener(actionListener);
        popmenu.add(item);

        item = new JMenuItem("总单界面设置");
        item.setActionCommand(CMdeModel.ACTION_SETUPUI);
        item.addActionListener(actionListener);
        popmenu.add(item);


		item = new JMenuItem("细单界面设置");
		item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
		item.addActionListener(actionListener);
		popmenu.add(item);

        
        item = new JMenuItem("规则设置");
        item.setActionCommand(CMdeModel.ACTION_SETUPRULE);
        item.addActionListener(actionListener);
        popmenu.add(item);
        
        
        return popmenu;
    }


    public static JPopupMenu createPrintMenu(ActionListener actionListener) {
        JPopupMenu popmenu = new JPopupMenu("打印");
        JMenuItem item;
        item = new JMenuItem("打印设置");
        item.setActionCommand(CMdeModel.ACTION_PRINTSETUP);
        item.addActionListener(actionListener);
        popmenu.add(item);


        item = new JMenuItem("打印 CTRL+P");
        item.setActionCommand(CMdeModel.ACTION_PRINT);
        item.addActionListener(actionListener);
        popmenu.add(item);
        return popmenu;
    }
}
