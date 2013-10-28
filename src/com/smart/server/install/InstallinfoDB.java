package com.smart.server.install;

import java.sql.Connection;
import java.util.Enumeration;

import org.apache.log4j.Category;

import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.InsertHelper;
import com.smart.platform.util.SelectHelper;
import com.smart.platform.util.UpdateHelper;

/**
 * 将安装信息中的产品 模块 功能 HOV 服务 安装到数据库中
 * 
 * @author Administrator
 * 
 */
public class InstallinfoDB {
	public static void install(Connection con, Installinfo installinfo)
			throws Exception {
		// 登记产品 模块 功能 HOV 服务
		try {
			installProd(con, installinfo);
			installModule(con, installinfo);
			installOp(con, installinfo);
			installHov(con, installinfo);
			installService(con, installinfo);
			con.commit();
		} catch (Exception e) {
			con.rollback();
			throw e;
		} finally {

		}
	}

	/**
	 * 安装产品
	 * 
	 * @param con
	 * @param installinfo
	 * @throws Exception
	 */
	static void installProd(Connection con, Installinfo installinfo)
			throws Exception {
		String sql = "select prodname from np_prod where prodname=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(installinfo.getProdname());
		DBTableModel dbmodel = sh.executeSelect(con, 0, 1);
		if (dbmodel.getRowCount() == 1)
			return;

		// 新增
		InsertHelper ih = new InsertHelper("np_prod");
		ih.bindParam("prodname", installinfo.getProdname());
		ih.executeInsert(con);

	}

	/**
	 * 安装模块
	 * 
	 * @param con
	 * @param installinfo
	 * @throws Exception
	 */
	static void installModule(Connection con, Installinfo installinfo)
			throws Exception {
		String sql = "select engname,version from np_module where prodname=? and modulename=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(installinfo.getProdname());
		sh.bindParam(installinfo.getModulename());

		DBTableModel dbmodel = sh.executeSelect(con, 0, 1);
		if (dbmodel.getRowCount() == 1) {
			boolean diff = false;
			if (!dbmodel.getItemValue(0, "engname").equals(
					installinfo.getModuleengname())) {
				diff = true;
			}
			if (!dbmodel.getItemValue(0, "version").equals(
					installinfo.getVersion())) {
				diff = true;
			}

			if (diff) {
				UpdateHelper uh = new UpdateHelper(
						"update np_module set engname=?,version=?  "
								+ " where prodname=? and modulename=?");
				uh.bindParam(installinfo.getModuleengname());
				uh.bindParam(installinfo.getVersion());
				uh.bindParam(installinfo.getProdname());
				uh.bindParam(installinfo.getModulename());
				uh.executeUpdate(con);
			}

			return;
		}

		// 新增
		InsertHelper ih = new InsertHelper("np_module");
		ih.bindParam("prodname", installinfo.getProdname());
		ih.bindParam("engname", installinfo.getModuleengname());
		ih.bindParam("version", installinfo.getVersion());
		ih.bindParam("modulename", installinfo.getModulename());
		ih.executeInsert(con);

	}

	static void installOp(Connection con, Installinfo installinfo)
			throws Exception {
		// 安装每一个功能
		Enumeration<Installinfo.Opinfo> en = installinfo.getOpinfos()
				.elements();
		while (en.hasMoreElements()) {
			Installinfo.Opinfo opinfo = en.nextElement();
			installOp(con, installinfo, opinfo);
		}
	}

	/**
	 * 安装一个功能
	 * 
	 * @param con
	 * @param installinfo
	 * @param opinfo
	 * @throws Exception
	 */
	static void installOp(Connection con, Installinfo installinfo,
			Installinfo.Opinfo opinfo) throws Exception {
		String sql = "select opid,opcode,opname,classname,prodname,modulename,groupname,sortno from np_op where opid=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(opinfo.opid);
		DBTableModel opdbmodel = sh.executeSelect(con, 0, 1);
		if (opdbmodel.getRowCount() == 0) {
			InsertHelper ih = new InsertHelper("np_op");
			ih.bindParam("opid", opinfo.opid);
			ih.bindParam("opcode", opinfo.opcode);
			ih.bindParam("opname", opinfo.opname);
			ih.bindParam("classname", opinfo.classname);
			ih.bindParam("prodname", installinfo.prodname);
			ih.bindParam("modulename", installinfo.modulename);
			ih.bindParam("groupname", opinfo.groupname);
			ih.bindParam("sortno", opinfo.sortno);
			ih.executeInsert(con);
		} else {
			boolean diff = false;
			if (!opdbmodel.getItemValue(0, "opcode").equals(opinfo.opcode)) {
				diff = true;
			}
			if (!opdbmodel.getItemValue(0, "opname").equals(opinfo.opname)) {
				diff = true;
			}
			if (!opdbmodel.getItemValue(0, "classname")
					.equals(opinfo.classname)) {
				diff = true;
			}
			if (!opdbmodel.getItemValue(0, "prodname").equals(
					installinfo.getProdname())) {
				diff = true;
			}
			if (!opdbmodel.getItemValue(0, "modulename").equals(
					installinfo.getModulename())) {
				diff = true;
			}
			if (!opdbmodel.getItemValue(0, "groupname")
					.equals(opinfo.groupname)) {
				diff = true;
			}
			if (!opdbmodel.getItemValue(0, "sortno")
					.equals(opinfo.sortno)) {
				diff = true;
			}

			// 更新
			if (diff) {
				UpdateHelper uh = new UpdateHelper(
						"update np_op set opcode=?,opname=?,classname=?,"
								+ " prodname=?,modulename=?,groupname=?,sortno=? where opid=?");
				uh.bindParam(opinfo.opcode);
				uh.bindParam(opinfo.opname);
				uh.bindParam(opinfo.classname);
				uh.bindParam(installinfo.prodname);
				uh.bindParam(installinfo.modulename);
				uh.bindParam(opinfo.groupname);
				uh.bindParam(opinfo.sortno);
				uh.bindParam(opinfo.opid);
				uh.executeUpdate(con);
			}
		}
	}

