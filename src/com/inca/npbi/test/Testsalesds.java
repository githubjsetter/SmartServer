package com.inca.npbi.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Category;

import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DBHelper;
import com.inca.np.util.SelectHelper;

/**
 * 测试生成销售单速度
 * 
 * @author user
 * 
 */
public class Testsalesds {
	Category logger=Category.getInstance(Testsalesds.class);
	String tablename = "npbi_test";
	HashMap<String, Columninfo> columnmap = new HashMap<String, Columninfo>();
	String startdate;
	String enddate;
	HashMap<String, String> keycolumnmap = new HashMap<String, String>();

	public Testsalesds() {
		super();
		//keycolumnmap.put("goodsid", "goodsid");
		keycolumnmap.put("customid", "customid");
	}

	public void clearTable(Connection con)throws Exception{
		DBHelper.executeSql(con, "truncate table "+tablename);
	}
	
	public void fetchBasetableinfo(Connection con) throws Exception {

		String sql = "select * from " + tablename + " where 1=2";
		PreparedStatement c1 = null;
		try {
			c1 = con.prepareStatement(sql);
			ResultSet rs = c1.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			for (int c = 0; c < rsm.getColumnCount(); c++) {
				Columninfo colinfo = new Columninfo();

				colinfo.colname = rsm.getColumnName(c + 1);
				colinfo.coltype = DBModel2Jdbc.getColumntype(rsm
						.getColumnType(c + 1));
				colinfo.precision = rsm.getPrecision(c + 1);
				colinfo.scale = rsm.getPrecision(c + 1);
				columnmap.put(colinfo.colname, colinfo);
			}
		} finally {
			if (c1 != null)
				c1.close();
		}
	}

	/**
	 * 取逻辑月信息
	 * 
	 * @param con1
	 * @throws Exception
	 */
	public void fetchUsemm(Connection con1) throws Exception {

		// 先查询pub_settle_account表,取出useyear usemonth startdate enddate放入基表中
		// 再从bms_sa_doc和bms_sa_dtl查询销售额,按品种汇总
		// 从当前成本价表查询成本价

		String sql = "";
		sql = "select useyear,usemonth,startdate,enddate from pub_settle_account_v "
				+ " where  useyear=2008 and usemonth=7 ";
		SelectHelper sh = new SelectHelper(sql);
		DBTableModel dm = sh.executeSelect(con1, 0, 1);
		if (dm.getRowCount() == 0)
			throw new Exception("没有找到逻辑月定义");
		startdate = dm.getItemValue(0, "startdate");
		enddate = dm.getItemValue(0, "enddate");

	}
	
