package com.inca.npworkflow.server;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
import com.inca.npworkflow.client.Approvestatus_ste;
/*功能"流程状态管理"应用服务器处理*/
public class Approvestatus_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Approvestatus_ste(null);
	}
	protected String getTablename() {
		return "np_wf_approvestatus";
	}
}
