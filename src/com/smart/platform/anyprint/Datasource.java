package com.smart.platform.anyprint;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * 打印数据源信息
 * @author Administrator
 *
 */
public class Datasource {
	String sql="";
	/**
	 * type类型。为主数据源，辅助一加一或辅助助串加
	 */
	String type="";
	String viewname="";
	public String getViewname() {
		return viewname;
	}
	public void setViewname(String viewname) {
		this.viewname = viewname;
	}
	public Datasource(String sql, String relatecolname) {
		super();
		this.sql = sql;
		this.type = relatecolname;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * 将{}替为''
	 * @param s
	 * @return
	 */
	public static String replaceParamtonull(String s){
		StringBuffer sb=new StringBuffer();
		int p=0;
		for(;;){
			p=s.indexOf("{",p);
			if(p<0)break;
			int p1=s.indexOf("}",p);
			if(p1<0)break;
			sb.append(s.subSequence(0, p));
			sb.append("''");
			s=s.substring(p1+1);
		}
		sb.append(s);
		return sb.toString();
	}
	
	public void write(PrintWriter out){
		out.println("<datasource>");
		out.println("<sql>");
		out.println(sql);
		out.println("</sql>");
		out.println(type);
		out.println(viewname);
		out.println("</datasource>");
	}
	
	public static Vector<Datasource> read(BufferedReader rd) throws Exception{
		Vector<Datasource> dses=new Vector<Datasource>();
		String line;
		while((line=rd.readLine())!=null){
			if(!line.startsWith("<datasource>"))break;
			Datasource ds=new Datasource("","");
			readSql(ds,rd);
			ds.setType(rd.readLine());
			ds.setViewname(rd.readLine());
			dses.add(ds);
			line = rd.readLine();//read </datasource>
		}
		return dses;
	}
	
	static void readSql(Datasource ds,BufferedReader rd)  throws Exception{
		String line=rd.readLine();
		//这时line里应该是<sql>
		StringBuffer sb=new StringBuffer();
		while((line=rd.readLine())!=null){
			if(line.startsWith("</sql>"))break;
			sb.append(line+"\n");
		}
		ds.setSql(sb.toString());
	}
	
}
