package com.inca.np.gui.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

public class SDlg extends JDialog implements ActionListener{

	private JButton btn1;

	public SDlg() {
		super((Frame)null,true);
		
		Container cp=getContentPane();
		cp.setLayout(new DirectLayout());
		btn1 = new JButton("click");
		btn1.setName("mycomp1");
		cp.add(btn1);
		
		btn1.addActionListener(this);
	}

	public static void main(String[] args) {
		SDlg dlg=new SDlg();
		dlg.pack();
		dlg.setVisible(true);
	}

	
	class DirectLayout implements LayoutManager{

		public void addLayoutComponent(String name, Component comp) {
			// TODO Auto-generated method stub
			
		}

		public void layoutContainer(Container parent) {
			btn1.setBounds(30, 30, 180, 27);
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(320,240);
		}

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(320,240);
		}

		public void removeLayoutComponent(Component comp) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Container cp=getContentPane();
		for(int i=0;i<cp.getComponentCount();i++){
			Component comp=cp.getComponent(i);
			System.out.println(comp.getName());
		}
	}
}
