package com.inca.np.communicate;

import org.apache.log4j.Category;

import java.sql.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.HashMap;

import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.StesaveIF;
import com.inca.np.server.MdesaveIF;
import com.inca.np.util.SelectHelper;
import com.inca.npworkflow.server.WfEngine;

/**
 * dbmodel���浽���ݿ�
 */
public class DBModel2Jdbc {
	static Category logger = Category.getInstance(DBModel2Jdbc.class);

	/**
	 * ��ResultSet�Ľ��ת��Ϊһ��dbmodel
	 * 
	 * @param rs
	 *            �����
	 * @return
	 * @throws Exception
	 */
	public static DBTableModel createFromRS(ResultSet rs) throws Exception {
		return createFromRS(rs, 0, 1000000);
	}

	/**
	 * ��ResultSet�Ľ��ת��Ϊһ��dbmodel
	 * 
	 * @param rs
	 *            �����
	 * @param startrow
	 *            ��ʼ��,��0��.
	 * @param maxrowcount
	 *            ��෵������
	 * @return
	 * @throws Exception
	 */
	public static DBTableModel createFromRS(ResultSet rs, int startrow,
			int maxrowcount) throws Exception {
		ResultSetMetaData meta = rs.getMetaData();
		int colct = meta.getColumnCount();

		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		for (int c = 1; c <= colct; c++) {
			DBColumnDisplayInfo info = new DBColumnDisplayInfo(meta
					.getColumnName(c), getColumntype(meta.getColumnType(c)),
					meta.getColumnName(c));
			cols.add(info);
		}
		DBTableModel dbmodel = new DBTableModel(cols);

		long t1 = System.currentTimeMillis();
		// ���ɼ�¼,������ʼ
		for (int r = 0; r < startrow && rs.next(); r++)
			;
		/*
		 * logger.info("begin call DBModel2Jdbc absolute");
		 * rs.absolute(startrow); logger.info("finish call DBModel2Jdbc
		 * absolute");
		 */

		boolean hasmore = false;
		for (int r = 0; r < maxrowcount + 1 && rs.next(); r++) {
			long t2 = System.currentTimeMillis();
			if (t2 - t1 > 1000L) {
				//logger.debug("����Result.next()ʱ��=" + String.valueOf((t2 - t1)));
			}
			RecordTrunk rec = new RecordTrunk(colct);
			rec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
			if (r == maxrowcount) {
				hasmore = true;
				break;
			}

			dbmodel.addRecord(rec);
			for (int c = 0; c < colct; c++) {
				String value = rs.getString(c + 1);
				if (value == null)
					value = "";
				String coltype = dbmodel.getColumnDBType(c);
				if (coltype.equalsIgnoreCase("date")) {
					if (value.length() > 19) {
						value = value.substring(0, 19);
					}
				}
				rec.setdbValueAt(c, value);
			}
			rec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
		}
		dbmodel.setHasmore(hasmore);
		return dbmodel;
	}

	/**
	 * ��sql����תΪ�ַ�������
	 * 
	 * @param sqltype
	 * @return
	 * @throws Exception
	 */
	public static String getColumntype(int sqltype) throws Exception {
		switch (sqltype) {
		case -7:
			return "BIT";
		case -6:
			return "TINYINT";
		case 5:
			return "SMALLINT";
		case 4:
			return "INTEGER";
		case -5:
			return "BIGINT";
		case 6:// float
		case 7:// real
		case 8:// double
		case 2:// NUMERIC
		case 3:// DECIMAL
			return "NUMBER";
		case 1: // CHAR
		case 12:// VARCHAR
			return "VARCHAR";
		case -1:
			return "LONGVARCHAR";
		case 91:
			return "DATE";
		case 92:
			return "TIME";
		case 93:
			return "TIMESTAMP";
		case -3:
			return "VARBINARY";
		case -4:
			return "LONGVARBINARY";
		case 0:
			return "NULL";
		default:
			throw new Exception("�Ҳ���sqltype=" + sqltype);
		}

	}

