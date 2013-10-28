package com.smart.server.servermanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.adminclient.fullscan.Fullscan_dbprocess;
import com.smart.adminclient.remotesql.Generalsave_dbprocess;
import com.smart.adminclient.remotesql.Remotesql_dbprocess;
import com.smart.adminclient.serverinfo.Listlogin_dbprocess;
import com.smart.adminclient.serverinfo.Serverinfo_dbprocess;
import com.smart.adminclient.serverinfo.Session_dbprocess;
import com.smart.adminclient.serverinfo.Sessionlock_dbprocess;
import com.smart.adminclient.serverinfo.Sqlmonitor_dbprocess;
import com.smart.adminclient.serverinfo.Tablespace_dbprocess;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.server.RequestProcessIF;
import com.smart.platform.server.ServerContext;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.StringUtil;
import com.smart.server.dbcp.DBConnectPoolFactory;
import com.smart.server.dbcp.ListdbcpDbprocessor;
import com.smart.server.dbcp.SavedbcpProcessor;
import com.smart.server.server.sysproc.GetloggerinfoProcessor;
import com.smart.server.server.sysproc.LoggerfiledownloadProcessor;
import com.smart.server.server.sysproc.SelectProcessor;

/**
 * ���ڹ�������Ĵ��� 1 admin��¼ 2 ��Ʒ��װ 3 ��Ʒ����
 * 
 * @author Administrator
 * 
 */
public class AdminRequestDispatcher {

	Category logger = Category.getInstance(AdminRequestDispatcher.class);
	Vector<RequestProcessIF> processortable = new Vector<RequestProcessIF>();

	private static HashMap notneedautocmds = new HashMap();
	static {
		notneedautocmds.put("��ѯϵͳѡ���ֵ�", "��ѯϵͳѡ���ֵ�");
	}

	protected boolean isNoautocmd(String cmd) {
		return notneedautocmds.get(cmd) != null;
	}

	public Enumeration<RequestProcessIF> getProcessors() {
		return processortable.elements();
	}

	void loadprocessor() {
		processortable.add(new AdminloginProcessor());
		processortable.add(new GetprodinfoProcessor());
		processortable.add(new UploadlicensefileProcessor());
		processortable.add(new UploadModulefileProcessor());

		processortable.add(new Fullscan_dbprocess());
		processortable.add(new Fullscan_dbprocess());

		processortable.add(new Generalsave_dbprocess());
		processortable.add(new Remotesql_dbprocess());
		processortable.add(new Listlogin_dbprocess());
		processortable.add(new Serverinfo_dbprocess());
		processortable.add(new Session_dbprocess());
		processortable.add(new Sessionlock_dbprocess());
		processortable.add(new Sqlmonitor_dbprocess());
		processortable.add(new Tablespace_dbprocess());
		processortable.add(new SelectProcessor());

		processortable.add(new ListdbcpDbprocessor());
		processortable.add(new SavedbcpProcessor());
		processortable.add(new AdminrepasswdProcessor());
		processortable.add(new SetsystempasswordProcessor());


		//ȡ��־��Ϣ
		processortable.add(new GetloggerinfoProcessor());
		//������־�ļ�
		processortable.add(new LoggerfiledownloadProcessor());
		
		//�û�sql���
		processortable.add(new UsersqlmonitorDbprocessor());

		//����������
		processortable.add(new ServerperformProcessor());

	}

	private AdminRequestDispatcher() {

	}

	private static AdminRequestDispatcher inst = null;

	public static synchronized AdminRequestDispatcher getInstance() {
		if (inst == null) {
			inst = new AdminRequestDispatcher();
			inst.loadprocessor();
		}
		return inst;
	}

