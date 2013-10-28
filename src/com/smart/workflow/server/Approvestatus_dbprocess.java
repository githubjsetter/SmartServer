package com.smart.workflow.server;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.workflow.client.Approvestatus_ste;
/*功能"流程状态管理"应用服务器处理*/
public class Approvestatus_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Approvestatus_ste(null);
	}
	protected String getTablename() {
		return "np_wf_approvestatus";
	}
}
