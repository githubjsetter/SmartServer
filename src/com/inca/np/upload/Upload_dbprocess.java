package com.inca.np.upload;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*功能"上传日志管理"应用服务器处理*/
public class Upload_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Upload_ste(null);
	}
	protected String getTablename() {
		return "np_upload_log";
	}
}
