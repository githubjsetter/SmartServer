package com.smart.platform.gui.mde;

import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.log4j.Category;

import com.smart.extension.ap.Aphelper;
import com.smart.extension.mde.CMdeModelAp;
import com.smart.extension.ste.Apinfo;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CMdetoolbar;
import com.smart.platform.gui.control.CQueryDialog;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.CSteModelListener;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.gui.ste.QuerylinkInfo;
import com.smart.platform.rule.enginee.Ruleenginee;
import com.smart.platform.util.DBHelper;
import com.smart.platform.util.DefaultNPParam;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-17 Time: 13:24:16
 * 总单单表编辑
 */
public abstract class CMasterModel extends CSteModel {
	protected CMdeModel mdemodel;

	Category logger = Category.getInstance(CMasterModel.class);

	public CMasterModel() {

	}

	public CMasterModel(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		this.mdemodel = mdemodel;
		modelnameinzxzip = "ste.model";
		this.frame = frame;
		this.title = title;
		if (frame != null) {
			setOpid(((MdeFrame) frame).getOpid());
		}
		// 加载专项
		initInitdelegate();
		this.loadDBColumnInfos();
		this.loadPrintplan();
		try {
			Ruleenginee ruleeng = mdemodel.getRuleeng();
			if (ruleeng != null)
				ruleeng.processCalcColumn(this, "计算列", -1);
		} catch (Exception e1) {
			logger.error("error", e1);
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

	/*
	 * protected CSteFormWindow createFormwindow() { CMasterFormWindow
	 * formwindow = new CMasterFormWindow(this .getParentFrame(), form,
	 * mdemodel, title); formwindow.setFocusTraversalPolicy(new
	 * CFormFocusTraversalPolicy( mdemodel.getMasterModel())); return
	 * formwindow; }
	 */
	@Override
	protected void initControl() {
		// TODO Auto-generated method stub
		super.initControl();
		/*
		 * if(ruleeng!=null){ ruleeng.process(this, "细单屏蔽新增");
		 * ruleeng.process(this, "细单屏蔽修改"); ruleeng.process(this, "细单屏蔽删除");
		 * ruleeng.process(this, "细单屏蔽撤消"); }
		 */
	}

	@Override
	protected JPopupMenu createPopmenu() {
		// return SteControlFactory.createPopupmenu(this);
		JPopupMenu popmenu = new JPopupMenu("级联菜单");
		JMenuItem item;

		int row = getRow();
		if (ruleeng != null) {
			Vector<QuerylinkInfo> qlinfos = ruleeng.processQuerylink(this,
					"级联查询");
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
		ActionListener actionListener = this;
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
		CMdetoolbar mtb = MdeControlFactory.createMdetoolbar(mdemodel);
		Enumeration<String> en = printplans.elements();
		while (en.hasMoreElements()) {
			String planname = en.nextElement();
			mtb.addPrintmenu(planname, "DOCPRINT_" + planname);
		}
		return mtb;
	}

	public void onstartRun() {
		// 功能登记由mdemodel完成
		// do nothing
	}

	public void onstopRun() {
		// 功能登记由mdemodel完成
		// do nothing
	}

	@Override
	public boolean cancelEdit() {
		if (mdemodel.getDetailModel().getRowCount() > 0) {
			return true;
		}
		return super.cancelEdit();
	}

	@Override
	protected int on_beforequery() {
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCanquery()) {
				setStatusmessage("你没有查询的授权");
				return -1;
			}
		}

		if (!table.confirm()) {
			return -1;
		}

		// 总单细目中的总单查询前检查,由mdemodel去实现
		Enumeration<CSteModelListener> en = actionListeners.elements();
		while (en.hasMoreElements()) {
			CSteModelListener listener = en.nextElement();
			if (0 != listener.on_beforequery()) {
				return -1;
			}
		}
		return 0;
	}

	@Override
	public void doDel(int row) {
		// 如果是新增，有细单，要提示
		if (row < 0)
			return;
		int dbstatus = getdbStatus(row);
		if (RecordTrunk.DBSTATUS_NEW == dbstatus) {
			String msg = "如果你删除总单，细单也要自动删除，你确定吗？";
			int ret = JOptionPane.showConfirmDialog(getParentFrame(), msg,
					"警告", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (ret != JOptionPane.OK_OPTION) {
				return;
			}

			// 删除所有细单
			mdemodel.getDetailModel().reset();
		}
		super.doDel(row);
		mdemodel.getDetailModel().tableChanged();
	}

	@Override
	protected int on_beforedel(int row) {
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCandelete()) {
				setStatusmessage("你没有删除的授权");
				return -1;
			}
		}
		/*
		 * if (isApModifyselfonly()) { // TODO:如果只能改自已的,还要处理 }
		 */return super.on_beforedel(row);
	}

