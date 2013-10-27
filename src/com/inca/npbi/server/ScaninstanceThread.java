package com.inca.npbi.server;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Category;
import org.apache.poi.hssf.record.UseSelFSRecord;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DecimalHelper;
import com.inca.np.util.SelectHelper;
import com.inca.npserver.dbcp.DBConnectPool;
import com.inca.npserver.dbcp.DBConnectPoolFactory;

/**
 * 后台线程,查询需要计算的报表实例,定时生成.
 * 
 * @author user
 * 
 */
public class ScaninstanceThread extends Thread {
	Category logger = Category.getInstance(ScaninstanceThread.class);

	public void run() {
		logger.info("!!!!!!!!scan bi report instance started....");
		DBConnectPool pool = DBConnectPoolFactory.getInstance()
				.getDefaultpool();
		for (;;) {
			Connection con = null;
			try {
				con=Dsengine.getInstance().getConnection();
				scan(con);
			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
					}
				}
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
		}
	}

	void scan(Connection con) {
		String sql = "select  npbi_report_def.reportid,\n"
				+ "npbi_report_def.reportname,\n"
				+ "npbi_report_def.timetype,\n"
				+ "npbi_report_def.usemonthendday,\n"
				+ "npbi_report_def.calctime,\n" + "npbi_report_def.calchour,\n"
				+ "npbi_instance.instanceid,\n" + "npbi_instance.year,\n"
				+ "npbi_instance.month,\n" + "npbi_instance.day,\n"
				+ "npbi_instance.year1,\n" + "npbi_instance.month1,\n"
				+ "npbi_instance.day1,\n"
				+ "npbi_instance.usestatus,\n"
				+ "npbi_instance.lastcalctime,npbi_instance.npbi_instanceid\n" +

				"from npbi_report_def,npbi_instance where\n"
				+ "npbi_instance.reportid=npbi_report_def.reportid\n"
				+ "and npbi_report_def.usestatus=1\n"
				+ "and npbi_instance.usestatus<>3\n" + "  order by calcorder,npbi_instanceid";
		//logger.debug(sql);
		
		PreparedStatement c1 = null;
		try {
			SelectHelper sh = new SelectHelper(
					"select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') nowdate from dual");
			DBTableModel dm = sh.executeSelect(con, 0, 1);
			String nowdate = dm.getItemValue(0, 0);

			boolean needcalc = false;
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				needcalc = false;
				String reportid = rs.getString("reportid");
				String reportname = rs.getString("reportname");
				String timetype = rs.getString("timetype");
				if(timetype==null)timetype="";
				String usemonthendday = rs.getString("usemonthendday");
				String calctime = rs.getString("calctime");
				if(calctime==null)calctime="";
				String calchour = rs.getString("calchour");
				String instanceid = rs.getString("instanceid");
				String year = rs.getString("year");
				String month = rs.getString("month");
				String day = rs.getString("day");
				String year1 = rs.getString("year1");
				String month1 = rs.getString("month1");
				String day1 = rs.getString("day1");
				String npbi_instanceid = rs.getString("npbi_instanceid");
				String usestatus = rs.getString("usestatus");
				String lastcalctime = rs.getString("lastcalctime");
				if(lastcalctime==null)lastcalctime="";

				// 检查计算时间是不是到了?
				BigDecimal reportneedhour = DecimalHelper.toDec(calchour);
				BigDecimal nowhour = DecimalHelper.toDec(nowdate
						.substring(11,13));
				
				/////////for debug, comment below 4 line
				if (nowhour.compareTo(reportneedhour) < 0) {
					needcalc = false;
					continue;
				}

				Timeparaminfo tparam = new Timeparaminfo();
				tparam.setTimetype(timetype);
				tparam.setYear(year);
				tparam.setMonth(month);
				tparam.setDay(day);
				tparam.setYear1(year1);
				tparam.setMonth1(month1);
				tparam.setDay1(day1);
				BIReportinfo.fillDateparam(tparam);
				//如果现在还没有到开始时间,不算
				if(nowdate.compareTo(tparam.getStartdate())<0){
					needcalc=false;
					continue;
				}
				
				if (calctime.equals("year") || calctime.equals("month")) {
					// 如果现在日期比结束日期大,就算
					if (nowdate.compareTo(tparam.getEnddate()) > 0) {
						needcalc = true;
					}

				} else {
					// 日计算
					// 比较日期
					if (nowdate.compareTo(lastcalctime) > 0) {
						needcalc = true;
					}

				}
				if (!needcalc) {
					continue;
				}
				// 检查上次计算时间.如果今天算过了,就不再算了.
				String lastcalcdatehour="";
				String nowdatehour="";
				if(lastcalctime.length()>0 ){
					if(lastcalctime.substring(0,10).equals(nowdate.substring(0,10))){
						lastcalcdatehour=lastcalctime.substring(11,13);
						nowdatehour=nowdate.substring(11,13);
						if(nowdatehour.compareTo(lastcalcdatehour)>=0){
							//今天算过了,不算了
							needcalc=false;
							continue;
							
						}
					}
				}

				try {
					logger.info("后台计算instanceid="+instanceid+",reportid="+reportid+","+reportname);
					Dsengine.getInstance().runReport(con, instanceid);
				} catch (Exception e) {
					logger.error("error", e);
				}
			}
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public static void main(String[] args) {
/*		ScaninstanceThread t = new ScaninstanceThread();
		t.setDaemon(false);
		t.start();
*/
		Dsengine.getInstance();
		for(;;){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
