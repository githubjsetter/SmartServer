package com.smart.workflow.client;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*����"����״̬����"����༭Model*/
public class Approvestatus_ste extends CSteModel{
	String wfid="";
	String currentv="";
	
	public Approvestatus_ste(CFrame frame) throws HeadlessException {
		super(frame, "����״̬");
	}

	public String getTablename() {
		return "np_wf_approvestatus";
	}

	public String getSaveCommandString() {
		return "Approvestatus_ste.��������״̬";
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
