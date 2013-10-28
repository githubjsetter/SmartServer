package com.smart.platform.gui.control;

import javax.swing.*;
import java.text.ParseException;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-6
 * Time: 17:27:26
 * To change this template use File | Settings | File Templates.
 */
public class CNumberTextField extends CFormatTextField{
    /**
     * 小数点位数
     */
    int scale=0;
    
    /**
     * 是否允许逗号.
     */
    boolean allowcomma=false;

    public CNumberTextField(int scale) {
        super(new CNumberFormatter(scale));
        this.scale = scale;
    }

    public void replaceSelection(String content) {
        if(!canedit){
            return;
        }
        char[] chars = content.toCharArray();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<chars.length;i++){
            char c=chars[i];
            
            if(c==',' && allowcomma){
            	sb.append(c);
            	continue;
            }
            
            if(! isvalidvarchar(c)){
                continue;
            }
            sb.append(c);
        }
        super.replaceSelection(sb.toString());
    }

    static boolean isvalidvarchar(char c){
        if(c=='.' || c=='+' || c=='-')return true;
        return c>='0' && c<='9';
    }

	public boolean isAllowcomma() {
		return allowcomma;
	}

	public void setAllowcomma(boolean allowcomma) {
		this.allowcomma = allowcomma;
	}

}

