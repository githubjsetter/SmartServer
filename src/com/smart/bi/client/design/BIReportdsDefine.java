package com.smart.bi.client.design;

import java.util.Enumeration;
import java.util.Vector;

import com.smart.bi.client.design.param.BIReportparamdefine;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DBHelper;

/**
 * 显示数据源定义
 * 
 * @author user
 * 
 */
public class BIReportdsDefine {
	//public String sql = "select 	credate,customname,total from bms_sa_doc order by customname";
	public String sql = "";
	public DBTableModel datadm;
	public Vector<BIReportparamdefine> params = new Vector<BIReportparamdefine>();

	/**
	 * 返回测试用数据
	 * @return
	 */
	public String getTestsql(){
		String fullsql=getSqlwithautocond();
		for(int i=0;i<params.size();i++){
			BIReportparamdefine p=params.elementAt(i);
			if(p.paramtype.equals("number")){
				fullsql=replaceParam(fullsql,p.paramname,"0");
			}else if(p.paramtype.equals("varchar")){
				fullsql=replaceParam(fullsql,p.paramname,"''");
			}else if(p.paramtype.equals("date")){
				fullsql=replaceParam(fullsql,p.paramname,"2000-01-01");
			}
		}
		fullsql=replaceParam(fullsql,"人员ID","0");
		fullsql=replaceParam(fullsql,"部门ID","0");
		fullsql=replaceParam(fullsql,"核算单元ID","0");
		fullsql=replaceParam(fullsql,"角色ID","0");
		return fullsql;
	}
	
	protected String getSqlwithautocond(){
		String s=sql;
		String autocond=getAutocond();
		if(autocond.length()>0){
			s=DBHelper.addWheres(s, autocond);
		}
		return s;
	}
	
	/**
	 * 加入自动条件
	 * @return
	 */
	protected String getAutocond(){
		StringBuffer sb=new StringBuffer();
		Enumeration<BIReportparamdefine> en=params.elements();
		while(en.hasMoreElements()){
			BIReportparamdefine pinfo=en.nextElement();
			if(pinfo.getOrgInputvalue().length()>0 && pinfo.autocond.length()>0){
				if(sb.length()>0){
					sb.append(" and ");
				}
				sb.append(pinfo.autocond);
			}
		}
		return sb.toString();
	}
	
	public void reset(){
		sql="";
		datadm.clearAll();
		datadm.getDisplaycolumninfos().clear();
		params=new Vector<BIReportparamdefine>();
	}

	/**
	 * 带参数的sql
	 */
	public String getFullsql(){
		String fullsql=getSqlwithautocond();
		for(int i=0;i<params.size();i++){
			BIReportparamdefine p=params.elementAt(i);
			String inputvalue=p.getInputvalue();
			if(inputvalue.length()==0){
				if(p.paramtype.equals("number")){
					inputvalue="to_number(null)";
				}else if(p.paramtype.equals("varchar") ){
					inputvalue="to_char(null)";
				}else if(p.paramtype.equals("datetime") ){
					inputvalue="to_date(null)";
				}
			}else{
				if(p.paramtype.equals("number")){
				}else if(p.paramtype.equals("varchar") ){
					inputvalue="'"+inputvalue+"'";
				}else if(p.paramtype.equals("datetime") ){
					inputvalue="'"+inputvalue+"'";
				}
			}
			fullsql=replaceParam(fullsql,p.paramname,inputvalue);
		}

		Userruninfo u=ClientUserManager.getCurrentUser();
		fullsql=replaceParam(fullsql,"人员ID",u.getUserid());
		fullsql=replaceParam(fullsql,"部门ID",u.getDeptid());
		fullsql=replaceParam(fullsql,"核算单元ID",u.getEntryid());
		fullsql=replaceParam(fullsql,"角色ID",u.getRoleid());

		return fullsql;
	}
	
	public static String replaceParam(String sql,String param,String target){
		StringBuffer sb=new StringBuffer();
		int p=0;
		for(;;){
			p=sql.toLowerCase().indexOf("{"+param.toLowerCase()+"}",p);
			if(p<0)break;
			int p1=sql.indexOf("}",p);
			if(p1<0)break;
			sb.append(sql.subSequence(0, p));
			sb.append(target);
			sql=sql.substring(p1+1);
			p=0;
		}
		sb.append(sql);
		return sb.toString();
	}
	
	public String getSql(){
		return sql;
	}
	
	public static void main(String[] args) {
		String sql="and ('{goodsid}'='' or goodsid={goodsid})";
		sql=replaceParam(sql,"goodsid","0");
		System.out.println(sql);
	}

}
