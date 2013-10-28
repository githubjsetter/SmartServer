package com.smart.sysmgr.oproleap;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;

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
