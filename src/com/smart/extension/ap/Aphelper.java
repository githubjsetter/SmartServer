package com.smart.extension.ap;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import com.smart.extension.ste.Apinfo;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SendHelper;
import com.smart.sysmgr.hov.HovapinfoModel;

public class Aphelper {
	/**
	 * 将apwheres中的<当前部门ID>等改为立即数
	 * @param wheres
	 * @return
	 */
	public static String filterApwheres(String wheres){
		Userruninfo u=ClientUserManager.getCurrentUser();
		wheres=wheres.replaceAll("<当前部门ID>",u.getDeptid());
		wheres=wheres.replaceAll("<当前人员ID>",u.getUserid());
		wheres=wheres.replaceAll("<当前角色ID>",u.getRoleid());
		return wheres;
	}

	public static String filterApwheres(String wheres,Userruninfo u){
		wheres=wheres.replaceAll("<当前部门ID>",u.getDeptid());
		wheres=wheres.replaceAll("<当前人员ID>",u.getUserid());
		wheres=wheres.replaceAll("<当前角色ID>",u.getRoleid());
		return wheres;
	}

	public static HashMap<String,Apinfo> downloadHovAp(String hovclassname,String roleid,StringBuffer hovidsb) throws Exception{
		ClientRequest req=new ClientRequest("np:查询HOV授权属性");
		ParamCommand paramcmd = new ParamCommand();
		req.addCommand(paramcmd);
		paramcmd.addParam("hovclassname",hovclassname);
		paramcmd.addParam("roleid",roleid);
		
		ServerResponse resp=SendHelper.sendRequest(req);
		StringCommand cmd0=(StringCommand) resp.commandAt(0);
		if(!cmd0.getString().startsWith("+OK")){
			throw new Exception(cmd0.getString());
		}
		ParamCommand paramcommand=(ParamCommand) resp.commandAt(1);
		String hovid=paramcommand.getValue("hovid");
		hovidsb.append(hovid);
		
		DataCommand datacmd=(DataCommand) resp.commandAt(2);
		DBTableModel apmodel=datacmd.getDbmodel();
		
		HashMap<String,Apinfo> infomap=new HashMap<String,Apinfo>();
		for (int r = 0; r < apmodel.getRowCount(); r++) {
			String roleopid = apmodel.getItemValue(r, "roleopid");
			String apid = apmodel.getItemValue(r, "apid");
			String apname = apmodel.getItemValue(r, "apname");
			String aptype = apmodel.getItemValue(r, "aptype");
			String apvalue = apmodel.getItemValue(r, "apvalue");
			Apinfo apinfo = new Apinfo(apname, aptype);
			apinfo.setApvalue(apvalue);
			infomap.put(apname,apinfo);
		}
		return infomap;
	}
	
	public static void saveHovAp(String roleid,String hovid,Vector<Apinfo> apinfos)throws Exception{
		HovapinfoModel apmodel=new HovapinfoModel();
		Enumeration<Apinfo> en = apinfos.elements();
		while (en.hasMoreElements()){
			Apinfo apinfo = en.nextElement();
			apmodel.appendRow();
			int r = apmodel.getRowCount() - 1;
			apmodel.setItemValue(r, "roleid", roleid);
			apmodel.setItemValue(r, "hovid", hovid);
			apmodel.setItemValue(r, "apname", apinfo.getApname());
			apmodel.setItemValue(r, "apname", apinfo.getApname());
			apmodel.setItemValue(r, "aptype", apinfo.getAptype());
			apmodel.setItemValue(r, "apvalue", apinfo.getApvalue());
		}
		
		ClientRequest req=new ClientRequest("np:保存HOV授权属性");
		ParamCommand paramcmd = new ParamCommand();
		req.addCommand(paramcmd);
		paramcmd.addParam("hovid",hovid);
		paramcmd.addParam("roleid",roleid);
		DataCommand datacmd=new DataCommand();
		datacmd.setDbmodel(apmodel);
		req.addCommand(datacmd);
		
		ServerResponse resp=SendHelper.sendRequest(req);
		StringCommand cmd0=(StringCommand) resp.commandAt(0);
		if(!cmd0.getString().startsWith("+OK")){
			throw new Exception(cmd0.getString());
		}
	}
	
	
	
	public static void main(String[] argv){
		String s="deptid=<当前角色ID>";
		s=Aphelper.filterApwheres(s);
		System.out.println(s);
	}
}
