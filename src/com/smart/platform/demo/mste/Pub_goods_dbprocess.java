package com.smart.platform.demo.mste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
/*����"��Ʒ����"Ӧ�÷���������*/
public class Pub_goods_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pub_goods_ste(null);
	}
	protected String getTablename() {
		return "pub_goods";
	}
}
