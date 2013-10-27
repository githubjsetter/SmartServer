package com.inca.npserver.server.sysproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.InsertHelper;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.UpdateHelper;

public class Uploadbireport_dbprocessor  extends RequestProcessorAdapter {
	String COMMAND = "npclient:上传bi报表";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!req.getCommand().equals(COMMAND)) {
			return -1;
		}

		// 收到上传的ZIP文件. 解压,安装其中的功能
		ParamCommand cmd1 = (ParamCommand) req.commandAt(1);
		String opid=cmd1.getValue("opid");
		String opcode=cmd1.getValue("opcode");
		String opname=cmd1.getValue("opname");
		String groupname=cmd1.getValue("groupname");
		String prodname=cmd1.getValue("prodname");
		String modulename=cmd1.getValue("modulename");
		
		Connection con = null;
		try {
			con = getConnection();
			updateOp(con,opid,opcode,opname,groupname,prodname,modulename);
			con.commit();
		} catch (Exception e) {
			con.rollback();
			logger.error("Error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				con.close();
			}
		}
		
		File classdir=CurrentappHelper.getClassesdir();
		File targetfile=new File(classdir,"BI报表/"+opid+".npbi");
		targetfile.getParentFile().mkdirs();

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


		BinfileCommand datacmd = (BinfileCommand) req.commandAt(2);
		byte[] bindata = datacmd.getBindata();


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
				logger.info("install zx zip ok");
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

	private void updateOp(Connection con, String opid, String opcode,
			String opname, String groupname,String prodname,String modulename) throws Exception {
		String sql="select opid from np_op where opid=?";
		SelectHelper sh=new SelectHelper(sql);
		sh.bindParam(opid);
		DBTableModel dm=sh.executeSelect(con, 0, 1);
		if(dm.getRowCount()==0){
			InsertHelper ih=new InsertHelper("np_op");
			ih.bindParam("opid", opid);
			ih.bindParam("opcode", opcode);
			ih.bindParam("opname", opname);
			ih.bindParam("groupname", groupname);
			ih.bindParam("classname", "bireport");
			ih.bindParam("prodname", prodname);
			ih.bindParam("modulename", modulename);
			ih.executeInsert(con);
		}else{
			UpdateHelper uh=new UpdateHelper("update np_op set opcode=?,opname=?,groupname=?," +
					"classname='bireport',prodname=?,modulename=? where opid=?");
			uh.bindParam(opcode);
			uh.bindParam(opname);
			uh.bindParam(groupname);
			uh.bindParam(prodname);
			uh.bindParam(modulename);
			uh.bindParam(opid);
			uh.executeUpdate(con);
		}
		
	}
}
