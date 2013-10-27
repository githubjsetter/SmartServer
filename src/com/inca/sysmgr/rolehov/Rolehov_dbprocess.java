package com.inca.sysmgr.rolehov;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.server.process.MdeProcessor;

public class Rolehov_dbprocess extends MdeProcessor{

	@Override
	protected String getDetailtablename() {
		return "np_hov_ap";
	}

	@Override
	protected String getMastertablename() {
		return "np_role";
	}

	@Override
	protected CMdeModel getMdeModel() {
		return new Rolehov_mde(null,"");
	}

}
