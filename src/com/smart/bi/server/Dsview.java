package com.smart.bi.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SelectHelper;

/**
 * 数据源视图
 * 
 * @author user
 * 
 */
public class Dsview {
	Category logger = Category.getInstance(Dsview.class);
	/**
	 * 关联,并新增
	 */
	public final static String jointype_add = "jointype_add";

	/**
	 * 关联,不新增
	 */
	public final static String jointype_noadd = "jointype_noadd";

	/**
	 * 数据源
	 */
	Dsinfo ds = null;

	/**
	 * sql语句
	 */
	String sql = "";

	String join_type = jointype_add;
	
	int maxrows=0;

	BIReportinfo report = null;
	Timeparaminfo timeparam = null;

	public String getJoin_type() {
		return join_type;
	}

	public void setJoin_type(String join_type) {
		this.join_type = join_type;
	}

	/**
	 * 生成基表数据
	 * 
	 * @param con
	 * @param timeparam
	 * @param testflag 测试标志.如果为true,只测试sql是否正确.
	 */
	public void runReport(Connection con, BIReportinfo report,
			Timeparaminfo timeparam,boolean testflag) throws Exception {
		this.report = report;
		this.timeparam = timeparam;
		// 从数据库中进行查询
		// 查询基表.由基表中的列值做为条件
		PreparedStatement c_sales = null;
		Connection con1 = null;
		boolean newconnction=false;
		try {
			if (ds == null) {
				con1 = con;
				newconnction=false;
			} else {
				con1 = ds.getConnect();
				newconnction=true;
			}

			// 将日期时间翻译成开始日期,结束日期
			BIReportinfo.fillDateparam(timeparam);

			String sql = fillParam(timeparam);

			c_sales = con1.prepareStatement(sql);

			ResultSet rs1 = c_sales.executeQuery();
			if(!testflag){
				generalFetch(con, con1, rs1, join_type.equals(jointype_add));
			}

			con.commit();
		} finally {
			if (c_sales != null) {
				c_sales.close();
			}
			if (newconnction && con1 != null) {
				con1.close();
			}
		}

	}

	/**
	 * 如果是逻辑月,需要通过数据库去取参数
	 * 
	 * @param con1
	 * @param timeparam
	 */

	private void generalFetch(Connection con, Connection con1, ResultSet rs1,
			boolean createnewrecord) throws Exception {
		PreparedStatement c_base = null;
		PreparedStatement c_update = null;
		PreparedStatement c_ins = null;

		String tablename = report.getBasetableinfo().getTablename();
		try {
			String sql = "select rowid  from " + tablename;
			HashMap<String, String> keycolumnmap = report.getKeycolumnmap();
			HashMap<String, BasetableColumninfo> columnmap = report
					.getBasetableinfo().getColumnmap();
			StringBuffer sb = new StringBuffer();
			StringBuffer sb1 = new StringBuffer();
			
			ResultSetMetaData rsm=rs1.getMetaData();
			
			for(int i=0;i<rsm.getColumnCount();i++){
				String colname = rsm.getColumnName(i+1);
				if(report.getKeycolumnmap().get(colname.toUpperCase())==null){
					//不是维度列
					continue;
				}
				BasetableColumninfo colinfo = columnmap.get(colname
						.toUpperCase());
				if (sb.length() > 0) {
					sb.append(" and ");
				}
				if (colinfo.coltype.equalsIgnoreCase("DATE")) {
					sb.append(colname + "=to_date(?,'yyyy-mm-dd hh24:mi:ss')");
				} else {
					sb.append(colname + "=?");
				}
			}
			sql += " where npbi_instanceid='" + timeparam.getNpbi_instanceid() +"'";
			if(sb.length()>0){
				sql+=" and "+sb.toString();
			}
			logger.debug(sql);
			c_base = con.prepareStatement(sql);


			// 开始查询rs1
			int rowindex = 0;
			int colindex = 1;
			for (rowindex = 0; (maxrows <=0 || maxrows>0 && rowindex<maxrows) && rs1.next(); rowindex++) {
				
				if ((rowindex + 1) % 1000 == 0) {
					logger.info("处理" + (rowindex + 1) + "条记录");
					con.commit();
				}
				// 查询基表
				colindex = 1;
				for(int i=0;i<rsm.getColumnCount();i++){
					String colname = rsm.getColumnName(i+1);
					if(report.getKeycolumnmap().get(colname.toUpperCase())==null){
						//不是维度列
						continue;
					}
					BasetableColumninfo colinfo = columnmap.get(colname
							.toUpperCase());
					String v = null;
					v=rs1.getString(colname);
					if (v == null)
						v = "";
					if (colinfo.coltype.equalsIgnoreCase("date")) {
						if (v.length() > 19)
							v = v.substring(0, 19);
					}
					c_base.setString(colindex++, v);
				}
				ResultSet rsbase = c_base.executeQuery();
				int fetchbasecount=0;
				while(rsbase.next()) {
					fetchbasecount++;
					// 进行update
					String rowid = rsbase.getString("rowid");
					if (c_update == null) {
						sb = new StringBuffer();
						// 新sql
						for (int c = 0; c < rsm.getColumnCount(); c++) {
							String colname = rsm.getColumnName(c + 1);
							if (columnmap.get(colname.toUpperCase()) == null)
								continue;
							String coltype = DBModel2Jdbc.getColumntype(rsm
									.getColumnType(c + 1));
							if (sb.length() > 0) {
								sb.append(",");
							}
							sb.append(colname);
							if (coltype.equalsIgnoreCase("date")) {
								sb
										.append("=to_date(?,'yyyy-mm-dd hh24:mi:ss')");
							} else {
								sb.append("=?");
							}
						}
						sql = "update " + tablename + " set " + sb.toString()
								+ " where rowid=?";
						logger.debug(sql);
						c_update = con.prepareStatement(sql);
					}

					// 绑定值
					colindex = 1;
					for (int c = 0; c < rsm.getColumnCount(); c++) {
						String colname = rsm.getColumnName(c + 1);
						if (columnmap.get(colname.toUpperCase()) == null)
							continue;
						String coltype = DBModel2Jdbc.getColumntype(rsm
								.getColumnType(c + 1));
						String v = rs1.getString(colname);
						if (v == null)
							v = "";
						if (coltype.equalsIgnoreCase("DATE")) {
							if (v.length() > 19)
								v = v.substring(0, 19);
						}
						c_update.setString(colindex++, v);
					}
					c_update.setString(colindex++, rowid);
					c_update.executeUpdate();

				} 
				if (fetchbasecount==0 && createnewrecord) {
					// insert
					if (c_ins == null) {
						sb = new StringBuffer();
						sb1 = new StringBuffer();
						// 新sql
						for (int c = 0; c < rsm.getColumnCount(); c++) {
							String colname = rsm.getColumnName(c + 1);
							if (columnmap.get(colname.toUpperCase()) == null)
								continue;
							String coltype = DBModel2Jdbc.getColumntype(rsm
									.getColumnType(c + 1));
							if (sb.length() > 0) {
								sb.append(",");
								sb1.append(",");
							}
							sb.append(colname);
							if (coltype.equalsIgnoreCase("date")) {
								sb1
										.append("to_date(?,'yyyy-mm-dd hh24:mi:ss')");
							} else {
								sb1.append("?");
							}
						}
						sb.append(",npbi_instanceid");
						sb1.append(",?");

						sql = "insert into " + tablename + "(" + sb.toString()
								+ ")values(" + sb1.toString() + ")";
						logger.debug(sql);
						c_ins = con.prepareStatement(sql);
					}
					// 绑定值
					colindex = 1;
					for (int c = 0; c < rsm.getColumnCount(); c++) {
						String colname = rsm.getColumnName(c + 1);
						if (columnmap.get(colname.toUpperCase()) == null)
							continue;
						String coltype = DBModel2Jdbc.getColumntype(rsm
								.getColumnType(c + 1));
						String v = rs1.getString(colname);
						if (v == null)
							v = "";
						if (coltype.equalsIgnoreCase("DATE")) {
							if (v.length() > 19)
								v = v.substring(0, 19);
						}
						c_ins.setString(colindex++, v);
					}

					c_ins.setString(colindex++, timeparam.getNpbi_instanceid());
					c_ins.executeUpdate();

				}
			}
		} finally {
			if (c_base != null) {
				c_base.close();
			}
			if (c_ins != null) {
				c_ins.close();
			}
			if (c_update != null) {
				c_update.close();
			}
		}

	}

