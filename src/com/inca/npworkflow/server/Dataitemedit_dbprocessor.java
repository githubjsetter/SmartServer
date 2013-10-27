package com.inca.npworkflow.server;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npworkflow.client.Dataitemedit_ste;

public class Dataitemedit_dbprocessor extends SteProcessor{

	@Override
	protected CSteModel getSteModel() {
		
		return new Dataitemedit_ste(null,"");
	}

	@Override
	protected String getTablename() {
		return "np_wf_dataitem";
	}

}
