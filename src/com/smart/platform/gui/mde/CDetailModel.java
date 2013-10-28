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
		ruleprefix = "细单";
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
		// 加载专项
		initInitdelegate();
		this.loadDBColumnInfos();
		try {
			Ruleenginee ruleeng = mdemodel.getRuleeng();
			if (ruleeng != null) {
				ruleeng.processCalcColumn(this, "细单计算列", -1);
			}
		} catch (Exception e) {
			logger.error("error", e);
		}
		/*
		 * this.loadRuleenginee();
		 * 
		 * if (ruleeng != null) { ruleeng.process(this, "设置下拉选择");
		 * ruleeng.process(this, "设置系统下拉选择"); ruleeng.process(this,
		 * "设置SQL下拉选择"); ruleeng.process(this, "表格可以编辑"); }
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
		JPopupMenu popmenu = new JPopupMenu("级联菜单");
		JMenuItem item;
		ActionListener actionListener = this;

		int row = getRow();
		if (ruleeng != null) {
			Vector<QuerylinkInfo> qlinfos = ruleeng.processQuerylink(this,
					"细单级联查询");
			if (qlinfos != null && row >= 0) {
				Enumeration<QuerylinkInfo> en = qlinfos.elements();
				while (en.hasMoreElements()) {
					QuerylinkInfo qlinfo = en.nextElement();
					// 菜单
					item = new JMenuItem(qlinfo.querylinkname);
					item.setActionCommand("run");
					item.addActionListener(new QuerylinkMenuListener(row,
							qlinfo));
					popmenu.add(item);
				}

				popmenu.addSeparator();
			}
		}
		
		item = new JMenuItem("文本搜索  Ctrl+F");
		item.setActionCommand(CSteModel.ACTION_SEARCH);
		item.addActionListener(new SearchHandler(ACTION_SEARCH));
		popmenu.add(item);
		
		item = new JMenuItem("搜索下一条 Ctrl+K");
		item.setActionCommand(CSteModel.ACTION_SEARCHNEXT);
		item.addActionListener(new SearchHandler(ACTION_SEARCH));
		popmenu.add(item);

		popmenu.addSeparator();

		item = new JMenuItem("第一条 Ctrl+HOME");
		item.setActionCommand(CMdeModel.ACTION_FIRST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("前一条");
		item.setActionCommand(CMdeModel.ACTION_PRIOR);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("后一条");
		item.setActionCommand(CMdeModel.ACTION_NEXT);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);
		item = new JMenuItem("末一条 Ctrl+END");
		item.setActionCommand(CMdeModel.ACTION_LAST);
		item.addActionListener(actionListener);
		;
		popmenu.add(item);

		popmenu.addSeparator();


		item = new JMenuItem("导出");
		item.setActionCommand(CMdeModel.ACTION_EXPORT);
		item.addActionListener(mdemodel);
		popmenu.add(item);

		item = new JMenuItem("导出为");
		item.setActionCommand(CMdeModel.ACTION_EXPORTAS);
		item.addActionListener(mdemodel);
		popmenu.add(item);

		item = new JMenuItem("总单导出为");
		item.setActionCommand(CMdeModel.ACTION_EXPORTMASTERAS);
		item.addActionListener(mdemodel);
		popmenu.add(item);

		item = new JMenuItem("细单导出为");
		item.setActionCommand(CMdeModel.ACTION_EXPORTDETAILAS);
		item.addActionListener(mdemodel);
		popmenu.add(item);
		
		popmenu.addSeparator();

		if (DefaultNPParam.develop == 1) {
			item = new JMenuItem("设计总单");
			item.setActionCommand(CMdeModel.ACTION_SETUPUI);
			item.addActionListener(actionListener);
			popmenu.add(item);

			item = new JMenuItem("设计细单");
			item.setActionCommand(CMdeModel.ACTION_SETUPUIDTL);
			item.addActionListener(actionListener);
			popmenu.add(item);
		}
		return popmenu;

	}

	protected CStetoolbar createToolbar() {
		// return MdeControlFactory.createMdedtltoolbar(mdemodel);
		return null;// 20070802 去掉细单 toolbar
	}

	@Override
	protected void initControl() {
		super.initControl();
		if (ruleeng != null) {
			ruleeng.process(this, "细单设置编辑控件大小");
			ruleeng.process(mdemodel, "细单屏蔽新增");
			ruleeng.process(mdemodel, "细单屏蔽修改");
			ruleeng.process(mdemodel, "细单屏蔽删除");
			ruleeng.process(mdemodel, "细单屏蔽撤消");
		}
	}

	@Override
	protected void on_itemvaluechange(int row, String colname, String value) {
		if (ruleeng != null) {
			ruleeng.process(this, "细单自动计算", row, colname);
			try {
				ruleeng.processCalcColumn(this, "细单计算列", row);
			} catch (Exception e1) {
				logger.error("error", e1);
			}
			ruleeng.process(mdemodel, "细单列计算结果赋值给总单列", row, colname);

			form.setbindingvalue(true);
			try {
				if (ruleeng != null) {
					ruleeng.processItemvaluechanged(this, "细单设置动态下拉选择", row,
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
		// 刷新合计行
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
			ruleeng.process(mdemodel, "细单列计算结果赋值给总单列", row, "");
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
			ruleeng.process(this, "细单表格多选");
		}
		//不能调用doHideform,要直接不显示form就行了.
		//doHideform();
		formvisible = false;
		tablescrollpane.setViewportView(table);

	}

	public void onstartRun() {
		// 功能登记由mdemodel完成
		// do nothing
	}

	public void onstopRun() {
		// 功能登记由mdemodel完成
		// do nothing
	}

	/**
	 * 细单只自动调整一次. tableresized记录是否已调整过
	 */
	protected boolean tableresized = false;

	/**
	 * 只要自动resize()一次 add by wwh 20071011
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
			int ret = ruleeng.process(this, "细单屏蔽撤消", 0);
			if (ret < 0)
				return;
		}
		super.doUndo();
	}

	@Override
	protected int on_beforedel(int row) {
		if (ruleeng != null) {
			if (ruleeng.process(this, "细单屏蔽删除", 0) < 0)
				return -1;
			if (ruleeng.process(this, "细单有条件禁止删改", row) < 0)
				return -1;
			int ret = ruleeng.process(mdemodel.getMasterModel(),
					"细单根据总单条件禁止删除", mdemodel.getMasterModel().getRow());
			if (ret < 0)
				return -1;

		}
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCandeletedtl()) {
				setStatusmessage("你没有删除细单的授权");
				return -1;
			}
		}
		/*
		 * if (isApModifyselfonly()) { // TODO:如果只能改自已的,还要处理 }
		 */return super.on_beforedel(row);
	}

	@Override
	protected int on_beforemodify(int row) {
		if (ruleeng != null) {
			if (ruleeng.process(this, "细单屏蔽修改", 0) < 0)
				return -1;
			if (ruleeng.process(this, "细单有条件禁止删改", row) < 0)
				return -1;
			int ret = ruleeng.process(mdemodel.getMasterModel(),
					"细单根据总单条件禁止修改", mdemodel.getMasterModel().getRow());
			if (ret < 0)
				return -1;
		}
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCanmodifydtl()) {
				setStatusmessage("你没有修改细单的授权");
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
			ruleeng.process(this, "细单设置初值", row);
		}
		return 0;
	}

	@Override
	protected int on_beforeNew() {
		if (ruleeng != null) {
			int ret = ruleeng.process(this, "细单屏蔽新增", 0);
			if (ret < 0)
				return -1;

			ret = ruleeng.process(mdemodel.getMasterModel(), "细单根据总单条件禁止新增",
					mdemodel.getMasterModel().getRow());
			if (ret < 0)
				return -1;

		}

		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCannewdtl()) {
				setStatusmessage("你没有新增细单的授权");
				return -1;
			}
		}
		if (0 != super.on_beforeNew()) {
			return -1;
		}

		// 立即保存?
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
		ruleeng.process(this, "细单设置下拉选择");
		ruleeng.process(this, "细单设置系统下拉选择");
		ruleeng.process(this, "细单设置SQL下拉选择");
		ruleeng.process(this, "细单表格可以编辑");
	}

	@Override
	protected void sort() {
		String rulesort = null;
		if (ruleeng != null) {
			rulesort = ruleeng.processSort(this, "细单设置排序");
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
			Color c = ruleeng.processColor(this, "细单表格数据颜色", row);
			if (c != null)
				return c;
		}
		return super.getCellColor(row, col);
	}

	@Override
	protected void on_retrieved() {
		if (toolbar != null) {
			toolbar.setQuerybuttonText("查询");
		}
		sort();

		if (ruleeng != null) {
			this.setWaitCursor();
			try {
				ruleeng.processCalcColumn(this, "细单计算列", -1);
			} catch (Exception e1) {
				logger.error("error", e1);
			}

			DBTableModel crossdbmodel;
			try {
				crossdbmodel = ruleeng.processCrosstable(dbmodel, getTableColumns(),"交叉表");
				if (crossdbmodel != null) {
					processCross(crossdbmodel);
				}
			} catch (Exception e) {
				logger.error("error", e);
				errorMessage("错误", "生成交叉表失败" + e.getMessage());
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
	 * 细单查询不要规则
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
		 * "附加查询条件"); if (s != null && s.length() > 0) { if (wheres.length() >
		 * 0) { wheres = wheres + " and "; } wheres += s; } } // 是否有查询前store
		 * proc? if (ruleeng != null) { String procname =
		 * ruleeng.processPrequerystoreproc(this, "查询前处理存储过程"); if (procname !=
		 * null && procname.length() > 0) { ClientRequest req = new
		 * ClientRequest( "npclient:execprequerystoreproc"); ParamCommand pcmd =
		 * new ParamCommand(); req.addCommand(pcmd); pcmd.addParam("procname",
		 * procname); pcmd.addParam("wheres", wheres);
		 * 
		 * SendHelper sh = new SendHelper(); try { ServerResponse resp =
		 * sh.sendRequest(req); if (!resp.getCommand().startsWith("+OK")) {
		 * logger.error("error:" + resp.getCommand()); errorMessage("错误",
		 * resp.getCommand()); return; } ParamCommand respcmd = (ParamCommand)
		 * resp.commandAt(1); otherwheres = respcmd.getValue("otherwheres");
		 * 
		 * if (otherwheres.length() > 0) { if (wheres.length() > 0) { wheres =
		 * wheres + " and "; } wheres += otherwheres; } } catch (Exception e) {
		 * logger.error("error", e); errorMessage("错误", e.getMessage()); return; } } }
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
		 * "附加查询条件"); if (s != null && s.length() > 0) { if (wheres.length() >
		 * 0) { wheres = wheres + " and "; } wheres += s; } } // 是否有查询前store
		 * proc? if (ruleeng != null) { String procname =
		 * ruleeng.processPrequerystoreproc(this, "查询前处理存储过程"); if (procname !=
		 * null && procname.length() > 0) { ClientRequest req = new
		 * ClientRequest( "npclient:execprequerystoreproc"); ParamCommand pcmd =
		 * new ParamCommand(); req.addCommand(pcmd); pcmd.addParam("procname",
		 * procname); pcmd.addParam("wheres", wheres);
		 * 
		 * SendHelper sh = new SendHelper(); try { ServerResponse resp =
		 * sh.sendRequest(req); if (!resp.getCommand().startsWith("+OK")) {
		 * logger.error("error:" + resp.getCommand()); errorMessage("错误",
		 * resp.getCommand()); return; } ParamCommand respcmd = (ParamCommand)
		 * resp.commandAt(1); otherwheres = respcmd.getValue("otherwheres");
		 * 
		 * if (otherwheres.length() > 0) { if (wheres.length() > 0) { wheres =
		 * wheres + " and "; } wheres += otherwheres; } } catch (Exception e) {
		 * logger.error("error", e); errorMessage("错误", e.getMessage()); return; } } }
		 * 
		 */doRetrieve(wheres,dm);

	}
	
	protected void doRetrieve(String wheres,DBTableModel dm) {
		String sql = buildSelectSql(wheres);
		String ob = getSqlOrderby();
		if (ob.length() > 0) {
			sql = sql + " " + ob;
		}
		logger.info("执行查询：" + sql);
		lastselectsql = sql;

		if (toolbar != null) {
			toolbar.setQuerybuttonText("停止");
		}
		querystarttime = System.currentTimeMillis();
		form.clearAll();
		currow = -1;

		this.setStatusmessage("开始进行查询..... ");
		on_retrievestart();
		dm.setUsequerythread(usequerythread);
		dm.doRetrieve(sql, DefaultNPParam.fetchmaxrow);
	}

	@Override
	protected boolean isColumneditable(int row, String colname) {
		boolean ret = super.isColumneditable(row, colname);
		if (ret == false)
			return ret;

		// 授权是否禁止
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
		// 如果有禁止,要设为readonly
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

		Vector<QuerylinkInfo> qlinfos = ruleeng.processQuerylink(this, "细单级联查询");
		if (qlinfos == null) {
			return;
		}

		JPopupMenu popmenu = new JPopupMenu("级联菜单");
		Enumeration<QuerylinkInfo> en = qlinfos.elements();
		while (en.hasMoreElements()) {
			QuerylinkInfo qlinfo = en.nextElement();
			// 菜单
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
			// 如果没有修改,就不要查
			return 0;
		}
		
		if(ruleeng!=null){
			String msg=ruleeng.processRowcheck(this, "细单行表达式检查", row);
			if(msg!=null && msg.length()>0){
				warnMessage("数据需要检查", msg);
				return -1;
			}
		}

		return super.on_checkrow(row, model);
	}
}
