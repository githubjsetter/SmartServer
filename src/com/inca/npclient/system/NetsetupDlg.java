package com.inca.npclient.system;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.inca.np.gui.control.CDialog;


public class NetsetupDlg extends CDialog{
	private JCheckBox cbuseproxy;
	private JTextField textProxyip;
	private JTextField textProxyport;
	HashMap<String, String> config=null;

	public NetsetupDlg(Dialog owner,HashMap<String, String> config){
		super(owner,"ͨѶ����",true);
		this.config=config;
		init();
		bind();
		localCenter();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	void init(){
		Container cp=this.getContentPane();
		GridBagLayout g=new GridBagLayout();
		cp.setLayout(g);
		
		cbuseproxy = new JCheckBox("ʹ��HTTP����");
		cp.add(cbuseproxy, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		
		
		JLabel lb=new JLabel("����IP");
		cp.add(lb, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		
		textProxyip = new JTextField(20);
		cp.add(textProxyip, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		/////port 
		lb=new JLabel("����Port");
		cp.add(lb, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		
		textProxyport = new JTextField(4);
		cp.add(textProxyport, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));

		
		JPanel jp=new JPanel();
		cp.add(jp, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,
						12, 5, 5), 0, 0));
		
		
		jp.setLayout(new FlowLayout());
		JButton btnOk=new JButton("ȷ��");
		btnOk.setActionCommand("ok");
		btnOk.addActionListener(this);
		jp.add(btnOk);
		
		JButton btnCancel=new JButton("ȡ��");
		jp.add(btnCancel);
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(this);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if("ok".equals(cmd)){
			onOk();
		}else if("cancel".equals(cmd)){
			this.dispose();
		}
	}

	void onOk(){
		config.put("useproxy",cbuseproxy.isSelected()?"true":"false");
		config.put("proxyip",textProxyip.getText());
		config.put("proxyport",textProxyport.getText());
		this.dispose();
	}
	
	void bind(){
		String v=config.get("useproxy");
		if("true".equals(v)){
			cbuseproxy.setSelected(true);
		}else{
			cbuseproxy.setSelected(false);
		}
		v=config.get("proxyip");
		if(v==null)v="";
		textProxyip.setText(v);
		v=config.get("proxyport");
		if(v==null)v="";
		textProxyport.setText(v);
	}
	
	public static void main(String[] args) {
		NetsetupDlg dlg=new NetsetupDlg(null,new HashMap<String, String>());
		dlg.pack();
		dlg.setVisible(true);
	}
}