	private void generalFetch(Connection con,Connection con1,ResultSet rs1,boolean createnewrecord)throws Exception{
		PreparedStatement c_base = null;
		PreparedStatement c_update = null;
		PreparedStatement c_ins = null;

		try{
		String sql = "select rowid  from " + tablename;

		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		Iterator<String> it = keycolumnmap.keySet().iterator();
		while (it.hasNext()) {
			String colname = it.next();
			Columninfo colinfo = columnmap.get(colname.toUpperCase());
			if (sb.length() > 0) {
				sb.append(" and ");
			}
			if (colinfo.coltype.equalsIgnoreCase("DATE")) {
				sb.append(colname + "=to_date(?,'yyyy-mm-dd hh24:mi:ss')");
			} else {
				sb.append(colname + "=?");
			}
		}
		sql += " where " + sb.toString();
		System.out.println(sql);
		c_base = con.prepareStatement(sql);

		ResultSetMetaData rsm = rs1.getMetaData();

		// 开始查询rs1
		int rowindex = 0;
		int colindex = 1;
		for (rowindex = 0; rs1.next(); rowindex++) {
			if((rowindex+1)%1000==0){
				logger.info("处理"+(rowindex+1)+"条记录");
				con.commit();
			}
			// 查询基表
			colindex = 1;
			it = keycolumnmap.keySet().iterator();
			while (it.hasNext()) {
				String colname = it.next();
				Columninfo colinfo = columnmap.get(colname.toUpperCase());
				String v = rs1.getString(colname);
				if (v == null)
					v = "";
				if (colinfo.coltype.equalsIgnoreCase("date")) {
					if (v.length() > 19)
						v = v.substring(0, 19);
				}
				c_base.setString(colindex++, v);
			}
			ResultSet rsbase = c_base.executeQuery();
			if (rsbase.next()) {
				// 进行update
				String rowid = rsbase.getString("rowid");
				if (c_update == null) {
					sb = new StringBuffer();
					// 新sql
					for (int c = 0; c < rsm.getColumnCount(); c++) {
						String colname = rsm.getColumnName(c + 1);
						if (columnmap.get(colname.toUpperCase()) == null)
							continue;
						String coltype = DBModel2Jdbc.getColumntype(rsm
								.getColumnType(c + 1));
						if (sb.length() > 0) {
							sb.append(",");
						}
						sb.append(colname);
						if (coltype.equalsIgnoreCase("date")) {
							sb
									.append("=to_date(?,'yyyy-mm-dd hh24:mi:ss')");
						} else {
							sb.append("=?");
						}
					}
					sql = "update " + tablename + " set " + sb.toString()
							+ " where rowid=?";
					System.out.println(sql);
					c_update = con.prepareStatement(sql);
				}

				// 绑定值
				colindex = 1;
				for (int c = 0; c < rsm.getColumnCount(); c++) {
					String colname = rsm.getColumnName(c + 1);
					if (columnmap.get(colname.toUpperCase()) == null)
						continue;
					String coltype = DBModel2Jdbc.getColumntype(rsm
							.getColumnType(c + 1));
					String v = rs1.getString(colname);
					if (v == null)
						v = "";
					if (coltype.equalsIgnoreCase("DATE")) {
						if (v.length() > 19)
							v = v.substring(0, 19);
					}
					c_update.setString(colindex++, v);
				}
				c_update.setString(colindex++, rowid);
				c_update.executeUpdate();

			} else if(createnewrecord) {
				// insert
				if (c_ins == null) {
					sb = new StringBuffer();
					sb1 = new StringBuffer();
					// 新sql
					for (int c = 0; c < rsm.getColumnCount(); c++) {
						String colname = rsm.getColumnName(c + 1);
						if (columnmap.get(colname.toUpperCase()) == null)
							continue;
						String coltype = DBModel2Jdbc.getColumntype(rsm
								.getColumnType(c + 1));
						if (sb.length() > 0) {
							sb.append(",");
							sb1.append(",");
						}
						sb.append(colname);
						if (coltype.equalsIgnoreCase("date")) {
							sb1
									.append("to_date(?,'yyyy-mm-dd hh24:mi:ss')");
						} else {
							sb1.append("?");
						}
					}

					sql = "insert into " + tablename + "(" + sb.toString()
							+ ")values(" + sb1.toString() + ")";
					System.out.println(sql);
					c_ins = con.prepareStatement(sql);
				}
				// 绑定值
				colindex = 1;
				for (int c = 0; c < rsm.getColumnCount(); c++) {
					String colname = rsm.getColumnName(c + 1);
					if (columnmap.get(colname.toUpperCase()) == null)
						continue;
					String coltype = DBModel2Jdbc.getColumntype(rsm
							.getColumnType(c + 1));
					String v = rs1.getString(colname);
					if (v == null)
						v = "";
					if (coltype.equalsIgnoreCase("DATE")) {
						if (v.length() > 19)
							v = v.substring(0, 19);
					}
					c_ins.setString(colindex++, v);
				}

				c_ins.executeUpdate();

			}
		}
		} finally {
			if (c_base != null) {
				c_base.close();
			}
			if (c_ins != null) {
				c_ins.close();
			}
			if (c_update != null) {
				c_update.close();
			}
		}

	}
	

