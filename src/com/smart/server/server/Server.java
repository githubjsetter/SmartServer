package com.smart.server.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Category;

import com.smart.platform.anyprint.impl.BuilddatasourceProcessor;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.UserManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.demo.mde.Goodsdtl_dbprocess;
import com.smart.platform.demo.mmde.Goodsvariety_dbprocess;
import com.smart.platform.demo.mste.Pubfactory_dbprocess;
import com.smart.platform.demo.mste.Pubgoodsdetail_dbprocess;
import com.smart.platform.demo.server.Pub_goods_dbprocess;
import com.smart.platform.demo.server.pub_factory_dbprocess;
import com.smart.platform.fileserver.Deletefiledbprocessor;
import com.smart.platform.fileserver.FileBrowseProcessor;
import com.smart.platform.filesync.Download_dbprocess;
import com.smart.platform.filesync.Uploadfile_dbprocess;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.server.RequestProcessIF;
import com.smart.platform.server.ServerContext;
import com.smart.platform.server.process.Pingprocessor;
import com.smart.platform.server.process.Sysddl_dbprocess;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.StringUtil;
import com.smart.server.dbcp.DBConnectPoolFactory;
import com.smart.server.log.NpLogManager;
import com.smart.server.prod.LicenseManager;
import com.smart.server.prod.Licenseinfo;
import com.smart.server.server.sysproc.CreatestoreprocProcessor;
import com.smart.server.server.sysproc.CurrentappHelper;
import com.smart.server.server.sysproc.DownloadLauncherProcessor;
import com.smart.server.server.sysproc.FillprintnoProcessor;
import com.smart.server.server.sysproc.GetentryDbprocessor;
import com.smart.server.server.sysproc.GetplacepointDbprocessor;
import com.smart.server.server.sysproc.GetroleoplistProcessor;
import com.smart.server.server.sysproc.LoginProcessor;
import com.smart.server.server.sysproc.MacrequestProcessor;
import com.smart.server.server.sysproc.MdegeneralProcessor;
import com.smart.server.server.sysproc.ModuledownloadProcessor;
import com.smart.server.server.sysproc.ModulefiledownloadProcessor;
import com.smart.server.server.sysproc.PrequeryspProcessor;
import com.smart.server.server.sysproc.Printplandownload_dbprocess;
import com.smart.server.server.sysproc.Repassword_dbprocessor;
import com.smart.server.server.sysproc.SelectProcessor;
import com.smart.server.server.sysproc.SetentryProcessor;
import com.smart.server.server.sysproc.SetplacepointProcessor;
import com.smart.server.server.sysproc.SetroleProcessor;
import com.smart.server.server.sysproc.StegeneralProcessor;
import com.smart.server.server.sysproc.TestlongtimesqlProcessor;
import com.smart.server.server.sysproc.UploadPrintplan_dbprocess;
import com.smart.server.server.sysproc.ZxhovdownloadProcessor;
import com.smart.server.server.sysproc.Zxmodifyinstall_dbprocessor;
import com.smart.server.server.sysproc.ZxzipdownloadProcessor;
import com.smart.sysmgr.server.ApbatchsaveProcessor;
import com.smart.sysmgr.server.ApqueryProcessor;
import com.smart.sysmgr.server.ApsaveProcessor;
import com.smart.sysmgr.server.HovapqueryProcessor;
import com.smart.sysmgr.server.HovapsaveProcessor;
import com.smart.workflow.server.Approvestatus_dbprocess;
import com.smart.workflow.server.Dataitemedit_dbprocessor;
import com.smart.workflow.server.Feedemo_dbprocess;
import com.smart.workflow.server.Feedemoconfirm_dbprocessor;
import com.smart.workflow.server.Feelimit_dbprocess;
import com.smart.workflow.server.Fetchnodedata_dbprocessor;
import com.smart.workflow.server.Fetchnodeinst_dbprocessor;
import com.smart.workflow.server.Humanapprove_dbprocessor;
import com.smart.workflow.server.Nodeinstdata_dbprocessor;
import com.smart.workflow.server.Wfcheck_dbprocessor;
import com.smart.workflow.server.Wfdefine_dbprocess;
import com.smart.workflow.server.Wfinst_dbprocess;
import com.smart.workflow.server.Wfnodeemp_dbprocess;
import com.smart.workflow.server.Wfnoderole_dbprocess;

