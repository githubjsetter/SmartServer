package com.smart.platform.gui.mde;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.smart.client.download.DownloadManager;
import com.smart.client.system.Clientframe;
import com.smart.extension.ste.ZxmodifyUploadHelper;
import com.smart.platform.auth.RunopManager;
import com.smart.platform.client.RemoteConnector;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ResultCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.env.Configer;
import com.smart.platform.filedb.CurrentdirHelper;
import com.smart.platform.gui.control.CButton;
import com.smart.platform.gui.control.CDefaultProgress;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CLabel;
import com.smart.platform.gui.control.CMessageDialog;
import com.smart.platform.gui.control.CProgressIF;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.Sumdbmodel;
import com.smart.platform.gui.ste.CModelBase;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.CSteModelListenerAdaptor;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;
import com.smart.platform.gui.ste.ExcelFileFilter;
import com.smart.platform.gui.ste.PrintSetupFrame;
import com.smart.platform.rule.define.Rulebase;
import com.smart.platform.rule.enginee.Ruleenginee;
import com.smart.platform.rule.setup.RuleRepository;
import com.smart.platform.rule.setup.RulesetupMaindialog;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.ExcelHelper;
import com.smart.platform.util.ZipHelper;
import com.smart.server.server.Server;

/**
 * 总单细目Model 包含总单mastermodel, 细单detailmodel,以及总单和细单的关联方法
 */
public abstract class CMdeModel extends CModelBase {
	public static final String ACTION_NEW = "新增";
	public static final String ACTION_MODIFY = "修改";
	public static final String ACTION_UNDO = "撤消修改";
	public static final String ACTION_UNDODTL = "撤消细单修改";
	public static final String ACTION_HIDEFORM = "隐藏";
	public static final String ACTION_DEL = "删除";
	public static final String ACTION_QUERY = "查询";
	public static final String ACTION_REFRESH = "刷新";
	public static final String ACTION_SAVE = "保存";
	public static final String ACTION_EXPORT = "导出";
	public static final String ACTION_EXPORTAS = "导出为";
	public static final String ACTION_EXPORTMASTERAS = "总单导出为";
	public static final String ACTION_EXPORTDETAILAS = "细单导出为";

	public static final String ACTION_NEXT = "下一行";
	public static final String ACTION_PRIOR = "上一行";
	public static final String ACTION_FIRST = "第一行";
	public static final String ACTION_LAST = "最后行";

	public static final String ACTION_NEWDTL = "新增细单";
	public static final String ACTION_MODIFYDTL = "修改细单";
	public static final String ACTION_DELDTL = "删除细单";

	public static final String ACTION_NEXTDTL = "下一行细单";
	public static final String ACTION_PRIORDTL = "上一行细单";
	public static final String ACTION_FIRSTDTL = "第一行细单";
	public static final String ACTION_LASTDTL = "最后行细单";

	public static final String ACTION_SETUPUI = "界面设置";
	public static final String ACTION_SAVEUI = "保存界面";

	public static final String ACTION_SETUPRULE = "规则设置";

	public static final String ACTION_SETUPUIDTL = "细单界面设置";
	public static final String ACTION_SAVEUIDTL = "细单保存界面";

	public static final String ACTION_PRINTSETUP = "打印设置";
	public static final String ACTION_PRINT = "打印";

	public static final String ACTION_SELECTOP = "选择功能";
	public static final String ACTION_EXIT = "退出功能";
	public static final String ACTION_SELFCHECK = "自检";
	public static final String ACTION_DOCPRINT_PREFIX = "DOCPRINT_";

	/**
	 * 总单Model
	 */
	protected CMasterModel mastermodel = null;

	/**
	 * 细单Model
	 */
	protected CDetailModel detailmodel = null;

	/**
	 * 总单关联列名,应该是主键列名
	 */
	protected String masterrelatecolname = "";

	/**
	 * 细单关联列名
	 */
	protected String detailrelatecolname = "";

	/**
	 * key是总单的临时主键,类型为String, 值为细单的DBTableModel
	 */
	protected HashMap<String, DBTableModel> detaildbmodelmap = new HashMap<String, DBTableModel>();

	Category logger = Category.getInstance(CMdeModel.class);

	/**
	 * 打印Frame
	 */
	private PrintSetupFrame printsetupfrm;

	/**
	 * 规则引擎 add by wwh 20071126
	 */
	protected Ruleenginee ruleeng = null;

	/**
	 * 是否进入专项开发调整
	 */
	protected boolean zxmodify = false;

	protected File zxzipfile = null;

	protected ZxmdejavaDelegate zxmdejavadelegate = null;

	/**
	 * 是不是行保存？
	 */
	protected boolean saveimmdiate = false;

	boolean usequerythread = true;

	/**
	 * 是否在新总单前要清掉已保存成功的
	 */
	protected boolean resetbeforenew = false;

	/**
	 * 细单查询线程锁定.防止一个总单tmppkid有多个线程查询.
	 */
	HashMap<String, QuerydtlThread> querydtlthreadmap = new HashMap<String, QuerydtlThread>();

	public CMdeModel() {
		super();
	}

	/**
	 * 构造
	 * 
	 * @param frame
	 *            窗口
	 * @param title
	 *            标题
	 */
	public CMdeModel(CFrame frame, String title) {
		this.frame = frame;
		this.title = title;
		if (frame != null) {
			this.setOpid(((MdeFrame) frame).getOpid());
		}

		initDelegate();
		loadRuleenginee();

		mastermodel = createMastermodel();
		mastermodel.addActionListener(new MasterModelListener());
		mastermodel.setRuleeng(ruleeng);

		detailmodel = createDetailmodel();
		detailmodel.addActionListener(new DetailModelListener());
		detailmodel.setRuleeng(ruleeng);

		masterrelatecolname = getMasterRelatecolname();
		detailrelatecolname = getDetailRelatecolname();

	}

	/**
	 * 加载delegate专项
	 */
	protected void initDelegate() {
		zxmdejavadelegate = ZxmdejavaDelegate.loadZxfromzxzip(this);
	}

	/**
	 * 返回总单Model
	 * 
	 * @return
	 */
	public CSteModel getMasterModel() {
		if (mastermodel == null) {
			mastermodel = createMastermodel();
		}
		return mastermodel;
	}

	/**
	 * 返回细单Model
	 * 
	 * @return
	 */
	public CSteModel getDetailModel() {
		if (detailmodel == null) {
			detailmodel = createDetailmodel();
		}
		return detailmodel;
	}

	/**
	 * 创建总单Model,必须重载
	 * 
	 * @return
	 */
	protected abstract CMasterModel createMastermodel();

	/**
	 * 创建细单Model,必须重载
	 * 
	 * @return
	 */
	protected abstract CDetailModel createDetailmodel();

	/**
	 * 返回总单关联列名,必须重载
	 * 
	 * @return
	 */
	public abstract String getMasterRelatecolname();

	/**
	 * 返回细单关联列名,必须重载
	 * 
	 * @return
	 */
	public abstract String getDetailRelatecolname();

	/**
	 * 查询细单.
	 * 使用了detaildbmodelmap做为细单的cache.取总单当前行的临时主键,查询cache.如果有则使用做为细单table的数据源,没有则
	 * 通过服务器查询细单数据,并放在cache中.
	 * 
	 * @param newrow
	 *            总单当前行
	 * @param oldrow
	 */
	protected void retrieveDetail(int newrow, int oldrow) {
		getDetailModel().setStatusmessage("");

		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_retrieveDetail(this, newrow);
			if (ret == 0) {
				return;
			}
		}

		// System.out.println("retrieveDetail,newrow=" + newrow + ",oldrow=" +
		// oldrow);
		if (newrow < 0 || newrow > mastermodel.getRowCount() - 1)
			return;

		getDetailModel().getTable().stopEdit();
		
		String tmppkid = mastermodel.getDBtableModel().getTmppkid(newrow);
		logger
				.debug("retrieve dtl,masterrow=" + newrow + ",tmppkid="
						+ tmppkid);
		DBTableModel olddetailmodel = (DBTableModel) detaildbmodelmap
				.get(tmppkid);
		if (olddetailmodel != null) {
			logger.debug("tmppkid=" + tmppkid + " olddetailmodel rowct="
					+ olddetailmodel.getRowCount());
			if (detailmodel.getDBtableModel() != olddetailmodel) {
				detailmodel.setDBtableModel(olddetailmodel);
				detailmodel.sort();
				if (detailmodel.getDBtableModel().getRowCount() > 0) {
					detailmodel.setRow(0);
				}
			}
			getDetailModel().setStatusmessage(
					"细单有" + olddetailmodel.getRowCount() + "条记录");
			return;
		}

		String value = mastermodel.getDBtableModel().getItemValue(newrow,
				masterrelatecolname);
		if (value.length() == 0) {
			detailmodel.recreateDBModel();
			cacheDetailMemds(tmppkid, detailmodel.getDBtableModel());
			return;
		}

