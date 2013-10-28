package com.smart.server.server.sysproc;

import java.sql.Connection;
import java.sql.SQLException;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.SelectHelper;
import com.smart.server.prod.LicenseManager;
import com.smart.server.prod.Licenseinfo;


/**
 * 上传ROLEID,下载这个ROLE对应的OP,并考虑模块授权
 * @author Administrator
 *
 */
public class GetroleoplistProcessor extends RequestProcessorAdapter {
	static String COMMAND="npclient:getroleoplist";
	
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse svrresp) throws Exception {
		if(!COMMAND.equals(req.getCommand())){
			return -1;
		}
		
		ParamCommand pcmd=(ParamCommand) req.commandAt(1);
		String roleid=pcmd.getValue("roleid");
		
		Connection con = null;
		String sql = "select * from np_role where roleid=?";

		LicenseManager lm=LicenseManager.getInst();
		try {
			con = this.getConnection();
			SelectHelper sh=new SelectHelper(sql);
			sh.bindParam(roleid);
			DBTableModel opsdbmodel=sh.executeSelect(con, 0, 10000);
			//去掉未授权的
			for(int r=0;r<opsdbmodel.getRowCount();r++){
				String prodname=opsdbmodel.getItemValue(r, "prodname");
				String modulename=opsdbmodel.getItemValue(r, "modulename");
				Licenseinfo linfo=lm.getLicense(prodname, modulename);
				if(linfo==null){
					opsdbmodel.removeRow(r);
					r--;
				}
			}
			svrresp.addCommand(new StringCommand("+OK"));
			DataCommand dcmd=new DataCommand();
			dcmd.setDbmodel(opsdbmodel);
			svrresp.addCommand(dcmd);
			
		} catch (Exception e) {
			logger.error("ERROR", e);
			svrresp.addCommand(new StringCommand("-ERROR"+e.getMessage()));
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
		
		return 0;
	}
}