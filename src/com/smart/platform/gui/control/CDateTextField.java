package com.smart.platform.gui.control;

import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.text.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-5
 * Time: 18:53:53
 * 以yyyy-MM-dd 方式　编辑
 */
public class CDateTextField extends CFormatTextField {

    public CDateTextField() {
        super(new CDateFormatter());
    }

    public void setText(String t) {
        super.setText(t);
    }

    /**
     * 重载 replaceSelection．　根据输入和位置设置doc的内容．
     * @param content
     */
    public void replaceSelection(String content) {
        if(!canedit){
            return;
        }

        //规整
        try {
            Date date = CDateFormatter.dateformat.parse(content);
            content = CDateFormatter.dateformat.format(date);
        } catch (ParseException e) {

        }
        AbstractDocument doc = (AbstractDocument) getDocument();
        Caret caret = this.getCaret();
        if (doc != null) {

            char[] chararray = content.toCharArray();
            for (int i = 0; i < chararray.length; i++) {
                char c = chararray[i];
                int p0 = Math.min(caret.getDot(), caret.getMark());
                if (c == '-') {
                    continue;
                }

                if( !isdigit(c)) {
                    continue;
                }

                StringBuffer sb = new StringBuffer();
                sb.append(c);

                int len = 1 ;
                try {
                    doc.replace(p0, len, sb.toString(), null);
                } catch (BadLocationException e) {
                    //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                if (p0 == 3 || p0 == 6) {
                    caret.setDot(p0 + 2);
                } else {
                    caret.setDot(p0 + 1);
                }
            }

        }
    }



    static boolean isdigit(char c){
        return c>='0' && c<='9';
    }

}
