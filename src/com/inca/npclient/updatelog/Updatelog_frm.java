package com.inca.npclient.updatelog;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Updatelog_frm extends Steframe{

	public Updatelog_frm() throws HeadlessException {
		super("查询修改日志");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Updatelog_ste(this,"修改日志");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Updatelog_frm frm=new Updatelog_frm();
		frm.pack();
		frm.setVisible(true);

	}
}
