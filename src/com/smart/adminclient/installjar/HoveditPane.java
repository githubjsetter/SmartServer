package com.smart.adminclient.installjar;

import java.util.Enumeration;
import java.util.Vector;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.server.install.Installinfo;
import com.smart.server.install.Installinfo.Hovinfo;


public class HoveditPane  extends Basepane{
	public HoveditPane(CFrame frame,Installinfo installinfo){
		super(frame,installinfo,"HOV");
	}

	@Override
	protected void createCols(){
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("hovname","varchar","HOV����");
		cols.add(col);
		col=new DBColumnDisplayInfo("classname","varchar","����");
		cols.add(col);
	}
	
	
	@Override
	protected void bind(){
		DBTableModel dbmodel=ste.getDBtableModel();
		dbmodel.clearAll();
		Enumeration<Hovinfo> en=installinfo.getHovinfos().elements();
		while(en.hasMoreElements()){
			Hovinfo hovinfo=en.nextElement();
			int r=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "hovname", hovinfo.hovname);
			dbmodel.setItemValue(r, "classname", hovinfo.classname);
			dbmodel.setdbStatus(r, RecordTrunk.DBSTATUS_SAVED);
		}
		ste.getSumdbmodel().fireDatachanged();
		ste.tableChanged();
		ste.getTable().autoSize();
	}
	
	@Override
	public void rebind(){
		ste.commitEdit();
		DBTableModel dbmodel=ste.getDBtableModel();
		Vector<Hovinfo> hovs=new Vector<Hovinfo>();
		for(int r=0;r<dbmodel.getRowCount();r++){
			Hovinfo hovinfo=new Hovinfo();
			hovinfo.hovname=dbmodel.getItemValue(r, "hovname");
			hovinfo.classname=dbmodel.getItemValue(r, "classname");
			hovs.add(hovinfo);
		}
		installinfo.setHovinfos(hovs);
	}
	
}
