package com.inca.np.gui.ste;

import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.util.DBHelper;

public class QuerylinkInfo {
	public String querylinkname="";
	public String opid="";
	public Vector<Querycondinfo> conds=new Vector<Querycondinfo>();
	public String wheres="";
	
	public static class Querycondinfo{
		public String cname1="";
		public String op="";
		public String cname2="";
	}
	
	public String getExpr(){
		StringBuffer sb=new StringBuffer();
		sb.append("("+querylinkname+","+opid+")");
		sb.append("(");
		Enumeration<Querycondinfo> en=conds.elements();
		for(int i=0;en.hasMoreElements();i++){
			if(i>0)sb.append(":");
			Querycondinfo cd=en.nextElement();
			sb.append(cd.cname1+","+cd.op+","+cd.cname2);
		}
		sb.append(")");
		sb.append("(");
		sb.append(DBHelper.replaceEnter1(wheres));
		sb.append(")");
		return sb.toString();
	}
	
	/*
 * expr:= (级联名称,调用opid)(查询条件[:查询条件])(wheres)
 * 查询条件:查询条件列名,逻辑操作符,本表列名

	 */
	public static QuerylinkInfo create(String expr) throws Exception{
		QuerylinkInfo info=new QuerylinkInfo();
		
		//找第一个括号
		int p,p1;
		p=expr.indexOf("(");
		if(p<0)throw new Exception("表达式非法");
		
		p1=expr.indexOf(")",p);
		if(p1<0)throw new Exception("表达式非法");
		
		String s=expr.substring(p+1,p1);
		p=s.indexOf(",");
		if(p<0)throw new Exception("表达式非法");
		
		info.querylinkname=s.substring(0,p);
		info.opid=s.substring(p+1);
		
		p=expr.indexOf("(",p1);
		if(p<0)throw new Exception("表达式非法");
		
		p1=expr.indexOf(")",p);
		if(p1<0)throw new Exception("表达式非法");
		
		String conds[]=expr.substring(p+1,p1).split(":");
		for(int i=0;i<conds.length;i++){
			String ss[]=conds[i].split(",");
			Querycondinfo cond=new Querycondinfo();
			cond.cname1=ss[0];
			cond.op=ss[1];
			cond.cname2=ss[2];
			info.conds.add(cond);
		}
		
		p=expr.indexOf("(",p1);
		if(p>0){
			//说明有(wheres)
			p1=expr.lastIndexOf(")");
			String wheres=expr.substring(p+1,p1);
			info.wheres=DBHelper.replaceEnter2(wheres);
		}
		
		return info;
	}
}
