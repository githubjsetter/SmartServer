package com.smart.sysmgr.oproleap;

import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;

public class Roleopap_process extends MdeProcessor{

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
		return new Roleopap_mde(null,"");
	}

}
