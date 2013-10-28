package com.smart.sysmgr.emproleop;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

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
