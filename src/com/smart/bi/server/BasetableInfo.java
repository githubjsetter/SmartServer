package com.smart.bi.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Category;

import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DBHelper;
import com.smart.platform.util.SelectHelper;

/**
 * 基表信息
 * 
 * @author user
 * 
 */
public class BasetableInfo {
	Category logger = Category.getInstance(BasetableInfo.class);
	String tablename = "";
	/**
	 * 表列的信息
	 */
	HashMap<String, BasetableColumninfo> columnmap = new HashMap<String, BasetableColumninfo>();
	/**
	 * 维度列.key为大写维度列名.
	 */
	HashMap<String, String> keycolumnmap = new HashMap<String, String>();
	HashMap<String, String> indexcolumnmap = new HashMap<String, String>();

	public HashMap<String, String> getKeycolumnmap() {
		return keycolumnmap;
	}

	public BasetableInfo(String tablename) {
		super();
		this.tablename = tablename;
	}

	public String getTablename() {
		return tablename;
	}

	/**
	 * 加载列信息
	 * 
	 * @param con
	 * @throws Exception
	 */
	public void loadColumninfo(Connection con, String reportid)
			throws Exception {
		String sql = "select * from npbi_basetable_column where reportid=?";
		try {
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(reportid);
			DBTableModel dm = sh.executeSelect(con, 0, 1000);
			columnmap.clear();
			keycolumnmap.clear();
			for (int r = 0; r < dm.getRowCount(); r++) {
				BasetableColumninfo col = new BasetableColumninfo();
				col.colname = dm.getItemValue(r, "columnname");
				col.coltype = dm.getItemValue(r, "coltype");
				try {
					col.precision = Integer.parseInt(dm.getItemValue(r,
							"precision"));
				} catch (Exception e) {
					col.precision = 0;
				}
				try {
					col.scale = Integer.parseInt(dm.getItemValue(r, "scale"));
				} catch (Exception e) {
					col.scale = 0;
				}
				String keycolumn = dm.getItemValue(r, "keycolumn");
				String useindex = dm.getItemValue(r, "useindex");
				columnmap.put(col.colname.toUpperCase(), col);
				if (keycolumn.equals("1")) {
					keycolumnmap.put(col.colname.toUpperCase(), col.colname
							.toUpperCase());
				}
				if(useindex.equals("1")){
					indexcolumnmap.put(col.colname.toUpperCase(), col.colname
							.toUpperCase());
				}
			}

		} finally {
		}
	}

	public BasetableColumninfo getColumn(String colname) {
		return columnmap.get(colname);
	}

	public HashMap<String, BasetableColumninfo> getColumnmap() {
		return columnmap;
	}

	public void clearBasetable(Connection con, String instanceid)
			throws Exception {
		String sql = "delete " + tablename + " where npbi_instanceid='"
				+ instanceid + "' and rownum<=1000";
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			for (;;) {
				int rowcount = c1.executeUpdate();
				if (rowcount <= 0)
					break;
				con.commit();
			}
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}

	/**
	 * 建基表.如果已建调整列.
	 * 
	 * @param con
	 * @throws Exception
	 */
	public void fixBasetable(Connection con) throws Exception {
		String sql = "select count(*) ct from tab where tname=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(tablename.toUpperCase());
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		String ct = dm.getItemValue(0, "ct");
		if (ct.equals("1")) {
			// 调整列
			modifyTable(con);
		} else {
			// 建表
			createBasetable(con);
		}

		// 建索引
		createIndex(con);
	}

	void createIndex(Connection con) throws Exception {
		String idxname = tablename + "_instid";
		while (idxname.length() > 21) {
			idxname = idxname.substring(1);
		}
		String sql = "create index " + idxname + " on " + tablename
				+ "(npbi_instanceid)";
		try {
			DBHelper.executeSql(con, sql);
		} catch (Exception e) {

		}
		// 索引列
		Iterator<String> it = indexcolumnmap.keySet().iterator();
		while (it.hasNext()) {
			String colname = it.next();
			BasetableColumninfo colinfo = columnmap.get(colname);
			idxname = colname + "_" + tablename;
			while (idxname.length() > 21) {
				idxname = idxname.substring(0, idxname.length() - 1);
			}
			sql = "create index " + idxname + " on " + tablename + "("
					+ colname + ")";
			try {
				DBHelper.executeSql(con, sql);
			} catch (Exception e) {

			}
		}

	}

