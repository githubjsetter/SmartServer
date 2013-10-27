package com.inca.np.logger;
import com.inca.np.server.process.MdeProcessor;
import com.inca.np.gui.mde.CMdeModel;
/*功能"查询服务器错误日志"应用服务器处理*/
public class Logger_dbprocess extends MdeProcessor{
	protected CMdeModel getMdeModel() {
		return new Logger_mde(null,"");
	}
	protected String getMastertablename() {
		return "np_error";
	}
	protected String getDetailtablename() {
		return "np_error_dtl";
	}
}
