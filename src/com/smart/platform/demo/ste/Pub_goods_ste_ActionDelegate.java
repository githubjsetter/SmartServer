package com.smart.platform.demo.ste;

import com.smart.platform.gui.ste.CSteModel;

public class Pub_goods_ste_ActionDelegate extends CSteModel.ActionDelegate{
	@Override
	public int on_actionPerformed(CSteModel stemodel,String command) {
		System.out.println("Pub_goods_model_ActionDelegate  ’µΩcommand="+command);
		//stemodel.getParentFrame().errorMessage(title, msg)
		return -1;
	}

}
