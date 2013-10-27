package com.inca.npclient.updatelog;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.npx.ste.CSteModelAp;

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
