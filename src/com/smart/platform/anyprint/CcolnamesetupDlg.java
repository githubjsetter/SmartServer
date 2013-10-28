package com.smart.platform.anyprint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 设置中文列名
 * 
 * @author Administrator
 * 
 */
public class CcolnamesetupDlg extends CDialog {
	Frame frm;
	Printplan plan;
	DBTableModel dbmodel = null;
	CEditableTable table = null;

	public CcolnamesetupDlg(Frame frm, Printplan plan) {
		super(frm, "设置中文列名", true);
		this.frm = frm;
		this.plan = plan;

		init();
		bindValue();
		this.localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);

	}

	void init() {
		dbmodel = new DBTableModel(createCols());
		table = new CEditableTable(dbmodel);

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		cp.add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel tb = new JPanel();
		JButton btn = new JButton("确定");
		addEnterkeyConfirm(btn);
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		tb.add(btn);
		btn = new JButton("取消");
		addEnterkeyTraver(btn);
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		tb.add(btn);
		cp.add(tb, BorderLayout.SOUTH);

	}

	void reversebind() {
		table.stopEdit();
		for (int r = 0; r < dbmodel.getRowCount() - 1; r++) {
			String colname = dbmodel.getItemValue(r, "colname");
			String title = dbmodel.getItemValue(r, "title");
			plan.getCcolnamemap().put(colname.toUpperCase(), title);
		}
		plan.defineChanged();
	}

	void onOk() {
		reversebind();
		dispose();
	}

	void onCancel() {
		dispose();
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("ok")) {
			onOk();
		} else if (cmd.equals("cancel")) {
			onCancel();
		}
	}

	void bindValue() {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Enumeration<Datasource> en = plan.getDatasources().elements();
			while (en.hasMoreElements()) {
				Datasource ds = en.nextElement();
				setColumn(ds);
			}
			dbmodel.appendRow();// 最后一行不编辑
			table.tableChanged(new TableModelEvent(dbmodel));
			table.autoSize();
		} finally {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	void setColumn(Datasource ds) {
		// 根据sql得列。
		String sql = ds.getSql();
		sql = Datasource.replaceParamtonull(sql);
		RemotesqlHelper sqlh = new RemotesqlHelper();
		String viewname = ds.getViewname();
		// 中文列名
		DBTableModel cbnamedbmodel = null;
		if (viewname.length() > 0) {
			String s = "select colname,cntitle from sys_column_cn where tablename='"
					+ viewname.toUpperCase() + "'";
			try {
				cbnamedbmodel = sqlh.doSelect(s, 0, 1000);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		DBTableModel tmpdbmodel = null;
		try {
			tmpdbmodel = sqlh.doSelect(sql, 0, 1);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "数据源sql错误:" + e.getMessage()
					+ ",sql:" + ds.getSql());
			return;
		}
		Enumeration<DBColumnDisplayInfo> en = tmpdbmodel
				.getDisplaycolumninfos().elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			int row = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(row, "colname", colinfo.getColname());
			dbmodel.setItemValue(row, "title", colinfo.getColname());
			// 回填plan中的定义
			String title = plan.getCcolnamemap().get(
					colinfo.getColname().toUpperCase());
			if (title != null) {
				dbmodel.setItemValue(row, "title", title);
			} else {
				// 查询table_cn中的定义
				if (cbnamedbmodel != null) {
					for (int r = 0; r < cbnamedbmodel.getRowCount(); r++) {
						String tmpname = cbnamedbmodel.getItemValue(r,
								"colname");
						if (colinfo.getColname().equalsIgnoreCase(tmpname)) {
							title = cbnamedbmodel.getItemValue(r, "cntitle");
							if (title.length() > 0) {
								dbmodel.setItemValue(row, "title", title);
							}
							break;
						}
					}
				}
			}
		}
	}

	static Vector<DBColumnDisplayInfo> createCols() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "列名");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "中文列名");
		cols.add(col);
		return cols;
	}

}
