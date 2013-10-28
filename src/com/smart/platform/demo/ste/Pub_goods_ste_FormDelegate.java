package com.smart.platform.demo.ste;

import java.util.Enumeration;
import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;

public class Pub_goods_ste_FormDelegate extends CSteModel.FormDelegate{

	@Override
	public void on_createForm(Vector<DBColumnDisplayInfo> formcolumndisplayinfos) {
		Enumeration<DBColumnDisplayInfo> en=formcolumndisplayinfos.elements();
		while(en.hasMoreElements()){
			DBColumnDisplayInfo colinfo=en.nextElement();
		}
	}
}
 