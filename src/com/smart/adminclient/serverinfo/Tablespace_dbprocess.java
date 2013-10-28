package com.smart.adminclient.serverinfo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.RequestProcessorAdapter;
import com.smart.platform.server.process.SteProcessor;
import com.smart.platform.util.StringUtil;

/*功能"系统信息查询"应用服务器处理*/
public class Tablespace_dbprocess extends RequestProcessorAdapter {

	Category logger = Category.getInstance(Tablespace_ste.class);

	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd = (StringCommand) req.commandAt(0);
		String strcmd = cmd.getString();
		if (!strcmd.equals("查询表空间")) {
			return -1;
		}

		Tablespace_ste ste = new Tablespace_ste(null);
		DBTableModel dbmodel = ste.getDBtableModel();
		Connection con = null;
		try {
			con = getSysConnection();
			queryTablespace(con, dbmodel);

		} catch (Exception e) {
			logger.error("ERROR", e);
			resp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
			return 0;
		} finally {
			if (con != null) {
				con.close();
			}
		}

		resp.addCommand(new StringCommand("+OK"));

		DataCommand datacmd = new DataCommand();
		resp.addCommand(datacmd);
		datacmd.setDbmodel(dbmodel);

		return 0;

	}

	void queryTablespace(Connection con, DBTableModel dbmodel)
			throws Exception {
		String sql = "SELECT TABLESPACE_NAME from dba_tablespaces";  
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			while (rs.next()) {
				int row=dbmodel.getRowCount();
				dbmodel.appendRow();
				dbmodel.setItemValue(row, "tablespacename", rs.getString("TABLESPACE_NAME"));
				BigDecimal freesize=getFreesize(con,rs.getString("TABLESPACE_NAME"));
				BigDecimal  totalsize=getTotalsize(con,rs.getString("TABLESPACE_NAME"));
				if(freesize!=null){
					dbmodel.setItemValue(row, "freesize",StringUtil.bytes2string(freesize));
				}
				if(totalsize!=null){
					dbmodel.setItemValue(row, "totalsize",StringUtil.bytes2string(totalsize));
					//计算使用比例
					MathContext mc=new MathContext(3,RoundingMode.HALF_UP);
					BigDecimal usesize=totalsize.subtract(freesize);
					BigDecimal rate=usesize.divide(totalsize,mc).multiply(new BigDecimal(100));
					rate=rate.setScale(2,BigDecimal.ROUND_HALF_UP);
					dbmodel.setItemValue(row,"usesize",StringUtil.bytes2string(usesize));
					dbmodel.setItemValue(row,"usepercent",rate.toPlainString()+"%");
				}
				dbmodel.setdbStatus(row, RecordTrunk.DBSTATUS_SAVED);
			}
		} finally {
			if (c1 != null) {
				c1.close();
			}
		}
	}

	BigDecimal getTotalsize(Connection con, String tsname)throws Exception {
		PreparedStatement c1=null;
		try
		{
			String sql="select sum(bytes) value from dba_data_files where tablespace_name = ?";
			c1=con.prepareStatement(sql);
			c1.setString(1, tsname);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				return rs.getBigDecimal(1);
			}
		}finally{
			if(c1!=null)c1.close();
		}
		return new BigDecimal(0);
	}

	BigDecimal getFreesize(Connection con, String tsname) throws Exception{
		PreparedStatement c1=null;
		try
		{
			String sql="select sum(bytes) value from dba_free_space where tablespace_name = ?";
			c1=con.prepareStatement(sql);
			c1.setString(1, tsname);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				return rs.getBigDecimal(1);
			}
		}finally{
			if(c1!=null)c1.close();
		}
		return new BigDecimal(0);
	}
}