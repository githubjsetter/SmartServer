package com.smart.bi.server;
import com.smart.bi.client.view.View_ste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*功能"视图管理"应用服务器处理*/
public class View_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new View_ste(null);
	}
	protected String getTablename() {
		return "npbi_view";
	}
}
