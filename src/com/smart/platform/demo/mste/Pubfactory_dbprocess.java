package com.smart.platform.demo.mste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"���ҹ���"Ӧ�÷���������*/
public class Pubfactory_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pubfactory_ste(null);
	}
	protected String getTablename() {
		return "pub_factory";
	}
}
