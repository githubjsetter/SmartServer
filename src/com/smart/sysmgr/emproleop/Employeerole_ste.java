package com.smart.sysmgr.emproleop;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;

/**
 * ÈËÔ±½ÇÉ«
 * @author user
 *
 */
public class Employeerole_ste  extends CSteModelAp{

	public Employeerole_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_employee_role_v";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}
}
