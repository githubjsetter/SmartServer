package com.inca.np.demo.mde;

import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
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
