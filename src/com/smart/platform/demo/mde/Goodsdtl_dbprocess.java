package com.smart.platform.demo.mde;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
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
