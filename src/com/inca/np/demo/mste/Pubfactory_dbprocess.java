package com.inca.np.demo.mste;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*����"���ҹ���"Ӧ�÷���������*/
public class Pubfactory_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pubfactory_ste(null);
	}
	protected String getTablename() {
		return "pub_factory";
	}
}
