package com.inca.sysmgr.oproleap;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.npx.ste.CSteModelAp;

/**
 * ¹¦ÄÜ.
 * @author user
 *
 */
public class Op_ste extends CSteModelAp{

	public Op_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_op";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}
}
