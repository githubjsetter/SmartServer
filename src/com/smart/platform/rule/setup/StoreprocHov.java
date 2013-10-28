package com.smart.platform.rule.setup;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import org.apache.log4j.Category;

import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.util.DBHelper;
import com.smart.platform.util.DefaultNPParam;

public class StoreprocHov extends CHovBase {

	Category logger = Category.getInstance(StoreprocHov.class);

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("name", "varchar",
				"名称");
		cols.add(col);

		col = new DBColumnDisplayInfo("type", "varchar", "类型");
		cols.add(col);

		return new DBTableModel(cols);
	}

	@Override
	public String getDefaultsql() {
		String sql = "select object_name name,object_type type "
			+ " from user_objects where object_type in ('PROCEDURE')";
		return sql;
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond = new Querycond();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("object_name",
				"varchar", "存储过程名称");
		col.setUppercase(true);
		Querycondline ql = new Querycondline(cond, col);
		cond.add(ql);

		return cond;
	}

	public String[] getColumns() {
		// TODO Auto-generated method stub
		return new String[] { "name" };
	}

	public String getDesc() {
		// TODO Auto-generated method stub
		return "查询选择存储过程";
	}

/*	@Override
	protected void doQuery() {
		String sql = "select object_name name,object_type type "
				+ " from user_objects where object_type in ('PROCEDURE')";
		String wheres = querycond.getHovWheres();
		sql = DBHelper.addWheres(sql, wheres);
		RemotesqlHelper sqlh = new RemotesqlHelper();
		try {
			DBTableModel dbmodel = (DBTableModel) dlgtable.getModel();
			dbmodel.clearAll();
			DBTableModel result = sqlh.doSelect(sql, 0, 1000);
			dbmodel.bindMemds(result);
			dlgtable.tableChanged(new TableModelEvent(dbmodel));
			dlgtable.autoSize();
		} catch (Exception e) {
			logger.error("ERROR", e);
			this.errorMessage("错误", e.getMessage());
			return;
		}
	}
*/
	@Override
	protected void addOtherbutton(JPanel toolpane) {
		JButton btn;
		btn = new JButton("编辑");
		btn.setActionCommand("editstoreproc");
		btn.addActionListener(new Buttonhandle());
		toolpane.add(btn);

		btn = new JButton("新增后处理");
		btn.setActionCommand("addstoreproc");
		btn.addActionListener(new Buttonhandle());
		toolpane.add(btn);

		btn = new JButton("新增查询前处理");
		btn.setActionCommand("addprequerystoreproc");
		btn.addActionListener(new Buttonhandle());
		toolpane.add(btn);

	}

	void editStoreproc() {
		int row = this.dlgtable.getRow();
		if (row < 0)
			return;
		DBTableModel dbmodel = (DBTableModel) dlgtable.getModel();
		String procname = dbmodel.getItemValue(row, "name");
		StoreproceditDlg editdlg = new StoreproceditDlg(
				StoreprocHov.this.hovdialog);
		editdlg.pack();
		try {
			editdlg.editStoreproc(procname);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(hovdialog, e.getMessage());
			return;
		}
		editdlg.setVisible(true);
		if(!editdlg.isOk())return;
		procname=editdlg.getStoreprocname();
		Querycondline condline = querycond.getQuerycondline("object_name");
		if (condline != null) {
			condline.setValue(procname);
			doQuery();
		}

	}

	void addStoreproc() {
		StoreproceditDlg editdlg = new StoreproceditDlg(
				StoreprocHov.this.hovdialog);
		editdlg.pack();
		editdlg.createNew();
		editdlg.setVisible(true);
		if(!editdlg.isOk())return;
		String procname=editdlg.getStoreprocname();
		Querycondline condline = querycond.getQuerycondline("object_name");
		if (condline != null) {
			condline.setValue(procname);
			doQuery();
		}

	}

	void addPrequerystoreproc(){
		StoreproceditDlg editdlg = new StoreproceditDlg(
				StoreprocHov.this.hovdialog);
		editdlg.pack();
		editdlg.createNewprequery();
		editdlg.setVisible(true);
		if(!editdlg.isOk())return;
		String procname=editdlg.getStoreprocname();
		Querycondline condline = querycond.getQuerycondline("object_name");
		if (condline != null) {
			condline.setValue(procname);
			doQuery();
		}
	}
	class Buttonhandle implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("editstoreproc")) {
				editStoreproc();
			} else if (cmd.equals("addstoreproc")) {
				addStoreproc();
			} else if(cmd.equals("addprequerystoreproc")){
				addPrequerystoreproc();
			}
		}

	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.defaultappsvrurl = "http://127.0.0.1/npserver/clientrequest.do";
		DefaultNPParam.prodcontext = "npserver";
		DefaultNPParam.depttable_use_pub_dept = false;

		StoreprocHov hov = new StoreprocHov();
		hov.showDialog((Frame) null, "");
	}
}
