package com.inca.np.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.inca.np.gui.control.CDialog;


/**
 * 显示状态和进度条
 * @author Administrator
 *
 */
public class CProgressDialog extends CDialog{
	private JProgressBar progbar=null;
	private JLabel lbstatus;
	public boolean ok=false;

	public CProgressDialog(Frame owner,String title){
		super(owner,title,true);
		init();
		this.setPreferredSize(new Dimension(400,100));
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	public CProgressDialog(Dialog owner,String title){
		super(owner,title,true);
		init();
		this.setPreferredSize(new Dimension(400,100));
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	void init(){
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		
		lbstatus = new JLabel("开始下载.....");
		cp.add(lbstatus,BorderLayout.NORTH);

		progbar = new JProgressBar();
		progbar.setMaximum(100);
		progbar.setIndeterminate(false);
		//progbar.setValue(40);
		cp.add(progbar,BorderLayout.CENTER);
		
		JPanel jp=new JPanel();
		JButton btn=new JButton("取消");
		btn.setActionCommand("取消");
		btn.addActionListener(this);
		jp.add(btn);
		cp.add(jp,BorderLayout.SOUTH);
		
	}

	public void setIndeterminate(boolean flag){
		progbar.setIndeterminate(flag);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("取消")){
			ok=false;
			this.dispose();
		}
	}

	public void setStatus(String s){
		lbstatus.setText(s);
	}
	
	public void setProgValue(int v){
		progbar.setValue(v);
		progbar.setString(String.valueOf(v)+"%");
	}
	
}
