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
 * 进行行检查.  表达式:=检查表达式:中文提示说明
 * @author user
 *
 */
public class RowcheckRule   extends Rulebase {
	Category logger = Category.getInstance(ExprRule.class);
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "行表达式检查", "细单行表达式检查" };
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
	 * @return 空串表示没有提示,如果返回字符串表示有误
	 */
	@Override
	public String processRowcheck(Object caller, int row)
			throws Exception {
		if (expr == null)
			return "";

		ExprCalcer c = new ExprCalcer(caller);
		// 解释表达式
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
				//表达式成立,返回空
				return "";
			}
			
			//返回提示信息
			return warnmsg;
		} catch (Exception e) {
			logger.error("ERROR", e);
			return "";
		}
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		// 弹出对话框进行设置
		DBTableModel dbmodel = null;
		
		if("行表达式检查".equals(ruletype)){
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
			super((Frame) null, "行检查布尔表达式");
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

			// 设置值
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

			JLabel lb=new JLabel("可选择的列");
			jp.add(lb);
			layout.addLayoutComponent(lb,null);
			JScrollPane jsp=new JScrollPane(listcol);
			jp.add(jsp);
			layout.addLayoutComponent(jsp, new CFormlineBreak());

			 lb=new JLabel("检查表达式");
			jp.add(lb);
			layout.addLayoutComponent(lb,null);
			
			//中部是表达式
			jp.add(textExpr,new CFormlineBreak());
			layout.addLayoutComponent(textExpr, new CFormlineBreak());
			
			//下部是提示
			lb=new JLabel("中 文 提示");
			jp.add(lb);
			layout.addLayoutComponent(lb,null);
			jp.add(textMsg, new CFormlineBreak());
			layout.addLayoutComponent(textExpr, new CFormlineBreak());
			
			setHelp("输入布尔表达式检查,比如goodsqty>0. 中文提示为检查的内容.比如\"数量必须大于0\"");

			

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
				errorMessage("提示","必须输入检查布尔表达式");
				return;
			}
			if(textMsg.getText().trim().length()==0){
				errorMessage("提示","必须输入提示表达式");
				return;
			}
			super.onOk();
		}

		/**
		 * 返回 列名,[id,value]
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
		String expr = "1=2:必须输入";
		RowcheckRule.SetupDialog dlg = new RowcheckRule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
