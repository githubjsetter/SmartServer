package com.smart.client.updatelog;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Updatereg_frm extends Steframe{

	public Updatereg_frm() throws HeadlessException {
		super("�Ǽ���Ҫ��¼�޸���־�ı�");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Updatereg_ste(this,"��Ҫ��¼�޸���־�ı�");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Updatereg_frm frm=new Updatereg_frm();
		frm.pack();
		frm.setVisible(true);
	}
}