	/**
	 * ���浥��༭�ı�
	 * 
	 * @param con
	 *            ���ݿ�����
	 * @param userrininfo
	 *            ��ǰ�û�
	 * @param tablename
	 *            ����
	 * @param viewname
	 *            ��ͼ��
	 * @param coldisplayinfos
	 *            �ж�������
	 * @param dbmodel
	 *            Ҫ���������Դ
	 * @param saveif
	 *            ����ص��ӿ�
	 * @param commit
	 *            �Ƿ��ύ.trueÿ����ɹ�һ����¼���ύ,false,��Ҫ�ύ.
	 * @return ���
	 * @throws Exception
	 */
	public static ResultCommand save2DB(Connection con,
			Userruninfo userrininfo, String tablename, String viewname,
			Vector<DBColumnDisplayInfo> coldisplayinfos, DBTableModel dbmodel,
			StesaveIF saveif, boolean commit) throws Exception {
		ResultCommand resultcmd = new ResultCommand();
		int okct = 0;
		int errorct = 0;
		coldisplayinfos = dbmodel.getDisplaycolumninfos();

		try {
			for (int row = 0; row < dbmodel.getRowCount(); row++) {
				try {
					if (saveif != null) {
						saveif.on_beforesave(con, userrininfo, dbmodel, row);
					}
					RecordTrunk savedrec = buildExecSql(con, tablename,
							viewname, coldisplayinfos, dbmodel, row);
					savedrec.setSaveresult(0, "����ɹ�");
					savedrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
					resultcmd.addLineResult(savedrec);
					if (saveif != null) {
						DBTableModel saveddbmodel = dbmodel.copyStruct();
						saveddbmodel.appendRecord(savedrec);
						saveif.on_aftersave(con, userrininfo, saveddbmodel, 0);

						String pkcolname = dbmodel.getPkcolname();
						if (pkcolname!=null && pkcolname.length() > 0) {
							String pkvalue = saveddbmodel.getItemValue(0,
									pkcolname);
							//WfEngine.getInstance().newQueue(con, tablename,
							//		pkvalue);
						}

					}
				} catch (Exception e) {
					con.rollback();
					logger.error("����ʧ��", e);
					RecordTrunk oldrec = dbmodel.getRecordThunk(row);
					oldrec.setSaveresult(1, e.getMessage());
					resultcmd.addLineResult(oldrec);
					resultcmd.setMessage(resultcmd.getMessage()
							+ e.getMessage());
					errorct++;
					continue;
				}

				// ���ɹ������ӿڱ�

				// ����ɹ��ˣ���Ҫ��ѯ��¼
				if (commit) {
					con.commit();
				}

				okct++;
			}

		} catch (Exception sqle) {
			con.rollback();
			ResultCommand errorcmd = new ResultCommand(
					ResultCommand.RESULT_FAILURE, sqle.getMessage());
			return errorcmd;
		} finally {
		}

		if (errorct == 0) {
			resultcmd.setResult(ResultCommand.RESULT_OK);
		} else if (errorct > 0 && okct > 0) {
			resultcmd.setResult(ResultCommand.RESULT_PARTFAILURE);
			resultcmd.setMessage("���ֳɹ�:" + resultcmd.getMessage());
		} else {
			resultcmd.setResult(ResultCommand.RESULT_FAILURE);
			resultcmd.setMessage("ʧ��:" + resultcmd.getMessage());
		}
		return resultcmd;
	}