		// 查询细单值
		int detailrow = detailmodel.getRow();
		if (detailmodel.getRowCount() > 0 && detailrow >= 0) {
			String detailvalue = detailmodel.getDBtableModel().getItemValue(
					detailrow, detailrelatecolname);
			if (detailvalue == null) {
				logger.error("细单找不到列名" + detailrelatecolname);
				return;
			}
			if (value.equals(detailvalue)) {
				// 说明现在细单表的值就是对的，不需要查询
				return;
			}
		}
		String wheres = getRetrievedetailWheres(value);

		// 20090109 不能有两个线程在启动状态.
		synchronized (querydtlthreadmap) {
			if (querydtlthreadmap.get(tmppkid) != null) {
				logger.debug("已有线程在查询总单tmppkid=" + tmppkid + ",返回");
				return;
			}
			for (;;) {
				if (querydtlthreadmap.size() > 0) {
					try {
						logger.debug("有线程在建立,等待.....");
						querydtlthreadmap.wait();
						continue;
					} catch (InterruptedException e) {
					}
				}
				logger.debug("准备创建线程,tmppkid=" + tmppkid);
				try {
					setWaitCursor();
					detailmodel.recreateDBModel();
					DBTableModel dtldm = detailmodel.getDBtableModel();
					QuerydtlThread dtlthread = new QuerydtlThread(newrow,
							tmppkid, wheres, dtldm);
					querydtlthreadmap.put(tmppkid, dtlthread);
					dtlthread.setDaemon(true);
					if (usequerythread) {
						dtlthread.start();
					} else {
						dtlthread.run();
					}
					break;
				} finally {
					setDefaultCursor();
				}
			}
		}

	}

	/**
	 * 增加QuerydtlThread,延时400ms,如果总单还是刚才的行,再进行查询. 防止操作人员快速滚动造成的多次无用查询
	 * 
	 * @author Administrator
	 * 
	 */
	class QuerydtlThread extends Thread {
		int memrow = -1;
		String tmppkid = null;
		String wheres;
		DBTableModel dtldm = null;

		public QuerydtlThread(int currow, String tmppkid, String wheres,
				DBTableModel dtldm) {
			this.memrow = currow;
			this.tmppkid = tmppkid;
			this.wheres = wheres;
			this.dtldm = dtldm;
		}

		public void run() {
			logger.debug("线程run()开始,tmppkid=" + tmppkid);
			try {
				if (usequerythread) {
					try {
						Thread.sleep(DefaultNPParam.mderetrievedtldeplay);
					} catch (InterruptedException e) {
					}
					int currow = getMasterModel().getRow();
					if (memrow != currow) {
						System.out.println("currow=" + currow + ",上次的row="
								+ memrow + ",所以不查询细单了");

						// 这里开放了
						logger.debug("tmppkid=" + tmppkid + ", 当前行移动太快,不需要查询");
						synchronized (querydtlthreadmap) {
							querydtlthreadmap.remove(tmppkid);
							querydtlthreadmap.notifyAll();
						}

						return;
					}
				}
				getDetailModel().setStatusmessage("开始查询细单....");
				cacheDetailMemds(tmppkid, dtldm);

				// 这里可以开放了
				logger.debug("tmppkid=" + tmppkid + ", 线程建立完毕");

				synchronized (querydtlthreadmap) {
					querydtlthreadmap.remove(tmppkid);
					querydtlthreadmap.notifyAll();
				}

				// 这里不需要再创新线程了啊
				detailmodel.setUsequerythread(false);
				detailmodel.doQuery(wheres, dtldm);
			} finally {
				synchronized (querydtlthreadmap) {
					querydtlthreadmap.remove(tmppkid);
					querydtlthreadmap.notifyAll();
				}
			}

		}

	}

	/**
	 * 将细单的数据源放在detaildbmodelmap中.
	 * 
	 * @param tmppkid
	 * @deprecated
	 */
	void cacheDetailMemds(String tmppkid) {
		DBTableModel detaildbmodel = this.detailmodel.getDBtableModel();
		logger.debug("cacheDetailMemds,tmppkid=" + tmppkid + ",detaildbmodel="
				+ detaildbmodel);
		detaildbmodelmap.put(tmppkid, detaildbmodel);
	}

	/**
	 * 将总单tmppkid,细单detaildbmodel放在detaildbmodelmap中.
	 * 
	 * @param tmppkid
	 * @param detaildbmodel
	 */
	void cacheDetailMemds(String tmppkid, DBTableModel detaildbmodel) {
		logger.debug("cacheDetailMemds,tmppkid=" + tmppkid + ",detaildbmodel="
				+ detaildbmodel);
		detaildbmodelmap.put(tmppkid, detaildbmodel);
	}

	/**
	 * @deprecated
	 */
	protected void retrieveMaster(int currow) {
		if (currow < 0)
			return;
		if (detailmodel.getdbStatus(currow) == RecordTrunk.DBSTATUS_NEW)
			return;
		String detailvalue = detailmodel.getDBtableModel().getItemValue(currow,
				detailrelatecolname);
		if (detailvalue == null) {
			logger.error("细单表找不到列名" + detailrelatecolname);
			return;
		}
		if (detailvalue.length() == 0) {
			return;
		}

		// 查询总单值
		int masterrow = mastermodel.getRow();
		if (masterrow >= 0) {
			String mastervalue = mastermodel.getDBtableModel().getItemValue(
					masterrow, masterrelatecolname);
			if (mastervalue == null) {
				logger.error("总单找不到列名" + mastervalue);
				return;
			}
			if (detailvalue.equals(mastervalue)) {
				// 说明现在总单表的值就是对的，不需要查询
				return;
			}
		}
		String coltype = mastermodel.getDBtableModel().getColumnDBType(
				masterrelatecolname);
		if (coltype == null) {
			logger.error("总单找不到列名" + detailrelatecolname);
			return;
		}

		String wheres = masterrelatecolname;
		if (coltype.equalsIgnoreCase("varchar")) {
			detailvalue = "'" + detailvalue + "'";
		}
		wheres += "=" + detailvalue;
		// System.out.println(wheres);
		mastermodel.doRetrieve(wheres);

	}

	/**
	 * 返回总单细单一共有多少记录被修改过
	 * 
	 * @return
	 */
	public int getModifiedRowCount() {
		int mastermodirow = mastermodel.getDBtableModel().getModifiedData()
				.getRowCount();
		int detailmodirow = 0;
		Iterator it = detaildbmodelmap.values().iterator();
		while (it.hasNext()) {
			DBTableModel dbmodel = (DBTableModel) it.next();
			detailmodirow += dbmodel.getModifiedData().getRowCount();
		}
		return mastermodirow + detailmodirow;
	}

	/**
	 * 是否允许没有细单
	 * 
	 * @return true可以没有细单 false不能没有细单
	 */
	protected boolean isAllownodetail() {
		return false;
	}

	/**
	 * 保存. 保存所有总单和细单. 按一条总单记录及相关细单全部保存成功为一个事务.
	 * 
	 * @return
	 */
	public int doSave() {
		if (ruleeng != null) {
			if (ruleeng.process(this, "屏蔽保存", 0) < 0)
				return -1;
		}
		mastermodel.commitEdit();
		detailmodel.commitEdit();

		if (!mastermodel.doHideform()) {
			return -1;
		}
		if (!detailmodel.doHideform()) {
			return -1;
		}

		/**
		 * 如果是自动保存。在doHideform()中就调用了slient保存，不需要再保存。
		 */
		if (saveimmdiate) {
			return 0;
		}

		if (0 != on_beforesave()) {
			return -1;
		}

		DBTableModel mastersaveds = mastermodel.getModifiedDbmodel();

		// 将数据保存进行提交.
		StringCommand cmd1 = new StringCommand(getSaveCommandString());
		DataCommand cmd2 = new DataCommand();
		cmd2.setDbmodel(mastersaveds);

		DataCommand cmd3 = new DataCommand();
		DBTableModel detailsaveds = new DBTableModel(detailmodel
				.getDBtableModel().getDisplaycolumninfos());
		cmd3.setDbmodel(detailsaveds);

		for (int r = 0; r < mastersaveds.getRowCount(); r++) {
			String tmppkid = mastersaveds.getTmppkid(r);
			DBTableModel detailmodel = (DBTableModel) detaildbmodelmap
					.get(tmppkid);
			if (detailmodel != null) {
				for (int j = 0; j < detailmodel.getRowCount(); j++) {
					// 设置行号
					detailmodel.setItemValue(j, 0, String.valueOf(j));
				}

				DBTableModel modifiedata = detailmodel.getModifiedData();
				for (int j = 0; j < modifiedata.getRowCount(); j++) {
					RecordTrunk rec = modifiedata.getRecordThunk(j);
					rec.setRelatevalue(tmppkid);
				}
				detailsaveds.appendDbmodel(modifiedata);
			}
		}

		ClientRequest req = new ClientRequest();
		req.addCommand(cmd1);
		req.addCommand(cmd2);
		req.addCommand(cmd3);

		CDefaultProgress prog = null;
		prog = new CDefaultProgress(this.getParentFrame());
		SaveThread t = new SaveThread(req, prog);
		t.start();

		prog.show();

		on_save();

		return 0;
	}

	/**
	 * 后台安静地保存
	 * 
	 * @return
	 */
	public int doSaveSlient() {
		if (commitcount > 0)
			return 0;
		if (getModifiedRowCount() == 0)
			return 0;

		if (ruleeng != null) {
			if (ruleeng.process(this, "屏蔽保存", 0) < 0)
				return -1;
		}
		mastermodel.commitEdit();
		detailmodel.commitEdit();

		if (0 != on_beforesave()) {
			return -1;
		}

		DBTableModel mastersaveds = mastermodel.getModifiedDbmodel();

		// 将数据保存进行提交.
		StringCommand cmd1 = new StringCommand(getSaveCommandString());
		DataCommand cmd2 = new DataCommand();
		cmd2.setDbmodel(mastersaveds);

		DataCommand cmd3 = new DataCommand();
		DBTableModel detailsaveds = new DBTableModel(detailmodel
				.getDBtableModel().getDisplaycolumninfos());
		cmd3.setDbmodel(detailsaveds);

		for (int r = 0; r < mastersaveds.getRowCount(); r++) {
			String tmppkid = mastersaveds.getTmppkid(r);
			DBTableModel detailmodel = (DBTableModel) detaildbmodelmap
					.get(tmppkid);
			if (detailmodel != null) {
				for (int j = 0; j < detailmodel.getRowCount(); j++) {
					// 设置行号
					detailmodel.setItemValue(j, 0, String.valueOf(j));
				}

				DBTableModel modifiedata = detailmodel.getModifiedData();
				for (int j = 0; j < modifiedata.getRowCount(); j++) {
					RecordTrunk rec = modifiedata.getRecordThunk(j);
					rec.setRelatevalue(tmppkid);
				}
				detailsaveds.appendDbmodel(modifiedata);
			}
		}

		ClientRequest req = new ClientRequest();
		req.addCommand(cmd1);
		req.addCommand(cmd2);
		req.addCommand(cmd3);

		SaveThreadSlient t = new SaveThreadSlient(req);
		t.start();

		on_save();

		return 0;
	}

	/**
	 * 保存到服务器，不使用线程
	 * 
	 * @param prog
	 * @return
	 */
	public int savetoserver(CProgressIF progress) {
		mastermodel.commitEdit();
		detailmodel.commitEdit();

		if (!mastermodel.doHideform()) {
			return -1;
		}
		if (!detailmodel.doHideform()) {
			return -1;
		}
		if (0 != on_beforesave()) {
			return -1;
		}

		DBTableModel mastersaveds = mastermodel.getModifiedDbmodel();

		// 将数据保存进行提交.
		StringCommand cmd1 = new StringCommand(getSaveCommandString());
		DataCommand cmd2 = new DataCommand();
		cmd2.setDbmodel(mastersaveds);

		DataCommand cmd3 = new DataCommand();
		DBTableModel detailsaveds = new DBTableModel(detailmodel
				.getDBtableModel().getDisplaycolumninfos());
		cmd3.setDbmodel(detailsaveds);

		for (int r = 0; r < mastersaveds.getRowCount(); r++) {
			String tmppkid = mastersaveds.getTmppkid(r);
			DBTableModel detailmodel = (DBTableModel) detaildbmodelmap
					.get(tmppkid);
			if (detailmodel != null) {
				for (int j = 0; j < detailmodel.getRowCount(); j++) {
					// 设置行号
					detailmodel.setItemValue(j, 0, String.valueOf(j));
				}

				DBTableModel modifiedata = detailmodel.getModifiedData();
				for (int j = 0; j < modifiedata.getRowCount(); j++) {
					RecordTrunk rec = modifiedata.getRecordThunk(j);
					rec.setRelatevalue(tmppkid);
				}
				detailsaveds.appendDbmodel(modifiedata);
			}
		}

		ClientRequest req = new ClientRequest();
		req.addCommand(cmd1);
		req.addCommand(cmd2);
		req.addCommand(cmd3);

		progress.appendMessage("正在提交数据，等待服务器响应....");

		if (!lockData()) {
			warnMessage("不要重复提交", "不要重复提交");
			progress.messageBox("提示", "没有保存");
			return -1;
		}

		try {
			// ///////////////debug only/////////////////
			RemoteConnector conn = new RemoteConnector();
			String url = DefaultNPParam.defaultappsvrurl;
			ServerResponse svrresp = null;
			if (DefaultNPParam.debug == 1) {
				svrresp = Server.getInstance().process(req);
			} else {
				svrresp = conn.submitRequest(url, req);
			}

			// ServerResponse svrresp = rmtconn.submitRequest(url, req);
			CommandBase cmd = svrresp.commandAt(0);
			if (cmd instanceof StringCommand) {
				String cmdstring = ((StringCommand) cmd).getString();
				progress.messageBox("保存失败", cmdstring);
				return -1;
			}
			ResultCommand resultcmd1 = (ResultCommand) svrresp.commandAt(0);
			mastermodel.setLineresults(resultcmd1.getLineresults());
			mastermodel.getDBtableModel().clearDeleted();
			// 计算总单失败数量．
			int errorct = 0;
			Enumeration<RecordTrunk> en = resultcmd1.getLineresults()
					.elements();
			while (en.hasMoreElements()) {
				RecordTrunk lineResult = en.nextElement();
				if (lineResult.getSaveresult() != 0) {
					errorct++;
				}
			}

			// 设置细单的返回值
			ResultCommand resultcmd2 = (ResultCommand) svrresp.commandAt(1);
			Iterator it = detaildbmodelmap.keySet().iterator();
			while (it.hasNext()) {
				String tmppkid = ((String) it.next());
				DBTableModel dbmodel = (DBTableModel) detaildbmodelmap
						.get(tmppkid);
				if (dbmodel == null) {
					continue;
				}

				Enumeration<RecordTrunk> en1 = resultcmd2.getLineresults()
						.elements();
				while (en1.hasMoreElements()) {
					RecordTrunk lineResult = en1.nextElement();
					if (lineResult.getRelatevalue().equals(tmppkid)) {
						dbmodel.setLineresult(lineResult);
					}
				}
				dbmodel.clearDeleted();
			}
			mastermodel.setRow(-1);
			detailmodel.setRow(-1);

			int detailerrorct = 0;
			en = resultcmd2.getLineresults().elements();
			while (en.hasMoreElements()) {
				RecordTrunk lineResult = en.nextElement();
				if (lineResult.getSaveresult() != 0) {
					detailerrorct++;
				}
			}

			mastermodel.tableChanged();
			detailmodel.tableChanged();

			// 定位于总单第一条保存成功
			if (resultcmd1.getLineresults().size() > 0) {
				RecordTrunk firstmaster = resultcmd1.getLineresults()
						.elementAt(0);
				int row = Integer.parseInt(firstmaster.getValueAt(0));
				mastermodel.setRow(row);
			} else {
				mastermodel.setRow(0);
			}

			StringBuffer sb = new StringBuffer();
			if (errorct + detailerrorct == 0) {
				getMasterModel().getDBtableModel().resetWantuploadfiles();
				Iterator<DBTableModel> itdtlmodel = detaildbmodelmap.values()
						.iterator();
				while (itdtlmodel.hasNext()) {
					DBTableModel dtlmodel = itdtlmodel.next();
					dtlmodel.resetWantuploadfiles();
				}
				sb.append("保存成功");
			} else {
				if (errorct == 0) {
					sb.append("总单保存成功");
				} else {
					sb.append("总单保存失败" + errorct + "条记录．");
				}
				if (detailerrorct == 0) {
					sb.append("细单保存成功");
				} else {
					sb.append("细单保存失败" + detailerrorct + "条记录．");
				}
			}
			// infoMessage("保存结果", sb.toString());

			progress.messageBox("保存完成", "保存完成:" + sb.toString());

		} catch (Exception e) {
			logger.error("保存错误", e);
			progress.messageBox("保存失败", "数据保存异常．错误原因:" + e.getMessage());
			return -1;
		} finally {
			unloakData();

			/*
			 * if(mastermodel.getForm().isVisible()){
			 * mastermodel.getForm().requestFocus(); }else
			 * if(detailmodel.getForm().isVisible()){
			 * detailmodel.getForm().requestFocus(); }else{
			 * getParentFrame().requestFocus(); }
			 */

			getParentFrame().setEnabled(true);
			mastermodel.getForm().setEnabled(true);
			detailmodel.getForm().setEnabled(true);

		}

		on_save();

		return 0;
	}

	/**
	 * 保存后
	 */
	protected void on_save() {
		if (zxmdejavadelegate != null) {
			zxmdejavadelegate.on_save(this);
		}
	}

	/**
	 * 保存线程
	 * 
	 * @author Administrator
	 * 
	 */
	class SaveThread extends Thread {
		ClientRequest req = null;
		CProgressIF progress = null;

		public SaveThread(ClientRequest req, CProgressIF progress) {
			this.req = req;
			this.progress = progress;
		}

		public void run() {
			progress.appendMessage("正在提交数据，等待服务器响应....");

			if (!lockData()) {
				warnMessage("不要重复提交", "不要重复提交");
				progress.messageBox("提示", "没有保存");
				return;
			}

			try {
				// ///////////////debug only/////////////////
				RemoteConnector conn = new RemoteConnector();
				String url = DefaultNPParam.defaultappsvrurl;
				ServerResponse svrresp = null;
				if (DefaultNPParam.debug == 1) {
					svrresp = Server.getInstance().process(req);
				} else {
					svrresp = conn.submitRequest(url, req);
				}

				// ServerResponse svrresp = rmtconn.submitRequest(url, req);
				CommandBase cmd = svrresp.commandAt(0);
				if (cmd instanceof StringCommand) {
					String cmdstring = ((StringCommand) cmd).getString();
					progress.messageBox("保存失败", cmdstring);
					return;
				}
				ResultCommand resultcmd1 = (ResultCommand) svrresp.commandAt(0);
				mastermodel.setLineresults(resultcmd1.getLineresults());
				mastermodel.getDBtableModel().clearDeleted();
				// 计算总单失败数量．
				int errorct = 0;
				Enumeration<RecordTrunk> en = resultcmd1.getLineresults()
						.elements();
				while (en.hasMoreElements()) {
					RecordTrunk lineResult = en.nextElement();
					if (lineResult.getSaveresult() != 0) {
						errorct++;
					}
				}

				// 设置细单的返回值
				ResultCommand resultcmd2 = (ResultCommand) svrresp.commandAt(1);
				Iterator it = detaildbmodelmap.keySet().iterator();
				while (it.hasNext()) {
					String tmppkid = ((String) it.next());
					DBTableModel dbmodel = (DBTableModel) detaildbmodelmap
							.get(tmppkid);
					if (dbmodel == null) {
						continue;
					}

					Enumeration<RecordTrunk> en1 = resultcmd2.getLineresults()
							.elements();
					while (en1.hasMoreElements()) {
						RecordTrunk lineResult = en1.nextElement();
						if (lineResult.getRelatevalue().equals(tmppkid)) {
							dbmodel.setLineresult(lineResult);
						}
					}
					dbmodel.clearDeleted();
				}
				mastermodel.setRow(-1);
				detailmodel.setRow(-1);

				int detailerrorct = 0;
				en = resultcmd2.getLineresults().elements();
				while (en.hasMoreElements()) {
					RecordTrunk lineResult = en.nextElement();
					if (lineResult.getSaveresult() != 0) {
						detailerrorct++;
					}
				}

				mastermodel.tableChanged();
				detailmodel.tableChanged();

				// 定位于总单第一条保存成功
				if (resultcmd1.getLineresults().size() > 0) {
					RecordTrunk firstmaster = resultcmd1.getLineresults()
							.elementAt(0);
					int row = Integer.parseInt(firstmaster.getValueAt(0));
					mastermodel.setRow(row);
				} else {
					mastermodel.setRow(0);
				}

				StringBuffer sb = new StringBuffer();
				if (errorct + detailerrorct == 0) {
					getMasterModel().getDBtableModel().resetWantuploadfiles();
					Iterator<DBTableModel> itdtlmodel = detaildbmodelmap
							.values().iterator();
					while (itdtlmodel.hasNext()) {
						DBTableModel dtlmodel = itdtlmodel.next();
						dtlmodel.resetWantuploadfiles();
					}
					sb.append("保存成功");
				} else {
					if (errorct == 0) {
						sb.append("总单保存成功");
					} else {
						sb.append("总单保存失败" + errorct + "条记录．");
					}
					if (detailerrorct == 0) {
						sb.append("细单保存成功");
					} else {
						sb.append("细单保存失败" + detailerrorct + "条记录．");
					}
				}
				// infoMessage("保存结果", sb.toString());

				progress.messageBox("保存完成", "保存完成:" + sb.toString());

			} catch (Exception e) {
				logger.error("保存错误", e);
				progress.messageBox("保存失败", "数据保存异常．错误原因:" + e.getMessage());
				return;
			} finally {
				unloakData();

				/*
				 * if(mastermodel.getForm().isVisible()){
				 * mastermodel.getForm().requestFocus(); }else
				 * if(detailmodel.getForm().isVisible()){
				 * detailmodel.getForm().requestFocus(); }else{
				 * getParentFrame().requestFocus(); }
				 */

				getParentFrame().setEnabled(true);
				mastermodel.getForm().setEnabled(true);
				detailmodel.getForm().setEnabled(true);

			}
		}
	}

	class SaveThreadSlient extends Thread {
		ClientRequest req = null;

		public SaveThreadSlient(ClientRequest req) {
			this.req = req;
		}

		public void run() {

			if (!lockData()) {
				// warnMessage("不要重复提交", "不要重复提交");
				return;
			}

			try {
				// ///////////////debug only/////////////////
				RemoteConnector conn = new RemoteConnector();
				String url = DefaultNPParam.defaultappsvrurl;
				ServerResponse svrresp = null;
				if (DefaultNPParam.debug == 1) {
					svrresp = Server.getInstance().process(req);
				} else {
					svrresp = conn.submitRequest(url, req);
				}

				// ServerResponse svrresp = rmtconn.submitRequest(url, req);
				CommandBase cmd = svrresp.commandAt(0);
				if (cmd instanceof StringCommand) {
					String cmdstring = ((StringCommand) cmd).getString();
					if (!cmdstring.startsWith("+OK")) {
						logger.error(cmdstring);
						return;
					}
				}
				ResultCommand resultcmd1 = (ResultCommand) svrresp.commandAt(0);
				mastermodel.setLineresults(resultcmd1.getLineresults());
				mastermodel.getDBtableModel().clearDeleted();
				// 计算总单失败数量．
				int errorct = 0;
				Enumeration<RecordTrunk> en = resultcmd1.getLineresults()
						.elements();
				while (en.hasMoreElements()) {
					RecordTrunk lineResult = en.nextElement();
					if (lineResult.getSaveresult() != 0) {
						errorct++;
					}
				}

				// 设置细单的返回值
				ResultCommand resultcmd2 = (ResultCommand) svrresp.commandAt(1);
				Iterator it = detaildbmodelmap.keySet().iterator();
				while (it.hasNext()) {
					String tmppkid = ((String) it.next());
					DBTableModel dbmodel = (DBTableModel) detaildbmodelmap
							.get(tmppkid);
					if (dbmodel == null) {
						continue;
					}

					Enumeration<RecordTrunk> en1 = resultcmd2.getLineresults()
							.elements();
					while (en1.hasMoreElements()) {
						RecordTrunk lineResult = en1.nextElement();
						if (lineResult.getRelatevalue().equals(tmppkid)) {
							dbmodel.setLineresult(lineResult);
						}
					}
					dbmodel.clearDeleted();
				}
				// Thread.sleep(5000);

				mastermodel.tableChanged();
				detailmodel.tableChanged();

			} catch (Exception e) {
				logger.error("保存错误", e);
				return;
			} finally {
				unloakData();
			}
		}
	}

	/**
	 * 查询前检查
	 * 
	 * @return 非0 不能查询
	 */
	public int on_beforequery() {
		// 有没有修改的呢？
		int mastermodirow = mastermodel.getDBtableModel().getModifiedData()
				.getRowCount();
		int detailmodirow = 0;
		Iterator it = detaildbmodelmap.values().iterator();
		while (it.hasNext()) {
			DBTableModel dbmodel = (DBTableModel) it.next();
			detailmodirow += dbmodel.getModifiedData().getRowCount();
		}
		if (mastermodirow + detailmodirow > 0) {
			String msg = "数据修改没有保存,如果继续查询会丢失修改的数据,还继续查询吗?";
			int ret = JOptionPane.showConfirmDialog(this.getParentFrame(), msg,
					"警告", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				return -1;
			}
		}

		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_beforequery(this);
			if (ret != 0) {
				return ret;
			}
		}

		return 0;
	}

	/**
	 * 关闭前检查.
	 * 
	 * @return 非0不能关闭
	 */
	public int on_beforeclose() {
		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_beforeclose(this);
			if (ret != 0) {
				return ret;
			}
		}

		if (getModifiedRowCount() > 0) {
			String msg = "数据已修改,是不是要保存?";
			int ret = JOptionPane.showConfirmDialog(this.getParentFrame(), msg,
					"警告", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret == JOptionPane.YES_OPTION) {
				if (0 != doSave()) {
					return -1;
				}
			} else if (ret == JOptionPane.NO_OPTION) {
				return 0;
			} else if (ret == JOptionPane.CANCEL_OPTION) {
				return -1;
			}
		}
		return 0;
	}

	/**
	 * 是否总单正在查询
	 * 
	 * @return
	 */
	public boolean isquerying() {
		return mastermodel.isquerying();
	}

	/**
	 * 输入查询条件查询
	 */
	public void doQuery() {
		if (ruleeng != null) {
			if (ruleeng.process(this, "屏蔽查询", 0) < 0)
				return;
		}

		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_beforequery(this);
			if (ret != 0) {
				return;
			}
		}

		mastermodel.setUsequerythread(usequerythread);
		mastermodel.doQuery();
	}

	/**
	 * 中止查询
	 */
	public void stopQuery() {
		mastermodel.stopQuery();
	}

	/**
	 * 新增总单
	 */
	public void doNew() {
		/*
		 * if (detailmodel.getSteformwindow().isVisible()) {
		 * detailmodel.doHideform(); }
		 */

		detailmodel.doHideform();

		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_beforeNew(this);
			if (ret != 0) {
				return;
			}
		}
		if (resetbeforenew) {
			clearSaved();
		}
		mastermodel.doNew();
	}

	/**
	 * 删除总单
	 */
	public void doDel() {
		mastermodel.doDel();
	}

	/**
	 * 修改总单
	 */
	public void doModify() {
		/*
		 * if (detailmodel.getSteformwindow().isVisible()) {
		 * detailmodel.doHideform(); }
		 */
		detailmodel.doHideform();
		if (zxmdejavadelegate != null) {
			int row = mastermodel.getRow();
			int ret = zxmdejavadelegate.on_beforemodify(this, row);
			if (ret != 0) {
				return;
			}
		}

		mastermodel.doModify();
	}

	/**
	 * 隐藏总单卡片窗口
	 */
	public void doHideform() {
		mastermodel.doHideform();
		detailmodel.doHideform();
		/*
		 * if (mastermodel.getSteformwindow().isVisible()) {
		 * mastermodel.doHideform(); } if
		 * (detailmodel.getSteformwindow().isVisible()) {
		 * detailmodel.doHideform(); }
		 */}

	/**
	 * 定位总单第一行
	 */
	public void doFirstRow() {
		mastermodel.doFirstRow();
	}

	/**
	 * 定位总单最后一行
	 */
	public void doLastRow() {
		mastermodel.doLastRow();
	}

	/**
	 * 定位总单下一行
	 */
	public void doNextRow() {
		mastermodel.doNextRow();
	}

	/**
	 * 定位总单上一行
	 */
	public void doPriorRow() {
		mastermodel.doPriorRow();
	}

	/**
	 * 定位细单第一行
	 */
	public void doFirstDtlRow() {
		detailmodel.doFirstRow();
	}

	/**
	 * 定位细单最后一行
	 */
	public void doLastDtlRow() {
		detailmodel.doLastRow();
	}

	/**
	 * 定位细单下一行
	 */
	public void doNextDtlRow() {
		detailmodel.doNextRow();
	}

	/**
	 * 定位细单上一行
	 */
	public void doPriorDtlRow() {
		detailmodel.doPriorRow();
	}

	/**
	 * 新增细单
	 */
	public void doNewdtl() {
		int masterrow = mastermodel.getRow();
		if (masterrow < 0) {
			warnMessage("不能新增", "请先增加总单，再增加细单");
			return;
		}

		getMasterModel().commitEdit();

		if (0 != getMasterModel().checkrow(masterrow)) {
			return;
		}

		if (zxmdejavadelegate != null) {
			int row = mastermodel.getRow();
			int ret = zxmdejavadelegate.on_beforenewdtl(this, row);
			if (ret != 0) {
				return;
			}
		}
		/*
		 * 新增细单不要求关总单 if (mastermodel.getSteformwindow().isVisible()) { if
		 * (!mastermodel.doHideform()) { return; } }
		 */
		detailmodel.doNew();
	}

	/**
	 * 修改细单
	 */
	public void doModifydtl() {
		/*
		 * if (mastermodel.getSteformwindow().isVisible()) {
		 * mastermodel.doHideform(); }
		 */
		if (zxmdejavadelegate != null) {
			int row = detailmodel.getRow();
			int ret = zxmdejavadelegate.on_beforemodifydtl(this, row);
			if (ret != 0) {
				return;
			}
		}

		detailmodel.doModify();
	}

	/**
	 * 删除细单
	 */
	public void doDeldtl() {
		if (zxmdejavadelegate != null) {
			int row = detailmodel.getRow();
			int ret = zxmdejavadelegate.on_beforedeldtl(this, row);
			if (ret != 0) {
				return;
			}
		}
		detailmodel.doDel();
	}

	/**
	 * 能否修改总单
	 * 
	 * @param row
	 * @return
	 */
	protected int on_beforemodifymaster(int row) {
		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_beforemodifymaster(this, row);
			if (ret != 0) {
				return ret;
			}
		}
		if (detailmodel.getRowCount() > 0) {
			// warnMessage("不能修改", "细单已有记录，总单不能改");
			return -1;
		}
		return 0;
	}

	/**
	 * 总单事件监听器
	 * 
	 * @author Administrator
	 * 
	 */
	protected class MasterModelListener extends CSteModelListenerAdaptor {
		public MasterModelListener() {

		}

		@Override
		public int on_beforemodify(int row) {
			if (on_beforemodifymaster(row) != 0)
				return -1;
			return super.on_beforemodify(row);
		}

		public void on_tablerowchanged(int newrow, int newcol, int oldrow,
				int oldcol) {
			retrieveDetail(newrow, oldrow);
		}

		public void on_click(int row, int col) {
			int mr=getMasterModel().getRow();
			if(mr!=row){
				retrieveDetail(row, -1);
			}
		}

		public int on_beforequery() {
			if (0 != super.on_beforequery()) {
				return -1;
			}
			if (CMdeModel.this.on_beforequery() != 0)
				return -1;
			return 0;
		}

		@Override
		public void on_retrievestart() {
			super.on_retrievestart();
			clearDetailCache();
			detailmodel.recreateDBModel();
		}

		public int on_beforeundo() {
			int row = mastermodel.getRow();
			if (row < 0) {
				return 0;
			}

			// 如果总单新增，需要提示细单要删除
			RecordTrunk masterrec = mastermodel.getDBtableModel()
					.getRecordThunk(row);
			if (masterrec.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
				String msg = "总单是新增的，如果你撤消新增总单，细单也要自动撤消，你确定吗？";
				int ret = JOptionPane.showConfirmDialog(getParentFrame(), msg,
						"警告", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (ret != JOptionPane.OK_OPTION) {
					return -1;
				}

				// 将细单全部删除
				detailmodel.getDBtableModel().clearAll();
				detailmodel.setRow(-1);
				detailmodel.getSumdbmodel().fireDatachanged();
				detailmodel.tableChanged();
			}
			return 0;

		}

		@Override
		public int on_beforedel(int row) {
			if (row < 0) {
				return 0;
			}

			String msg = "如果你删除总单，细单也要自动删除，你确定吗？";
			int ret = JOptionPane.showConfirmDialog(getParentFrame(), msg,
					"警告", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				return -1;
			}

			// 如果总单是新增,将细单一起丢了.
			DBTableModel dtldbmodel = detailmodel.getDBtableModel();
			int masterdbstatus = mastermodel.getdbStatus(row);
			if (RecordTrunk.DBSTATUS_NEW == masterdbstatus) {
				dtldbmodel.clearAll();
				detailmodel.getSumdbmodel().fireDatachanged();
				detailmodel.setRow(-1);
				detailmodel.tableChanged();
			} else {
				// 将细单全部置为删除状态
				for (int dr = 0; dr < dtldbmodel.getRowCount(); dr++) {
					RecordTrunk dtlrec = dtldbmodel.getRecordThunk(dr);
					if (dtlrec.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
						dtldbmodel.removeRow(dr);
						dr--;
					} else {
						dtlrec.setDbstatus(RecordTrunk.DBSTATUS_DELETE);
					}
				}
				detailmodel.getSumdbmodel().fireDatachanged();
				detailmodel.setRow(-1);
				detailmodel.tableChanged();
			}
			return 0;
		}

		public void on_retrieved() {
			mastermodel.getRootpanel().requestFocus();
		}

		@Override
		public void on_del(int row) {
			super.on_del(row);

			if (isSaveimmdiate() && getModifiedRowCount() > 0) {
				doSaveSlient();
			}
		}

	}

	/**
	 * 细单model监听器
	 * 
	 * @author Administrator
	 * 
	 */
	protected class DetailModelListener extends CSteModelListenerAdaptor {
		public DetailModelListener() {

		}

		public void on_tablerowchanged(int newrow, int newcol, int oldrow,
				int oldcol) {
			// retrieveMaster(newrow);
		}

		public void on_click(int row, int col) {
			// retrieveMaster(row);
		}

		public int on_beforenew() {
			mastermodel.commitEdit();

			// 总单有记录才行．
			int masterrow = mastermodel.getRow();
			if (masterrow < 0) {
				warnMessage("提示", "先增加相关总单记录");
				return -1;
			}
			/*
			 * 检查总单。
			 */
			if (mastermodel.checkrow(masterrow) != 0) {
				return -1;
			}

			if (CMdeModel.this.on_beforenewdtl() != 0) {
				return -1;
			}
			return 0;
		}

		public void on_new(int row) {
			// 取总单记录
			int masterrow = mastermodel.getRow();
			String masterv = mastermodel.getDBtableModel().getItemValue(
					masterrow, masterrelatecolname);
			// 设置当前值
			detailmodel.getDBtableModel().setItemValue(row,
					detailrelatecolname, masterv);
			// 重新设置总单
			mastermodel.bindDataSetEnable(mastermodel.getRow());
		}

		public void on_itemvaluechange(int row, String colname, String value) {
			super.on_itemvaluechange(row, colname, value);
			// 细单修改，总单也改
			if (detailmodel.getModifiedDbmodel().getRowCount() > 0) {
				int masterrow = mastermodel.getRow();
				if (mastermodel.getdbStatus(masterrow) == RecordTrunk.DBSTATUS_SAVED) {
					mastermodel.setdbStatus(masterrow,
							RecordTrunk.DBSTATUS_MODIFIED);
				}
				mastermodel.bindDataSetEnable(mastermodel.getRow());
			}
		}

		@Override
		public int on_beforedel(int row) {
			if (CMdeModel.this.on_beforedel(row) != 0) {
				return -1;
			}
			return 0;
		}

		public void on_del(int row) {
			super.on_del(row);
			// 细单删除，总单要修改
			int masterrow = mastermodel.getRow();
			if (mastermodel.getdbStatus(masterrow) == RecordTrunk.DBSTATUS_SAVED) {
				mastermodel.setdbStatus(masterrow,
						RecordTrunk.DBSTATUS_MODIFIED);
			}
			mastermodel.bindDataSetEnable(mastermodel.getRow());
		}

		public int on_beforemodify(int row) {
			if (CMdeModel.this.on_beforemodifydtl(row) != 0) {
				return -1;
			}
			return 0;
		}
	}

	/**
	 * 增加的非标准的命令。
	 * 
	 * @param command
	 * @return 返回0已处理，不要再处理。返回非0本函数未处理，需要继续处理。
	 */
	protected int on_actionPerformed(String command) {
		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_actionPerformed(this, command);
			if (ret == 0)
				return ret;
		}

		return -1;
	}

	/**
	 * 能否删除细单
	 * 
	 * @param row
	 * @return
	 */
	protected int on_beforedel(int row) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 能否新增细单
	 * 
	 * @return
	 */
	protected int on_beforenewdtl() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 能否细单修改
	 * 
	 * @param row
	 * @return
	 */
	public int on_beforemodifydtl(int row) {
		return 0;
	}

	/**
	 * 事件处理.调用了on_actionPerformed()
	 */
	public void actionPerformed(ActionEvent e) {
		String actioncommand = e.getActionCommand();
		if (actioncommand == null) {
			return;
		}

		int ret = on_actionPerformed(actioncommand);
		if (0 == ret) {
			return;
		}

		if (actioncommand.startsWith(ACTION_DOCPRINT_PREFIX)) {
			String planname = actioncommand.substring(ACTION_DOCPRINT_PREFIX
					.length());
			docPrint(planname);
		}

		if (actioncommand.equals(CSteModel.ACTION_QUERY)) {
			if (!isquerying()) {
				doQuery();
			} else {
				stopQuery();
			}

		} else if (actioncommand.equals(CSteModel.ACTION_REFRESH)) {
			getMasterModel().actionPerformed(e);
		} else if (actioncommand.equals(CMdeModel.ACTION_NEW)) {
			doNew();
		} else if (actioncommand.equals(CMdeModel.ACTION_DEL)) {
			doDel();
		} else if (actioncommand.equals(CMdeModel.ACTION_SAVE)) {
			doSave();
		} else if (actioncommand.equals(CMdeModel.ACTION_MODIFY)) {
			doModify();
		} else if (actioncommand.equals(CMdeModel.ACTION_MODIFYDTL)) {
			doModifydtl();
		} else if (actioncommand.equals(CMdeModel.ACTION_UNDO)) {
			doUndo();
		} else if (actioncommand.equals(CMdeModel.ACTION_UNDODTL)) {
			doUndodtl();
		} else if (actioncommand.equals(CMdeModel.ACTION_HIDEFORM)) {
			doHideform();
		} else if (actioncommand.equals(CMdeModel.ACTION_FIRST)) {
			doFirstRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_LAST)) {
			doLastRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_NEXT)) {
			doNextRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_PRIOR)) {
			doPriorRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_NEWDTL)) {
			doNewdtl();
		} else if (actioncommand.equals(CMdeModel.ACTION_MODIFYDTL)) {
			doModifydtl();
		} else if (actioncommand.equals(CMdeModel.ACTION_DELDTL)) {
			doDeldtl();
		} else if (actioncommand.equals(CMdeModel.ACTION_FIRSTDTL)) {
			doFirstDtlRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_LASTDTL)) {
			doLastDtlRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_NEXTDTL)) {
			doNextDtlRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_PRIORDTL)) {
			doPriorDtlRow();
		} else if (actioncommand.equals(CMdeModel.ACTION_SETUPUI)) {
			mastermodel.setupUI();
		} else if (actioncommand.equals(CMdeModel.ACTION_SAVEUI)) {
			mastermodel.saveUI();
		} else if (actioncommand.equals(CMdeModel.ACTION_SETUPUIDTL)) {
			detailmodel.setupUI();
		} else if (actioncommand.equals(CMdeModel.ACTION_SAVEUIDTL)) {
			detailmodel.saveUI();
		} else if (actioncommand.equals(CMdeModel.ACTION_PRINTSETUP)) {
			printSetup();
		} else if (actioncommand.equals(CMdeModel.ACTION_PRINT)) {
			print();
		} else if (actioncommand.equals(CMdeModel.ACTION_EXIT)) {
			doExit();
		} else if (actioncommand.equals(CMdeModel.ACTION_SELECTOP)) {
			Clientframe.getClientframe().requestFocus();
		} else if (CMdeModel.ACTION_EXPORT.equals(actioncommand)) {
			doExport();
		} else if (CMdeModel.ACTION_EXPORTAS.equals(actioncommand)) {
			doExportas();
		} else if (CMdeModel.ACTION_EXPORTMASTERAS.equals(actioncommand)) {
			doExportmasteras();
		} else if (CMdeModel.ACTION_EXPORTDETAILAS.equals(actioncommand)) {
			doExportdetailas();
		} else if (CMdeModel.ACTION_SELFCHECK.equals(actioncommand)) {
			doSelfcheck();
		} else if (ACTION_SETUPRULE.equals(actioncommand)) {
			doSetuprule();
		}
	}

	/**
	 * 设置规则
	 */
	public void doSetuprule() {
		if (ruleeng == null) {
			ruleeng = new Ruleenginee();
		}

		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p > 0) {
			classname = classname.substring(p + 1);
		}

		RulesetupMaindialog frm = new RulesetupMaindialog(
				this.getParentFrame(), ruleeng, this, "mde");
		frm.pack();
		frm.setVisible(true);
		if (!frm.getOk())
			return;

		if (zxmodify) {
			// 要存在classes/专项开发/opid.zip的文件中，并上传服务器。
			File dir = CurrentdirHelper.getZxdir();
			File zxfile = new File(dir, opid + ".zip");
			File tempfile = null;
			try {
				tempfile = File.createTempFile("temp", ".rule");
				RuleRepository.saveRule(tempfile, ruleeng.getRuletable());
				ZipHelper.replaceZipfile(zxfile, "mde.rule", tempfile);

				// 上传
				ZxmodifyUploadHelper zu = new ZxmodifyUploadHelper();
				if (!zu.uploadZxfile(opid, zxfile)) {
					errorMessage("上传错误", zu.getErrormessage());
					return;
				}

			} catch (Exception e) {
				logger.error("e", e);
				errorMessage("错误", e.getMessage());
				return;
			} finally {
				if (tempfile != null)
					tempfile.delete();
			}
		}

		// 先在class目录下存一个
		String url = this.getClass().getResource(classname + ".class")
				.toString();
		if (url.indexOf("!") < 0) {
			url = url.substring("file:/".length());
			p = url.lastIndexOf("/");
			url = url.substring(0, p + 1);
			url += classname + ".rule";
			File outf = new File(url);
			try {
				RuleRepository.saveRule(outf, ruleeng.getRuletable());
			} catch (Exception e) {
				logger.error("save rule", e);
			}
		} else {
			// 说明在一个JAR文件中.要找到这个jar文件所在的目录的 ../classes
			if (url.startsWith("jar:"))
				url = url.substring(4);
			if (url.startsWith("file:"))
				url = url.substring(5);

			p = url.indexOf("!");
			File jarfile = new File(url.substring(0, p));
			File dir = jarfile.getParentFile();
			File classdir = new File(dir, "classes");
			if (!classdir.exists())
				classdir.mkdirs();
			url = url.substring(p + 1);
			p = url.lastIndexOf("/");
			url = url.substring(0, p + 1);
			url += classname + ".model";

			File outf = new File(classdir, url);
			outf.getParentFile().mkdirs();
			try {
				RuleRepository.saveRule(outf, ruleeng.getRuletable());
			} catch (Exception e) {
				logger.error("save rule", e);
			}
		}

		if (!zxmodify) {
			classname = this.getClass().getName();
			classname = classname.replaceAll("\\.", "/");
			File outdir = new File("src");
			File outf = new File(outdir.getPath() + "/" + classname + ".rule");
			outf.getParentFile().mkdirs();
			try {
				RuleRepository.saveRule(outf, ruleeng.getRuletable());
			} catch (Exception e) {
				logger.error("save rule", e);
				errorMessage("save rule", "保存规则设置失败");
			}
		}
	}

	/**
	 * 自检
	 */
	protected void doSelfcheck() {
		String s = selfCheck();
		if (s == null || s.length() == 0)
			return;
		CDefaultProgress prop = new CDefaultProgress(this.getParentFrame());
		prop.appendMessage("自检ERROR:\r\n");
		prop.messageBox("", s);
		prop.show();
	}

	/**
	 * 导出前。返回0可以继续导出。
	 * 
	 * @return
	 */
	protected int on_beforeexport() {
		return 0;
	}

	/**
	 * 输出总单细单到excel文件
	 */
	protected void doExport() {
		if (on_beforeexport() != 0)
			return;
		setWaitCursor();

		File outdir = new File("输出EXCEL");
		outdir.mkdirs();
		File outf = new File(outdir, getTitle() + ".xls");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet0 = wb.createSheet(getMasterModel().getTitle());
		int mr = getMasterModel().getRow();
		if (mr < 0) {
			warnMessage("警告", "没有数据");
			setDefaultCursor();
			return;
		}
		ExcelHelper.bindData(wb, sheet0, getMasterModel().getTable(), mr, mr);

		HSSFSheet sheet1 = wb.createSheet(getDetailModel().getTitle());
		ExcelHelper.bindData(wb, sheet1, getDetailModel().getTable(), 0,
				getDetailModel().getTable().getRowCount() - 1 - 1);

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(outf);
			wb.write(fout);
			fout.flush();
			ExportinfoDlg dlg = new ExportinfoDlg(outf);
			dlg.pack();
			dlg.setVisible(true);
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("错误", e.getMessage());
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
			setDefaultCursor();
		}

	}

	protected void doExportas() {
		if (on_beforeexport() != 0)
			return;
		JFileChooser fc = new JFileChooser();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		fc.setSelectedFile(new File(getTitle() + "_"
				+ df.format(new java.util.Date())));
		fc.setFileFilter(new ExcelFileFilter());
		if (fc.showSaveDialog(this.getParentFrame()) != JFileChooser.APPROVE_OPTION)
			return;
		File outf = fc.getSelectedFile();
		if (!outf.getName().toLowerCase().endsWith(".xls")) {
			outf = new File(outf.getParentFile(), outf.getName() + ".xls");
		}

		setWaitCursor();
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet0 = wb.createSheet(getMasterModel().getTitle());
		int mr = getMasterModel().getRow();
		if (mr < 0) {
			warnMessage("警告", "没有数据");
			setDefaultCursor();
			return;
		}
		ExcelHelper.bindData(wb, sheet0, getMasterModel().getTable(), mr, mr);

		HSSFSheet sheet1 = wb.createSheet(getDetailModel().getTitle());
		ExcelHelper.bindData(wb, sheet1, getDetailModel().getTable(), 0,
				getDetailModel().getTable().getRowCount() - 1 - 1);

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(outf);
			wb.write(fout);
			fout.flush();
			ExportinfoDlg dlg = new ExportinfoDlg(outf);
			dlg.pack();
			dlg.setVisible(true);
		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("错误", e.getMessage());
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
			setDefaultCursor();
		}

	}

	/**
	 * 总单导出为
	 */
	protected void doExportmasteras() {
		if (on_beforeexport() != 0)
			return;
		getMasterModel().doExportas();

	}

	/**
	 * 细单导出为
	 */
	protected void doExportdetailas() {
		if (on_beforeexport() != 0)
			return;
		getDetailModel().doExportas();

	}

	/**
	 * 退出
	 */
	protected void doExit() {
		if (0 != on_beforeclose()) {
			return;
		}
		onstopRun();
		getParentFrame().dispose();
	}

	/**
	 * 退出处理
	 */
	public void onstopRun() {
		super.onstopRun();
		if (DefaultNPParam.debug == 1 || DefaultNPParam.develop == 1) {
			doSelfcheck();
		}
		freeMemory();
	}

	/**
	 * 释放内存
	 */
	public void freeMemory() {
		// 要释放掉cachemap
		if (detaildbmodelmap != null) {
			Iterator<DBTableModel> it = detaildbmodelmap.values().iterator();
			while (it.hasNext()) {
				DBTableModel detailm = it.next();
				detailm.freeMemory();
			}
			detaildbmodelmap.clear();
			detaildbmodelmap = null;
		}

		getMasterModel().freeMemory();
		getDetailModel().freeMemory();
		mastermodel=null;
		detailmodel=null;
	}

	/**
	 * 弹出选择功能菜单
	 * 
	 * @param x
	 * @param y
	 */
	protected void on_selectop(int x, int y) {
		JPopupMenu selectopMenu = this.createSelectopMenu();
		selectopMenu.show(this.getParentFrame(), x, y);
	}

	/**
	 * 打印设置
	 */
	protected void printSetup() {
		printsetupfrm = new PrintSetupFrame(opid, zxmodify, getMasterModel(),
				getDetailModel());
		printsetupfrm.pack();
		printsetupfrm.setVisible(true);
	}

	/**
	 * 打印
	 */
	protected void print() {
		String classname = getDetailModel().getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p >= 0) {
			classname = classname.substring(p + 1);
		}
		String configfilename = classname + ".properties";

		Configer config = new Configer(new File("conf/" + configfilename));
		String reportname = config.get("print.reportname");

		boolean needsetup = false;
		needsetup = reportname == null || reportname.length() == 0;
		if (needsetup) {
			printSetup();
			return;
		}
		// 加载报表,打印
		if (printsetupfrm == null) {
			printsetupfrm = new PrintSetupFrame(opid, zxmodify,
					getMasterModel(), getDetailModel());
		}
		printsetupfrm.pack();

		logger.info("调用发送到打印机doPrint()");
		if (!printsetupfrm.doPrint(reportname)) {
			printsetupfrm.setVisible(true);
		}

	}

	/**
	 * 撤消总单修改
	 */
	public void doUndo() {
		// 撤消总单修改
		mastermodel.doUndo();
	}

	/**
	 * 撤消细单修改
	 */
	public void doUndodtl() {
		// 撤消总单修改
		detailmodel.doUndo();
	}

	/**
	 * 提示信息
	 * 
	 * @param title
	 *            标题
	 * @param msg
	 *            信息内容
	 */
	protected void infoMessage(String title, String msg) {
		//implMessage(title, msg, JOptionPane.INFORMATION_MESSAGE);
		CMessageDialog.infoMessage(getParentFrame(), title, msg);
	}

	/**
	 * 错误信息
	 * 
	 * @param title
	 *            标题
	 * @param msg
	 *            信息内容
	 */
	protected void errorMessage(String title, String msg) {
		//implMessage(title, msg, JOptionPane.ERROR_MESSAGE);
		CMessageDialog.errorMessage(getParentFrame(), title, msg);
	}

	/**
	 * 警告信息
	 * 
	 * @param title
	 *            标题
	 * @param msg
	 *            信息内容
	 */
	protected void warnMessage(String title, String msg) {
		//implMessage(title, msg, JOptionPane.WARNING_MESSAGE);
		CMessageDialog.warnMessage(getParentFrame(), title, msg);

	}

	/**
	 * 显示信息
	 * 
	 * @param title
	 * @param msg
	 * @param type
	 */
	private void implMessage(String title, String msg, int type) {
		JOptionPane.showMessageDialog(getParentFrame(), msg, title, type);
		/*
		 * if (mastermodel.getSteformwindow().isVisible()) {
		 * JOptionPane.showMessageDialog(mastermodel.getSteformwindow(), msg,
		 * title, type); } else if (detailmodel.getSteformwindow().isVisible()) {
		 * JOptionPane.showMessageDialog(detailmodel.getSteformwindow(), msg,
		 * title, type); } else {
		 * JOptionPane.showMessageDialog(getParentFrame(), msg, title, type); }
		 */}

	/**
	 * 设置光标为沙漏等待
	 */
	protected void setWaitCursor() {
		this.getParentFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * 设置光标为缺省箭头
	 */
	protected void setDefaultCursor() {
		this.getParentFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * 自检
	 * 
	 * @return
	 */
	public String selfCheck() {
		String s = getMasterModel().selfCheck();
		s += getDetailModel().selfCheck();
		return s;
	}

	/**
	 * 清除细单cache
	 */
	public void clearDetailCache() {
		if (detaildbmodelmap != null) {
			Iterator<DBTableModel> it = detaildbmodelmap.values().iterator();
			while (it.hasNext()) {
				DBTableModel dm = it.next();
				dm.clearAll();
			}
			detaildbmodelmap.clear();
		}
	}

	public Ruleenginee getRuleeng() {
		return ruleeng;
	}

	public void setRuleeng(Ruleenginee ruleeng) {
		this.ruleeng = ruleeng;
	}

	public boolean isZxmodify() {
		return zxmodify;
	}

	public void setZxmodify(boolean zxmodify) {
		this.zxmodify = zxmodify;
	}

	protected void loadRuleenginee() {
		// 检查是不是有专项？
		BufferedReader rd = null;
		File zxfile = zxzipfile;
		if (zxfile != null) {
			// 从zxfile中找出ste.model
			File tempfile = null;
			try {
				tempfile = File.createTempFile("temp", ".model");
				if (ZipHelper.extractFile(zxfile, "mde.rule", tempfile)) {
					rd = null;
					rd = DBColumnInfoStoreHelp.getReaderFromFile(tempfile);
					Vector<Rulebase> rules = RuleRepository.loadRules(rd);
					rd.close();
					ruleeng = new Ruleenginee();
					ruleeng.setRuletable(rules);
					return;
				}
			} catch (Exception e) {
				logger.error("e", e);
			} finally {
				if (tempfile != null) {
					tempfile.delete();
				}
			}
		}

		String classname = this.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p >= 0) {
			classname = classname.substring(p + 1);
		}
		URL url = this.getClass().getResource(classname + ".rule");
		if (url == null) {
			return;
		}
		String pathname = url.toString();
		logger.debug("加载rule文件:" + pathname);
		File f = new File(pathname);
		try {
			rd = DBColumnInfoStoreHelp.getReaderFromFile(f);
			Vector<Rulebase> rules = RuleRepository.loadRules(rd);
			ruleeng = new Ruleenginee();
			ruleeng.setRuletable(rules);
		} catch (Exception e) {
			logger.error("load " + url.toString(), e);
		} finally {
			if (rd != null)
				try {
					rd.close();
				} catch (IOException e) {
				}
		}

	}

	/**
	 * 返回后处理存储过程名
	 * 
	 * @return
	 */
	public String getStoreprocname() {
		if (ruleeng == null)
			return null;
		return ruleeng.processStoreproc(this, "后处理存储过程");
	}

	class ExportinfoDlg extends CDialog {
		File outf = null;

		public ExportinfoDlg(File outf) {
			super(getParentFrame(), "导出成功", true);
			this.outf = outf;
			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());
			CLabel lb = new CLabel("输出成功, 文件:" + outf.getAbsolutePath());
			cp.add(lb, BorderLayout.CENTER);

			JPanel bottomp = new JPanel();
			cp.add(bottomp, BorderLayout.SOUTH);

			CButton btn = new CButton("关闭");
			btn.setActionCommand("close");
			btn.addActionListener(this);
			bottomp.add(btn);

			btn = new CButton("打开目录");
			btn.setActionCommand("opendir");
			btn.addActionListener(this);
			bottomp.add(btn);

			localCenter();
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("close")) {
				dispose();
			} else if (e.getActionCommand().equals("opendir")) {
				String cmd = "explorer \""
						+ outf.getParentFile().getAbsolutePath() + "\"";
				try {
					Runtime.getRuntime().exec(cmd);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				dispose();
			}
		}
	}

	/**
	 * @return the zxzipfile
	 */
	public File getZxzipfile() {
		return zxzipfile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inca.np.gui.ste.CModelBase#setOpid(java.lang.String)
	 */
	@Override
	public void setOpid(String opid) {
		// TODO Auto-generated method stub
		super.setOpid(opid);
		zxzipfile = DownloadManager.getInst().getZxfile(opid);
	}

	public boolean isSaveimmdiate() {
		return saveimmdiate;
	}

	public void setSaveimmdiate(boolean saveimmdiate) {
		this.saveimmdiate = saveimmdiate;
	}

	public void reset() {
		mastermodel.reset();
		detailmodel.reset();
	}

	/**
	 * 保存前检查。返回0为可以继续保存
	 * 
	 * @return
	 */
	protected int on_beforesave() {
		boolean usefile = getMasterModel().isUseattachfile()
				|| getDetailModel().isUseattachfile();
		int uploadfilect = 0;
		if (usefile) {
			if (!getMasterModel().uploadFiles()) {
				return -1;
			}
			uploadfilect += getMasterModel().getUploadedfilecount();

			Iterator<DBTableModel> it = detaildbmodelmap.values().iterator();
			while (it.hasNext()) {
				DBTableModel dtlmodel = it.next();
				if (!this.getDetailModel().uploadFiles(dtlmodel)) {
					return -1;
				}
				uploadfilect += getDetailModel().getUploadedfilecount();
			}
		}

		// 保存细单到cache
		/*
		 * 应该不需要20070913 if
		 * (detailmodel.getDBtableModel().getModifiedData().getRowCount() > 0) {
		 * this.cacheDetailMemds(mastermodel.getRow()); }
		 */if (getModifiedRowCount() == 0) {
			if (usefile && uploadfilect > 0) {
				getMasterModel().getDBtableModel().resetWantuploadfiles();
				Iterator<DBTableModel> it = detaildbmodelmap.values()
						.iterator();
				while (it.hasNext()) {
					DBTableModel dtlmodel = it.next();
					dtlmodel.resetWantuploadfiles();
				}

				warnMessage("文件上传成功", "附件文件已上传");
				return 0;
			} else {
				warnMessage("不需要保存", "没有修改的数据,不需要保存");
				return -1;
			}
		}

		for (int mr = 0; mr < mastermodel.getRowCount(); mr++) {
			int masterdbstatus = mastermodel.getDBtableModel().getdbStatus(mr);
			if (masterdbstatus == RecordTrunk.DBSTATUS_NEW
					|| masterdbstatus == RecordTrunk.DBSTATUS_MODIFIED) {
				// 必须有细单
				String tmppkid = mastermodel.getDBtableModel().getTmppkid(mr);
				DBTableModel dtlmodel = detaildbmodelmap.get(tmppkid);
				if (!isAllownodetail()
						&& (dtlmodel == null || dtlmodel.getRowCount() == 0)) {
					warnMessage("不能保存", "第" + (mr + 1) + "行总单没有细单,不能保存");
					mastermodel.setRow(mr);
					return -1;
				}
			}
		}

		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_beforesave(this);
			if (ret != 0) {
				return ret;
			}
		}

		if (0 != mastermodel.on_beforesave()) {
			return -1;
		}

		// 对每一行修改过的总单，都要检查细单
		for (int mrow = 0; mrow < mastermodel.getDBtableModel().getRowCount(); mrow++) {
			int dbstatus = mastermodel.getDBtableModel().getdbStatus(mrow);
			if (RecordTrunk.DBSTATUS_SAVED != dbstatus) {
				mastermodel.setRow(mrow);
				// 检查细单
				if (0 != detailmodel.on_beforesave()) {
					return -1;
				}
			}
		}

		return 0;
	}

	/**
	 * 取细单的dbtablemodel
	 * 
	 * @param row
	 *            总单行
	 * @return
	 */
	public DBTableModel getDetaildbmodel(int row) {
		String tmppkid = mastermodel.getDBtableModel().getTmppkid(row);
		return detaildbmodelmap.get(tmppkid);
	}

	/**
	 * 返回所有支持的打印方案
	 * 
	 * @return
	 */
	public Vector<String> getPrintplans() {
		return getMasterModel().getPrintplans();
	}

	/**
	 * 设置所有支持的打印方案
	 * 
	 * @param plans
	 */
	public void setPrintplans(Vector<String> plans) {
		getMasterModel().setPrintplans(plans);
	}

	/**
	 * 单据打印
	 * 
	 * @param planname
	 *            方案名
	 */
	protected void docPrint(String planname) {
		mastermodel.docPrint(planname);
	}

	public boolean isUsequerythread() {
		return usequerythread;
	}

	public void setUsequerythread(boolean usequerythread) {
		this.usequerythread = usequerythread;
	}

	/**
	 * 由总单的ID查询细单的where条件.
	 * 
	 * @param mastervalue
	 *            总单值
	 * @return
	 */
	protected String getRetrievedetailWheres(String mastervalue) {
		String coltype = detailmodel.getDBtableModel().getColumnDBType(
				detailrelatecolname);
		if (coltype == null) {
			logger.error("细单找不到列名" + detailrelatecolname);
			return "1=2";
		}

		String wheres = detailrelatecolname;
		if (coltype.equalsIgnoreCase("varchar")) {
			mastervalue = "'" + mastervalue + "'";
		}
		wheres += "=" + mastervalue;
		return wheres;
	}

	/**
	 * 清单保存成功的
	 */
	protected void clearSaved() {
		DBTableModel mdbmodel = getMasterModel().getDBtableModel();
		for (int mr = 0; mr < mdbmodel.getRowCount(); mr++) {
			if (mdbmodel.getdbStatus(mr) != RecordTrunk.DBSTATUS_SAVED) {
				continue;
			}
			String tmppkid = mdbmodel.getTmppkid(mr);
			logger.debug("clearSaved(),tmppkid=" + tmppkid);
			DBTableModel detailmodel = detaildbmodelmap.get(tmppkid);
			detaildbmodelmap.remove(tmppkid);
			if (detailmodel != null) {
				detailmodel.clearAll();
				// 删除总单记录
			}
			mdbmodel.removeRow(mr);
			// 行号要减一
			mr--;
		}
		getMasterModel().getSumdbmodel().fireDatachanged();
		getMasterModel().tableChanged();
		getDetailModel().recreateDBModel();

	}

	public boolean isResetbeforenew() {
		return resetbeforenew;
	}

	public void setResetbeforenew(boolean resetbeforenew) {
		this.resetbeforenew = resetbeforenew;
	}

}
