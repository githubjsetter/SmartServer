package com.inca.sysmgr.emproleop;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.npx.ste.CSteModelAp;

/**
 * ¹¦ÄÜ.
 * @author user
 *
 */
public class Op_ste extends CMasterModel{


	public Op_ste(CFrame frame, String title, CMdeModel mdemodel)
			throws HeadlessException {
		super(frame, title, mdemodel);
	}

	@Override
	public String getTablename() {
		return "np_role_op_v";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	public void doNew() {
		// TODO Auto-generated method stub
		super.doNew();
	}
	
	
}
