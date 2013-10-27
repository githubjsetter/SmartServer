package com.inca.np.demo.mste;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import java.awt.*;

/*功能"货品明细"单表编辑Model*/
public class Pubgoodsdetail_ste extends CSteModel{
	public Pubgoodsdetail_ste(CFrame frame) throws HeadlessException {
		super(frame, "货品明细");
	}

	public String getTablename() {
		return "pub_goods_detail";
	}

	public String getSaveCommandString() {
		return "com.inca.np.demo.mste.Pubgoodsdetail_ste.保存货品明细";
	}

	@Override
	protected int on_beforeNew() {
		// TODO Auto-generated method stub
		return super.on_beforeNew();
	}
	
	
}