	void createBasetable(Connection con) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("npbi_instanceid varchar(40),npbi_lineno number(10,0)");
		Iterator<String> it = columnmap.keySet().iterator();
		while (it.hasNext()) {
			String colname = it.next();
			BasetableColumninfo colinfo = columnmap.get(colname);
			if (sb.length() > 0)
				sb.append(",");
			createColumnsql(colinfo, sb);
		}
		String sql = "create table " + tablename + "(" + sb.toString() + ")";
		logger.debug(sql);
		DBHelper.executeSql(con, sql);
	}

	void createColumnsql(BasetableColumninfo colinfo, StringBuffer sb) {
		sb.append(colinfo.colname);
		sb.append(" ");
		sb.append(colinfo.coltype);
		if (colinfo.coltype.equalsIgnoreCase("number")) {
			sb.append("(" + colinfo.precision + "," + colinfo.scale + ")");
		} else if (colinfo.coltype.equalsIgnoreCase("varchar")) {
			sb.append("(" + colinfo.precision + ")");
		} else if (colinfo.coltype.equalsIgnoreCase("date")) {
			// nothing
		}

	}

	void modifyTable(Connection con) throws Exception {
		// 比较列
		HashMap<String, BasetableColumninfo> dbcolmap = new HashMap<String, BasetableColumninfo>();

		String sql = "select cname,coltype,width,precision,scale from col where tname=? order by colno";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(tablename.toUpperCase());
		DBTableModel dm = sh.executeSelect(con, 0, 1000);
		for (int row = 0; row < dm.getRowCount(); row++) {
			String dbcolname = dm.getItemValue(row, "cname");
			if (dbcolname.equalsIgnoreCase("npbi_instanceid"))
				continue;
			String dbcoltype = dm.getItemValue(row, "coltype");
			if (dbcoltype.startsWith("VARCHAR")) {
				dbcoltype = "VARCHAR";
			}
			int width = 0;
			try {
				width = Integer.parseInt(dm.getItemValue(row, "width"));
			} catch (Exception e) {
			}
			int dbprecision = 0;
			try {
				dbprecision = Integer.parseInt(dm
						.getItemValue(row, "precision"));
			} catch (Exception e) {
			}
			int dbscale = 0;
			try {
				dbscale = Integer.parseInt(dm.getItemValue(row, "scale"));
			} catch (Exception e) {
			}
			if (dbcoltype.startsWith("VARCHAR")) {
				dbprecision = width;
			}
			BasetableColumninfo tmpinfo = new BasetableColumninfo();
			dbcolmap.put(dbcolname, tmpinfo);
			tmpinfo.colname = dbcolname;
			tmpinfo.coltype = dbcoltype;
			tmpinfo.precision = dbprecision;
			tmpinfo.scale = dbscale;
		}
		Iterator<String> it = columnmap.keySet().iterator();
		while (it.hasNext()) {
			String colname = it.next();
			BasetableColumninfo colinfo = columnmap.get(colname);
			BasetableColumninfo dbcolinfo = dbcolmap.get(colname);
			if (dbcolinfo == null) {
				// 加列
				StringBuffer sb = new StringBuffer();
				createColumnsql(colinfo, sb);
				sql = "alter table " + tablename + " add(" + sb.toString()
						+ ")";
				logger.debug(sql);
				DBHelper.executeSql(con, sql);

			} else {
				// 调整列
				if (!colinfo.coltype.equalsIgnoreCase(dbcolinfo.coltype)) {
					// 删除列重建
					sql = "alter table " + tablename + " drop column "
							+ colinfo.colname;
					logger.debug(sql);
					DBHelper.executeSql(con, sql);
					// 建列
					StringBuffer sb = new StringBuffer();
					createColumnsql(colinfo, sb);
					sql = "alter table " + tablename + " add(" + sb.toString()
							+ ")";
					logger.debug(sql);
					DBHelper.executeSql(con, sql);

				} else if (colinfo.precision != dbcolinfo.precision
						|| colinfo.scale != dbcolinfo.scale) {
					StringBuffer sb = new StringBuffer();
					createColumnsql(colinfo, sb);
					sql = "alter table "+tablename+" modify(" + sb.toString() + ")";
					logger.debug(sql);
					DBHelper.executeSql(con, sql);

				}

			}

		}
	}

}
