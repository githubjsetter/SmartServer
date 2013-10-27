package com.inca.npworkflow.client;

import java.awt.HeadlessException;

import javax.swing.SwingUtilities;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;

public class Wfap_frame extends Steframe{

	public Wfap_frame() throws HeadlessException {
		super("工作流基表数据授权");
	}

	@Override
	protected CSteModel getStemodel() {
		return new Wfap_ste(this,"基表数据授权");
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		
		if(b){
			Runnable r=new Runnable(){
				public void run(){
					stemodel.doQuery();
				}
			};
			SwingUtilities.invokeLater(r);
		}
	}

	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";
		
		Wfap_frame frm=new Wfap_frame();
		frm.pack();
		Wfap_ste ste=(Wfap_ste)frm.getCreatedStemodel();
		ste.setWfid("3");
		frm.setVisible(true);
	}
}
