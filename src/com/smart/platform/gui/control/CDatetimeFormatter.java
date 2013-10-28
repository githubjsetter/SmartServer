package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-7
 * Time: 16:11:50
 * To change this template use File | Settings | File Templates.
 */
public class CDatetimeFormatter extends CFormatterbase {
    static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    JFormattedTextField ftf = null;

    public void install(JFormattedTextField ftf) {
        this.ftf = ftf;
        super.install(ftf);    //To change body of overridden methods use File | Settings | File Templates.
		String s=ftf.getText();
		ftf.getCaret().setDot(s.length());
		ftf.getCaret().moveDot(0);
    }

    /**
     * 用字符串表达日期
     * @param text
     * @return
     * @throws java.text.ParseException
     */
    public Object stringToValue(String text) throws ParseException {
        if(text.startsWith("0000-00-00")){
            return "";
        }
        try {
            Date date = dateformat.parse(text);
            return dateformat.format(date);
        } catch (ParseException e) {
            throw new ParseException("请输入的日期格式有误，请按YYYY-MM-DD HH:MM:SS格式输入日期时间",0);
        }
    }

    public String valueToString(Object value) throws ParseException {
        if (value == null || ((String)value).length()==0) {
            return "0000-00-00 00:00:00";
        }
        return (String)value;
    }

    protected DocumentFilter getDocumentFilter() {
        return new DateDocumentFilter();
    }


    protected NavigationFilter getNavigationFilter() {
        //return super.getNavigationFilter();    //To change body of overridden methods use File | Settings | File Templates.
        return new DateTextFieldNavigate();
    }


    int maskpos[] = {4,7,10,13,16};


    class DateDocumentFilter extends DocumentFilter {
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if(!canedit){
                return;
            }
            AbstractDocument doc = (AbstractDocument)fb.getDocument();
            for(int i=offset;i<offset + length;i++){
                String s = doc.getText(i,1);
                if(s.equals("-") || s.equals(" ") || s.equals(":")){
                    continue;
                }else{
                    super.replace(fb,i,1,"0",null);
                }
            }
            ftf.getCaret().setDot(offset);
        }

        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if(!canedit){
                return;
            }
            super.insertString(fb, offset, string, attr);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if(!canedit){
                return;
            }
            for(int i=0;i<text.length();i++){
            	if(isMarkpos(offset+i))continue;
            	char c=text.charAt(i);
            	StringBuffer sb=new StringBuffer();
            	sb.append(c);
            	super.replace(fb, offset+i, 1, sb.toString(), attrs);
            }
        }
        
        boolean isMarkpos(int p){
        	for(int i=0;i<maskpos.length;i++){
        		if(p==maskpos[i])return true;
        	}
        	return false;
        }

    }

    class DateTextFieldNavigate extends NavigationFilter {
        public void setDot(FilterBypass fb, int dot, Position.Bias bias) {
            fb.setDot(dot, bias);
        }

        public void moveDot(FilterBypass fb, int dot, Position.Bias bias) {
            fb.moveDot(dot, bias);
        }

        public int getNextVisualPositionFrom(JTextComponent text, int pos, Position.Bias bias, int direction, Position.Bias[] biasRet) throws BadLocationException {
            int retpos = super.getNextVisualPositionFrom(text, pos, bias, direction, biasRet);    //To change body of overridden methods use File | Settings | File Templates.
            int newpos;
            if (SwingConstants.EAST == direction) {
                if (pos >= text.getText().length()) {
                    return pos;
                }
                newpos = calcNextposition(retpos, 1);
            } else {
                if (pos == 0) return pos;
                newpos = calcNextposition(retpos, -1);
            }

            //System.out.println("pos="+pos+",bias="+bias+",direction="+direction+",retpos="+retpos+",newpos="+newpos);

            return newpos;
        }

        int calcNextposition(int dot, int direction) {
            for (int i = 0; i < maskpos.length; i++) {
                if (maskpos[i] == dot) {
                    return dot + direction;
                }
            }
            return dot;
        }
    }

}


