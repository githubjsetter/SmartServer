package com.inca.npserver.prod;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

/**
 *  ⁄»®–≈œ¢
 * @author Administrator
 *
 */
public class Licenseinfo {
	private String copyright;
	private String authunit;
	private String prodname;
	private Vector<String> modules=new Vector<String>();
	private Calendar startdate;
	private Calendar enddate;
	private int maxclientuser;
	private String serverip;
	private String digitsign;
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public String getAuthunit() {
		return authunit;
	}
	public void setAuthunit(String authunit) {
		this.authunit = authunit;
	}
	public Vector<String> getModules() {
		return modules;
	}

	public String getModulestr() {
		String s="";
		Enumeration<String> en=modules.elements();
		for(int i=0;en.hasMoreElements();i++){
			if(i>0)s+=",";
			s += en.nextElement();
		}
		return s;
	}

	public void setModules(Vector<String> modules) {
		this.modules = modules;
	}
	public Calendar getStartdate() {
		return startdate;
	}
	public void setStartdate(Calendar startdate) {
		this.startdate = startdate;
	}
	public Calendar getEnddate() {
		return enddate;
	}
	public void setEnddate(Calendar enddate) {
		this.enddate = enddate;
	}
	public int getMaxclientuser() {
		return maxclientuser;
	}
	public void setMaxclientuser(int maxclientuser) {
		this.maxclientuser = maxclientuser;
	}
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getProdname() {
		return prodname;
	}
	public void setProdname(String prodname) {
		this.prodname = prodname;
	}
	public String getDigitsign() {
		return digitsign;
	}
	public void setDigitsign(String digitsign) {
		this.digitsign = digitsign;
	}
	
	
}
