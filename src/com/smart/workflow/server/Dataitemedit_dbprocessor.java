package com.smart.workflow.server;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.workflow.client.Dataitemedit_ste;

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