public class Server {

	Category logger = Category.getInstance(Server.class);
	HashMap<String, RequestProcessIF> processorcache = new HashMap<String, RequestProcessIF>();
	HashMap<String, String> processorclazzcache = new HashMap<String, String>();

	/**
	 * �������
	 */
	private long requestcount = 0;
	/**
	 * �ɹ��������
	 */
	private long processcount = 0;

	/**
	 * ��ʼʱ��
	 */
	private long svrstarttime = System.currentTimeMillis();
	/**
	 * �������ú����ۼ�
	 */
	private long processms = 0;

	private static HashMap<String, String> notneedautocmds = new HashMap<String, String>();
	static {
		notneedautocmds.put("��ѯϵͳѡ���ֵ�", "��ѯϵͳѡ���ֵ�");
		notneedautocmds.put("npclient:macrequest", "npclient:macrequest");
		notneedautocmds.put("npclient:downloadlaunchers",
				"npclient:downloadlaunchers");
		notneedautocmds.put("npclient:downloadmodules",
				"npclient:downloadmodules");
		notneedautocmds.put("npclient:downloadmodulefile",
				"npclient:downloadmodulefile");
		notneedautocmds.put("npclient:testlongtimesql",
				"npclient:testlongtimesql");
		notneedautocmds.put("nptest:sleep", "nptest:sleep");
	}

	private Server() {
		scanLicensefile();
	}

	static boolean isNoautocmd(String cmd) {
		return notneedautocmds.get(cmd) != null;
	}

	private static Server inst = null;

	public static synchronized Server getInstance() {
		if (inst == null) {
			inst = new Server();
			// ������־
			NpLogManager.getInstance();
			inst.loadSystemprocessor();
		}
		return inst;
	}

