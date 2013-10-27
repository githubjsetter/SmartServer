package com.inca.np.rule.define;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.inca.np.demo.ste.Pub_goods_ste;
import com.inca.np.gui.control.CStetoolbar;
import com.inca.np.gui.control.CTextField;
import com.inca.np.gui.control.CToolbar;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.SteActionListener;
import com.inca.np.util.HotkeyUtils;

/**
 * 工具条加按钮 expr:=按钮title : tips : action
 * 
 * @author Administrator
 * 
 */
public class AddbuttonRule extends Rulebase {
	static protected String[] treatableruletypes = null;
	static {
		treatableruletypes = new String[] { "工具条加按钮" };
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
	public int process(Object caller) throws Exception {
		if (expr == null || expr.length() == 0)
			return 0;

		CStetoolbar tb = null;
		JFrame frm=null;
		ActionListener al=null;
		if (getRuletype().equals("工具条加按钮")) {
			if(caller instanceof CMasterModel){
				JPanel rootpane = ((CSteModel) caller).getRootpanel();
				tb = searchToolbar(rootpane);
				frm = ((CSteModel) caller).getParentFrame();
				al=((MdeFrame)frm).getCreatedMdemodel();
			}else if (caller instanceof CSteModel) {
				JPanel rootpane = ((CSteModel) caller).getRootpanel();
				tb = searchToolbar(rootpane);
				frm = ((CSteModel) caller).getParentFrame();
				al=((CSteModel) caller);
			} else if (caller instanceof CMdeModel) {
				JPanel rootpane = ((CMdeModel) caller).getMasterModel()
						.getRootpanel();
				tb = searchToolbar(rootpane);
				frm = ((CMdeModel) caller).getParentFrame();
				al=((CMdeModel) caller);
			} else {
				throw new Exception("caller " + caller
						+ " 一定是CSteModel或CMdeModel");
			}
		}
		if (tb == null) {
			return 0;
		}

		// 解释表达式
		String ss[] = expr.split(":");
		if (ss.length < 3)
			return 0;
		String title = ss[0];
		String tips = ss[1];
		String action = ss[2];

		addButton(tb, title, tips, action);
		
		//加热键
		if(ss.length>=5){
			String keyname=ss[3];
			int mask=Integer.parseInt(ss[4]);
			if(!keyname.equals("无")){
				int keycode=HotkeyUtils.getKeycode(keyname);
				if(keycode>0){
					KeyStroke vk=KeyStroke.getKeyStroke(keycode,mask,false);
					JComponent jcp=(JComponent)frm.getContentPane();
					InputMap im=jcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
					im.put(vk, action);
					jcp.getActionMap().put(action,new SteActionListener(action,al));
				}
			}
		}

		return 0;
	}


	@Override
	public boolean setupUI(Object caller) throws Exception {
		DBTableModel dbmodel = null;
		// 弹出对话框进行设置
		SetupDialog dlg = new SetupDialog(expr);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.getOk())
			return false;
		expr = dlg.getExpr();

		return true;
	}

	static class SetupDialog extends RulesetupDialogbase {
		String expr = null;
		CTextField textTitle = new CTextField(40);
		CTextField textTips = new CTextField(40);
		CTextField textAction = new CTextField(40);
		JComboBox cbKeyname = null;
		JCheckBox cbCtrl = new JCheckBox("ctrl");
		JCheckBox cbAlt = new JCheckBox("alt");
		JCheckBox cbShift = new JCheckBox("shift");

		SetupDialog(String expr) {
			super((Frame) null, "设置新增按钮");
			this.expr = expr;
			createComponent();
			bindValue();
			localCenter();
			super.setHelp("设置按钮标题，按钮帮助信息，按钮命令和热键");
		}

