package com.smart.adminclient.installjar;

import java.awt.HeadlessException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.TableCellEditor;





import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.server.install.Installinfo;
import com.smart.server.install.Installinfo.Opinfo;

public class OpeditPane extends Basepane{
	
	public OpeditPane(CFrame frame,Installinfo installinfo){
		super(frame,installinfo,"功能");
	}
	
	@Override
	protected void createCols(){
		DBColumnDisplayInfo col=new DBColumnDisplayInfo("行号","行号","行号");
		cols.add(col);
		
		col=new DBColumnDisplayInfo("opid","number","ID");
		cols.add(col);
		col=new DBColumnDisplayInfo("opcode","varchar","操作码");
		cols.add(col);
		col=new DBColumnDisplayInfo("opname","varchar","功能名");
		cols.add(col);
		col=new DBColumnDisplayInfo("classname","varchar","类名");
		cols.add(col);
		col=new DBColumnDisplayInfo("groupname","varchar","组名");
		cols.add(col);
	}

	
	@Override
	protected void bind(){
		DBTableModel dbmodel=ste.getDBtableModel();
		dbmodel.clearAll();
		Enumeration<Opinfo> en=installinfo.getOpinfos().elements();
		while(en.hasMoreElements()){
			Opinfo opinfo=en.nextElement();
			int r=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(r, "opid", opinfo.opid);
			dbmodel.setItemValue(r, "opcode", opinfo.opcode);
			dbmodel.setItemValue(r, "opname", opinfo.opname);
			dbmodel.setItemValue(r, "classname", opinfo.classname);
			dbmodel.setItemValue(r, "groupname", opinfo.groupname);
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
		Vector<Opinfo> ops=new Vector<Opinfo>();
		for(int r=0;r<dbmodel.getRowCount();r++){
			Opinfo opinfo=new Opinfo();
			opinfo.opid=dbmodel.getItemValue(r, "opid");
			opinfo.opcode=dbmodel.getItemValue(r, "opcode");
			opinfo.opname=dbmodel.getItemValue(r, "opname");
			opinfo.classname=dbmodel.getItemValue(r, "classname");
			opinfo.groupname=dbmodel.getItemValue(r, "groupname");
			ops.add(opinfo);
		}
		installinfo.setOpinfos(ops);
	}
}
