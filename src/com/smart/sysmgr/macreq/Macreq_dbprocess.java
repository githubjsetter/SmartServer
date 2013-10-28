package com.smart.sysmgr.macreq;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*功能"入网请求审批"应用服务器处理*/
public class Macreq_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Macreq_ste(null);
	}
	protected String getTablename() {
		return "np_mac_req";
	}
}
