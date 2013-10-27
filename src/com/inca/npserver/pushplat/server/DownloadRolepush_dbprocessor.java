package com.inca.npserver.pushplat.server;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Vector;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;
import com.inca.npserver.pushplat.common.Pushdbmodel;
import com.inca.npserver.pushplat.common.Pushinfo;

/**
 * 下载某个角色的role_push
 * @author user
 *
 */
public class DownloadRolepush_dbprocessor extends RequestProcessorAdapter{
	static String COMMAND="npserver:下载角色推送"; 
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		String roleid=userinfo.getRoleid();
		Connection con = null;
		try {
			con = getConnection();
			String sql="select pushid,otherwheres from np_role_push where roleid=?";
			SelectHelper sh=new SelectHelper(sql);
			sh.bindParam(roleid);
			DBTableModel rolepushdm=sh.executeSelect(con, 0, 1000);
			DBTableModel dbmodel=new Pushdbmodel();
			Vector<Pushinfo>allpushinfos=PushManager.getAllpushinfo();
			
			for(int r=0;r<rolepushdm.getRowCount();r++){
				String pushid=rolepushdm.getItemValue(r, "pushid");

				Pushinfo pushinfo=searchPushinfo(allpushinfos,pushid);
				if(pushinfo==null){
					continue;
				}
				
				String otherwheres=rolepushdm.getItemValue(r, "otherwheres");
				int newrow=dbmodel.getRowCount();
				dbmodel.appendRow();
				dbmodel.setItemValue(newrow, "pushid", pushid);
				dbmodel.setItemValue(newrow, "otherwheres", otherwheres);
				
				dbmodel.setItemValue(newrow, "pushname", pushinfo.getPushname());
				dbmodel.setItemValue(newrow, "callopid", pushinfo.getCallopid());
				dbmodel.setItemValue(newrow, "callopname", pushinfo.getCallopname());
				dbmodel.setItemValue(newrow, "groupname", pushinfo.getGroupname());
				dbmodel.setItemValue(newrow, "wheres", pushinfo.getWheres());
				dbmodel.setItemValue(newrow, "level", String.valueOf(pushinfo.getLevel()));
			}
			
			resp.addCommand(new StringCommand("+OK"));
			DataCommand dcmd=new DataCommand();
			resp.addCommand(dcmd);
			dcmd.setDbmodel(dbmodel);
			return 0;
			
		} catch (Exception e) {
			logger.error("Error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
		} finally {
			if (con != null) {
				con.close();
			}
		}
		
		return super.process(userinfo, req, resp);
	}
	private Pushinfo searchPushinfo(Vector<Pushinfo> allpushinfos, String pushid) {
		Enumeration<Pushinfo>en=allpushinfos.elements();
		while(en.hasMoreElements()){
			Pushinfo pinfo=en.nextElement();
			if(pinfo.getPushid().equals(pushid)){
				return pinfo;
			}
		}
		return null;
	}

}