	/**
	 * ����ϸ��,ʧ�������쳣,��rollbackҲ��commit
	 * 
	 * @param con
	 *            ����
	 * @param tablename
	 *            ϸ������
	 * @param viewname
	 *            ϸ����ͼ��
	 * @param coldisplayinfos
	 *            ϸ���ж�������
	 * @param dbmodel
	 *            ϸ������
	 * @param saveif
	 *            �ܵ�ϸĿ����ص��ӿ�
	 * @return ���
	 * @throws Exception
	 */
	public static ResultCommand saveDtl2DB(Connection con,
			Userruninfo userruninfo, String tablename, String viewname,
			Vector<DBColumnDisplayInfo> coldisplayinfos, DBTableModel dbmodel,
			StesaveIF saveif) throws Exception {
		ResultCommand resultcmd = new ResultCommand();
		int okct = 0;
		int errorct = 0;
		coldisplayinfos = dbmodel.getDisplaycolumninfos();

		for (int row = 0; row < dbmodel.getRowCount(); row++) {
			try {
				if (saveif != null) {
					saveif.on_beforesave(con, userruninfo, dbmodel, row);
				}
				RecordTrunk savedrec = buildExecSql(con, tablename, viewname,
						coldisplayinfos, dbmodel, row);
				if (saveif != null) {
					DBTableModel saveddbmodel = dbmodel.copyStruct();
					saveddbmodel.appendRecord(savedrec);
					saveif.on_aftersave(con, userruninfo, saveddbmodel, 0);
				}
				savedrec.setSaveresult(0, "����ɹ�");
				savedrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
				resultcmd.addLineResult(savedrec);

				okct++;
			} catch (Exception e) {
				logger.error("error", e);
				errorct++;
				RecordTrunk resultrec = dbmodel.getRecordThunk(row);
				resultrec.setSaveresult(1, e.getMessage());
				resultcmd.addLineResult(dbmodel.getRecordThunk(row));
			}
		}

		if (errorct == 0) {
			resultcmd.setResult(ResultCommand.RESULT_OK);
		} else if (errorct > 0 && okct > 0) {
			resultcmd.setResult(ResultCommand.RESULT_PARTFAILURE);
			resultcmd.setMessage("���ֳɹ�");
		} else {
			resultcmd.setResult(ResultCommand.RESULT_FAILURE);
			resultcmd.setMessage("ʧ��");
		}
		return resultcmd;
	}

	/**
	 * ִ��һ��insert��update��delete sql
	 * 
	 * @param con
	 *            ����
	 * @param sql
	 *            �������ݵ�sql
	 * @param valuevect
	 *            ����ֵ
	 * @throws Exception
	 */
	static void executeSql(Connection con, String sql, Vector<String> valuevect)
			throws Exception {
		PreparedStatement c1 = null;

		try {
			c1 = con.prepareStatement(sql);
			for (int i = 0; i < valuevect.size(); i++) {
				c1.setString(i + 1, valuevect.elementAt(i));
			}
			c1.executeUpdate();
		} catch (Exception e) {
			logger.error("ERROR sql:" + sql, e);
			throw e;
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}

	/**
	 * ȡ���к�ֵ
	 * 
	 * @param con
	 *            ����
	 * @param seqname
	 *            ���к���
	 * @return ���кŵ�ǰֵ
	 * @throws Exception
	 */
	public static String getSeqvalue(Connection con, String seqname)
			throws Exception {
		String sql = "select " + seqname + ".nextval from dual";
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			rs.next();
			return rs.getString(1);
		} catch (Exception e) {
			logger.error("ȡ���к�" + seqname + "ʧ��", e);
			throw e;
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}

	}

