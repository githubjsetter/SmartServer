package com.inca.np.demo.mste;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*����"��Ʒ��ϸ"Ӧ�÷���������*/
public class Pubgoodsdetail_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pubgoodsdetail_ste(null);
	}
	protected String getTablename() {
		return "pub_goods_detail";
	}
}
