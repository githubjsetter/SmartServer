package com.inca.np.gui.control;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.text.ParseException;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-6-12 Time: 16:19:40
 * To change this template use File | Settings | File Templates.
 */
public class CNumberFormatter extends CFormatterbase {
	CNumberTextField nftf = null;

	@Override
	public void install(JFormattedTextField ftf) {
		super.install(ftf);
		nftf = (CNumberTextField) ftf;
		String s = ftf.getText();
		ftf.getCaret().setDot(s.length());
		ftf.getCaret().moveDot(0);
	}

	/**
	 * 小数精度
	 */
	int scale = 0;

	public CNumberFormatter(int scale) {
		this.scale = scale;
	}

	protected DocumentFilter getDocumentFilter() {
		return new NumberDocumentFilter();
	}

	/**
	 * 用字符串表示数字
	 * 
	 * @param text
	 * @return
	 * @throws java.text.ParseException
	 */
	public Object stringToValue(String text) throws ParseException {
		if (text == null || text.trim().length() == 0) {
			return "";
		}

		if (nftf.isAllowcomma()) {
			return text;
		}
		if (scale == 0) {
			return text;
		}

		try {
			text=text.replaceAll(",", "");
			BigDecimal d = new BigDecimal(text);
			d = d.setScale(scale, BigDecimal.ROUND_HALF_UP);
			String strvalue = d.toPlainString();
			return strvalue;
		} catch (Exception e) {
			throw new ParseException("请输入合法的数字", 0);
		}
	}

	/**
	 * 用字符串表示数字
	 * 
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public String valueToString(Object value) throws ParseException {
		return (String) value;
	}

	class NumberDocumentFilter extends DocumentFilter {
		public void insertString(FilterBypass fb, int offset, String string,
				AttributeSet attr) throws BadLocationException {
			if (!canedit) {
				return;
			}
			super.insertString(fb, offset, string.toUpperCase(), attr); 
																		
		}

		public void replace(FilterBypass fb, int offset, int length,
				String text, AttributeSet attrs) throws BadLocationException {
			if (!canedit) {
				return;
			}
			super.replace(fb, offset, length, text.toUpperCase(), attrs); 
		}

		public void remove(FilterBypass fb, int offset, int length)
				throws BadLocationException {
			if (!canedit) {
				return;
			}
			super.remove(fb, offset, length); // To change body of overridden
												// methods use File | Settings |
												// File Templates.
		}
	}

}
