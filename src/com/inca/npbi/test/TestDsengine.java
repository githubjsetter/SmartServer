package com.inca.npbi.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Calendar;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.SteGeneralTool;
import com.inca.npbi.server.BIReportinfo;
import com.inca.npbi.server.BasetableInfo;
import com.inca.npbi.server.Dsengine;
import com.inca.npbi.server.Dsinfo;
import com.inca.npbi.server.Dsview;
import com.inca.npbi.server.Timeparaminfo;


public class TestDsengine {

	static BIReportinfo createDebugReport() {
		BIReportinfo report = new BIReportinfo();
		report.setReportname("销售额,回款额");
		report.setReporttimetype(BIReportinfo.reporttimetype_month);
		BasetableInfo tableinfo = new BasetableInfo("npbi_test");
		report.setBasetableinfo(tableinfo);
		report.getKeycolumnmap().put("GOODSID", "GOODSID");

		Dsinfo ds = new Dsinfo();
		ds.setDbname("orcl");

		ds.setIp("192.9.200.63");
		ds.setPort(1521);
		ds.setUsername("cqcy080723");
		ds.setPassword("cqcy080723");

		// ds.setDbname("orcl");
		// ds.setIp("192.9.200.47");
		// ds.setUsername("npserver");
		// ds.setPassword("npserver");

		Dsview view = new Dsview();
		view.setDs(ds);

		String sql = "select goodsid,sum(goodsqty) goodsqty,sum(total_line) total_line from  \n"
				+ " bms_sa_doc ,bms_sa_dtl \n"
				+ " where bms_sa_doc.salesid=bms_sa_dtl.salesid \n"
				+ " and credate between {时间维度.开始日期} and {时间维度.结束日期} \n"
				+ " group by goodsid";

		view.setSql(sql);
		report.addView(view);

		sql = "select goodsid,sum(goodsqty) recgoodsqty,sum(total_line) recmoney from  \n"
				+ " bms_sa_rec_doc ,bms_sa_rec_dtl \n"
				+ " where bms_sa_rec_doc.sarecid=bms_sa_rec_dtl.sarecid \n"
				+ " and credate between {时间维度.开始日期} and {时间维度.结束日期} \n"
				+ " group by goodsid";
		view = new Dsview();
		view.setDs(ds);
		view.setSql(sql);
		report.addView(view);

		sql = "select goodsid,goodsname from pub_goods";
		view = new Dsview();
		view.setJoin_type(Dsview.jointype_noadd);
		view.setDs(ds);
		view.setSql(sql);
		report.addView(view);

		return report;
	}

	static Connection getTestCon() throws Exception {
		String dbip = "192.9.200.47";
		String dbname = "orcl";
		String dbuser = "npserver";
		String dbpass = "npserver";

		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		con.setAutoCommit(false);
		return con;

	}

	public static void gen() {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		SteGeneralTool stegen = new SteGeneralTool();
		stegen.pack();
		stegen.setVisible(true);
	}

	public static void testBadsql() throws Exception {
		String sql = "select " + "storageid," + "goodsid," + "goodsdtlid,"
				+ "batchid,batchsortno,batchno," + "lotid,lotno,lotsortno,"
				+ "goodsstatusid," + "sum(goodsqty) goodsqty "
				+ "from szyz_bms_stqty_sum_v " + "where " + "storageid=127 "
				+ "and goodsid=49881 " + "and goodsdtlid=37403 "
				+ "and goodsstatusid=1 " + "group by " + "storageid, "
				+ "goodsid, " + "goodsdtlid, "
				+ "batchid,batchno,batchsortno, " + "lotid,lotno,lotsortno, "
				+ "goodsstatusid " + "having " + " sum(goodsqty)>0 ";

		String dbip = "192.9.200.63";
		String dbname = "orcl";
		String dbuser = "szyz";
		String dbpass = "szyz";

		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		SelectHelper sh=new SelectHelper(sql);
		DBTableModel dm=sh.executeSelect(con, 0, 1);
		System.out.println(dm.getRowCount());
		
		DBTableModel dm1=queryStqtysum1(con,"127","49881","37403","","","","1");
		System.out.println(dm1.getRowCount());

	}
	
	static DBTableModel queryStqtysum1(Connection con, String storageid,
			String goodsid, String goodsdtlid, String batchid, String lotid,
			String posid, String goodsstatusid) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select\n");
		sb.append("storageid,\n");
		sb.append("goodsid,\n");
			sb.append("goodsdtlid,\n");
			sb.append("batchid,batchsortno,batchno,\n");
			sb.append("lotid,lotno,lotsortno,\n");
			sb.append("posid,\n");
		sb.append("goodsstatusid,\n");
		sb.append("sum(goodsqty) goodsqty\n");
		sb.append("from szyz_bms_stqty_sum_v\n");
		sb.append("where\n");
		sb.append("storageid="+storageid+"\n");
		sb.append("and goodsid="+goodsid+"\n");
			sb.append("and goodsdtlid="+goodsdtlid+"\n");
			//sb.append("and batchid="+batchid+"\n");
			//sb.append("and lotid="+lotid+"\n");
			//sb.append("and posid="+posid+"\n");
			sb.append("and goodsstatusid="+goodsstatusid+"\n");

		sb.append("group by\n");
		sb.append("storageid,\n");
		sb.append("goodsid,\n");
			sb.append("goodsdtlid,\n");
			sb.append("batchid,batchno,batchsortno,\n");
			sb.append("lotid,lotno,lotsortno,\n");
			sb.append("posid,\n");
		sb.append("goodsstatusid\n");
		sb.append("having\n");
		sb.append("sum(goodsqty)>0\n");

		String sql = sb.toString();

		SelectHelper sh = new SelectHelper(sql);
		System.out.println(sql);
		return sh.executeSelect(con, 0, 1000);
	}


	public static void main(String[] args) {
		

		BIReportinfo report = createDebugReport();
		Dsengine dseng = Dsengine.getInstance();
		try {
			testBadsql();
			//Connection con = getTestCon();
			//dseng.runReport(con, "43");
			// dseng.runReport(con, "2");
			// dseng.runReport(con, "3");
			// dseng.runReport(con, "4");
			/*
			 * Timeparaminfo timeparam=new Timeparaminfo();
			 * timeparam.year="2008"; timeparam.month="07";
			 * timeparam.instanceid=timeparam.year+timeparam.month;
			 * //dseng.runReport(con, report, timeparam);
			 * 
			 * timeparam.year="2008"; timeparam.month="06";
			 * timeparam.instanceid=timeparam.year+timeparam.month;
			 * //dseng.runReport(con, report, timeparam);
			 * 
			 * timeparam.year="2008"; timeparam.month="05";
			 * timeparam.instanceid=timeparam.year+timeparam.month;
			 * dseng.runReport(con, report, timeparam);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
