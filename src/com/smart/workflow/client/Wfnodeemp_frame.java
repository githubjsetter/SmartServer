package com.smart.workflow.client;

import com.smart.platform.gui.ste.*;
import com.smart.platform.util.DefaultNPParam;

import java.awt.*;

/*功能"结点人员"Frame窗口*/
public class Wfnodeemp_frame extends Steframe{
	public Wfnodeemp_frame() throws HeadlessException {
		super("结点人员");
	}

	protected CSteModel getStemodel() {
		return new Wfnodeemp_ste(this);
	}
	public void setWfnodeid(String wfnodeid){
		((Wfnodeemp_ste)stemodel).setWfnodeid(wfnodeid);
	}

	
	@Override
	public Dimension getPreferredSize() {
		
		return new Dimension(640,480);
	}

	@Override
	public void setVisible(boolean b) {
		if(b){
			stemodel.doQuery("");
		}
		super.setVisible(b);
	}


	public static void main(String[] argv){
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		
		Wfnodeemp_frame w=new Wfnodeemp_frame();
		w.pack();
		w.setVisible(true);
	}
}
