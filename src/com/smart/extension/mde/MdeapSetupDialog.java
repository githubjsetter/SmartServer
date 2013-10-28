package com.smart.extension.mde;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.smart.extension.ste.Apinfo;
import com.smart.extension.ste.EditcolApDlg;
import com.smart.extension.ste.HidecolApDlg;
import com.smart.extension.ste.ParamapSetupPanel;
import com.smart.extension.ste.PrintplanlistDlg;
import com.smart.platform.gui.control.CCheckBox;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DefaultNPParam;

public class MdeapSetupDialog extends CDialog {

	ApdtlIF apif = null;
	private JTextArea textquerycond;
	String roleid;
	private DBTableModel dmhide;
	private DBTableModel dmeditable;
	private DBTableModel dtldmhide;
	private DBTableModel dtldmeditable;

	public MdeapSetupDialog(Frame owner, String title, ApdtlIF apif)
			throws HeadlessException {
		super(owner, title, true);
		this.apif = apif;
		initdialog();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		this.localCenter();
	}

	protected void initdialog() {
		createEditablepane();
		createHidepane();
		createDtlEditablepane();
		createDtlHidepane();

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(createProppane());
		


		JPanel bottompane = createBottomPanel();
		cp.add(bottompane, BorderLayout.SOUTH);

	}

	JPanel createProppane() {
		JPanel jpprop = new JPanel();
		jpprop.setLayout(new BorderLayout());

		JPanel upppane = createForbidpanel();
		JPanel midpane = createDataconstraintPanel();

		jpprop.add(upppane, BorderLayout.NORTH);
		jpprop.add(midpane, BorderLayout.CENTER);
		return jpprop;
	}

	protected JPanel createBottomPanel() {
		JPanel jp = new JPanel();
		JButton btn;
		
		btn = new JButton("禁止编辑");
		btn.setActionCommand("forbidedit");
		btn.setName("btnforbidedit");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("隐藏列");
		btn.setActionCommand("hidecol");
		btn.setName("btnhidecol");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("细单禁止编辑");
		btn.setActionCommand("dtlforbidedit");
		btn.setName("btndtlforbidedit");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("细单隐藏列");
		btn.setActionCommand("dtlhidecol");
		btn.setName("btndtlhidecol");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("确定");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		btn.setName("btnok");
		jp.add(btn);

		btn = new JButton("取消");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		jp.add(btn);
		btn.setName("btncancel");
		
		if(DefaultNPParam.debug==1){
			jp.add(createUIDesignbutton());
		}
		
		return jp;
	}

