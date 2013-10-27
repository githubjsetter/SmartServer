package com.inca.adminclient.viewlog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.inca.adminclient.auth.AdminSendHelper;
import com.inca.np.auth.ClientUserManager;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.CFrame;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SendHelper;

public class Viewlogfile_frame extends CFrame{
	private JTextArea textView;
	private JTextField textFilename;
	private JTextField textFilelength;
	private JScrollBar fileposbar;
	int startpos=0;
	int filelength=-1;
	private JTextField textStartpos;
	private int partlength;

	public Viewlogfile_frame(){
		super("查看日志文件片段");
		init();
		setDefaultCloseOperation(CFrame.DISPOSE_ON_CLOSE);
	}

	@Override
    public Dimension getPreferredSize() {
		return new Dimension(800,600);
	}

	
	void init(){
		Container cp=getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(createToolbar(),BorderLayout.NORTH);
		
		textView = new JTextArea();
		textView.setLineWrap(false);
		textView.setEditable(false);
		cp.add(new JScrollPane(textView),BorderLayout.CENTER);
	}
	
	JPanel createToolbar(){
		JPanel tb=new JPanel();
		JLabel lb;
		lb=new JLabel("文件名");
		tb.add(lb);

		textFilename = new JTextField(20);
		textFilename.setEditable(false);
		tb.add(textFilename);
		
		
		lb=new JLabel("文件大小");
		tb.add(lb);

		textFilelength = new JTextField(10);
		textFilelength.setEditable(false);
		tb.add(textFilelength);
		
		lb=new JLabel("下载片段位置");
		tb.add(lb);
		
		textStartpos = new JTextField(10);
		textStartpos.setEditable(false);
		tb.add(textStartpos);
		
		
		fileposbar = new JScrollBar(JScrollBar.HORIZONTAL);
		tb.add(fileposbar);
		fileposbar.setValues(0, 1, 0, 100);
		fileposbar.setPreferredSize(new Dimension(100,20));
		fileposbar.addAdjustmentListener(new Hsbhandle());
		
		return tb;
	}
	
	boolean settingvalue=false;
	class Hsbhandle implements AdjustmentListener{

		public void adjustmentValueChanged(AdjustmentEvent e) {
			if(e.getValueIsAdjusting() || settingvalue)return;
			startpos = fileposbar.getValue() * partlength;
			if(startpos>filelength)return;
			downloadPart();
		}
		
	}
	
	public void viewFilepar(String filename){
		textFilename.setText(filename);
		startpos=0;
		downloadPart();
	}
	
	void downloadPart() {
		ClientRequest req = new ClientRequest("npclient:downloadloggerfile");
		ParamCommand pcmd = new ParamCommand();
		req.addCommand(pcmd);
		pcmd.addParam("filename", textFilename.getText());
		pcmd.addParam("startpos", String.valueOf(startpos));

		ServerResponse resp=null;
		try {
			setWaitcursor();
			resp = AdminSendHelper.sendRequest(req);
		} catch (Exception e) {
			errorMessage("错误",e.getMessage());
			return;
		}finally{
			setDefaultcursor();
		}

		// Thread.sleep(5000);

		String respstr = resp.getCommand();
		if (respstr.startsWith("-ERROR")){
			errorMessage("错误",respstr);
			return;
		}

		ParamCommand respcmd = (ParamCommand) resp.commandAt(1);
		partlength = Integer.parseInt(respcmd.getValue("length"));
		//String finished = respcmd.getValue("finished");
		filelength=Integer.parseInt(respcmd.getValue("totallength"));
		int max=filelength/partlength;
		if(filelength%partlength!=0){
			max++;
		}
		//当前位置
		int pos=startpos/partlength;
		settingvalue=true;
		fileposbar.setValues(pos, 1, 0, max);
		settingvalue=false;
		
		textStartpos.setText(String.valueOf(startpos));
		textFilelength.setText(String.valueOf(filelength));
		

		BinfileCommand bincmd=(BinfileCommand) resp.commandAt(2);
		String text=new String(bincmd.getBindata());
		textView.setText(text);
	}
	
	
	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "nbms";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "nbms";
		DefaultNPParam.prodcontext = "npserver";
		DefaultNPParam.defaultappsvrurl = "http://127.0.0.1/npserver/serveradmin.do";

		
		ClientUserManager.getCurrentUser().setUserid("0");
		
		
		Viewlogfile_frame f=new Viewlogfile_frame();
		f.pack();
		f.setVisible(true);
		f.viewFilepar("npserver_app.log");
	}
}
