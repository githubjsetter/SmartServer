package com.inca.np.demo.mste;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*����"��Ʒ����"Ӧ�÷���������*/
public class Pub_goods_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pub_goods_ste(null);
	}
	protected String getTablename() {
		return "pub_goods";
	}
}
