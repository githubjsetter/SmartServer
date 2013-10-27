package com.inca.npclient.skin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.util.SendHelper;

/**
 * @author Administrator 处理自定义界面的存储与读取
 *         存储到本地的规则，./conf/界面方案/userid/opid/方案ID_最后修改时间.properties
 */
public class SkinHelper {
	private static Category logger = Category.getInstance(SkinHelper.class);
	private static String basedir = "";

	static {
		String url = SkinHelper.class.getResource("SkinHelper.class")
				.toString();
		if (url.startsWith("file:")) {
			url = url.substring("file:/".length());
			int i = url.indexOf("classes");
			basedir = url.substring(0, i);
		} else if (url.startsWith("jar:")) {
			url = url.substring("jar:file:/".length());
			int i = url.indexOf("lib");
			basedir = url.substring(0, i);
		}
		basedir += "conf/" + "界面方案";
	}

	// 根据功能id、用户ID、方案ID、方案最后修改时间读取自定义界面配置
	public static List<SkinInfo> loadskin(CSteModel model)
			throws Exception {

		List<SkinInfo> list = new ArrayList<SkinInfo>();
		String viewid = model.getUserviewid();
		if (null == viewid || "".equals(viewid)) {
			return list;
		}
		String userid = ClientUserManager.getCurrentUser().getUserid();

		String dir = getDir(userid, model.getOpid());
		String path = getFileName(dir, model.getUserviewid());

		// 存在该方案则读取
		File f = new File(path);
		if (f.exists()) {
			BufferedReader bufferedreader = getReader(f);
			String lm = readLastModify(bufferedreader);
			// 判断最后修改时间
			if (model.getLastmodify().equals(lm)) {
				read(bufferedreader, list);
				closeReader(bufferedreader);
			} else {
				closeReader(bufferedreader);
				list = downloadskin(path, model);
			}
		} else {
			f = new File(dir);
			if (!f.exists()) {

				f.mkdirs();
			}
			list = downloadskin(path, model);
		}
		return list;
	}

	// 单列表的保存，同时保存到本地和数据库
	public static void save(CSteModel model, SkinInfo skininfo)
			throws Exception {
		List<SkinInfo> list = new ArrayList<SkinInfo>();
		list.add(skininfo);
		save(model, list);
	}

	// mde,mmde,mtab模式的保存
	public static void save(CSteModel model, List<SkinInfo> skininfos)
			throws Exception {
		String s = savetodb(model, skininfos);

		String[] userviewid_lastmodify = s.split("_");

		String userid = ClientUserManager.getCurrentUser().getUserid();

		String dir = getDir(userid, model.getOpid());
		// 不存在该方案则建目录，否则删除掉老文件。
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdirs();
		}

