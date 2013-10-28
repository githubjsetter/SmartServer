package com.smart.server.pushplat.server;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
import com.smart.server.pushplat.client.Rolepush_mde;

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
