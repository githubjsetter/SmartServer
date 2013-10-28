package com.smart.client.updatelog;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;

public class Updatereg_ste extends CSteModelAp{

	public Updatereg_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_update_reg";
	}

	@Override
	public String getSaveCommandString() {
		return "npclient:±£´ænp_update_reg";
	}

}
