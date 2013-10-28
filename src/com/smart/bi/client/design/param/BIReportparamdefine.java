package com.smart.bi.client.design.param;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 参数定义.
 * 
 * @author user
 * 
 */
public class BIReportparamdefine {
	public String paramname = "";;
	public String paramtype = "";;
	public String title = "";;
	public int numberwidth = 0;
	public boolean mustinput = false;
	public String hovclass = "";;
	public String hovcols = "";
	public String initvalue = "";
	public String autocond="";
	private String inputvalue = "";

	public String getOrgInputvalue() {
		return inputvalue;
	}

	public String getInputvalue() {
		String rets="";
		if ( numberwidth > 0) {
			rets = fillZeroleft(inputvalue, numberwidth);
		}else{
			rets=inputvalue;
		}
		if(rets.length()==0){
			rets=getRealInitvalue();
		}
		return rets;
	}

	static String fillZeroleft(String s, int maxw) {
		StringBuffer sb = new StringBuffer();
		int offset = maxw - s.length();
		for (int i = 0; i < offset; i++) {
			sb.append("0");
		}
		sb.append(s);
		return sb.toString();
	}

	public void setInputvalue(String inputvalue) {
		this.inputvalue = inputvalue;
	}

	/**
	 * 取现在初值
	 * 
	 * @return
	 */
	public String getRealInitvalue() {
		String s = "";
		Date now = new Date();
		if (initvalue.equals("nowyear")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			return df.format(now);
		} else if (initvalue.equals("nowyearmonth")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
			return df.format(now);
		} else if (initvalue.equals("nowday")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.format(now);
		} else if (initvalue.equals("nowmonth")) {
			SimpleDateFormat df = new SimpleDateFormat("MM");
			return df.format(now);
		}
		return "";
	}
}
