package com.smart.platform.anyprint;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 设置数据源对话框
 * 
 * @author Administrator
 * 
 */
public class DssetupDlg extends CDialog {
	private JTextArea textSql;
	Datasource datasource;
	Datasource maindatasource;
	private JTextField textRelatecolname;
	private JComboBox cbType;
	boolean ok;
	private JTextField textViewname;

	/**
	 * 设置数据源
	 * 
	 * @param frame
	 * @param datasource
	 *            数据源
	 * @param maindatasource
	 *            主数据源
	 */
	public DssetupDlg(java.awt.Frame frame, Datasource datasource,
			Datasource maindatasource) {
		super(frame, "设置数据源", true);
		this.datasource = datasource;
		this.maindatasource = maindatasource;
		init();
		bindValue();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	void bindValue() {
		if (datasource == null)
			return;
		textSql.setText(datasource.getSql());
		cbType.setSelectedItem(datasource.getType());
		textViewname.setText(datasource.getViewname());
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

		textSql = new JTextArea(30, 80);
		textSql.setWrapStyleWord(true);
		textSql.setLineWrap(true);
		cp.add(new JScrollPane(textSql), new GridBagConstraints(1, y, 1, 1,
				0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 12, 5, 5), 0, 0));

		y++;
		lb = new JLabel("取中文列名视图");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		textViewname = new JTextField(20);
		cp.add(textViewname, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		y++;
		lb = new JLabel("类型");
		cp.add(lb, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		if (maindatasource == null) {
			String dstypes[] = { "主数据源" };
			cbType = new JComboBox(dstypes);
			cbType.setEnabled(false);
		} else {
			String dstypes[] = { "辅助数据源(关联)", "辅助数据源(字符串相加)" };
			cbType = new JComboBox(dstypes);
			cbType.setEditable(false);
		}
		cp.add(cbType, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0,
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
	protected void enterkeyConfirm() {
		onOk();
	}

	void onOk() {
		String sql = this.getSql();
		if (sql.length() == 0) {
			JOptionPane.showMessageDialog(this, "必须输入sql");
			return;
		}
		
		if(!checkSql(sql)){
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
		textViewname.setText(tablename);
		StringBuffer colnamesb = new StringBuffer();
		ArrayList<String> ar = new ArrayList<String>();
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			colnamesb.append("\t" + dtlmodel.getItemValue(row, "cname"));
			if (i != rows.length - 1)
				colnamesb.append(",");
			colnamesb.append("\n");
			ar.add(dtlmodel.getItemValue(row, "cname"));
		}
		String[] colnames = new String[ar.size()];
		ar.toArray(colnames);
		String mcolnames[] = null;
		if (maindatasource == null) {
		} else {
			// 再选择一次主数据源的列
			String mainsql = maindatasource.getSql();
			mainsql = mainsql.replaceAll("\\{入口参数\\}", "''");
			RemotesqlHelper sqlh = new RemotesqlHelper();
			DBTableModel maindb = null;
			try {
				maindb = sqlh.doSelect(mainsql, 0, 1);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "执行主数据源sql错误:"
						+ e.getMessage());
				return;
			}
			ar = new ArrayList<String>();
			Enumeration<DBColumnDisplayInfo> en = maindb
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo col = en.nextElement();
				ar.add(col.getColname());
			}
			mcolnames = new String[ar.size()];
			ar.toArray(mcolnames);
		}

		// 显示maindb的列供选择
		SelectdscolDlg coldlg = new SelectdscolDlg(this, colnames, mcolnames);
		coldlg.pack();
		coldlg.setVisible(true);
		if (!coldlg.isOk())
			return;
		String relatecolname = coldlg.getSelectcolname();
		String mcolname = coldlg.getMaincolname();
		String sql = "select " + colnamesb.toString() + " from " + tablename
				+ " where " + relatecolname + " in ({" + mcolname + "})";
		textSql.setText(sql);
		return;
	}

	public boolean isOk() {
		return ok;
	}

	public String getSql() {
		return textSql.getText().trim();
	}

	public String getViewname() {
		return textViewname.getText().trim();
	}

	public String getDstype() {
		return (String) cbType.getSelectedItem();
	}

	
	@Override
	public void setVisible(boolean b) {
		if(datasource.getSql().length()==0){
			Runnable r=new Runnable(){
				public void run(){
					onGuide();
				}
			};
			SwingUtilities.invokeLater(r);
		}
		super.setVisible(b);
	}
	
	boolean checkSql(String sql){
		sql = sql.replaceAll("\\{入口参数\\}", "''");
		sql = sql.replaceAll("\\{.*\\}", "''");
		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel maindb = null;
		try {
			maindb = sqlh.doSelect(sql, 0, 1);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "sql错误:"
					+ e.getMessage());
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		Datasource ds = new Datasource("select * from pub_goods", "goodsid");
		DssetupDlg dlg = new DssetupDlg(null, ds, null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
