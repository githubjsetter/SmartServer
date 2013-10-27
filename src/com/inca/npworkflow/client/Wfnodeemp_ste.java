package com.inca.npworkflow.client;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

import java.awt.*;

/*����"�����Ա"����༭Model*/
public class Wfnodeemp_ste extends CSteModel{
	String wfnodeid="";
	public Wfnodeemp_ste(CFrame frame) throws HeadlessException {
		super(frame, "�����Ա");
	}

	public String getTablename() {
		return "np_wf_node_employeeid_v";
	}

	public String getSaveCommandString() {
		return "Wfnodeemp_ste.��������Ա";
	}
	@Override
	protected void doExit() {
		getParentFrame().dispose();
		super.freeMemory();
	}

	@Override
	protected String getOtherWheres() {
		return "wfnodeid="+wfnodeid;
	}

	@Override
	public void doNew() {
		Employee_mhov hov=new Employee_mhov();
		DBTableModel result=hov.showDialog(getParentFrame(), "ѡ����Ա(��ѡ)");
		if(result==null){
			return;
		}
		result=hov.getTablemodel();
		int rows[]=hov.getDlgtable().getSelectedRows();
		
		for(int i=0;i<rows.length;i++){
			String employeeid=result.getItemValue(rows[i], "employeeid");
			String employeename=result.getItemValue(rows[i], "employeename");
			if(isExistsRoleid(employeeid)){
				continue;
			}
			int newrow=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(newrow, "employeeid", employeeid);
			dbmodel.setItemValue(newrow, "employeename", employeename);
			dbmodel.setItemValue(newrow, "wfnodeid", wfnodeid);
		}
		
		tableChanged();
		table.autoSize();
	}

	private boolean isExistsRoleid(String employeeid) {
		for(int r=0;r<dbmodel.getRowCount();r++){
			if(dbmodel.getItemValue(r, "employeeid").equals(employeeid))return true;
		}
		return false;
	}

	public String getWfnodeid() {
		return wfnodeid;
	}

	public void setWfnodeid(String wfnodeid) {
		this.wfnodeid = wfnodeid;
	}

}
