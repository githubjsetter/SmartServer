package com.smart.platform.logger;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
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
