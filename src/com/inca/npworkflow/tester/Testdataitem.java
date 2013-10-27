package com.inca.npworkflow.tester;

import java.sql.Connection;

import com.inca.np.server.ServerContext;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SteGeneralTool;
import com.inca.npworkflow.common.WfDataitem;
import com.inca.npworkflow.common.Wfdefine;
import com.inca.npworkflow.server.WfDataitemManager;
import com.inca.npworkflow.server.WfEngine;


/**
 * ��������Դ
 * @author user
 *
 */
public class Testdataitem {
	/**
	 * ���Լ���������
	 * @param con
	 * @throws Exception
	 */
	public void test1(Connection con) throws Exception{
		WfDataitem.loadFromDB(con, "1");
		Wfdefine.loadFromDB(con, "1");
	}
	
	/**
	 * ���Ա��ʽ
	 * @param con
	 * @throws Exception
	 */
	public void test2(Connection con)throws Exception{
		WfDataitemManager datamgr=WfDataitemManager.loadFromDB(con, "1");
		Wfdefine wfdefine = Wfdefine.loadFromDB(con, "1");
		boolean ret=datamgr.calcWfCond(con, wfdefine, "1");
		System.out.println(ret);
		
	}
	
	
	
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		SteGeneralTool stet=new SteGeneralTool();
		stet.pack();
		stet.setVisible(true);
		if(true){
			return;
		}

		Testdataitem t=new Testdataitem();
		try {
			ServerContext.regServercontext(new ServerContext());
			Connection con=WfEngine.getConnection();

			t.test2(con);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
