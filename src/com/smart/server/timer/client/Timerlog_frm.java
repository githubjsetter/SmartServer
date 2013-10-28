package com.smart.server.timer.client;

import java.awt.HeadlessException;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.util.DefaultNPParam;

public class Timerlog_frm extends Steframe {

	public Timerlog_frm() throws HeadlessException {
		super("查询定时任务日志");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Timerlog_ste(this,"定时任务日志");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";

		Timerlog_frm frm=new Timerlog_frm();
		frm.pack();
		frm.setVisible(true);
		
	}
}
