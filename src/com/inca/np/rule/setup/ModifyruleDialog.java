package com.inca.np.rule.setup;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inca.np.gui.control.CDialog;
import com.inca.np.rule.define.Rulebase;

/**
 * 修改规则
 * @author Administrator
 *
 */
public class ModifyruleDialog extends CDialog{
	Rulebase rule=null;
	String optype="";
	Object caller=null;
	private JTextArea textExpr;
	boolean ok;
	
	/**
	 * 
	 * @param frame
	 * @param optype ste|mde
	 */
	public ModifyruleDialog(Dialog frame,Object caller,String optype,Rulebase rule){
		super(frame,"修改规则",true);
		this.caller=caller;
		this.optype=optype;
		this.rule=rule;
		initDialog();
		this.localScreenCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("setup")){
			onSetup();
		}else if(e.getActionCommand().equals("ok")){
			onOk();
		}else if(e.getActionCommand().equals("cancel")){
			onCancel();
		}
	}

	void onSetup(){
		try {
			rule.setExpr(textExpr.getText());
			if(rule.setupUI(caller)){
				textExpr.setText(rule.getExpr());
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
		}
		
	}


	protected void initDialog(){
		Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		
		//中间是表达式
		cp.add(new JScrollPane(createMidpane()),BorderLayout.CENTER);
		
		//下面按钮
		cp.add(new JScrollPane(createBottompane()),BorderLayout.SOUTH);
	}
	
	
	public boolean getOk(){
		return ok;
	}
	
	void onOk(){
		rule.setExpr(textExpr.getText());
		ok=true;
		this.dispose();
	}
	void onCancel(){
		ok=false;
		this.dispose();
	}
	
	public Rulebase getRule(){
		return rule;
	}
	
	
	protected JPanel createBottompane(){
		JPanel jp=new JPanel();

		JButton btnsetup=new JButton("设置表达式");
		btnsetup.setActionCommand("setup");
		btnsetup.addActionListener(this);
		jp.add(btnsetup);
		
		JButton btnok=new JButton("确定");
		btnok.setActionCommand("ok");
		btnok.addActionListener(this);
		jp.add(btnok);
		
		JButton btncancel=new JButton("取消");
		btncancel.setActionCommand("cancel");
		btncancel.addActionListener(this);
		jp.add(btncancel);
		return jp;
	}
	
	protected JPanel createMidpane(){
		JPanel jp=new JPanel();
		jp.setLayout(new BorderLayout());

		
		textExpr = new JTextArea(2,40);
		textExpr.setText(rule.getExpr());
		jp.add(textExpr,BorderLayout.CENTER);
		
		
		
		
		return jp;
	}
	
}
