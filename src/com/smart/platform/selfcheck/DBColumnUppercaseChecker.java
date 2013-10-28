package com.smart.platform.selfcheck;

import java.util.Enumeration;
import java.util.Vector;

import com.smart.platform.gui.control.DBColumnDisplayInfo;

/**
 * ��д���
 * opcode no pinyin��д
 * @author Administrator
 *
 */
public class DBColumnUppercaseChecker {
	public static void checkUppercase(Vector<DBColumnDisplayInfo> cols,
			Vector<SelfcheckError> errors) {
		Enumeration<DBColumnDisplayInfo> en = cols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			String colname = colinfo.getColname().toLowerCase();
			if(!colinfo.isReadonly() && !colinfo.isHide()){
				if(colname.endsWith("no") || colname.endsWith("opcode")||
						colname.endsWith("pinyin")){
					if(!colinfo.isUppercase()){
						SelfcheckError error = new SelfcheckError("UI0006",
								SelfcheckConstants.UI0006);
						errors.add(error);
						error.setMsg(colinfo.getColname()+"�����д");
					}
				}
			}
		}
	}

}
