package com.smart.bi.server;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"������"Ӧ�÷���������*/
public class Report_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new com.smart.bi.client.report.Report_ste(null);
	}
	protected String getTablename() {
		return "npbi_report_def";
	}
}
