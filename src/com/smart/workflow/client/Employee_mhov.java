package com.smart.workflow.client;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;

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

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "������", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeename", "varchar", "����", false);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("employeeid", "number", "��ԱID", false);
		querycond.add(new Querycondline(querycond, colinfo));

		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("employeename", "varchar", "����", false);
		infos.add(colinfo);

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "������", false);
		infos.add(colinfo);

		colinfo = new DBColumnDisplayInfo("employeeid", "number", "��ԱID", false);
		infos.add(colinfo);

		return new DBTableModel(infos);
	}

	public String getDesc() {
		return "ѡ����Ա(��ѡ)";
	}

	public String[] getColumns() {
		return new String[] { "employeeid","employeename" };
	}


}
