package com.inca.npworkflow.server;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npworkflow.client.Wfnoderole_ste;
/*����"����ɫ"Ӧ�÷���������*/
public class Wfnoderole_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Wfnoderole_ste(null);
	}
	protected String getTablename() {
		return "np_wf_node_roleid";
	}
}
