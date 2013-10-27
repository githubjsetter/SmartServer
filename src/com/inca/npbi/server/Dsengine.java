package com.inca.npbi.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

import org.apache.log4j.Category;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.UpdateHelper;
import com.inca.npserver.dbcp.DBConnectPool;
import com.inca.npserver.dbcp.DBConnectPoolFactory;

/**
 * 数据源引擎
 * 
 * @author user
 * 
 */
public class Dsengine {
	private static Dsengine inst = null;
	Category logger = Category.getInstance(Dsengine.class);

	public static Dsengine getInstance() {
		if (inst == null) {
			inst = new Dsengine();
		}
		return inst;
	}

	private Dsengine() {
		ScaninstanceThread t = new ScaninstanceThread();
		t.setDaemon(true);
		t.start();
	}

	public void runReport(Connection con, BIReportinfo report,
			Timeparaminfo timeparam) throws Exception {
		report.fixBasetable(con);
		report.clearBasetable(con, timeparam.getNpbi_instanceid());

		// 对每个视图进行运算
		Enumeration<Dsview> en = report.getViews();
		while (en.hasMoreElements()) {
			Dsview view = en.nextElement();
			view.runReport(con, report, timeparam, false);
		}

		// 执行后处理
		postTreate(con, report, timeparam);

		// 执行计算列处理
		postCalccolumn(con, report, timeparam);
	}

