package com.smart.workflow.client;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*功能"流程状态管理"单表编辑Model*/
public class Approvestatus_ste extends CSteModel{
	String wfid="";
	String currentv="";
	
	public Approvestatus_ste(CFrame frame) throws HeadlessException {
		super(frame, "流程状态");
	}

	public String getTablename() {
		return "np_wf_approvestatus";
	}

	public String getSaveCommandString() {
		return "Approvestatus_ste.保存流程状态";
	}

	public String getWfid() {
		return wfid;
	}

	public void setWfid(String wfid) {
		this.wfid = wfid;
	}

	@Override
	protected int on_new(int row) {
		setItemValue(row, "wfid", wfid);
		return super.on_new(row);
	}

	public String getCurrentv() {
		return currentv;
	}

	public void setCurrentv(String currentv) {
		this.currentv = currentv;
	}
	
}
