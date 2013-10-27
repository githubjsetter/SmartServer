package com.inca.np.demo.mste;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*功能"厂家管理"应用服务器处理*/
public class Pubfactory_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pubfactory_ste(null);
	}
	protected String getTablename() {
		return "pub_factory";
	}
}
