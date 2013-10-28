package com.smart.platform.demo.mde;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

import java.awt.*;

/*����"��Ʒ�ͻ�Ʒ��ϸ"�ܵ�Model*/
public class Goodsdtl_master extends CMasterModel{
	public Goodsdtl_master(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
		super(frame, "��Ʒ", mdemodel);
	}

	public String getTablename() {
		return "pub_goods";
	}

	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected void invokeMultimdehov(int row, String colname, String value) {
		// TODO Auto-generated method stub
		super.invokeMultimdehov(row, colname, value);
	}

	@Override
	protected String getEditablecolumns(int row) {
		return "credate,opcode,goodsname";
		}
}
