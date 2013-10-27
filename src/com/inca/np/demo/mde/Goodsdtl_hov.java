package com.inca.np.demo.mde;

import com.inca.np.gui.control.CMdehovEx;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.ste.Querycond;
import com.inca.np.gui.ste.Querycondline;
import com.inca.np.util.DefaultNPParam;

public class Goodsdtl_hov extends CMdehovEx{

	@Override
	protected CMdeModel createMdemodel() {
		MdeFrame frm=new Goodsdtl_frame();
		frm.setOpid("");
		return frm.getCreatedMdemodel();
	}

	@Override
	public Querycond getQuerycond() {
		Querycond cond= new Querycond();
		DBColumnDisplayInfo colinfo=new DBColumnDisplayInfo("goodsid","number","货品ID");
		Querycondline ql=new Querycondline(cond,colinfo);
		cond.add(ql);

		colinfo=new DBColumnDisplayInfo("opcode","varchar","opcode");
		colinfo.setUppercase(true);
		ql=new Querycondline(cond,colinfo);
		cond.add(ql);

		return cond;
	}

	public String[] getColumns() {
		return new String[]{"goodsid,goodsdtlid"};
	}

	public String getDesc() {
		return "货品明细MDE HOV。demo only";
	}

	public static void main(String[] args) {
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";

		
		Goodsdtl_hov hov=new Goodsdtl_hov();
		DBTableModel result=hov.showDialog(null, "货品明细hov demo");
		if(result==null){
			System.out.println("cancel hov");
		}
		System.out.println("result="+result.getRowCount());
		CMdeModel mde=hov.getMdemodel();
		int masterselectrows[]=mde.getMasterModel().getTable().getSelectedRows();
		System.out.println(masterselectrows.length);
		int detailselectrows[]=mde.getDetailModel().getTable().getSelectedRows();
		System.out.println(detailselectrows.length);
	}
}
