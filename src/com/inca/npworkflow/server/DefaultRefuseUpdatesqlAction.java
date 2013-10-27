package com.inca.npworkflow.server;

import com.inca.npworkflow.common.WfnodeActionIF;

public class DefaultRefuseUpdatesqlAction extends DefaultUpdatesqlAction{
	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL;
	}

}
