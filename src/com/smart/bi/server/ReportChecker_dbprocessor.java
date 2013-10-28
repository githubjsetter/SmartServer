package com.smart.bi.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Enumeration;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;

/**
 * 检查报表定义是否正确
 * 
 * @author user
 * 
 */
public class ReportChecker_dbprocessor extends RequestProcessorAdapter {

	static String COMMAND = "npbi.检查报表定义";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		String command = req.getCommand();
		if (!command.equals(COMMAND))
			return -1;
		DataCommand dcmd = (DataCommand) req.commandAt(1);
		DBTableModel dm = dcmd.getDbmodel();
		Connection con = null;
		try {
			con = getConnection();
			for (int row = 0; row < dm.getRowCount(); row++) {
				String reportid = dm.getItemValue(row, "reportid");
				StringBuffer sb = new StringBuffer();
				checkReport(con, reportid, sb);
				if (sb.length() == 0) {
					dm.setItemValue(row, "status", "");
					dm.setItemValue(row, "message", "");
				} else {
					dm.setItemValue(row, "status", "-ERROR");
					dm.setItemValue(row, "message", sb.toString());
				}
			}
			resp.addCommand(new StringCommand("+OK"));
			DataCommand respdcmd = new DataCommand();
			respdcmd.setDbmodel(dm);
			resp.addCommand(respdcmd);
		} catch (Exception e) {
			logger.error("error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				con.close();
			}
		}
		return 0;
	}

	/**
	 * 检查报表. 检查基表列定义.试建基表. 检查视图.测试每条sql
	 * 
	 * @param reportid
	 * @param sb
	 * @throws Exception
	 */
	void checkReport(Connection con, String reportid, StringBuffer sb)
			throws Exception {
		BIReportinfo report = new BIReportinfo();
		try {
			report.setReportid(reportid);
			report.loadfromDB(con);
			report.fixBasetable(con);
		} catch (Exception e) {
			logger.debug("error",e);
			sb.append("建基表错误:" + e.getMessage());
		}

		// 检查视图
		Timeparaminfo tparam = new Timeparaminfo();
		tparam.setTimetype(report.getReporttimetype());
		Calendar now = Calendar.getInstance();
		tparam.setYear(String.valueOf(now.get(Calendar.YEAR)));
		tparam.setMonth(String.valueOf(now.get(Calendar.MONTH) + 1));
		tparam.setDay(String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
		tparam.setYear1(tparam.getYear());
		tparam.setMonth1(tparam.getMonth());
		tparam.setDay1(tparam.getDay());

		Enumeration<Dsview> en = report.getViews();
		while (en.hasMoreElements()) {
			Dsview view = en.nextElement();
			// 测试报表
			try {
				view.runReport(con, report, tparam, true);
			} catch (Exception e) {
				sb.append("视图错误:" + view.getSql() + ":\n" + e.getMessage());
			}
		}

		// 检查后处理
		try {
			checkPosttreate(con, report, tparam);
		} catch (Exception e) {
			sb.append("后处理错误:" + e.getMessage());
		}

		// 检查计算列
		try {
			checkCalccolumn(con, report, tparam);
		} catch (Exception e) {
			sb.append("计算列定义错误:" + e.getMessage());
		}
		

	}

	void checkCalccolumn(Connection con, BIReportinfo report,
			Timeparaminfo timeparam) throws Exception {
		String calccolumns = report.getCalccolumns();
		if(calccolumns.length()==0)return;
		PreparedStatement cupdate = null;
		String updatesql = "update "
			+ report.getBasetableinfo().getTablename() + " set "
			+ calccolumns + " where npbi_instanceid='test'";
		try {
			cupdate = con.prepareStatement(updatesql);
			logger.debug(updatesql);
			cupdate.executeUpdate();
			con.rollback();
		}catch(Exception e){
			con.rollback();
			throw new Exception(calccolumns+"计算列定义错误:"+e.getMessage()+",sql="+updatesql);
		}finally{
			if(cupdate!=null)cupdate.close();
		}
	}
	void checkPosttreate(Connection con, BIReportinfo report,
			Timeparaminfo timeparam) throws Exception {
		String posttreate = report.getPosttreate();
		if (posttreate == null || posttreate.length() == 0)
			return;
		// 按行分解.
		String lines[] = posttreate.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.length() == 0)
				continue;
			doCheckPosttreate(con, report, timeparam, line);
		}
	}

	void doCheckPosttreate(Connection con, BIReportinfo report,
			Timeparaminfo timeparam, String treateline) throws Exception {
		String ss[] = treateline.split(":");
		if (ss.length == 0)
			return;
		String command = ss[0];
		if (command.equalsIgnoreCase("sort")) {
			doCheckSort(con, report, timeparam, treateline);
		} else if (command.equalsIgnoreCase("top")) {
			doCheckTop(con, report, timeparam, treateline);
		}
	}

	void doCheckTop(Connection con, BIReportinfo report,
			Timeparaminfo timeparam, String treateline) throws Exception {
		String helpmsg="top定义应为\"top:数量\",如top:100";
		int p=treateline.indexOf(":");
		if(p<0)throw new Exception(treateline+"错误."+helpmsg);
		int ct=0;
		try{
			ct=Integer.parseInt(treateline.substring(p+1));
		}catch(Exception e){
			throw new Exception(treateline+"错误."+helpmsg); 
		}
	}

	void doCheckSort(Connection con, BIReportinfo report,
			Timeparaminfo timeparam, String treateline) throws Exception {
		logger.info("开始检查排序");
		int p = treateline.indexOf(":");
		String sorts = treateline.substring(p + 1);
		if (sorts.length() == 0)
			return;
		String sql = "select rowid from "
				+ report.getBasetableinfo().getTablename()
				+ " where npbi_instanceid=?";
		sql += " order by " + sorts;
		logger.debug(sql);
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			c1.setString(1, timeparam.getNpbi_instanceid());
			ResultSet rs = c1.executeQuery();
			rs.next();
		} catch (Exception e) {
			throw new Exception(treateline+"排序定义错误:" + e.getMessage() + ".sql=" + sql);
		} finally {
			if (c1 != null)
				c1.close();
		}
	}

}
