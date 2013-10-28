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
 * 单表编辑服务器处理类
 */
public abstract class SteProcessor extends RequestProcessorAdapter implements
		StesaveIF {
	protected Category logger = Category.getInstance(SteProcessor.class);

	/**
	 * 单表编辑CSteModel
	 */
	protected CSteModel stemodel = null;

	/**
	 * 是否已加载过专项了。
	 */
	private boolean zxinited = false;

	public SteProcessor() {
		stemodel = getSteModel();
	}


	/**
	 * 处理函数
	 * 
	 * @param userinfo
	 *            当前用户信息
	 * @param req
	 *            客户端请求
	 * @param resp
	 *            服务器响应
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
						// 保存成功,可以记录日志了.
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
			resp.addCommand(new StringCommand("-ERROR保存失败:" + e.getMessage()));
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
	 *            连接
	 * @param userinfo
	 *            用户信息
	 * @param dbmodel
	 *            数据源
	 * @param commit
	 *            true：每一条记录保存成功就提交；false不提交
	 * @return 结果
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
				throw new Exception(this.getClass().getName() + "自检失败:"
						+ checks);
			}
		}

		return resultcmd;

	}

	/**
	 * 返回单表编辑CSteModel,必须重载的函数
	 * 
	 * @return
	 */
	protected abstract CSteModel getSteModel();

	/**
	 * 返回要处理的表名(不能是视图名),必须重载的函数
	 * 
	 * @return
	 */
	protected abstract String getTablename();

	/**
	 * 返回单表编辑CSteModel实例,避免重复创建
	 * 
	 * @return
	 */
	public CSteModel getStemodelInst() {
		return stemodel;
	}

	/**
	 * 为了selfcheck加的
	 * 
	 * @return
	 */
	public String getDbtablename() {
		return getTablename();
	}

	/**
	 * 保存前处理
	 * 
	 * @param con
	 *            数据库连接
	 * @param userrininfo
	 *            当前用户信息
	 * @param dbmodel
	 *            客户端提交的数据
	 * @param row
	 *            当前处理的dbmodel的行
	 */
	public void on_beforesave(Connection con, Userruninfo userrininfo,
			DBTableModel dbmodel, int row) throws Exception {
	}

	/**
	 * 保存后处理
	 * 
	 * @param con
	 *            数据库连接
	 * @param userrininfo
	 *            当前用户信息
	 * @param dbmodel
	 *            客户端提交的数据
	 * @param row
	 *            当前处理的dbmodel的行
	 */
	public void on_aftersave(Connection con, Userruninfo userrininfo,
			DBTableModel saveddbmodel, int row) throws Exception {

		// 是否有专项？
		String opid = userrininfo.getActiveopid();
		if (!zxinited && opid != null && opid.length() > 0) {
			File zxzipfile = new File(CurrentappHelper.getClassesdir(), "专项开发/"
					+ opid + ".zip");
			if (zxzipfile.exists()) {
				// 从zxfile中找出ste.model
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
			throw new Exception("没定定义主键列，因此无法调用后处理存储过程" + procname);
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
	 * 自检程序
	 * 
	 * @param userinfo
	 *            当前用户信息
	 * @param resultcmd
	 *            保存后, 所有被处理的数据记录.
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
	 * 检查一条记录
	 * 
	 * @param userinfo
	 *            当前用户信息
	 * @param dbmdel
	 *            只有一行被处理的数据记录.
	 * @param out
	 *            输出错误信息
	 */
	protected void selfCheckOne(Connection con, Userruninfo userinfo,
			DBTableModel dbmdel, PrintWriter out) {

	}

	/**
	 * 检查 cname1列值 乘以 cname2列值 = cname3列值
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
	 *             if (mult.compareTo(d3) != 0) { out.println(cname1 + "乘以" +
	 *             cname2 + "不等于" + cname3 + ",应该是" + mult.toPlainString() +
	 *             ",实际是" + d3.toPlainString()); } }
	 */

}