	void postCalccolumn(Connection con, BIReportinfo report,
			Timeparaminfo timeparam) throws Exception {
		String calccolumns = report.getCalccolumns();
		if (calccolumns == null || calccolumns.length() == 0)
			return;
		logger.info("计算计算列");
		String sql = "select rowid from "
				+ report.getBasetableinfo().getTablename()
				+ " where npbi_instanceid=?";
		logger.debug(sql);
		PreparedStatement c1 = null;
		PreparedStatement cupdate = null;
		try {
			String updatesql = "update "
					+ report.getBasetableinfo().getTablename() + " set "
					+ calccolumns + " where rowid=?";
			cupdate = con.prepareStatement(updatesql);
			logger.debug(updatesql);
			c1 = con.prepareStatement(sql);
			c1.setString(1, timeparam.getNpbi_instanceid());
			int lineno = 1;
			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				String rowid = rs.getString(1);
				cupdate.setString(1, rowid);
				try{
					cupdate.executeUpdate();
				}catch(Exception e){
					//可能被0除,先不管了
					logger.error("sql="+sql+",rowid="+rowid,e);
				}
				if (lineno % 1000 == 0) {
					con.commit();
				}
				lineno++;
			}
			con.commit();

		} catch (Exception e) {
			logger.error("Error", e);
			con.rollback();
		} finally {
			if (c1 != null)
				c1.close();
			if (cupdate != null)
				cupdate.close();
		}

	}

	/**
	 * 执行后处理
	 * 
	 * @param con
	 * @param report
	 */
	void postTreate(Connection con, BIReportinfo report, Timeparaminfo timeparam)
			throws Exception {
		String posttreate = report.getPosttreate();
		if (posttreate == null || posttreate.length() == 0)
			return;
		// 按行分解.
		String lines[] = posttreate.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.length() == 0)
				continue;
			doPosttreate(con, report, timeparam, line);
		}
	}

	void doPosttreate(Connection con, BIReportinfo report,
			Timeparaminfo timeparam, String treateline) throws Exception {
		String ss[] = treateline.split(":");
		if (ss.length == 0)
			return;
		String command = ss[0];
		if (command.equalsIgnoreCase("sort")) {
			doSort(con, report, timeparam, treateline);
		} else if (command.equalsIgnoreCase("top")) {
			doTop(con, report, timeparam, treateline);
		}
	}

	/**
	 * 按npbi_lineno排序的结果,取前top个.
	 * 
	 * @param con
	 * @param report
	 * @param timeparam
	 * @param treateline
	 * @throws Exception
	 */
	void doTop(Connection con, BIReportinfo report, Timeparaminfo timeparam,
			String treateline) throws Exception {
		logger.info("开始TOP");
		int p = treateline.indexOf(":");
		String tops = treateline.substring(p + 1);
		if (tops.length() == 0)
			return;
		int topct = 0;
		try {
			topct = Integer.parseInt(tops);
		} catch (Exception e) {
			return;
		}
		PreparedStatement c1 = null;
		PreparedStatement cdel = null;
		try {
			cdel = con.prepareStatement("delete "
					+ report.getBasetableinfo().getTablename()
					+ "  where rowid=?");
			String sql = "select rowid from "
					+ report.getBasetableinfo().getTablename()
					+ " where npbi_instanceid=?" + " order by npbi_lineno asc";
			logger.debug(sql);
			c1 = con.prepareStatement(sql);
			c1.setString(1, timeparam.getNpbi_instanceid());
			ResultSet rs = c1.executeQuery();
			int ct = 0;
			while (rs.next()) {
				String rowid = rs.getString(1);
				ct++;
				if (ct <= topct) {
					continue;
				}
				cdel.setString(1, rowid);
				cdel.executeUpdate();
				if (ct % 1000 == 0) {
					con.commit();
				}
			}
			con.commit();
		} finally {
			if (c1 != null)
				c1.close();
		}
	}

	/**
	 * 如果是排序,treateline=sort:排序列
	 * 
	 * @param con
	 * @param report
	 * @param timeparam
	 * @param treateline
	 * @throws Exception
	 */
	void doSort(Connection con, BIReportinfo report, Timeparaminfo timeparam,
			String treateline) throws Exception {
		logger.info("开始排序");
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
		PreparedStatement cupdate = null;
		try {
			cupdate = con.prepareStatement("update "
					+ report.getBasetableinfo().getTablename()
					+ " set npbi_lineno=? where rowid=?");
			c1 = con.prepareStatement(sql);
			c1.setString(1, timeparam.getNpbi_instanceid());
			int lineno = 1;
			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				String rowid = rs.getString(1);
				cupdate.setInt(1, lineno);
				cupdate.setString(2, rowid);
				cupdate.executeUpdate();
				if (lineno % 1000 == 0) {
					con.commit();
				}
				lineno++;
			}
			con.commit();

		} catch (Exception e) {
			logger.error("Error", e);
			con.rollback();
		} finally {
			if (c1 != null)
				c1.close();
			if (cupdate != null)
				cupdate.close();
		}
	}

	public void runReport(Connection con, String instanceid) throws Exception {
		logger.info("开始计算报表instanceid=" + instanceid);
		// 查询report定义
		String sql = "select * from npbi_instance where instanceid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(instanceid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		if (dm.getRowCount() == 0)
			throw new Exception("没有找到instanceid=" + instanceid);
		UpdateHelper uh = new UpdateHelper(
				"update npbi_instance set lastcalctime=sysdate,usestatus=1 where instanceid=?");
		uh.bindParam(instanceid);
		uh.executeUpdate(con);
		con.commit();
		String reportid = dm.getItemValue(0, "reportid");
		String npbi_instanceid = dm.getItemValue(0, "npbi_instanceid");
		String year = dm.getItemValue(0, "year");
		String month = dm.getItemValue(0, "month");
		String day = dm.getItemValue(0, "day");
		String year1 = dm.getItemValue(0, "year1");
		String month1 = dm.getItemValue(0, "month1");
		String day1 = dm.getItemValue(0, "day1");

		BIReportinfo report = new BIReportinfo();
		report.setReportid(reportid);
		report.loadfromDB(con);

		// 生成时间维度参数timeparam
		Timeparaminfo tparam = new Timeparaminfo();
		tparam.setNpbi_instanceid(npbi_instanceid);
		tparam.setTimetype(report.getReporttimetype());
		tparam.setYear(year);
		tparam.setMonth(month);
		tparam.setDay(day);
		tparam.setYear1(year1);
		tparam.setMonth1(month1);
		tparam.setDay1(day1);

		runReport(con, report, tparam);
		// 如果现在时间超过结束日期,就完成
		sh = new SelectHelper(
				"select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') nowdate from dual");
		dm = sh.executeSelect(con, 0, 1);
		String sysdate = dm.getItemValue(0, "nowdate");
		String usestatus = sysdate.compareTo(tparam.getEnddate()) >= 0 ? "3"
				: "2";
		uh = new UpdateHelper(
				"update npbi_instance set lastcalctime=sysdate,usestatus=? where instanceid=?");
		uh.bindParam(usestatus);
		uh.bindParam(instanceid);
		uh.executeUpdate(con);
		con.commit();
		logger.info("计算完成,报表instanceid=" + instanceid);

	}

	public Connection getConnection() throws Exception {
		DBConnectPool pool = DBConnectPoolFactory.getInstance()
				.getDefaultpool();
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = pool.getUrl();
		Connection con = DriverManager.getConnection(url, pool.getUsername(),
				pool.getPassword());
		con.setAutoCommit(false);
		return con;

	}
}
