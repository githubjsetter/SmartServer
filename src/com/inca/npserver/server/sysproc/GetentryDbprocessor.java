package com.inca.npserver.server.sysproc;

import java.sql.Connection;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.util.SelectHelper;

/**
 * �г�����Щ���㵥Ԫ
 * 
 * @author Administrator
 * 
 */
public class GetentryDbprocessor extends RequestProcessorAdapter {
	String COMMAND = "npclient:getentry";
	Category logger = Category.getInstance(GetentryDbprocessor.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		String cmd = req.getCommand();
		if (!cmd.equals(COMMAND))
			return -1;

		String deptid = userinfo.getDeptid();
		// �ɲ��Ų�ѯ�ϼ����ţ��ҵ����㵥ԪΪֹ
		Connection con = null;
		try {
			con = getConnection();
			String sql = "select companyid from pub_company start with companyid = ?"
					+ " connect by companyid=prior parentcompanyid";
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(deptid);
			DBTableModel dbmodel = sh.executeSelect(con, 0, 1000);

			// ��ÿ������ID��ѯ�Ƿ����˺��㵥Ԫ
			DBTableModel entrydbmodel = null;
			sql = "select entryid,entryname from pub_entry where entrycompanyid=?";
			for (int i = 0; i < dbmodel.getRowCount(); i++) {
				String companyid = dbmodel.getItemValue(i, "companyid");
				sh = new SelectHelper(sql);
				sh.bindParam(companyid);
				DBTableModel tmpdbmodel = sh.executeSelect(con, 0, 1);
				if (entrydbmodel == null) {
					entrydbmodel = tmpdbmodel;
				} else {
					entrydbmodel.appendDbmodel(tmpdbmodel);
				}
				if(entrydbmodel.getRowCount()>=1)break;
			}
			if (entrydbmodel==null || entrydbmodel.getRowCount() == 0) {
				// û���ҵ�
				resp.addCommand(new StringCommand("-ERROR:û�ж�����㵥Ԫ"));
			} else {
				resp.addCommand(new StringCommand("+OK"));
				DataCommand dcmd = new DataCommand();
				dcmd.setDbmodel(entrydbmodel);
				String entryid=entrydbmodel.getItemValue(0, "entryid");
				logger.info("set user="+userinfo.getUserid()+"'s default entryid="+entryid);
				userinfo.setEntryid(entryid);
				resp.addCommand(dcmd);
			}
			return 0;

		} catch (Exception e) {
			logger.error("error", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
			return 0;
		} finally {
			if (con != null)
				con.close();
		}
	}

}
