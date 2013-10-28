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
 * �ܵ�ϸĿModel �����ܵ�mastermodel, ϸ��detailmodel,�Լ��ܵ���ϸ���Ĺ�������
 */
public abstract class CMdeModel extends CModelBase {
	public static final String ACTION_NEW = "����";
	public static final String ACTION_MODIFY = "�޸�";
	public static final String ACTION_UNDO = "�����޸�";
	public static final String ACTION_UNDODTL = "����ϸ���޸�";
	public static final String ACTION_HIDEFORM = "����";
	public static final String ACTION_DEL = "ɾ��";
	public static final String ACTION_QUERY = "��ѯ";
	public static final String ACTION_REFRESH = "ˢ��";
	public static final String ACTION_SAVE = "����";
	public static final String ACTION_EXPORT = "����";
	public static final String ACTION_EXPORTAS = "����Ϊ";
	public static final String ACTION_EXPORTMASTERAS = "�ܵ�����Ϊ";
	public static final String ACTION_EXPORTDETAILAS = "ϸ������Ϊ";

	public static final String ACTION_NEXT = "��һ��";
	public static final String ACTION_PRIOR = "��һ��";
	public static final String ACTION_FIRST = "��һ��";
	public static final String ACTION_LAST = "�����";

	public static final String ACTION_NEWDTL = "����ϸ��";
	public static final String ACTION_MODIFYDTL = "�޸�ϸ��";
	public static final String ACTION_DELDTL = "ɾ��ϸ��";

	public static final String ACTION_NEXTDTL = "��һ��ϸ��";
	public static final String ACTION_PRIORDTL = "��һ��ϸ��";
	public static final String ACTION_FIRSTDTL = "��һ��ϸ��";
	public static final String ACTION_LASTDTL = "�����ϸ��";

	public static final String ACTION_SETUPUI = "��������";
	public static final String ACTION_SAVEUI = "�������";

	public static final String ACTION_SETUPRULE = "��������";

	public static final String ACTION_SETUPUIDTL = "ϸ����������";
	public static final String ACTION_SAVEUIDTL = "ϸ���������";

	public static final String ACTION_PRINTSETUP = "��ӡ����";
	public static final String ACTION_PRINT = "��ӡ";

	public static final String ACTION_SELECTOP = "ѡ����";
	public static final String ACTION_EXIT = "�˳�����";
	public static final String ACTION_SELFCHECK = "�Լ�";
	public static final String ACTION_DOCPRINT_PREFIX = "DOCPRINT_";

	/**
	 * �ܵ�Model
	 */
	protected CMasterModel mastermodel = null;

	/**
	 * ϸ��Model
	 */
	protected CDetailModel detailmodel = null;

	/**
	 * �ܵ���������,Ӧ������������
	 */
	protected String masterrelatecolname = "";

	/**
	 * ϸ����������
	 */
	protected String detailrelatecolname = "";

	/**
	 * key���ܵ�����ʱ����,����ΪString, ֵΪϸ����DBTableModel
	 */
	protected HashMap<String, DBTableModel> detaildbmodelmap = new HashMap<String, DBTableModel>();

	Category logger = Category.getInstance(CMdeModel.class);

	/**
	 * ��ӡFrame
	 */
	private PrintSetupFrame printsetupfrm;

	/**
	 * �������� add by wwh 20071126
	 */
	protected Ruleenginee ruleeng = null;

	/**
	 * �Ƿ����ר�������
	 */
	protected boolean zxmodify = false;

	protected File zxzipfile = null;

	protected ZxmdejavaDelegate zxmdejavadelegate = null;

	/**
	 * �ǲ����б��棿
	 */
	protected boolean saveimmdiate = false;

	boolean usequerythread = true;

	/**
	 * �Ƿ������ܵ�ǰҪ����ѱ���ɹ���
	 */
	protected boolean resetbeforenew = false;

	/**
	 * ϸ����ѯ�߳�����.��ֹһ���ܵ�tmppkid�ж���̲߳�ѯ.
	 */
	HashMap<String, QuerydtlThread> querydtlthreadmap = new HashMap<String, QuerydtlThread>();

	public CMdeModel() {
		super();
	}

