package com.inca.np.rule.define;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.SortsetupDialog;

/**
 * ����.
 * expr:=����,asc|desc[,����,asc|desc]
 * @author Administrator
 *
 */
public class SortRule extends Rulebase {
	static protected String[] treatableruletypes = null;
	static {
		treatableruletypes = new String[] { "��������", "ϸ����������" };
	}

	public static String[] getRuleypes() {
		return treatableruletypes;
	}

	public static boolean canProcessruletype(String ruletype) {
		for (int i = 0; treatableruletypes != null
				&& i < treatableruletypes.length; i++) {
			if (treatableruletypes[i].equals(ruletype))
				return true;
		}
		return false;
	}

	@Override
	public String processSort(Object caller) throws Exception {
		if (expr == null || expr.length() == 0)
			return null;

/*		DBTableModel dbmodel = null;
		if (getRuletype().equals("��������")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " һ����CSteModel��CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else if (caller instanceof CDetailModel) {
				dbmodel = ((CDetailModel)caller).getDBtableModel();
			} else {
				throw new Exception("caller " + caller + " ������CMdeModel");
			}
		}
*/
		// ���ͱ��ʽ
		String ss[] = expr.split(":");
		if (ss.length < 0 || ss.length%2!=0)
			return null;
		
		//dbmodel.sort(expr);

		return expr;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("��������")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " һ����CSteModel��CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else if(caller instanceof CDetailModel){
				dbmodel = ((CDetailModel)caller).getDBtableModel();
			}else {
				throw new Exception("caller " + caller + " ������CMdeModel");
			}
		}

		SortsetupDialog dlg=new SortsetupDialog(null,dbmodel,expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}


}