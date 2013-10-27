package com.inca.npbi.server;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npbi.client.ds.Ds_ste;
/*功能"数据源连接管理"应用服务器处理*/
public class Ds_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Ds_ste(null);
	}
	protected String getTablename() {
		return "npbi_ds";
	}
}
