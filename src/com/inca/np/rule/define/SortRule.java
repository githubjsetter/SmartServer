package com.inca.np.rule.define;

import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.SortsetupDialog;

/**
 * 排序.
 * expr:=列名,asc|desc[,列名,asc|desc]
 * @author Administrator
 *
 */
public class SortRule extends Rulebase {
	static protected String[] treatableruletypes = null;
	static {
		treatableruletypes = new String[] { "设置排序", "细单设置排序" };
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
		if (getRuletype().equals("设置排序")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else if (caller instanceof CDetailModel) {
				dbmodel = ((CDetailModel)caller).getDBtableModel();
			} else {
				throw new Exception("caller " + caller + " 必须是CMdeModel");
			}
		}
*/
		// 解释表达式
		String ss[] = expr.split(":");
		if (ss.length < 0 || ss.length%2!=0)
			return null;
		
		//dbmodel.sort(expr);

		return expr;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("设置排序")) {
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel()
						.getDBtableModel();
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		} else {
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel()
						.getDBtableModel();
			} else if(caller instanceof CDetailModel){
				dbmodel = ((CDetailModel)caller).getDBtableModel();
			}else {
				throw new Exception("caller " + caller + " 必须是CMdeModel");
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