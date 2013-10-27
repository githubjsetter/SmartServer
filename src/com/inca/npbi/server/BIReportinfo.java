package com.inca.npbi.server;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SelectHelper;

/**
 * 报表信息
 * 
 * @author user
 * 
 */
public class BIReportinfo {
	/**
	 * 时间维度为年
	 */
	public final static String reporttimetype_year = "year";
	/**
	 * 时间维度为年月
	 */
	public final static String reporttimetype_month = "month";
	/**
	 * 时间维度为逻辑月
	 */

	public final static String reporttimetype_usemonth = "usemonth";

	/**
	 * 时间维度为日
	 */

	public final static String reporttimetype_day = "day";

	/**
	 * 时间维度为日期范围
	 */

	public final static String reporttimetype_daybetween = "daybetween";

	/**
	 * 报表定义ID
	 */
	String reportid = "";

	/**
	 * 编号
	 */
	String reportno = "";

	/**
	 * 名称
	 */
	String reportname = "";

	/**
	 * 时间维度类型
	 */
	String reporttimetype = "";

	BasetableInfo basetableinfo = null;

	/**
	 * 后处理
	 */
	String posttreate="";
	
	/**
	 * 计算列表达式
	 */
	String calccolumns="";
	
	Vector<Dsview> views = new Vector<Dsview>();

	public String getCalccolumns() {
		return calccolumns;
	}

	public void setCalccolumns(String calccolumns) {
		this.calccolumns = calccolumns;
	}

	public String getPosttreate() {
		return posttreate;
	}

	public void setPosttreate(String posttreate) {
		this.posttreate = posttreate;
	}

	public void setBasetableinfo(BasetableInfo basetableinfo) {
		this.basetableinfo = basetableinfo;
	}

	public HashMap<String, String> getKeycolumnmap() {
		return basetableinfo.getKeycolumnmap();
	}

	public String getReportid() {
		return reportid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	public String getReportno() {
		return reportno;
	}

	public void setReportno(String reportno) {
		this.reportno = reportno;
	}

	public String getReportname() {
		return reportname;
	}

	public void setReportname(String reportname) {
		this.reportname = reportname;
	}

	public String getReporttimetype() {
		return reporttimetype;
	}

	public void setReporttimetype(String reporttimetype) {
		this.reporttimetype = reporttimetype;
	}

	public BasetableInfo getBasetableinfo() {
		return basetableinfo;
	}

	public Enumeration<Dsview> getViews() {
		return views.elements();
	}

	public void addView(Dsview view) {
		views.add(view);
	}

	public void clearBasetable(Connection con, String instanceid)
			throws Exception {
		basetableinfo.clearBasetable(con, instanceid);
	}

	public void loadfromDB(Connection con) throws Exception {
		String sql = "select * from npbi_report_def where reportid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(reportid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		reportno = dm.getItemValue(0, "reportno");
		reportname = dm.getItemValue(0, "reportname");
		reporttimetype = dm.getItemValue(0, "timetype");
		String tablename = dm.getItemValue(0, "basetablename");
		basetableinfo = new BasetableInfo(tablename);
		basetableinfo.loadColumninfo(con, reportid);
		posttreate=dm.getItemValue(0, "posttreate");
		calccolumns=dm.getItemValue(0, "calccolumns");
		views = Dsview.loadViewsFromdb(con, reportid);

	}

	public void fixBasetable(Connection con) throws Exception {
		basetableinfo.fixBasetable(con);

	}

	public static void fillDateparam(Timeparaminfo timeparam) throws Exception {
		String timetype = timeparam.getTimetype();
		if (timetype.equals(BIReportinfo.reporttimetype_year)) {
			// 开始日期为年的1-1 至12 31
			timeparam.setStartdate( timeparam.getYear() + "-01-01 00:00:00");
			if (timeparam.getYear1().length() == 0) {
				timeparam.setEnddate( timeparam.getYear() + "-12-31 23:59:59");
			} else {
				timeparam.setEnddate(timeparam.getYear1() + "-12-31 23:59:59");
			}
		} else if (timetype.equals(BIReportinfo.reporttimetype_month)) {
			int iy = Integer.parseInt(timeparam.getYear());
			int im = Integer.parseInt(timeparam.getMonth());
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, iy);
			cal.set(Calendar.MONTH, im - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			timeparam.setStartdate( df.format(cal.getTime()) + " 00:00:00");
			if (timeparam.getYear1().length() > 0 && timeparam.getMonth1().length() > 0) {
				iy = Integer.parseInt(timeparam.getYear1());
				im = Integer.parseInt(timeparam.getMonth1());
				cal.set(Calendar.YEAR, iy);
				cal.set(Calendar.MONTH, im - 1);
				cal.set(Calendar.DAY_OF_MONTH, 1);
			}
			int lastdayofmonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			cal.set(Calendar.DAY_OF_MONTH, lastdayofmonth);
			timeparam.setEnddate( df.format(cal.getTime()) + " 23:59:59");
		} else if (timetype.equals(BIReportinfo.reporttimetype_usemonth)) {

		} else if (timetype.equals(BIReportinfo.reporttimetype_day)) {
			timeparam.setStartdate( timeparam.getYear() + "-" + timeparam.getMonth() + "-"
					+ timeparam.getDay() + " 00:00:00");
			if (timeparam.getYear1().length() > 0 && timeparam.getMonth1().length()>0
					&& timeparam.getDay1().length()>0) {
				timeparam.setEnddate( timeparam.getYear1() + "-" + timeparam.getMonth1()
				+ "-" + timeparam.getDay1() + " 23:59:59");
			} else {
				timeparam.setEnddate( timeparam.getYear() + "-" + timeparam.getMonth()
						+ "-" + timeparam.getDay() + " 23:59:59");
			}
		} else {
			throw new Exception("cann't treat report timetype=" + timetype);
		}
	}

}