	/**
	 * ����
	 * 
	 * @param frame
	 *            ����
	 * @param title
	 *            ����
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
	 * ����delegateר��
	 */
	protected void initDelegate() {
		zxmdejavadelegate = ZxmdejavaDelegate.loadZxfromzxzip(this);
	}

	/**
	 * �����ܵ�Model
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
	 * ����ϸ��Model
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
	 * �����ܵ�Model,��������
	 * 
	 * @return
	 */
	protected abstract CMasterModel createMastermodel();

	/**
	 * ����ϸ��Model,��������
	 * 
	 * @return
	 */
	protected abstract CDetailModel createDetailmodel();

	/**
	 * �����ܵ���������,��������
	 * 
	 * @return
	 */
	public abstract String getMasterRelatecolname();

	/**
	 * ����ϸ����������,��������
	 * 
	 * @return
	 */
	public abstract String getDetailRelatecolname();

	/**
	 * ��ѯϸ��.
	 * ʹ����detaildbmodelmap��Ϊϸ����cache.ȡ�ܵ���ǰ�е���ʱ����,��ѯcache.�������ʹ����Ϊϸ��table������Դ,û����
	 * ͨ����������ѯϸ������,������cache��.
	 * 
	 * @param newrow
	 *            �ܵ���ǰ��
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
					"ϸ����" + olddetailmodel.getRowCount() + "����¼");
			return;
		}

		String value = mastermodel.getDBtableModel().getItemValue(newrow,
				masterrelatecolname);
		if (value.length() == 0) {
			detailmodel.recreateDBModel();
			cacheDetailMemds(tmppkid, detailmodel.getDBtableModel());
			return;
		}

		// ��ѯϸ��ֵ
		int detailrow = detailmodel.getRow();
		if (detailmodel.getRowCount() > 0 && detailrow >= 0) {
			String detailvalue = detailmodel.getDBtableModel().getItemValue(
					detailrow, detailrelatecolname);
			if (detailvalue == null) {
				logger.error("ϸ���Ҳ�������" + detailrelatecolname);
				return;
			}
			if (value.equals(detailvalue)) {
				// ˵������ϸ�����ֵ���ǶԵģ�����Ҫ��ѯ
				return;
			}
		}
		String wheres = getRetrievedetailWheres(value);

		// 20090109 �����������߳�������״̬.
		synchronized (querydtlthreadmap) {
			if (querydtlthreadmap.get(tmppkid) != null) {
				logger.debug("�����߳��ڲ�ѯ�ܵ�tmppkid=" + tmppkid + ",����");
				return;
			}
			for (;;) {
				if (querydtlthreadmap.size() > 0) {
					try {
						logger.debug("���߳��ڽ���,�ȴ�.....");
						querydtlthreadmap.wait();
						continue;
					} catch (InterruptedException e) {
					}
				}
				logger.debug("׼�������߳�,tmppkid=" + tmppkid);
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
	 * ����QuerydtlThread,��ʱ400ms,����ܵ����Ǹղŵ���,�ٽ��в�ѯ. ��ֹ������Ա���ٹ�����ɵĶ�����ò�ѯ
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
			logger.debug("�߳�run()��ʼ,tmppkid=" + tmppkid);
			try {
				if (usequerythread) {
					try {
						Thread.sleep(DefaultNPParam.mderetrievedtldeplay);
					} catch (InterruptedException e) {
					}
					int currow = getMasterModel().getRow();
					if (memrow != currow) {
						System.out.println("currow=" + currow + ",�ϴε�row="
								+ memrow + ",���Բ���ѯϸ����");

						// ���￪����
						logger.debug("tmppkid=" + tmppkid + ", ��ǰ���ƶ�̫��,����Ҫ��ѯ");
						synchronized (querydtlthreadmap) {
							querydtlthreadmap.remove(tmppkid);
							querydtlthreadmap.notifyAll();
						}

						return;
					}
				}
				getDetailModel().setStatusmessage("��ʼ��ѯϸ��....");
				cacheDetailMemds(tmppkid, dtldm);

				// ������Կ�����
				logger.debug("tmppkid=" + tmppkid + ", �߳̽������");

				synchronized (querydtlthreadmap) {
					querydtlthreadmap.remove(tmppkid);
					querydtlthreadmap.notifyAll();
				}

				// ���ﲻ��Ҫ�ٴ����߳��˰�
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
	 * ��ϸ��������Դ����detaildbmodelmap��.
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
	 * ���ܵ�tmppkid,ϸ��detaildbmodel����detaildbmodelmap��.
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
			logger.error("ϸ�����Ҳ�������" + detailrelatecolname);
			return;
		}
		if (detailvalue.length() == 0) {
			return;
		}

		// ��ѯ�ܵ�ֵ
		int masterrow = mastermodel.getRow();
		if (masterrow >= 0) {
			String mastervalue = mastermodel.getDBtableModel().getItemValue(
					masterrow, masterrelatecolname);
			if (mastervalue == null) {
				logger.error("�ܵ��Ҳ�������" + mastervalue);
				return;
			}
			if (detailvalue.equals(mastervalue)) {
				// ˵�������ܵ����ֵ���ǶԵģ�����Ҫ��ѯ
				return;
			}
		}
		String coltype = mastermodel.getDBtableModel().getColumnDBType(
				masterrelatecolname);
		if (coltype == null) {
			logger.error("�ܵ��Ҳ�������" + detailrelatecolname);
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
	 * �����ܵ�ϸ��һ���ж��ټ�¼���޸Ĺ�
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
	 * �Ƿ�����û��ϸ��
	 * 
	 * @return true����û��ϸ�� false����û��ϸ��
	 */
	protected boolean isAllownodetail() {
		return false;
	}

