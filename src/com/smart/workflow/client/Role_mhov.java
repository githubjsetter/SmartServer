package com.smart.workflow.client;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.table.TableModel;

import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;

public class Role_mhov extends CMultiHov{
	public Role_mhov() throws HeadlessException {
		super();
	}

	public String getDefaultsql() {
		return "select roleid,opcode,rolename from np_role";
	}

	public Querycond getQuerycond() {
		Querycond querycond = new Querycond();

		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "操作码", false);
		colinfo.setUppercase(true);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("rolename", "varchar", "角色", false);
		querycond.add(new Querycondline(querycond, colinfo));

		colinfo = new DBColumnDisplayInfo("roleid", "number", "角色ID", false);
		querycond.add(new Querycondline(querycond, colinfo));

		return querycond;
	}

	protected TableModel createTablemodel() {
		Vector<DBColumnDisplayInfo> infos = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo colinfo = null;

		colinfo = new DBColumnDisplayInfo("rolename", "varchar", "角色", false);
		infos.add(colinfo);

		colinfo = new DBColumnDisplayInfo("opcode", "varchar", "操作码", false);
		infos.add(colinfo);

		colinfo = new DBColumnDisplayInfo("roleid", "number", "角色ID", false);
		infos.add(colinfo);

		return new DBTableModel(infos);
	}

	public String getDesc() {
		return "选择角色(多选)";
	}

	public String[] getColumns() {
		return new String[] { "roleid","rolename" };
	}


}