	/**
	 * 显示能否增删查改存
	 * 
	 * @return
	 */
	protected JPanel createForbidpanel() {
		JPanel jp = new JPanel();
		BoxLayout layout = new BoxLayout(jp, BoxLayout.X_AXIS);
		jp.setLayout(layout);

		cbcannew = new CCheckBox("新增");
		cbcannew.setName("cbcannew");
		jp.add(cbcannew);
		if (!apif.isDevelopCannew()) {
			cbcannew.setSelected(false);
			cbcannew.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbidnew = apif.getApvalue(Apinfo.APNAME_FORBIDNEW);
			cbcannew.setSelected(!forbidnew.equals("true"));
		}

		cbcandelete = new CCheckBox("删除");
		cbcandelete.setName("cbcandelete");
		jp.add(cbcandelete);
		if (!apif.isDevelopCandelete()) {
			cbcandelete.setSelected(false);
			cbcandelete.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbiddelete = apif.getApvalue(Apinfo.APNAME_FORBIDDELETE);
			cbcandelete.setSelected(!forbiddelete.equals("true"));
		}

		cbcanquery = new CCheckBox("查询");
		cbcanquery.setName("cbcanquery");
		jp.add(cbcanquery);
		if (!apif.isDevelopCanquery()) {
			cbcanquery.setSelected(false);
			cbcanquery.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbidquery = apif.getApvalue(Apinfo.APNAME_FORBIDQUERY);
			cbcanquery.setSelected(!forbidquery.equals("true"));
		}

		cbcanmodify = new CCheckBox("修改");
		cbcanmodify.setName("cbcanmodify");
		jp.add(cbcanmodify);
		if (!apif.isDevelopCanquery()) {
			cbcanmodify.setSelected(false);
			cbcanmodify.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbidmodify = apif.getApvalue(Apinfo.APNAME_FORBIDMODIFY);
			cbcanmodify.setSelected(!forbidmodify.equals("true"));
		}

		cbcansave = new CCheckBox("保存");
		cbcansave.setName("cbcansave");
		jp.add(cbcansave);
		if (!apif.isDevelopCanquery()) {
			cbcansave.setSelected(false);
			cbcansave.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbidsave = apif.getApvalue(Apinfo.APNAME_FORBIDSAVE);
			cbcansave.setSelected(!forbidsave.equals("true"));
		}
		cbcanexport = new CCheckBox("导出");
		cbcanexport.setName("cbcanexport");
		jp.add(cbcanexport);
		// 看授权属性是怎么定义的
		String forbidexport = apif.getApvalue(Apinfo.APNAME_FORBIDEXPORT);
		cbcanexport.setSelected(!forbidexport.equals("true"));

		/*
		 * cbselfonly = new CCheckBox("只能改本人"); jp.add(cbselfonly); //
		 * 看授权属性是怎么定义的 String modifyselfonly =
		 * apif.getApvalue(Apinfo.APNAME_MODIFYSELFONLY);
		 * cbselfonly.setSelected(modifyselfonly.equals("true"));
		 */

		cbforbidreprint = new CCheckBox("禁止再打印");
		cbforbidreprint.setName("cbforbidreprint");
		jp.add(cbforbidreprint);
		// 看授权属性是怎么定义的
		String forbidreprint = apif.getApvalue(Apinfo.APNAME_FORBIDREPRINT);
		cbforbidreprint.setSelected(forbidreprint.equals("true"));
		// /////////////////
		cbcannewdtl = new CCheckBox("新增细单");
		cbcannewdtl.setName("cbcannewdtl");
		jp.add(cbcannewdtl);
		if (!apif.isDevelopCannewdtl()) {
			cbcannewdtl.setSelected(false);
			cbcannewdtl.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbidnew = apif.getApvalue(Apinfo.APNAME_FORBIDNEWDTL);
			cbcannewdtl.setSelected(!forbidnew.equals("true"));
		}

		cbcandeletedtl = new CCheckBox("删除细单");
		cbcandeletedtl.setName("cbcandeletedtl");
		jp.add(cbcandeletedtl);
		if (!apif.isDevelopCandeletedtl()) {
			cbcandeletedtl.setSelected(false);
			cbcandeletedtl.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbiddelete = apif
					.getApvalue(Apinfo.APNAME_FORBIDDELETEDTL);
			cbcandeletedtl.setSelected(!forbiddelete.equals("true"));
		}

		cbcanmodifydtl = new CCheckBox("修改细单");
		cbcanmodifydtl.setName("cbcanmodifydtl");
		jp.add(cbcanmodifydtl);
		if (!apif.isDevelopCanquery()) {
			cbcanmodifydtl.setSelected(false);
			cbcanmodifydtl.setEnabled(false);
		} else {
			// 看授权属性是怎么定义的
			String forbidmodify = apif
					.getApvalue(Apinfo.APNAME_FORBIDMODIFYDTL);
			cbcanmodifydtl.setSelected(!forbidmodify.equals("true"));
		}

		return jp;
	}

