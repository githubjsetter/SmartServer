package com.inca.sysmgr.roleop;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.server.process.MdeProcessor;
import com.inca.np.util.DefaultNPParam;
/*功能"角色管理"应用服务器处理*/
public class Roleop_dbprocess extends MdeProcessor{
	protected CMdeModel getMdeModel() {
		return new Roleop_mde(null,"");
	}
	protected String getMastertablename() {
		return "np_role";
	}
	protected String getDetailtablename() {
		return "np_role_op";
	}
	@Override
	public void on_beforesavemaster(Connection con, Userruninfo userruninfo,
			DBTableModel dbmodel, int row) throws Exception {
		// TODO Auto-generated method stub
		super.on_beforesavemaster(con, userruninfo, dbmodel, row);
		dbmodel.setItemValue(row, "prodname",DefaultNPParam.prodcontext);
	}
	
	@Override
	public void on_beforesave(Connection con, Userruninfo userruninfo,
			DBTableModel dbmodel, int row) throws Exception {
		super.on_beforesave(con, userruninfo, dbmodel, row);
		//如果删除细单,要将功能授权也一并删除
		if(dbmodel.getdbStatus(row)==RecordTrunk.DBSTATUS_DELETE){
			String roleopid=dbmodel.getItemValue(row, "roleopid");
			PreparedStatement c1=null;
			try
			{
				String sql="delete np_op_ap where roleopid=?";
				c1=con.prepareStatement(sql);
				c1.setString(1,roleopid);
				c1.executeUpdate();
				
			}finally{
				if(c1!=null)c1.close();
			}
		}
	}
}
