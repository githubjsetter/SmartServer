package com.smart.platform.gui.control;

import java.util.Enumeration;
import java.util.Vector;

/**
 * ���鷽��
 * @author Administrator
 *
 */
public class SplitGroupInfo {
	/**
	 * �����������
	 */
	public static String DATACOLUMN_SUM="sum";
	/**
	 * ����������ƽ��
	 */
	public static String DATACOLUMN_AVG="avg";

	public static String DATACOLUMN_MAX="max";

	public static String DATACOLUMN_MIN="min";

	public static String DATACOLUMN_COUNT="count";

	/**
	 * ��������
	 */
	Vector<String> groupcolnames=new Vector<String>();
	Vector<Datacolumn> datacolumns=new Vector<Datacolumn>();
	
	/**
	 * �����
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
	 * ���������
	 * @param colname
	 */
	public void addGroupColumn(String colname){
		groupcolnames.add(colname);
	}
	
	public String getGroupcolumn(){
		return groupcolnames.elementAt(0);
	}
	
	/**
	 * ����������
	 * @param colname
	 * @param method  �����з��� DATACOLUMN_SUM | DATACOLUMN_AVG
	 */
	public void addDataColumn(String colname,String method){
		datacolumns.add(new Datacolumn(colname,method));
	}
	
	/**
	 * �����ж���
	 * @author Administrator
	 *
	 */
	public class Datacolumn{
		String colname;
		/**
		 * �����д�����,ȡֵ DATACOLUMN_SUM|DATACOLUMN_AVG
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
