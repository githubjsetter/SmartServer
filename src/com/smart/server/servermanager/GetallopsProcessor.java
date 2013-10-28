package com.smart.server.servermanager;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.SelectHelper;
import com.smart.server.prod.LicenseManager;
import com.smart.server.prod.Licenseinfo;

/**
 * 取得全部功能清单
 * @author Administrator
 *
 */
public class GetallopsProcessor extends RequestProcessorAdapter {

	static String SVRCOMMAND="npserver:getallops";
	
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if(!SVRCOMMAND.equals(req.getCommand())){
			return -1;
		}
		
		
		Connection con = null;
		try {
			con = this.getConnection();
			//处理
			SelectHelper sh=new SelectHelper("select * from np_op");
			DBTableModel ops=sh.executeSelect(con, 0, 100000);
			
			DBTableModel prodinfos = sh.executeSelect(con, 0, 1000);
			sh = new SelectHelper("select prodname from np_prod");

			HashMap<String,HashMap<String,String>> prodmodualmap=new HashMap<String,HashMap<String,String>>();
			// 检查是不是过期了
			for (int i = 0; i < prodinfos.getRowCount(); i++) {
				String prodname = prodinfos.getItemValue(i, "prodname");
				Licenseinfo licenseinfo = LicenseManager.getInst().getLicense(
						prodname);
				if(licenseinfo==null){
					continue;
				}
				HashMap<String,String> modulemap=new HashMap<String,String>();
				Enumeration<String> en=licenseinfo.getModules().elements();
				while(en.hasMoreElements()){
					String mname=en.nextElement();
					modulemap.put(mname, mname);
				}
				
				prodmodualmap.put(prodname,modulemap);
			}

			//去掉没有授权的ops
			for(int i=0;i<ops.getRowCount();i++){
				String prodname=ops.getItemValue(i, "prodname");
				String modulename=ops.getItemValue(i, "modulename");
				HashMap<String, String> modulemap = prodmodualmap.get(prodname);
				if (modulemap == null || modulemap.get(modulename) == null) {
					ops.removeRow(i);
					i--;
					continue;
				}
			}
			
			
			resp.addCommand(new StringCommand("+OK"));
			
			DataCommand dcmd=new  DataCommand();
			dcmd.setDbmodel(ops);
			resp.addCommand(dcmd);
		} catch (Exception e) {
			//如果修改数据库 发生错误 rollback
			//con.rollback();
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
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
