package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.text.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-6
 * Time: 9:58:30
 * To change this template use File | Settings | File Templates.
 */
public class TestTextFormater extends JFormattedTextField.AbstractFormatter{
    SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");

    public void install(JFormattedTextField ftf) {
        super.install(ftf);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Object stringToValue(String text) throws ParseException {

        //DefaultFormatter
        //DateFormatter
        //MaskFormatter
        return dateformat.parse(text);
    }

    public String valueToString(Object value) throws ParseException {
        if(value==null){
            return "0000-00-00";
        }
        Date date=(Date)value;
        String text = dateformat.format(date);
        return text;
    }



    protected NavigationFilter getNavigationFilter() {
        //return super.getNavigationFilter();    //To change body of overridden methods use File | Settings | File Templates.
        return new TestNav();
    }

    protected DocumentFilter getDocumentFilter() {
        return new TestDocFilter();
    }

    int maskpos[]={4,7};


    class TestDocFilter extends DocumentFilter{
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            super.remove(fb, offset, length);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, offset, string, attr);    //To change body of overridden methods use File | Settings | File Templates.
        }

        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            super.replace(fb, offset, length, text, attrs);    //To change body of overridden methods use File | Settings | File Templates.
        }

    }

    class TestNav extends NavigationFilter{
        public void setDot(FilterBypass fb, int dot, Position.Bias bias) {
            //int newdot = calcNextposition(dot,bias);
            fb.setDot(dot,bias);
            //System.out.println("setDot");
        }

        public void moveDot(FilterBypass fb, int dot, Position.Bias bias) {
            //int newdot = calcNextposition(dot,bias);
            fb.moveDot(dot,bias);
            //System.out.println("moveDot");
        }

        public int getNextVisualPositionFrom(JTextComponent text, int pos, Position.Bias bias, int direction, Position.Bias[] biasRet) throws BadLocationException {
            int retpos = super.getNextVisualPositionFrom(text, pos, bias, direction, biasRet);    //To change body of overridden methods use File | Settings | File Templates.
            int newpos;
            if(SwingConstants.EAST  == direction){
                if(pos >=text.getText().length()) {
                    return pos;
                }
                newpos=filterPos(retpos,1);
            }else{
                if(pos==0)return pos;
                newpos=filterPos(retpos,-1);
            }

            //System.out.println("pos="+pos+",bias="+bias+",direction="+direction+",retpos="+retpos+",newpos="+newpos);

            return newpos ;
        }

        public int filterPos(int dot,int direction){
            for(int i=0;i<maskpos.length;i++){
                if(maskpos[i]==dot){
                    return dot + direction;
                }
            }
            return dot;
        }
    }

}
