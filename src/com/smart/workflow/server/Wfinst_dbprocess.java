package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
import com.smart.platform.util.SelectHelper;
import com.smart.workflow.client.Wfinst_mde;

public class Wfinst_dbprocess  extends MdeProcessor{
	protected CMdeModel getMdeModel() {
		return new Wfinst_mde(null,"");
	}
	protected String getMastertablename() {
		return "np_wf_instance";
	}
	protected String getDetailtablename() {
		return "np_wf_node_instance";
	}
	@Override
	public void on_beforesavemaster(Connection con, Userruninfo userruninfo,
			DBTableModel dbmodel, int row) throws Exception {
		super.on_beforesavemaster(con, userruninfo, dbmodel, row);
		int dbstatus=dbmodel.getdbStatus(row);
		String wfinstanceid=dbmodel.getItemValue(row, "wfinstanceid");
		String sql="select wfstatus from np_wf_instance where wfinstanceid=? for update";
		SelectHelper sh=new SelectHelper(sql);
		sh.bindParam(wfinstanceid);
		DBTableModel dm=sh.executeSelect(con, 0, 1);
		if(dm.getRowCount()>0){
			String wfstatus=dm.getItemValue(0, "wfstatus");
			if(wfstatus.equalsIgnoreCase("close")){
				throw new Exception("流程已关闭了,不能修改");
			}
		}
	}
	
	
}

