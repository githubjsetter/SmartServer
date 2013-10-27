package com.inca.npbi.server;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*功能"报表定义"应用服务器处理*/
public class Report_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new com.inca.npbi.client.report.Report_ste(null);
	}
	protected String getTablename() {
		return "npbi_report_def";
	}
}