	/**
	 * ��һ����¼�����ɲ�ִ��sql�������������²�ѯһ��������¼����
	 * 
	 * @param con
	 *            ����
	 * @param tablename
	 *            ����
	 * @param coldisplayinfos
	 *            �ж�������
	 * @param dbmodel
	 *            ����Դ
	 * @param row
	 *            ����Դ���к�
	 * @return �������ɹ������²�ѯ����������¼
	 * @throws Exception
	 */
	static RecordTrunk buildExecSql(Connection con, String tablename,
			String viewname, Vector<DBColumnDisplayInfo> coldisplayinfos,
			DBTableModel dbmodel, int row) throws Exception {

		Vector<String> valuevect = new Vector<String>();
		StringBuffer wheresb = new StringBuffer();
		RecordTrunk rec = dbmodel.getRecordThunk(row);
		HashMap valuemap = new HashMap();
		boolean hascolmodified = false;
		StringBuffer sqlsb = new StringBuffer();
		if (rec.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
			sqlsb.append("insert into " + tablename + "(");
			StringBuffer colsb = new StringBuffer();
			StringBuffer valuesb = new StringBuffer();

			Enumeration<DBColumnDisplayInfo> en = coldisplayinfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo dispinfo = (DBColumnDisplayInfo) en
						.nextElement();
				if (!dispinfo.isDbcolumn()
						|| !validDbcolumntype(dispinfo.getColtype())
						|| !dispinfo.isUpdateable())
					continue;

				String value = dbmodel.getItemValueWithoutformat(row, dispinfo
						.getColname());
				if (value != null) {
					if (colsb.length() > 0) {
						colsb.append(",");
						valuesb.append(",");
					}
					colsb.append(dispinfo.getColname());

					if (dispinfo.getSeqname().length() > 0
							&& value.length() == 0) {
						value = getSeqvalue(con, dispinfo.getSeqname());
					}
					valuesb.append(buildColumnSqlvar(dispinfo));
					value = buildColumnSqlvalue(dispinfo, value);
					valuevect.add(value);
					valuemap.put(dispinfo.getColname(), value);
					hascolmodified = true;
				}
			}
			sqlsb.append(colsb.toString());
			sqlsb.append(")values(");
			sqlsb.append(valuesb.toString());
			sqlsb.append(")");
			// System.out.println(sqlsb.toString());

		} else if (rec.getDbstatus() == RecordTrunk.DBSTATUS_MODIFIED) {
			sqlsb.append("update " + tablename + " set ");

			StringBuffer namevaluesb = new StringBuffer();

			Enumeration<DBColumnDisplayInfo> en = coldisplayinfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo dispinfo = (DBColumnDisplayInfo) en
						.nextElement();
				if (!validDbcolumntype(dispinfo.getColtype()))
					continue;
				String value = dbmodel.getItemValueWithoutformat(row, dispinfo
						.getColname());
				if (dispinfo.isIspk()) {
					// һ��Ҫ��������ֵ 20080725
					valuemap.put(dispinfo.getColname(), value);
					continue;
				}
				if (!dispinfo.isUpdateable()
						|| !dbmodel
								.isColumnmodified(row, dispinfo.getColname()))
					continue;

				if (namevaluesb.length() > 0) {
					namevaluesb.append(",");
				}
				namevaluesb.append(dispinfo.getColname() + "="
						+ buildColumnSqlvar(dispinfo));
				value = buildColumnSqlvalue(dispinfo, value);
				valuevect.add(value);
				valuemap.put(dispinfo.getColname(), value);
				hascolmodified = true;
			}
			sqlsb.append(namevaluesb.toString());

