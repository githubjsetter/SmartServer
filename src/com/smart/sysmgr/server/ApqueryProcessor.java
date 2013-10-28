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
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.DefaultNPParam;


/**
 * 查询授权属性的服务
 * ParamCommand上传功能frame的类名,查出opid

, 由opid和当前人员的当前roleid来查询授权属性
 * @author Administrator
 *
 */
public class ApqueryProcessor extends RequestProcessorAdapter{
	String COMMAND="np:查询授权属性";
	
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!(req.commandAt(0) instanceof StringCommand))return -1;
		StringCommand cmd=(StringCommand) req.commandAt(0);
		if(!cmd.getString().equals(COMMAND))return -1;
		
		ParamCommand paramcmd=(ParamCommand)req.commandAt(1);
		String opid=paramcmd.getValue("opid");
		String roleid=paramcmd.getValue("roleid");
		
		Connection con=null;
		PreparedStatement c1=null;
		PreparedStatement c2=null;
		
		try
		{
			con=getConnection();
			String sql="select opid from np_op where opid=?";
			c2=con.prepareStatement(sql);
			c2.setString(1,opid);
			ResultSet rs=c2.executeQuery();
			if(!rs.next()){
				resp.addCommand(new StringCommand("-ERROR:内部错误,功能没有在np_op表登记,opid="+opid));
				return 0;
			}
			ParamCommand respparam=new ParamCommand();
			respparam.addParam("opid",opid);
			
			sql="select * from np_op_ap where roleopid in(select roleopid from np_role_op" +
					" where roleid=? and opid=?)";
			c1=con.prepareStatement(sql);
			c1.setString(1,roleid);
			c1.setString(2,opid);
			rs=c1.executeQuery();
			DBTableModel apmodel=DBModel2Jdbc.createFromRS(rs);
			resp.addCommand(new StringCommand("+OK"));
			resp.addCommand(respparam);
			DataCommand datacmd=new DataCommand();
			resp.addCommand(datacmd);
			datacmd.setDbmodel(apmodel);
		}catch(Exception e){
			logger.error("ERROR",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		}finally{
			if(c1!=null)c1.close();
			if(c2!=null)c2.close();
			if(con!=null)con.close();
		}
		return 0;
		
	}
	Category logger=Category.getInstance(ApqueryProcessor.class);
}
