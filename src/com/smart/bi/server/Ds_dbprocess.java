package com.smart.bi.server;
import com.smart.bi.client.ds.Ds_ste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"����Դ���ӹ���"Ӧ�÷���������*/
public class Ds_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Ds_ste(null);
	}
	protected String getTablename() {
		return "npbi_ds";
	}
}
