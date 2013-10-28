package com.smart.sysmgr.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DBModel2Jdbc;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;

public class ApbatchsaveProcessor  extends RequestProcessorAdapter {
	String COMMAND = "np:批量保存授权属性";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!(req.commandAt(0) instanceof StringCommand))
			return -1;
		StringCommand cmd = (StringCommand) req.commandAt(0);
		if (!cmd.getString().equals(COMMAND))
			return -1;

		ParamCommand paramcmd = (ParamCommand) req.commandAt(1);
		String roleid = paramcmd.getValue("roleid");
		String opids[] = paramcmd.getValue("opids").split(":");

		DataCommand datacommand = (DataCommand) req.commandAt(2);
		DBTableModel apmodel = datacommand.getDbmodel();

		Connection con = null;
		PreparedStatement c1 = null;
		PreparedStatement c2 = null;

		try {
			// 从np_role_op表查询到roleopid,再由roleopid删除所有np_op_ap表相关记录.
			// 全部重新插入
			con=getConnection();
			String sql = "select roleopid from np_role_op where roleid=? and opid=?";
			c1 = con.prepareStatement(sql);
			for(int i=0;i<opids.length;i++){
				String opid=opids[i];
				c1.setString(1, roleid);
				c1.setString(2, opid);
				ResultSet rs = c1.executeQuery();
				if (!rs.next()) {
					resp.addCommand(new StringCommand("内部错误:找不到roleid=" + roleid
							+ " and opid=" + opid));
					return 0;
				}
				String roleopid = rs.getString("roleopid");
				addAp(con,roleopid,apmodel);
			}
			
			resp.addCommand(new StringCommand("+OK:保存成功"));
			con.commit();
		} catch (Exception e) {
			con.rollback();
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (c1 != null)
				c1.close();
			if (c2 != null)
				c2.close();
			if (con != null)
				con.close();
		}
		return 0;

	}
	
	/**
	 * 设置授权属性
	 * @param con
	 * @param roleid
	 * @param apmodel
	 * @throws Exception
	 */
	void addAp(Connection con,String roleopid,DBTableModel apmodel)throws Exception{
		for(int r=0;r<apmodel.getRowCount();r++){
			String apname=apmodel.getItemValue(r, "apname");
			String apvalue=apmodel.getItemValue(r, "apvalue");
			String aptype=apmodel.getItemValue(r, "aptype");
			String sql="select apid from np_op_ap where roleopid=? and apname=?";
			SelectHelper sh=new SelectHelper(sql);
			sh.bindParam(roleopid);
			sh.bindParam(apname);
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			if(dm.getRowCount()==1){
				//update
				String apid=apmodel.getItemValue(0, "apid");
				UpdateHelper uh=new UpdateHelper("update np_op_ap set apvalue=? where apid=?");
				uh.bindParam(apvalue);
				uh.bindParam(apid);
				uh.executeUpdate(con);
			}else{
				//insert
				InsertHelper ih=new InsertHelper("np_op_ap");
				ih.bindSequence("apid", "np_opap_seq");
				ih.bindParam("roleopid", roleopid);
				ih.bindParam("apname", apname);
				ih.bindParam("aptype", aptype);
				ih.bindParam("apvalue", apvalue);
				ih.executeInsert(con);
			}
		}
	}
	

	Category logger = Category.getInstance(ApbatchsaveProcessor.class);
}
