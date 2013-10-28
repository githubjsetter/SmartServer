package com.smart.platform.selfcheck;

import java.util.Enumeration;
import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;

/**
 * 状态要使用下拉选择
 * 
 * @author Administrator
 * 
 */
public class DBColumnComboboxChecker {
	public static void checkComboBox(Vector<DBColumnDisplayInfo> cols,
			Vector<SelfcheckError> errors) {
		Enumeration<DBColumnDisplayInfo> en = cols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			String colname = colinfo.getColname().toLowerCase();
			if (colname.endsWith("goodsstatus"))
				continue;

			if (colname.endsWith("status") || colname.endsWith("statusid")) {
				if (!colinfo.getEditcomptype().equals(
						DBColumnDisplayInfo.EDITCOMP_COMBOBOX)) {
					SelfcheckError error = new SelfcheckError("UI0004",
							SelfcheckConstants.UI0004);
					errors.add(error);
					error.setMsg("列" + colinfo.getColname() + "应该使用下拉选择");
				}
			}
		}
	}

}
