package com.inca.np.logger;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*����"������־��ѯ"Ӧ�÷���������*/
public class Visitlogger_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Visitlogger_ste(null);
	}
	protected String getTablename() {
		return "np_log";
	}
}
