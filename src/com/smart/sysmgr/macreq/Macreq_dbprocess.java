package com.smart.sysmgr.macreq;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"������������"Ӧ�÷���������*/
public class Macreq_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Macreq_ste(null);
	}
	protected String getTablename() {
		return "np_mac_req";
	}
}
