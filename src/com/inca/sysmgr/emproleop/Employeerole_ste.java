package com.inca.sysmgr.emproleop;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.npx.ste.CSteModelAp;

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
