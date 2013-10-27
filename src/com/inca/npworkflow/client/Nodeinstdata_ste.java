package com.inca.npworkflow.client;

import java.awt.HeadlessException;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.ste.CSteModel;

/**
 * 结点决策数据项ste
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
		return "Nodeinstdata_ste.保存结点决策数据";
	}

}
