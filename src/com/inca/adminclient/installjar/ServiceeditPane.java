package com.inca.adminclient.installjar;

import java.awt.HeadlessException;
import java.util.Enumeration;
import java.util.Vector;


import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.npserver.install.Installinfo;
import com.inca.npserver.install.Installinfo.Serviceinfo;

public class ServiceeditPane extends Basepane{
	
	public ServiceeditPane(CFrame frame,Installinfo installinfo){
		super(frame,installinfo,"����");
	}

	@Override
	protected void createCols(){
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("�к�","�к�","�к�");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("command","varchar","����");
		cols.add(col);
		col=new DBColumnDisplayInfo("classname","varchar","����");
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
