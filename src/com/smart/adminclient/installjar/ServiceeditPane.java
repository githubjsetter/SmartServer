package com.smart.adminclient.installjar;

import java.awt.HeadlessException;
import java.util.Enumeration;
import java.util.Vector;





import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.server.install.Installinfo;
import com.smart.server.install.Installinfo.Serviceinfo;

public class ServiceeditPane extends Basepane{
	
	public ServiceeditPane(CFrame frame,Installinfo installinfo){
		super(frame,installinfo,"服务");
	}

	@Override
	protected void createCols(){
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("行号","行号","行号");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("command","varchar","命令");
		cols.add(col);
		col=new DBColumnDisplayInfo("classname","varchar","类名");
		cols.add(col);
	}
	
	
	@Override
	protected void bind(){
		DBTableModel dbmodel=ste.getDBtableModel();
		dbmodel.clearAll();
		Enumeration<Serviceinfo> en=installinfo.getServiceinfos().elements();
		while(en.hasMoreElements()){
			Serviceinfo serviceinfo=en.nextElement();
			int r=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "command", serviceinfo.command);
			dbmodel.setItemValue(r, "classname", serviceinfo.classname);
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
		Vector<Serviceinfo> services=new Vector<Serviceinfo>();
		for(int r=0;r<dbmodel.getRowCount();r++){
			Serviceinfo sinfo=new Serviceinfo();
			sinfo.command=dbmodel.getItemValue(r, "command");
			sinfo.classname=dbmodel.getItemValue(r, "classname");
			services.add(sinfo);
		}
		installinfo.setServiceinfos(services);
	}
	
}
