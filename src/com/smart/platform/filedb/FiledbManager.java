package com.smart.platform.filedb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.CommandFactory;
import com.smart.platform.communicate.CommandHead;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DefaultNPParam;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-6-27 Time: 10:29:39
 * 1 将数据库中的数据搬到本地文件。 2 将需要保存到数据库的数据先暂存在文件
 */
public class FiledbManager {
	private FiledbManager() {
	}

	static Category logger = Category.getInstance(FiledbManager.class);
	private static FiledbManager inst = null;

	public static FiledbManager getInstance() {
		if (inst == null) {
			inst = new FiledbManager();
		}
		return inst;
	}

	private static File storedir = new File("filedb");

	public static File[] listFiles() {
		if (storedir == null) {
			return null;
		}
		return storedir.listFiles();
	}

	/**
	 * 从远程服务器下载数据。
	 * 
	 * @param sql
	 *            查询语句
	 * @param fn
	 *            文件名
	 * @throws Exception
	 */
	public int downloadData(String sql, String fn) throws Exception {
		RemotesqlHelper sqlhelper = new RemotesqlHelper();

		int r = 0;
		int rowcount = DefaultNPParam.fetchmaxrow;

		DBTableModel resultdbmodel = null;
		if (!storedir.exists()) {
			storedir.mkdirs();
		}
		File outf = new File(storedir, fn);

		int ct = 0;
		while (true) {
			ct++;
			DBTableModel dbmodel = sqlhelper.doSelect(sql, r, rowcount);
			if (ct == 1) {
				saveFile(dbmodel, outf);
			} else {
				appendFile(dbmodel, outf);
			}
			r += dbmodel.getRowCount();
			logger.info("脱机下载,已下载" + r + "条记录");
			if (!dbmodel.hasmore()) {
				break;
			}

		}
		return r;
	}

	public static DBTableModel loadDatafile(String fn) throws Exception {
		File inf = new File(storedir, fn);
		InputStream in = new BufferedInputStream(new FileInputStream(inf),
				10240);
		DataCommand datacmd = (DataCommand) CommandFactory.readCommand(in);
		return datacmd.getDbmodel();
	}

