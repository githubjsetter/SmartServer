package com.smart.adminclient.installjar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.smart.server.install.Installinfo;

/**
 * 模块基本信息
 * @author Administrator
 *
 */
public class Installinfopane extends JPanel{
	private JTextField textModulename;
	private JTextField textProdname;
	private JTextField textModuleengname;
	private JTextField textVersion;
	
	Installinfo installinfo=null;

	public Installinfopane(Installinfo installinfo){
		this.installinfo=installinfo;
		GridBagLayout g=new GridBagLayout();
		setLayout(g);
		Insets insets=new Insets(2,2,2,2);
		JLabel lb;
		GridBagConstraints c;
		int row=0;
		lb=new JLabel("产品名");
		c=new GridBagConstraints(0,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(lb, c);
		add(lb);
		
		textProdname = new JTextField(30);
		c=new GridBagConstraints(1,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(textProdname, c);
		add(textProdname);
		
		row++;
		lb=new JLabel("模块名");
		c=new GridBagConstraints(0,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(lb, c);
		add(lb);
		
		textModulename = new JTextField(30);
		c=new GridBagConstraints(1,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(textModulename, c);
		add(textModulename);
		
		row++;
		lb=new JLabel("英文名");
		c=new GridBagConstraints(0,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(lb, c);
		add(lb);
		
		textModuleengname = new JTextField(30);
		c=new GridBagConstraints(1,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(textModuleengname, c);
		add(textModuleengname);
		
		row++;
		lb=new JLabel("版本");
		c=new GridBagConstraints(0,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(lb, c);
		add(lb);
		
		textVersion = new JTextField(30);
		c=new GridBagConstraints(1,row,1,1,0,0,GridBagConstraints.WEST,0,insets,0,0);
		g.setConstraints(textVersion, c);
		add(textVersion);
		
		bind();
	}
	
	void bind(){
		textModulename.setText(installinfo.getModulename());
		textProdname.setText(installinfo.getProdname());
		textModuleengname.setText(installinfo.getModuleengname());
		textVersion.setText(installinfo.getVersion());
	}
	
	public void rebind(){
		installinfo.setModulename(textModulename.getText());
		installinfo.setProdname(textProdname.getText());
		installinfo.setModuleengname(textModuleengname.getText());
		installinfo.setVersion(textVersion.getText());
	}
}
