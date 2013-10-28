package com.smart.client.skin;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.smart.platform.gui.control.CCheckBox;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.util.DefaultNPParam;

public class SaveDialog extends CDialogOkcancel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField scheme;
	private CCheckBox isdefault;
	private String defaultvalue;

	public SaveDialog(Frame frame, String s, String defaultName,String isdefault) {
		super(frame, s,true);
		dialogInit();
		defaultvalue = isdefault;
		getContentPane().add(createtop(defaultName), "Center");

		getContentPane().add(super.createOkcancelPane(), "South");
		localCenter();
	}

	private JPanel createtop(String defaultName) {
		JPanel jpanel = new JPanel();
		JLabel jl = new JLabel("界面方案名称:");
		jl.setName("jl");
		jl.setPreferredSize(new Dimension(100, 27));
		// jl.setBounds(20, 30, 100, 50);
		scheme = new JTextField(defaultName);
		scheme.setName("jf");
		scheme.setPreferredSize(new Dimension(100, 27));
		// jf.setBounds(160, 160, 200, 50);
		isdefault = new CCheckBox("是否默认方案");
		isdefault.setName("isdefault");
		isdefault.setValue(defaultvalue);
		jpanel.add(jl);
		jpanel.add(scheme);
		jpanel.add(isdefault);
		return jpanel;

	}

	public String getSchemeName() {
		return scheme == null ? "" : scheme.getText();
	}
	public String isDefault()
	{
		return isdefault==null?"0":isdefault.getValue();
	}
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		super.actionPerformed(arg0);
	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		SaveDialog d = new SaveDialog(null, "test", "默认方案","0");
		d.setSize(320, 300);
		d.pack();
		d.setVisible(true);
	}

}
