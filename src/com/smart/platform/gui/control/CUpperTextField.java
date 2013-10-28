package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-6
 * Time: 17:55:27
 * ´óÐ´ÎÄ±¾¿ò
 */
public class CUpperTextField extends CFormatTextField {
    public CUpperTextField() {
        super(new CUpperFormatter());
    }

    public void setText(String t) {
        if (t != null) {
            super.setText(t.toUpperCase());    //To change body of overridden methods use File | Settings | File Templates.
        }
    }


    public void replaceSelection(String content) {
        if(canedit){
            content = content.toUpperCase();
            super.replaceSelection(content);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }


}
