package com.inca.np.gui.ste;

import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CScrollPane;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 15:39:20
 * ²éÑ¯¶Ô»°¿ò
 */
public class Stequerydialog extends CDialog{
    public Stequerydialog() throws HeadlessException {
        super();
        dialogInit();
    }

    public Stequerydialog(Frame owner, String title) throws HeadlessException {
        super(owner, title);
        dialogInit();
    }


    
    CScrollPane condpane=null;



}