	/**
	 * ����. ���������ܵ���ϸ��. ��һ���ܵ���¼�����ϸ��ȫ������ɹ�Ϊһ������.
	 * 
	 * @return
	 */
	public int doSave() {
		if (ruleeng != null) {
			if (ruleeng.process(this, "���α���", 0) < 0)
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
		 * ������Զ����档��doHideform()�о͵�����slient���棬����Ҫ�ٱ��档
		 */
		if (saveimmdiate) {
			return 0;
		}

		if (0 != on_beforesave()) {
			return -1;
		}

		DBTableModel mastersaveds = mastermodel.getModifiedDbmodel();

		// �����ݱ�������ύ.
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
					// �����к�
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
	 * ��̨�����ر���
	 * 
	 * @return
	 */
	public int doSaveSlient() {
		if (commitcount > 0)
			return 0;
		if (getModifiedRowCount() == 0)
			return 0;

		if (ruleeng != null) {
			if (ruleeng.process(this, "���α���", 0) < 0)
				return -1;
		}
		mastermodel.commitEdit();
		detailmodel.commitEdit();

		if (0 != on_beforesave()) {
			return -1;
		}

		DBTableModel mastersaveds = mastermodel.getModifiedDbmodel();

		// �����ݱ�������ύ.
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
					// �����к�
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
	 * ���浽����������ʹ���߳�
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

		// �����ݱ�������ύ.
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
					// �����к�
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

		progress.appendMessage("�����ύ���ݣ��ȴ���������Ӧ....");

		if (!lockData()) {
			warnMessage("��Ҫ�ظ��ύ", "��Ҫ�ظ��ύ");
			progress.messageBox("��ʾ", "û�б���");
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
				progress.messageBox("����ʧ��", cmdstring);
				return -1;
			}
			ResultCommand resultcmd1 = (ResultCommand) svrresp.commandAt(0);
			mastermodel.setLineresults(resultcmd1.getLineresults());
			mastermodel.getDBtableModel().clearDeleted();
			// �����ܵ�ʧ��������
			int errorct = 0;
			Enumeration<RecordTrunk> en = resultcmd1.getLineresults()
					.elements();
			while (en.hasMoreElements()) {
				RecordTrunk lineResult = en.nextElement();
				if (lineResult.getSaveresult() != 0) {
					errorct++;
				}
			}

			// ����ϸ���ķ���ֵ
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

			// ��λ���ܵ���һ������ɹ�
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
				sb.append("����ɹ�");
			} else {
				if (errorct == 0) {
					sb.append("�ܵ�����ɹ�");
				} else {
					sb.append("�ܵ�����ʧ��" + errorct + "����¼��");
				}
				if (detailerrorct == 0) {
					sb.append("ϸ������ɹ�");
				} else {
					sb.append("ϸ������ʧ��" + detailerrorct + "����¼��");
				}
			}
			// infoMessage("������", sb.toString());

			progress.messageBox("�������", "�������:" + sb.toString());

		} catch (Exception e) {
			logger.error("�������", e);
			progress.messageBox("����ʧ��", "���ݱ����쳣������ԭ��:" + e.getMessage());
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
	 * �����
	 */
	protected void on_save() {
		if (zxmdejavadelegate != null) {
			zxmdejavadelegate.on_save(this);
		}
	}

	/**
	 * �����߳�
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
			progress.appendMessage("�����ύ���ݣ��ȴ���������Ӧ....");

			if (!lockData()) {
				warnMessage("��Ҫ�ظ��ύ", "��Ҫ�ظ��ύ");
				progress.messageBox("��ʾ", "û�б���");
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
					progress.messageBox("����ʧ��", cmdstring);
					return;
				}
				ResultCommand resultcmd1 = (ResultCommand) svrresp.commandAt(0);
				mastermodel.setLineresults(resultcmd1.getLineresults());
				mastermodel.getDBtableModel().clearDeleted();
				// �����ܵ�ʧ��������
				int errorct = 0;
				Enumeration<RecordTrunk> en = resultcmd1.getLineresults()
						.elements();
				while (en.hasMoreElements()) {
					RecordTrunk lineResult = en.nextElement();
					if (lineResult.getSaveresult() != 0) {
						errorct++;
					}
				}

				// ����ϸ���ķ���ֵ
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

				// ��λ���ܵ���һ������ɹ�
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
					sb.append("����ɹ�");
				} else {
					if (errorct == 0) {
						sb.append("�ܵ�����ɹ�");
					} else {
						sb.append("�ܵ�����ʧ��" + errorct + "����¼��");
					}
					if (detailerrorct == 0) {
						sb.append("ϸ������ɹ�");
					} else {
						sb.append("ϸ������ʧ��" + detailerrorct + "����¼��");
					}
				}
				// infoMessage("������", sb.toString());

				progress.messageBox("�������", "�������:" + sb.toString());

			} catch (Exception e) {
				logger.error("�������", e);
				progress.messageBox("����ʧ��", "���ݱ����쳣������ԭ��:" + e.getMessage());
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
				// warnMessage("��Ҫ�ظ��ύ", "��Ҫ�ظ��ύ");
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
				// �����ܵ�ʧ��������
				int errorct = 0;
				Enumeration<RecordTrunk> en = resultcmd1.getLineresults()
						.elements();
				while (en.hasMoreElements()) {
					RecordTrunk lineResult = en.nextElement();
					if (lineResult.getSaveresult() != 0) {
						errorct++;
					}
				}

				// ����ϸ���ķ���ֵ
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
				logger.error("�������", e);
				return;
			} finally {
				unloakData();
			}
		}
	}

	/**
	 * ��ѯǰ���
	 * 
	 * @return ��0 ���ܲ�ѯ
	 */
	public int on_beforequery() {
		// ��û���޸ĵ��أ�
		int mastermodirow = mastermodel.getDBtableModel().getModifiedData()
				.getRowCount();
		int detailmodirow = 0;
		Iterator it = detaildbmodelmap.values().iterator();
		while (it.hasNext()) {
			DBTableModel dbmodel = (DBTableModel) it.next();
			detailmodirow += dbmodel.getModifiedData().getRowCount();
		}
		if (mastermodirow + detailmodirow > 0) {
			String msg = "�����޸�û�б���,���������ѯ�ᶪʧ�޸ĵ�����,��������ѯ��?";
			int ret = JOptionPane.showConfirmDialog(this.getParentFrame(), msg,
					"����", JOptionPane.YES_NO_OPTION,
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
	 * �ر�ǰ���.
	 * 
	 * @return ��0���ܹر�
	 */
	public int on_beforeclose() {
		if (zxmdejavadelegate != null) {
			int ret = zxmdejavadelegate.on_beforeclose(this);
			if (ret != 0) {
				return ret;
			}
		}

		if (getModifiedRowCount() > 0) {
			String msg = "�������޸�,�ǲ���Ҫ����?";
			int ret = JOptionPane.showConfirmDialog(this.getParentFrame(), msg,
					"����", JOptionPane.YES_NO_CANCEL_OPTION,
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
	 * �Ƿ��ܵ����ڲ�ѯ
	 * 
	 * @return
	 */
	public boolean isquerying() {
		return mastermodel.isquerying();
	}

	/**
	 * �����ѯ������ѯ
	 */
	public void doQuery() {
		if (ruleeng != null) {
			if (ruleeng.process(this, "���β�ѯ", 0) < 0)
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
	 * ��ֹ��ѯ
	 */
	public void stopQuery() {
		mastermodel.stopQuery();
	}

	/**
	 * �����ܵ�
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
	 * ɾ���ܵ�
	 */
	public void doDel() {
		mastermodel.doDel();
	}

	/**
	 * �޸��ܵ�
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
	 * �����ܵ���Ƭ����
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
	 * ��λ�ܵ���һ��
	 */
	public void doFirstRow() {
		mastermodel.doFirstRow();
	}

	/**
	 * ��λ�ܵ����һ��
	 */
	public void doLastRow() {
		mastermodel.doLastRow();
	}

	/**
	 * ��λ�ܵ���һ��
	 */
	public void doNextRow() {
		mastermodel.doNextRow();
	}

	/**
	 * ��λ�ܵ���һ��
	 */
	public void doPriorRow() {
		mastermodel.doPriorRow();
	}

	/**
	 * ��λϸ����һ��
	 */
	public void doFirstDtlRow() {
		detailmodel.doFirstRow();
	}

	/**
	 * ��λϸ�����һ��
	 */
	public void doLastDtlRow() {
		detailmodel.doLastRow();
	}

	/**
	 * ��λϸ����һ��
	 */
	public void doNextDtlRow() {
		detailmodel.doNextRow();
	}

	/**
	 * ��λϸ����һ��
	 */
	public void doPriorDtlRow() {
		detailmodel.doPriorRow();
	}

	/**
	 * ����ϸ��
	 */
	public void doNewdtl() {
		int masterrow = mastermodel.getRow();
		if (masterrow < 0) {
			warnMessage("��������", "���������ܵ���������ϸ��");
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
		 * ����ϸ����Ҫ����ܵ� if (mastermodel.getSteformwindow().isVisible()) { if
		 * (!mastermodel.doHideform()) { return; } }
		 */
		detailmodel.doNew();
	}

	/**
	 * �޸�ϸ��
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
	 * ɾ��ϸ��
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
	 * �ܷ��޸��ܵ�
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
			// warnMessage("�����޸�", "ϸ�����м�¼���ܵ����ܸ�");
			return -1;
		}
		return 0;
	}

	/**
	 * �ܵ��¼�������
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

			// ����ܵ���������Ҫ��ʾϸ��Ҫɾ��
			RecordTrunk masterrec = mastermodel.getDBtableModel()
					.getRecordThunk(row);
			if (masterrec.getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
				String msg = "�ܵ��������ģ�����㳷�������ܵ���ϸ��ҲҪ�Զ���������ȷ����";
				int ret = JOptionPane.showConfirmDialog(getParentFrame(), msg,
						"����", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
				if (ret != JOptionPane.OK_OPTION) {
					return -1;
				}

				// ��ϸ��ȫ��ɾ��
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

			String msg = "�����ɾ���ܵ���ϸ��ҲҪ�Զ�ɾ������ȷ����";
			int ret = JOptionPane.showConfirmDialog(getParentFrame(), msg,
					"����", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				return -1;
			}

			// ����ܵ�������,��ϸ��һ����.
			DBTableModel dtldbmodel = detailmodel.getDBtableModel();
			int masterdbstatus = mastermodel.getdbStatus(row);
			if (RecordTrunk.DBSTATUS_NEW == masterdbstatus) {
				dtldbmodel.clearAll();
				detailmodel.getSumdbmodel().fireDatachanged();
				detailmodel.setRow(-1);
				detailmodel.tableChanged();
			} else {
				// ��ϸ��ȫ����Ϊɾ��״̬
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
	 * ϸ��model������
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

			// �ܵ��м�¼���У�
			int masterrow = mastermodel.getRow();
			if (masterrow < 0) {
				warnMessage("��ʾ", "����������ܵ���¼");
				return -1;
			}
			/*
			 * ����ܵ���
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
			// ȡ�ܵ���¼
			int masterrow = mastermodel.getRow();
			String masterv = mastermodel.getDBtableModel().getItemValue(
					masterrow, masterrelatecolname);
			// ���õ�ǰֵ
			detailmodel.getDBtableModel().setItemValue(row,
					detailrelatecolname, masterv);
			// ���������ܵ�
			mastermodel.bindDataSetEnable(mastermodel.getRow());
		}

		public void on_itemvaluechange(int row, String colname, String value) {
			super.on_itemvaluechange(row, colname, value);
			// ϸ���޸ģ��ܵ�Ҳ��
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
			// ϸ��ɾ�����ܵ�Ҫ�޸�
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
	 * ���ӵķǱ�׼�����
	 * 
	 * @param command
	 * @return ����0�Ѵ�����Ҫ�ٴ������ط�0������δ������Ҫ��������
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
	 * �ܷ�ɾ��ϸ��
	 * 
	 * @param row
	 * @return
	 */
	protected int on_beforedel(int row) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * �ܷ�����ϸ��
	 * 
	 * @return
	 */
	protected int on_beforenewdtl() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * �ܷ�ϸ���޸�
	 * 
	 * @param row
	 * @return
	 */
	public int on_beforemodifydtl(int row) {
		return 0;
	}

	/**
	 * �¼�����.������on_actionPerformed()
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
	 * ���ù���
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
			// Ҫ����classes/ר���/opid.zip���ļ��У����ϴ���������
			File dir = CurrentdirHelper.getZxdir();
			File zxfile = new File(dir, opid + ".zip");
			File tempfile = null;
			try {
				tempfile = File.createTempFile("temp", ".rule");
				RuleRepository.saveRule(tempfile, ruleeng.getRuletable());
				ZipHelper.replaceZipfile(zxfile, "mde.rule", tempfile);

				// �ϴ�
				ZxmodifyUploadHelper zu = new ZxmodifyUploadHelper();
				if (!zu.uploadZxfile(opid, zxfile)) {
					errorMessage("�ϴ�����", zu.getErrormessage());
					return;
				}

			} catch (Exception e) {
				logger.error("e", e);
				errorMessage("����", e.getMessage());
				return;
			} finally {
				if (tempfile != null)
					tempfile.delete();
			}
		}

		// ����classĿ¼�´�һ��
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
			// ˵����һ��JAR�ļ���.Ҫ�ҵ����jar�ļ����ڵ�Ŀ¼�� ../classes
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
				errorMessage("save rule", "�����������ʧ��");
			}
		}
	}

	/**
	 * �Լ�
	 */
	protected void doSelfcheck() {
		String s = selfCheck();
		if (s == null || s.length() == 0)
			return;
		CDefaultProgress prop = new CDefaultProgress(this.getParentFrame());
		prop.appendMessage("�Լ�ERROR:\r\n");
		prop.messageBox("", s);
		prop.show();
	}

	/**
	 * ����ǰ������0���Լ���������
	 * 
	 * @return
	 */
	protected int on_beforeexport() {
		return 0;
	}

	/**
	 * ����ܵ�ϸ����excel�ļ�
	 */
	protected void doExport() {
		if (on_beforeexport() != 0)
			return;
		setWaitCursor();

		File outdir = new File("���EXCEL");
		outdir.mkdirs();
		File outf = new File(outdir, getTitle() + ".xls");

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet0 = wb.createSheet(getMasterModel().getTitle());
		int mr = getMasterModel().getRow();
		if (mr < 0) {
			warnMessage("����", "û������");
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
			errorMessage("����", e.getMessage());
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
			warnMessage("����", "û������");
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
			errorMessage("����", e.getMessage());
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
	 * �ܵ�����Ϊ
	 */
	protected void doExportmasteras() {
		if (on_beforeexport() != 0)
			return;
		getMasterModel().doExportas();

	}

	/**
	 * ϸ������Ϊ
	 */
	protected void doExportdetailas() {
		if (on_beforeexport() != 0)
			return;
		getDetailModel().doExportas();

	}

	/**
	 * �˳�
	 */
	protected void doExit() {
		if (0 != on_beforeclose()) {
			return;
		}
		onstopRun();
		getParentFrame().dispose();
	}

	/**
	 * �˳�����
	 */
	public void onstopRun() {
		super.onstopRun();
		if (DefaultNPParam.debug == 1 || DefaultNPParam.develop == 1) {
			doSelfcheck();
		}
		freeMemory();
	}

	/**
	 * �ͷ��ڴ�
	 */
	public void freeMemory() {
		// Ҫ�ͷŵ�cachemap
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
	 * ����ѡ���ܲ˵�
	 * 
	 * @param x
	 * @param y
	 */
	protected void on_selectop(int x, int y) {
		JPopupMenu selectopMenu = this.createSelectopMenu();
		selectopMenu.show(this.getParentFrame(), x, y);
	}

	/**
	 * ��ӡ����
	 */
	protected void printSetup() {
		printsetupfrm = new PrintSetupFrame(opid, zxmodify, getMasterModel(),
				getDetailModel());
		printsetupfrm.pack();
		printsetupfrm.setVisible(true);
	}

	/**
	 * ��ӡ
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
		// ���ر���,��ӡ
		if (printsetupfrm == null) {
			printsetupfrm = new PrintSetupFrame(opid, zxmodify,
					getMasterModel(), getDetailModel());
		}
		printsetupfrm.pack();

		logger.info("���÷��͵���ӡ��doPrint()");
		if (!printsetupfrm.doPrint(reportname)) {
			printsetupfrm.setVisible(true);
		}

	}

	/**
	 * �����ܵ��޸�
	 */
	public void doUndo() {
		// �����ܵ��޸�
		mastermodel.doUndo();
	}

	/**
	 * ����ϸ���޸�
	 */
	public void doUndodtl() {
		// �����ܵ��޸�
		detailmodel.doUndo();
	}

	/**
	 * ��ʾ��Ϣ
	 * 
	 * @param title
	 *            ����
	 * @param msg
	 *            ��Ϣ����
	 */
	protected void infoMessage(String title, String msg) {
		//implMessage(title, msg, JOptionPane.INFORMATION_MESSAGE);
		CMessageDialog.infoMessage(getParentFrame(), title, msg);
	}

	/**
	 * ������Ϣ
	 * 
	 * @param title
	 *            ����
	 * @param msg
	 *            ��Ϣ����
	 */
	protected void errorMessage(String title, String msg) {
		//implMessage(title, msg, JOptionPane.ERROR_MESSAGE);
		CMessageDialog.errorMessage(getParentFrame(), title, msg);
	}

	/**
	 * ������Ϣ
	 * 
	 * @param title
	 *            ����
	 * @param msg
	 *            ��Ϣ����
	 */
	protected void warnMessage(String title, String msg) {
		//implMessage(title, msg, JOptionPane.WARNING_MESSAGE);
		CMessageDialog.warnMessage(getParentFrame(), title, msg);

	}

	/**
	 * ��ʾ��Ϣ
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
	 * ���ù��Ϊɳ©�ȴ�
	 */
	protected void setWaitCursor() {
		this.getParentFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * ���ù��Ϊȱʡ��ͷ
	 */
	protected void setDefaultCursor() {
		this.getParentFrame().setCursor(
				Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * �Լ�
	 * 
	 * @return
	 */
	public String selfCheck() {
		String s = getMasterModel().selfCheck();
		s += getDetailModel().selfCheck();
		return s;
	}

	/**
	 * ���ϸ��cache
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
		// ����ǲ�����ר�
		BufferedReader rd = null;
		File zxfile = zxzipfile;
		if (zxfile != null) {
			// ��zxfile���ҳ�ste.model
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
		logger.debug("����rule�ļ�:" + pathname);
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
	 * ���غ���洢������
	 * 
	 * @return
	 */
	public String getStoreprocname() {
		if (ruleeng == null)
			return null;
		return ruleeng.processStoreproc(this, "����洢����");
	}

	class ExportinfoDlg extends CDialog {
		File outf = null;

		public ExportinfoDlg(File outf) {
			super(getParentFrame(), "�����ɹ�", true);
			this.outf = outf;
			Container cp = getContentPane();
			cp.setLayout(new BorderLayout());
			CLabel lb = new CLabel("����ɹ�, �ļ�:" + outf.getAbsolutePath());
			cp.add(lb, BorderLayout.CENTER);

			JPanel bottomp = new JPanel();
			cp.add(bottomp, BorderLayout.SOUTH);

			CButton btn = new CButton("�ر�");
			btn.setActionCommand("close");
			btn.addActionListener(this);
			bottomp.add(btn);

			btn = new CButton("��Ŀ¼");
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
	 * ����ǰ��顣����0Ϊ���Լ�������
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

		// ����ϸ����cache
		/*
		 * Ӧ�ò���Ҫ20070913 if
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

				warnMessage("�ļ��ϴ��ɹ�", "�����ļ����ϴ�");
				return 0;
			} else {
				warnMessage("����Ҫ����", "û���޸ĵ�����,����Ҫ����");
				return -1;
			}
		}

		for (int mr = 0; mr < mastermodel.getRowCount(); mr++) {
			int masterdbstatus = mastermodel.getDBtableModel().getdbStatus(mr);
			if (masterdbstatus == RecordTrunk.DBSTATUS_NEW
					|| masterdbstatus == RecordTrunk.DBSTATUS_MODIFIED) {
				// ������ϸ��
				String tmppkid = mastermodel.getDBtableModel().getTmppkid(mr);
				DBTableModel dtlmodel = detaildbmodelmap.get(tmppkid);
				if (!isAllownodetail()
						&& (dtlmodel == null || dtlmodel.getRowCount() == 0)) {
					warnMessage("���ܱ���", "��" + (mr + 1) + "���ܵ�û��ϸ��,���ܱ���");
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

		// ��ÿһ���޸Ĺ����ܵ�����Ҫ���ϸ��
		for (int mrow = 0; mrow < mastermodel.getDBtableModel().getRowCount(); mrow++) {
			int dbstatus = mastermodel.getDBtableModel().getdbStatus(mrow);
			if (RecordTrunk.DBSTATUS_SAVED != dbstatus) {
				mastermodel.setRow(mrow);
				// ���ϸ��
				if (0 != detailmodel.on_beforesave()) {
					return -1;
				}
			}
		}

		return 0;
	}

	/**
	 * ȡϸ����dbtablemodel
	 * 
	 * @param row
	 *            �ܵ���
	 * @return
	 */
	public DBTableModel getDetaildbmodel(int row) {
		String tmppkid = mastermodel.getDBtableModel().getTmppkid(row);
		return detaildbmodelmap.get(tmppkid);
	}

	/**
	 * ��������֧�ֵĴ�ӡ����
	 * 
	 * @return
	 */
	public Vector<String> getPrintplans() {
		return getMasterModel().getPrintplans();
	}

	/**
	 * ��������֧�ֵĴ�ӡ����
	 * 
	 * @param plans
	 */
	public void setPrintplans(Vector<String> plans) {
		getMasterModel().setPrintplans(plans);
	}

	/**
	 * ���ݴ�ӡ
	 * 
	 * @param planname
	 *            ������
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
	 * ���ܵ���ID��ѯϸ����where����.
	 * 
	 * @param mastervalue
	 *            �ܵ�ֵ
	 * @return
	 */
	protected String getRetrievedetailWheres(String mastervalue) {
		String coltype = detailmodel.getDBtableModel().getColumnDBType(
				detailrelatecolname);
		if (coltype == null) {
			logger.error("ϸ���Ҳ�������" + detailrelatecolname);
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
	 * �嵥����ɹ���
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
				// ɾ���ܵ���¼
			}
			mdbmodel.removeRow(mr);
			// �к�Ҫ��һ
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
