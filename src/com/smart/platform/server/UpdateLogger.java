package com.smart.platform.server;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.log4j.Category;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DecimalHelper;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;

/**
 * 修改 删除记录器.
 * 
 * @author user
 * 
 */
public class UpdateLogger {
	private static UpdateLogger instance;

	public static UpdateLogger getInstance() {
		if (instance == null) {
			instance = new UpdateLogger();
		}
		return instance;
	}

	HashMap<String, Boolean> needlogtablemap = new HashMap<String, Boolean>();

	/**
	 * 表是否需要记录修改日志?
	 * 
	 * @param tablename
	 * @return
	 */
	public boolean isNeeduploadlog(Connection con, String tablename) {
		Boolean bb;
		synchronized (needlogtablemap) {
			bb = needlogtablemap.get(tablename.toUpperCase());
		}
		if (bb != null) {
			return bb.booleanValue();
		}

		SelectHelper sh = new SelectHelper(
				"select tablename from np_update_reg where tablename=?");
		sh.bindParam(tablename.toUpperCase());
		try {
			DBTableModel dm = sh.executeSelect(con, 0, 1);
			if (dm.getRowCount() == 1) {
				synchronized (needlogtablemap) {
					needlogtablemap.put(tablename.toUpperCase(), new Boolean(
							true));
				}
				return true;
			} else {
				synchronized (needlogtablemap) {
					needlogtablemap.put(tablename.toUpperCase(), new Boolean(
							false));
				}
				return false;
			}
		} catch (Exception e) {
			Category.getInstance(UpdateLogger.class).error("Error", e);
			return false;
		}

	}
	
	public void reset(){
		synchronized (needlogtablemap) {
			needlogtablemap.clear();
		}
	}

	/**
	 * dm的row行,如果为修改,返回将某列由"xxx"改为"xxxx". 如果为删除,记录删除主键列名=xxx
	 */
	public static String createLogstring(DBTableModel dm, int row) {
		StringBuffer sb = new StringBuffer();
		int status = dm.getdbStatus(row);
		if (status == RecordTrunk.DBSTATUS_MODIFIED) {
			Enumeration<DBColumnDisplayInfo> en = dm.getDisplaycolumninfos()
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo col = en.nextElement();
				if (!col.isUpdateable() || !col.isDbcolumn()) {
					continue;
				}
				int colindex = dm.getColumnindex(col.getColname());
				String v = dm.getItemValue(row, colindex);
				if (v == null) {
					v = "";
				}
				String dbv = dm.getRecordThunk(row).getdbValueAt(colindex);
				if (dbv == null) {
					dbv = "";
				}
				if (col.getColtype().equals(DBColumnDisplayInfo.COLTYPE_NUMBER)) {
					if (v.length() == 0 && dbv.length() > 0
							|| dbv.length() == 0 && v.length() > 0) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append(col.getTitle() + "由" + dbv + "改为" + v);
					} else if (DecimalHelper.comparaDecimal(v, dbv) != 0) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append(col.getTitle() + "由" + dbv + "改为" + v);
					}
				} else {
					if (v.compareTo(dbv) != 0) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append(col.getTitle() + "由'" + dbv + "'改为'" + v
								+ "'");
					}
				}
			}
			if (sb.length() > 0) {
				String pkcol = dm.getPkcolname();
				String pkv = dm.getItemValue(row, pkcol);
				pkcol = dm.getColumninfo(pkcol).getTitle();
				sb.insert(0, "修改" + pkcol + "=" + pkv + ":");
			}
		} else if (status == RecordTrunk.DBSTATUS_DELETE) {
			String pkcol = dm.getPkcolname();
			String pkv = dm.getItemValue(row, pkcol);
			pkcol = dm.getColumninfo(pkcol).getTitle();
			sb.append("删除:" + pkcol + "=" + pkv);
		}
		return sb.toString();
	}

	public static void addLog(Connection con, String tablename, String userid,
			String username, String logmsg,String pkvalue) {
		if (logmsg.length() == 0)
			return;
		InsertHelper ih = new InsertHelper("np_update_log");
		ih.bindSequence("Seqid", "np_update_log_seq");
		ih.bindParam("tablename", tablename.toUpperCase());
		ih.bindSysdate("credate");
		ih.bindParam("userid", userid);
		ih.bindParam("username", username);
		ih.bindParam("updatelog", logmsg);
		ih.bindParam("pkvalue", pkvalue);
		try {
			ih.executeInsert(con);
		} catch (Exception e) {
			Category.getInstance(UpdateLogger.class).error("Error", e);
		}

	}
}
