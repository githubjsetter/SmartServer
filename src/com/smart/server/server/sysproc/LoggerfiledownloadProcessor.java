package com.smart.server.server.sysproc;

import java.io.File;
import java.io.RandomAccessFile;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.DefaultNPParam;

/**
 * ������־�ļ�
 * @author Administrator
 *
 */
public class LoggerfiledownloadProcessor  extends RequestProcessorAdapter {
	static String COMMAND = "npclient:downloadloggerfile";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {

		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		ParamCommand pcmd = (ParamCommand) req.commandAt(1);
		String filename = pcmd.getValue("filename");
		String strstartpos = pcmd.getValue("startpos");
		long startpos = 0;
		try {
			startpos = Long.parseLong(strstartpos);
		} catch (Exception e) {
		}

		File targetfile=new File(CurrentappHelper.guessAppdir()+"/logs/"+filename);

		if (!targetfile.exists()) {
			resp.addCommand(new StringCommand("-ERROR:�������ļ�"
					+ targetfile.getAbsolutePath()));
			return 0;
		}
		long fn = targetfile.length();

		int buflen = DefaultNPParam.binfileblocksize;
		byte[] buf = new byte[buflen];

		RandomAccessFile raf = new RandomAccessFile(targetfile, "r");
		raf.seek(startpos);
		int readed = raf.read(buf);
		raf.close();

		resp.addCommand(new StringCommand("+OK"));

		ParamCommand resppcmd = new ParamCommand();
		resppcmd.addParam("filename", filename);
		resppcmd.addParam("totallength", String.valueOf(targetfile.length()));
		resppcmd.addParam("length", String.valueOf(readed));
		resppcmd
				.addParam("finished", startpos + readed < fn ? "false" : "true");
		resp.addCommand(resppcmd);

		if(readed<0){
			readed=0;
		}
		BinfileCommand bfc = new BinfileCommand(buf, 0, readed);
		resp.addCommand(bfc);

		return 0;
	}

}
