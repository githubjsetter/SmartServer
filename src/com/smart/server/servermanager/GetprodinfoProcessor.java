package com.smart.server.servermanager;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.util.SelectHelper;
import com.smart.server.prod.LicenseManager;
import com.smart.server.prod.Licenseinfo;

/**
 * 取得服务器上安装产品信息
 * 
 * @author Administrator
 * 
 */
public class GetprodinfoProcessor extends RequestProcessorAdapter {

	static String SVRCOMMAND = "npserver:getprodinfo";

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		if (!SVRCOMMAND.equals(req.getCommand())) {
			return -1;
		}

		LicenseManager lm = LicenseManager.getInst();
		Connection con = null;
		try {
			con = this.getConnection();
			// 处理
			SelectHelper sh = null;
			DBTableModel prodinfos = new Proddbmodel();

			sh = new SelectHelper(
					"select modulename,prodname,engname,version,'' license from np_module");
			DBTableModel moduleinfos = sh.executeSelect(con, 0, 1000);

			resp.addCommand(new StringCommand("+OK"));

			DataCommand dcmd = new DataCommand();
			dcmd.setDbmodel(prodinfos);
			resp.addCommand(dcmd);

			HashMap<String, HashMap<String, String>> prodmodualmap = new HashMap<String, HashMap<String, String>>();
			Enumeration<Licenseinfo> en = lm.getLicenseinfos().elements();

			SimpleDateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; en.hasMoreElements(); i++) {
				Licenseinfo licenseinfo = en.nextElement();
				prodinfos.appendRow();

				String prodname = licenseinfo.getProdname();
				prodinfos.setItemValue(i, "prodname", prodname);

				prodinfos.setItemValue(i, "copyright", licenseinfo
						.getCopyright());
				prodinfos
						.setItemValue(i, "authunit", licenseinfo.getAuthunit());
				prodinfos.setItemValue(i, "startdate", dfmt.format(licenseinfo
						.getStartdate().getTime()));
				prodinfos.setItemValue(i, "enddate", dfmt.format(licenseinfo
						.getEnddate().getTime()));
				prodinfos
						.setItemValue(i, "serverip", licenseinfo.getServerip());
				prodinfos.setItemValue(i, "maxclientuser", String
						.valueOf(licenseinfo.getMaxclientuser()));
				prodinfos
						.setItemValue(i, "modules", licenseinfo.getModulestr());

				HashMap<String, String> modulemap = new HashMap<String, String>();
				Enumeration<String> en1 = licenseinfo.getModules().elements();
				while (en1.hasMoreElements()) {
					String mname = en1.nextElement();
					modulemap.put(mname, mname);
				}

				prodmodualmap.put(prodname, modulemap);
				prodinfos.setdbStatus(i, RecordTrunk.DBSTATUS_SAVED);
			}

			dcmd = new DataCommand();
			dcmd.setDbmodel(moduleinfos);

			// 检查授权
			for (int i = 0; i < moduleinfos.getRowCount(); i++) {
				String prodname = moduleinfos.getItemValue(i, "prodname");
				String modulename = moduleinfos.getItemValue(i, "modulename");
				if (prodname.equals("npserver")) {
					moduleinfos.setItemValue(i, "license", "已授权");
				} else {
					HashMap<String, String> modulemap = prodmodualmap
							.get(prodname);
					if (modulemap == null || modulemap.get(modulename) == null) {
						moduleinfos.setItemValue(i, "license", "未授权");
					} else {
						moduleinfos.setItemValue(i, "license", "已授权");
					}
				}
				moduleinfos.setdbStatus(i, RecordTrunk.DBSTATUS_SAVED);
			}

			resp.addCommand(dcmd);
		} catch (Exception e) {
			// 如果修改数据库 发生错误 rollback
			// con.rollback();
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
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
