package com.inca.npworkflow.server;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.UpdateHelper;


/**
 * ��������ȷ��
 * @author user
 *
 */
public class Feedemoconfirm_dbprocessor extends RequestProcessorAdapter{
	Category logger=Category.getInstance(Feedemoconfirm_dbprocessor.class);
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		String cmd=req.getCommand();
		if(cmd.equals("npworkflow.demo.Feedemo.�ύ")){
			commit(userinfo,req,resp);
		//}else if(cmd.equals("npworkflow.demo.Feedemo.ȡ��ȷ��")){
		//	unconfirm(userinfo,req,resp);
		}
		return 0;
	}

	/**
	 * �ύ���
	 * @param userinfo
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	void commit(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp)throws Exception{
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String feedocid=pcmd.getValue("feedocid");
		String sql="select usestatus from np_wf_demo_fee where feedocid=? for update";
		SelectHelper sh=new SelectHelper(sql);
		sh.bindParam(feedocid);
		Connection con=null;
		try {
			con=getConnection();
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			if(dm.getRowCount()==0)throw new Exception("û���ҵ�feedocid="+feedocid);
			String usestatus=dm.getItemValue(0, "usestatus");
			if(!usestatus.equals("1"))throw new Exception("������ʱ״̬,�����ύ");
			sql="update np_wf_demo_fee set usestatus=2 where feedocid=?";
			UpdateHelper uh=new UpdateHelper(sql);
			uh.bindParam(feedocid);
			uh.executeUpdate(con);
			
			//����һ����������
			WfEngine.getInstance().newQueue(con, "np_wf_demo_fee", feedocid);
			
			con.commit();
			resp.addCommand(new StringCommand("+OK"));
			
		} catch (Exception e) {
			logger.error("error",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		}finally{
			if(con!=null)con.close();
		}
	}
/*
	void unconfirm(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp)throws Exception{
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String feedocid=pcmd.getValue("feedocid");
		String sql="select usestatus,approvestatus from np_wf_demo_fee where feedocid=? for update";
		SelectHelper sh=new SelectHelper(sql);
		sh.bindParam(feedocid);
		Connection con=null;
		try {
			con=getConnection();
			DBTableModel dm=sh.executeSelect(con, 0, 1);
			if(dm.getRowCount()==0)throw new Exception("û���ҵ�feedocid="+feedocid);
			String usestatus=dm.getItemValue(0, "usestatus");
			String approvestatus=dm.getItemValue(0, "approvestatus");
			if(!usestatus.equals("2"))throw new Exception("����ȷ��״̬,����ȡ��ȷ��");
			if(approvestatus.compareTo("1")>0)throw new Exception("�ѽ�������,����ȡ��ȷ��");
			sql="update np_wf_demo_fee set usestatus=1 where feedocid=?";
			UpdateHelper uh=new UpdateHelper(sql);
			uh.bindParam(feedocid);
			uh.executeUpdate(con);
			con.commit();
			resp.addCommand(new StringCommand("+OK"));
			
		} catch (Exception e) {
			logger.error("error",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
		}finally{
			if(con!=null)con.close();
		}
	}
*/	
}