	/**
	 * 生成数据约束Panel
	 * 
	 * @return
	 */
	protected JPanel createDataconstraintPanel() {
		JPanel jp = new JPanel();
		GridBagLayout g = new GridBagLayout();
		jp.setLayout(g);

		GridBagConstraints c = new GridBagConstraints();
		JLabel lb = new JLabel("查询约束(where条件)");
		lb.setName("lbwhere");
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(lb, c);
		jp.add(lb);

		textquerycond = new JTextArea(6, 80);
		c.gridwidth = GridBagConstraints.RELATIVE;
		g.setConstraints(textquerycond, c);
		JScrollPane jspwheres=new JScrollPane(textquerycond);
		jspwheres.setName("jspwheres");
		jp.add(jspwheres);
		String wheres = apif.getApvalue(Apinfo.APNAME_WHERES);
		textquerycond.setText(wheres);

		JPanel buttonpanel = createButtonpanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(buttonpanel, c);
		jp.add(buttonpanel);

		// ///////////////// 设置打印方案
		JButton btn = null;
		btn = new JButton("设置单据打印方案");
		btn.setName("btnprintplan");
		btn.setActionCommand("设置单据打印方案");
		btn.addActionListener(this);
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(btn, c);
		jp.add(btn);

		// //////////////参数类型授权属性
		lb = new JLabel("参数类型授权属性");
		lb.setName("lbparam");
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(lb, c);
		jp.add(lb);

		Vector<Apinfo> paramapinfos = apif.getParamapinfos();
		if (paramapinfos == null) {
			paramapinfos = new Vector<Apinfo>();
		}
		Enumeration<Apinfo> en = paramapinfos.elements();
		while (en.hasMoreElements()) {
			Apinfo apinfo = en.nextElement();
			// 取值
			apinfo.setApvalue(apif.getApvalue(apinfo.getApname()));
		}
		JPanel jpparam = new JPanel();
		paramapsetuppane = new ParamapSetupPanel();
		paramapsetuppane.setup(jpparam, paramapinfos);

		c.gridwidth = GridBagConstraints.REMAINDER;
		
		JScrollPane jspparam=new JScrollPane(jpparam);
		jspparam.setName("jspparam");
		g.setConstraints(jpparam, c);
		jp.add(jspparam);

		return jp;
	}

	protected JPanel createButtonpanel() {
		JPanel jp = new JPanel();
		BoxLayout layout = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(layout);

		JButton btn = new JButton("当前部门");
		btn.setActionCommand("当前部门");
		btn.addActionListener(this);
		btn.setName("btndept");
		jp.add(btn);

		btn = new JButton("当前人员");
		btn.setActionCommand("当前人员");
		btn.addActionListener(this);
		btn.setName("btnemployee");
		jp.add(btn);

		btn = new JButton("当前角色");
		btn.setActionCommand("当前角色");
		btn.addActionListener(this);
		btn.setName("btnrole");
		jp.add(btn);

		return jp;
	}

	private boolean ok = false;
	private CCheckBox cbcannew;
	private CCheckBox cbcandelete;
	private CCheckBox cbcanquery;
	private CCheckBox cbcanmodify;
	private CCheckBox cbcansave;
	private CCheckBox cbcanexport;
	private CCheckBox cbforbidreprint;
	// private CCheckBox cbselfonly;
	private CCheckBox cbcannewdtl;
	private CCheckBox cbcandeletedtl;
	private CCheckBox cbcanmodifydtl;
	private ParamapSetupPanel paramapsetuppane;

	protected void onOk() {
		ok = true;
		dispose();
	}

	protected void onCancel() {
		ok = false;
		dispose();
	}