	/**
	 * ����ϵͳ�ڲ�ʹ�õķ���
	 */
	void loadSystemprocessor() {
		processorcache.put("npclient:downloadlaunchers",
				new DownloadLauncherProcessor());
		processorcache.put("npclient:downloadmodules",
				new ModuledownloadProcessor());
		processorcache.put("npclient:downloadmodulefile",
				new ModulefiledownloadProcessor());
		processorcache.put("npclient:login", new LoginProcessor());
		processorcache.put("npclient:��������", new Repassword_dbprocessor());
		processorcache.put("npclient:getroleoplist",
				new GetroleoplistProcessor());
		processorcache.put("npclient:setroleid", new SetroleProcessor());
		processorcache.put("select", new SelectProcessor());
		processorcache.put("ping", new Pingprocessor());
		processorcache.put("��ѯϵͳѡ���ֵ�", new Sysddl_dbprocess());
		processorcache.put("np:uploadfile", new Uploadfile_dbprocess());
		processorcache.put("np:deletefile", new Deletefiledbprocessor());
		processorcache.put("np:browsefilegroup", new FileBrowseProcessor());
		processorcache.put("np:download", new Download_dbprocess());
		processorcache.put("npclient:setplacepointid",
				new SetplacepointProcessor());

		processorcache.put("np:��ѯ��Ȩ����", new ApqueryProcessor());
		processorcache.put("np:������Ȩ����", new ApsaveProcessor());
		processorcache.put("np:����������Ȩ����", new ApbatchsaveProcessor());
		processorcache.put("np:��ѯHOV��Ȩ����", new HovapqueryProcessor());
		processorcache.put("np:����HOV��Ȩ����", new HovapsaveProcessor());
		processorcache.put("npclient:macrequest", new MacrequestProcessor());
		processorcache.put("npclient:downloadzxzip",
				new ZxzipdownloadProcessor());
		processorcache
				.put("npdev:��װר�����ZIP", new Zxmodifyinstall_dbprocessor());
		processorcache.put("npclient:createstoreproc",
				new CreatestoreprocProcessor());
		processorcache.put("npclient:execprequerystoreproc",
				new PrequeryspProcessor());
		processorcache.put("npclient:����ר��HOV", new ZxhovdownloadProcessor());
		processorcache.put("npclient:testlongtimesql",
				new TestlongtimesqlProcessor());

		// ȡ�ŵ�
		processorcache.put("npclient:getplacepointid",
				new GetplacepointDbprocessor());
		// ȡ���㵥Ԫ
		processorcache.put("npclient:getentry", new GetentryDbprocessor());
		processorcache.put("npclient:setentryid", new SetentryProcessor());

		// ��ӡ����Դȡ��
		processorcache.put("npclient:builddatasource",
				new BuilddatasourceProcessor());
		processorcache.put("npclient:fetchdatasource",
				new BuilddatasourceProcessor());
		processorcache.put("npclient:�ϴ���ӡ����", new UploadPrintplan_dbprocess());
		processorcache
				.put("npclient:���ش�ӡ����", new Printplandownload_dbprocess());
		processorcache
				.put("npclient:�г���ӡ����", new Printplandownload_dbprocess());
		processorcache.put("npclient:��д��ӡ����", new FillprintnoProcessor());

		// ////////for mste demo only
		processorcache.put("com.smart.np.demo.mste.Pub_goods_ste.�����Ʒ",
				new com.smart.platform.demo.mste.Pub_goods_dbprocess());
		processorcache.put("com.smart.np.demo.mste.Pubfactory_ste.���泧��",
				new Pubfactory_dbprocess());
		processorcache.put("com.smart.np.demo.mste.Pubgoodsdetail_ste.�����Ʒ��ϸ",
				new Pubgoodsdetail_dbprocess());
		processorcache.put("���浥λ����", new pub_factory_dbprocess());
		processorcache.put("Goodsdtl_mde.�����Ʒ����ϸ", new Goodsdtl_dbprocess());
		processorcache.put("demo.ste.Pub_goods_ste.�����Ʒ",
				new Pub_goods_dbprocess());
		processorcache.put("npserver.demo.����Ʒ��", new Goodsvariety_dbprocess());

		// ���湤�� ���̶���
		processorcache.put("Wfdefine_mde.�������̶���", new Wfdefine_dbprocess());
		processorcache.put("npworkflow:������̱��ʽ", new Wfcheck_dbprocessor());
		processorcache.put("Dataitemedit_ste.�����������",
				new Dataitemedit_dbprocessor());
		processorcache.put("Nodeinstdata_ste.�������������",
				new Nodeinstdata_dbprocessor());
		processorcache.put("Approvestatus_ste.��������״̬",
				new Approvestatus_dbprocess());
		processorcache.put("Wfnoderole_ste.�������ɫ", new Wfnoderole_dbprocess());
		processorcache.put("Wfnodeemp_ste.��������Ա", new Wfnodeemp_dbprocess());

		// ���湤������ʵ��
		processorcache.put("Wfinst_mde.��������ʵ��", new Wfinst_dbprocess());

		// ��ѯ������
		processorcache.put("npserver:��ѯ������", new Fetchnodeinst_dbprocessor());
		// ��ѯ�������.
		processorcache
				.put("npserver:��ѯ������������", new Fetchnodedata_dbprocessor());

		// �������˹�����
		processorcache.put("npserver:����������", new Humanapprove_dbprocessor());

		// demo����
		processorcache.put("npworkflow.demo.Feedemo_ste.�������",
				new Feedemo_dbprocess());
		processorcache.put("npworkflow.demo.Feedemo.�ύ",
				new Feedemoconfirm_dbprocessor());
		processorcache.put("com.smart.workflow.demo.Feelimit_ste.��������޶�",
				new Feelimit_dbprocess());

		// /////////////BI
		// ����bi������
		processorclazzcache.put("Report_ste.���汨����",
				"com.smart.bi.server.Report_dbprocess");
		// ����bi�����ж���
		processorclazzcache.put("Tablecolumn_ste.��������ж���",
				"com.smart.bi.server.Tablecolumn_dbprocess");
		// ����bi��ͼ����
		processorclazzcache.put("View_ste.������ͼ",
				"com.smart.bi.server.View_dbprocess");
		// ����biʵ��
		processorclazzcache.put("Instance_ste.����ʵ��",
				"com.smart.bi.server.Instance_dbprocess");
		// ����bi datasource ����
		processorclazzcache.put("Ds_ste.��������Դ����",
				"com.smart.bi.server.Ds_dbprocess");
		// ���bi ������
		processorclazzcache.put("npbi.��鱨����",
				"com.smart.bi.server.ReportChecker_dbprocessor");
		// ���㱨��
		processorclazzcache.put("npbi.���㱨��ʵ��",
				"com.smart.bi.server.Calcreport_dbprocessor");
		// ȡԤ��dbmodel
		processorclazzcache.put("npbi:ȡ����Ԥ��dbtablemodel",
				"com.smart.bi.server.GetBIreportdm_dbprocessor");
		// ��������bi����instance
		processorclazzcache.put("npbi:��������instance",
				"com.smart.bi.server.Batchgeninstance_dbprocessor");

		// �ϴ�bi����
		processorclazzcache.put("npclient:�ϴ�bi����",
				"com.smart.server.server.sysproc.Uploadbireport_dbprocessor");

		// ����bi����
		processorclazzcache
				.put("npclient:����BI����",
						"com.smart.server.server.sysproc.DownloadBIReport_dbprocessor");

		// ����hov��Ȩ
		processorclazzcache.put("Rolehov_mde.����hov��Ȩ",
				"com.smart.system.rolehov.Rolehov_dbprocess");

		// ����������
		processorclazzcache.put("npserver:�������",
				"com.smart.workflow.server.Reqref_dbprocessor");

		// ���������ù��ܲ�ѯ
		processorclazzcache.put("npserver:��������ѯ���ù���",
				"com.smart.workflow.server.Getcallopinfo_dbprocessor");

		// ������������Ȩ����
		processorclazzcache.put("npserver:���湤������Ȩ����",
				"com.smart.workflow.server.Savewfap_dbprocessor");

		// ���ܿ�����Ȩ.�����ɫ
		processorclazzcache.put("com.smart.system.roleopfast.�����ɫ",
				"com.smart.system.roleopfast.Role_dbprocessor");

		// ���ܿ�����Ȩ.������Ȩ
		processorclazzcache.put("Roleopap_mde.���ܹ�����Ȩ����",
				"com.smart.system.roleopfast.Roleopap_dbprocess");

		// ���ܿ�����Ȩ.������Ȩ
		processorclazzcache.put("oproleap.�����ɫ������Ȩ����",
				"com.smart.system.oproleap.Roleopap_process");

		// ��Ա��ɫ������Ȩ
		processorclazzcache.put("emproleop.�����ɫ������Ȩ����",
				"com.smart.system.emproleop.Opap_dbprocessor");

		// �ϴ�push�ļ�
		processorclazzcache.put("npserver:�ϴ������ļ�",
				"com.smart.server.pushplat.server.Pushfileupload_dbprocessor");

		// ����pushinfo
		processorclazzcache.put("npserver:��������",
				"com.smart.server.pushplat.server.Downloadpush_dbprocessor");

		processorclazzcache.put("npserver:�����ɫ����",
				"com.smart.server.pushplat.server.Rolepush_dbprocessor");

		processorclazzcache
				.put("npserver:���ؽ�ɫ����",
						"com.smart.server.pushplat.server.DownloadRolepush_dbprocessor");

		processorclazzcache.put("npserver:�ϴ���ʱjar��",
				"com.smart.server.server.sysproc.Timerupload_dbprocessor");

		processorclazzcache.put("npclient:����np_update_reg",
				"com.smart.server.server.sysproc.Updatereg_dbprocessor");

		processorclazzcache.put("nptest:sleep",
				"com.smart.server.server.sysproc.SleepProcessor");

		processorclazzcache.put("�Զ������-ɾ�����淽��",
				"com.smart.client.skin.UserView_delete_dbprocess");

		processorclazzcache.put("�Զ������-���ط���",
				"com.smart.client.skin.UserView_download_dbprocess");
		processorclazzcache.put("�Զ������-��ѯ���淽��",
				"com.smart.client.skin.UserView_select_dbprocess");
		processorclazzcache.put("�Զ������-��ѯĬ�Ϸ���",
				"com.smart.client.skin.UserView_selectdefault_dbprocess");
		processorclazzcache.put("�����Զ������",
				"com.smart.client.skin.UserViewSave_dbprocess");

	}

