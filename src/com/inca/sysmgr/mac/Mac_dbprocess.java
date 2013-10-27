package com.inca.sysmgr.mac;
import java.sql.Connection;

import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npserver.server.sysproc.MacManager;
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
