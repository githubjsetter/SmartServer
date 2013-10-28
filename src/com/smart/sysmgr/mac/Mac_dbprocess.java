package com.smart.sysmgr.mac;
import java.sql.Connection;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.server.server.sysproc.MacManager;
/*����"������������"Ӧ�÷���������*/
public class Mac_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Mac_ste(null);
	}
	protected String getTablename() {
		return "np_mac";
	}
	@Override
	public void on_aftersave(Connection con, Userruninfo userrininfo,
			DBTableModel saveddbmodel, int row) throws Exception {
		// TODO Auto-generated method stub
		super.on_aftersave(con, userrininfo, saveddbmodel, row);
		
		//�����ύ
		con.commit();
		MacManager.getInst().reload();
	}
	
}
