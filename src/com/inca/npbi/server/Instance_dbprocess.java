package com.inca.npbi.server;
import java.sql.Connection;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.util.SelectHelper;
import com.inca.npbi.client.instance.Instance_ste;
/*功能"报表实例管理"应用服务器处理*/
public class Instance_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Instance_ste(null);
	}
	
	
	
	@Override
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
		//对于新增的,生成instanceid
		int dbstatus=dbmodel.getdbStatus(row);
		if(dbstatus==RecordTrunk.DBSTATUS_NEW ||dbstatus==RecordTrunk.DBSTATUS_MODIFIED){
			String reportid=dbmodel.getItemValue(row, "reportid");
			SelectHelper sh=new SelectHelper("select timetype from npbi_report_def where reportid=?");
			sh.bindParam(reportid);
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			
			Timeparaminfo tp=new Timeparaminfo();
			tp.setTimetype(dm.getItemValue(0, "timetype"));
			tp.setYear(dbmodel.getItemValue(row, "year"));
			tp.setMonth(dbmodel.getItemValue(row, "month"));
			tp.setDay(dbmodel.getItemValue(row, "day"));
			tp.setYear1(dbmodel.getItemValue(row, "year1"));
			tp.setMonth1(dbmodel.getItemValue(row, "month1"));
			tp.setDay1(dbmodel.getItemValue(row, "day1"));
			tp.genNpbi_instanceid();
			String instanceid=tp.getNpbi_instanceid();
			
			dbmodel.setItemValue(row, "npbi_instanceid", instanceid);
			dbmodel.setItemValue(row, "year", fillZero(dbmodel.getItemValue(row, "year"),4));
			dbmodel.setItemValue(row, "month", fillZero(dbmodel.getItemValue(row, "month"),2));
			dbmodel.setItemValue(row, "day", fillZero(dbmodel.getItemValue(row, "day"),2));
			dbmodel.setItemValue(row, "year1", fillZero(dbmodel.getItemValue(row, "year1"),4));
			dbmodel.setItemValue(row, "month1", fillZero(dbmodel.getItemValue(row, "month1"),2));
			dbmodel.setItemValue(row, "day1", fillZero(dbmodel.getItemValue(row, "day1"),2));
		}
		super.on_beforesave(con, userrininfo, dbmodel, row);
	}

	String fillZero(String s,int len){
		if(s.length()==0)return s;
		while(s.length()<len){
			s="0"+s;
		}
		return s;
	}

	protected String getTablename() {
		return "npbi_instance";
	}
}
