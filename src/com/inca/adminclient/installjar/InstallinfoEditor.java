package com.inca.adminclient.installjar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Category;

import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.ste.COpframe;
import com.inca.npserver.install.Installinfo;
import com.inca.npserver.install.InstallinfoReader;
import com.inca.npserver.install.InstallinfoWriter;

public class InstallinfoEditor extends COpframe{
	File installinfofile=null;
	public InstallinfoEditor(File installinfofile){
		super("编辑安装信息文件");
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
		tabpane.add(infopane,"模块基本信息");
		
		oppane = new OpeditPane(this,installinfo);
		tabpane.add(oppane,"功能清单");
		
		servicepane = new ServiceeditPane(this,installinfo);
		tabpane.add(servicepane,"服务清单");
		
		hovpane = new HoveditPane(this,installinfo);
		tabpane.add(hovpane,"HOV清单");
		
		JPanel bottompane=new JPanel(); 
		cp.add(bottompane,BorderLayout.SOUTH);
		JButton btn;
		btn=new JButton("确定");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		bottompane.add(btn);
		
		btn=new JButton("取消");
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
			JOptionPane.showMessageDialog(this, "保存错误"+e.getMessage());
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
