package com.smart.workflow.tester;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.ServerContext;
import com.smart.platform.util.DefaultNPParam;
import com.smart.workflow.server.WfEngine;

public class Testenginst {
	void testnewInst() throws Exception{
		WfEngine eng=WfEngine.getInstance();
		eng.startWorkflow("BMS_SA_DOC","48");
	}
	
	void testFetchnodeinst()throws Exception{
		ServerContext.regServercontext(new ServerContext());
		Connection con=WfEngine.getConnection();
		
		WfEngine eng=WfEngine.getInstance();
		Userruninfo userinfo=new Userruninfo();
		userinfo.setUserid("0");
		userinfo.setRoleid("0");
		DBTableModel dm=eng.fetchNodeinstanceByemployee(con, userinfo);
		for(int i=0;i<dm.getRowCount();i++){
			System.out.print(dm.getItemValue(i, "startdate"));
			System.out.print("\t");
			System.out.print(dm.getItemValue(i, "summary"));
			System.out.print("\t");
			System.out.print(dm.getItemValue(i, "wfnodeinstanceid"));
			
		}
		
	}
	
	void testFetchnodedata()throws Exception{
		ServerContext.regServercontext(new ServerContext());
		Connection con=WfEngine.getConnection();
		
		WfEngine eng=WfEngine.getInstance();
		String wfnodeinstanceid="24";
		DBTableModel dm=eng.fetchNodeinstanceData(con, wfnodeinstanceid);
		
	}
	
	public static void main(String[] args) {
		Testenginst t=new Testenginst();
		try {
			
			DefaultNPParam.debug=1;
			DefaultNPParam.develop=1;
			DefaultNPParam.debugdbip = "192.9.200.47";
			DefaultNPParam.debugdbpasswd = "npserver";
			DefaultNPParam.debugdbsid = "orcl";
			DefaultNPParam.debugdbusrname = "npserver";
			DefaultNPParam.prodcontext = "npserver";

			//ServerContext.regServercontext(new ServerContext());
			//Connection con=WfEngine.getConnection();

			t.testnewInst();
			//t.testFetchnodeinst();
			//t.testFetchnodedata();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
