package com.smart.platform.gui.ste;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.DBTableModel;

/**
 * 选择上传附件
 * @deprecated 
 * @author Administrator
 * 
 */
public class SelectfilesDialog extends CDialog {
	DBTableModel stedbmodel = null;
	int sterow = 0;
	private JList listFiles;

	public SelectfilesDialog(Dialog dlg, DBTableModel stedbmodel, int sterow) {
		super(dlg, "选择要上传的文件", true);
		this.stedbmodel = stedbmodel;
		this.sterow = sterow;
		initDialog();
		setPreferredSize(new Dimension(640,300));
		localCenter();
		setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		bindData();
	}
	
	
	
	@Override
	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		if(b){
			selectFile();
		}
		super.setVisible(b);
	}



	void bindData(){
		if(stedbmodel!=null){
			RecordTrunk rec=stedbmodel.getRecordThunk(sterow);
			Enumeration<File> en=rec.getWantuploadfiles().elements();
			while(en.hasMoreElements()){
				listmodel.addElement(en.nextElement());
			}
		}
	}

	void initDialog() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		listmodel = new DefaultListModel();
		listFiles = new JList(listmodel);
		cp.add(listFiles);

		cp.add(new JScrollPane(listFiles), BorderLayout.CENTER);

		JPanel jpbtm=new JPanel();
		cp.add(jpbtm,BorderLayout.SOUTH);
		JButton btn=new JButton("增加文件");
		btn.setActionCommand("增加文件");
		btn.addActionListener(this);
		jpbtm.add(btn);
		
		btn=new JButton("取消文件");
		btn.setActionCommand("取消文件");
		btn.addActionListener(this);
		jpbtm.add(btn);		

		btn=new JButton("确定");
		btn.setActionCommand("确定");
		btn.addActionListener(this);
		jpbtm.add(btn);		
		
		btn=new JButton("取消");
		btn.setActionCommand("取消");
		btn.addActionListener(this);
		jpbtm.add(btn);		
	}
	
	void selectFile(){
		jfc.setMultiSelectionEnabled(true);
		int ret=jfc.showOpenDialog(this);
		if(ret!=JFileChooser.APPROVE_OPTION){
			return;
		}
		File[] selectedfs=jfc.getSelectedFiles();
		for(int i=0;selectedfs!=null && i<selectedfs.length;i++){
			listmodel.addElement(selectedfs[i]);
		}
	}
	
	boolean ok=false;
	JFileChooser jfc=new JFileChooser();
	private DefaultListModel listmodel;
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("增加文件")){
			selectFile();
		}else if(e.getActionCommand().equals("确定")){
			Vector<File> ftable=new Vector<File>(); 
			for(int i=0;i<listmodel.size();i++){
				ftable.add((File)listmodel.elementAt(i));
			}
			stedbmodel.getRecordThunk(sterow).setWantuploadfile(ftable);
			ok=true;
			dispose();
		}else if(e.getActionCommand().equals("取消")){
			ok=false;
			dispose();
		}else if(e.getActionCommand().equals("取消文件")){
			int si=listFiles.getSelectedIndex();
			if(si>=0){
				listmodel.remove(si);
			}
		}
	}
	public boolean getOk(){
		return ok;
	}

	public static void main(String[] argv){
		Properties prop=System.getProperties();
		Enumeration en=prop.keys();
		while(en.hasMoreElements()){
			String s=(String)en.nextElement();
			if(s.toLowerCase().indexOf("home")>=0){
				System.out.println(s);
			}
		}
		System.out.println(prop.getProperty("user.home"));
		
/*		SelectfilesDialog dlg=new SelectfilesDialog(null,null,0);
		dlg.pack();
		dlg.setVisible(true);
*/	}
}
