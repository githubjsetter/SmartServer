package com.inca.adminclient.modulemgr;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

public class ModuleDbmodel extends DBTableModel {

	static Vector<DBColumnDisplayInfo> createCols() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("行号", "行号", "行号");
		cols.add(col);

		col = new DBColumnDisplayInfo("prodname", "varchar", "产品名");
		cols.add(col);

		col = new DBColumnDisplayInfo("modulename", "varchar", "模块名");
		cols.add(col);

		col = new DBColumnDisplayInfo("engname", "varchar", "英文名");
		cols.add(col);

		col = new DBColumnDisplayInfo("version", "varchar", "版本");
		cols.add(col);

		col = new DBColumnDisplayInfo("license", "varchar", "授权");
		cols.add(col);

		col = new DBColumnDisplayInfo("clientjar", "varchar", "clientjar");
		col.setHide(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("clientjarmd5", "varchar", "clientjarmd5");
		col.setHide(true);
		cols.add(col);
		return cols;

	}

	public ModuleDbmodel() {
		super(createCols());
	}
}
