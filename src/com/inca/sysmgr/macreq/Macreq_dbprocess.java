package com.inca.sysmgr.macreq;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*����"������������"Ӧ�÷���������*/
public class Macreq_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Macreq_ste(null);
	}
	protected String getTablename() {
		return "np_mac_req";
	}
}
