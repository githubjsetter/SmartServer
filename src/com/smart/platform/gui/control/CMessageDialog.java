package com.smart.platform.gui.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import sun.swing.DefaultLookup;

/**
 * 消息对话框
 * @author user
 *
 */
public class CMessageDialog extends CDialog{
	public static int INFO_MESSAGE=1;
	public static int WARN_MESSAGE=2;
	public static int ERROR_MESSAGE=3;
	
	int dlgtype=INFO_MESSAGE;
	String message="";
	JTextArea textMsg=null;
	public static void infoMessage(Frame frame,String title,String msg){
		CMessageDialog dlg=new CMessageDialog(frame,title,msg,INFO_MESSAGE);
		dlg.pack();
		dlg.setVisible(true);
	}


	public static void warnMessage(Frame frame,String title,String msg){
		CMessageDialog dlg=new CMessageDialog(frame,title,msg,WARN_MESSAGE);
		dlg.pack();
		dlg.setVisible(true);
	}


	public static void errorMessage(Frame frame,String title,String msg){
		CMessageDialog dlg=new CMessageDialog(frame,title,msg,ERROR_MESSAGE);
		dlg.pack();
		dlg.setVisible(true);
	}

	public static void infoMessage(Dialog owndlg,String title,String msg){
		CMessageDialog dlg=new CMessageDialog(owndlg,title,msg,INFO_MESSAGE);
		dlg.pack();
		dlg.setVisible(true);
	}


	public static void warnMessage(Dialog owndlg,String title,String msg){
		CMessageDialog dlg=new CMessageDialog(owndlg,title,msg,WARN_MESSAGE);
		dlg.pack();
		dlg.setVisible(true);
	}


	public static void errorMessage(Dialog owndlg,String title,String msg){
		CMessageDialog dlg=new CMessageDialog(owndlg,title,msg,ERROR_MESSAGE);
		dlg.pack();
		dlg.setVisible(true);
	}

	private CMessageDialog(Frame frame,String title,String msg,int dlgtype){
		super(frame,title,true);
		this.dlgtype=dlgtype;
		this.message=msg;
		init();
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		

	}

	private CMessageDialog(Dialog ownerdlg,String title,String msg,int dlgtype){
		super(ownerdlg,title,true);
		this.dlgtype=dlgtype;
		this.message=msg;
		init();
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}
	
	JPanel createToolbar(){
		JPanel jp=new JPanel();
		JButton btn;
		btn=new JButton("关闭");
		btn.setActionCommand("close");
		btn.addActionListener(this);
		setHotkey(btn);

		jp.add(btn);
		
		btn=new JButton("复制到剪裁板");
		btn.setActionCommand("copy");
		btn.addActionListener(this);
		jp.add(btn);
		
		return jp;
	}
	
	Color lightgray=new Color(236,236,236);
	
	void init(){
		JOptionPane tmpop=null;
		String propname="OptionPane.informationIcon";
        setUndecorated(true);
        if(dlgtype==INFO_MESSAGE){
        	getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
        	tmpop=new JOptionPane("tmp",JOptionPane.INFORMATION_MESSAGE);
        	propname="OptionPane.informationIcon";
        }else if(dlgtype==WARN_MESSAGE){
        	getRootPane().setWindowDecorationStyle(JRootPane.WARNING_DIALOG);
        	tmpop=new JOptionPane("tmp",JOptionPane.WARNING_MESSAGE);
        	propname="OptionPane.warningIcon";
        }else if(dlgtype==ERROR_MESSAGE){
        	getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
        	tmpop=new JOptionPane("tmp",JOptionPane.ERROR_MESSAGE);
        	propname="OptionPane.errorIcon";
        }

		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		setHotkey((JComponent)cp);
		
		Icon icon=(Icon)DefaultLookup.get(tmpop, null, propname);
		JLabel lb=new JLabel(icon);
        cp.add(lb,BorderLayout.BEFORE_LINE_BEGINS);
        
        
		
		textMsg=new JTextArea();
		textMsg.setText(message);
		textMsg.setBackground(lightgray);
		textMsg.setBorder(BorderFactory.createEtchedBorder());
		
		setHotkey(textMsg);
		JScrollPane jsp=new JScrollPane(textMsg);
		jsp.setPreferredSize(new Dimension(500,80));
		cp.add(jsp,BorderLayout.CENTER);
		textMsg.setEditable(false);
		textMsg.setLineWrap(true);
		
		cp.add(createToolbar(),BorderLayout.SOUTH);
		
	}
	
	void setHotkey(JComponent jcp){
		KeyStroke esckey=KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0,false);
		KeyStroke enterkey=KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0,false);
		jcp.getInputMap().put(enterkey, "close");
		jcp.getInputMap().put(esckey, "close");
		jcp.getActionMap().put("close", new Myaction("close"));
	}
	
	void onClose(){
		dispose();
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if("close".equals(e.getActionCommand())){
			onClose();
		}else if("copy".equals(e.getActionCommand())){
			Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection  contents=new StringSelection(message);
			clipboard.setContents(contents, contents);
			onClose();
		}
	}



	class Myaction extends AbstractAction{

		public Myaction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("close")){
				onClose();
			}
		}
		
	}
	
	public static void main(String[] args) {
		CMessageDialog.infoMessage((Frame)null,"提示","保存成功");
		CMessageDialog.warnMessage((Frame)null,"提示","不能这样");
		CMessageDialog.errorMessage((Frame)null,"错误","错误的sql");
	}
}