		protected void bindValue() {
			if (expr == null)
				expr = "";
			String ss[] = expr.split(":");
			if (ss.length < 3)
				return;
			String title = ss[0];
			String tips = ss[1];
			String action = ss[2];
			textTitle.setText(title);
			textTips.setText(tips);
			textAction.setText(action);
			if(ss.length>=4){
				cbKeyname.setSelectedItem(ss[3]);
			}
			if(ss.length>=5){
				int mask=Integer.parseInt(ss[4]);
				if((mask & Event.CTRL_MASK) !=0){
					cbCtrl.setSelected(true);
				}
				if((mask & Event.ALT_MASK) !=0){
					cbAlt.setSelected(true);
				}
				if((mask & Event.SHIFT_MASK) !=0){
					cbShift.setSelected(true);
				}
			}

		}

		protected void createComponent() {
			Container cp = this.getContentPane();
			JPanel jp = new JPanel();
			cp.add(jp, BorderLayout.CENTER);

			GridBagLayout g = new GridBagLayout();
			jp.setLayout(g);
			GridBagConstraints c = new GridBagConstraints();
			c.gridwidth = GridBagConstraints.RELATIVE;
			JLabel lb = new JLabel("按钮标题");
			g.setConstraints(lb, c);
			jp.add(lb);

			c.gridwidth = GridBagConstraints.REMAINDER;
			g.setConstraints(textTitle, c);
			jp.add(textTitle);

			// //
			lb = new JLabel("按钮TIPS");
			c.gridwidth = GridBagConstraints.RELATIVE;
			g.setConstraints(lb, c);
			jp.add(lb);

			c.gridwidth = GridBagConstraints.REMAINDER;
			g.setConstraints(textTips, c);
			jp.add(textTips);

			// //
			lb = new JLabel("ACTION");
			c.gridwidth = GridBagConstraints.RELATIVE;
			g.setConstraints(lb, c);
			jp.add(lb);

			c.gridwidth = GridBagConstraints.REMAINDER;
			g.setConstraints(textAction, c);
			jp.add(textAction);

			lb = new JLabel("热键");
			c.gridwidth = GridBagConstraints.RELATIVE;
			g.setConstraints(lb, c);
			jp.add(lb);

			JPanel hotpanel = new JPanel();
			c.gridwidth = GridBagConstraints.RELATIVE;
			g.setConstraints(hotpanel, c);
			jp.add(hotpanel);

			cbKeyname = new JComboBox(HotkeyUtils.getKeynames());
			hotpanel.add(cbKeyname);

			hotpanel.add(cbCtrl);

			hotpanel.add(cbAlt);

			hotpanel.add(cbShift);
		}

		/**
		 * 返回 列名,[id,value]
		 * 
		 * @return
		 */
		public String getExpr() {
			StringBuffer sb = new StringBuffer();
			sb.append(textTitle.getText());
			sb.append(":");
			sb.append(textTips.getText());
			sb.append(":");
			sb.append(textAction.getText());
			sb.append(":");
			sb.append(cbKeyname.getSelectedItem());
			int mask=0;
			if(cbCtrl.isSelected()){
				mask|=Event.CTRL_MASK;
			}
			if(cbAlt.isSelected()){
				mask|=Event.ALT_MASK;
			}
			if(cbShift.isSelected()){
				mask|=Event.SHIFT_MASK;
			}
			sb.append(":");
			sb.append(String.valueOf(mask));
			
			
			return sb.toString();
		}

		@Override
		protected void onOk() {
			super.onOk();
		}

	}

	public static void main(String[] argv) {

		Pub_goods_ste ste = new Pub_goods_ste(null);
		String expr = "新按钮:测试加按钮:NEWBUTTON::2";
		AddbuttonRule.SetupDialog dlg = new AddbuttonRule.SetupDialog(expr);
		dlg.pack();
		dlg.setVisible(true);
		System.out.println(dlg.getExpr());
		if (dlg.getOk()) {
			System.out.println(dlg.getExpr());
		}
	}
}
