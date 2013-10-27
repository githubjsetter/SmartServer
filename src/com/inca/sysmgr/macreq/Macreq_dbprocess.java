package com.inca.sysmgr.macreq;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*功能"入网请求审批"应用服务器处理*/
public class Macreq_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Macreq_ste(null);
	}
	protected String getTablename() {
		return "np_mac_req";
	}
}
