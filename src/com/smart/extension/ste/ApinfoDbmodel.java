package com.smart.extension.ste;

import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 传递授权属性的Dbmodel
 * @author Administrator
 *
 */
public class ApinfoDbmodel extends DBTableModel{
	

	public ApinfoDbmodel() {
		super(cols);
	}
	
	static Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
	static{
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("apid","number","apid");
		col.setIspk(true);
		col.setSeqname("np_opap_seq");
		cols.add(col);

		col=new DBColumnDisplayInfo("roleopid","number","roleopid");
		cols.add(col);
		col=new DBColumnDisplayInfo("aptype","varchar","aptype");
		cols.add(col);
		col=new DBColumnDisplayInfo("apname","varchar","apname");
		cols.add(col);
		col=new DBColumnDisplayInfo("apvalue","varchar","apvalue");
		cols.add(col);
		
	}
}
