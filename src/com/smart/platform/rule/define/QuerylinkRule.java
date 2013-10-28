package com.smart.platform.rule.define;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.QuerylinkInfo;
import com.smart.platform.gui.ste.QuerylinkSetupDlg;

/**
 * ����
 * expr:= (��������,����opid)(��ѯ����[:��ѯ����])
 * ��ѯ����:��ѯ��������,�߼�������,��������
 * @author Administrator
 *
 */
public class QuerylinkRule  extends Rulebase {
	Category logger=Category.getInstance(QuerylinkRule.class);
	static protected String[] treatableruletypes = null;
	static {
		treatableruletypes = new String[] { "������ѯ", "ϸ��������ѯ" };
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
	public QuerylinkInfo processQuerylink(Object caller) throws Exception {
		if (expr == null || expr.length() == 0)
			return null;


		try{
			QuerylinkInfo qlinfo=QuerylinkInfo.create(expr);
			return qlinfo;
		}catch(Exception e){
			logger.error("error",e);
			return null;
		}
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("������ѯ")) {
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

		QuerylinkSetupDlg dlg=new QuerylinkSetupDlg(null,dbmodel,expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return false;
		QuerylinkInfo qlinfo=dlg.getQuerylinkinfo();
		expr = qlinfo.getExpr();

		return true;
	}


}