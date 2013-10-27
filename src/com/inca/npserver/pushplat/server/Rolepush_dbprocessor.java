package com.inca.npserver.pushplat.server;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.server.process.MdeProcessor;
import com.inca.npserver.pushplat.client.Rolepush_mde;

public class Rolepush_dbprocessor extends MdeProcessor{

	public Rolepush_dbprocessor() {
		super();
	}

	@Override
	protected String getDetailtablename() {
		return "np_role_push";
	}

	@Override
	protected String getMastertablename() {
		return "np_role";
	}

	@Override
	protected CMdeModel getMdeModel() {
		return new Rolepush_mde(null,"");
	}

}
