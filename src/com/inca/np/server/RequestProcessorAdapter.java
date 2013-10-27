package com.inca.np.server;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Category;

import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DBModel2Jdbc;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;
import com.inca.npserver.dbcp.DBConnectPoolFactory;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-26 Time: 16:45:59
 * 请求处理器适配器。
 */
public class RequestProcessorAdapter implements RequestProcessIF {
	protected Category logger = Category
			.getInstance(RequestProcessorAdapter.class);

	/**
	 * 处理函数
	 * @param userinfo 当前用户信息
	 * @param req 客户端请求
	 * @param resp 服务器响应
	 */
	public int process(Userruninfo userinfo, ClientRequest req,
			ServerResponse resp) throws Exception {
		return 0; // To change body of implemented methods use File | Settings
		// | File Templates.
	}


	String dbip = DefaultNPParam.debugdbip;
	String dbname = DefaultNPParam.debugdbsid;
	String dbuser = DefaultNPParam.debugdbusrname;
	String dbpass = DefaultNPParam.debugdbpasswd;

	private Connection getTestCon() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, dbuser, dbpass);
		con.setAutoCommit(false);
		ServerContext svrcontext=ServerContext.getServercontext();
		JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
		return conwrap;
	}

	private Connection getTestSysCon() throws Exception {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		String url = "jdbc:oracle:thin:@" + dbip + ":1521:" + dbname;

		Connection con = DriverManager.getConnection(url, "system", "sys");
		con.setAutoCommit(false);
		ServerContext svrcontext=ServerContext.getServercontext();
		JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
		return conwrap;
	}

	protected String dburl = "java:comp/env/oracle/db";
	protected String sysdburl = "java:comp/env/oracle/sysdb";

	InitialContext ic = null;

	protected Connection getConnection() throws Exception {
		if (DefaultNPParam.debug == 1) {
			return getTestCon();
		} else {
/*			if (ic == null)
				ic = new InitialContext();
			DataSource ds = (DataSource) ic.lookup(dburl);
			Connection con = ds.getConnection();
*/			
			Connection con = DBConnectPoolFactory.getInstance().getConnection();
			con.setAutoCommit(false);
			ServerContext svrcontext=ServerContext.getServercontext();
			//logger.info("getconnection,svrcontext="+svrcontext);
			JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
			return conwrap;
		}
	}

	protected Connection getSysConnection() throws Exception {
		if (DefaultNPParam.debug == 1) {
			return getTestSysCon();
		} else {
			Connection con = DBConnectPoolFactory.getInstance().getSysconnection();
			con.setAutoCommit(false);
			ServerContext svrcontext=ServerContext.getServercontext();
			JdbcConnectWraper conwrap = new JdbcConnectWraper(svrcontext, con);
			return conwrap;
		}
	}

	protected File getWebapplicationDir() {
		String clsname = this.getClass().getName();
		int p = clsname.lastIndexOf(".");
		if (p > 0) {
			clsname = clsname.substring(p + 1);
		}
		URL url = this.getClass().getResource(clsname + ".class");
		//logger.debug(url);

		String strurl = url.toString();
		p = strurl.indexOf("/WEB-INF/");

		if (p < 0) {
			logger.error("找不到/WEB-INF/,返回c:/tomcat51/webapps/ngpcs");
			return new File("c:/tomcat51/webapps/ngpcs");
		}

		String s = strurl.substring(0, p);
		if (s.startsWith("jar:")) {
			s = s.substring(4);
		}

		if (s.startsWith("file:")) {
			s = s.substring(5);
		}

		//logger.debug("返回应用目录" + s);
		return new File(s);

	}

	/**
	 * 释放资源 2007 08 29
	 */
	public void release() {
	}


	protected void checkNotnull(Userruninfo runuserinfo, DBTableModel dbmodel,
			String colname, PrintWriter out) {
		for (int r = 0; r < dbmodel.getRowCount(); r++) {
			String v = dbmodel.getItemValue(r, colname);
			if (v == null || v.length() == 0) {
				out.println("字段" + colname + "不能为空");
			}
		}
	}


	/**
	 * 是否生成了临时出入库单表
	 */
	protected void checkBmsstouttmp(Connection con, Userruninfo runuserinfo,
			String comefrom, String sourcetable, DBTableModel dbmodel,
			String pkcolname, String goodsidcolname, String goodsqtycolname,
			PrintWriter out) {

		String sql = "select * from bms_st_io_doc_tmp where comefrom=? and sourcetable=? and  sourceid=?";
		PreparedStatement c1 = null;
		PreparedStatement c2 = null;
		try {
			c1 = con.prepareCall(sql);
			String pkid = dbmodel.getItemValue(0, pkcolname);
			c1.setString(1, comefrom);
			c1.setString(2, sourcetable);
			c1.setString(3, pkid);
			ResultSet rs = c1.executeQuery();
			DBTableModel stiodocmodel = DBModel2Jdbc.createFromRS(rs);
			if (stiodocmodel.getRowCount() == 0) {
				out.println("没有生成bms_st_io_doc_tmp记录,comefrom=" + comefrom
						+ ",sourcetable=" + sourcetable + ",sourceid=" + pkid);
				return;
			}

			// 进行检查
			String inoutid = stiodocmodel.getItemValue(0, "inoutid");
			String credate = stiodocmodel.getItemValue(0, "credate");
			String companyid = stiodocmodel.getItemValue(0, "companyid");
			String storageid = stiodocmodel.getItemValue(0, "storageid");
			String inoutflag = stiodocmodel.getItemValue(0, "inoutflag");
			String outqty = stiodocmodel.getItemValue(0, "outqty");
			String oldqty = stiodocmodel.getItemValue(0, "oldqty");
			String usestatus = stiodocmodel.getItemValue(0, "usestatus");
			String entryid = stiodocmodel.getItemValue(0, "entryid");

			String recmsg = "生成bms_st_io_doc_tmp记录,comefrom=" + comefrom
					+ ",sourcetable=" + sourcetable + ",sourceid=" + pkid;

			if (credate == null || credate.length() == 0) {
				out.println(recmsg + ",credate为空");
			}
			if (runuserinfo.getPlacepointid().compareTo(companyid) != 0) {
				out.println(recmsg + ",companyid不正确,应该是"
						+ runuserinfo.getPlacepointid());
			}
			if (runuserinfo.getStorageid().compareTo(storageid) != 0) {
				out.println(recmsg + ",storageid不正确,应该是"
						+ runuserinfo.getStorageid());
			}
			if (!inoutflag.equals("0")) {
				out.println(recmsg + ",inoutflag不正确,应该是0");
			}
			if (!usestatus.equals("1")) {
				out.println(recmsg + ",usestatus不正确,应该是1");
			}

			// 检查数量
			if (!outqty.equals(oldqty)) {
				out.println(recmsg + ",outqty应该等于oldqty");
			}
			String goodsqty = dbmodel.getItemValue(0, goodsqtycolname);
			if (!outqty.equals(goodsqty)) {
				out.println(recmsg + ",outqty应该" + goodsqty);
			}

			// 检查细单
			sql = "select * from bms_st_io_dtl_tmp where inoutid=?";
			c2 = con.prepareCall(sql);
			c2.setString(1, inoutid);
			rs = c2.executeQuery();
			DBTableModel stiodtlmodel = DBModel2Jdbc.createFromRS(rs);
			if (stiodtlmodel.getRowCount() == 0) {
				out.println(recmsg + ",没有生成细单");

			} else {
				for (int dtlr = 0; dtlr < stiodtlmodel.getRowCount(); dtlr++) {
					String batchid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String lotid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String posid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String goodsdtlid = stiodtlmodel.getItemValue(dtlr,
							"goodsdtlid");
					String dtlgoodsqty = stiodtlmodel.getItemValue(dtlr,
							"dtlgoodsqty");

					if (batchid.length() == 0) {
						out.println(recmsg + ",sourceid=" + pkid
								+ ",细单batchid为空");
					}
					if (lotid.length() == 0) {
						out
								.println(recmsg + ",sourceid=" + pkid
										+ ",细单lotid为空");
					}
					if (posid.length() == 0) {
						out
								.println(recmsg + ",sourceid=" + pkid
										+ ",细单posid为空");
					}
					if (goodsdtlid.length() == 0) {
						out.println(recmsg + ",sourceid=" + pkid
								+ ",细单goodsdtlid为空");
					}
					if (!dtlgoodsqty.equals(goodsqty)) {
						out.println(recmsg + ",细单dtlgoodsqty不对,应该为" + goodsqty);
					}
				}
			}

		} catch (Exception e) {
			logger.error("自检ERROR", e);
			return;
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
					logger.error("ERROR", e);
				}
			}
			if (c2 != null) {
				try {
					c2.close();
				} catch (SQLException e) {
					logger.error("ERROR", e);
				}
			}
		}

	}


	/**
	 * 是否生成了临时出入库单表,并记了帐
	 */
	protected void checkBmsstout(Connection con, Userruninfo runuserinfo,
			String comefrom, String sourcetable, DBTableModel dbmodel,
			String pkcolname, String goodsidcolname, String goodsqtycolname,
			PrintWriter out) {

		String sql = "select * from bms_st_io_doc where comefrom=? and sourcetable=? and  sourceid=?";
		PreparedStatement c1 = null;
		PreparedStatement c2 = null;
		try {
			c1 = con.prepareCall(sql);
			String pkid = dbmodel.getItemValue(0, pkcolname);
			c1.setString(1, comefrom);
			c1.setString(2, sourcetable);
			c1.setString(3, pkid);
			ResultSet rs = c1.executeQuery();
			DBTableModel stiodocmodel = DBModel2Jdbc.createFromRS(rs);
			if (stiodocmodel.getRowCount() == 0) {
				out.println("没有生成bms_st_io_doc记录,comefrom=" + comefrom
						+ ",sourcetable=" + sourcetable + ",sourceid=" + pkid);
				return;
			}

			// 进行检查
			String inoutid = stiodocmodel.getItemValue(0, "inoutid");
			String credate = stiodocmodel.getItemValue(0, "credate");
			String companyid = stiodocmodel.getItemValue(0, "companyid");
			String storageid = stiodocmodel.getItemValue(0, "storageid");
			String inoutflag = stiodocmodel.getItemValue(0, "inoutflag");
			String outqty = stiodocmodel.getItemValue(0, "outqty");
			String oldqty = stiodocmodel.getItemValue(0, "oldqty");
			String usestatus = stiodocmodel.getItemValue(0, "usestatus");
			String entryid = stiodocmodel.getItemValue(0, "entryid");

			String recmsg = "生成bms_st_io_doc_tmp记录,comefrom=" + comefrom
					+ ",sourcetable=" + sourcetable + ",sourceid=" + pkid;

			if (credate == null || credate.length() == 0) {
				out.println(recmsg + ",credate为空");
			}
			if (runuserinfo.getPlacepointid().compareTo(companyid) != 0) {
				out.println(recmsg + ",companyid不正确,应该是"
						+ runuserinfo.getPlacepointid());
			}
			if (runuserinfo.getStorageid().compareTo(storageid) != 0) {
				out.println(recmsg + ",storageid不正确,应该是"
						+ runuserinfo.getStorageid());
			}
			if (!inoutflag.equals("0")) {
				out.println(recmsg + ",inoutflag不正确,应该是0");
			}
			if (!usestatus.equals("2")) {
				out.println(recmsg + ",usestatus不正确,应该是2");
			}

			// 检查数量
			if (!outqty.equals(oldqty)) {
				out.println(recmsg + ",outqty应该等于oldqty");
			}
			String goodsqty = dbmodel.getItemValue(0, goodsqtycolname);
			if (!outqty.equals(goodsqty)) {
				out.println(recmsg + ",outqty应该" + goodsqty);
			}

			// 检查细单
			sql = "select * from bms_st_io_dtl where inoutid=?";
			c2 = con.prepareCall(sql);
			c2.setString(1, inoutid);
			rs = c2.executeQuery();
			DBTableModel stiodtlmodel = DBModel2Jdbc.createFromRS(rs);
			if (stiodtlmodel.getRowCount() == 0) {
				out.println(recmsg + ",没有生成细单");

			} else {
				for (int dtlr = 0; dtlr < stiodtlmodel.getRowCount(); dtlr++) {
					String batchid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String lotid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String posid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String goodsdtlid = stiodtlmodel.getItemValue(dtlr,
							"goodsdtlid");
					String dtlgoodsqty = stiodtlmodel.getItemValue(dtlr,
							"dtlgoodsqty");

					if (batchid.length() == 0) {
						out.println(recmsg + ",sourceid=" + pkid
								+ ",细单batchid为空");
					}
					if (lotid.length() == 0) {
						out
								.println(recmsg + ",sourceid=" + pkid
										+ ",细单lotid为空");
					}
					if (posid.length() == 0) {
						out
								.println(recmsg + ",sourceid=" + pkid
										+ ",细单posid为空");
					}
					if (goodsdtlid.length() == 0) {
						out.println(recmsg + ",sourceid=" + pkid
								+ ",细单goodsdtlid为空");
					}
					if (!dtlgoodsqty.equals(goodsqty)) {
						out.println(recmsg + ",细单dtlgoodsqty不对,应该为" + goodsqty);
					}
				}
			}

		} catch (Exception e) {
			logger.error("自检ERROR", e);
			return;
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
					logger.error("ERROR", e);
				}
			}
			if (c2 != null) {
				try {
					c2.close();
				} catch (SQLException e) {
					logger.error("ERROR", e);
				}
			}
		}

	}

	/**
	 * 是否生成了入库单表
	 */
	protected void checkBmsstin(Connection con,Userruninfo runuserinfo, String comefrom,
			String sourcetable, DBTableModel dbmodel, String pkcolname,
			String goodsidcolname, String goodsqtycolname, PrintWriter out) {

		String sql = "select * from bms_st_io_doc where comefrom=? and sourcetable=? and  sourceid=?";
		PreparedStatement c1 = null;
		PreparedStatement c2 = null;
		try {
			c1 = con.prepareCall(sql);
			String pkid = dbmodel.getItemValue(0, pkcolname);
			c1.setString(1, comefrom);
			c1.setString(2, sourcetable);
			c1.setString(3, pkid);
			ResultSet rs = c1.executeQuery();
			DBTableModel stiodocmodel = DBModel2Jdbc.createFromRS(rs);
			if (stiodocmodel.getRowCount() == 0) {
				out.println("没有生成bms_st_io_doc记录,comefrom=" + comefrom
						+ ",sourcetable=" + sourcetable + ",sourceid=" + pkid);
				return;
			}

			// 进行检查
			String inoutid = stiodocmodel.getItemValue(0, "inoutid");
			String credate = stiodocmodel.getItemValue(0, "credate");
			String companyid = stiodocmodel.getItemValue(0, "companyid");
			String storageid = stiodocmodel.getItemValue(0, "storageid");
			String inoutflag = stiodocmodel.getItemValue(0, "inoutflag");
			String inqty = stiodocmodel.getItemValue(0, "inqty");
			String usestatus = stiodocmodel.getItemValue(0, "usestatus");
			String entryid = stiodocmodel.getItemValue(0, "entryid");

			String recmsg = "生成bms_st_io_doc_tmp记录,comefrom=" + comefrom
					+ ",sourcetable=" + sourcetable + ",sourceid=" + pkid;

			if (credate == null || credate.length() == 0) {
				out.println(recmsg + ",credate为空");
			}
			if (runuserinfo.getPlacepointid().compareTo(companyid) != 0) {
				out.println(recmsg + ",companyid不正确,应该是"
						+ runuserinfo.getPlacepointid());
			}
			if (runuserinfo.getStorageid().compareTo(storageid) != 0) {
				out.println(recmsg + ",storageid不正确,应该是"
						+ runuserinfo.getStorageid());
			}
			if (!inoutflag.equals("1")) {
				out.println(recmsg + ",inoutflag不正确,应该是1");
			}
			/*
			 * if (!usestatus.equals("1")) { out.println(recmsg +
			 * ",usestatus不正确,应该是1"); }
			 */
			// 检查数量
			String goodsqty = dbmodel.getItemValue(0, goodsqtycolname);
			if (!inqty.equals(goodsqty)) {
				out.println(recmsg + ",inqty应该" + goodsqty);
			}

			// 检查细单
			sql = "select * from bms_st_io_dtl where inoutid=?";
			c2 = con.prepareCall(sql);
			c2.setString(1, inoutid);
			rs = c2.executeQuery();
			DBTableModel stiodtlmodel = DBModel2Jdbc.createFromRS(rs);
			if (stiodtlmodel.getRowCount() == 0) {
				out.println(recmsg + ",没有生成细单");

			} else {
				for (int dtlr = 0; dtlr < stiodtlmodel.getRowCount(); dtlr++) {
					String batchid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String lotid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String posid = stiodtlmodel.getItemValue(dtlr, "batchid");
					String goodsdtlid = stiodtlmodel.getItemValue(dtlr,
							"goodsdtlid");
					String dtlgoodsqty = stiodtlmodel.getItemValue(dtlr,
							"dtlgoodsqty");

					if (batchid.length() == 0) {
						out.println(recmsg + ",sourceid=" + pkid
								+ ",细单batchid为空");
					}
					if (lotid.length() == 0) {
						out
								.println(recmsg + ",sourceid=" + pkid
										+ ",细单lotid为空");
					}
					if (posid.length() == 0) {
						out
								.println(recmsg + ",sourceid=" + pkid
										+ ",细单posid为空");
					}
					if (goodsdtlid.length() == 0) {
						out.println(recmsg + ",sourceid=" + pkid
								+ ",细单goodsdtlid为空");
					}
					if (!dtlgoodsqty.equals(goodsqty)) {
						out.println(recmsg + ",细单dtlgoodsqty不对,应该为" + goodsqty);
					}
				}
			}

		} catch (Exception e) {
			logger.error("自检ERROR", e);
			return;
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
					logger.error("ERROR", e);
				}
			}
			if (c2 != null) {
				try {
					c2.close();
				} catch (SQLException e) {
					logger.error("ERROR", e);
				}
			}
		}

	}

	
}
