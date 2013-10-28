package com.smart.server.server.sysproc;

import java.sql.Connection;

import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;

public class SerialnoHelper {
	/**
	 * ȡ�ⲿ���к�
	 * 
	 * @param con
	 * @param serialnoid
	 * @param busidate
	 *            ���ڱ�����yyyy-mm-dd ��ʽ
	 * @return
	 * @throws Exception
	 */
	public static String getSerialno(Connection con, String serialnoid,
			String busidate, String entryid) throws Exception {
		String sql = "select * from pub_serialno where serialnoid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(serialnoid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		if (dm.getRowCount() == 0)
			throw new Exception("�Ҳ����ⲿ���к�serialnoid=" + serialnoid);
		String strusemm = getUsemm(con, busidate, entryid);
		int usemm = Integer.parseInt(strusemm);
		int y = usemm / 12;
		int m = usemm % 12;
		if (m == 0) {
			y--;
			m = 12;
		}
		String sy = busidate.substring(0, 4);
		String sm = busidate.substring(5, 7);
		String sd = busidate.substring(8, 10);

		String template = dm.getItemValue(0, "template");
		String resettype = dm.getItemValue(0, "resettype");
		String currentvalue = "";
		if (resettype.equals("1")) {
			// ������ϸ��
			currentvalue = queryDtlcurrentvalue(con, serialnoid, String
					.valueOf(y), "", "");
		} else if (resettype.equals("2")) {
			// ������ϸ��
			currentvalue = queryDtlcurrentvalue(con, serialnoid, String
					.valueOf(y), String.valueOf(m), "");
		} else if (resettype.equals("3")) {
			currentvalue = queryDtlcurrentvalue(con, serialnoid, sy, sm, sd);
		} else {
			currentvalue = queryDtlcurrentvalue(con, serialnoid, "", "", "");
		}

		// �������滻template�е����ں����
		String value = template;
		// �滻��
		value = replaceTemplate(value, "{��}", formatLength(y, 4));
		value = replaceTemplate(value, "{����}", formatLength(y, 4)
				+ formatLength(m, 2));
		value = replaceTemplate(value, "{������}", sy + sm + sd);
		value = replaceSequence(value, currentvalue);

		return value;
	}

	static String queryDtlcurrentvalue(Connection con, String serialnoid,
			String y, String m, String d) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("select serialnodtlid,currentvalue+1 currentvalue "
				+ " from pub_serialno_dtl where serialnoid=? \n");
		if (y.length() > 0) {
			sb.append(" and serialnoyear=? \n");
		}
		if (m.length() > 0) {
			sb.append(" and serialnomonth=? \n");
		}
		if (d.length() > 0) {
			sb.append(" and serialnoday=? \n");
		}
		SelectHelper sh = new SelectHelper(sb.toString());
		sh.bindParam(serialnoid);
		if (y.length() > 0) {
			sh.bindParam(y);
		}
		if (m.length() > 0) {
			sh.bindParam(m);
		}
		if (d.length() > 0) {
			sh.bindParam(d);
		}
		DBTableModel dtlmodel = sh.executeSelect(con, 0, 1);
		if (dtlmodel.getRowCount() == 1) {
			String serialnodtlid = dtlmodel.getItemValue(0, "serialnodtlid");
			// ֵҪ��1
			String sql = "update pub_serialno_dtl set currentvalue=nvl(currentvalue,0)+1 where serialnodtlid=?";
			UpdateHelper uh = new UpdateHelper(sql);
			uh.bindParam(serialnodtlid);
			uh.executeUpdate(con);
			return dtlmodel.getItemValue(0, "currentvalue");
		}

		// ����
		String serialnodtlid = DBModel2Jdbc.getSeqvalue(con,
				"pub_serialno_dtl_seq");
		InsertHelper ih = new InsertHelper("pub_serialno_dtl");
		ih.bindParam("serialnoid", serialnoid);
		ih.bindParam("serialnodtlid", serialnodtlid);
		ih.bindParam("serialnoyear", y);
		ih.bindParam("serialnomonth", m);
		ih.bindParam("serialnoday", d);
		ih.bindParam("currentvalue", "1");
		ih.executeInsert(con);
		// ����1
		return "1";
	}

	static String formatLength(int n, int len) {
		String v = String.valueOf(n);
		while (v.length() < len) {
			v = "0" + v;
		}
		return v;
	}

	static String replaceTemplate(String value, String src, String target) {
		int p = 0;
		for (;;) {
			p = value.indexOf(src);
			if (p < 0)
				break;

			value = value.substring(0, p) + target
					+ value.substring(p + src.length());
		}
		return value;
	}

	/**
	 * �����
	 * 
	 * @param value
	 * @param currentvalue
	 * @return
	 */
	static String replaceSequence(String value, String currentvalue) {
		int p = 0;
		for (;;) {
			p = value.indexOf("{���");
			if (p < 0)
				break;
			int p1 = value.indexOf("}", p + 1);
			if (p1 < 0)
				break;
			String seqname = value.substring(p + 1, p1);
			// ��û�ж���?
			int p2 = seqname.indexOf(",");
			if (p2 < 0) {
				value = value.substring(0, p) + currentvalue
						+ value.substring(p1 + 1);
			} else {
				// λ��
				int len = 0;
				try {
					len = Integer.parseInt(seqname.substring(p2 + 1));
				} catch (Exception e) {
				}
				value = value.substring(0, p)
						+ formatLength(Integer.parseInt(currentvalue), len)
						+ value.substring(p1 + 1);
			}

		}
		return value;
	}

	public static String getUsemm(Connection con, String credate, String entryid)
			throws Exception {
		String sql = "select usemm from pub_settle_account_v where to_date(?,'yyyy-mm-dd')>startdate \n"
				+ " and to_date(?,'yyyy-mm-dd')<=enddate and entryid=?";

		SelectHelper sh = new SelectHelper(sql);
		if (credate.length() > 10)
			credate = credate.substring(0, 10);
		sh.bindParam(credate);
		sh.bindParam(credate);
		sh.bindParam(entryid);
		DBTableModel dm = sh.executeSelect(con, 0, 1);
		if (dm.getRowCount() == 0) {
			throw new Exception("û���ҵ�" + credate + "����Ӧ���߼���");
		}
		return dm.getItemValue(0, 0);
	}

	public static void main(String[] args) {
		String template = "��{����}-{����}-{���,10}";
		String v = replaceTemplate(template, "{����}", "2008"
				+ formatLength(1, 2));
		v = replaceSequence(template, "135");
		System.out.println(v);
	}
}
