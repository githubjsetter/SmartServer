package com.smart.adminclient.installjar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.ste.COpframe;
import com.smart.server.install.Installinfo;
import com.smart.server.install.InstallinfoReader;
import com.smart.server.install.InstallinfoWriter;

public class InstallinfoEditor extends COpframe{
	File installinfofile=null;
	public InstallinfoEditor(File installinfofile){
		super("�༭��װ��Ϣ�ļ�");
		this.installinfofile=installinfofile;
		init();
	}

	Category logger=Category.getInstance(InstallinfoEditor.class);
	private Installinfopane infopane;
	private OpeditPane oppane;
	private ServiceeditPane servicepane;
	private HoveditPane hovpane;
	private Installinfo installinfo;
	
	public Installinfo getInstallinfo() {
		return installinfo;
	}


	void init(){
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		
		JTabbedPane tabpane=new JTabbedPane();
		cp.add(tabpane,BorderLayout.CENTER);
		
		InstallinfoReader iireader=new InstallinfoReader();
		installinfo = null;
		try{
			installinfo = iireader.read(installinfofile);
		}catch(Exception e){
			logger.error("error",e);
			installinfo=new Installinfo();
		}
		
		infopane = new Installinfopane(installinfo);
		tabpane.add(infopane,"ģ�������Ϣ");
		
		oppane = new OpeditPane(this,installinfo);
		tabpane.add(oppane,"�����嵥");
		
		servicepane = new ServiceeditPane(this,installinfo);
		tabpane.add(servicepane,"�����嵥");
		
		hovpane = new HoveditPane(this,installinfo);
		tabpane.add(hovpane,"HOV�嵥");
		
		JPanel bottompane=new JPanel(); 
		cp.add(bottompane,BorderLayout.SOUTH);
		JButton btn;
		btn=new JButton("ȷ��");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		bottompane.add(btn);
		
		btn=new JButton("ȡ��");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		bottompane.add(btn);
	}
	

	void onOk(){
		infopane.rebind();
		oppane.rebind();
		servicepane.rebind();
		hovpane.rebind();
		InstallinfoWriter iw=new InstallinfoWriter();
		try {
			iw.write(installinfo, installinfofile);
			this.dispose();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "�������"+e.getMessage());
		}
	}
	
	void onCancel(){
		dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd=e.getActionCommand();
		if(cmd.equals("ok")){
			onOk();
		}else if (cmd.equals("cancel")){
			onCancel();
		}
	}



	public static void main(String[] args) {
		File installinfofile=new File("testdata/sysmgr/installinfo");
		InstallinfoEditor dlg=new InstallinfoEditor(installinfofile);
		dlg.pack();
		dlg.setVisible(true);
	}
}
