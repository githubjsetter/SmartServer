package com.inca.npserver.pushplat.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.npserver.server.sysproc.CurrentappHelper;

public class Pushfileupload_dbprocessor  extends RequestProcessorAdapter {
	String COMMAND = "npserver:上传推送文件";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!req.getCommand().equals(COMMAND)) {
			return -1;
		}

		// 收到上传的ZIP文件. 解压,安装其中的功能
		ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
		int datalen = 0;
		try {
			datalen = Integer.parseInt(cmd1.getValue("length"));
		} catch (Exception e) {
		}

		int startpos = 0;
		try {
			startpos = Integer.parseInt(cmd1.getValue("startpos"));
		} catch (Exception e) {
		}

		boolean finished = cmd1.getValue("finished").equals("true");

		String filename = cmd1.getValue("filename");

		BinfileCommand datacmd = (BinfileCommand) req.commandAt(2);
		byte[] bindata = datacmd.getBindata();

		File repodir = new File(CurrentappHelper.getClassesdir(), "push");
		repodir.mkdirs();

		File targetfile = new File(repodir, filename);

		FileOutputStream fout = null;
		try {
			if (startpos == 0) {
				fout = new FileOutputStream(targetfile, false);
			} else {
				fout = new FileOutputStream(targetfile, true);
			}
			fout.write(bindata);

		} catch (Exception e) {
			logger.error("error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
			return 0;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}

		logger.info("finished=" + finished);
		if (finished) {
			try {
				logger.info("install push file ok");
			} catch (Exception e) {
				logger.error("ERROR", e);
				resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
				return 0;
			}
		}
		logger.info("return ok");
		resp.addCommand(new StringCommand("+OK"));
		return 0;
	}
}
