package com.inca.np.gui.control;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 15:26:42
 * To change this template use File | Settings | File Templates.
 */
public class CStatusbar extends JToolBar{
    JLabel lbstatus=new JLabel();
    public CStatusbar() {
        add(lbstatus);
    }

    public void setStatus(String s){
        lbstatus.setText(s);
    }
}