	public void fetchSales(Connection con, Connection con1) throws Exception {
		// 查询基表.由基表中的列值做为条件
		PreparedStatement c_sales = null;

		try {
			String sql = "select customid,sum(goodsqty) goodsqty,sum(total_line) total_line from  \n"
					+ " bms_sa_doc ,bms_sa_dtl \n"
					+ " where bms_sa_doc.salesid=bms_sa_dtl.salesid \n"
					+ " and credate>to_date(?,'yyyy-mm-dd hh24:mi:ss') and credate<=to_date(?,'yyyy-mm-dd hh24:mi:ss') \n"
					+ " group by customid";
			c_sales = con1.prepareStatement(sql);

			c_sales.setString(1, startdate);
			c_sales.setString(2, enddate);
			ResultSet rs1 = c_sales.executeQuery();
			generalFetch(con, con1, rs1,true);
			

			con.commit();
		} finally {
			if (c_sales != null) {
				c_sales.close();
			}
		}

	}

	public void fetchSalesrec(Connection con, Connection con1) throws Exception {
		// 查询基表.由基表中的列值做为条件
		PreparedStatement c_sales = null;

		try {
			String sql = "select customid,sum(goodsqty) recgoodsqty,sum(total_line) recmoney from  \n"
					+ " bms_sa_rec_doc ,bms_sa_rec_dtl \n"
					+ " where bms_sa_rec_doc.sarecid=bms_sa_rec_dtl.sarecid \n"
					+ " and credate>to_date(?,'yyyy-mm-dd hh24:mi:ss') and credate<=to_date(?,'yyyy-mm-dd hh24:mi:ss') \n"
					+ " group by customid";
			c_sales = con1.prepareStatement(sql);

			c_sales.setString(1, startdate);
			c_sales.setString(2, enddate);
			ResultSet rs1 = c_sales.executeQuery();
			generalFetch(con, con1, rs1,true);
			

			con.commit();
		} finally {
			if (c_sales != null) {
				c_sales.close();
			}
		}

	}

	public void fetchGoodsname(Connection con, Connection con1) throws Exception {
		// 查询基表.由基表中的列值做为条件
		PreparedStatement c_goods = null;

		try {
			String sql = "select goodsid,goodsname from pub_goods ";
			c_goods = con1.prepareStatement(sql);

			//c_goods.setString(1, startdate);
			//c_goods.setString(2, enddate);
			ResultSet rs1 = c_goods.executeQuery();
			generalFetch(con, con1, rs1,false);
			

			con.commit();
		} finally {
			if (c_goods != null) {
				c_goods.close();
			}
		}

	}

	public void fetchCustom(Connection con, Connection con1) throws Exception {
		// 查询基表.由基表中的列值做为条件
		PreparedStatement c_goods = null;

		try {
			String sql = "select companyid customid,companyname customname from pub_company ";
			c_goods = con1.prepareStatement(sql);

			//c_goods.setString(1, startdate);
			//c_goods.setString(2, enddate);
			ResultSet rs1 = c_goods.executeQuery();
			generalFetch(con, con1, rs1,false);
			

			con.commit();
		} finally {
			if (c_goods != null) {
				c_goods.close();
			}
		}

	}

	class Columninfo {
		String colname, coltype;
		int precision;
		int scale;
	}

	static Connection getTestCon() throws Exception {
		String dbip = "192.9.200.47";
		String dbname = "orcl";
		String dbuser = "npserver";
		String dbpass = "npserver";

		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		con.setAutoCommit(false);
		return con;

	}

	static Connection getTestCon1() throws Exception {
		String dbip = "192.9.200.63";
		String dbname = "orcl";
		String dbuser = "cqcy080723";
		String dbpass = "cqcy080723";

		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		con.setAutoCommit(false);
		return con;

	}

	public static void main(String[] args) {
		Connection con = null;
		Connection con1 = null;
		try {
			con = getTestCon();
			con1 = getTestCon1();
			Category logger=Category.getInstance(Testsalesds.class);
			logger.debug("start");
			Testsalesds app = new Testsalesds();
			//app.clearTable(con);
			app.fetchBasetableinfo(con);
			
			//con1=con;
			
			app.fetchUsemm(con1);
			logger.debug("begin fetch sales");
			//app.fetchSales(con, con1);
			logger.debug("begin fetch sales rec");
			//app.fetchSalesrec(con, con1);
			logger.debug("begin fetch goodsname");
			//app.fetchGoodsname(con, con1);
			app.fetchCustom(con, con1);
			logger.debug("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
