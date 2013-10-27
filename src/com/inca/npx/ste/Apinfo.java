package com.inca.npx.ste;

/**
 * 一个授权属性信息
 * @author Administrator
 *
 */
public class Apinfo {
	public static final String APTYPE_PARAM="param";
	public static final String APTYPE_FORBID="forbid";
	public static final String APTYPE_DATA="data";
	/**
	 * 授权属性名,禁止新增
	 */
	public static String APNAME_FORBIDNEW="forbidnew";
	/**
	 * 授权属性名,禁止删除
	 */
	public static String APNAME_FORBIDDELETE="forbiddelete";
	/**
	 * 授权属性名,禁止查询
	 */
	public static String APNAME_FORBIDQUERY="forbidquery";
	/**
	 * 授权属性名,禁止修改
	 */
	public static String APNAME_FORBIDMODIFY="forbidmodify";
	/**
	 * 授权属性名,禁止保存
	 */
	public static String APNAME_FORBIDSAVE="forbidsave";

	/**
	 * 禁止导出
	 */
	public static String APNAME_FORBIDEXPORT="forbidexport";

	/**
	 * 禁止再打印
	 */
	public static String APNAME_FORBIDREPRINT="forbidreprint";

	/**
	 * 禁止增细单
	 */
	public static String APNAME_FORBIDNEWDTL="forbidnewdtl";
	/**
	 * 禁止删细单
	 */
	public static String APNAME_FORBIDDELETEDTL="forbiddeletedtl";
	/**
	 * 禁止修改细单
	 */
	public static String APNAME_FORBIDMODIFYDTL="forbidmodifydtl";
	
	
	public static String APNAME_MODIFYSELFONLY="modifyselfonly";

	public static String APNAME_WHERES="wheres";
	
	/**
	 * 自动打印方案
	 */
	public static String APNAME_AUTOPRINTPLAN="autoprintplan";
	
	String aptype="";
	String apname="";
	String apvalue="";
	public Apinfo(String apname, String aptype) {
		super();
		this.apname = apname;
		this.aptype = aptype;
	}
	public Apinfo(String apname, String aptype,String apvalue) {
		super();
		this.apname = apname;
		this.aptype = aptype;
		this.apvalue = apvalue;
	}
	
	public String getAptype() {
		return aptype;
	}
	public String getApname() {
		return apname;
	}
	public String getApvalue() {
		return apvalue;
	}
	public void setApvalue(String apvalue) {
		this.apvalue = apvalue;
	}


}
