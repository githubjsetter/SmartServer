package com.smart.platform.demo.mste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*功能"厂家管理"应用服务器处理*/
public class Pubfactory_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pubfactory_ste(null);
	}
	protected String getTablename() {
		return "pub_factory";
	}
}
