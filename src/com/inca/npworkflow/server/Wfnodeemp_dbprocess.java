package com.inca.npworkflow.server;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npworkflow.client.Wfnodeemp_ste;
/*����"�����Ա"Ӧ�÷���������*/
public class Wfnodeemp_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Wfnodeemp_ste(null);
	}
	protected String getTablename() {
		return "np_wf_node_employeeid";
	}
}