	public ServerResponse process(ClientRequest req) {
		ServerResponse svrresp = new ServerResponse();
		String employeeid = "";
		String employeename = "";
		Userruninfo userinfo = AdminManager.authUser(req);
		logger.info("��֤:" + req.getAuthstring() + ",userinfo=" + userinfo);
		if (userinfo != null) {
			userinfo.dump(logger);
		}
		if (userinfo == null) {
			// �û���ʱ��δ��¼

			if (req.commandAt(0) instanceof StringCommand) {
				StringCommand cmd1 = (StringCommand) req.commandAt(0);
				if (cmd1.getString().indexOf("login") < 0) {
					// �Ƿ��û�

					if (DefaultNPParam.debug == 1
							|| isNoautocmd(cmd1.getString())) {
						logger.warn("�û�δ��֤��Ϊ�˵�������userid=128,deptid=6");
						userinfo = new Userruninfo();
						// ////////////����,�����ǲ���Ҫ��֤��
						userinfo.setUserid("128");
						userinfo.setUsername("����");
						userinfo.setDeptid("6");
					} else {
						svrresp.addCommand(new StringCommand(
								"-ERROR:δ��¼��ʱ�������µ�¼"));
						return svrresp;
					}
				} else {
					try {
						ParamCommand cmd2 = (ParamCommand) req.commandAt(1);
						employeeid = cmd2.getValue("userid");
					} catch (Exception e) {
					}
				}
			}

		}

		if (userinfo != null) {
			employeeid = userinfo.getUserid();
			employeename = userinfo.getUsername();
			userinfo.setLastaccesstime(System.currentTimeMillis());
			userinfo.setRemoteip(req.getRemoteip());
			/*
			 * if (req.getMsgid().startsWith("selfcheck")) {
			 * userinfo.setDevelop(true); }
			 */}

		int cmdct = req.getCommandcount();
		String command = "";
		if (cmdct > 0) {
			CommandBase cmd0 = req.commandAt(0);
			if (cmd0 instanceof StringCommand) {
				logger.info("���й�" + cmdct + "������,��0��������\""
						+ ((StringCommand) cmd0).getString() + "\"");
				command = ((StringCommand) cmd0).getString();
			} else {
				logger.info("���й�" + cmdct + "������,��0����������"
						+ cmd0.getClass().getName());
			}
		}

		long starttime = System.currentTimeMillis();
		long endtime = 0;
		String errormsg = "";
		Enumeration<RequestProcessIF> en = processortable.elements();
		int retcode = -1;

		while (en.hasMoreElements()) {
			RequestProcessIF processor = en.nextElement();
			ServerContext context = new ServerContext(command);
			context.setUserinfo(userinfo);
			ServerContext.regServercontext(context);
			try {
				retcode = processor.process(userinfo, req, svrresp);
			} catch (Exception e) {
				logger.error("RequestDisatch ����"
						+ processor.getClass().getName() + " ERROR", e);
				svrresp
						.addCommand(new StringCommand("-ERROR:"
								+ e.getMessage()));
				retcode = -1;
				errormsg = e.getMessage();
				endtime = System.currentTimeMillis();
				log(employeeid, employeename, DefaultNPParam.prodcontext, req
						.getRemoteip(), command, retcode, errormsg, endtime
						- starttime);

				return svrresp;
			} finally {
				processor.release();
				ServerContext.releaseServercontext();
			}

			if (retcode == 0) {
				// ����ɹ���
				logger.info("��������,��������:" + processor.getClass().getName());
				break;
			}
		}

		if (retcode == -1) {
			StringCommand errorcmd = new StringCommand("-ERROR:�޷����������");
			errormsg = errorcmd.getString();
			svrresp.addCommand(errorcmd);
		}
		endtime = System.currentTimeMillis();

		StringCommand respcmd = null;
		if (svrresp.commandAt(0) instanceof StringCommand) {
			respcmd = (StringCommand) svrresp.commandAt(0);
			if (respcmd.getString().startsWith("-ERROR")) {
				retcode = -1;
				errormsg = respcmd.getString();
			}
		}
		if (respcmd != null && command.indexOf("login") >= 0
				&& respcmd.getString().startsWith("+OK")) {
			// ����ǵ�¼,���ҳ��û�
			ParamCommand pcmd = (ParamCommand) svrresp.commandAt(1);
			employeename = pcmd.getValue("username");
		}

		log(employeeid, employeename, DefaultNPParam.prodcontext, req
				.getRemoteip(), command, retcode, errormsg, endtime - starttime);
		return svrresp;
	}

	public void addProcesses(RequestProcessIF processor) {
		processortable.add(processor);
	}

	void log(String employeeid, String employeename, String prodname,
			String remoteip, String command, int result, String errormsg,
			long usetime) {
		Connection con = null;
		PreparedStatement c1 = null;
		String sql = "insert into np_log(Seqid,credate,employeeid,employeename,"
				+ "Prodname,Remoteip,command,result,errormsg,usetime)values(np_log_seq.nextval,"
				+ "sysdate,?,?," + "?,?,?,?,?,?)";
		try {
			con = getConnection();
			int col = 1;
			c1 = con.prepareStatement(sql);
			c1.setString(col++, employeeid);
			c1.setString(col++, StringUtil.max(employeename, 20));
			c1.setString(col++, StringUtil.max(prodname, 20));
			c1.setString(col++, StringUtil.max(remoteip, 15));
			c1.setString(col++, StringUtil.max(command, 40));
			c1.setInt(col++, result);
			c1.setString(col++, StringUtil.max(errormsg, 40));
			c1.setLong(col++, usetime);
			c1.executeUpdate();
			con.commit();
		} catch (Exception e) {
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException e1) {
			}
			logger.error("ERROR", e);
		} finally {
			if (c1 != null) {
				try {
					c1.close();
				} catch (SQLException e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}

	}

	protected String dburl = "java:comp/env/oracle/db";

	protected Connection getConnection() throws Exception {
		if (DefaultNPParam.debug == 1) {
			return getTestCon();
		} else {
			/*
			 * InitialContext ic = new InitialContext(); DataSource ds =
			 * (DataSource) ic.lookup(dburl);
			 */return DBConnectPoolFactory.getInstance().getConnection();

		}
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
		return con;

	}
}
