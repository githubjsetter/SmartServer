package com.inca.sysmgr.roleopfast;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;

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
