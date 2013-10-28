package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.Caret;
import javax.swing.text.DocumentFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;

import java.awt.Container;
import java.awt.Frame;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-10
 * Time: 11:02:37
 * To change this template use File | Settings | File Templates.
 */
public class CPlainTextField extends CFormatTextField{
    public CPlainTextField() {
        super(new CPlainFormatter());
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void replaceSelection(String content) {

        if(!canedit){
            return;
        }

        super.replaceSelection(content);    //To change body of overridden methods use File | Settings | File Templates.
    }


    public static void main(String[] argv){
    	JDialog dlg=new JDialog((Frame)null,"Test",true);
    	Container cp= dlg.getContentPane();
    	CPlainTextField tf=new CPlainTextField();
    	tf.setValue("123456");

    	
    	cp.add(tf);
    	dlg.pack();
    	dlg.setVisible(true);
    }
    
}
