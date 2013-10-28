package com.smart.platform.gui.mde;

import com.smart.extension.mde.CMdeModelAp;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.CSteModelListener;
import com.smart.platform.gui.ste.QuerylinkInfo;
import com.smart.platform.rule.enginee.Ruleenginee;
import com.smart.platform.util.DefaultNPParam;

import javax.swing.*;

import org.apache.log4j.Category;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-17 Time: 13:25:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class CDetailModel extends CSteModel {
	Category logger = Category.getInstance(CDetailModel.class);
	protected CMdeModel mdemodel = null;

	protected CDetailModel() {
		modelnameinzxzip = "ste1.model";
		ruleprefix = "ϸ��";
	}

	protected CDetailModel(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		this.mdemodel = mdemodel;
		modelnameinzxzip = "ste1.model";
		this.frame = frame;
		this.title = title;
		if (frame != null) {
			setOpid(((MdeFrame) frame).getOpid());
		}
		// ����ר��
		initInitdelegate();
		this.loadDBColumnInfos();
		try {
			Ruleenginee ruleeng = mdemodel.getRuleeng();
			if (ruleeng != null) {
				ruleeng.processCalcColumn(this, "ϸ��������", -1);
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		/*
		 * this.loadRuleenginee();
		 * 
		 * if (ruleeng != null) { ruleeng.process(this, "��������ѡ��");
		 * ruleeng.process(this, "����ϵͳ����ѡ��"); ruleeng.process(this,
		 * "����SQL����ѡ��"); ruleeng.process(this, "�����Ա༭"); }
		 */
		if (initdelegate != null) {
			initdelegate.on_init(this);
		}

		DBColumnDisplayInfo colinfo = getDBColumnDisplayInfo("filegroupid");
		useattachfile = colinfo != null;

	}

	@Override
	protected JPopupMenu createPopmenu() {
		// return SteControlFactory.createPopupmenu(this);
		JPopupMenu popmenu = new JPopupMenu("�����˵�");
		JMenuItem item;
		ActionListener actionListener = this;

		int row = getRow();
		if (ruleeng != null) {
			Vector<QuerylinkInfo> qlinfos = ruleeng.processQuerylink(this,
					"ϸ��������ѯ");
			if (qlinfos != null && row >= 0) {
				Enumeration<QuerylinkInfo> en = qlinfos.elements();
				while (en.hasMoreElements()) {
					QuerylinkInfo qlinfo = en.nextElement();
					// �˵�
					item = new JMenuItem(qlinfo.querylinkname);
					item.setActionCommand("run");
					item.addActionListener(new QuerylinkMenuListener(row,
							qlinfo));
					popmenu.add(item);
				}

				popmenu.addSeparator();
			}
		}
		
		item = new JMenuItem("�ı�����  Ctrl+F");
		item.setActionCommand(CSteModel.ACTION_SEARCH);
		item.addActionListener(new SearchHandler(ACTION_SEARCH));
		popmenu.add(item);
		
		item = new JMenuItem("������һ�� Ctrl+K");
		item.setActionCommand(CSteModel.ACTION_SEARCHNEXT);
		item.addActionListener(new SearchHandler(ACTION_SEARCH));
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("��һ�� Ctrl+HOME");
		item.setActionCommand(CMdeModel.ACTION_FIRST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ǰһ��");
		item.setActionCommand(CMdeModel.ACTION_PRIOR);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("��һ��");
		item.setActionCommand(CMdeModel.ACTION_NEXT);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("ĩһ�� Ctrl+END");
		item.setActionCommand(CMdeModel.ACTION_LAST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		popmenu.addSeparator();


		item = new JMenuItem("����");
		item.setActionCommand(CMdeModel.ACTION_EXPORT);
		item.addActionListener(mdemodel);
		popmenu.add(item);

		item = new JMenuItem("����Ϊ");
		item.setActionCommand(CMdeModel.ACTION_EXPORTAS);
		item.addActionListener(mdemodel);
		popmenu.add(item);

		item = new JMenuItem("�ܵ�����Ϊ");
		item.setActionCommand(CMdeModel.ACTION_EXPORTMASTERAS);
		item.addActionListener(mdemodel);
		popmenu.add(item);

		item = new JMenuItem("ϸ������Ϊ");
		item.setActionCommand(CMdeModel.ACTION_EXPORTDETAILAS);
		item.addActionListener(mdemodel);
		popmenu.add(item);
		
		popmenu.addSeparator();

		if (DefaultNPParam.develop == 1) {
			item = new JMenuItem("����ܵ�");
			item.setActionCommand(CMdeModel.ACTION_SETUPUI);
			item.addActionListener(actionListener);
			popmenu.add(item);

			item = new JMenuItem("���ϸ��");
			item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
			item.addActionListener(actionListener);
			popmenu.add(item);
		}
		return popmenu;

	}

	protected CStetoolbar createToolbar() {
		// return MdeControlFactory.createMdedtltoolbar(mdemodel);
		return null;// 20070802 ȥ��ϸ�� toolbar
	}

	@Override
	protected void initControl() {
		super.initControl();
		if (ruleeng != null) {
			ruleeng.process(this, "ϸ�����ñ༭�ؼ���С");
			ruleeng.process(mdemodel, "ϸ����������");
			ruleeng.process(mdemodel, "ϸ�������޸�");
			ruleeng.process(mdemodel, "ϸ������ɾ��");
			ruleeng.process(mdemodel, "ϸ�����γ���");
		}
	}

	@Override
	protected void on_itemvaluechange(int row, String colname, String value) {
		if (ruleeng != null) {
			ruleeng.process(this, "ϸ���Զ�����", row, colname);
			try {
				ruleeng.processCalcColumn(this, "ϸ��������", row);
			} catch (Exception e1) {
				logger.error("error", e1);
			}
			ruleeng.process(mdemodel, "ϸ���м�������ֵ���ܵ���", row, colname);

			form.setbindingvalue(true);
			try {
				if (ruleeng != null) {
					ruleeng.processItemvaluechanged(this, "ϸ�����ö�̬����ѡ��", row,
							colname, value);
					resetDdldbmodel(row);
				}
			} catch (Exception e1) {
				logger.error("error", e1);
			} finally {
				form.setbindingvalue(false);
			}

		}

		sumdbmodel.fireDatachanged();
		// ˢ�ºϼ���
		tableChanged(table.getRowCount() - 1);

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_itemvaluechange(row, colname, value);
		}

		if (editdelegate != null) {
			editdelegate.on_itemvaluechange(this, row, colname, value);
		}
		if (zxdelegate != null) {
			zxdelegate.on_itemvaluechange(this, row, colname, value);
		}

	}

	@Override
	public String getTablename() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void on_del(int row) {
		if (ruleeng != null) {
			ruleeng.process(mdemodel, "ϸ���м�������ֵ���ܵ���", row, "");
		}
		super.on_del(row);
		if (mdemodel.isSaveimmdiate()) {
			mdemodel.doSaveSlient();
		}
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recreateDBModel() {
		// TODO Auto-generated method stub
		super.recreateDBModel();
		if (ruleeng != null) {
			ruleeng.process(this, "ϸ������ѡ");
		}
		//���ܵ���doHideform,Ҫֱ�Ӳ���ʾform������.
		//doHideform();
		formvisible = false;
		tablescrollpane.setViewportView(table);

	}

	public void onstartRun() {
		// ���ܵǼ���mdemodel���
		// do nothing
	}

	public void onstopRun() {
		// ���ܵǼ���mdemodel���
		// do nothing
	}

	/**
	 * ϸ��ֻ�Զ�����һ��. tableresized��¼�Ƿ��ѵ�����
	 */
	protected boolean tableresized = false;

	/**
	 * ֻҪ�Զ�resize()һ�� add by wwh 20071011
	 */
	@Override
	protected void resizeTable() {
		if (tableresized) {
			return;
		}
		super.resizeTable();
		tableresized = true;
	}

	@Override
	public void doUndo() {
		if (ruleeng != null) {
			int ret = ruleeng.process(this, "ϸ�����γ���", 0);
			if (ret < 0)
				return;
		}
		super.doUndo();
	}

	@Override
	protected int on_beforedel(int row) {
		if (ruleeng != null) {
			if (ruleeng.process(this, "ϸ������ɾ��", 0) < 0)
				return -1;
			if (ruleeng.process(this, "ϸ����������ֹɾ��", row) < 0)
				return -1;
			int ret = ruleeng.process(mdemodel.getMasterModel(),
					"ϸ�������ܵ�������ֹɾ��", mdemodel.getMasterModel().getRow());
			if (ret < 0)
				return -1;

		}
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCandeletedtl()) {
				setStatusmessage("��û��ɾ��ϸ������Ȩ");
				return -1;
			}
		}
		/*
		 * if (isApModifyselfonly()) { // TODO:���ֻ�ܸ����ѵ�,��Ҫ���� }
		 */return super.on_beforedel(row);
	}

	@Override
	protected int on_beforemodify(int row) {
		if (ruleeng != null) {
			if (ruleeng.process(this, "ϸ�������޸�", 0) < 0)
				return -1;
			if (ruleeng.process(this, "ϸ����������ֹɾ��", row) < 0)
				return -1;
			int ret = ruleeng.process(mdemodel.getMasterModel(),
					"ϸ�������ܵ�������ֹ�޸�", mdemodel.getMasterModel().getRow());
			if (ret < 0)
				return -1;
		}
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCanmodifydtl()) {
				setStatusmessage("��û���޸�ϸ������Ȩ");
				return -1;
			}
		}

		return super.on_beforemodify(row);
	}

	@Override
	protected int on_new(int row) {
		// TODO Auto-generated method stub
		if (super.on_new(row) < 0)
			return -1;
		if (ruleeng != null) {
			ruleeng.process(this, "ϸ�����ó�ֵ", row);
		}
		return 0;
	}

	@Override
	protected int on_beforeNew() {
		if (ruleeng != null) {
			int ret = ruleeng.process(this, "ϸ����������", 0);
			if (ret < 0)
				return -1;

			ret = ruleeng.process(mdemodel.getMasterModel(), "ϸ�������ܵ�������ֹ����",
					mdemodel.getMasterModel().getRow());
			if (ret < 0)
				return -1;

		}

		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCannewdtl()) {
				setStatusmessage("��û������ϸ������Ȩ");
				return -1;
			}
		}
		if (0 != super.on_beforeNew()) {
			return -1;
		}

		// ��������?
		if (mdemodel.isSaveimmdiate() && getRowCount() > 0) {
			if (0 != mdemodel.doSaveSlient())
				return -1;
		}
		return 0;
	}

	public void setRuleeng(Ruleenginee ruleeng) {
		this.ruleeng = ruleeng;
		if (ruleeng == null)
			return;
		ruleeng.process(this, "ϸ����������ѡ��");
		ruleeng.process(this, "ϸ������ϵͳ����ѡ��");
		ruleeng.process(this, "ϸ������SQL����ѡ��");
		ruleeng.process(this, "ϸ�������Ա༭");
	}

	@Override
	protected void sort() {
		String rulesort = null;
		if (ruleeng != null) {
			rulesort = ruleeng.processSort(this, "ϸ����������");
		}

		if (sortcolumns == null) {
			if (rulesort != null && rulesort.length() > 0) {
				try {
					getDBtableModel().sort(rulesort);
				} catch (Exception e) {
					logger.error("ERROR", e);
				}
			} else {
				getDBtableModel().resort();
			}
		} else {
			getDBtableModel().sort(sortcolumns, sortasc);
		}
	}

	@Override
	protected Color getCellColor(int row, int col) {
		if (ruleeng != null) {
			Color c = ruleeng.processColor(this, "ϸ�����������ɫ", row);
			if (c != null)
				return c;
		}
		return super.getCellColor(row, col);
	}

	@Override
	protected void on_retrieved() {
		if (toolbar != null) {
			toolbar.setQuerybuttonText("��ѯ");
		}
		sort();

		if (ruleeng != null) {
			this.setWaitCursor();
			try {
				ruleeng.processCalcColumn(this, "ϸ��������", -1);
			} catch (Exception e1) {
				logger.error("error", e1);
			}

			DBTableModel crossdbmodel;
			try {
				crossdbmodel = ruleeng.processCrosstable(dbmodel, getTableColumns(),"�����");
				if (crossdbmodel != null) {
					processCross(crossdbmodel);
				}
			} catch (Exception e) {
				logger.error("error", e);
				errorMessage("����", "���ɽ����ʧ��" + e.getMessage());
			} finally {
				this.setDefaultCursor();
			}
		} else {
			getSumdbmodel().fireDatachanged();
		}

		resizeTable();

		// logger.debug("on_retrieved,rowcount="+dbmodel.getRowCount());
		// logger.debug("on_retrieved,sumdbmodel
		// rowcount="+sumdbmodel.getRowCount()+",sumdbmodel="+sumdbmodel);

		if (currow >= 0 && currow < getDBtableModel().getRowCount()) {
			setRow(currow);
		} else if (getDBtableModel().getRowCount() > 0) {
			setRow(0);
		} else {
			setRow(-1);
		}

		if (editdelegate != null) {
			editdelegate.on_retrieved(this);
		}
		if (zxdelegate != null) {
			zxdelegate.on_retrieved(this);
		}

		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			listener.on_retrieved();
		}

		if (mdemodel.getMasterModel().getRow() >= 0) {
			mdemodel.getMasterModel().bindDataSetEnable(
					mdemodel.getMasterModel().getRow());
		}
	}

	@Override
	public boolean doHideform() {
		boolean ret = super.doHideform();
		if (mdemodel.isSaveimmdiate()) {
			if (0 != mdemodel.doSaveSlient()) {
				return false;
			}
		}
		return ret;
	}

	/**
	 * ϸ����ѯ��Ҫ����
	 */
	@Override
	public void doQuery(String wheres) {
		String otherwheres = getOtherWheres();
		if (otherwheres.length() > 0) {
			if (wheres.length() > 0) {
				wheres = wheres + " and ";
			}
			wheres += otherwheres;
		}
		/*
		 * if (ruleeng != null) { String s = ruleeng.processWhere(this,
		 * "���Ӳ�ѯ����"); if (s != null && s.length() > 0) { if (wheres.length() >
		 * 0) { wheres = wheres + " and "; } wheres += s; } } // �Ƿ��в�ѯǰstore
		 * proc? if (ruleeng != null) { String procname =
		 * ruleeng.processPrequerystoreproc(this, "��ѯǰ����洢����"); if (procname !=
		 * null && procname.length() > 0) { ClientRequest req = new
		 * ClientRequest( "npclient:execprequerystoreproc"); ParamCommand pcmd =
		 * new ParamCommand(); req.addCommand(pcmd); pcmd.addParam("procname",
		 * procname); pcmd.addParam("wheres", wheres);
		 * 
		 * SendHelper sh = new SendHelper(); try { ServerResponse resp =
		 * sh.sendRequest(req); if (!resp.getCommand().startsWith("+OK")) {
		 * logger.error("error:" + resp.getCommand()); errorMessage("����",
		 * resp.getCommand()); return; } ParamCommand respcmd = (ParamCommand)
		 * resp.commandAt(1); otherwheres = respcmd.getValue("otherwheres");
		 * 
		 * if (otherwheres.length() > 0) { if (wheres.length() > 0) { wheres =
		 * wheres + " and "; } wheres += otherwheres; } } catch (Exception e) {
		 * logger.error("error", e); errorMessage("����", e.getMessage()); return; } } }
		 * 
		 */doRetrieve(wheres);
	}

	public void doQuery(String wheres,DBTableModel dm) {
		String otherwheres = getOtherWheres();
		if (otherwheres.length() > 0) {
			if (wheres.length() > 0) {
				wheres = wheres + " and ";
			}
			wheres += otherwheres;
		}
		/*
		 * if (ruleeng != null) { String s = ruleeng.processWhere(this,
		 * "���Ӳ�ѯ����"); if (s != null && s.length() > 0) { if (wheres.length() >
		 * 0) { wheres = wheres + " and "; } wheres += s; } } // �Ƿ��в�ѯǰstore
		 * proc? if (ruleeng != null) { String procname =
		 * ruleeng.processPrequerystoreproc(this, "��ѯǰ����洢����"); if (procname !=
		 * null && procname.length() > 0) { ClientRequest req = new
		 * ClientRequest( "npclient:execprequerystoreproc"); ParamCommand pcmd =
		 * new ParamCommand(); req.addCommand(pcmd); pcmd.addParam("procname",
		 * procname); pcmd.addParam("wheres", wheres);
		 * 
		 * SendHelper sh = new SendHelper(); try { ServerResponse resp =
		 * sh.sendRequest(req); if (!resp.getCommand().startsWith("+OK")) {
		 * logger.error("error:" + resp.getCommand()); errorMessage("����",
		 * resp.getCommand()); return; } ParamCommand respcmd = (ParamCommand)
		 * resp.commandAt(1); otherwheres = respcmd.getValue("otherwheres");
		 * 
		 * if (otherwheres.length() > 0) { if (wheres.length() > 0) { wheres =
		 * wheres + " and "; } wheres += otherwheres; } } catch (Exception e) {
		 * logger.error("error", e); errorMessage("����", e.getMessage()); return; } } }
		 * 
		 */doRetrieve(wheres,dm);

	}
	
	protected void doRetrieve(String wheres,DBTableModel dm) {
		String sql = buildSelectSql(wheres);
		String ob = getSqlOrderby();
		if (ob.length() > 0) {
			sql = sql + " " + ob;
		}
		logger.info("ִ�в�ѯ��" + sql);
		lastselectsql = sql;

		if (toolbar != null) {
			toolbar.setQuerybuttonText("ֹͣ");
		}
		querystarttime = System.currentTimeMillis();
		form.clearAll();
		currow = -1;

		this.setStatusmessage("��ʼ���в�ѯ..... ");
		on_retrievestart();
		dm.setUsequerythread(usequerythread);
		dm.doRetrieve(sql, DefaultNPParam.fetchmaxrow);
	}

	@Override
	protected boolean isColumneditable(int row, String colname) {
		boolean ret = super.isColumneditable(row, colname);
		if (ret == false)
			return ret;

		// ��Ȩ�Ƿ��ֹ
		if (mdemodel instanceof CMdeModelAp) {
			String apvalue = ((CMdeModelAp)mdemodel).getApvalue("dtlforbidedit_"
					+ colname.toLowerCase());
			if (apvalue != null && apvalue.equals("true")) {
				return false;
			}
		}
		return ret;
	}

	public void bindDataSetEnable(int row) {
		// ����н�ֹ,Ҫ��Ϊreadonly
		if (mdemodel instanceof CMdeModelAp) {
		Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			String apvalue = ((CMdeModelAp)mdemodel).getApvalue("dtlforbidedit_"
					+ col.getColname().toLowerCase());
			if (apvalue != null && apvalue.equals("true")) {
				col.setReadonly(true);
			}
		}
		}
		super.bindDataSetEnable(row);

	}

	@Override
	protected void processQuerylink(int row) {
		if (ruleeng == null)
			return;

		Vector<QuerylinkInfo> qlinfos = ruleeng.processQuerylink(this, "ϸ��������ѯ");
		if (qlinfos == null) {
			return;
		}

		JPopupMenu popmenu = new JPopupMenu("�����˵�");
		Enumeration<QuerylinkInfo> en = qlinfos.elements();
		while (en.hasMoreElements()) {
			QuerylinkInfo qlinfo = en.nextElement();
			// �˵�
			JMenuItem item;
			item = new JMenuItem(qlinfo.querylinkname);
			item.setActionCommand("run");
			item.addActionListener(new QuerylinkMenuListener(row, qlinfo));
			popmenu.add(item);
		}

		popmenu.show(table, (int) mouseclickpoint.getX(), (int) mouseclickpoint
				.getY());

	}

	
	@Override
	protected int on_checkrow(int row, DBTableModel model) {
		if (model.getdbStatus(row) == RecordTrunk.DBSTATUS_SAVED) {
			// ���û���޸�,�Ͳ�Ҫ��
			return 0;
		}
		
		if(ruleeng!=null){
			String msg=ruleeng.processRowcheck(this, "ϸ���б��ʽ���", row);
			if(msg!=null && msg.length()>0){
				warnMessage("������Ҫ���", msg);
				return -1;
			}
		}

		return super.on_checkrow(row, model);
	}
}