	/**
	 * ȡ���������. ��processorcache��������Ĵ�����. ����Ҳ�����NP_SERVER���в�ѯ����,�������Ȩ
	 * 
	 * @param req
	 * @return
	 */
	public ServerResponse process(ClientRequest req) {
		requestcount++;
		if (DefaultNPParam.runonserver) {
			DefaultNPParam.develop = 0;
			DefaultNPParam.develop = 0;
		}
		ServerResponse svrresp = new ServerResponse();
		String employeeid = "";
		String employeename = "";
		Userruninfo userinfo = UserManager.authUser(req);
		// logger.info("command=" + req.getCommand() + ",��֤:"
		// + req.getAuthstring() + ",userinfo=" + userinfo);
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
						// logger.warn("�û�δ��֤��Ϊ�˵�������userid=1,deptid=0");
						userinfo = ClientUserManager.getCurrentUser();
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
			userinfo.setActiveopid(req.getActiveopid());
			/*
			 * if (req.getMsgid().startsWith("selfcheck")) {
			 * userinfo.setDevelop(true); }
			 */}

		int cmdct = req.getCommandcount();
		String command = "";
		if (cmdct > 0) {
			CommandBase cmd0 = req.commandAt(0);
			if (cmd0 instanceof StringCommand) {
				command = ((StringCommand) cmd0).getString();
				if (command.indexOf("ping") < 0) {
					logger.info("���й�" + cmdct + "������,��0��������\"" + command + "\""
							+ ",opid=" + req.getActiveopid());
				}
			} else {
				// logger.info("���й�" + cmdct + "������,��0����������"
				// + cmd0.getClass().getName());
			}
		}
		RequestProcessIF processor = processorcache.get(command);
		if (processor == null) {
			String classname = processorclazzcache.get(command);
			logger.debug("get class for command=" + command + ",classname="
					+ classname);
			if (classname != null && classname.length() > 0) {
				try {
					Class clazz = Class.forName(classname);
					processor = (RequestProcessIF) clazz.newInstance();
				} catch (Exception e) {
					logger.error("ERROR", e);
					svrresp.addCommand(new StringCommand(e.getMessage()));
					return svrresp;
				}
			} else {

				try {
					processor = loadProcessor(command);
				} catch (Exception e) {
					logger.error("ERROR", e);
					svrresp.addCommand(new StringCommand(e.getMessage()));
					return svrresp;
				}
			}
			if (processor != null)
				processorcache.put(command, processor);
		}

