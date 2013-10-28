package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * ��ֵ�趨 expr:=����:��ֵ
 * 
 * ��ֵ��ѡ��Ϊ ��ǰ��ԱID ��ǰ����ID ��ǰ��ɫID ��ǰ�ŵ�ID ��ǰ������ID ��ǰ�߼��� ��ǰʱ��
 * 
 * @author Administrator
 * 
 */
public class Initrule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "���ó�ֵ", "ϸ�����ó�ֵ" };
	}
	public static String[] getRuleypes(){
		return treatableruletypes;
	}
	
	public static boolean canProcessruletype(String ruletype){
		for(int i=0;treatableruletypes!=null && i<treatableruletypes.length;i++){
			if(treatableruletypes[i].equals(ruletype))return true;
		}
		return false;
	}

	static String[] initnames = { "��ǰ��ԱID", "��ǰ����ID", "��ǰ���㵥ԪID", "��ǰ��ɫID", "��ǰ�ŵ�ID",
			"��ǰ������ID", "��ǰ�߼���", "��ǰʱ��", };

	@Override
	public int process(Object caller, int row) throws Exception {
		if (expr == null)
			return 0;

		DBTableModel dbmodel = null;
		if (getRuletype().equals("���ó�ֵ")) {
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

		// ���ͱ��ʽ
		String ss[] = expr.split(":");
		if (ss.length < 2)
			return 0;
		String colname = ss[0];
		DBColumnDisplayInfo thisinfo = null;
		Enumeration<DBColumnDisplayInfo> en = dbmodel.getDisplaycolumninfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equalsIgnoreCase(colname)) {
				thisinfo = colinfo;
				break;
			}
		}
		if (thisinfo == null) {
			throw new Exception("û���ҵ���" + colname);
		}

		String initname = ss[1];
		Userruninfo u = ClientUserManager.getCurrentUser();
		if (initname.equals("��ǰ��ԱID")) {
			dbmodel.setItemValue(row, colname, u.getUserid());
		} else if (initname.equals("��ǰ����ID")) {
			dbmodel.setItemValue(row, colname, u.getDeptid());
		} else if (initname.equals("��ǰ���㵥ԪID")) {
			dbmodel.setItemValue(row, colname, u.getEntryid());
		} else if (initname.equals("��ǰ��ɫID")) {
			dbmodel.setItemValue(row, colname, u.getRoleid());
		} else if (initname.equals("��ǰ�ŵ�ID")) {
			dbmodel.setItemValue(row, colname, u.getPlacepointid());
		} else if (initname.equals("��ǰ������ID")) {
			dbmodel.setItemValue(row, colname, u.getStorageid());
		} else if (initname.equals("��ǰ�߼���")) {
			dbmodel.setItemValue(row, colname, String.valueOf(u.getUseday()));
		} else if (initname.equals("��ǰʱ��")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dbmodel.setItemValue(row, colname, df.format(new java.util.Date()));
		}

		return 0;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("���ó�ֵ")) {
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
			} else {
				throw new Exception("caller " + caller + " ������CMdeModel");
			}
		}

		// �����Ի����������
		// �����Ի����������
		SetupDialog dlg = new SetupDialog(dbmodel, expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}

	static class SetupDialog extends RulesetupDialogbase {
		DBTableModel dbmodel = null;
		String expr = null;

		SetupDialog(DBTableModel dbmodel, String expr) {
			super((Frame) null, "�����е�����ѡ��ֵ");
			this.dbmodel = dbmodel;
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			String ss[] = expr.split(":");
			if (ss.length < 2)
				return;
			String colname = ss[0];
			ListModel lm = collist.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i)).equalsIgnoreCase(colname)) {
					collist.setSelectedIndex(i);
					break;
				}
			}
			String initname = ss[1];
			lm = initlist.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i)).equalsIgnoreCase(initname)) {
					initlist.setSelectedIndex(i);
					break;
				}
			}
		}

		JList collist = null;
		JList initlist = null;

		protected void createComponent() {
			Container cp = this.getContentPane();

			// ���������
			collist = createColumnlist();
			cp.add(new JScrollPane(collist), BorderLayout.WEST);

			initlist = new JList(initnames);
			cp.add(new JScrollPane(initlist), BorderLayout.CENTER);

		}

		JList createColumnlist() {
			Vector<String> colnames = new Vector<String>();
			Enumeration<DBColumnDisplayInfo> en = dbmodel
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				colnames.add(en.nextElement().getColname());
			}
			JList list = new JList(colnames);
			return list;
		}

		/**
		 * ���� ����,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			// ����
			if (collist.getSelectedIndex() >= 0) {
				String colname = (String) collist.getSelectedValue();
				sb.append(colname);

				if (initlist.getSelectedIndex() >= 0) {
					String initname = (String) initlist.getSelectedValue();
					sb.append(":");
					sb.append(initname);
				}
			}

			return sb.toString();
		}
	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "credate:��ǰʱ��";
		Initrule.SetupDialog dlg = new Initrule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