	/**
	 * 将sql中的时间参数换为timeparam中的时间
	 * 
	 * @return
	 */
	String fillParam(Timeparaminfo timeparam) {
		sql = replace(sql, "{时间维度.年}", timeparam.getYear());
		sql = replace(sql, "{时间维度.月}", timeparam.getMonth());
		sql = replace(sql, "{时间维度.日}", timeparam.getDay());
		sql = replace(sql, "{时间维度.结束年}", timeparam.getYear1());
		sql = replace(sql, "{时间维度.结束月}", timeparam.getMonth1());
		sql = replace(sql, "{时间维度.结束日}", timeparam.getDay1());
		sql = replace(sql, "{时间维度.结束日}", timeparam.getDay1());
		sql = replace(sql, "{时间维度.上月.年}", timeparam.getPriormonthYear());
		sql = replace(sql, "{时间维度.上月.月}", timeparam.getPriormonthMonth());
		
		sql = replace(sql, "{时间维度.开始日期}", "to_date('" + timeparam.getStartdate()
				+ "','yyyy-mm-dd hh24:mi:ss')");
		sql = replace(sql, "{时间维度.结束日期}", "to_date('" + timeparam.getEnddate()
				+ "','yyyy-mm-dd hh24:mi:ss')");
		logger.debug(sql);
		return sql;
	}

	/**
	 * 将sql中的src替为target
	 * 
	 * @param sql
	 * @param src
	 * @param target
	 * @return
	 */
	static String replace(String sql, String src, String target) {
		for (;;) {
			int p = sql.indexOf(src);
			if (p < 0)
				break;
			int p1 = p + src.length();
			sql = sql.substring(0, p) + target + sql.substring(p1);
		}
		return sql;
	}

	public Dsinfo getDs() {
		return ds;
	}

	public void setDs(Dsinfo ds) {
		this.ds = ds;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public static Vector<Dsview> loadViewsFromdb(Connection con, String reportid)
			throws Exception {
		Vector<Dsview> views = new Vector<Dsview>();
		String sql = "select * from npbi_view where reportid=? order by calcorder";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(reportid);
		DBTableModel dm = sh.executeSelect(con, 0, 1000);
		for (int row = 0; row < dm.getRowCount(); row++) {
			String vsql = dm.getItemValue(row, "sql");
			String jointype = dm.getItemValue(row, "jointype");
			Dsview view = new Dsview();
			view.setSql(vsql);
			view.setJoin_type(jointype);
			try {
				view.maxrows=Integer.parseInt(dm.getItemValue(row, "maxrows"));
			} catch (Exception e) {
			}
			views.add(view);

			//查询数据库源信息
			String dsid = dm.getItemValue(row, "dsid");
			if(dsid!=null && dsid.length()>0){
				//
				Dsinfo ds=new Dsinfo();
				ds.loadFromdb(con,dsid);
				view.ds=ds;
			}

		}
		
		
		return views;
	}
}
