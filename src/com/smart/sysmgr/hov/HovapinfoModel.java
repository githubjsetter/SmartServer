package com.smart.sysmgr.hov;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

public class HovapinfoModel extends DBTableModel{
	public HovapinfoModel(){
		super(cols);
	}
	
	
	static Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
	static{
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("apid","number");
		col.setIspk(true);
		col.setSeqname("np_hovap_seq");
		cols.add(col);
		
		
		col=new DBColumnDisplayInfo("hovid","number");
		cols.add(col);
		col=new DBColumnDisplayInfo("roleid","number");
		cols.add(col);
		col=new DBColumnDisplayInfo("aptype","varchar");
		cols.add(col);
		col=new DBColumnDisplayInfo("apname","varchar");
		cols.add(col);
		col=new DBColumnDisplayInfo("apvalue","varchar");
		cols.add(col);
		
	}

}
