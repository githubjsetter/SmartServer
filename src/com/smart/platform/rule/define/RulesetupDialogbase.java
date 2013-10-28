package com.smart.platform.rule.define;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.smart.platform.gui.control.CButton;
import com.smart.platform.gui.control.CDialog;

public class RulesetupDialogbase extends CDialog{
	RulesetupDialogbase(Frame owner,String title){
		super((Frame)owner,title,true);
		initDialog();
		setHotkey();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	private void initDialog(){
		Container cp=this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(createbottomPanel(),BorderLayout.SOUTH);
		
	}
	
	
	protected JPanel createbottomPanel(){
		JPanel jp=new JPanel();
		jp.setLayout(new BorderLayout());
		
		
		JPanel tbpanel=new JPanel();
		jp.add(tbpanel,BorderLayout.CENTER);
		
		CButton btnok=new CButton("确定");
		tbpanel.add(btnok);
		btnok.setActionCommand("ok");
		btnok.addActionListener(this);
		
		CButton btncancel=new CButton("取消");
		tbpanel.add(btncancel);
		btncancel.setActionCommand("cancel");
		btncancel.addActionListener(this);
		
		
		textHelp = new JTextArea(4,40);
		textHelp.setEditable(false);
		textHelp.setLineWrap(true);
		jp.add(new JScrollPane(textHelp),BorderLayout.SOUTH);
		
		return jp;
	}

	protected void setHelp(String s){
		textHelp.setText(s);
	}
	
	protected void setHotkey(){
		JComponent jcp=(JComponent)this.getContentPane();
		InputMap im=jcp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "ok");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "cancel");
		jcp.getActionMap().put("ok",new DlgAction("ok"));
		jcp.getActionMap().put("cancel",new DlgAction("cancel"));
	}

	protected  boolean ok=false;
	private JTextArea textHelp;
	public boolean getOk(){
		return ok;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ok")){
			onOk();
		}else if(e.getActionCommand().equals("cancel")){
			onCancel();
		}
	}
	
	class DlgAction extends AbstractAction{
		DlgAction(String cmd){
			super(cmd);
			putValue(AbstractAction.ACTION_COMMAND_KEY,cmd);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("ok")){
				onOk();
			}else if(e.getActionCommand().equals("cancel")){
				onCancel();
			}
		}
	}
	
	protected void onOk(){
		ok=true;
		dispose();
	}
	
	protected void onCancel(){
		ok=false;
		dispose();
	}
	
}
