package com.inca.np.logger;
import com.inca.np.server.process.MdeProcessor;
import com.inca.np.gui.mde.CMdeModel;
/*����"��ѯ������������־"Ӧ�÷���������*/
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
