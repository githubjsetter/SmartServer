package com.smart.platform.demo.mde;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;

import java.awt.*;

/*����"��Ʒ�ͻ�Ʒ��ϸ"ϸ��Model*/
public class Goodsdtl_detail extends CDetailModel{
	public Goodsdtl_detail(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "��Ʒ��ϸ", mdemodel);
	}

	public String getTablename() {
		return "pub_goods_detail";
	}

	public String getSaveCommandString() {
		return null;
	}

	@Override
	public String getHovOtherWheres(int row, String colname) {
		return "100=100";
	}
	
	
}
