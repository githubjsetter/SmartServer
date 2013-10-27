package com.inca.npworkflow.server;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npworkflow.client.Nodeinstdata_ste;

public class Nodeinstdata_dbprocessor extends SteProcessor{

	@Override
	protected CSteModel getSteModel() {
		return new Nodeinstdata_ste(null,"");
	}

	@Override
	protected String getTablename() {
		return "np_wf_node_data";
	}

}
