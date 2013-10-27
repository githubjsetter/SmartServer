package com.inca.np.gui.control;

import javax.swing.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-6-12
 * Time: 16:10:11
 * To change this template use File | Settings | File Templates.
 */
public abstract class CFormatterbase extends JFormattedTextField.AbstractFormatter{
    protected boolean canedit=true;

    public boolean isCanedit() {
        return canedit;
    }

    public void setCanedit(boolean canedit) {
        this.canedit = canedit;
    }
}
