package com.smart.workflow.client;

import java.awt.HeadlessException;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.ste.CSteModel;

/**
 * ������������ste
 * @author user
 *
 */
public class Nodeinstdata_ste extends CSteModel{

	public Nodeinstdata_ste(CFrame frame, String title)
			throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_wf_node_data";
	}

	@Override
	public String getSaveCommandString() {
		return "Nodeinstdata_ste.�������������";
	}

}
