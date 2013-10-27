package com.inca.np.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-29
 * Time: 14:18:21
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {
    /**
     * 速度字节数转为 k m显示
     * @param bytes
     * @param ms 用时毫秒
     * @return
     */
    public static String bytespeed2string(int bytes,long ms){
        StringBuffer sb=new StringBuffer();
        DecimalFormat fm=new DecimalFormat("0.0");
        double speed = (double)bytes / (ms/1000.0);
        if(speed>=1048576.0){
             sb.append(fm.format(speed/1048576.0)+"M");
        }else if(speed>1024){
            sb.append(fm.format(speed/1024)+"K");
        }else{
            sb.append(fm.format(speed)+"B");
        }

        return sb.toString();
    }


    public static String bytes2string(int bytes){
        StringBuffer sb=new StringBuffer();
        DecimalFormat fm=new DecimalFormat("0.0");
        double db=(double)bytes;
        if(db>=1048576.0){
             sb.append(fm.format(db/1048576.0)+"M");
        }else if(db>1024){
            sb.append(fm.format(db/1024)+"K");
        }else{
            sb.append(fm.format(db)+"B");
        }

        return sb.toString();
    }
    
    public static String bytes2string(BigDecimal bytes){
        BigDecimal decK=new BigDecimal(1024);
        BigDecimal decM=decK.multiply(decK);
        BigDecimal decG=decM.multiply(decK);
        
        MathContext mc=new MathContext(3,RoundingMode.HALF_UP);
        if(bytes.compareTo(decG)>=0){
        	return bytes.divide(decG,mc).toPlainString()+"G";
        }else if(bytes.compareTo(decM)>=0){
        	return bytes.divide(decM,mc).toPlainString()+"M";
        }else if(bytes.compareTo(decK)>=0){
        	return bytes.divide(decK,mc).toPlainString()+"K";
        }else{
        	return bytes.toPlainString();
        }

    }

    public static String max(String s,int len){
    	if(s==null)s="";
    	int curlen=0;
    	for(int i=0;i<s.length();i++){
    		char c=s.charAt(i);
        	StringBuffer sb=new StringBuffer();
        	sb.append(c);
    		int addlen=0;
    		try {
				addlen = sb.toString().getBytes("gbk").length;
			} catch (UnsupportedEncodingException e) {
			}
    		if(addlen + curlen > len){
    			return s.substring(0,i);
    		}
    		curlen+=addlen;
    	}
    	return s;
    }

    public static void main(String[] argv){
    	BigDecimal bytes=new BigDecimal("51111048576");
    	String s=StringUtil.bytes2string(bytes);
    	System.out.println(s);
    }
}
