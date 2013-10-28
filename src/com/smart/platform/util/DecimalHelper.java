package com.smart.platform.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 十进制数字处理
 * @author Administrator
 *
 */
public class DecimalHelper {
	public static BigDecimal zero=new BigDecimal("0");
	/**
	 * 将字符串转为BigDecimal
	 * @param s
	 * @return 如果异常按0处理
	 */
	public static BigDecimal toDec(String s){
		try
		{
			if(s==null)s="";
			s=s.replaceAll(",", "");
			return new BigDecimal(s);
		}catch(Exception e){
			return new BigDecimal(0);
		}
	}
	
	/**
	 * 加法
	 * @param s1 字符串1
	 * @param s2 字符串2
	 * @param scale 精度,一般为2 或4
	 * @return 返回结果字符串
	 */
	public static String add(String s1,String s2,int scale){
		BigDecimal d1=toDec(s1);
		BigDecimal d2=toDec(s2);
		BigDecimal result=d1.add(d2);
		result=result.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return result.toPlainString();
	}

	/**
	 * 减法
	 * @param s1
	 * @param s2
	 * @param scale
	 * @return
	 */
	public static String sub(String s1,String s2,int scale){
		BigDecimal d1=toDec(s1);
		BigDecimal d2=toDec(s2);
		BigDecimal result=d1.subtract(d2);
		result=result.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return result.toPlainString();
	}
	/**
	 * 乘法
	 * @param s1
	 * @param s2
	 * @param scale
	 * @return
	 */
	public static String multi(String s1,String s2,int scale){
		BigDecimal d1=toDec(s1);
		BigDecimal d2=toDec(s2);
		BigDecimal result=d1.multiply(d2);
		result=result.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return result.toPlainString();
	}
	
	/**
	 * 除法
	 * @param s1
	 * @param s2
	 * @param scale
	 * @return
	 */
	public static String divide(String s1,String s2,int scale){
		MathContext mc=new MathContext(20, RoundingMode.HALF_UP); 
		BigDecimal d1=toDec(s1);
		BigDecimal d2=toDec(s2);
		BigDecimal result=d1.divide(d2,mc);
		result=result.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return result.toPlainString();
	}
	
	
	public static String format(String s,int scale){
		BigDecimal d=toDec(s);
		d=d.setScale(scale,BigDecimal.ROUND_HALF_UP);
		return d.toPlainString();
	}

	
	/**
	 * 转为负数
	 * @param s
	 * @return
	 */
	public static String toNego(String s){
		BigDecimal dec=DecimalHelper.toDec(s);
		dec=new BigDecimal(0).subtract(dec);
		return dec.toPlainString();
	}
	
	/**
	 * 去小数点后多余的0。如果是整数，小数点也去掉。
	 * @param s
	 * @return
	 */
	public static String trimZero(String s){
		int p=s.lastIndexOf(".");
		if(p<0)return s;
		StringBuffer sb=new StringBuffer();
		sb.append(s);
		
		for(int i=sb.length()-1;i>p;i--){
			char c=sb.charAt(i);
			if(c!='0')break;
			sb.deleteCharAt(i);
		}
		if(sb.charAt(sb.length()-1)=='.'){
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}

    public static String removeZero(String s){
    	int p=s.indexOf(".");
    	if(p<0)return s;
    	
    	int p1=s.length()-1;
    	for(;p1>p;p1--){
    		if(s.charAt(p1)!='0')break;
    	}
    	s= s.substring(0,p1+1);
    	if(s.endsWith(".")){
    		s=s.substring(0,s.length()-1);
    	}
    	return s;
    }

	public static int comparaDecimal(String s1,String s2){
		BigDecimal d1=DecimalHelper.toDec(s1);
		BigDecimal d2=DecimalHelper.toDec(s2);
		return d1.compareTo(d2);
	}

	public static void test1(){
		String s1="1";
		String s2="6";

		for(int i=1;i<=20;i++)
		System.out.println(i+":"+divide(s1, s2, i));
	}
	
	public static void main(String[] argv){
		test1();
		if(true){
			return;
		}
		String s1="12.0341";
		String s2=".34";
		int scale=2;
		System.out.println(s1+"+"+s2+"="+DecimalHelper.add(s1, s2, scale));
		scale=3;
		System.out.println(s1+"+"+s2+"="+DecimalHelper.add(s1, s2, scale));

		s1="10";
		s2=".51";
		scale=2;
		System.out.println(s1+"-"+s2+"="+DecimalHelper.sub(s1, s2, scale));

		s1="3.5";
		s2=".2";
		scale=4;
		System.out.println(s1+"*"+s2+"="+DecimalHelper.multi(s1, s2, scale));
		
		
		s1="10";
		s2="3";
		scale=4;
		System.out.println(s1+"/"+s2+"="+DecimalHelper.divide(s1, s2, scale));
		
		
		
		
	}
}
