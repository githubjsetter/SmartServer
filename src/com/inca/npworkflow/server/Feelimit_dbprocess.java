package com.inca.npworkflow.server;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
import com.inca.npworkflow.demo.Feelimit_ste;
/*����"�����޶�(demo)"Ӧ�÷���������*/
public class Feelimit_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Feelimit_ste(null);
	}
	protected String getTablename() {
		return "np_wf_demo_fee_limit";
	}
}
