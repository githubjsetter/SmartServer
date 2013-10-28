package com.smart.bi.client.design.link;

/**
 * 链接
 * @author user
 *
 */
public class Linkinfo {
	/**
	 * 调用名称,用于菜单显示
	 */
	String linkname="";
	
	
	/**
	 * 调用的报表功能ID
	 */
	String callopid="";

	String callopname="";

	/**
	 * 调用参数
	 */
	String callcond="";

	public Linkinfo(){
		
	}

	public Linkinfo(String linkname, String callopid, String callopname,
			String callcond) {
		super();
		this.linkname = linkname;
		this.callopid = callopid;
		this.callopname = callopname;
		this.callcond = callcond;
	}

	public String getCallopname() {
		return callopname;
	}

	public void setCallopname(String callopname) {
		this.callopname = callopname;
	}

	public String getLinkname() {
		return linkname;
	}

	public void setLinkname(String linkname) {
		this.linkname = linkname;
	}

	public String getCallopid() {
		return callopid;
	}

	public void setCallopid(String callopid) {
		this.callopid = callopid;
	}

	public String getCallcond() {
		return callcond;
	}

	public void setCallcond(String callcond) {
		this.callcond = callcond;
	}
	
	
}
