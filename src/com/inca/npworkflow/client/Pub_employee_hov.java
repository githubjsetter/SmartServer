package com.inca.npworkflow.client;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.TableModel;

import com.inca.np.gui.control.CHovBase;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class Pub_employee_hov  extends CHovBase {
	public Pub_employee_hov() throws HeadlessException {
		super();
	}

	@Override
	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> tablecolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo editor = null;

		editor = new DBColumnDisplayInfo("employeeid", "number", "人员ID", true);
		editor.setIspk(true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("employeename", "varchar", "人员名称",
				false);
		tablecolumndisplayinfos.add(editor);
		
		editor = new DBColumnDisplayInfo("deptname", "varchar", "部门名称", false);
		tablecolumndisplayinfos.add(editor);

		editor = new DBColumnDisplayInfo("deptid", "number", "部门ID", true);
		tablecolumndisplayinfos.add(editor);
		editor = new DBColumnDisplayInfo("opcode", "varchar", "人员操作码", false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		
		editor = new DBColumnDisplayInfo("pinyin", "varchar", "人员拼音", false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);
		
		editor = new DBColumnDisplayInfo("deptopcode", "varchar", "部门操作码",
				false);
		editor.setUppercase(true);
		tablecolumndisplayinfos.add(editor);

		return new DBTableModel(tablecolumndisplayinfos);
	}

	@Override
	public String getDefaultsql() {
		if(stopbox.isSelected()){
			return "select * from pub_employee_v where (usestatus = 1 or usestatus = 0)";
		}
		return "select * from pub_employee_v where usestatus = 1";
	}

	@Override
	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "人员操作码", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeename", "varchar", "人员名称",
				false);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeeid", "number", "人员ID", true);
		colinfo.setIspk(true);
		querycond.add(new Querycondline(querycond, colinfo));

		return querycond;
	}

	public String[] getColumns() {
		return new String[] { "employeeid", "opcode", "employeename", "pinyin",
				"deptopcode", "deptname", "deptid" };
	}

	public String getDesc() {
		return "选择人员";
	}
	JCheckBox stopbox= new JCheckBox();
	@Override
	protected JPanel buildQuerypanel(Querycond arg0) {
		JPanel jp=super.buildQuerypanel(arg0);
		jp.add(stopbox);
		jp.add(new JLabel("含停用"));
		//stopbox = new JCheckBox();
		return jp;
	}
}
