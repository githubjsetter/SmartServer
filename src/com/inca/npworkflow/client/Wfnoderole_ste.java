package com.inca.npworkflow.client;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

import java.awt.*;
import java.awt.dnd.Autoscroll;

/*功能"结点角色"单表编辑Model*/
public class Wfnoderole_ste extends CSteModel{
	String wfnodeid="";

	public Wfnoderole_ste(CFrame frame) throws HeadlessException {
		super(frame, "结点角色");
	}

	public String getTablename() {
		return "np_wf_node_roleid_v";
	}

	public String getSaveCommandString() {
		return "Wfnoderole_ste.保存结点角色";
	}

	@Override
	protected String getOtherWheres() {
		return "wfnodeid="+wfnodeid;
	}

	@Override
	protected void doExit() {
		getParentFrame().dispose();
		super.freeMemory();
	}

	@Override
	public void doNew() {
		Role_mhov hov=new Role_mhov();
		DBTableModel result=hov.showDialog(getParentFrame(), "选择角色(多选)");
		if(result==null){
			return;
		}
		result=hov.getTablemodel();
		int rows[]=hov.getDlgtable().getSelectedRows();

		for(int i=0;i<rows.length;i++){
			String roleid=result.getItemValue(rows[i], "roleid");
			String rolename=result.getItemValue(rows[i], "rolename");
			if(isExistsRoleid(roleid)){
				continue;
			}
			int newrow=dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(newrow, "roleid", roleid);
			dbmodel.setItemValue(newrow, "rolename", rolename);
			dbmodel.setItemValue(newrow, "wfnodeid", wfnodeid);
		}
		
		tableChanged();
		table.autoSize();
	}

	private boolean isExistsRoleid(String roleid) {
		for(int r=0;r<dbmodel.getRowCount();r++){
			if(dbmodel.getItemValue(r, "roleid").equals(roleid))return true;
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
