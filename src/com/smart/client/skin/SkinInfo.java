package com.smart.client.skin;

import java.util.ArrayList;
import java.util.List;

public class SkinInfo {

	private List<ColInfo> colinfos = new ArrayList<ColInfo>();
	private String classname="";

	// ≈≈–Ú±Ì¥Ô Ω
	private String expr="";

	
	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getExpr() {
		return expr==null||expr.equals("")?"":expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public void setColinfos(List<ColInfo> colinfos) {
		this.colinfos = colinfos;
	}

	public List<ColInfo> getColinfos() {
		return colinfos;
	}



}