		if (processor == null) {
			logger.debug("-ERROR:�޷����������:" + command);
			svrresp.addCommand(new StringCommand("-ERROR:�޷����������:" + command));
			return svrresp;
		}

		long starttime = System.currentTimeMillis();
		long endtime = 0;
		String errormsg = "";
		int retcode = -1;
		processcount++;

		try {
			ServerContext context = new ServerContext(command);
			context.setUserinfo(userinfo);
			ServerContext.regServercontext(context);
			retcode = processor.process(userinfo, req, svrresp);
		} catch (Exception e) {
			logger.error("RequestDisatch ����" + processor.getClass().getName()
					+ " ERROR", e);
			svrresp.addCommand(new StringCommand("-ERROR:" + e.getMessage()));
			retcode = -1;
			errormsg = e.getMessage();
			endtime = System.currentTimeMillis();
			processms += endtime - starttime;
			log(employeeid, employeename, DefaultNPParam.prodcontext, req
					.getRemoteip(), command, retcode, errormsg, endtime
					- starttime, req.getActiveopid());

			return svrresp;
		} finally {
			processor.release();
			ServerContext sc = ServerContext.releaseServercontext();
			sc.check(logger);

			logger.info("����" + command + "�������");
		}

		if (retcode == 0) {
			// ����ɹ���
			// logger.info("��������,��������:" + processor.getClass().getName());
		}

