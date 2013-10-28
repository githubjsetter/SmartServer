package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-6-12
 * Time: 16:03:52
 * To change this template use File | Settings | File Templates.
 */
public class CPlainFormatter extends CFormatterbase {
    @Override
	public void install(JFormattedTextField ftf) {
		// TODO Auto-generated method stub
		super.install(ftf);
		String s=ftf.getText();
		ftf.getCaret().setDot(s.length());
		ftf.getCaret().moveDot(0);
		
	}

	public Object stringToValue(String text) throws ParseException {
    	//System.out.println("stringToValue text="+text);
    	return text;
    }

    public String valueToString(Object value) throws ParseException {
    	//System.out.println("valueToString value="+value);
        return (String) value;
    }

    protected DocumentFilter getDocumentFilter() {
        return new PlaindocFilter();
    }


    class PlaindocFilter extends DocumentFilter {
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (!canedit) {
                return;
            }
            super.replace(fb, offset, length, text, attrs);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if (!canedit) {
                return;
            }
            super.remove(fb, offset, length);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
