package com.smart.sysmgr.roleopfast;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;

public class Role_dbprocessor extends SteProcessor{

	@Override
	protected CSteModel getSteModel() {
		return new Role_ste(null,"");
	}

	@Override
	protected String getTablename() {
		return "np_role";
	}

}
