package com.inca.np.demo.mste;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import java.awt.*;

/*����"���ҹ���"����༭Model*/
public class Pubfactory_ste extends CSteModel{
	public Pubfactory_ste(CFrame frame) throws HeadlessException {
		super(frame, "����");
	}

	public String getTablename() {
		return "pub_factory_v";
	}

	public String getSaveCommandString() {
		return "com.inca.np.demo.mste.Pubfactory_ste.���泧��";
	}

	@Override
	protected int on_beforeNew() {
		// TODO Auto-generated method stub
		return super.on_beforeNew();
	}
	
	
}
