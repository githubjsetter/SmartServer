package com.inca.np.gui.ste;

import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.mde.CMasterModel;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-28 Time: 9:47:23
 * 将DBColumnDisplayInfo的内容保存在文件中
 * <p/>
 * 文件格式 文件名：类名.coldef <form>
 * DBColumnDisplayInfo:=colname,coltype,title,editcomp,
 * ispk,seqname,upper,readonly,focusable,updateable </form>
 * <p/>
 * <table>
 * 列的次序:=colname[,colname]
 * </table>
 * <p/>
 * 格式：一行一行，用逗号分隔。boolean型用T、F表示
 */
public class DBColumnInfoStoreHelp {
	public static void writeFile(Vector<DBColumnDisplayInfo> formcolinfos,
			File file) throws Exception {
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.println("<form>");
		Enumeration<DBColumnDisplayInfo> en = formcolinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equals("行号")) {
				continue;
			}
			writeOneColumn(colinfo, out);
		}
		out.println("</form>");
		out.flush();
		out.close();
	}

	public static void writeFile(CSteModel ste, File file) throws Exception {
		PrintWriter out = new PrintWriter(new FileWriter(file));
		Vector<DBColumnDisplayInfo> formcolinfos = ste
				.getFormcolumndisplayinfos();
		out.println("<form>");
		Enumeration<DBColumnDisplayInfo> en = formcolinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equals("行号")) {
				continue;
			}
			writeOneColumn(colinfo, out);
		}
		out.println("</form>");

		// 保存列的顺序
		out.println("<table>");
		String[] colnames = ste.getTableColumns();
		for (int i = 0; i < colnames.length; i++) {
			if (i > 0)
				out.print(",");
			out.print(colnames[i]);
		}
		out.println();
		out.println("</table>");

		// 查询列
		out.println("<querycolumns>");
		Enumeration<String> enqueryc = ste.getQuerycolumns().elements();
		for (int i = 0; enqueryc.hasMoreElements(); i++) {
			if (i > 0)
				out.print(":");
			String querycolname = enqueryc.nextElement();
			out.print(querycolname);
			DBColumnDisplayInfo col = ste.getDBColumnDisplayInfo(querycolname);
			if (col == null) {
				if (ste instanceof CMasterModel) {
					col = ((CMasterModel) ste).getMdemodel().getDetailModel()
							.getDBColumnDisplayInfo(querycolname);
				}
			}

			if (col != null) {
				String callopid = col.getSubqueryopid();
				if (callopid.length() > 0) {
					out.print("," + callopid);
				}
			}
		}
		out.println();
		out.println("</querycolumns>");

		// 必须的查询条件列
		out.println("<querymustcolumns>");
		Iterator<String> itqm = ste.getQuerymustcolmap().keySet().iterator();
		for (int i = 0; itqm.hasNext(); i++) {
			String colname = itqm.next();
			if (i > 0)
				out.print(":");
			out.print(colname);
		}
		out.println();
		out.println("</querymustcolumns>");

		Vector<Hovdefine> hovdefs = ste.getHovdefines();
		writeHov(hovdefs, out);
		writeInitvalue(ste.getFormcolumndisplayinfos(), out);
		writeRowcheck(ste.getFormcolumndisplayinfos(), out);

		out.flush();
		out.close();
	}

	/**
	 * 从.model文件读入列、hov、行检查等
	 * 
	 * @param ste
	 * @param file
	 * @throws Exception
	 */
	public static void readFile(CSteModel ste, File file) throws Exception {
		readFormColumn(ste, file);
		readTableColumn(ste, file);
		readQueryColumn(ste, file);
		readQuerymustColumn(ste, file);
		Vector<Hovdefine> hovdefines = readHovFile(file);
		ste.setHovdefines(hovdefines);
		readInitvalue(ste.getFormcolumndisplayinfos(), file);
		readRowcheck(ste.getFormcolumndisplayinfos(), file);
	}

	public static BufferedReader getReaderFromFile(File file) throws Exception {
		String path = file.getPath();
		path = path.replaceAll("\\\\", "/");
		// System.out.println("path="+path);
		String jartarget = "jar:file:";
		if (path.startsWith(jartarget)) {
			path = path.substring("jar:file:".length());
		}
		// System.out.println("path1="+path);
		int p = path.indexOf("!");
		BufferedReader rd = null;
		if (p < 0) {
			if (path.startsWith("file:")) {
				path = path.substring(5);
			}
			rd = new BufferedReader(new FileReader(path));
		} else {
			String jarfile = path.substring(0, p);
			String filename = path.substring(p + 2);
			// System.out.println("jarfile="+jarfile);
			// System.out.println("filename="+filename);

			jarfile = jarfile.replaceAll("%20", " ");
			Category logger = Category.getInstance(DBColumnInfoStoreHelp.class);
			logger.debug("jarfile=" + jarfile);
			ZipFile zipFile = new ZipFile(new File(jarfile));
			ZipEntry entry = zipFile.getEntry(filename);
			if (entry == null) {
				throw new Exception("在" + jarfile + "中无法读" + filename);
			}
			InputStream in = zipFile.getInputStream(entry);
			rd = new BufferedReader(new InputStreamReader(in, "gbk"));
		}
		return rd;
	}

	private static void readFormColumn(CSteModel ste, File file)
			throws Exception {
		Vector<DBColumnDisplayInfo> infos = ste.getFormcolumndisplayinfos();
		BufferedReader rd = getReaderFromFile(file);

		try {
			String line;
			boolean bfind = true;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<form>")) {
					break;
				}
			}
			if (!bfind) {
				ste.setFormcolumndisplayinfos(infos);
				return;
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</form>")) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				infos.add(readOneColumn(line));
			}
			/*
			 * //复制一份，用于表格 Vector<DBColumnDisplayInfo> tableinfos = new
			 * Vector<DBColumnDisplayInfo>(); Enumeration<DBColumnDisplayInfo>
			 * en = infos.elements(); while (en.hasMoreElements()) {
			 * DBColumnDisplayInfo tmpinfo = en.nextElement();
			 * tableinfos.add(tmpinfo); }
			 * ste.setTablecolumndisplayinfos(tableinfos);
			 */

		} finally {
			rd.close();
		}
	}

	private static void readTableColumn(CSteModel ste, File file)
			throws Exception {
		Vector<DBColumnDisplayInfo> infos = ste.getFormcolumndisplayinfos();
		BufferedReader rd = getReaderFromFile(file);

		try {
			String line;
			boolean bfind = true;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<table>")) {
					break;
				}
			}
			if (!bfind) {
				return;
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</table>")) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}

				// 取列的次序
				String ss[] = line.split(",");
				ste.setTableColumns(ss);
			}

		} finally {
			rd.close();
		}
	}

	private static void readQueryColumn(CSteModel ste, File file)
			throws Exception {
		Vector<DBColumnDisplayInfo> infos = ste.getFormcolumndisplayinfos();
		BufferedReader rd = getReaderFromFile(file);

		try {
			String line;
			boolean bfind = true;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<querycolumns>")) {
					break;
				}
			}
			if (!bfind) {
				return;
			}

			Vector<String> querycolumns = new Vector<String>();
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</querycolumns>")) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}

				// 取列的次序
				String ss[] = line.split(":");
				for (int i = 0; i < ss.length; i++) {
					String colnamedefs = ss[i];
					querycolumns.add(colnamedefs);
				}
			}
			ste.setQuerycolumns(querycolumns);

		} finally {
			rd.close();
		}
	}

	private static void readQuerymustColumn(CSteModel ste, File file)
			throws Exception {
		Vector<DBColumnDisplayInfo> infos = ste.getFormcolumndisplayinfos();
		BufferedReader rd = getReaderFromFile(file);

		try {
			String line;
			boolean bfind = true;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<querymustcolumns>")) {
					break;
				}
			}
			if (!bfind) {
				return;
			}

			while ((line = rd.readLine()) != null) {
				if (line.startsWith("</querymustcolumns>")) {
					break;
				}
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}

				// 取列的次序
				String ss[] = line.split(":");
				for (int i = 0; i < ss.length; i++) {
					if (ss[i] != null && ss[i].length() > 0) {
						ste.addQuerymustcol(ss[i]);
					}
				}
			}

		} finally {
			rd.close();
		}
	}

	public static void writeOneColumn(DBColumnDisplayInfo colinfo,
			PrintWriter writer) throws Exception {
		writer.print(colinfo.getColname());
		writer.print("," + colinfo.getColtype());
		writer.print("," + colinfo.getTitle());
		writer.print("," + colinfo.getEditcomptype());
		writer.print("," + (colinfo.isIspk() ? "T" : "F"));
		writer.print("," + colinfo.getSeqname());
		writer.print("," + (colinfo.isUppercase() ? "T" : "F"));
		writer.print("," + (colinfo.isReadonly() ? "T" : "F"));
		writer.print("," + (colinfo.isFocusable() ? "T" : "F"));
		writer.print("," + (colinfo.isUpdateable() ? "T" : "F"));
		writer.print("," + (colinfo.isLinebreak() ? "T" : "F"));
		writer.print("," + colinfo.getNumberscale());
		writer.print("," + (colinfo.isHide() ? "T" : "F"));
		writer.print("," + (colinfo.isQueryable() ? "T" : "F"));
		writer.print("," + (colinfo.isCalcsum() ? "T" : "F"));
		String fmt = colinfo.getNumberDisplayformat();
		// 要把fmt中的","换成"，"
		fmt = fmt.replace(',', '，');
		writer.print("," + fmt);
		writer.print("," + (colinfo.isWithtime() ? "T" : "F"));
		writer.print("," + colinfo.getTablecolumnwidth());
		writer.print("," + (colinfo.isHidetitleoncard() ? "T" : "F"));
		writer.println();
		writer.flush();
	}

	public static DBColumnDisplayInfo readOneColumn(String line)
			throws Exception {
		String colname = "", coltype = "", title = "", comptype = "";
		boolean ispk = false;
		String seqname = "";
		boolean uppercase = false, readonly = false, focusable = false, updateable = false, linebreak = false;
		boolean hide = false;
		boolean queryable = true;
		boolean calcsum = false;
		int scale = 0;
		String displayformat = "";
		boolean withtime = false;
		boolean ishidetitleoncard = false;
		int tablecolumnwidth = -1;

		line += ",";
		char[] chars = line.toCharArray();
		StringBuffer sb = new StringBuffer();
		int fieldindex = 0;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == ',') {
				if (fieldindex == 0) {
					colname = sb.toString();
				} else if (fieldindex == 1) {
					coltype = sb.toString();
				} else if (fieldindex == 2) {
					title = sb.toString();
				} else if (fieldindex == 3) {
					comptype = sb.toString();
				} else if (fieldindex == 4) {
					ispk = sb.toString().equals("T");
				} else if (fieldindex == 5) {
					seqname = sb.toString();
				} else if (fieldindex == 6) {
					uppercase = sb.toString().equals("T");
				} else if (fieldindex == 7) {
					readonly = sb.toString().equals("T");
				} else if (fieldindex == 8) {
					focusable = sb.toString().equals("T");
				} else if (fieldindex == 9) {
					updateable = sb.toString().equals("T");
				} else if (fieldindex == 10) {
					linebreak = sb.toString().equals("T");
				} else if (fieldindex == 11) {
					try {
						scale = Integer.parseInt(sb.toString());
					} catch (NumberFormatException e) {

					}
				} else if (fieldindex == 12) {
					hide = sb.toString().equals("T");
				} else if (fieldindex == 13) {
					queryable = sb.toString().equals("T");
				} else if (fieldindex == 14) {
					calcsum = sb.toString().equals("T");
				} else if (fieldindex == 15) {
					displayformat = sb.toString();
					displayformat = displayformat.replace('，', ',');
				} else if (fieldindex == 16) {
					withtime = sb.toString().equals("T");
				} else if (fieldindex == 17) {
					try {
						tablecolumnwidth = Integer.parseInt(sb.toString());
					} catch (Exception e) {
						tablecolumnwidth = -1;
					}
				} else if (fieldindex == 18) {
					ishidetitleoncard = sb.toString().equals("T");
				} else {
					System.err.println("error,fieldindex cann't be "
							+ fieldindex);
				}

				sb.delete(0, sb.length());
				fieldindex++;
			} else {
				sb.append(c);
			}
		}
		DBColumnDisplayInfo colinfo = new DBColumnDisplayInfo(colname, coltype,
				title, linebreak);
		colinfo.setEditcomptype(comptype);
		colinfo.setIspk(ispk);
		colinfo.setSeqname(seqname);
		colinfo.setUppercase(uppercase);
		colinfo.setReadonly(readonly);
		colinfo.setFocusable(focusable);
		colinfo.setUpdateable(updateable);
		colinfo.setNumberscale(scale);
		colinfo.setHide(hide);
		colinfo.setQueryable(queryable);
		colinfo.setCalcsum(calcsum);
		colinfo.setNumberDisplayformat(displayformat);
		colinfo.setWithtime(withtime);
		colinfo.setTablecolumnwidth(tablecolumnwidth);
		colinfo.setHidetitleoncard(ishidetitleoncard);
		return colinfo;
	}

	private static void writeHov(Vector<Hovdefine> hovs, PrintWriter out)
			throws Exception {
		Enumeration<Hovdefine> en = hovs.elements();
		while (en.hasMoreElements()) {
			Hovdefine hov = en.nextElement();
			out.println("<hov>");
			out.println(hov.getUsecontext());
			out.print(hov.getInvokecolname());
			out.print("," + hov.getHovclassname());
			out.print(",");
			HashMap map = hov.getColpairmap();
			Iterator it = map.keySet().iterator();
			while (it.hasNext()) {
				String hovcolname = (String) it.next();
				String dbcolname = (String) map.get(hovcolname);
				out.print("(" + hovcolname + "," + dbcolname + ")");
			}
			out.println();
			out.println("</hov>");
			out.flush();
		}
	}

	private static Hovdefine createHov(String line) throws Exception {
		int p = line.indexOf(",");
		if (p < 0)
			return null;
		String invokecolname = line.substring(0, p);
		p++;
		int p1 = line.indexOf(",", p);
		if (p1 < 0)
			return null;
		String classname = line.substring(p, p1);
		p = p1 + 1;

		// 读列对应

		Hovdefine hov = new Hovdefine(classname, invokecolname);
		while (true) {
			p = line.indexOf("(", p);
			if (p < 0)
				break;
			p++;
			p1 = line.indexOf(")", p);
			if (p1 < 0)
				break;
			String s = line.substring(p, p1);
			int k = s.indexOf(",");
			String hovcolname = s.substring(0, k);
			String dbcolname = s.substring(k + 1);
			hov.putColpair(hovcolname, dbcolname);
			p = p1;
		}

		return hov;
	}

	public static Vector<Hovdefine> readHovFile(File f) throws Exception {
		Vector<Hovdefine> hovs = new Vector<Hovdefine>();
		BufferedReader rd = getReaderFromFile(f);
		try {
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<hov>")) {
					line = rd.readLine();
					if (line == null)
						break;
					String usecontext = "编辑查询";
					if (line.indexOf("编辑") >= 0 || line.indexOf("查询") >= 0) {
						usecontext = line;
						line = rd.readLine();
					}
					Hovdefine hovdefine = createHov(line);
					hovdefine.setUsecontext(usecontext);
					hovs.add(hovdefine);
				}
			}
		} finally {
			rd.close();
		}
		return hovs;
	}

	private static void writeInitvalue(Vector<DBColumnDisplayInfo> colinfos,
			PrintWriter out) throws Exception {
		Enumeration<DBColumnDisplayInfo> en = colinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getInitvalue() != null
					&& colinfo.getInitvalue().length() > 0) {
				out.println("<initvalue>");
				out
						.println(colinfo.getColname() + ","
								+ colinfo.getInitvalue());
				out.println("</initvalue>");
			}
		}
	}

	private static void readInitvalue(
			Vector<DBColumnDisplayInfo> formcolumndisplayinfos, File file)
			throws Exception {
		BufferedReader rd = getReaderFromFile(file);
		try {
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<initvalue>")) {
					line = rd.readLine();
					if (line == null)
						break;
					readInitvalue(formcolumndisplayinfos, line);
				}
			}
		} finally {
			rd.close();
		}
	}

	private static void readInitvalue(Vector<DBColumnDisplayInfo> colinfos,
			String line) throws Exception {
		int p = line.indexOf(",");
		if (p < 0)
			return;

		String colname = line.substring(0, p);
		String initvalue = line.substring(p + 1);

		Enumeration<DBColumnDisplayInfo> en = colinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equals(colname)) {
				colinfo.setInitvalue(initvalue);
			}
		}
	}

	private static void writeRowcheck(Vector<DBColumnDisplayInfo> colinfos,
			PrintWriter out) throws Exception {
		Enumeration<DBColumnDisplayInfo> en = colinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getRowcheck().getChecktype().length() > 0) {
				out.println("<rowcheck>");
				out.println(colinfo.getColname() + ","
						+ colinfo.getRowcheck().getChecktype() + ","
						+ colinfo.getRowcheck().getInfomessage());
				out.println("</rowcheck>");
			}
		}
	}

	private static void readRowcheck(
			Vector<DBColumnDisplayInfo> formcolumndisplayinfos, File file)
			throws Exception {
		BufferedReader rd = getReaderFromFile(file);
		try {
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<rowcheck>")) {
					line = rd.readLine();
					if (line == null)
						break;
					readRowcheck(formcolumndisplayinfos, line);
				}
			}
		} finally {
			rd.close();
		}
	}

	private static void readRowcheck(Vector<DBColumnDisplayInfo> colinfos,
			String line) throws Exception {
		int p = line.indexOf(",");
		if (p < 0)
			return;

		String colname = line.substring(0, p);
		line = line.substring(p + 1);
		p = line.indexOf(",");
		if (p < 0)
			return;
		String rowchecktype = line.substring(0, p);
		String infomsg = line.substring(p + 1);

		Enumeration<DBColumnDisplayInfo> en = colinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equals(colname)) {
				colinfo.getRowcheck().setChecktype(rowchecktype);
				colinfo.getRowcheck().setInfomessage(infomsg);
			}
		}
	}

	public static void main(String[] argv) {
		// File f = new
		// File("jar:file:/d:/np/release/np-SNAPSHOT.jar!/com/inca/np/demo/ste/Pub_goods_ste.model");
		File f = new File(
				"jar:file:C:/Documents%20and%20Settings/Administrator/.maven/repository/np/jars/np-SNAPSHOT.jar!/com/inca/np/demo/ste/Pub_goods_ste.model");
		String path = f.getPath();
		int p = path.indexOf("!");
		if (p > 0) {
			path = path.replaceAll("\\\\", "/");
			String jarfile = path.substring(0, p);
			String filename = path.substring(p + 2);

			try {
				// ZipFile zipFile = new ZipFile(new File(jarfile));
				DBColumnInfoStoreHelp.getReaderFromFile(f);
				System.out.println("ok");
				/*
				 * Enumeration<ZipEntry> en = (Enumeration<ZipEntry>)
				 * zipFile.entries(); while (en.hasMoreElements()) { ZipEntry
				 * entry = en.nextElement();
				 * System.out.println(entry.getName()); }
				 */

				/*
				 * ZipEntry entry = zipFile.getEntry(filename); String name =
				 * entry.getName(); InputStream in =
				 * zipFile.getInputStream(entry); int m; m = 3;
				 */} catch (Exception e) {
				e.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
			}
		}

	}
}
