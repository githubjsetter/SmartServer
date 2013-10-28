package com.smart.extension.ste;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.ste.CSteModel;

public class CStetoolbarAp extends CStetoolbar{
	
    public CStetoolbarAp(ActionListener l) {
		super(l);
		// TODO Auto-generated constructor stub
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


        item = new JMenuItem("保存界面");
        item.setActionCommand(CSteModel.ACTION_SAVEUI);
        item.addActionListener(actionListener);
        popmenu.add(item);

        item = new JMenuItem("规则设置");
        item.setActionCommand(CSteModel.ACTION_SETUPRULE);
        item.addActionListener(actionListener);
        popmenu.add(item);

        item = new JMenuItem("授权属性");
        item.setActionCommand(CSteModelAp.ACTION_SETAP);
        item.addActionListener(actionListener);
        popmenu.add(item);
        

        return popmenu;
    }

}
