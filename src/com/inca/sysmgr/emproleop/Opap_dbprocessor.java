package com.inca.sysmgr.emproleop;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.server.process.MdeProcessor;

public class Opap_dbprocessor extends MdeProcessor{

	@Override
	protected String getDetailtablename() {
		return "np_op_ap";
	}

	@Override
	protected String getMastertablename() {
		return "np_role_op";
	}

	@Override
	protected CMdeModel getMdeModel() {
		return new Opap_mde(null,"");
	}

}
