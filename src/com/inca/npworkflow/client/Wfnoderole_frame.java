package com.inca.npworkflow.client;

import com.inca.np.gui.ste.*;
import com.inca.np.util.DefaultNPParam;

import java.awt.*;

/*功能"结点角色"Frame窗口*/
public class Wfnoderole_frame extends Steframe{
	public Wfnoderole_frame() throws HeadlessException {
		super("结点角色");
	}

	protected CSteModel getStemodel() {
		return new Wfnoderole_ste(this);
	}

	public void setWfnodeid(String wfnodeid){
		((Wfnoderole_ste)stemodel).setWfnodeid(wfnodeid);
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

		
		Wfnoderole_frame w=new Wfnoderole_frame();
		w.pack();
		w.setVisible(true);
	}
}
