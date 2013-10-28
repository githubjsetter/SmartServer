package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-9
 * Time: 17:50:02
 * To change this template use File | Settings | File Templates.
 */
public class CUpperFormatter extends CFormatterbase {
	
    @Override
	public void install(JFormattedTextField ftf) {
		super.install(ftf);
		String s=ftf.getText();
		ftf.getCaret().setDot(s.length());
		ftf.getCaret().moveDot(0);
	}

	public Object stringToValue(String text) throws ParseException {
        return text.toUpperCase();
    }

    public String valueToString(Object value) throws ParseException {
        return (String) value;
    }

    protected DocumentFilter getDocumentFilter() {
        return new UpperDocumentFilter();
    }

    class UpperDocumentFilter extends DocumentFilter {
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if(!canedit){
                return;
            }
            super.insertString(fb, offset, string.toUpperCase(), attr);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if(!canedit){
                return;
            }
            super.replace(fb, offset, length, text.toUpperCase(), attrs);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if(!canedit){
                return;
            }
            super.remove(fb, offset, length);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}

