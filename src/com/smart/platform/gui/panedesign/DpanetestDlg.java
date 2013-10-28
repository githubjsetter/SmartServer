package com.smart.platform.gui.panedesign;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CDialogUIManager;
import com.smart.platform.gui.control.CTextArea;

public class DpanetestDlg extends CDialogOkcancel{
	private DPanel dpane;

	public DpanetestDlg(){
		super((Frame)null,"≤‚ ‘dpane",true);
		init();
	}
	
	class DpanetestPane extends DPanel{
		
	}
	
	void init(){
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		JPanel toolbar=createToolbar();
		cp.add(toolbar,BorderLayout.NORTH);
		JPanel centerp=new JPanel();
		JLabel lb=new JLabel("±ÍÃ‚1");
		lb.setName("lb1");
		centerp.add(lb);
		cp.add(centerp,BorderLayout.CENTER);
		
		cp.add(createOkcancelPane(),BorderLayout.SOUTH);
	}
	
	JPanel createToolbar(){
		JPanel jp=new JPanel();
		JButton btn=new JButton("save");
		btn.setActionCommand("save");
		//btn.addActionListener(this);
		btn.setName("btnSave");
		jp.add(btn);
		return jp;
	}
	
	
	void doSave(){
		DpanestoreHelper.savePanel(dpane);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("save")){
			doSave();
		}else {
			super.actionPerformed(e);
		}
	}
	
	

	public static void main(String[] args) {
		DpanetestDlg dlg=new DpanetestDlg();
		dlg.pack();
		dlg.setVisible(true);
	}
}
