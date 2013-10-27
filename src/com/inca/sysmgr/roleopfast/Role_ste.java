package com.inca.sysmgr.roleopfast;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.ste.CSteModel;

public class Role_ste extends CSteModel{

	public Role_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_role";
	}

	@Override
	public String getSaveCommandString() {
		return "com.inca.sysmgr.roleopfast.±£´æ½ÇÉ«";
	}

}
