package com.inca.npworkflow.server;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
import com.inca.npworkflow.client.Approvestatus_ste;
/*����"����״̬����"Ӧ�÷���������*/
public class Approvestatus_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Approvestatus_ste(null);
	}
	protected String getTablename() {
		return "np_wf_approvestatus";
	}
}
