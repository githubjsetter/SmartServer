package com.smart.platform.upload;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"�ϴ���־����"Ӧ�÷���������*/
public class Upload_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Upload_ste(null);
	}
	protected String getTablename() {
		return "np_upload_log";
	}
}
