package com.smart.server.timer.client;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Category;

import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.util.SendHelper;

public class Timerlog_ste extends CSteModel{
	Category logger=Category.getInstance(Timerlog_ste.class);
	public Timerlog_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
	}

	@Override
	public String getTablename() {
		return "np_timer_log";
	}

	@Override
	public String getSaveCommandString() {
		return "";
	}

	@Override
	protected int on_actionPerformed(String command) {
		if(command.equals("upload")){
			upload();
			return 0;
		}else{
			return super.on_actionPerformed(command);
		}
	}

	void upload() {
		JFileChooser fc = new JFileChooser(new File("."));
		fc.setFileFilter(new JarfileFilter());
		fc.setMultiSelectionEnabled(true);
		if (fc.showOpenDialog(getParentFrame()) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File fs[] = fc.getSelectedFiles();
		for (int i = 0; i < fs.length; i++) {
			if(!uploadFile(fs[i])){
				break;
			}
		}
		infoMessage("成功", "上传成功. 如果是新增了定时处理类,不需要重启服务器.如果修改了定时处理类,需要重新启动tomcat服务器");
	}

	boolean uploadFile(File f) {
		FileInputStream fin =null;
		try {
			fin = new FileInputStream(f);
			int length = (int) f.length();
			int buflen = 102400;
			byte[] buf = new byte[buflen];
			int totalsend = 0;
			boolean cancelflag = false;
			while (!cancelflag && length > 0) {
				ClientRequest req = new ClientRequest("npserver:上传定时jar包");
				ParamCommand pcmd = new ParamCommand();
				req.addCommand(pcmd);
				int rd = fin.read(buf);
				pcmd.addParam("filename", f.getName());
				pcmd.addParam("length", String.valueOf(rd));
				pcmd.addParam("startpos", String.valueOf(totalsend));
				totalsend += rd;
				length -= rd;
				pcmd.addParam("finished", length == 0 ? "true" : "false");
				BinfileCommand bincmd = new BinfileCommand(buf, 0, rd);
				req.addCommand(bincmd);
				ServerResponse resp = SendHelper.sendRequest(req);
				// Thread.sleep(3000);
				String resultcmd = resp.getCommand();
				if (!resultcmd.startsWith("+OK")){
					logger.error(resultcmd);
					errorMessage("错误", resultcmd);
					return false;
				}
			}
			return true;
		} catch(Exception e) {
			logger.error("error",e);
			errorMessage("错误", e.getMessage());
			return false;
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
		}

	}
	
	class JarfileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().endsWith(".jar"))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "平台定时类jar包(*.jar)";
		}

	}

}
