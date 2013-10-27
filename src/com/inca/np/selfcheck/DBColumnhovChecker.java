package com.inca.np.selfcheck;

import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.gui.control.DBColumnDisplayInfo;

/**
 * 检查触发hov列
 * @author Administrator
 *
 */
public class DBColumnhovChecker {
	public static void checkHov(Vector<DBColumnDisplayInfo> cols,
			Vector<SelfcheckError> errors) {
		Enumeration<DBColumnDisplayInfo> en = cols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			String colname=colinfo.getColname().toLowerCase();
			if(colinfo.getHovdefine()!=null && colname.endsWith("id")){
				SelfcheckError error=new SelfcheckError("UI0003",SelfcheckConstants.UI0003);
				errors.add(error);
				error.setMsg("列"+colinfo.getColname()+"不能触发HOV");
			}
		}
	}

}
