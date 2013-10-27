package com.inca.npclient.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.filedb.CurrentdirHelper;
import com.inca.np.filedb.DirHelper;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.MD5Helper;
import com.inca.np.util.SendHelper;
import com.inca.npclient.system.ExitcopyfileProc;
import com.inca.npclient.system.NpclientParam;
import com.inca.npserver.server.sysproc.CurrentappHelper;

/**
 * 下载NPSERVER启动环境.
 * 
 * 
 * @author Administrator
 * 
 */
public class LauncherManager {
	private static LauncherManager inst;

	public Vector<String> getLaunchjars() {
		return launchjars;
	}

	public boolean isHasdownload() {
		return hasdownload;
	}

	public static LauncherManager getInst() {
		if (inst == null) {
			inst = new LauncherManager();
		}
		return inst;
	}

	boolean hasdownload = false;

	Category logger = Category.getInstance(LauncherManager.class);

	Vector<String> launchjars=new Vector<String>();
	public void loadLuncherjars() throws Exception {
		hasdownload = false;
		ClientRequest req = new ClientRequest("npclient:downloadlaunchers");
		ServerResponse resp;
		try {
			resp = SendHelper.sendRequest(req);
		} catch (Exception e) {
			logger.error("ERROR", e);
			return;
		}

		if (!resp.getCommand().startsWith("+OK")) {
			logger.error(resp.getCommand());
			return;
		}

		DataCommand dcmd = (DataCommand) resp.commandAt(1);
		DBTableModel dbmodel = dcmd.getDbmodel();

		File appdir = CurrentdirHelper.getappDir();
		File libdir = new File(appdir, "lib");
		libdir.mkdirs();
		File bindir = new File(appdir, "bin");
		bindir.mkdirs();

		launchjars.clear();
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String jarfilename = dbmodel.getItemValue(r, "jarfilename");
			String md5 = dbmodel.getItemValue(r, "md5");
			File targetfile = null;
			if (jarfilename.endsWith(".jar")) {
				targetfile = new File(libdir, jarfilename);
				launchjars.add(jarfilename);
			} else if (jarfilename.endsWith(".cmd")) {
				targetfile = new File(bindir, jarfilename);
			}
			String curmd5 = "not exists";
			if (targetfile.exists()) {
				curmd5 = MD5Helper.MD5(targetfile);
			}

			if (!curmd5.equals(md5)) {
				// 下载文件
				logger.debug(jarfilename + ",curmd5=" + curmd5 + ",server md5="
						+ md5);

				Moduledownloader mdl = new Moduledownloader();
				File downloadedfile = mdl.downloadModule("launcher",
						jarfilename, true, true);
				if (downloadedfile == null) {
					logger.error("下载失败:" + mdl.getErrormessage());
					throw new Exception("下载失败:" + mdl.getErrormessage());
				}
				logger.info("downloaded launcher file=" + jarfilename);
				hasdownload = true;

				if (jarfilename.endsWith(".cmd")) {
					// setpath.cmd下载到了lib下了.move到bin下
					copyFile(downloadedfile, new File(bindir, "setpath.cmd"));
					copyFile(downloadedfile, new File(bindir, "setpath.bat"));
					downloadedfile.delete();
				} else {
					// 清除非同版本的.因使用了cache,要找上一个目录
					DirHelper.clearOtherVersionOnexit(downloadedfile
							.getParentFile().getParentFile(), downloadedfile
							.getName());
					// 加到需要复制的线程中

					ExitcopyfileProc.addCopyfile(downloadedfile, targetfile,
							true);
				}
			}

		}
	}

	private void copyFile(File srcfile, File outfile) {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try {
			fin = new FileInputStream(srcfile);
			fout = new FileOutputStream(outfile);
			int buflen = 8192;
			byte buf[] = new byte[buflen];
			while (true) {
				int rd = fin.read(buf);
				if (rd <= 0)
					break;
				fout.write(buf, 0, rd);
			}
		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
									// File | Settings | File Templates.
		} finally {
			if (fin != null)
				try {
					fin.close();
				} catch (IOException e) {
				}
			if (fout != null)
				try {
					fout.close();
				} catch (IOException e) {
				}
		}
	}

	public static void main(String[] args) {
		new NpclientParam();
		LauncherManager lm = LauncherManager.getInst();
		System.out.println(lm.isHasdownload());
	}
}
