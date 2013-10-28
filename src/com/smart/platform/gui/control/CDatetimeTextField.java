package com.smart.platform.gui.control;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Caret;
import javax.swing.text.BadLocationException;
import java.util.Date;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-7
 * Time: 16:11:22
 * To change this template use File | Settings | File Templates.
 */
public class CDatetimeTextField  extends CFormatTextField {

    public CDatetimeTextField() {
        super(new CDatetimeFormatter());
    }

    public void setText(String t) {
        super.setText(t);
    }

    /**
     * 重载 replaceSelection．　根据输入和位置设置doc的内容．
     * @param content
     */
    public void replaceSelection(String content) {

        //规整
        try {
            Date date = CDatetimeFormatter.dateformat.parse(content);
            content = CDatetimeFormatter.dateformat.format(date);
        } catch (ParseException e) {

        }
        AbstractDocument doc = (AbstractDocument) getDocument();
        Caret caret = this.getCaret();
        if (doc != null) {

            char[] chararray = content.toCharArray();
            for (int i = 0; i < chararray.length; i++) {
                char c = chararray[i];
                int p0 = Math.min(caret.getDot(), caret.getMark());
                if (c == '-' || c==' ' || c==':') {
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
                if (p0 == 3 || p0 == 6 || p0==9 ||p0==12 || p0==15) {
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
