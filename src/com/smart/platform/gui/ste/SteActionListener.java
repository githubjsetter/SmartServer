package com.smart.platform.gui.ste;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-14
 * Time: 16:15:36
 * To change this template use File | Settings | File Templates.
 */
public class SteActionListener extends AbstractAction{
    ActionListener listener=null;

    public SteActionListener(String name, ActionListener listener) {
        super(name);
        this.listener = listener;
        super.putValue(AbstractAction.ACTION_COMMAND_KEY,name);
    }

    public void actionPerformed(ActionEvent e) {
        listener.actionPerformed(e);
    }
}
