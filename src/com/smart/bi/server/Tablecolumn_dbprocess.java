package com.smart.bi.server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.smart.bi.client.tablecolumn.Tablecolumn_ste;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
import com.smart.platform.util.SelectHelper;
/*功能"基表列定义"应用服务器处理*/
public class Tablecolumn_dbprocess extends SteProcessor{
	protected CSteModel getSteModel() {
		return new Tablecolumn_ste(null);
	}
	protected String getTablename() {
		return "npbi_basetable_column";
	}
	@Override
	public void on_aftersave(Connection con, Userruninfo userrininfo,
			DBTableModel saveddbmodel, int row) throws Exception {
		super.on_aftersave(con, userrininfo, saveddbmodel, row);
		//在sys_column_cn中插入中文列名
		String reportid=saveddbmodel.getItemValue(row, "reportid");
		SelectHelper sh=new SelectHelper("select basetablename from npbi_report_def" +
				" where reportid=?");
		sh.bindParam(reportid);
		String columnname=saveddbmodel.getItemValue(row, "columnname");
		String title=saveddbmodel.getItemValue(row, "title");
		DBTableModel dm=sh.executeSelect(con, 0, 1);
		if(dm.getRowCount()==1){
			String tablename=dm.getItemValue(0, "basetablename");
			tablename=tablename.toUpperCase();
			updateCncolname(con,tablename,columnname,title);
		}
	}
	
	void updateCncolname(Connection con, String tablename, String colname,
			String cntitle) throws Exception {
		PreparedStatement cs = null;
		PreparedStatement ci = null;
		PreparedStatement cu = null;
		try {
			String sql = "select * from SYS_COLUMN_CN where tablename=? and colname=?";
			cs = con.prepareCall(sql);
			cs.setString(1, tablename.toUpperCase());
			cs.setString(2, colname.toUpperCase());
			ResultSet rs = cs.executeQuery();
			if (rs.next()) {
				String dbcntitle = rs.getString("cntitle");
				if(dbcntitle==null)dbcntitle="";
				if (!dbcntitle.equals(cntitle)) {
					// update
					sql = "update SYS_COLUMN_CN set cntitle=? ,cnname=? where tablename=? and colname=?";
					cu = con.prepareStatement(sql);
					cu.setString(1, cntitle);
					cu.setString(2, cntitle);
					cu.setString(3, tablename.toUpperCase());
					cu.setString(4, colname.toUpperCase());
					cu.executeUpdate();
				}
			} else {
				// 插入
				sql = "insert into SYS_COLUMN_CN(tablename,colname,cntitle,cnname)values(?,?,?,?)";
				ci = con.prepareStatement(sql);
				ci.setString(1, tablename.toUpperCase());
				ci.setString(2, colname.toUpperCase());
				ci.setString(3, cntitle);
				ci.setString(4, cntitle);
				ci.executeUpdate();

			}

		} finally {
			if (cs != null)
				cs.close();
			if (ci != null)
				ci.close();
			if (cu != null)
				cu.close();
		}
	}

}
