package com.smart.server.pushplat.common;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

public class Pushdbmodel extends DBTableModel{

	public Pushdbmodel() {
		super(createCols());
	}

	public static Vector<DBColumnDisplayInfo> createCols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col=new DBColumnDisplayInfo("行号","行号","行号");
		cols.add(col);
		col=new DBColumnDisplayInfo("pushid","number","推送ID");
		cols.add(col);
		col=new DBColumnDisplayInfo("pushname","varchar","推送名称");
		cols.add(col);
		col=new DBColumnDisplayInfo("groupname","varchar","分组名");
		cols.add(col);
		col=new DBColumnDisplayInfo("level","number","级别");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		col.addComboxBoxItem("1", "非常紧急");
		col.addComboxBoxItem("2", "紧急");
		col.addComboxBoxItem("3", "普通");
		cols.add(col);

		col=new DBColumnDisplayInfo("callopid","number","调用功能ID");
		cols.add(col);
		col=new DBColumnDisplayInfo("callopname","varchar","调用功能");
		cols.add(col);

		col=new DBColumnDisplayInfo("wheres","varchar","调用where条件");
		cols.add(col);

		col=new DBColumnDisplayInfo("otherwheres","varchar","授权where条件");
		cols.add(col);
		return cols;
	}
}
