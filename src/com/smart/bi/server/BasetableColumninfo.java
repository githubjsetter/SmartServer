package com.smart.bi.server;

/**
 * 基表列信息
 * @author user
 *
 */
public class BasetableColumninfo {
	String colname;
	String coltype;
	int precision;
	int scale;
	public String getColname() {
		return colname;
	}
	public void setColname(String colname) {
		this.colname = colname;
	}
	public String getColtype() {
		return coltype;
	}
	public void setColtype(String coltype) {
		this.coltype = coltype;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	public int getScale() {
		return scale;
	}
	public void setScale(int scale) {
		this.scale = scale;
	}
	
}
