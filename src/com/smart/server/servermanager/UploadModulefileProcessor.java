package com.smart.server.servermanager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.filedb.DirHelper;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.server.install.Installinfo;
import com.smart.server.install.InstallinfoDB;
import com.smart.server.install.InstallinfoReader;
import com.smart.server.prod.ModuleManager;
import com.smart.server.server.sysproc.CurrentappHelper;

public class UploadModulefileProcessor extends RequestProcessorAdapter {
	Category logger = Category.getInstance(UploadModulefileProcessor.class);
	static String COMMAND = "npserver:uploadmodulefile";

	/**
	 * �����ļ� ����np:fileupload ParamCommand filename �ļ��� filegroupid ��������������
	 * 
	 * @param req
	 * @return -1 ʧ�� 0 �ɹ� 1 �ɹ�,��ȫ���ϴ����
	 * @throws Exception
	 */

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!COMMAND.equals(req.getCommand())) {
			return -1;
		}

		logger.info("start recv module file");

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

		File repodir = getModuleFileRepositoryDir();
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
				installModuelfile(targetfile);
				ModuleManager.getInst().loadModulefromDB();
				logger.info("installModuelfile ok");
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

	/**
	 * ��װģ�鰲װ�ļ�
	 * 
	 * @param targetfile
	 * @throws Exception
	 */
	void installModuelfile(File targetfile) throws Exception {
		ZipFile zipfile = new ZipFile(targetfile);
		Installinfo installinfo = null;
		Enumeration<ZipEntry> en = zipfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry entry = en.nextElement();
			if (entry.getName().equals("installinfo")) {
				InputStream in = null;
				try {
					in = zipfile.getInputStream(entry);
					installinfo = installInfo(in);
				} finally {
					if (in != null)
						in.close();
				}
				in.close();
				break;
			}
		}

		// ������Ϣ���а�װ
		String moduleengname = installinfo.getModuleengname();
		// ��װjar�ļ�
		en = zipfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry entry = en.nextElement();
			if (entry.getName().toLowerCase().endsWith(".jar")) {
				InputStream in = null;
				try {
					in = zipfile.getInputStream(entry);
					installJar(moduleengname, entry, in);
				} finally {
					if (in != null)
						in.close();
				}
			} else if (entry.getName().equals("public_html.zip")) {
				InputStream in = null;
				try {
					in = zipfile.getInputStream(entry);
					installPublic_html(moduleengname, entry, in);
				} finally {
					if (in != null)
						in.close();
				}
			}
		}

	}

	/**
	 * ��װjar�ļ� ����ǲ��������е�jar,�ǲ�����Ҫ����?
	 * 
	 * @param entry
	 * @param in
	 */
	void installJar(String moduleengname, ZipEntry entry, InputStream in)
			throws Exception {
		File libdir = CurrentappHelper.getLibrarydir();
		String jarfilename = entry.getName();
		boolean needwrite = true;
		String purename = DirHelper.getPurename(jarfilename);
		String version = DirHelper.getVersion(jarfilename);
		// String postfix = ss[2];

		// ���lib����û��.
		File fs[] = CurrentappHelper.getLibrarydir().listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			String purename1=DirHelper.getPurename(f.getName());
			String version1=DirHelper.getVersion(f.getName());

			if (!purename1.equals(purename))
				continue;

			logger.debug("found same purename file " + f.getAbsolutePath());
			if (version.compareTo(version1) > 0) {
				logger.debug("clear server lib file " + f.getAbsolutePath());
				DirHelper.clearFile(f);
				needwrite = true;
			}
			if (version.compareTo(version1) == 0) {
				needwrite = true;
			}
		}
/*
		logger.debug(jarfilename + " needwrite=" + needwrite);
		if (!needwrite)
			return;
*/
		// д�ļ�
		File targetfile = new File(CurrentappHelper.getLibrarydir(),
				jarfilename);
		logger.debug("install server jar file,target file=" + targetfile.getAbsolutePath());
		BufferedOutputStream fout = null;
		try {
			fout = new BufferedOutputStream(new FileOutputStream(targetfile));
			int buflen = 102400;
			byte[] buf = new byte[buflen];
			int ct;
			while ((ct = in.read(buf)) > 0) {
				fout.write(buf, 0, ct);
			}
		} finally {
			if (fout != null) {
				fout.close();
			}
		}
	}

	void installPublic_html(String moduleengname, ZipEntry entry, InputStream in)
			throws Exception {
		File htmldir = CurrentappHelper.guessAppdir();
		// ��ZIP.д����ʱ�ļ�
		File tempzip = File.createTempFile("tempzip", "zip");
		FileOutputStream fout = null;
		try {
			int buflen = 102400;
			byte[] buf = new byte[buflen];
			fout = new FileOutputStream(tempzip);
			int rd;
			while ((rd = in.read(buf)) > 0) {
				fout.write(buf, 0, rd);
			}
			fout.close();
			fout = null;

			// ���ļ�
			unzipFile(tempzip, htmldir);

		} finally {
			tempzip.delete();
			if (fout != null) {
				fout.close();
			}
		}
	}

	void unzipFile(File tempzip, File htmldir) throws Exception {
		ZipFile zipfile = new ZipFile(tempzip);
		Enumeration<ZipEntry> en = zipfile.getEntries();
		while (en.hasMoreElements()) {
			ZipEntry zentry = en.nextElement();
			InputStream in = zipfile.getInputStream(zentry);
			FileOutputStream fout = null;
			try {
				int buflen = 102400;
				byte[] buf = new byte[buflen];
				int rd;
				fout = new FileOutputStream(new File(htmldir, zentry.getName()));
				while ((rd = in.read(buf)) > 0) {
					fout.write(buf, 0, rd);
				}
			} finally {
				if (fout != null) {
					fout.close();
				}
				if (in != null) {
					in.close();
				}
			}
		}
	}


	Installinfo installInfo(InputStream in) throws Exception {
		BufferedReader rd = new BufferedReader(new InputStreamReader(in, "gbk"));
		InstallinfoReader ird = new InstallinfoReader();
		Installinfo installinfo = ird.read(rd);

		Connection con = null;
		try {
			con = getConnection();
			InstallinfoDB.install(con, installinfo);
			con.commit();
			return installinfo;
		} catch (Exception e) {
			logger.error("ERROR", e);
			if (con != null)
				con.rollback();
			throw e;
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	/**
	 * license file �Ĵ洢Ŀ¼
	 * 
	 * @return
	 */
	File getModuleFileRepositoryDir() {
		File appdir = CurrentappHelper.guessAppdir();
		return new File(appdir, "WEB-INF/install");
	}

}
