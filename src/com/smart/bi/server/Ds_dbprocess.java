package com.smart.bi.server;
import com.smart.bi.client.ds.Ds_ste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*功能"数据源连接管理"应用服务器处理*/
public class Ds_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Ds_ste(null);
	}
	protected String getTablename() {
		return "npbi_ds";
	}
}
