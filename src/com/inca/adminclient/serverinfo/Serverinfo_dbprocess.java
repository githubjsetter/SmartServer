package com.inca.adminclient.serverinfo;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Category;

import com.inca.np.server.RequestProcessorAdapter;
import com.inca.np.server.process.SteProcessor;
import com.inca.np.util.StringUtil;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.RecordTrunk;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
/*功能"系统信息查询"应用服务器处理*/
public class Serverinfo_dbprocess extends RequestProcessorAdapter{

	Category logger=Category.getInstance(Serverinfo_ste.class);
	@Override
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		StringCommand cmd=(StringCommand)req.commandAt(0);
		String strcmd=cmd.getString();
		if(!strcmd.equals("查询服务器信息")){
			return -1;
		}
		
		Serverinfo_ste ste=new Serverinfo_ste(null);
		DBTableModel dbmodel=ste.getDBtableModel();
		Connection con=null;
		try
		{
			con=getSysConnection();
			
			loadFixsize(con,dbmodel,"fixed size");
			loadFixsize(con,dbmodel,"variable size");
			loadFixsize(con,dbmodel,"database buffers");
			loadFixsize(con,dbmodel,"redo buffers");
			
			loadDbrate(con,dbmodel);
			loadDbdirrate(con,dbmodel);

			loadDatarate(con,dbmodel);
			loadSessionmem(con,dbmodel);
			loadMaxSessionmem(con,dbmodel);

			loadSortmem(con,dbmodel);
			loadSortdisk(con,dbmodel);
			loadSgafreemem(con,dbmodel);
			
		}catch(Exception e){
			logger.error("ERROR",e);
			resp.addCommand(new StringCommand("-ERROR:"+e.getMessage()));
			return 0;
		}finally{
			if(con!=null){
				con.close();
			}
		}
		
		resp.addCommand(new StringCommand("+OK"));
		
		DataCommand datacmd=new DataCommand();
		resp.addCommand(datacmd);
		datacmd.setDbmodel(dbmodel);
		
		
		return 0;
		
	}
	void loadFixsize(Connection con, DBTableModel dbmodel,String keyname) throws Exception{
		String sql="select value from v$sga where lower(name)='"+keyname+"'";
		
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				String value=rs.getString("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", keyname);
				int iv=0;
				try{
					iv=Integer.parseInt(value);
				}catch(Exception e){}
				String striv=StringUtil.bytes2string(iv);
				dbmodel.setItemValue(row, "value", striv);
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
	
	/**
	 * 缓存命中率
	 * @param con
	 * @param dbmodel
	 * @throws Exception
	 */
	void loadDbrate(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT (1 - SUM(RELOADS) / SUM(PINS))  value  FROM V$LIBRARYCACHE";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				String value=rs.getString("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", "库缓存命中率");
				BigDecimal rate=new BigDecimal(value);
				rate=rate.multiply(new BigDecimal(100));
				rate=rate.setScale(2,BigDecimal.ROUND_HALF_UP);
				dbmodel.setItemValue(row, "value", rate.toPlainString()+"%");
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
		
	}
	
	/**
	 * 数据字典
	 * @param con
	 * @param dbmodel
	 * @throws Exception
	 */
	void loadDbdirrate(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT (1 - SUM(GETMISSES ) / SUM(GETS)) value FROM V$ROWCACHE";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				String value=rs.getString("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", "数据字典命中率");
				BigDecimal rate=new BigDecimal(value);
				rate=rate.multiply(new BigDecimal(100));
				rate=rate.setScale(2,BigDecimal.ROUND_HALF_UP);
				dbmodel.setItemValue(row, "value", rate.toPlainString()+"%");
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
	
	/**
	 * 数据命中率
	 * @param con
	 * @param dbmodel
	 * @throws Exception
	 */
	void loadDatarate(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT  VALUE      FROM V$SYSSTAT   "+   
			" WHERE upper(NAME) IN ('DB BLOCK GETS', 'CONSISTENT GETS',        'PHYSICAL READS')"+
			" order by name";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			rs.next();BigDecimal v1=rs.getBigDecimal("value");
			rs.next();BigDecimal v2=rs.getBigDecimal("value");
			rs.next();BigDecimal v3=rs.getBigDecimal("value");
			//v =1 - (v3 /(v1 + v2))
			BigDecimal tmp=v1.add(v2);
			MathContext mc=new MathContext(10,RoundingMode.HALF_UP);
			v3=v3.divide(tmp,mc);
			BigDecimal v=new BigDecimal(1).subtract(v3);
			v=v.multiply(new BigDecimal(100));
			v=v.setScale(2,BigDecimal.ROUND_HALF_UP);

			dbmodel.appendRow();
			int row=dbmodel.getRowCount()-1;
			dbmodel.setItemValue(row, "name", "数据访问命中率");
			dbmodel.setItemValue(row, "value", v.toPlainString()+"%");
			dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
	
	/**
	 * 连接占内存
	 * @param con
	 * @param dbmodel
	 * @throws Exception
	 */
	void loadSessionmem(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT SUM(VALUE) value FROM V$SESSTAT, V$STATNAME "+      
			" WHERE upper(NAME) = 'SESSION UGA MEMORY' "+         
			" AND V$SESSTAT.STATISTIC# = V$STATNAME.STATISTIC#";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				int value=rs.getInt("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", "客户端连接占内存");
				dbmodel.setItemValue(row, "value", StringUtil.bytes2string(value));
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
	
	void loadMaxSessionmem(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT SUM(VALUE) value FROM V$SESSTAT, V$STATNAME "+      
			" WHERE upper(NAME) = 'SESSION UGA MEMORY MAX' "+         
			" AND V$SESSTAT.STATISTIC# = V$STATNAME.STATISTIC#";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				int value=rs.getInt("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", "客户端连接占最大内存");
				dbmodel.setItemValue(row, "value", StringUtil.bytes2string(value));
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
	
	/**
	 * 内存排序次
	 * @param con
	 * @param dbmodel
	 * @throws Exception
	 */
	void loadSortmem(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT value   FROM v$sysstat   WHERE lower(name) IN ('sorts (memory)')";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				String value=rs.getString("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", "内存排序次");
				dbmodel.setItemValue(row, "value", value);
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
	
	void loadSortdisk(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT value   FROM v$sysstat   WHERE lower(name) IN ('sorts (disk)')";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				String value=rs.getString("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", "磁盘排序次");
				dbmodel.setItemValue(row, "value", value);
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
	
	void loadSgafreemem(Connection con, DBTableModel dbmodel) throws Exception{
		String sql="SELECT nvl(sum(bytes),0) value FROM V$SGASTAT WHERE upper(NAME) = 'FREE MEMORY'";
		PreparedStatement c1=null;
		try{
			c1=con.prepareStatement(sql);
			ResultSet rs=c1.executeQuery();
			if(rs.next()){
				int value=rs.getInt("value");
				dbmodel.appendRow();
				int row=dbmodel.getRowCount()-1;
				dbmodel.setItemValue(row, "name", "SGA剩余空间");
				dbmodel.setItemValue(row, "value", StringUtil.bytes2string(value));
				dbmodel.setdbStatus(row,RecordTrunk.DBSTATUS_SAVED);
			}
		}finally{
			if(c1!=null){
				c1.close();
			}
		}
	}
}