package com.inca.np.anyprint;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TableModelEvent;

import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.CToolbar;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * 数据源定义
 * 
 * @author Administrator
 * 
 */
public class Datasourcepane extends JPanel implements ActionListener {
	DBTableModel dbmodel = null;
	CTable table = null;
	AnyprintFrame frm = null;
	Printplan plan = null;

	public Datasourcepane(AnyprintFrame frm,Printplan plan) {
		this.frm = frm;
		this.plan=plan;
		setLayout(new BorderLayout());
		add(createToolbar(), BorderLayout.NORTH);

		dbmodel = new DBTableModel(createCols());
		table = new CTable(dbmodel);
		table.setReadonly(true);
		table.addMouseListener(new TablemouseListener());
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void bind() {
		dbmodel.clearAll();
		Enumeration<Datasource> en = plan.getDatasources().elements();
		while (en.hasMoreElements()) {
			Datasource ds = en.nextElement();
			int r = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "sql", ds.getSql());
			dbmodel.setItemValue(r, "viewname", ds.getViewname());
			dbmodel.setItemValue(r, "type", ds.getType());
		}
		table.tableChanged(new TableModelEvent(table.getModel()));
		table.autoSize();
	}

	CToolbar createToolbar() {
		CToolbar tb = new CToolbar();
		JButton btn=null;
		
		btn = new JButton("增加数据源");
		btn.setActionCommand("add");
		btn.addActionListener(Datasourcepane.this);
		tb.add(btn);

		btn = new JButton("修改数据源");
		btn.setActionCommand("modify");
		btn.addActionListener(Datasourcepane.this);
		tb.add(btn);

		btn = new JButton("删除数据源");
		btn.setActionCommand("del");
		btn.addActionListener(Datasourcepane.this);
		tb.add(btn);

		btn = new JButton("设置中文列名");
		btn.setActionCommand("setupcname");
		btn.addActionListener(Datasourcepane.this);
		tb.add(btn);

		btn = new JButton("测试数据源");
		btn.setActionCommand("query");
		btn.addActionListener(Datasourcepane.this);
		tb.add(btn);
		
		btn = new JButton("导出数据源");
		btn.setActionCommand("export");
		btn.addActionListener(Datasourcepane.this);
		//tb.add(btn);

		btn = new JButton("导入数据源");
		btn.setActionCommand("import");
		btn.addActionListener(Datasourcepane.this);
		//tb.add(btn);

		return tb;
	}

	static Vector<DBColumnDisplayInfo> createCols() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("sql", "varchar", "sql");
		cols.add(col);

		col = new DBColumnDisplayInfo("type", "varchar", "数据源类型");
		cols.add(col);

		col = new DBColumnDisplayInfo("viewname", "varchar", "中文列名视图");
		cols.add(col);
		return cols;
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("add")) {
			onAdd();
		} else if (cmd.equals("modify")) {
			onModify();
		} else if (cmd.equals("del")) {
			onDel();
		} else if (cmd.equals("setupcname")) {
			setCname();
		} else if (cmd.equals("query")) {
			doQuery();
		} 
	}

	void doQuery() {
		frm.setWaitcursor();
		QuerydsDlg dlg = new QuerydsDlg(frm, plan);
		frm.setDefaultcursor();
		dlg.pack();
		dlg.setVisible(true);
	}

	void setCname() {
		frm.setWaitcursor();
		CcolnamesetupDlg dlg = new CcolnamesetupDlg(frm, plan);
		frm.setDefaultcursor();
		dlg.pack();
		dlg.setVisible(true);
	}

	void onDel() {
		int row = table.getRow();
		if (row < 0)
			return;
		if (row == 0) {
			JOptionPane.showMessageDialog(frm, "不能删除主数据源");
			return;
		}
		dbmodel.removeRow(row);
		table.tableChanged(new TableModelEvent(table.getModel()));
		table.autoSize();
		reverseBind();

	}

	void onModify() {
		int row = table.getRow();
		if (row < 0)
			return;
		Datasource newds = Row2ds(row);
		Datasource maindatasource = null;
		if (row == 0) {

		} else {
			maindatasource = Row2ds(0);
		}
		DssetupDlg dlg = new DssetupDlg(frm, newds, maindatasource);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		newds.setSql(dlg.getSql());
		newds.setType(dlg.getDstype());
		newds.setViewname(dlg.getViewname());
		Dsinfo2row(row, newds);
		table.tableChanged(new TableModelEvent(table.getModel()));
		table.autoSize();
		reverseBind();

	}

	void onAdd() {
		Datasource maindatasource = null;
		if (dbmodel.getRowCount() == 0) {
		} else {
			maindatasource = Row2ds(0);
		}
		Datasource newds = new Datasource("", "");
		DssetupDlg dlg = new DssetupDlg(frm, newds, maindatasource);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		newds.setSql(dlg.getSql());
		newds.setType(dlg.getDstype());
		newds.setViewname(dlg.getViewname());
		int row = dbmodel.getRowCount();
		dbmodel.appendRow();
		Dsinfo2row(row, newds);
		table.tableChanged(new TableModelEvent(table.getModel()));
		table.autoSize();
		reverseBind();

	}

	void Dsinfo2row(int row, Datasource ds) {
		dbmodel.setItemValue(row, "sql", ds.getSql());
		dbmodel.setItemValue(row, "type", ds.getType());
		dbmodel.setItemValue(row, "viewname", ds.getViewname());
		table.tableChanged(new TableModelEvent(table.getModel(), row));
	}

	Datasource Row2ds(int row) {
		Datasource ds = new Datasource(dbmodel.getItemValue(row, "sql"),
				dbmodel.getItemValue(row, "type"));
		ds.setViewname(dbmodel.getItemValue(row, "viewname"));
		return ds;
	}

	void reverseBind() {
		plan.getDatasources().clear();
		for (int i = 0; i < dbmodel.getRowCount(); i++) {
			Datasource ds = Row2ds(i);
			plan.addDatasource(ds);
		}
	}

	class TablemouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				onModify();
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