	@Override
	protected int on_beforemodify(int row) {
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCanmodify()) {
				setStatusmessage("你没有修改的授权");
				return -1;
			}
		}

		return super.on_beforemodify(row);
	}

	@Override
	protected int on_beforeNew() {
		if (mdemodel instanceof CMdeModelAp) {
			if (!((CMdeModelAp) mdemodel).isApCannew()) {
				setStatusmessage("你没有新增的授权");
				return -1;
			}
		}
		return super.on_beforeNew();
	}

	@Override
	public String buildSelectSql(String wheres) {
		if (!(mdemodel instanceof CMdeModelAp)) {
			return super.buildSelectSql(wheres);
		}

		String sql = super.buildSelectSql(wheres);
		// 加上授权
		String apwhere = ((CMdeModelAp) mdemodel)
				.getApvalue(Apinfo.APNAME_WHERES);
		if (apwhere == null)
			apwhere = "";
		if (apwhere.length() > 0) {
			apwhere = "(" + apwhere + ")";
		}
		apwhere = Aphelper.filterApwheres(apwhere);
		sql = DBHelper.addWheres(sql, apwhere);
		return sql;
	}

	@Override
	public Vector<DBColumnDisplayInfo> getCanquerycolinfos() {
		// TODO Auto-generated method stub
		Vector<DBColumnDisplayInfo> cols = super.getCanquerycolinfos();
		cols.addAll(mdemodel.getDetailModel().getCanquerycolinfos());
		return cols;
	}

	@Override
	protected Querycond getQuerycond() {
		if (querycond != null) {
			return querycond;
		}
		querycond = new Querycond();
		querycond.setHovcond(form);
		querycond.setDetailHovcond(mdemodel.getDetailModel().getForm());

		if (querycolumns == null || querycolumns.size() == 0) {
			Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				if (!colinfo.isQueryable()) {
					continue;
				}
				colinfo.setQuerymust(isQuerymustcol(colinfo.getColname()));
				String coltype = colinfo.getColtype();
				if (coltype.equalsIgnoreCase("number")
						|| coltype.equalsIgnoreCase("date")
						|| coltype.equalsIgnoreCase("varchar")) {
					querycond.add(new Querycondline(querycond, colinfo, "m"));
				}
			}
			en = mdemodel.getDetailModel().getFormcolumndisplayinfos()
					.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				if (!colinfo.isQueryable()) {
					continue;
				}
				colinfo.setQuerymust(isQuerymustcol(colinfo.getColname()));
				String coltype = colinfo.getColtype();
				if (coltype.equalsIgnoreCase("number")
						|| coltype.equalsIgnoreCase("date")
						|| coltype.equalsIgnoreCase("varchar")) {
					querycond.add(new Querycondline(querycond, colinfo, "d"));
				}
			}
		} else {
			Enumeration<String> en = querycolumns.elements();
			while (en.hasMoreElements()) {
				String colname = en.nextElement();
				String subqueryopid="";
				int p=colname.indexOf(",");
				if(p>0){
					subqueryopid=colname.substring(p+1);
					colname=colname.substring(0,p);
				}

				
				String mdflag = "m";
				DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(colname);
				if (colinfo == null) {
					colinfo = mdemodel.getDetailModel().getDBColumnDisplayInfo(
							colname);
					if (colinfo != null) {
						mdflag = "d";
					}
				}
				if (colinfo == null)
					continue;
				colinfo.setQuerymust(isQuerymustcol(colinfo.getColname()));
				colinfo.setSubqueryopid(subqueryopid);
				/*
				 * if (!colinfo.isQueryable()) { continue; }
				 */String coltype = colinfo.getColtype();
				if (coltype.equalsIgnoreCase("number")
						|| coltype.equalsIgnoreCase("date")
						|| coltype.equalsIgnoreCase("varchar")) {
					querycond
							.add(new Querycondline(querycond, colinfo, mdflag));
				}
			}
		}
		return querycond;
	}

	
	@Override
	public String doQueryreturnWheres(){
		if (querydlg == null) {
			CFrame pframe=getParentFrame();
			String title="";
			if(pframe!=null){
				title=pframe.getTitle();
			}

			querydlg = new CQueryDialog(getParentFrame(),title+ "查询条件", this);
			getQuerycond();
			if (querydelegate != null) {
				Querycond newcond = querydelegate.on_query(querycond);
				if (newcond != null) {
					querycond = newcond;
				}
			}
			querydlg.initControl(querycond);
			querydlg.pack();
		}
		querydlg.setVisible(true);
		boolean confirm = querydlg.isConfirm();
		if (!confirm) {
			return null;
		}
		this.usecrosstable = false;
		String wheres = querycond.getWheres();
		String fulldetailwheres=getTablename()+"."+mdemodel.getMasterRelatecolname()+"="+
		mdemodel.getDetailModel().getTablename()+"."+mdemodel.getDetailRelatecolname();
		String detailwheres = querycond.getDetailWheres();

		if (detailwheres.length() > 0) {
			detailwheres = fulldetailwheres+" and ("+detailwheres+")";
			String detailsql = "select " + mdemodel.getDetailRelatecolname()
					+ " from " + mdemodel.getDetailModel().getTablename()
					+ " where " + detailwheres;
			detailwheres = mdemodel.getMasterRelatecolname() + " in ("
					+ detailsql + ")";
		}
		if (wheres.length() == 0) {
			if (detailwheres.length() > 0) {
				wheres = detailwheres;
			}
		} else {
			if (detailwheres.length() > 0) {
				wheres = wheres + " and (" + detailwheres + ")";
			}
		}
		return wheres;
	}
	
	/**
	 * export 总单的检查
	 * 
	 * @param row
	 * @return
	 */
	public int checkRow(int row) {
		return on_checkrow(row, dbmodel);
	}

	@Override
	protected boolean isColumneditable(int row, String colname) {
		boolean ret = super.isColumneditable(row, colname);
		if (ret == false)
			return ret;

		// 授权是否禁止
		if (mdemodel instanceof CMdeModelAp) {
			String apvalue = ((CMdeModelAp)mdemodel).getApvalue("forbidedit_"
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
			String apvalue = ((CMdeModelAp)mdemodel).getApvalue("forbidedit_"
					+ col.getColname().toLowerCase());
			if (apvalue != null && apvalue.equals("true")) {
				col.setReadonly(true);
			}
		}
		}
		super.bindDataSetEnable(row);

	}

	@Override
	protected boolean isForbidReprint(){
		if(mdemodel instanceof CMdeModelAp){
			CMdeModelAp mdeap=(CMdeModelAp)mdemodel;
			return mdeap.getApvalue("forbidreprint").equals("true");
		}
		return false;
	}

	/**
	 * 返回对应的mdemodel
	 * @return
	 */
	public CMdeModel getMdemodel(){
		return mdemodel;
	}
}
