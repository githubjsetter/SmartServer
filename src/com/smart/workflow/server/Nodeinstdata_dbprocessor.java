package com.smart.workflow.server;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.workflow.client.Nodeinstdata_ste;

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
