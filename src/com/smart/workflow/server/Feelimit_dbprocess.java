package com.smart.workflow.server;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.workflow.demo.Feelimit_ste;
/*����"�����޶�(demo)"Ӧ�÷���������*/
public class Feelimit_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Feelimit_ste(null);
	}
	protected String getTablename() {
		return "np_wf_demo_fee_limit";
	}
}
