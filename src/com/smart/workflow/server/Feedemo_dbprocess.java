package com.smart.workflow.server;

import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.platform.util.SelectHelper;
import com.smart.workflow.demo.Feedemo_ste;

public class Feedemo_dbprocess  extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Feedemo_ste(null);
	}
	protected String getTablename() {
		return "np_wf_demo_fee";
	}
	@Override
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
		super.on_beforesave(con, userrininfo, dbmodel, row);
		if(dbmodel.getdbStatus(row)!=RecordTrunk.DBSTATUS_NEW){
			//检查审批过了？
			String feedocid=dbmodel.getItemValue(row, "feedocid");
			String sql="select usestatus,approvestatus from np_wf_demo_fee where feedocid=? for update ";
			SelectHelper sh=new SelectHelper(sql);
			sh.bindParam(feedocid);
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			if(dm.getRowCount()>0){
				String usestatus=dm.getItemValue(0, "usestatus");
				String approvestatus=dm.getItemValue(0, "approvestatus");
				if(usestatus.compareTo("1")>0 || approvestatus.compareTo("1")>0){
					throw new Exception("已提交进入审批，不能修改");
				}
			}
			
		}
	}
	
	
}
