package com.smart.adminclient.gui;


import com.smart.platform.gui.runop.Opgroup;
import com.smart.platform.gui.runop.Opnode;

public class Adminops {

	public static Opgroup createAdminOps() {
		Opgroup topgroup = new Opgroup("npadmin");

		/**
		 * 初始化部分:
		 */
		Opgroup group = createProdGroup();
		topgroup.addSubgroup(group);

		group = createMonitorGroup();
		topgroup.addSubgroup(group);

		return topgroup;

	}

	/**
	 * 产品管理
	 */
	private static Opgroup createProdGroup() {
		Opgroup group = new Opgroup("服务器产品管理");
		Opnode opnode = null;
		boolean ok = true;
		opnode = new Opnode("", "服务器数据库连接池配置");
		opnode.setClassname("com.inca.adminclient.dbcp.Dbcpframe");
		group.addOpnode(opnode);

		opnode = new Opnode("", "产品授权管理");
		opnode.setClassname("com.inca.adminclient.prodmanager.ProdmanagerFrame");
		group.addOpnode(opnode);

		opnode = new Opnode("", "制作模块安装包");
		opnode.setClassname("com.inca.adminclient.installjar.Installjarbuilder");
		group.addOpnode(opnode);
		
		opnode = new Opnode("", "产品模块安装");
		opnode.setClassname("com.inca.adminclient.modulemgr.ModulemgrFrame");
		group.addOpnode(opnode);

		
		return group;

	}

	private static Opgroup createMonitorGroup() {
		Opgroup group = new Opgroup("NPServer监控");
		Opnode opnode = null;
		opnode = new Opnode("", "查询服务器性能");
		opnode.setClassname("com.inca.adminclient.svrperform.Svrperform_frm");
		group.addOpnode(opnode);
		opnode = new Opnode("", "查询已登录用户");
		opnode.setClassname("com.inca.adminclient.serverinfo.Listlogin_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "查询sql执行情况");
		opnode.setClassname("com.inca.adminclient.serverinfo.Sqlmonitor_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "监控用户sql");
		opnode.setClassname("com.inca.adminclient.usersqlm.Usersqlm_frm");
		group.addOpnode(opnode);
		opnode = new Opnode("", "远程查询");
		opnode.setClassname("com.inca.adminclient.remotesql.Remotesql_frame");
		group.addOpnode(opnode);
		//opnode = new Opnode("", "查询服务器访问日志");
		//opnode.setClassname("com.inca.np.logger.Visitlogger_frame");
		//group.addOpnode(opnode);
		opnode = new Opnode("", "查询服务器日志");
		opnode.setClassname("com.inca.adminclient.viewlog.ViewlogFrame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "查询数据库服务器表空间");
		opnode.setClassname("com.inca.adminclient.serverinfo.Tablespace_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "查询数据库服务器信息");
		opnode.setClassname("com.inca.adminclient.serverinfo.Serverinfo_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "查询数据库服务器连接");
		opnode.setClassname("com.inca.adminclient.serverinfo.Session_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "查询数据库服务器锁库");
		opnode.setClassname("com.inca.adminclient.serverinfo.Sessionlock_frame");
		group.addOpnode(opnode);
		opnode = new Opnode("", "检查数据库Fullscan");
		opnode.setClassname("com.inca.adminclient.fullscan.Fullscan_frame");
		group.addOpnode(opnode);

		return group;
	}
}