			en = coldisplayinfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo dispinfo = (DBColumnDisplayInfo) en
						.nextElement();
				if (!validDbcolumntype(dispinfo.getColtype()))
					continue;
				if (false == dispinfo.isIspk()) {
					continue;
				}
				if (wheresb.length() > 0)
					wheresb.append(" and ");
				wheresb.append(dispinfo.getColname() + "="
						+ buildColumnSqlvar(dispinfo));
				String value = dbmodel.getItemValueWithoutformat(row, dispinfo
						.getColname());
				valuevect.add(buildColumnSqlvalue(dispinfo, value));
			}
			sqlsb.append(" where " + wheresb.toString());
			// System.out.println(sqlsb.toString());

		} else if (rec.getDbstatus() == RecordTrunk.DBSTATUS_DELETE) {
			sqlsb.append("delete " + tablename);

			Enumeration<DBColumnDisplayInfo> en = coldisplayinfos.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo dispinfo = (DBColumnDisplayInfo) en
						.nextElement();
				if (!validDbcolumntype(dispinfo.getColtype()))
					continue;
				if (false == dispinfo.isIspk()) {
					continue;
				}
				String value = dbmodel.getItemValueWithoutformat(row, dispinfo
						.getColname());
				if (wheresb.length() > 0)
					wheresb.append(" and ");
				wheresb.append(dispinfo.getColname() + "="
						+ buildColumnSqlvar(dispinfo));
				value = buildColumnSqlvalue(dispinfo, value);
				valuevect.add(value);
				valuemap.put(dispinfo.getColname(), value);
			}
			hascolmodified = true;
			sqlsb.append(" where " + wheresb.toString());
			// System.out.println(sqlsb.toString());
		}

		String sql = sqlsb.toString();
		if (hascolmodified) {
			logger.info("Exec sql:" + sql);
			executeSql(con, sql, valuevect);
		}

		if (rec.getDbstatus() == RecordTrunk.DBSTATUS_DELETE) {
			rec.setDbdeleted(1);
			return rec;
		}

		// �������²�ѯ������¼
		StringBuffer columnsb = new StringBuffer();
		Enumeration<DBColumnDisplayInfo> en = coldisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dispinfo = (DBColumnDisplayInfo) en
					.nextElement();
			if (!dispinfo.isDbcolumn() /* || !dispinfo.isUpdateable() */) {
				continue;
			}
			if (columnsb.length() > 0) {
				columnsb.append(",");
			}
			columnsb.append(dispinfo.getColname());
		}

		Vector<String> keycolumnvalue = new Vector<String>();
		wheresb.delete(0, wheresb.length());
		en = coldisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dispinfo = (DBColumnDisplayInfo) en
					.nextElement();
			if (dispinfo.isDbcolumn() && dispinfo.isIspk()) {
				if (wheresb.length() > 0) {
					wheresb.append(" and ");
				}
				wheresb.append(dispinfo.getColname() + "="
						+ buildColumnSqlvar(dispinfo));
				keycolumnvalue
						.add((String) valuemap.get(dispinfo.getColname()));
			}
		}
		sql = "select " + columnsb.toString() + " from " + viewname + " where "
				+ wheresb;
		logger.debug("write db ok ,reselect sql:" + sql);

		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			for (int c = 0; c < keycolumnvalue.size(); c++) {
				c1.setString(c + 1, keycolumnvalue.elementAt(c));
			}
			ResultSet rs = c1.executeQuery();
			RecordTrunk newrec = rec.copy();
			if (rs.next()) {

				en = coldisplayinfos.elements();
				for (int c = 0; en.hasMoreElements(); c++) {
					DBColumnDisplayInfo dispinfo = (DBColumnDisplayInfo) en
							.nextElement();
					if (!dispinfo.isDbcolumn()) {
						continue;
					}
					String colname = dispinfo.getColname();
					String dbv = "";
					try {
						dbv = rs.getString(colname);
					} catch (SQLException e) {
						logger.error("ERROR", e);
						continue;
					}
					if (dispinfo.getColtype().equalsIgnoreCase("date")) {
						if (dbv != null && dbv.length() > 19) {
							dbv = dbv.substring(0, 19);
						}
					}
					newrec.setElementAt(dbv, c);
					newrec.dbvalues.setElementAt(dbv, c);
				}

			}
			newrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);
			return newrec;
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}

	/**
	 * ���ɱ���������number��varchar����"?" ��date���ͷ���to_date(?,'yyyy-mm-dd hh24:mi:ss')
	 * 
	 * @param info
	 * @return
	 */
	static String buildColumnSqlvar(DBColumnDisplayInfo info) {
		if (info.getColtype().equalsIgnoreCase("date")) {
			return "to_date(?,'yyyy-mm-dd hh24:mi:ss')";
		} else {
			return "?";
		}
	}

	/**
	 * ���ɱ���ֵ��number��varchar����ֵ��date���ͷ���ֵ+" 00:00:00"
	 * 
	 * @param info
	 * @param value
	 * @return
	 */
	static String buildColumnSqlvalue(DBColumnDisplayInfo info, String value) {
		if (info.getColtype().equalsIgnoreCase("date")) {
			if (value.startsWith("0000-00-00")) {
				value = "";
			} else if (value.length() == 10) {
				value = value + " 00:00:00";
			}
		}
		return value;
	}

	/**
	 * ����ǲ��ǿ��Դ�������ͣ�ֻ�ܴ���number varchar��date
	 * 
	 * @param coltype
	 * @return
	 */
	static boolean validDbcolumntype(String coltype) {
		if (coltype.equalsIgnoreCase("number")
				|| coltype.equalsIgnoreCase("varchar")
				|| coltype.equalsIgnoreCase("date"))
			return true;
		return false;
	}

	/**
	 * �����ܵ�ϸĿ
	 * 
	 * @param con
	 *            ����
	 * @param userruninfo
	 *            �û���Ϣ
	 * @param tablename
	 *            �ܵ�����
	 * @param viewname
	 *            �ܵ���ͼ��
	 * @param masterdbmodel
	 *            �ܵ�����Դ
	 * @param masterrelatecolname
	 *            �ܵ���������
	 * @param detailtablename
	 *            ϸ������
	 * @param detailviewname
	 *            ϸ����ͼ��
	 * @param detaildbmodel
	 *            ϸ������Դ
	 * @param detailrelatecolname
	 *            ϸ����������
	 * @param saveif
	 *            �ܵ�ϸĿ����ص��ӿ�
	 * @param results
	 *            �����������Ӧ�÷ֱ𷵻��ܵ���ϸ���Ľ������
	 * @param commit
	 *            true:һ���ܵ������ϸ������ɹ����ύ��false���ύ
	 * @throws Exception
	 */
	public static void save2DB(Connection con, Userruninfo userruninfo,
			String tablename, String viewname, DBTableModel masterdbmodel,
			String masterrelatecolname, String detailtablename,
			String detailviewname, DBTableModel detaildbmodel,
			String detailrelatecolname, MdesaveIF saveif,
			Vector<ResultCommand> results, boolean commit) throws Exception {

		// ���ܵ���tmppkid��ѯϸ����relatevalue���ҵ�����б���

		// �ȱ����ܵ����ٱ���ϸ��
		ResultCommand masterresult = new ResultCommand();
		ResultCommand detailresult = new ResultCommand();
		results.add(masterresult);
		results.add(detailresult);
		for (int i = 0; i < masterdbmodel.getRowCount(); i++) {
			RecordTrunk masterrec = masterdbmodel.getRecordThunk(i);
			RecordTrunk savedrec = null;
			ResultCommand tmpdetailresult = new ResultCommand();
			if (masterrec.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
				// ������,�ȴ��ܵ�,���ϸ��
				savedrec = saveMaster(con, userruninfo, tablename, viewname,
						masterdbmodel, saveif, i, masterresult);
				// �������ɹ�������ϸ��
				if (0 != savedrec.getSaveresult()) {
					continue;
				}
				// �������Ľ�����õ�masterrec��.by wwh 20080905
				masterrec.setSaveresult(savedrec.getSaveresult(), savedrec
						.getSavemessage());

				// ����ϸ��
				boolean dtlret = saveDetail(con, userruninfo, masterdbmodel,
						masterrelatecolname, detailtablename, detailviewname,
						detaildbmodel, detailrelatecolname, saveif, i,
						savedrec, tmpdetailresult);

				boolean dtlhaserror = false;
				for (int r = 0; r < tmpdetailresult.getLineresultCount(); r++) {
					RecordTrunk dtlrec = tmpdetailresult.getLineresult(r);
					if (dtlrec.getSaveresult() != 0) {
						dtlhaserror = true;
						break;
					}
				}
				if (dtlhaserror) {
					masterrec.setSaveresult(1, "ϸ���д���,�ܵ�û�б���");
					masterresult.addLineResult(masterrec);
				} else {
					masterresult.addLineResult(savedrec);
				}
				detailresult.append(tmpdetailresult);

			} else {
				// �޸Ļ�ɾ����,�ȴ�ϸ��
				saveDetail(con, userruninfo, masterdbmodel,
						masterrelatecolname, detailtablename, detailviewname,
						detaildbmodel, detailrelatecolname, saveif, i,
						masterrec, tmpdetailresult);
				// ����д���Ͳ�Ҫ�����ܵ���
				boolean dtlhaserror = false;
				for (int r = 0; r < tmpdetailresult.getLineresultCount(); r++) {
					RecordTrunk dtlrec = tmpdetailresult.getLineresult(r);
					if (dtlrec.getSaveresult() != 0) {
						dtlhaserror = true;
						break;
					}
				}
				if (dtlhaserror) {
					masterrec.setSaveresult(1, "ϸ���д���,�ܵ�û�б���");
					masterresult.addLineResult(masterrec);
					detailresult.append(tmpdetailresult);
					continue;
				}

				// �����ܵ�
				savedrec = saveMaster(con, userruninfo, tablename, viewname,
						masterdbmodel, saveif, i, masterresult);
				// �������Ľ�����õ�masterrec��.by wwh 20080905
				masterrec.setSaveresult(savedrec.getSaveresult(), savedrec
						.getSavemessage());
				masterresult.addLineResult(savedrec);
				detailresult.append(tmpdetailresult);

			}

			// �����ܵ������
			if (saveif != null) {
				DBTableModel saveddtldbmodel = detaildbmodel.copyStruct();
				Enumeration<RecordTrunk> en = tmpdetailresult.getLineresults()
						.elements();
				while (en.hasMoreElements()) {
					RecordTrunk saveddtlrec = (RecordTrunk) en.nextElement();
					saveddtldbmodel.appendRecord(saveddtlrec);
				}

				DBTableModel savedmasterdbmodel = masterdbmodel.copyStruct();
				Enumeration<RecordTrunk> masteren = masterresult.lineresults
						.elements();
				while (masteren.hasMoreElements()) {
					savedmasterdbmodel.appendRecord(masteren.nextElement());
				}
				saveif.on_aftersavemaster(con, userruninfo, savedmasterdbmodel,
						i, saveddtldbmodel);

				if (masterrec.getSaveresult() == 0) {
					// �����ܵ�������
					String pkcolname = masterdbmodel.getPkcolname();
					if (pkcolname != null && pkcolname.length() > 0) {
						String pkvalue = savedmasterdbmodel.getItemValue(0, pkcolname);
						//WfEngine.getInstance()
						//		.newQueue(con, tablename, pkvalue);
					}
				}
			}

			if (commit) {
				if (masterrec.getSaveresult() != 0) {
					con.rollback();
				} else {
					con.commit();
				}
			}
		}// master row loop

		for (int i = 0; i < masterresult.getLineresultCount(); i++) {
			RecordTrunk lr = masterresult.getLineresult(i);
			if (lr.getSaveresult() != 0) {
				masterresult.setResult(ResultCommand.RESULT_FAILURE);
				masterresult.setMessage("���ܵ�û�б���ɹ�");
			}
		}
		for (int i = 0; i < detailresult.getLineresultCount(); i++) {
			RecordTrunk lr = detailresult.getLineresult(i);
			if (lr.getSaveresult() != 0) {
				detailresult.setResult(ResultCommand.RESULT_FAILURE);
				detailresult.setMessage("��ϸ��û�б���ɹ�");
			}
		}

		return;
	}

	/**
	 * �ڲ�����,�����ܵ�
	 * 
	 * @param con
	 * @param userruninfo
	 * @param tablename
	 * @param viewname
	 * @param masterdbmodel
	 * @param saveif
	 * @param masterrow
	 * @param masterresult
	 * @return
	 */
	static RecordTrunk saveMaster(Connection con, Userruninfo userruninfo,
			String tablename, String viewname, DBTableModel masterdbmodel,
			MdesaveIF saveif, int masterrow, ResultCommand masterresult) {
		RecordTrunk masterrec = masterdbmodel.getRecordThunk(masterrow);
		RecordTrunk savedrec = null;
		try {
			if (saveif != null) {
				saveif.on_beforesavemaster(con, userruninfo, masterdbmodel,
						masterrow);
			}
			Vector<DBColumnDisplayInfo> coldisplayinfos = masterdbmodel
					.getDisplaycolumninfos();
			savedrec = buildExecSql(con, tablename, viewname, coldisplayinfos,
					masterdbmodel, masterrow);
			savedrec.setSaveresult(0, "����ɹ�");
			savedrec.setDbstatus(RecordTrunk.DBSTATUS_SAVED);

			// masterresult.addLineResult(savedrec);
		} catch (Exception e) {
			logger.error("�����ܵ�ʧ��", e);
			savedrec = masterrec;
			savedrec.setSaveresult(1, "����ʧ��:" + e.getMessage());
			masterresult.addLineResult(savedrec);
		}
		return savedrec;
	}

	/**
	 * ����ϸ��
	 * 
	 * @param con
	 * @param userruninfo
	 * @param viewname
	 * @param viewname
	 * @param masterdbmodel
	 * @param masterrelatecolname
	 * @param detailtablename
	 * @param detailviewname
	 * @param detaildbmodel
	 * @param detailrelatecolname
	 * @param saveif
	 * @param results
	 * @param commit
	 * @return trueû�д� false�д�
	 * @throws Exception
	 */
	static boolean saveDetail(Connection con, Userruninfo userruninfo,
			DBTableModel masterdbmodel, String masterrelatecolname,
			String detailtablename, String detailviewname,
			DBTableModel detaildbmodel, String detailrelatecolname,
			MdesaveIF saveif, int masterrow, RecordTrunk masterrec,
			ResultCommand detailresult) throws Exception {

		Vector<DBColumnDisplayInfo> detailcoldisplayinfos = detaildbmodel
				.getDisplaycolumninfos();
		int mastercol = masterdbmodel.getColumnindex(masterrelatecolname);
		int detailcol = detaildbmodel.getColumnindex(detailrelatecolname);

		String masterv = masterrec.getValueAt(mastercol);
		DBTableModel tmpdbdetail = new DBTableModel(detailcoldisplayinfos);
		String tmppkid = masterdbmodel.getTmppkid(masterrow);
		// ��master tmmpkid��ѯϸ��
		for (int r = 0; r < detaildbmodel.getRowCount(); r++) {
			RecordTrunk detailrec = detaildbmodel.getRecordThunk(r);
			if (!detailrec.getRelatevalue().equals(tmppkid)) {
				continue;
			}
			detailrec.setValueAt(detailcol, masterv);
			tmpdbdetail.addRecord(detailrec);
		}

		// ����ϸ��
		ResultCommand tmpdetailresult = saveDtl2DB(con, userruninfo,
				detailtablename, detailviewname, detailcoldisplayinfos,
				tmpdbdetail, saveif);
		for (int i = 0; i < tmpdetailresult.getLineresultCount(); i++) {
			detailresult.addLineResult(tmpdetailresult.getLineresult(i));
		}
		return tmpdetailresult.getResult() == 0;
	}

	public static String getSysdatetime(Connection con) throws Exception {
		String sql = "select to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') from dual";
		SelectHelper sh = new SelectHelper(sql);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		return dm.getItemValue(0, 0);
	}
}
