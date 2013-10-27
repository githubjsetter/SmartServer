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
		col=new DBColumnDisplayInfo("name","varchar","���ӳ�����");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("driverClassName","varchar","��������");
		cols.add(col);

		col=new DBColumnDisplayInfo("url","varchar","����URL");
		cols.add(col);

		col=new DBColumnDisplayInfo("username","varchar","�����û���");
		cols.add(col);

		col=new DBColumnDisplayInfo("password","varchar","��������");
		cols.add(col);

		col=new DBColumnDisplayInfo("maxActive","varchar","�����");
		cols.add(col);

		col=new DBColumnDisplayInfo("maxIdle","varchar","��������");
		cols.add(col);

		col=new DBColumnDisplayInfo("maxWait","varchar","���ȴ�����");
		cols.add(col);

		return cols;
	}
}
