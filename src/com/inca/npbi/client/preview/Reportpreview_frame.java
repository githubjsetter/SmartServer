package com.inca.npbi.client.preview;

import java.awt.HeadlessException;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Reportpreview_frame extends Steframe{
	String instanceid;

	public Reportpreview_frame(String title,String instanceid) throws HeadlessException {
		super(title);
		this.instanceid=instanceid;
	}

	@Override
	protected CSteModel getStemodel() {
		return new Reportpreview_ste(this,"preview",instanceid);
	}
	
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Reportpreview_frame frm=new Reportpreview_frame("‘§¿¿","5");
		frm.pack();
		frm.setVisible(true);
		
	}
}
