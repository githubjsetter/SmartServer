package com.inca.np.demo.mste;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.gui.ste.CSteModel;
/*功能"货品明细"应用服务器处理*/
public class Pubgoodsdetail_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Pubgoodsdetail_ste(null);
	}
	protected String getTablename() {
		return "pub_goods_detail";
	}
}
