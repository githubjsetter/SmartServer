package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-20
 * Time: 10:36:47
 * 以YYYY-MM形式录入
 */
public class CYMFormatter extends CFormatterbase {
    static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM");
    JFormattedTextField ftf = null;

    public void install(JFormattedTextField ftf) {
        this.ftf = ftf;
        super.install(ftf);    //To change body of overridden methods use File | Settings | File Templates.
        ftf.getCaret().setDot(0);
    }

    /**
     * 用字符串表达日期
     * @param text
     * @return
     * @throws java.text.ParseException
     */
    public Object stringToValue(String text) throws ParseException {
        if(text.equals("0000-00")){
            return "";
        }
        try {
            Date date = dateformat.parse(text);
            return dateformat.format(date);
        } catch (ParseException e) {
            throw new ParseException("请输入的年月格式有误，请按YYYY-MM格式输入年月 ",0);
        }
    }

    public String valueToString(Object value) throws ParseException {
        if (value == null || ((String)value).length()==0) {
            return "0000-00";
        }
        return (String)value;
    }

    protected DocumentFilter getDocumentFilter() {
        return new YMDocumentFilter();
    }


    protected NavigationFilter getNavigationFilter() {
        //return super.getNavigationFilter();    //To change body of overridden methods use File | Settings | File Templates.
        return new DateTextFieldNavigate();
    }


    int maskpos[] = {4};


    class YMDocumentFilter extends DocumentFilter {
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            if(!canedit){
                return;
            }
            AbstractDocument doc = (AbstractDocument)fb.getDocument();
            for(int i=offset;i<offset + length;i++){
                String s = doc.getText(i,1);
                if(s.equals("-")){
                    continue;
                }else{
                    super.replace(fb,i,1,"0",null);
                }
            }
            ftf.getCaret().setDot(offset);
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if(!canedit){
                return;
            }
            super.replace(fb, offset, length, text, attrs);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if(!canedit){
                return;
            }
            super.insertString(fb, offset, string, attr);    //To change body of overridden methods use File | Settings | File Templates.
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



