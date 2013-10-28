package com.smart.platform.demo.ste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*功能"附件测试"应用服务器处理*/
public class Testfile_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Testfile_ste(null);
	}
	protected String getTablename() {
		return "testfile";
	}
}
