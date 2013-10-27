package com.inca.adminclient.dbcp;


import com.inca.np.gui.control.DBColumnDisplayInfo;
import java.util.Vector;

import com.inca.np.gui.control.DBTableModel;

public class DbcpDbmodel extends DBTableModel{
	public DbcpDbmodel(){
		super(createCols());
	}

	public static Vector<DBColumnDisplayInfo> createCols(){
		Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col=null;
		col=new DBColumnDisplayInfo("name","varchar","连接池名称");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("driverClassName","varchar","驱动类名");
		cols.add(col);

		col=new DBColumnDisplayInfo("url","varchar","连接URL");
		cols.add(col);

		col=new DBColumnDisplayInfo("username","varchar","连接用户名");
		cols.add(col);

		col=new DBColumnDisplayInfo("password","varchar","连接密码");
		cols.add(col);

		col=new DBColumnDisplayInfo("maxActive","varchar","最大活动数");
		cols.add(col);

		col=new DBColumnDisplayInfo("maxIdle","varchar","最大空闲数");
		cols.add(col);

		col=new DBColumnDisplayInfo("maxWait","varchar","最大等待毫秒");
		cols.add(col);

		return cols;
	}
}