	public static void saveRequestFile(ClientRequest req, String fn)
			throws Exception {
		FileOutputStream out = null;
		try {
			if (!storedir.exists())
				storedir.mkdirs();
			File outf = new File(storedir, fn);
			out = new FileOutputStream(outf);
			for (int i = 0; i < req.getCommandcount(); i++) {
				CommandBase cmd = req.commandAt(i);
				CommandFactory.writeCommand(cmd, out);
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static ClientRequest loadRequestFile(String fn) throws Exception {
		InputStream in = null;
		ClientRequest req = new ClientRequest();
		try {
			if (!storedir.exists())
				return null;
			File inf = new File(storedir, fn);
			in = new BufferedInputStream(new FileInputStream(inf));
			while (true) {
				try {
					CommandBase cmd = CommandFactory.readCommand(in);
					req.addCommand(cmd);
				} catch (Exception e) {
					break;
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return req;
	}

	public static void saveFile(DBTableModel dbmodel, File outf)
			throws Exception {
		DataCommand datacmd = new DataCommand();
		datacmd.setDbmodel(dbmodel);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outf);
			CommandFactory.writeCommand(datacmd, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 在文件后面附加数据
	 * 
	 * @param dbmodel
	 * @param outf
	 * @throws Exception
	 */
	public static void appendFile(DBTableModel dbmodel, File outf)
			throws Exception {
		DataCommand datacmd = new DataCommand();
		datacmd.setDbmodel(dbmodel);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outf, true);
			CommandFactory.writeCommand(datacmd, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * 在内存搜索
	 * 
	 * @param srcmodel
	 * @param colname
	 *            列名
	 * @param op
	 *            操作符。= 或 like
	 * @param searchvalue
	 *            要查询的值。不要带%
	 * @return
	 */
	public static DBTableModel search(DBTableModel srcmodel, String colname,
			String op, String searchvalue) {
		DBTableModel resultdbmodel = new DBTableModel(srcmodel
				.getDisplaycolumninfos());
		for (int i = 0; i < srcmodel.getRowCount(); i++) {
			String colvalue = srcmodel.getItemValue(i, colname);
			boolean ok = false;
			if (op.equals("=")) {
				if (colvalue.equals(searchvalue)) {
					ok = true;
				}
			} else if (op.equalsIgnoreCase("like")) {
				if (colvalue.indexOf(searchvalue) >= 0) {
					ok = true;
				}
			}

			if (ok) {
				resultdbmodel.getDataVector().add(srcmodel.getRecordThunk(i));
			}
		}
		return resultdbmodel;
	}

	/**
	 * 从文件查询
	 * 
	 * @param localfile
	 * @param filedbconds
	 * @return
	 */
	public static DBTableModel searchFile(String filename,
			FiledbSearchCond[] filedbconds, int maxresultcount)
			throws Exception {
		File localfile = new File(storedir, filename);
		BufferedInputStream in = null;
		DBTableModel result = null;
		try {
			in = new BufferedInputStream(new FileInputStream(localfile));
			while (true) {
				CommandHead head = new CommandHead();
				head.read(in);
				if (!head.commandtype.equals(CommandHead.COMMANDTYPE_DATA)) {
					throw new Exception("应该是COMMANDTYPE_DATA");
				}
				int colct = CommandFactory.readShort(in);
				if (colct < 0)
					break;
				Vector<DBColumnDisplayInfo> displaycolumninfos = new Vector<DBColumnDisplayInfo>();
				for (int i = 0; i < colct; i++) {
					DBColumnDisplayInfo info = new DBColumnDisplayInfo("", "");
					info.readData(in);
					displaycolumninfos.add(info);
				}

				if (result == null) {
					result = new DBTableModel(displaycolumninfos);
				}
				int recct = CommandFactory.readShort(in);
				// 判断列序
				for (int i = 0; i < filedbconds.length; i++) {
					FiledbSearchCond cond = filedbconds[i];
					Enumeration<DBColumnDisplayInfo> en = displaycolumninfos
							.elements();
					for (int j = 0; en.hasMoreElements(); j++) {
						DBColumnDisplayInfo colinfo = en.nextElement();
						if (colinfo.getColname().equalsIgnoreCase(cond.colname)) {
							cond.colindex = j;
							break;
						}
					}
					if (cond.colindex < 0) {
						String errormsg = "searchFile file="
								+ localfile.getAbsolutePath() + "中没有列"
								+ cond.colname;
						logger.error(errormsg);
						throw new Exception(errormsg);
					}
				}
				for (int i = 0; i < recct; i++) {
					RecordTrunk rec = RecordTrunk.readData(in);
					boolean bok = true;
					for (int j = 0; j < filedbconds.length; j++) {
						FiledbSearchCond cond = filedbconds[j];
						boolean bret = cond.match(rec);
						if (!bret) {
							bok = false;
							break;
						}
					}

					if (bok) {
						result.appendRecord(rec);
						if (result.getRowCount() > maxresultcount) {
							break;
						}
					}
				}
				int hasmore=in.read();
				if(hasmore!=1){
					break;
				}
				if (result.getRowCount() > maxresultcount) {
					break;
				}
			}
			return result;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static void main(String[] argv) {
		FiledbManager filedb = FiledbManager.getInstance();
		String sql = "select goodsid,goodsname,opcode,goodstype,goodsunit,prodarea from pub_goods";
		String sql1 = "select * from pub_goods_detail";
		try {
			// filedb.downloadData(sql, "pub_goods");
			// filedb.downloadData(sql1, "pub_goods_detail");

			FiledbSearchCond cond = new FiledbSearchCond();
			cond.colname = "opcode";
			cond.op = "like";
			cond.value = "2";

			FiledbSearchCond conds[] = new FiledbSearchCond[1];
			conds[0] = cond;

			DBTableModel dbmodel = filedb.searchFile("pub_goods", conds, 100);
			System.out.println("result get rowcount=" + dbmodel.getRowCount());

		} catch (Exception e) {
			e.printStackTrace(); // To change body of catch statement use
			// File | Settings | File Templates.
		}
	}
}
