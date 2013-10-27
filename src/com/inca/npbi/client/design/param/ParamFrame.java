package com.inca.npbi.client.design.param;

import java.awt.Dimension;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class ParamFrame extends Steframe {
	public ParamFrame() {
		super("��������");
		setDefaultCloseOperation(Steframe.DISPOSE_ON_CLOSE);

	}

	public void pack() {
		super.pack();
		localCenter();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800, 480);
	}

	@Override
	protected CSteModel getStemodel() {
		return new Paramste(this, "����");
	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		ClientUserManager.getCurrentUser().setUserid("0");

		ParamFrame frm = new ParamFrame();
		frm.pack();
		frm.setVisible(true);

	}
}
