package com.smart.platform.logger;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"������־��ѯ"Ӧ�÷���������*/
public class Visitlogger_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Visitlogger_ste(null);
	}
	protected String getTablename() {
		return "np_log";
	}
}
