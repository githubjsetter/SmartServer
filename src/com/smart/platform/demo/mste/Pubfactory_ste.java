package com.smart.platform.demo.mste;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

import java.awt.*;

/*功能"厂家管理"单表编辑Model*/
public class Pubfactory_ste extends CSteModel{
	public Pubfactory_ste(CFrame frame) throws HeadlessException {
		super(frame, "厂家");
	}

	public String getTablename() {
		return "pub_factory_v";
	}

	public String getSaveCommandString() {
		return "com.inca.np.demo.mste.Pubfactory_ste.保存厂家";
	}

	@Override
	protected int on_beforeNew() {
		// TODO Auto-generated method stub
		return super.on_beforeNew();
	}
	
	
}
