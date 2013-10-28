package com.smart.bi.client.design;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.smart.bi.client.design.param.BIReportparamdefine;
import com.smart.platform.anyprint.SelectcolumnHov;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

public class ReportsqlDlg extends CDialog {
	private JTextArea textSql;
	boolean ok;
	String sql = "";
	HashMap<String, String> cntitlemap = new HashMap<String, String>();
	BIReportdsDefine dsdefine;
	private CTable paramtable;

	/**
	 * 设置数据源
	 * 
	 * @param frame
	 * @param datasource
	 *            数据源
	 * @param maindatasource
	 *            主数据源
	 */
	public ReportsqlDlg(java.awt.Frame frame, String sql,
			BIReportdsDefine dsdefine) {
		super(frame, "写SQL", true);
		this.sql = sql;
		this.dsdefine = dsdefine;
		init();
		bindValue();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	void bindValue() {
		textSql.setText(sql);

	}

	void init() {
		Container cp = this.getContentPane();
		GridBagLayout g = new GridBagLayout();
		cp.setLayout(g);

		int y = 0;
		JLabel lb = new JLabel("sql");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		textSql = new JTextArea();
		textSql.setWrapStyleWord(true);
		textSql.setLineWrap(true);
		
		JScrollPane jspsql=new JScrollPane(textSql);
		Dimension sqlsize=new Dimension(550,300);
		jspsql.setMinimumSize(sqlsize);
		jspsql.setMaximumSize(sqlsize);
		jspsql.setPreferredSize(sqlsize);
		cp.add(jspsql, new GridBagConstraints(1, y, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 12, 5, 5), 0, 0));

		// 参数列表.
		y++;
		lb = new JLabel("参数");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		paramtable = createParamtable();
		JScrollPane jsp = new JScrollPane(paramtable);
		Dimension compsize = new Dimension(550, 200);
		jsp.setPreferredSize(compsize);
		jsp.setMaximumSize(compsize);
		jsp.setMinimumSize(compsize);
		cp.add(jsp, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		JPanel tb = new JPanel();
		JButton btn = null;
		btn = new JButton("SQL向导");
		addEnterkeyTraver(btn);
		btn.setActionCommand("guide");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new JButton("确定");
		addEnterkeyConfirm(btn);
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		tb.add(btn);
		btn = new JButton("取消");
		addEnterkeyTraver(btn);
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		tb.add(btn);
		y++;
		cp.add(tb, new GridBagConstraints(0, y, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 12, 5, 5), 0, 0));

	}

	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(640,580);
	}

	@Override
	protected void enterkeyConfirm() {
		onOk();
	}

	void onOk() {
		String sql = this.getSql();
		if (sql.length() == 0) {
			JOptionPane.showMessageDialog(this, "必须输入sql");
			return;
		}
		String oldsql = dsdefine.sql;
		dsdefine.sql = sql;
		if (!checkSql(dsdefine.getTestsql())) {
			dsdefine.sql = oldsql;
			return;
		}

		ok = true;
		dispose();
	}

	void onCancel() {
		ok = false;
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("guide")) {
			onGuide();
		} else if (cmd.equals("ok")) {
			onOk();
		} else if (cmd.equals("cancel")) {
			onCancel();
		}
	}

	/**
	 * 向导。选择表或视图。 选择关联列。 选参数。
	 */
	void onGuide() {
		SelectcolumnHov hov = new SelectcolumnHov();
		DBTableModel result = hov.showDialog(this, "选择表视图和列", "", "", "");
		if (result == null)
			return;
		String tablename = result.getItemValue(0, "tname");
		// 选择关联列
		CTable table = hov.getDtltable();
		DBTableModel dtlmodel = (DBTableModel) table.getModel();
		int rows[] = table.getSelectedRows();
		if (rows.length == 0)
			return;
		// textViewname.setText(tablename);
		StringBuffer colnamesb = new StringBuffer();
		ArrayList<String> ar = new ArrayList<String>();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			colnamesb.append("\t" + dtlmodel.getItemValue(row, "cname"));
			if (i != rows.length - 1)
				colnamesb.append(",");
			colnamesb.append("\n");
			String cname = dtlmodel.getItemValue(row, "cname");
			String cntitle = dtlmodel.getItemValue(row, "cntitle");
			ar.add(cname);
			cntitlemap.put(cname, cntitle);
		}
		String[] colnames = new String[ar.size()];
		ar.toArray(colnames);
		String mcolnames[] = null;
		/*
		 * // 显示maindb的列供选择 SelectdscolDlg coldlg = new SelectdscolDlg(this,
		 * colnames, mcolnames); coldlg.pack(); coldlg.setVisible(true); if
		 * (!coldlg.isOk()) return; String relatecolname =
		 * coldlg.getSelectcolname(); String mcolname = coldlg.getMaincolname();
		 */
		String sql = "select " + colnamesb.toString() + " from " + tablename;
		textSql.setText(sql);
		return;
	}

	public boolean isOk() {
		return ok;
	}

	public String getSql() {
		return textSql.getText().trim();
	}

	@Override
	public void setVisible(boolean b) {
		/*
		 * if(datasource.getSql().length()==0){ Runnable r=new Runnable(){
		 * public void run(){ onGuide(); } }; SwingUtilities.invokeLater(r); }
		 */
		super.setVisible(b);
	}

	boolean checkSql(String sql) {
		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel maindb = null;
		try {
			maindb = sqlh.doSelect(sql, 0, 1);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "sql错误:" + e.getMessage());
			return false;
		}
		return true;
	}

	public HashMap<String, String> getCntitlemap() {
		return cntitlemap;
	}

	/**
	 * 生成参数列表
	 * 
	 * @return
	 */
	CTable createParamtable() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("paramname",
				"varchar", "参数名");
		cols.add(col);
		col = new DBColumnDisplayInfo("title", "varchar", "标题");
		cols.add(col);

		DBTableModel dm = new DBTableModel(cols);
		Enumeration<BIReportparamdefine> en = dsdefine.params.elements();
		while (en.hasMoreElements()) {
			BIReportparamdefine p = en.nextElement();
			int r = dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(r, "paramname", p.paramname);
			dm.setItemValue(r, "title", p.title);
		}

		String[][] fixes = { { "人员ID", "人员ID" }, { "部门ID", "部门ID" },
				{ "核算单元ID", "核算单元ID" }, { "角色ID", "角色ID" }, };

		for (int i = 0; i < fixes.length; i++) {
			int r = dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(r, "paramname", fixes[i][1]);
			dm.setItemValue(r, "title", fixes[i][0]);
		}

		CTable table = new CTable(dm);
		table.setReadonly(true);
		table.addMouseListener(new Paramtablemousehandler());
		return table;
	}

	class Paramtablemousehandler implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			int r = paramtable.rowAtPoint(e.getPoint());
			if (r >= 0) {
				DBTableModel dm = (DBTableModel) paramtable.getModel();
				String paramname = dm.getItemValue(r, "paramname");
				textSql.replaceSelection("{" + paramname + "}");
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
}
