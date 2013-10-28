package com.smart.platform.gui.control;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Caret;
import javax.swing.text.BadLocationException;
import java.util.Date;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-7-20
 * Time: 11:24:51
 * 以YYYY-MM形式编辑月
 */
public class CYMTextField extends CFormatTextField {

    public CYMTextField() {
        super(new CYMFormatter());
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
            Date date = CYMFormatter.dateformat.parse(content);
            content = CYMFormatter.dateformat.format(date);
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

                //约束
                /*位数 约束
                0      1 2
                1      9 0 1
                2
                3
                4      -
                5      0 1
                6
                */

                if(p0==0){
                    if(c!='1' && c!='2'){
                        return;
                    }
                }else if(p0==1){
                    if(c!='0' && c!='1' && c!='9'){
                        return;
                    }
                }else if(p0==5){
                    if(c!='0' && c!='1' ){
                        return;
                    }
                }else if(p0==6){
                    try {
                        String dig5 = doc.getText(5,1);
                        if(dig5.equals("1")){
                            if(c!='0' && c!='1' && c!='2' ){
                                return;
                            }
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
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
