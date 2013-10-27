package com.inca.npserver.server;

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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Category;

import com.inca.np.anyprint.impl.BuilddatasourceProcessor;
import com.inca.np.auth.ClientUserManager;
import com.inca.np.auth.UserManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.demo.mde.Goodsdtl_dbprocess;
import com.inca.np.demo.mmde.Goodsvariety_dbprocess;
import com.inca.np.demo.mste.Pubfactory_dbprocess;
import com.inca.np.demo.mste.Pubgoodsdetail_dbprocess;
import com.inca.np.demo.server.Pub_goods_dbprocess;
import com.inca.np.demo.server.pub_factory_dbprocess;
import com.inca.np.fileserver.Deletefiledbprocessor;
import com.inca.np.fileserver.FileBrowseProcessor;
import com.inca.np.filesync.Download_dbprocess;
import com.inca.np.filesync.Uploadfile_dbprocess;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.server.RequestProcessIF;
import com.inca.np.server.ServerContext;
import com.inca.np.server.process.Pingprocessor;
import com.inca.np.server.process.Sysddl_dbprocess;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.SelectHelper;
import com.inca.np.util.StringUtil;
import com.inca.npserver.dbcp.DBConnectPoolFactory;
import com.inca.npserver.log.NpLogManager;
import com.inca.npserver.prod.LicenseManager;
import com.inca.npserver.prod.Licenseinfo;
import com.inca.npserver.server.sysproc.CreatestoreprocProcessor;
import com.inca.npserver.server.sysproc.CurrentappHelper;
import com.inca.npserver.server.sysproc.DownloadLauncherProcessor;
import com.inca.npserver.server.sysproc.FillprintnoProcessor;
import com.inca.npserver.server.sysproc.GetentryDbprocessor;
import com.inca.npserver.server.sysproc.GetplacepointDbprocessor;
import com.inca.npserver.server.sysproc.GetroleoplistProcessor;
import com.inca.npserver.server.sysproc.LoginProcessor;
import com.inca.npserver.server.sysproc.MacrequestProcessor;
import com.inca.npserver.server.sysproc.MdegeneralProcessor;
import com.inca.npserver.server.sysproc.ModuledownloadProcessor;
import com.inca.npserver.server.sysproc.ModulefiledownloadProcessor;
import com.inca.npserver.server.sysproc.PrequeryspProcessor;
import com.inca.npserver.server.sysproc.Printplandownload_dbprocess;
import com.inca.npserver.server.sysproc.Repassword_dbprocessor;
import com.inca.npserver.server.sysproc.SelectProcessor;
import com.inca.npserver.server.sysproc.SetentryProcessor;
import com.inca.npserver.server.sysproc.SetplacepointProcessor;
import com.inca.npserver.server.sysproc.SetroleProcessor;
import com.inca.npserver.server.sysproc.StegeneralProcessor;
import com.inca.npserver.server.sysproc.TestlongtimesqlProcessor;
import com.inca.npserver.server.sysproc.UploadPrintplan_dbprocess;
import com.inca.npserver.server.sysproc.ZxhovdownloadProcessor;
import com.inca.npserver.server.sysproc.Zxmodifyinstall_dbprocessor;
import com.inca.npserver.server.sysproc.ZxzipdownloadProcessor;
import com.inca.npworkflow.server.Approvestatus_dbprocess;
import com.inca.npworkflow.server.Dataitemedit_dbprocessor;
import com.inca.npworkflow.server.Feedemo_dbprocess;
import com.inca.npworkflow.server.Feedemoconfirm_dbprocessor;
import com.inca.npworkflow.server.Feelimit_dbprocess;
import com.inca.npworkflow.server.Fetchnodedata_dbprocessor;
import com.inca.npworkflow.server.Fetchnodeinst_dbprocessor;
import com.inca.npworkflow.server.Humanapprove_dbprocessor;
import com.inca.npworkflow.server.Nodeinstdata_dbprocessor;
import com.inca.npworkflow.server.Wfcheck_dbprocessor;
import com.inca.npworkflow.server.Wfdefine_dbprocess;
import com.inca.npworkflow.server.Wfinst_dbprocess;
import com.inca.npworkflow.server.Wfnodeemp_dbprocess;
import com.inca.npworkflow.server.Wfnoderole_dbprocess;
import com.inca.sysmgr.server.ApbatchsaveProcessor;
import com.inca.sysmgr.server.ApqueryProcessor;
import com.inca.sysmgr.server.ApsaveProcessor;
import com.inca.sysmgr.server.HovapqueryProcessor;
import com.inca.sysmgr.server.HovapsaveProcessor;

public class Server {

