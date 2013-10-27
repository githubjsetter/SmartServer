package com.inca.np.demo.ste;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*功能"附件测试"应用服务器处理*/
public class Testfile_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Testfile_ste(null);
	}
	protected String getTablename() {
		return "testfile";
	}
}
