package com.smart.workflow.server;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.workflow.client.Wfnodeemp_ste;
/*����"�����Ա"Ӧ�÷���������*/
public class Wfnodeemp_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Wfnodeemp_ste(null);
	}
	protected String getTablename() {
		return "np_wf_node_employeeid";
	}
}
