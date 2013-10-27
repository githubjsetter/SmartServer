package com.inca.npx.ste;

/**
 * һ����Ȩ������Ϣ
 * @author Administrator
 *
 */
public class Apinfo {
	public static final String APTYPE_PARAM="param";
	public static final String APTYPE_FORBID="forbid";
	public static final String APTYPE_DATA="data";
	/**
	 * ��Ȩ������,��ֹ����
	 */
	public static String APNAME_FORBIDNEW="forbidnew";
	/**
	 * ��Ȩ������,��ֹɾ��
	 */
	public static String APNAME_FORBIDDELETE="forbiddelete";
	/**
	 * ��Ȩ������,��ֹ��ѯ
	 */
	public static String APNAME_FORBIDQUERY="forbidquery";
	/**
	 * ��Ȩ������,��ֹ�޸�
	 */
	public static String APNAME_FORBIDMODIFY="forbidmodify";
	/**
	 * ��Ȩ������,��ֹ����
	 */
	public static String APNAME_FORBIDSAVE="forbidsave";

	/**
	 * ��ֹ����
	 */
	public static String APNAME_FORBIDEXPORT="forbidexport";

	/**
	 * ��ֹ�ٴ�ӡ
	 */
	public static String APNAME_FORBIDREPRINT="forbidreprint";

	/**
	 * ��ֹ��ϸ��
	 */
	public static String APNAME_FORBIDNEWDTL="forbidnewdtl";
	/**
	 * ��ֹɾϸ��
	 */
	public static String APNAME_FORBIDDELETEDTL="forbiddeletedtl";
	/**
	 * ��ֹ�޸�ϸ��
	 */
	public static String APNAME_FORBIDMODIFYDTL="forbidmodifydtl";
	
	
	public static String APNAME_MODIFYSELFONLY="modifyselfonly";

	public static String APNAME_WHERES="wheres";
	
	/**
	 * �Զ���ӡ����
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
