package com.smart.workflow.server;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.workflow.client.Wfnoderole_ste;
/*功能"结点角色"应用服务器处理*/
public class Wfnoderole_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Wfnoderole_ste(null);
	}
	protected String getTablename() {
		return "np_wf_node_roleid";
	}
}
