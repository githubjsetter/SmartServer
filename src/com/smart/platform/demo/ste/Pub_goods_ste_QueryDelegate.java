package com.smart.platform.demo.ste;

import java.util.Enumeration;

import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;

public class Pub_goods_ste_QueryDelegate extends CSteModel.QueryDelegate{

/*	@Override
	public Querycond on_query(Querycond querycond) {
		Querycond newcond=new Querycond();
		
		Enumeration<Querycondline> en=querycond.elements();
		while(en.hasMoreElements()){
			Querycondline line=en.nextElement();
			if(line.getColname().equals("goodsid")){
				newcond.add(line);
			}else if(line.getColname().equals("opcode")){
					newcond.add(line);
			}else if(line.getColname().equals("goodsname")){
				newcond.add(line);
			}
		}
		
		return newcond;
	}
	
*/
}
