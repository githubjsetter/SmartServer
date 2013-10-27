package com.inca.npserver.servermanager;

import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

public class Proddbmodel extends DBTableModel {

	static Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
	static {
		DBColumnDisplayInfo col;

		col = new DBColumnDisplayInfo("prodname", "varchar", "产品名");
		cols.add(col);

		col = new DBColumnDisplayInfo("authunit", "varchar", "授权单位");
		cols.add(col);

		col = new DBColumnDisplayInfo("serverip", "varchar", "服务器IP");
		cols.add(col);

		col = new DBColumnDisplayInfo("maxclientuser", "varchar", "最大客户端数");
		cols.add(col);

		col = new DBColumnDisplayInfo("startdate", "varchar", "授权开始日期");
		cols.add(col);

		col = new DBColumnDisplayInfo("enddate", "varchar", "授权结束日期");
		cols.add(col);

		col = new DBColumnDisplayInfo("copyright", "varchar", "版权所有");
		cols.add(col);
		col = new DBColumnDisplayInfo("modules", "varchar", "授权模块");
		cols.add(col);
	}

	public Proddbmodel() {
		super(cols);
	}
}
