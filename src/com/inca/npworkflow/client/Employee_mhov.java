package com.inca.npworkflow.client;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.inca.np.gui.control.CMultiHov;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;

public class Employee_mhov  extends CMultiHov{
	public Employee_mhov() throws HeadlessException {
		super();
	}

	public String getDefaultsql() {
		return "select employeeid,opcode,employeename from pub_employee";
	}

	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();

		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "操作码", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeename", "varchar", "姓名", false);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeeid", "number", "人员ID", false);
		querycond.add(new Querycondline(querycond, colinfo));

		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("employeename", "varchar", "姓名", false);
		infos.add(colinfo);

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "操作码", false);
		infos.add(colinfo);

		colinfo = new DBColumnDisplayInfo("employeeid", "number", "人员ID", false);
		infos.add(colinfo);

		return new DBTableModel(infos);
	}

	public String getDesc() {
		return "选择人员(多选)";
	}

	public String[] getColumns() {
		return new String[] { "employeeid","employeename" };
	}


}
