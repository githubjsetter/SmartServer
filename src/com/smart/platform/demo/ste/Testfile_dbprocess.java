package com.smart.platform.demo.ste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"��������"Ӧ�÷���������*/
public class Testfile_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Testfile_ste(null);
	}
	protected String getTablename() {
		return "testfile";
	}
}
