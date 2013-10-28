package com.smart.platform.server.process;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.*;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;
import com.smart.platform.rule.define.Rulebase;
import com.smart.platform.rule.enginee.Ruleenginee;
import com.smart.platform.rule.setup.RuleRepository;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.server.StesaveIF;
import com.smart.platform.server.UpdateLogger;
import com.smart.platform.util.DecimalHelper;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.ZipHelper;
import com.smart.server.server.sysproc.CurrentappHelper;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.util.Enumeration;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;

import org.apache.log4j.Category;

/**
 * ����༭������������
 */
public abstract class SteProcessor extends RequestProcessorAdapter implements
		StesaveIF {
	protected Category logger = Category.getInstance(SteProcessor.class);

	/**
	 * ����༭CSteModel
	 */
	protected CSteModel stemodel = null;

	/**
	 * �Ƿ��Ѽ��ع�ר���ˡ�
	 */
	private boolean zxinited = false;

	public SteProcessor() {
		stemodel = getSteModel();
	}


	/**
	 * ������
	 * 
	 * @param userinfo
	 *            ��ǰ�û���Ϣ
	 * @param req
	 *            �ͻ�������
	 * @param resp
	 *            ��������Ӧ
	 */
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		CommandBase cmd = req.commandAt(0);
		if (!(cmd instanceof StringCommand)) {
			return -1;
		}

		StringCommand strcmd = (StringCommand) cmd;
		if (!strcmd.getString().equals(stemodel.getSaveCommandString())) {
			return -1;
		}

		DataCommand cmd2 = (DataCommand) req.commandAt(1);
		DBTableModel dbmodel = cmd2.getDbmodel();

		Connection con = null;
		ResultCommand resultcmd = null;
		try {
			con = getConnection();
			Vector<String> updatelogs = new Vector<String>();
			if (UpdateLogger.getInstance().isNeeduploadlog(con, getTablename())) {
				for (int r = 0; r < dbmodel.getRowCount(); r++) {
					String s = UpdateLogger.createLogstring(dbmodel, r);
					updatelogs.add(s);
				}
			}
			resultcmd = doSave(con, userinfo, dbmodel, true);
			if (UpdateLogger.getInstance().isNeeduploadlog(con, getTablename())) {
				String pkcolname=dbmodel.getPkcolname();
				for (int r = 0; r < resultcmd.getLineresultCount(); r++) {
					int saveresult = resultcmd.getLineresult(r).getSaveresult();
					if (saveresult == 0) {
						// ����ɹ�,���Լ�¼��־��.
						String pkvalue=dbmodel.getItemValue(r, pkcolname);
						UpdateLogger.addLog(con, getTablename(), userinfo
								.getUserid(), userinfo.getUsername(),
								updatelogs.elementAt(r),pkvalue);
					}
				}
			}
			con.commit();
			resp.addCommand(resultcmd);
		} catch (Exception e) {
			con.rollback();
			logger.error("save", e);
			resp.addCommand(new StringCommand("-ERROR����ʧ��:" + e.getMessage()));
			return 0;
		} finally {
			if (con != null) {
				con.close();
			}
		}

		return 0;
	}

	/**
	 * 
	 * @param con
	 *            ����
	 * @param userinfo
	 *            �û���Ϣ
	 * @param dbmodel
	 *            ����Դ
	 * @param commit
	 *            true��ÿһ����¼����ɹ����ύ��false���ύ
	 * @return ���
	 * @throws Exception
	 */
	public ResultCommand doSave(Connection con, Userruninfo userinfo,
			DBTableModel dbmodel, boolean commit) throws Exception {
		Vector<DBColumnDisplayInfo> coldisplayinfos = stemodel
				.getFormcolumndisplayinfos();
		String tablename = getTablename();
		ResultCommand resultcmd = DBModel2Jdbc.save2DB(con, userinfo,
				tablename, stemodel.getTablename(), coldisplayinfos, dbmodel,
				this, commit);
		if (userinfo.isDevelop()) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintWriter out = null;
			try {
				out = new PrintWriter(new OutputStreamWriter(bout, "gbk"));
			} catch (UnsupportedEncodingException e) {
				logger.error("error", e);
			}
			selfCheck(con, userinfo, resultcmd, out);
			out.flush();
			String checks = new String(bout.toByteArray(), "gbk");
			if (checks.length() > 0) {
				throw new Exception(this.getClass().getName() + "�Լ�ʧ��:"
						+ checks);
			}
		}

		return resultcmd;

	}

	/**
	 * ���ص���༭CSteModel,�������صĺ���
	 * 
	 * @return
	 */
	protected abstract CSteModel getSteModel();

	/**
	 * ����Ҫ����ı���(��������ͼ��),�������صĺ���
	 * 
	 * @return
	 */
	protected abstract String getTablename();

	/**
	 * ���ص���༭CSteModelʵ��,�����ظ�����
	 * 
	 * @return
	 */
	public CSteModel getStemodelInst() {
		return stemodel;
	}

	/**
	 * Ϊ��selfcheck�ӵ�
	 * 
	 * @return
	 */
	public String getDbtablename() {
		return getTablename();
	}

	/**
	 * ����ǰ����
	 * 
	 * @param con
	 *            ���ݿ�����
	 * @param userrininfo
	 *            ��ǰ�û���Ϣ
	 * @param dbmodel
	 *            �ͻ����ύ������
	 * @param row
	 *            ��ǰ�����dbmodel����
	 */
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
	}

	/**
	 * �������
	 * 
	 * @param con
	 *            ���ݿ�����
	 * @param userrininfo
	 *            ��ǰ�û���Ϣ
	 * @param dbmodel
	 *            �ͻ����ύ������
	 * @param row
	 *            ��ǰ�����dbmodel����
	 */
	public void on_aftersave(Connection con, Userruninfo userrininfo,
			DBTableModel saveddbmodel, int row) throws Exception {

		// �Ƿ���ר�
		String opid = userrininfo.getActiveopid();
		if (!zxinited && opid != null && opid.length() > 0) {
			File zxzipfile = new File(CurrentappHelper.getClassesdir(), "ר���/"
					+ opid + ".zip");
			if (zxzipfile.exists()) {
				// ��zxfile���ҳ�ste.model
				File tempfile = null;
				BufferedReader rd = null;
				try {
					tempfile = File.createTempFile("temp", ".model");
					if (ZipHelper.extractFile(zxzipfile, "ste.rule", tempfile)) {
						rd = null;
						rd = DBColumnInfoStoreHelp.getReaderFromFile(tempfile);
						Vector<Rulebase> rules = RuleRepository.loadRules(rd);
						rd.close();
						Ruleenginee ruleeng = new Ruleenginee();
						ruleeng.setRuletable(rules);
						stemodel.setRuleeng(ruleeng);
						zxinited = true;
					}
				} catch (Exception e) {
					logger.error("e", e);
				} finally {
					if (tempfile != null) {
						tempfile.delete();
					}
				}
			}
		}

		String procname = stemodel.getStoreprocname();
		if (procname == null || procname.length() == 0)
			return;
		System.out.println("procname=" + procname);

		DBTableModel dbmodel = stemodel.getDBtableModel();
		String pkcolname = null;
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.isIspk()) {
				pkcolname = colinfo.getColname();
				break;
			}
		}
		if (pkcolname == null) {
			throw new Exception("û�����������У�����޷����ú���洢����" + procname);
		}

		CallableStatement call = null;
		try {
			String sql = "{call " + procname + "(?,?,?)}";
			call = con.prepareCall(sql);
			call.setString(1, saveddbmodel.getItemValue(row, pkcolname));
			call.setString(2, userrininfo.getUserid());
			call.setString(3, userrininfo.getRoleid());
			call.execute();
		} finally {
			if (call != null) {
				call.close();
			}
		}
	}

	/**
	 * �Լ����
	 * 
	 * @param userinfo
	 *            ��ǰ�û���Ϣ
	 * @param resultcmd
	 *            �����, ���б���������ݼ�¼.
	 */
	protected void selfCheck(Connection con, Userruninfo userinfo,
			ResultCommand resultcmd, PrintWriter out) {
		for (int i = 0; i < resultcmd.getLineresultCount(); i++) {
			RecordTrunk dtlrec = resultcmd.getLineresult(i);
			if (dtlrec.getSaveresult() == 0) {
				DBTableModel dbmdel = stemodel.getDBtableModel().copyStruct();
				dbmdel.appendRecord(dtlrec);
				selfCheckOne(con, userinfo, dbmdel, out);
			}

		}
	}

	/**
	 * ���һ����¼
	 * 
	 * @param userinfo
	 *            ��ǰ�û���Ϣ
	 * @param dbmdel
	 *            ֻ��һ�б���������ݼ�¼.
	 * @param out
	 *            ���������Ϣ
	 */
	protected void selfCheckOne(Connection con, Userruninfo userinfo,
			DBTableModel dbmdel, PrintWriter out) {

	}

	/**
	 * ��� cname1��ֵ ���� cname2��ֵ = cname3��ֵ
	 * 
	 * @param rec
	 * @param dbmodel
	 * @param cname1
	 * @param cname2
	 * @param cname3
	 * @param scale
	 * @throws Exception
	 *             protected void assertMultiEqual(RecordTrunk rec, DBTableModel
	 *             dbmodel, String cname1, String cname2, String cname3, int
	 *             scale, PrintWriter out) { int c1 =
	 *             dbmodel.getColumnindex(cname1); int c2 =
	 *             dbmodel.getColumnindex(cname2); int c3 =
	 *             dbmodel.getColumnindex(cname3);
	 * 
	 *             BigDecimal d1 = DecimalHelper.toDec(rec.getdbValueAt(c1));
	 *             BigDecimal d2 = DecimalHelper.toDec(rec.getdbValueAt(c2));
	 *             BigDecimal d3 = DecimalHelper.toDec(rec.getdbValueAt(c3));
	 * 
	 *             MathContext mc = new MathContext(scale,
	 *             RoundingMode.HALF_UP); BigDecimal mult = d1.multiply(d2, mc);
	 * 
	 *             if (mult.compareTo(d3) != 0) { out.println(cname1 + "����" +
	 *             cname2 + "������" + cname3 + ",Ӧ����" + mult.toPlainString() +
	 *             ",ʵ����" + d3.toPlainString()); } }
	 */

}
