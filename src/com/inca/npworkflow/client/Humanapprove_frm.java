package com.inca.npworkflow.client;

import java.awt.HeadlessException;

import javax.swing.SwingUtilities;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Humanapprove_frm extends MdeFrame{

	public Humanapprove_frm() throws HeadlessException {
		super("…Û≈˙");
		// TODO Auto-generated constructor stub
	}
	@Override
	protected CMdeModel getMdeModel() {
		return new Humanapprove_mde(this,"…Û≈˙");
	}
	
	
	
	@Override
	public void pack() {
		// TODO Auto-generated method stub
		super.pack();
		
		
		Runnable r=new Runnable(){
			public void run(){
				getCreatedMdemodel().doQuery();
			}
		};
		SwingUtilities.invokeLater(r);
	}
	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Humanapprove_frm frm=new Humanapprove_frm();
		frm.pack();
		frm.setVisible(true);
	}
}