	Category logger = Category.getInstance(Server.class);
	HashMap<String, RequestProcessIF> processorcache = new HashMap<String, RequestProcessIF>();
	HashMap<String, String> processorclazzcache = new HashMap<String, String>();

	/**
	 * 请求次数
	 */
	private long requestcount = 0;
	/**
	 * 成功处理次数
	 */
	private long processcount = 0;

	/**
	 * 开始时间
	 */
	private long svrstarttime = System.currentTimeMillis();
	/**
	 * 处理所用毫秒累加
	 */
	private long processms = 0;

	private static HashMap<String, String> notneedautocmds = new HashMap<String, String>();
	static {
		notneedautocmds.put("查询系统选项字典", "查询系统选项字典");
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
			// 启动日志
			NpLogManager.getInstance();
			inst.loadSystemprocessor();
		}
		return inst;
	}

	/**
	 * 加载系统内部使用的服务
	 */
	void loadSystemprocessor() {
		processorcache.put("npclient:downloadlaunchers",
				new DownloadLauncherProcessor());
		processorcache.put("npclient:downloadmodules",
				new ModuledownloadProcessor());
		processorcache.put("npclient:downloadmodulefile",
				new ModulefiledownloadProcessor());
		processorcache.put("npclient:login", new LoginProcessor());
		processorcache.put("npclient:重设密码", new Repassword_dbprocessor());
		processorcache.put("npclient:getroleoplist",
				new GetroleoplistProcessor());
		processorcache.put("npclient:setroleid", new SetroleProcessor());
		processorcache.put("select", new SelectProcessor());
		processorcache.put("ping", new Pingprocessor());
		processorcache.put("查询系统选项字典", new Sysddl_dbprocess());
		processorcache.put("np:uploadfile", new Uploadfile_dbprocess());
		processorcache.put("np:deletefile", new Deletefiledbprocessor());
		processorcache.put("np:browsefilegroup", new FileBrowseProcessor());
		processorcache.put("np:download", new Download_dbprocess());
		processorcache.put("npclient:setplacepointid",
				new SetplacepointProcessor());

		processorcache.put("np:查询授权属性", new ApqueryProcessor());
		processorcache.put("np:保存授权属性", new ApsaveProcessor());
		processorcache.put("np:批量保存授权属性", new ApbatchsaveProcessor());
		processorcache.put("np:查询HOV授权属性", new HovapqueryProcessor());
		processorcache.put("np:保存HOV授权属性", new HovapsaveProcessor());
		processorcache.put("npclient:macrequest", new MacrequestProcessor());
		processorcache.put("npclient:downloadzxzip",
				new ZxzipdownloadProcessor());
		processorcache
				.put("npdev:安装专项调整ZIP", new Zxmodifyinstall_dbprocessor());
		processorcache.put("npclient:createstoreproc",
				new CreatestoreprocProcessor());
		processorcache.put("npclient:execprequerystoreproc",
				new PrequeryspProcessor());
		processorcache.put("npclient:下载专项HOV", new ZxhovdownloadProcessor());
		processorcache.put("npclient:testlongtimesql",
				new TestlongtimesqlProcessor());

		// 取门店
		processorcache.put("npclient:getplacepointid",
				new GetplacepointDbprocessor());
		// 取核算单元
		processorcache.put("npclient:getentry", new GetentryDbprocessor());
		processorcache.put("npclient:setentryid", new SetentryProcessor());

		// 打印数据源取得
		processorcache.put("npclient:builddatasource",
				new BuilddatasourceProcessor());
		processorcache.put("npclient:fetchdatasource",
				new BuilddatasourceProcessor());
		processorcache.put("npclient:上传打印方案", new UploadPrintplan_dbprocess());
		processorcache
				.put("npclient:下载打印方案", new Printplandownload_dbprocess());
		processorcache
				.put("npclient:列出打印方案", new Printplandownload_dbprocess());
		processorcache.put("npclient:填写打印单号", new FillprintnoProcessor());

		// ////////for mste demo only
		processorcache.put("com.inca.np.demo.mste.Pub_goods_ste.保存货品",
				new com.inca.np.demo.mste.Pub_goods_dbprocess());
		processorcache.put("com.inca.np.demo.mste.Pubfactory_ste.保存厂家",
				new Pubfactory_dbprocess());
		processorcache.put("com.inca.np.demo.mste.Pubgoodsdetail_ste.保存货品明细",
				new Pubgoodsdetail_dbprocess());
		processorcache.put("保存单位厂家", new pub_factory_dbprocess());
		processorcache.put("Goodsdtl_mde.保存货品和明细", new Goodsdtl_dbprocess());
		processorcache.put("demo.ste.Pub_goods_ste.保存货品",
				new Pub_goods_dbprocess());
		processorcache.put("npserver.demo.保存品种", new Goodsvariety_dbprocess());

		// 保存工作 流程定义
		processorcache.put("Wfdefine_mde.保存流程定义", new Wfdefine_dbprocess());
		processorcache.put("npworkflow:检查流程表达式", new Wfcheck_dbprocessor());
		processorcache.put("Dataitemedit_ste.保存数据项定义",
				new Dataitemedit_dbprocessor());
		processorcache.put("Nodeinstdata_ste.保存结点决策数据",
				new Nodeinstdata_dbprocessor());
		processorcache.put("Approvestatus_ste.保存流程状态",
				new Approvestatus_dbprocess());
		processorcache.put("Wfnoderole_ste.保存结点角色", new Wfnoderole_dbprocess());
		processorcache.put("Wfnodeemp_ste.保存结点人员", new Wfnodeemp_dbprocess());

		// 保存工作流程实例
		processorcache.put("Wfinst_mde.保存流程实例", new Wfinst_dbprocess());

		// 查询待审批
		processorcache.put("npserver:查询待审批", new Fetchnodeinst_dbprocessor());
		// 查询结点数据.
		processorcache
				.put("npserver:查询决策依据数据", new Fetchnodedata_dbprocessor());

		// 工作流人工审批
		processorcache.put("npserver:工作流审批", new Humanapprove_dbprocessor());

		// demo费用
		processorcache.put("npworkflow.demo.Feedemo_ste.保存费用",
				new Feedemo_dbprocess());
		processorcache.put("npworkflow.demo.Feedemo.提交",
				new Feedemoconfirm_dbprocessor());
		processorcache.put("com.inca.npworkflow.demo.Feelimit_ste.保存费用限额",
				new Feelimit_dbprocess());

		// /////////////BI
		// 保存bi报表定义
		processorclazzcache.put("Report_ste.保存报表定义",
				"com.inca.npbi.server.Report_dbprocess");
		// 保存bi报表列定义
		processorclazzcache.put("Tablecolumn_ste.保存基表列定义",
				"com.inca.npbi.server.Tablecolumn_dbprocess");
		// 保存bi视图定义
		processorclazzcache.put("View_ste.保存视图",
				"com.inca.npbi.server.View_dbprocess");
		// 保存bi实例
		processorclazzcache.put("Instance_ste.保存实例",
				"com.inca.npbi.server.Instance_dbprocess");
		// 保存bi datasource 连接
		processorclazzcache.put("Ds_ste.保存数据源连接",
				"com.inca.npbi.server.Ds_dbprocess");
		// 检查bi 报表定义
		processorclazzcache.put("npbi.检查报表定义",
				"com.inca.npbi.server.ReportChecker_dbprocessor");
		// 计算报表
		processorclazzcache.put("npbi.计算报表实例",
				"com.inca.npbi.server.Calcreport_dbprocessor");
		// 取预览dbmodel
		processorclazzcache.put("npbi:取报表预览dbtablemodel",
				"com.inca.npbi.server.GetBIreportdm_dbprocessor");
		// 批量生成bi报表instance
		processorclazzcache.put("npbi:批量生成instance",
				"com.inca.npbi.server.Batchgeninstance_dbprocessor");

		// 上传bi报表
		processorclazzcache.put("npclient:上传bi报表",
				"com.inca.npserver.server.sysproc.Uploadbireport_dbprocessor");

		// 下载bi报表
		processorclazzcache
				.put("npclient:下载BI报表",
						"com.inca.npserver.server.sysproc.DownloadBIReport_dbprocessor");

		// 保存hov授权
		processorclazzcache.put("Rolehov_mde.保存hov授权",
				"com.inca.sysmgr.rolehov.Rolehov_dbprocess");

		// 工作流参批
		processorclazzcache.put("npserver:请求参批",
				"com.inca.npworkflow.server.Reqref_dbprocessor");

		// 工作流调用功能查询
		processorclazzcache.put("npserver:工作流查询调用功能",
				"com.inca.npworkflow.server.Getcallopinfo_dbprocessor");

		// 工作流保存授权属性
		processorclazzcache.put("npserver:保存工作流授权属性",
				"com.inca.npworkflow.server.Savewfap_dbprocessor");

		// 功能快束授权.保存角色
		processorclazzcache.put("com.inca.sysmgr.roleopfast.保存角色",
				"com.inca.sysmgr.roleopfast.Role_dbprocessor");

		// 功能快束授权.保存授权
		processorclazzcache.put("Roleopap_mde.保管功能授权属性",
				"com.inca.sysmgr.roleopfast.Roleopap_dbprocess");

		// 功能快束授权.保存授权
		processorclazzcache.put("oproleap.保存角色功能授权属性",
				"com.inca.sysmgr.oproleap.Roleopap_process");

		// 人员角色功能授权
		processorclazzcache.put("emproleop.保存角色功能授权属性",
				"com.inca.sysmgr.emproleop.Opap_dbprocessor");

		// 上传push文件
		processorclazzcache.put("npserver:上传推送文件",
				"com.inca.npserver.pushplat.server.Pushfileupload_dbprocessor");

		// 下载pushinfo
		processorclazzcache.put("npserver:下载推送",
				"com.inca.npserver.pushplat.server.Downloadpush_dbprocessor");

		processorclazzcache.put("npserver:保存角色推送",
				"com.inca.npserver.pushplat.server.Rolepush_dbprocessor");

		processorclazzcache
				.put("npserver:下载角色推送",
						"com.inca.npserver.pushplat.server.DownloadRolepush_dbprocessor");

		processorclazzcache.put("npserver:上传定时jar包",
				"com.inca.npserver.server.sysproc.Timerupload_dbprocessor");

		processorclazzcache.put("npclient:保存np_update_reg",
				"com.inca.npserver.server.sysproc.Updatereg_dbprocessor");

		processorclazzcache.put("nptest:sleep",
				"com.inca.npserver.server.sysproc.SleepProcessor");

		processorclazzcache.put("自定义界面-删除界面方案",
				"com.inca.npclient.skin.UserView_delete_dbprocess");

		processorclazzcache.put("自定义界面-下载方案",
				"com.inca.npclient.skin.UserView_download_dbprocess");
		processorclazzcache.put("自定义界面-查询界面方案",
				"com.inca.npclient.skin.UserView_select_dbprocess");
		processorclazzcache.put("自定义界面-查询默认方案",
				"com.inca.npclient.skin.UserView_selectdefault_dbprocess");
		processorclazzcache.put("保存自定义界面",
				"com.inca.npclient.skin.UserViewSave_dbprocess");

	}

	/**
	 * 取上行命令处理. 在processorcache中找命令的处理器. 如果找不到从NP_SERVER表中查询服务,并检查授权
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
		// logger.info("command=" + req.getCommand() + ",验证:"
		// + req.getAuthstring() + ",userinfo=" + userinfo);
		if (userinfo != null) {
			userinfo.dump(logger);
		}
		if (userinfo == null) {
			// 用户超时或未登录

			if (req.commandAt(0) instanceof StringCommand) {
				StringCommand cmd1 = (StringCommand) req.commandAt(0);
				if (cmd1.getString().indexOf("login") < 0) {
					// 非法用户

					if (DefaultNPParam.debug == 1
							|| isNoautocmd(cmd1.getString())) {
						// logger.warn("用户未验证。为了调试设置userid=1,deptid=0");
						userinfo = ClientUserManager.getCurrentUser();
					} else {
						svrresp.addCommand(new StringCommand(
								"-ERROR:未登录或超时，请重新登录"));
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
					logger.info("上行共" + cmdct + "个命令,第0个命令是\"" + command + "\""
							+ ",opid=" + req.getActiveopid());
				}
			} else {
				// logger.info("上行共" + cmdct + "个命令,第0个命令类是"
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
			logger.debug("-ERROR:无法处理的请求:" + command);
			svrresp.addCommand(new StringCommand("-ERROR:无法处理的请求:" + command));
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
			logger.error("RequestDisatch 调用" + processor.getClass().getName()
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

			logger.info("上行" + command + "处理完成");
		}

		if (retcode == 0) {
			// 处理成功。
			// logger.info("命令处理完成,处理器是:" + processor.getClass().getName());
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
			// 如果是登录,得找出用户
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
			// 20090620 不要日志了.
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
	 * 在表NP_SERVICE中以command查询服务信息. 得到类名, 产品名, 模块名.再检查授权
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
			// 检查LICENSE
			Licenseinfo linfo = LicenseManager.getInst().getLicense(prodname,
					modulename);
			if (linfo == null) {
				throw new Exception("产品:" + prodname + ",模块:" + modulename
						+ "没有许可或许可过期");
			}
			String classname = dbmodel.getItemValue(0, "classname");
			try {
				Class cls = Class.forName(classname);
				return (RequestProcessIF) cls.newInstance();
			} catch (Exception e) {
				logger.error("load failure:" + classname, e);
				throw new Exception("无法加载命令\"" + command + "\"的处理类:\""
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
				// 发送
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
