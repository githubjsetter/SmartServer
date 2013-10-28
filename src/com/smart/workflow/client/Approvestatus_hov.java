package com.smart.workflow.client;

import com.smart.platform.gui.control.CStehovEx;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.util.DefaultNPParam;

public class Approvestatus_hov extends CStehovEx{

	@Override
	protected CSteModel createStemodel() {
		Approvestatus_frame frm=new Approvestatus_frame();
		frm.pack();
		CSteModel ste=frm.getCreatedStemodel();
		return ste;
	}

	public Querycond getQuerycond() {
		Querycond cond=new Querycond();
		return cond;
	}
	
	public void setWfid(String wfid){
		((Approvestatus_ste)stemodel).setWfid(wfid);
		setOtherwheres("wfid="+wfid);
	}
	public void setCurrentv(String currentv) {
		((Approvestatus_ste)stemodel).setCurrentv(currentv);
	}

	
	@Override
	protected boolean autoSelect() {
		return true;
	}
	
	

	@Override
	protected boolean autoReturn() {
		return false;
	}
	
	
	@Override
	protected void on_retrieved() {
		super.on_retrieved();
		DBTableModel dbmodel=stemodel.getDBtableModel();
		dbmodel.sort(new String[]{"statusid"},true);
		stemodel.tableChanged();
		String currentv=((Approvestatus_ste)stemodel).getCurrentv();
		for(int i=0;i<dbmodel.getRowCount();i++){
			if(dbmodel.getItemValue(i, "statusid").equals(currentv)){
				stemodel.getTable().getSelectionModel().addSelectionInterval(i, i);
			}
		}
	}


	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		Approvestatus_hov hov=new Approvestatus_hov();
		hov.showDialog(null, "hov");

	}
}
