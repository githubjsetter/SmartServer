package com.smart.platform.demo.mste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"��Ʒ��ϸ"Ӧ�÷���������*/
public class Pubgoodsdetail_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pubgoodsdetail_ste(null);
	}
	protected String getTablename() {
		return "pub_goods_detail";
	}
}
