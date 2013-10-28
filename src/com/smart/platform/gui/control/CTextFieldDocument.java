package com.smart.platform.gui.control;

import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-5
 * Time: 15:40:38
 * To change this template use File | Settings | File Templates.
 */
public class CTextFieldDocument extends PlainDocument {

    public CTextFieldDocument() {
        super();
    }



    public void insertString(int offs, String str, AttributeSet a)
            throws BadLocationException {

        if (str == null) {
            return;
        }
        char[] upper = str.toCharArray();
        for (int i = 0; i < upper.length; i++) {
            upper[i] = Character.toUpperCase(upper[i]);
        }
        super.insertString(offs, new String(upper), a);
    }

}