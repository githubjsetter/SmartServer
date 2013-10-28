package com.smart.server.server.sysproc;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SelectHelper;
import com.smart.server.prod.LicenseManager;
import com.smart.server.prod.Licenseinfo;
import com.smart.server.prod.ModuleManager;

/**
 * 下载某个模块的客户端文件 参数 modulename startpos
 * 
 * @author Administrator
 * 
 */
public class ModulefiledownloadProcessor extends RequestProcessorAdapter {
	static String COMMAND = "npclient:downloadmodulefile";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {

		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		ParamCommand pcmd = (ParamCommand) req.commandAt(1);
		String prodname = pcmd.getValue("prodname");
		String modulename = pcmd.getValue("modulename");
		String strstartpos = pcmd.getValue("startpos");
		long startpos = 0;
		try {
			startpos = Long.parseLong(strstartpos);
		} catch (Exception e) {
		}

		String jarfile = null;
		if (prodname.equals("launcher")) {
			if (!ModuleManager.getInst().isLauncherjar(modulename)) {
				logger.error("-ERROR:不是launcher相关的JAR"
						+ modulename);
				resp.addCommand(new StringCommand("-ERROR:不是launcher相关的JAR"
						+ modulename));
				return 0;
			}
			jarfile = modulename;
		} else {

			if (!prodname.equals("npserver")) {
				Licenseinfo linfo = LicenseManager.getInst().getLicense(
						prodname, modulename);
				if (linfo == null) {
					logger.error("产品:" + prodname + ",模块:"
							+ modulename + "没有授权,无法下载");
					resp.addCommand(new StringCommand("产品:" + prodname + ",模块:"
							+ modulename + "没有授权,无法下载"));
					return 0;
				}
			}
			if (modulename.equals("npbichart")) {
				jarfile = "npbichart-2.3.1.jar";
			} else {
				jarfile = ModuleManager.getInst().getJarfilename(prodname,
						modulename);
			}
		}

		if (jarfile == null || jarfile.length() == 0) {
			logger.error("找不到产品:" + prodname + ",模块:"
					+ modulename + "的客户端jar文件");
			resp.addCommand(new StringCommand("找不到产品:" + prodname + ",模块:"
					+ modulename + "的客户端jar文件"));
			return 0;
		}

		File libdir = CurrentappHelper.getLibrarydir();
		File clientfile = new File(libdir, jarfile);
		if (!clientfile.exists()) {
			logger.error("-ERROR:不存在文件"
					+ clientfile.getAbsolutePath());
			resp.addCommand(new StringCommand("-ERROR:不存在文件"
					+ clientfile.getAbsolutePath()));
			return 0;
		}
		long fn = clientfile.length();

		int buflen = DefaultNPParam.binfileblocksize;
		byte[] buf = new byte[buflen];

		RandomAccessFile raf = new RandomAccessFile(clientfile, "r");
		raf.seek(startpos);
		int readed = raf.read(buf);
		raf.close();

		resp.addCommand(new StringCommand("+OK"));

		ParamCommand resppcmd = new ParamCommand();
		resppcmd.addParam("filename", jarfile);
		resppcmd.addParam("totallength", String.valueOf(clientfile.length()));
		resppcmd.addParam("length", String.valueOf(readed));
		resppcmd
				.addParam("finished", startpos + readed < fn ? "false" : "true");
		resp.addCommand(resppcmd);

		if (readed < 0) {
			readed = 0;
		}
		BinfileCommand bfc = new BinfileCommand(buf, 0, readed);
		resp.addCommand(bfc);

		return 0;
	}

}
