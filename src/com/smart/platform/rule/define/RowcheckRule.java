package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Category;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CFormlineBreak;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * �����м��.  ���ʽ:=�����ʽ:������ʾ˵��
 * @author user
 *
 */
public class RowcheckRule   extends Rulebase {
	Category logger = Category.getInstance(ExprRule.class);
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "�б��ʽ���", "ϸ���б��ʽ���" };
	}

	public static boolean canProcessruletype(String ruletype) {
		for (int i = 0; treatableruletypes != null
				&& i < treatableruletypes.length; i++) {
			if (treatableruletypes[i].equals(ruletype))
				return true;
		}
		return false;
	}

	public static String[] getRuleypes() {
		return treatableruletypes;
	}

	/**
	 * @return �մ���ʾû����ʾ,��������ַ�����ʾ����
	 */
	@Override
	public String processRowcheck(Object caller, int row)
			throws Exception {
		if (expr == null)
			return "";

		ExprCalcer c = new ExprCalcer(caller);
		// ���ͱ��ʽ
		String ss[] = expr.split(":");
		if (ss.length < 2)
			return "";
		String expr = ss[0];
		String warnmsg = ss[1];

		String v = null;
		try {
			v = c.calc(row, expr);
			if(v==null || v.length() == 0){
				return "";
			}
			
			if(v.equals("1")){
				//���ʽ����,���ؿ�
				return "";
			}
			
			//������ʾ��Ϣ
			return warnmsg;
		} catch (Exception e) {
			logger.error("ERROR", e);
			return "";
		}
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		// �����Ի����������
		DBTableModel dbmodel = null;
		
		if("�б��ʽ���".equals(ruletype)){
			if (caller instanceof CSteModel) {
				dbmodel = ((CSteModel) caller).getDBtableModel();
			} else if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getMasterModel().getDBtableModel();
			}
		}else{
			if (caller instanceof CMdeModel) {
				dbmodel = ((CMdeModel) caller).getDetailModel().getDBtableModel();
			}else if (caller instanceof CDetailModel){
				dbmodel = ((CDetailModel) caller).getDBtableModel();
			}
		}
		SetupDialog dlg = new SetupDialog(dbmodel, expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}

	static class SetupDialog extends RulesetupDialogbase {
		String expr = null;
		DBTableModel dbmodel = null;

		SetupDialog(DBTableModel dbmodel, String expr) {
			super((Frame) null, "�м�鲼�����ʽ");
			this.expr = expr;
			this.dbmodel = dbmodel;
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
			String expr = ss[0];
			String msg = ss[1];

			// ����ֵ
			textExpr.setText(expr);
			
			textMsg.setText(msg);
		}

		JTextArea textExpr = new JTextArea(2, 40);
		JTextArea textMsg = new JTextArea(2, 40);

		protected void createComponent() {
			Container cp = this.getContentPane();
			
			JPanel jp=new JPanel();
			cp.add(jp,BorderLayout.CENTER);
			
			
			CFormlayout layout=new CFormlayout(2,2);
			jp.setLayout(layout);

			JList listcol=createColumnlist();
			listcol.addListSelectionListener(new ListHandler());

			JLabel lb=new JLabel("��ѡ�����");
			jp.add(lb);
			layout.addLayoutComponent(lb,null);
			JScrollPane jsp=new JScrollPane(listcol);
			jp.add(jsp);
			layout.addLayoutComponent(jsp, new CFormlineBreak());

			 lb=new JLabel("�����ʽ");
			jp.add(lb);
			layout.addLayoutComponent(lb,null);
			
			//�в��Ǳ��ʽ
			jp.add(textExpr,new CFormlineBreak());
			layout.addLayoutComponent(textExpr, new CFormlineBreak());
			
			//�²�����ʾ
			lb=new JLabel("�� �� ��ʾ");
			jp.add(lb);
			layout.addLayoutComponent(lb,null);
			jp.add(textMsg, new CFormlineBreak());
			layout.addLayoutComponent(textExpr, new CFormlineBreak());
			
			setHelp("���벼�����ʽ���,����goodsqty>0. ������ʾΪ��������.����\"�����������0\"");

			

		}
		
		class ListHandler implements ListSelectionListener{

			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting())return;
				JList list=(JList)e.getSource();
				if(list.getSelectedIndex()>=0){
					String s=(String) list.getSelectedValue();
					textExpr.replaceSelection(s);
				}
			}
			
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

		@Override
		protected void onOk() {
			if(textExpr.getText().trim().length()==0){
				errorMessage("��ʾ","���������鲼�����ʽ");
				return;
			}
			if(textMsg.getText().trim().length()==0){
				errorMessage("��ʾ","����������ʾ���ʽ");
				return;
			}
			super.onOk();
		}

		/**
		 * ���� ����,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			sb.append(textExpr.getText().trim());
			sb.append(":");
			sb.append(textMsg.getText().trim());
			return sb.toString();
		}

	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		ste.getRootpanel();
		String expr = "1=2:��������";
		RowcheckRule.SetupDialog dlg = new RowcheckRule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