		f = new File(getFileName(dir, userviewid_lastmodify[0]));
		PrintWriter pw = new PrintWriter(f);
		writelastmodify(pw, userviewid_lastmodify[1]);
		for (SkinInfo skininfo : skininfos) {
			write(pw, skininfo);
		}
		pw.close();
		pw = null;

	}

	/**
	 * 打开一个功能时，查询默认方案
	 * 
	 * @param model
	 */
	public static void searchDefault(CSteModel model) {

		String userid = ClientUserManager.getCurrentUser().getUserid();

		ClientRequest rc = new ClientRequest();
		StringCommand sc = new StringCommand("自定义界面-查询默认方案");
		rc.addCommand(sc);

		ParamCommand pc = new ParamCommand();
		rc.addCommand(pc);
		pc.addParam("userid", userid);
		// pc.addParam("roleId", roleid);
		pc.addParam("opid", model.getOpid());
		pc.addParam("isdefault", "1");

		try {
			ServerResponse sr = SendHelper.sendRequest(rc);
			StringCommand c = (StringCommand) sr.commandAt(0);
			if (c.getString().startsWith("+OK")) {
				DataCommand dc = (DataCommand) sr.commandAt(1);
				DBTableModel db = dc.getDbmodel();
				if (db.getRowCount() > 0) {
					model.setLastmodify(db.getItemValue(0, "lastmodify"));
					model.setUserviewid(db.getItemValue(0, "userviewid"));
					model.setSchemeName(db.getItemValue(0, "schemename"));
					model.setIsdefaultscheme("1");
				}
			} else {
				System.err.println(c.getString());
			}

		} catch (Exception e) {

			logger.error("error",e);
		}
	}

	// 根据功能ID，用户ID，获取界面方案列表
	public static DBTableModel initSchemeList(String opid, String userid) {
		DBTableModel db = getSchemeNameDbTableModel();
		int row = 0;

		ClientRequest rc = new ClientRequest();
		StringCommand sc = new StringCommand("自定义界面-查询界面方案");
		rc.addCommand(sc);

		ParamCommand pc = new ParamCommand();
		rc.addCommand(pc);
		pc.addParam("userid", userid);
		pc.addParam("schemename", "");
		pc.addParam("opid", opid);

		try {
			ServerResponse sr = SendHelper.sendRequest(rc);
			StringCommand c = (StringCommand) sr.commandAt(0);
			if (c.getString().startsWith("+OK")) {
				DataCommand dc = (DataCommand) sr.commandAt(1);
				DBTableModel model = dc.getDbmodel();

				for (int i = 0; i < model.getRowCount(); i++) {
					row = db.getRowCount();
					db.appendRow();
					db.setItemValue(row, "schemename", model.getItemValue(i,
							"schemename"));
				}
			} else {
				logger.error(c.getString());
			}

		} catch (Exception e) {
			logger.error("error",e);
		}
		return db;
	}

	// 切换界面方案
	public static List<SkinInfo> changeScheme(CSteModel model)
			throws Exception {
		List<SkinInfo> list = new ArrayList<SkinInfo>();

		String userid = ClientUserManager.getCurrentUser().getUserid();

		ClientRequest rc = new ClientRequest();
		StringCommand sc = new StringCommand("自定义界面-查询界面方案");
		rc.addCommand(sc);

		ParamCommand pc = new ParamCommand();
		rc.addCommand(pc);
		pc.addParam("userid", userid);
		pc.addParam("schemename", model.getSchemeName());
		pc.addParam("opid", model.getOpid());
		// pc.addParam("isdefault", "1");

		try {
			ServerResponse sr = SendHelper.sendRequest(rc);
			StringCommand c = (StringCommand) sr.commandAt(0);
			if (c.getString().startsWith("+OK")) {
				DataCommand dc = (DataCommand) sr.commandAt(1);
				DBTableModel db = dc.getDbmodel();
				if (db.getRowCount() > 0) {
					model.setLastmodify(db.getItemValue(0, "lastmodify"));
					model.setUserviewid(db.getItemValue(0, "userviewid"));
					model.setSchemeName(db.getItemValue(0, "schemename"));
					model.setIsdefaultscheme(db.getItemValue(0, "isdefault"));
					list = loadskin(model);

				}
			} else {
				System.err.println(c.getString());
				throw new Exception(c.getString());
			}

		} catch (Exception e) {
			logger.error("error",e);
			throw e;
		}

		return list;

	}

	// 删除界面方案
	public static void deleteScheme(String opid, String userid,
			String schemename) throws Exception {
		ClientRequest rc = new ClientRequest();
		StringCommand sc = new StringCommand("自定义界面-删除界面方案");
		rc.addCommand(sc);

		ParamCommand pc = new ParamCommand();
		rc.addCommand(pc);
		pc.addParam("userid", userid);
		pc.addParam("schemename", schemename);
		pc.addParam("opid", opid);

		try {
			ServerResponse sr = SendHelper.sendRequest(rc);
			StringCommand c = (StringCommand) sr.commandAt(0);
			if (c.getString().startsWith("+OK")) {
				DataCommand dc = (DataCommand) sr.commandAt(1);
				DBTableModel db = dc.getDbmodel();
				if (db.getRowCount() > 0) {
					String userviewid = db.getItemValue(0, "userviewid");
					String dir = getDir(userid, opid);
					String path = getFileName(dir, userviewid);
					deleteFile(path);
				}
			} else {
				System.err.println(c.getString());
				throw new Exception(c.getString());
			}

		} catch (Exception e) {
			logger.error("error",e);
			throw e;
		}
	}

	private static void deleteFile(String path) {
		File f = new File(path);
		if (f.exists())
			f.delete();

	}

	// 方案文件名
	private static String getFileName(String dir, String userviewid) {
		String path = dir + "/" + userviewid + ".properties";

		return path;
	}

	// 方案所在目录
	private static String getDir(String userid, String opid) {
		String dir = basedir + "/" + userid + "/" + opid;

		return dir;
	}

	private static DBTableModel getSchemeNameDbTableModel() {
		Vector<DBColumnDisplayInfo> vector = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo dbcolumndisplayinfo = new DBColumnDisplayInfo(
				"schemename", "varchar", "界面方案名称");
		dbcolumndisplayinfo.setReadonly(true);
		dbcolumndisplayinfo.setTablecolumnwidth(110);
		vector.add(dbcolumndisplayinfo);
		DBTableModel db = new DBTableModel(vector);
		return db;
	}

	/**
	 *从服务器下载方案到本地
	 * 
	 * @param path
	 * @param model
	 */
	private static List<SkinInfo> downloadskin(String path,
			CSteModel model) {
		List<SkinInfo> list = new ArrayList<SkinInfo>();
		ClientRequest rc = new ClientRequest();
		StringCommand sc = new StringCommand("自定义界面-下载方案");
		rc.addCommand(sc);
		ParamCommand pc = new ParamCommand();
		rc.addCommand(pc);
		pc.addParam("userviewid", model.getUserviewid());

		try {
			ServerResponse sr = SendHelper.sendRequest(rc);
			StringCommand c = (StringCommand) sr.commandAt(0);
			if (c.getString().startsWith("+OK")) {
				DataCommand dc = (DataCommand) sr.commandAt(1);
				DataCommand dc2 = (DataCommand) sr.commandAt(2);
				DBTableModel dtl = dc.getDbmodel();
				DBTableModel expr = dc2.getDbmodel();

				list = convertToSkinInfo(dtl, expr);

				PrintWriter pw = new PrintWriter(new File(path));
				writelastmodify(pw, model.getLastmodify());
				for (SkinInfo skininfo : list) {
					write(pw, skininfo);
				}
				pw.close();
				pw = null;
			} else {
				System.err.println(c.getString());
			}

		} catch (Exception e) {

			logger.error("error",e);
		}

		return list;
	}

	private static List<SkinInfo> convertToSkinInfo(DBTableModel dtl,
			DBTableModel expr) {
		List<SkinInfo> skininfolist = new ArrayList<SkinInfo>();
		Map<String, List<ColInfo>> map = new HashMap<String, List<ColInfo>>();
		Map<String, String> exprmap = new HashMap<String, String>();
		String classname;

		for (int i = 0; i < dtl.getRowCount(); i++) {
			classname = dtl.getItemValue(i, "classname");
			ColInfo info = new ColInfo();
			info.setColname(dtl.getItemValue(i, "colname"));
			info.setColwidth(new Integer(dtl.getItemValue(i, "colwidth"))
					.intValue());
			info
					.setOrder(new Integer(dtl.getItemValue(i, "orders"))
							.intValue());
			if (map.get(classname) == null) {
				List<ColInfo> list = new ArrayList<ColInfo>();
				list.add(info);
				map.put(classname, list);

			} else {
				List<ColInfo> list = (List<ColInfo>) map.get(classname);
				list.add(info);
				map.put(classname, list);
			}
		}

		for (int i = 0; i < expr.getRowCount(); i++) {
			exprmap.put(expr.getItemValue(i, "classname"), expr.getItemValue(i,
					"orderexpr"));
		}
		SkinInfo info;
		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {
			classname = (String) it.next();
			info = new SkinInfo();
			info.setClassname(classname);
			info.setColinfos((List<ColInfo>) map.get(classname));
			info.setExpr((String) exprmap.get(classname));

			skininfolist.add(info);
		}

		return skininfolist;
	}

	private static void read(BufferedReader bufferedreader, List<SkinInfo> list)
			throws IOException {
		if (bufferedreader == null)
			return;

		try {
			String s = "";
			SkinInfo info;
			while (s != null) {
				s = bufferedreader.readLine();
				if (s != null && s.equals("<classname>")) {
					info = new SkinInfo();
					info.setClassname(bufferedreader.readLine());
					info.setExpr(bufferedreader.readLine());
					info.setColinfos(converttoColinfos(bufferedreader
							.readLine()));

					list.add(info);
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}

	private static BufferedReader getReader(File file)
			throws FileNotFoundException {
		BufferedReader bufferedreader = null;
		try {
			bufferedreader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw e;
		}

		return bufferedreader;
	}

	private static void closeReader(BufferedReader bufferedreader)
			throws IOException {
		if (bufferedreader != null) {
			try {
				bufferedreader.close();
				bufferedreader = null;
			} catch (IOException e) {
				throw e;
			}

		}
	}

	private static String readLastModify(BufferedReader bufferedreader) {
		String s = "";
		if (bufferedreader == null)
			return s;
		try {
			while (s != null) {
				s = bufferedreader.readLine();
				if (s != null && s.equals("<lastmodify>")) {
					s = bufferedreader.readLine();
					break;
				}
			}
		} catch (IOException e) {
			logger.error("error",e);
		}
		return s;
	}

	private static List<ColInfo> converttoColinfos(String line) {

		String[] s = line.split(",");
		List<ColInfo> infos = new ArrayList<ColInfo>();
		String[] ss;
		ColInfo info;
		for (int i = 0; i < s.length; i++) {
			ss = s[i].split(":");

			info = new ColInfo();
			info.setColname(ss[0]);
			info.setColwidth(new Integer(ss[1]).intValue());
			info.setOrder(new Integer(ss[2]).intValue());
			infos.add(info);
		}
		return infos;
	}

	/**
	 * @param model
	 * @param skininfos
	 * @return 方案ID_最后修改时间
	 * @throws Exception
	 */
	private static String savetodb(CSteModel model,
			List<SkinInfo> skininfos) throws Exception {
		String lastmodify = "" + System.currentTimeMillis();

		String userid = ClientUserManager.getCurrentUser().getUserid();
		String roleid = ClientUserManager.getCurrentUser().getRoleid();
		ClientRequest rc = new ClientRequest();
		StringCommand sc = new StringCommand("保存自定义界面");
		rc.addCommand(sc);

		ParamCommand pc = new ParamCommand();
		rc.addCommand(pc);
		pc.addParam("lastmodify", lastmodify);
		pc.addParam("userid", userid);
		pc.addParam("roleId", roleid);
		pc.addParam("opid", model.getOpid());
		pc.addParam("schemeName", model.getSchemeName());
		pc.addParam("isdefault", model.getIsdefaultscheme());
		pc.addParam("modelcount", "" + skininfos.size());
		pc.addParam("userviewid", model.getUserviewid());

		String classname = "";
		int i = 0;
		for (SkinInfo info : skininfos) {
			classname = info.getClassname();
			pc.addParam("classname," + i, classname);
			pc.addParam("expr," + i, info.getExpr());
			DataCommand dc = new DataCommand();
			dc.setDbmodel(convert(info));
			rc.addCommand(dc);
			i++;
		}

		try {

			ServerResponse sr = SendHelper.sendRequest(rc);
			StringCommand c = (StringCommand) sr.commandAt(0);
			if (c.getString().startsWith("+OK")) {
				StringCommand cc = (StringCommand) sr.commandAt(1);

				model.setUserviewid(cc.getString());
				model.setLastmodify(lastmodify);

				return cc.getString() + "_" + lastmodify;
			} else
				throw new Exception("保存自定义界面发生异常：" + c.getString());

		} catch (Exception e) {

			logger.error("error",e);
			throw new Exception("发送服务器命令：保存自定义界面发送异常" + e.getMessage());
		}
	}

	private static DBTableModel convert(SkinInfo info) {
		DBTableModel db = getDbTableModel();
		int row;
		for (ColInfo col : info.getColinfos()) {
			row = db.getRowCount();
			db.appendRow();
			db.setItemValue(row, "colname", col.getColname());
			db.setItemValue(row, "colwidth", "" + col.getColwidth());
			db.setItemValue(row, "orders", "" + col.getOrder());
		}
		return db;
	}

	private static void write(PrintWriter out, SkinInfo skininfo)
			throws IOException {
		StringBuffer sb = new StringBuffer();
		for (ColInfo info : skininfo.getColinfos()) {
			sb.append(",");
			sb.append(info.getColname());
			sb.append(":");
			sb.append(info.getColwidth());
			sb.append(":");
			sb.append(info.getOrder());
		}
		String s = sb.toString();
		if (s.length() > 0) {
			// 去掉第一个 逗号
			s = s.substring(1);
		}
		out.println("<classname>");
		out.println(skininfo.getClassname());
		out.println(skininfo.getExpr());
		out.println(s);
		out.println("</classname>");
	}

	private static void writelastmodify(PrintWriter pw, String lastmodify) {
		pw.println("<lastmodify>");
		pw.println(lastmodify);
		pw.println("</lastmodify>");

	}

	private static DBTableModel getDbTableModel() {
		Vector<DBColumnDisplayInfo> vector = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo dbcolumndisplayinfo = new DBColumnDisplayInfo(
				"colname", "varchar", "列名");
		dbcolumndisplayinfo.setReadonly(true);
		vector.add(dbcolumndisplayinfo);

		dbcolumndisplayinfo = new DBColumnDisplayInfo("colwidth", "number",
				"宽度");
		vector.add(dbcolumndisplayinfo);

		dbcolumndisplayinfo = new DBColumnDisplayInfo("orders", "number", "顺序");
		vector.add(dbcolumndisplayinfo);

		return new DBTableModel(vector);
	}

	public static void main(String[] args) {
		System.out.println(basedir);

	}

}
