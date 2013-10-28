package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.control.CNumberTextField;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;

/**
 * 设置编辑框大小
 * expr:=列名:宽:高
 * @author Administrator
 *
 */
public class CompsizeRule extends Rulebase {
	static String[] treatableruletypes;
	static {
		treatableruletypes = new String[] { "设置编辑控件大小", "细单设置编辑控件大小" };
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


	@Override
	public int process(Object caller) throws Exception {
		if (expr == null)
			return 0;

		DBTableModel dbmodel = null;
		if (getRuletype().equals("设置编辑控件大小")) {
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

		// 解释表达式
		String ss[] = expr.split(":");
		if (ss.length < 3)
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
			return 0;
		}
		
		int w=100;
		int h=27;
		
		try{
			w=(int)Double.parseDouble(ss[1]);
		}catch(Exception e){}

		try{
			h=(int)Double.parseDouble(ss[2]);
		}catch(Exception e){}
		
		thisinfo.getEditComponent().setPreferredSize(new Dimension(w,h));
		thisinfo.getEditComponent().setSize(new Dimension(w,h));

		return 0;
	}

	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		if (getRuletype().equals("设置编辑控件大小")) {
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
			} else {
				throw new Exception("caller " + caller + " 必须是CMdeModel");
			}
		}

		// 弹出对话框进行设置
		// 弹出对话框进行设置
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
			super((Frame) null, "编辑控件大小");
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
			if (ss.length < 3)
				return;
			String colname = ss[0];
			ListModel lm = collist.getModel();
			for (int i = 0; i < lm.getSize(); i++) {
				if (((String) lm.getElementAt(i)).equalsIgnoreCase(colname)) {
					collist.setSelectedIndex(i);
					break;
				}
			}
			textWidth.setText(ss[1]);
			textHeight.setText(ss[2]);
		}

		JList collist = null;
		CNumberTextField textWidth=new CNumberTextField(0);
		CNumberTextField textHeight=new CNumberTextField(0);

		protected void createComponent() {
			Container cp = this.getContentPane();

			// 左边是列名
			collist = createColumnlist();
			cp.add(new JScrollPane(collist), BorderLayout.WEST);
			collist.addListSelectionListener(new ListListen());

			
			JPanel jp=new JPanel();
			cp.add(jp, BorderLayout.CENTER);
			GridBagLayout g=new GridBagLayout();
			jp.setLayout(g);
			GridBagConstraints c=new GridBagConstraints();
			
			JLabel lb=new JLabel("控件宽");
			c.gridwidth=GridBagConstraints.RELATIVE;
			g.setConstraints(lb,c);
			jp.add(lb);
			c.gridwidth=GridBagConstraints.REMAINDER;
			g.setConstraints(textWidth,c);
			jp.add(textWidth);
			
			
			lb=new JLabel("控件高");
			c.gridwidth=GridBagConstraints.RELATIVE;
			g.setConstraints(lb,c);
			jp.add(lb);
			c.gridwidth=GridBagConstraints.REMAINDER;
			g.setConstraints(textHeight,c);
			jp.add(textHeight);

			Dimension size=new Dimension(100,27);
			textWidth.setPreferredSize(size);
			textHeight.setPreferredSize(size);
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
		 * 返回 列名,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			// 列名
			if (collist.getSelectedIndex() >= 0) {
				String colname = (String) collist.getSelectedValue();
				sb.append(colname);
				sb.append(":");
				sb.append(textWidth.getText());
				sb.append(":");
				sb.append(textHeight.getText());
			}

			return sb.toString();
		}
		
		class ListListen implements ListSelectionListener{

			public void valueChanged(ListSelectionEvent e) {
				String colname=(String)collist.getSelectedValue();
				DBColumnDisplayInfo col=dbmodel.getColumninfo(colname);
				Dimension size=col.getEditComponent().getPreferredSize();
				textWidth.setText(String.valueOf(size.getWidth()));
				textHeight.setText(String.valueOf(size.getHeight()));
			}
			
		}
	}

	public static void main(String[] argv) {
		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "credate:100:27";
		CompsizeRule.SetupDialog dlg = new CompsizeRule.SetupDialog(ste
				.getDBtableModel(), expr);
		dlg.pack();
		dlg.setVisible(true);
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
