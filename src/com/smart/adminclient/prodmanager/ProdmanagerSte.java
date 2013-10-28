package com.smart.adminclient.prodmanager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Category;

import com.smart.adminclient.auth.AdminSendHelper;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CStetoolbar;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;

public class ProdmanagerSte extends CSteModel {

	Category logger = Category.getInstance(ProdmanagerSte.class);

	public ProdmanagerSte(CFrame frame) throws HeadlessException {
		super(frame, "产品管理");
	}

	@Override
	public String getTablename() {
		// TODO Auto-generated method stub
		return "np_prod";
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return "nothing";
	}

	@Override
	protected void loadDBColumnInfos() {
		Proddbmodel prodmodel = new Proddbmodel();
		formcolumndisplayinfos = prodmodel.getDisplaycolumninfos();
	}

	@Override
	protected CStetoolbar createToolbar() {
		// TODO Auto-generated method stub
		return new Toolbar(this);
	}

	@Override
	public void doQuery() {
		ClientRequest req = new ClientRequest("npserver:getprodinfo");
		AdminSendHelper sender = new AdminSendHelper();
		try {
			setWaitCursor();
			this.getDBtableModel().clearAll();
			ServerResponse resp = sender.sendRequest(req);
			StringCommand cmd0 = (StringCommand) resp.commandAt(0);
			String msg = cmd0.getString();
			if (!msg.startsWith("+OK")) {
				errorMessage("ERROR", msg);
				return;
			}
			DataCommand cmd1 = (DataCommand) resp.commandAt(1);
			DBTableModel proddbmodel = cmd1.getDbmodel();
			getDBtableModel().bindMemds(proddbmodel);

			getDBtableModel().sort(new String[]{"prodname"},true);
			getSumdbmodel().fireDatachanged();
			tableChanged();
			table.autoSize();

		} catch (Exception e) {
			logger.error("ERROR", e);
			errorMessage("ERROR", e.getMessage());
		} finally {
			setDefaultCursor();
		}
	}

	/**
	 * 安装授权证书
	 */
	void installLicensefile() {
		InstallLicenseDlg dlg=new InstallLicenseDlg();
		dlg.pack();
		dlg.setVisible(true);
		if(!dlg.isOk())return;
		
		File licensefile=new File(dlg.getFilepath());
		
		//上传文件
		int bufsize=102400;
		int totallen=(int)licensefile.length();
		byte[] buf=new byte[bufsize];
		FileInputStream fin=null;
		try {
			fin=new FileInputStream(licensefile);
			int startpos=0;
			while(totallen>0){
				int rded=fin.read(buf);
				totallen-=rded;
				uploadFile(licensefile,startpos,rded,buf,totallen==0);
				startpos += rded;
			}
			
		} catch (Exception e) {
			logger.error("ERROR",e);
			errorMessage("ERROR",e.getMessage());
			return;
		}finally{
			if(fin!=null){
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		doQuery();
	}

	void uploadFile(File licensefile, int startpos, int rded,byte[] data,boolean finished) throws Exception{
		ClientRequest req=new ClientRequest("npserver:uploadlicensefile");
		ParamCommand paramcmd=new ParamCommand();
		req.addCommand(paramcmd);
		paramcmd.addParam("length",String.valueOf(rded));
		paramcmd.addParam("startpos",String.valueOf(startpos));
		paramcmd.addParam("finished",finished?"true":"false");
		paramcmd.addParam("filename",licensefile.getName());
		
		BinfileCommand bfcmd=new BinfileCommand(data,0,rded);
		req.addCommand(bfcmd);
		
		AdminSendHelper sender=new AdminSendHelper();
		ServerResponse svrresp=sender.sendRequest(req);
		
		StringCommand cmd0=(StringCommand) svrresp.commandAt(0);
		String msg=cmd0.getString();
		
		if(!msg.startsWith("+OK")){
			throw new Exception(msg);
		}
	}

	@Override
	protected int on_actionPerformed(String command) {
		if ("upload".equals(command)) {
			installLicensefile();
		}
		return super.on_actionPerformed(command);
	}

	@Override
	public int on_beforeclose() {
		return 0;
	}

	@Override
	protected int on_beforedel(int row) {
		return -1;
	}

	@Override
	protected int on_beforemodify(int row) {
		return -1;
	}

	@Override
	protected int on_beforeNew() {
		return -1;
	}

	@Override
	public int on_beforesave() {
		return -1;
	}

	class Toolbar extends CStetoolbar {

		public Toolbar(ActionListener l) {
			super(l);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected boolean isUsebutton(String actionname) {
			return false;
		}

		@Override
		protected void createOtherButton(ActionListener listener) {
			JButton btn;
			btn = new JButton("查询授权的产品");
			btn.setActionCommand("查询");
			btn.addActionListener(listener);
			btn.setFocusable(false);
			add(btn);

			btn = new JButton("安装产品授证书");
			btn.setActionCommand("upload");
			btn.addActionListener(listener);
			btn.setFocusable(false);
			add(btn);
			
			btn = new JButton("退出");
			btn.setActionCommand(CSteModel.ACTION_EXIT);
			btn.addActionListener(listener);
			btn.setFocusable(false);
			add(btn);
			
		}

	}
	
	
	class InstallLicenseDlg extends CDialog{

		private JTextField textFilepath;
		InstallLicenseDlg(){
			super(ProdmanagerSte.this.getParentFrame(),"安装产品授权证书",true);
			init();
			this.localCenter();
		}
		
		void init(){
			Container cp=this.getContentPane();
			cp.setLayout(new BorderLayout());
			
			JPanel midpanel=createMidpanel();
			cp.add(midpanel,BorderLayout.CENTER);
			
			JPanel bottompanel=createBottompanel();
			cp.add(bottompanel,BorderLayout.SOUTH);
		}

		private JPanel createMidpanel() {
			JPanel jp=new JPanel();
			JLabel lb=new JLabel("授权文件");
			jp.add(lb);
			
			textFilepath = new JTextField(40);
			jp.add(textFilepath);
			
			JButton btn=new JButton("...");
			btn.setActionCommand("choose");
			btn.addActionListener(this);
			jp.add(btn);
			
			return jp;
		}

		private JPanel createBottompanel() {
			JPanel jp=new JPanel();
			
			JButton btn=new JButton("安装");
			btn.setActionCommand("install");
			btn.addActionListener(this);
			jp.add(btn);
			
			btn=new JButton("取消");
			btn.setActionCommand("cancel");
			btn.addActionListener(this);
			jp.add(btn);
			return jp;
		}

		void chooseFile(){
			JFileChooser fc=new JFileChooser(new File("."));
			int ret=fc.showOpenDialog(this);
			if(ret!=JFileChooser.APPROVE_OPTION){
				return;
			}
			textFilepath.setText(fc.getSelectedFile().getAbsolutePath());
		}

		boolean ok=false;
		String filepath=null;
		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd=e.getActionCommand();
			if(cmd.equals("choose")){
				chooseFile();
			}else if(cmd.equals("install")){
				ok=true;
				filepath=textFilepath.getText();
				this.dispose();
			}else if(cmd.equals("cancel")){
				ok=false;
				this.dispose();
			}else{
				super.actionPerformed(e);
			}
		}

		public boolean isOk() {
			return ok;
		}

		public String getFilepath() {
			return filepath;
		}
	}
	
}