	public boolean getOk() {
		return ok;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("ok")) {
			onOk();
		} else if (command.equals("cancel")) {
			onCancel();
		} else if (command.equals("当前部门")) {
			textquerycond.replaceSelection("<当前部门ID>");
		} else if (command.equals("当前人员")) {
			textquerycond.replaceSelection("<当前人员ID>");
		} else if (command.equals("当前角色")) {
			textquerycond.replaceSelection("<当前角色ID>");
		} else if (command.equals("设置单据打印方案")) {
			setupPrintplan();
		} else if (command.equals("forbidedit")) {
			EditcolApDlg dlg=new EditcolApDlg(this,apif,dmeditable);
			dlg.pack();
			dlg.setVisible(true);
		} else if (command.equals("hidecol")) {
			HidecolApDlg dlg=new HidecolApDlg(this,apif,dmhide);
			dlg.pack();
			dlg.setVisible(true);
		} else if (command.equals("dtlforbidedit")) {
			EditcolApDlg dlg=new EditcolApDlg(this,apif,dtldmeditable);
			dlg.pack();
			dlg.setVisible(true);
		} else if (command.equals("dtlhidecol")) {
			HidecolApDlg dlg=new HidecolApDlg(this,apif,dtldmhide);
			dlg.pack();
			dlg.setVisible(true);
		}else{
			super.actionPerformed(e);
		}
	}

	/**
	 * 返回授权属性设置
	 * 
	 * @return
	 */

	public Vector<Apinfo> getApinfos() {
		Vector<Apinfo> infos = new Vector<Apinfo>();
		Apinfo info = null;

		if (!cbcannew.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDNEW, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcandelete.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDDELETE, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanquery.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDQUERY, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanmodify.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDMODIFY, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcansave.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDSAVE, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanexport.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDEXPORT, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
/*		if (cbselfonly.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_MODIFYSELFONLY,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
*/
		if (cbforbidreprint.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDREPRINT,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}

		// 授权条件
		info = new Apinfo(Apinfo.APNAME_WHERES, Apinfo.APTYPE_DATA);
		info.setApvalue(textquerycond.getText());
		infos.add(info);

		// 自动打印
		info = new Apinfo(Apinfo.APNAME_AUTOPRINTPLAN, Apinfo.APTYPE_PARAM);
		info.setApvalue(apif.getAutoprintplan());
		infos.add(info);

		// ///细单
		if (!cbcannewdtl.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDNEWDTL, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcandeletedtl.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDDELETEDTL,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanmodifydtl.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDMODIFYDTL,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}

		// 禁止编辑
		for (int r = 0; r < dmeditable.getRowCount() - 1; r++) {
			String forbidedit = dmeditable.getItemValue(r, "forbidedit");
			if (forbidedit.equals("1")) {
				String colname = dmeditable.getItemValue(r, "colname");
				info = new Apinfo("forbidedit_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}
		// 禁止显示
		for (int r = 0; r < dmhide.getRowCount() - 1; r++) {
			String hide = dmhide.getItemValue(r, "hide");
			if (hide.equals("1")) {
				String colname = dmhide.getItemValue(r, "colname");
				info = new Apinfo("hide_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}

		// 禁止编辑
		for (int r = 0; r < dtldmeditable.getRowCount() - 1; r++) {
			String forbidedit = dtldmeditable.getItemValue(r, "forbidedit");
			if (forbidedit.equals("1")) {
				String colname = dtldmeditable.getItemValue(r, "colname");
				info = new Apinfo("dtlforbidedit_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}
		// 禁止显示
		for (int r = 0; r < dtldmhide.getRowCount() - 1; r++) {
			String hide = dtldmhide.getItemValue(r, "hide");
			if (hide.equals("1")) {
				String colname = dtldmhide.getItemValue(r, "colname");
				info = new Apinfo("dtlhide_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}

		infos.addAll(paramapsetuppane.getApinfos());

		return infos;
	}

	/**
	 * 下载所有打印方案名称，再根据专项中的文件配置选择
	 */
	void setupPrintplan() {
		PrintplanlistDlg dlg = new PrintplanlistDlg(this, apif);
		dlg.pack();
		dlg.setVisible(true);
	}

	DBTableModel createColdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "列名");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("title", "varchar", "中文名");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("forbidedit", "varchar", "禁止编辑");
		col.setReadonly(false);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);
		col.setTablecolumnwidth(100);

		return new DBTableModel(cols);
	}

	DBTableModel createHideColdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "列名");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("title", "varchar", "中文名");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("hide", "varchar", "隐藏");
		col.setReadonly(false);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);
		col.setTablecolumnwidth(100);

		return new DBTableModel(cols);
	}

	/**
	 * 生成 是否可编辑
	 * 
	 * @return
	 */
	void createEditablepane() {
		dmeditable = createColdm();
		// 插入列
		Enumeration<DBColumnDisplayInfo> en = apif.getDBColumnDisplayInfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			if (dbcol.isReadonly())
				continue;
			if (dbcol.getColtype().equals("行号"))
				continue;
			int r = dmeditable.getRowCount();
			dmeditable.appendRow();
			dmeditable.setItemValue(r, "colname", dbcol.getColname());
			dmeditable.setItemValue(r, "title", dbcol.getTitle());

			// 是否有禁止?
			String afvalue = apif.getApvalue("forbidedit_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dmeditable.setItemValue(r, "forbidedit", "1");
			} else {
				dmeditable.setItemValue(r, "forbidedit", "0");
			}
		}
		dmeditable.appendRow();

	}

	void createDtlEditablepane() {
		dtldmeditable = createColdm();
		// 插入列
		Enumeration<DBColumnDisplayInfo> en = apif.getDtlDBColumnDisplayInfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			if (dbcol.isReadonly())
				continue;
			if (dbcol.getColtype().equals("行号"))
				continue;
			int r = dtldmeditable.getRowCount();
			dtldmeditable.appendRow();
			dtldmeditable.setItemValue(r, "colname", dbcol.getColname());
			dtldmeditable.setItemValue(r, "title", dbcol.getTitle());

			// 是否有禁止?
			String afvalue = apif.getApvalue("dtlforbidedit_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dtldmeditable.setItemValue(r, "forbidedit", "1");
			} else {
				dtldmeditable.setItemValue(r, "forbidedit", "0");
			}
		}
		dtldmeditable.appendRow();
	}

	/**
	 * 生成 是否隐藏
	 * 
	 * @return
	 */
	void createHidepane() {
		dmhide = createHideColdm();


		// 插入列
		Enumeration<DBColumnDisplayInfo> en = apif.loadOrgDBmodeldefine()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			//if (dbcol.isHide())
			//	continue;
			if (dbcol.getColtype().equals("行号"))
				continue;
			int r = dmhide.getRowCount();
			dmhide.appendRow();
			dmhide.setItemValue(r, "colname", dbcol.getColname());
			dmhide.setItemValue(r, "title", dbcol.getTitle());

			// 是否有隐藏?
			String afvalue = apif.getApvalue("hide_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dmhide.setItemValue(r, "hide", "1");
			} else {
				dmhide.setItemValue(r, "hide", "0");
			}
		}
		dmhide.appendRow();
	}

	void createDtlHidepane() {
		dtldmhide = createHideColdm();
		CEditableTable table = new CEditableTable(dtldmhide);


		// 插入列
		Enumeration<DBColumnDisplayInfo> en = apif.loadDtlOrgDBColumnDisplayInfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			//if (dbcol.isHide())
			//	continue;
			if (dbcol.getColtype().equals("行号"))
				continue;
			int r = dtldmhide.getRowCount();
			dtldmhide.appendRow();
			dtldmhide.setItemValue(r, "colname", dbcol.getColname());
			dtldmhide.setItemValue(r, "title", dbcol.getTitle());

			// 是否有隐藏?
			String afvalue = apif.getApvalue("dtlhide_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dtldmhide.setItemValue(r, "hide", "1");
			} else {
				dtldmhide.setItemValue(r, "hide", "0");
			}
		}
		dtldmhide.appendRow();
	}

}
