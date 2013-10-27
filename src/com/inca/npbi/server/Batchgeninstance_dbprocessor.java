package com.inca.npbi.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.InsertHelper;
import com.inca.np.util.SelectHelper;

/**
 * 批量生成按年生成instance
 * 
 * @author user
 * 
 */
public class Batchgeninstance_dbprocessor extends RequestProcessorAdapter {
	static String COMMAND = "npbi:批量生成instance";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		
		Connection con = null;
		String sql = "select reportid,timetype from npbi_report_def where usestatus=1";
		PreparedStatement c1 = null;
		try {
			con = getConnection();
			Calendar now = Calendar.getInstance();
			int year = now.get(Calendar.YEAR);
			try {
				year=Integer.parseInt(pcmd.getValue("year"));
			} catch (Exception e) {
				// TODO: handle exception
			}
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				String reportid = rs.getString("reportid");
				String timetype = rs.getString("timetype");
				logger.debug("create instance,reportid="+reportid+",timetype="+timetype);
				createInstance(con, reportid, timetype, year);
			}

			resp.addCommand(new StringCommand("+OK"));
		} catch (Exception e) {
			logger.error("Error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if(c1!=null)c1.close();
			if (con != null) {
				con.close();
			}
		}
		return 0;
	}

	void createInstance(Connection con, String reportid, String timetype,
			int year) throws Exception {
		if (timetype.equals("year")) {
			newInstanceYear(con, reportid, year);
		} else if (timetype.equals("month")) {
			newInstanceMonth(con, reportid, year);
		} else {
			newInstanceDay(con, reportid, year);
		}

	}

	private void newInstanceYear(Connection con, String reportid, int year)
			throws Exception {
		Timeparaminfo tp = new Timeparaminfo();
		tp.setYear(String.valueOf(year));
		tp.setTimetype(BIReportinfo.reporttimetype_year);
		tp.genNpbi_instanceid();
		if(isExists(con,reportid,tp.getNpbi_instanceid())){
			return;
		}

		InsertHelper ih = new InsertHelper("npbi_instance");
		ih.bindSequence("instanceid", "npbi_instance_seq");
		ih.bindParam("reportid", reportid);
		ih.bindParam("npbi_instanceid", tp.getNpbi_instanceid());
		ih.bindParam("year", tp.getYear());
		ih.bindParam("usestatus","0");
		try {
			ih.executeInsert(con);
		} catch (Exception e) {
			con.rollback();
			if(e.getMessage().indexOf("ORA-00001")<0)throw e;
		}
		con.commit();
	}

	private void newInstanceMonth(Connection con, String reportid, int year)
			throws Exception {
		for(int m=1;m<=12;m++){
			Timeparaminfo tp = new Timeparaminfo();
			tp.setYear(String.valueOf(year));
			tp.setMonth(String.valueOf(m));
			tp.setTimetype(BIReportinfo.reporttimetype_month);
			tp.genNpbi_instanceid();
			if(isExists(con,reportid,tp.getNpbi_instanceid())){
				continue;
			}

			InsertHelper ih = new InsertHelper("npbi_instance");
			ih.bindSequence("instanceid", "npbi_instance_seq");
			ih.bindParam("reportid", reportid);
			ih.bindParam("npbi_instanceid", tp.getNpbi_instanceid());
			ih.bindParam("year", tp.getYear());
			ih.bindParam("month", tp.getMonth());
			ih.bindParam("usestatus","0");
			try {
				ih.executeInsert(con);
			} catch (Exception e) {
				con.rollback();
				if(e.getMessage().indexOf("ORA-00001")<0)throw e;
			}
			con.commit();
		}

	}

	private void newInstanceDay(Connection con, String reportid, int year)
			throws Exception {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, year);
		now.set(Calendar.MONTH,0);
		now.set(Calendar.DAY_OF_YEAR,1);
		while(now.get(Calendar.YEAR)==year){
			int im=now.get(Calendar.MONTH)+1;
			int id=now.get(Calendar.DAY_OF_MONTH);

			Timeparaminfo tp = new Timeparaminfo();
			tp.setYear(String.valueOf(year));
			tp.setMonth(String.valueOf(im));
			tp.setDay(String.valueOf(id));
			tp.setTimetype(BIReportinfo.reporttimetype_day);
			tp.genNpbi_instanceid();
			//logger.debug("is exists instance "+tp.getNpbi_instanceid());
			if(isExists(con,reportid,tp.getNpbi_instanceid())){
				now.add(Calendar.DAY_OF_YEAR, 1);
				continue;
			}

			InsertHelper ih = new InsertHelper("npbi_instance");
			ih.bindSequence("instanceid", "npbi_instance_seq");
			ih.bindParam("reportid", reportid);
			ih.bindParam("npbi_instanceid", tp.getNpbi_instanceid());
			ih.bindParam("year", tp.getYear());
			ih.bindParam("month", tp.getMonth());
			ih.bindParam("day", tp.getDay());
			ih.bindParam("usestatus","0");
			try {
				ih.executeInsert(con);
			} catch (Exception e) {
				con.rollback();
				if(e.getMessage().indexOf("ORA-00001")<0)throw e;
			}
			now.add(Calendar.DAY_OF_YEAR, 1);
			con.commit();

		}

	}
	
	boolean isExists(Connection con,String reportid,String npbi_instanceid){
		SelectHelper sh=new SelectHelper("select instanceid from npbi_instance where" +
				" reportid=? and npbi_instanceid=?");
		sh.bindParam(reportid);
		sh.bindParam(npbi_instanceid);
		try {
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			return dm.getRowCount()>0;
		} catch (Exception e) {
			logger.error("error",e);
			return false;
		}
	}
}
