package com.inca.np.filesync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipFile;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.fileserver.FileServer;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.ZipHelper;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-8-2 Time: 18:55:42
 * To change this template use File | Settings | File Templates.
 */
public class Uploadfile_dbprocess extends RequestProcessorAdapter {

	Category logger = Category.getInstance(Uploadfile_dbprocess.class);

	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		CommandBase cmd0 = req.commandAt(0);
		if (!(cmd0 instanceof StringCommand && ((StringCommand) cmd0)
				.getString().equals("np:uploadfile"))) {
			return -1;
		}

		ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
		String uploadtype = cmd1.getValue("uploadtype");

		if (uploadtype.equals("WEBAPP")) {
			uploadWebinf(userinfo, req, resp);
		} else if (uploadtype.equals("RECORDFILE")) {
			uploadRecordFile(userinfo, req, resp);
		} else {
			resp.addCommand(new StringCommand("-ERROR:不明uploadtype"));
		}

		return 0;
	}

	/**
	 * 上传记录附件文件
	 * 
	 * @param userinfo
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	void uploadRecordFile(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
		String filegroupid = cmd1.getValue("filegroupid");
		if (filegroupid == null || filegroupid.length() == 0) {
			Connection con = null;
			try {
				con = this.getConnection();
				filegroupid = DBModel2Jdbc.getSeqvalue(con, "np_filegroup_seq");
				cmd1.addParam("filegroupid", filegroupid);
			} catch (Exception e) {
				logger.error("ERROR", e);
				throw e;
			} finally {
				if (con != null) {
					con.close();
				}
			}
		}
		ParamCommand respparamcmd = new ParamCommand();
		respparamcmd.addParam("filegroupid", filegroupid);

		FileServer filesvr = FileServer.getInstance();
		int ret = filesvr.recvFile(req, resp);
		// ret==-1 失败 ==0成功 ==1成功,并全部下载完
		if (ret == 1) {
			Connection con = null;
			PreparedStatement c1 = null;
			try {
				con = this.getConnection();
				String sql = "insert into np_filegroup(filegroupid,credate,inputmanid)values(?,sysdate,?)";
				c1 = con.prepareStatement(sql);
				c1.setString(1, filegroupid);
				c1.setString(2, userinfo.getUserid());
				c1.executeUpdate();
				con.commit();
			} catch (Exception e) {
				con.rollback();
				if (e.getMessage().indexOf("ORA-00001") < 0) {
					logger.error("ERROR", e);
					resp.addCommand(new StringCommand("-ERROR:"
							+ e.getMessage()));
				}
			} finally {
				if (c1 != null) {
					c1.close();
				}
				if (con != null) {
					con.close();
				}
			}

		}

		resp.addCommand(new StringCommand("+OK:upload成功"));
		resp.addCommand(respparamcmd);
	}

	/**
	 * 上传更新WEB-INF 接收文件
	 * 
	 * @param req
	 */
	void uploadWebinf(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) {
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

		String memo = cmd1.getValue("memo");

		BinfileCommand datacmd = (BinfileCommand) req.commandAt(2);
		byte[] bindata = datacmd.getBindata();

		File tmpdir = getTempdir();
		File targetfile = new File(tmpdir, "WEB-INF.zip");

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
			return;
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
				}
			}
		}

		if (finished) {
			// 解压
			File webinfodir = getWebapplicationDir();
			try {
				ZipHelper.unzipFile(targetfile, webinfodir);
				log(userinfo, webinfodir, targetfile, req.getRemoteip(), memo);
			} catch (Exception e) {
				logger.error("error", e);
				resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
				return;
			}
		}
		resp.addCommand(new StringCommand("+OK:upload成功"));
	}

	File getTempdir() {
		File f = null;
		try {
			f = File.createTempFile("tmp", "dat");
			return f.getParentFile();
		} catch (IOException e) {
			logger.error("error", e);
			return new File(".");
		} finally {
			if (f != null) {
				f.delete();
			}
		}
	}

	void log(Userruninfo userinfo, File dir, File zipfile, String remoteip,
			String memo) {
		Connection con = null;
		PreparedStatement c1 = null;
		String sql = "insert into np_upload_log(seqid,credate,prodname,"
				+ "remoteip,filecount,memo,employeeid,employeename)values(np_upload_log_seq.nextval,sysdate,?,"
				+ "?,?,?,?,?)";
		String path = dir.getAbsolutePath();
		path = path.replaceAll("\\\\", "/");
		int p = path.lastIndexOf("/");
		String prodname = path.substring(p + 1);

		int filect = 0;
		try {
			Enumeration en = new ZipFile(zipfile).getEntries();
			while (en.hasMoreElements()) {
				en.nextElement();
				filect++;
			}
		} catch (IOException e2) {
		}

		try {
			con = getConnection();
			c1 = con.prepareStatement(sql);
			c1.setString(1, prodname);
			c1.setString(2, remoteip);
			c1.setInt(3, filect);
			c1.setString(4, memo);
			c1.setString(5, userinfo.getUserid());
			c1.setString(6, userinfo.getUsername());
			c1.executeUpdate();
			con.commit();
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
			}
			logger.error("ERROR", e);
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}

	}
}