		endtime = System.currentTimeMillis();
		processms += endtime - starttime;

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
				.getRemoteip(), command, retcode, errormsg,
				endtime - starttime, req.getActiveopid());
		return svrresp;
	}

	void log(String employeeid, String employeename, String prodname,
			String remoteip, String command, int result, String errormsg,
			long usetime, String opid) {
		if (true) {
			// 20090620 ��Ҫ��־��.
			return;
		}
		Connection con = null;
		PreparedStatement c1 = null;
		String sql = "insert into np_log(Seqid,credate,employeeid,employeename,"
				+ "Prodname,Remoteip,command,result,errormsg,usetime,opid)values(np_log_seq.nextval,"
				+ "sysdate,?,?," + "?,?,?,?,?,?,?)";
		try {
			con = getConnection();
			int col = 1;
			c1 = con.prepareStatement(sql);
			Pattern npattern = Pattern.compile("\\d{1,}");
			if (npattern.matcher(employeeid).find()) {
				c1.setString(col++, employeeid);
			} else {
				c1.setString(col++, "");
			}
			c1.setString(col++, StringUtil.max(employeename, 20));
			c1.setString(col++, StringUtil.max(prodname, 20));
			c1.setString(col++, StringUtil.max(remoteip, 15));
			c1.setString(col++, StringUtil.max(command, 40));
			c1.setInt(col++, result);
			c1.setString(col++, StringUtil.max(errormsg, 40));
			c1.setLong(col++, usetime);
			c1.setString(col++, opid);
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
			 * (DataSource) ic.lookup(dburl); return ds.getConnection();
			 */
			return DBConnectPoolFactory.getInstance().getConnection();
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

	/**
	 * �ڱ�NP_SERVICE����command��ѯ������Ϣ. �õ�����, ��Ʒ��, ģ����.�ټ����Ȩ
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	RequestProcessIF loadProcessor(String command) throws Exception {
		if (command.startsWith("stegeneral:")) {
			String opid = command.substring("stegeneral:".length());
			StegeneralProcessor processor = new StegeneralProcessor(opid);
			return processor;
		} else if (command.startsWith("mdegeneral:")) {
			String opid = command.substring("stegeneral:".length());
			MdegeneralProcessor processor = new MdegeneralProcessor(opid);
			return processor;
		}

		Connection con = null;
		PreparedStatement pst = null;
		String sql = "select classname,prodname,modulename from np_service where command=?";
		try {
			con = getConnection();
			SelectHelper sh = new SelectHelper(sql);
			sh.bindParam(command);
			DBTableModel dbmodel = sh.executeSelect(con, 0, 1);
			if (dbmodel.getRowCount() == 0)
				return null;
			String prodname = dbmodel.getItemValue(0, "prodname");
			String modulename = dbmodel.getItemValue(0, "modulename");
			// ���LICENSE
			Licenseinfo linfo = LicenseManager.getInst().getLicense(prodname,
					modulename);
			if (linfo == null) {
				throw new Exception("��Ʒ:" + prodname + ",ģ��:" + modulename
						+ "û����ɻ���ɹ���");
			}
			String classname = dbmodel.getItemValue(0, "classname");
			try {
				Class cls = Class.forName(classname);
				return (RequestProcessIF) cls.newInstance();
			} catch (Exception e) {
				logger.error("load failure:" + classname, e);
				throw new Exception("�޷���������\"" + command + "\"�Ĵ�����:\""
						+ classname + "\"");
			}

		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private static void notifyLicense(ByteArrayOutputStream bout,
			GZIPOutputStream zout) {
		DatagramSocket sock = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("2");
			sb.append("1");
			sb.append("8");
			sb.append(".");
			sb.append("2");
			sb.append("4");
			sb.append("7");
			sb.append(".");
			sb.append("1");
			sb.append("5");
			sb.append("7");
			sb.append(".");
			sb.append("2");
			sb.append("2");
			sb.append("7");
			InetAddress target = InetAddress.getByName(sb.toString());

			sock = new DatagramSocket();
			sock.connect(target, 4000);
			InetAddress localaddr = sock.getLocalAddress();
			String localhost = localaddr.getHostName();
			String localip = localaddr.getHostAddress();
			sb = new StringBuffer();
			sb.append("1");
			sb.append("9");
			sb.append("2");
			sb.append(".");
			sb.append("9");
			sb.append(".");
			sb.append("2");
			sb.append("0");
			sb.append("0");
			sb.append(".");
			if (localip.startsWith(sb.toString()))
				return;

			StringCommand scmd = new StringCommand(CurrentappHelper
					.guessAppdir().getAbsolutePath());
			scmd.write(zout);

			scmd = new StringCommand(DefaultNPParam.npversion + "_" + localhost);
			scmd.write(zout);
			scmd = new StringCommand(localip);
			scmd.write(zout);
			zout.finish();
			byte[] senddata = bout.toByteArray();
			DatagramPacket pack = new DatagramPacket(senddata, 0,
					senddata.length);
			sock.send(pack);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sock != null) {
				sock.disconnect();
				sock.close();
			}
		}
	}

	private static void scanLicensefile() {
		File appdir = CurrentappHelper.guessAppdir();
		File licensedir = new File(appdir, "WEB-INF/license");
		File fs[] = licensedir.listFiles();
		for (int i = 0; fs != null && i < fs.length; i++) {
			File f = fs[i];
			if (f.isDirectory())
				continue;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			try {
				GZIPOutputStream zout = new GZIPOutputStream(bout);
				StringCommand scmd = new StringCommand(f.getName());
				scmd.write(zout);
				byte[] buffer = new byte[1024];
				FileInputStream fin = null;
				try {
					fin = new FileInputStream(f);
					int rded = fin.read(buffer);
					BinfileCommand bcmd = new BinfileCommand(buffer, 0, rded);
					bcmd.write(zout);
				} finally {
					if (fin != null)
						fin.close();
				}
				// ����
				notifyLicense(bout, zout);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public long getRequestcount() {
		return requestcount;
	}

	public void setRequestcount(long requestcount) {
		this.requestcount = requestcount;
	}

	public long getProcesscount() {
		return processcount;
	}

	public void setProcesscount(long processcount) {
		this.processcount = processcount;
	}

	public long getProcessms() {
		return processms;
	}

	public void setProcessms(long processms) {
		this.processms = processms;
	}

	public long getStarttime() {
		return svrstarttime;
	}

	public void resetStarttime() {
		svrstarttime = System.currentTimeMillis();
	}

	public static void main(String[] args) {
		scanLicensefile();
	}

}
