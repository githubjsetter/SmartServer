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
 * �޸� ɾ����¼��.
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
	 * ���Ƿ���Ҫ��¼�޸���־?
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
	 * dm��row��,���Ϊ�޸�,���ؽ�ĳ����"xxx"��Ϊ"xxxx". ���Ϊɾ��,��¼ɾ����������=xxx
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
						sb.append(col.getTitle() + "��" + dbv + "��Ϊ" + v);
					} else if (DecimalHelper.comparaDecimal(v, dbv) != 0) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append(col.getTitle() + "��" + dbv + "��Ϊ" + v);
					}
				} else {
					if (v.compareTo(dbv) != 0) {
						if (sb.length() > 0)
							sb.append(",");
						sb.append(col.getTitle() + "��'" + dbv + "'��Ϊ'" + v
								+ "'");
					}
				}
			}
			if (sb.length() > 0) {
				String pkcol = dm.getPkcolname();
				String pkv = dm.getItemValue(row, pkcol);
				pkcol = dm.getColumninfo(pkcol).getTitle();
				sb.insert(0, "�޸�" + pkcol + "=" + pkv + ":");
			}
		} else if (status == RecordTrunk.DBSTATUS_DELETE) {
			String pkcol = dm.getPkcolname();
			String pkv = dm.getItemValue(row, pkcol);
			pkcol = dm.getColumninfo(pkcol).getTitle();
			sb.append("ɾ��:" + pkcol + "=" + pkv);
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
