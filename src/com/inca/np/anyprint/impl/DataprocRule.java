package com.inca.np.anyprint.impl;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.inca.np.gui.control.DBTableModel;

/**
 * 数据处理规则信息
 * @author Administrator
 *
 */
public class DataprocRule {
	public static final String RULETYPE_SORT="排序";
	public static final String RULETYPE_SPLITPAGE="分页";
	public static final String RULETYPE_FILLPRINTNO="填写打印单号";
	
	String ruletype="";
	String expr="";
	public DataprocRule(String ruletype) {
		super();
		this.ruletype = ruletype;
	}
	public String getRuletype() {
		return ruletype;
	}
	public void setRuletype(String ruletype) {
		this.ruletype = ruletype;
	}
	public String getExpr() {
		return expr;
	}
	public void setExpr(String expr) {
		this.expr = expr;
	}
	
	/**
	 * 处理排序 分组分页等
	 * @param dbmodel
	 */
	public void process(DBTableModel dbmodel) throws Exception{
		if(RULETYPE_SORT.equals(ruletype)){
			doSort(dbmodel);
		}else if(RULETYPE_SPLITPAGE.equals(ruletype)){
			//nothing;
		}else if(RULETYPE_FILLPRINTNO.equals(ruletype)){
			//nothing;
		}else{
			throw new Exception("unknow rule type "+ruletype);
		}
	}
	
	void doSort(DBTableModel dbmodel) throws Exception{
		dbmodel.sort(expr);
	}
	
	public void write(PrintWriter out){
		out.println("<dataprocrule>");
		out.println("<ruletype>"+ruletype+"<ruletype>");
		out.println("<expr>");
		out.println(expr);
		out.println("</expr>");
		out.println("</dataprocrule>");
	}
	
	public void read(BufferedReader rd)throws Exception{
		String line;
		while((line=rd.readLine())!=null){
			if(line.startsWith("</dataprocrule>")){
				break;
			}
			if(line.startsWith("<ruletype>")){
				setRuletype(getXmlvalue(line));
			}else if(line.startsWith("<expr>")){
				readExpr(rd);
			}
		}
	}
	
	void readExpr(BufferedReader rd)throws Exception{
		StringBuffer sb=new StringBuffer();
		String line;
		while((line=rd.readLine())!=null){
			if(line.startsWith("</expr>")){
				break;
			}
			sb.append(line+"\n");
		}
		setExpr(sb.toString().trim());
	}
	
	protected String getXmlvalue(String line) {
		int p=line.indexOf(">");
		int p1=line.indexOf("<",p);
		return line.substring(p+1,p1);
	}

}
