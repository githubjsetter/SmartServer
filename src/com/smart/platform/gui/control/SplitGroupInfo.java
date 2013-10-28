package com.smart.platform.gui.control;

import java.util.Enumeration;
import java.util.Vector;

/**
 * 分组方法
 * @author Administrator
 *
 */
public class SplitGroupInfo {
	/**
	 * 对数据列求和
	 */
	public static String DATACOLUMN_SUM="sum";
	/**
	 * 对数据列求平均
	 */
	public static String DATACOLUMN_AVG="avg";

	public static String DATACOLUMN_MAX="max";

	public static String DATACOLUMN_MIN="min";

	public static String DATACOLUMN_COUNT="count";

	/**
	 * 分组列名
	 */
	Vector<String> groupcolnames=new Vector<String>();
	Vector<Datacolumn> datacolumns=new Vector<Datacolumn>();
	
	/**
	 * 组标题
	 */
	String title="";
	
	int level=0;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 定义分组列
	 * @param colname
	 */
	public void addGroupColumn(String colname){
		groupcolnames.add(colname);
	}
	
	public String getGroupcolumn(){
		return groupcolnames.elementAt(0);
	}
	
	/**
	 * 定义数据列
	 * @param colname
	 * @param method  数据列方法 DATACOLUMN_SUM | DATACOLUMN_AVG
	 */
	public void addDataColumn(String colname,String method){
		datacolumns.add(new Datacolumn(colname,method));
	}
	
	/**
	 * 数据列定义
	 * @author Administrator
	 *
	 */
	public class Datacolumn{
		String colname;
		/**
		 * 数据列处理方法,取值 DATACOLUMN_SUM|DATACOLUMN_AVG
		 */
		String method;
		public Datacolumn(String colname, String method) {
			super();
			this.colname = colname;
			this.method = method;
		}
		public String getColname() {
			return colname;
		}
		public String getMethod() {
			return method;
		} 
		
	}
	
	public Enumeration<String> getGroupGroupcolumns(){
		return groupcolnames.elements();
	}
	
	public Enumeration<Datacolumn> getDatacolumn(){
		return datacolumns.elements();
	}

	public Enumeration<String> getDatacolumnname(){
		Vector<String> cnames=new Vector<String>();
		Enumeration<Datacolumn> en=getDatacolumn();
		while(en.hasMoreElements()){
			Datacolumn dc=en.nextElement();
			cnames.add(dc.colname);
		}
		return cnames.elements();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