	static void installHov(Connection con, Installinfo installinfo)
			throws Exception {
		Enumeration<Installinfo.Hovinfo> en = installinfo.getHovinfos()
				.elements();
		while (en.hasMoreElements()) {
			Installinfo.Hovinfo hovinfo = en.nextElement();
			try {
				installHov(con, installinfo, hovinfo);
			} catch (Exception e) {
				Category.getInstance(InstallinfoDB.class).error(
						"安装HOV " + hovinfo.hovname + "失败:" ,e);
				throw new Exception("安装HOV " + hovinfo.hovname + "失败:" + e.getMessage());
			}
		}
	}

	/**
	 * 安装一个HOV
	 * 
	 * @param con
	 * @param installinfo
	 * @param opinfo
	 * @throws Exception
	 */
	static void installHov(Connection con, Installinfo installinfo,
			Installinfo.Hovinfo hovinfo) throws Exception {
		String sql = "select hovid,hovname,classname,prodname,modulename from np_hov where "
				+ "  hovname=? and prodname=? and modulename=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(hovinfo.hovname);
		sh.bindParam(installinfo.prodname);
		sh.bindParam(installinfo.modulename);

		DBTableModel hovdbmodel = sh.executeSelect(con, 0, 1);
		if (hovdbmodel.getRowCount() == 0) {
			InsertHelper ih = new InsertHelper("np_hov");
			ih.bindSequence("hovid", "NP_HOV_SEQ");
			ih.bindParam("hovname", hovinfo.hovname);
			ih.bindParam("classname", hovinfo.classname);
			ih.bindParam("prodname", installinfo.prodname);
			ih.bindParam("modulename", installinfo.modulename);
			ih.executeInsert(con);
		} else {
			boolean diff = false;
			if (!hovdbmodel.getItemValue(0, "classname").equals(
					hovinfo.classname)) {
				diff = true;
			}
			String hovid = hovdbmodel.getItemValue(0, "hovid");

			// 更新
			if (diff) {
				UpdateHelper uh = new UpdateHelper(
						"update np_hov set classname=? " + "  where hovid=?");
				uh.bindParam(hovinfo.classname);
				uh.bindParam(hovid);
				uh.executeUpdate(con);
			}
		}
	}

	static void installService(Connection con, Installinfo installinfo)
			throws Exception {
		Enumeration<Installinfo.Serviceinfo> en = installinfo.getServiceinfos()
				.elements();
		while (en.hasMoreElements()) {
			Installinfo.Serviceinfo serviceinfo = en.nextElement();
			installService(con, installinfo, serviceinfo);
		}
	}

	/**
	 * 安装一个服务
	 * 
	 * @param con
	 * @param installinfo
	 * @param opinfo
	 * @throws Exception
	 */
	static void installService(Connection con, Installinfo installinfo,
			Installinfo.Serviceinfo serviceinfo) throws Exception {
		String sql = "select command,classname,prodname,modulename from np_service where "
				+ "  command=?";
		SelectHelper sh = new SelectHelper(sql);
		sh.bindParam(serviceinfo.command);

		DBTableModel servicedbmodel = sh.executeSelect(con, 0, 1);
		if (servicedbmodel.getRowCount() == 0) {
			InsertHelper ih = new InsertHelper("np_service");
			ih.bindParam("command", serviceinfo.command);
			ih.bindParam("classname", serviceinfo.classname);
			ih.bindParam("prodname", installinfo.getProdname());
			ih.bindParam("modulename", installinfo.getModulename());
			ih.executeInsert(con);
		} else {
			boolean diff = false;
			if (!servicedbmodel.getItemValue(0, "classname").equals(
					serviceinfo.classname)) {
				diff = true;
			}
			if (!servicedbmodel.getItemValue(0, "prodname").equals(
					installinfo.getProdname())) {
				diff = true;
			}
			if (!servicedbmodel.getItemValue(0, "modulename").equals(
					installinfo.getModulename())) {
				diff = true;
			}

			// 更新
			if (diff) {
				UpdateHelper uh = new UpdateHelper(
						"update np_service set classname=?,prodname=?,modulename=? "
								+ "  where command=?");
				uh.bindParam(serviceinfo.classname);
				uh.bindParam(installinfo.getProdname());
				uh.bindParam(installinfo.getModulename());
				uh.bindParam(serviceinfo.command);
				uh.executeUpdate(con);
			}
		}
	}
}
