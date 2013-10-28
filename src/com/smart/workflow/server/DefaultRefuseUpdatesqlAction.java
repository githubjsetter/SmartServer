package com.smart.workflow.server;

import com.smart.workflow.common.WfnodeActionIF;

public class DefaultRefuseUpdatesqlAction extends DefaultUpdatesqlAction{
	public String getActiontype() {
		return WfnodeActionIF.ACTIONTYPE_REFUSE_UPDATESQL;
	}

}
