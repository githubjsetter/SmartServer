package com.smart.platform.demo.mde;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
/*功能"货品和货品明细"应用服务器处理*/
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
