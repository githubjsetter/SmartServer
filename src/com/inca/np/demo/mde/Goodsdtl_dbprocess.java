package com.inca.np.demo.mde;
import com.inca.np.server.process.MdeProcessor;
import com.inca.np.gui.mde.CMdeModel;
/*����"��Ʒ�ͻ�Ʒ��ϸ"Ӧ�÷���������*/
public class Goodsdtl_dbprocess extends MdeProcessor{
	protected CMdeModel getMdeModel() {
		return new Goodsdtl_mde(null,"");
	}
	protected String getMastertablename() {
		return "pub_goods";
	}
	protected String getDetailtablename() {
		return "pub_goods_detail";
	}
}
