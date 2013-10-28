package com.smart.platform.anyprint;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.PrintJob;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.swing.JOptionPane;

import org.apache.log4j.Category;

import com.smart.platform.anyprint.impl.DataprocRule;
import com.smart.platform.anyprint.impl.Parts;
import com.smart.platform.anyprint.impl.Partsprinter;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.print.printer.PrintsetupDialog;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.MD5Helper;
import com.smart.platform.util.SendHelper;

/**
 * ��ӡ����
 * 
 * @author Administrator
 * 
 */
public class Printplan {
	String planname;
	String plantype;
	Vector<Datasource> datasources = new Vector<Datasource>();
	DBTableModel fulldbmodel = null;
	boolean fetched = false;
	/**
	 * ���ڲ����õĲ�ѯ����
	 */
	String defaultinputparam = "";
	/**
	 * ��ӡ����
	 */
	Parts parts = new Parts(this);
	/**
	 * ��������
	 */
	HashMap<String, String> ccolnamemap = new HashMap<String, String>();

	/**
	 * ���ݴ������
	 */
	Vector<DataprocRule> dataprocrules = new Vector<DataprocRule>();

	Category logger = Category.getInstance(Printplan.class);

	String inputparam = "";
	
	/**
	 * ��ֹ�ٴ�ӡ.
	 */

	public boolean isForbidreprint() {
		return parts.isForbidreprint();
	}

	public void setForbidreprint(boolean forbidreprint) {
		parts.setForbidreprint(forbidreprint);
	}

	public String getPlanname() {
		return planname;
	}

	public void setPlanname(String planname) {
		this.planname = planname;
	}

	public String getPlantype() {
		return plantype;
	}

	public void setPlantype(String plantype) {
		this.plantype = plantype;
		parts.setPlantype(plantype);
	}

	public Printplan(String planname, String plantype) {
		super();
		this.planname = planname;
		this.plantype = plantype;
		parts.setPlantype(plantype);
	}

	public Vector<Datasource> getDatasources() {
		return datasources;
	}

	public void addDatasource(Datasource ds) {
		datasources.add(ds);
		defineChanged();
	}

	public HashMap<String, String> getCcolnamemap() {
		return ccolnamemap;
	}

	public DBTableModel createFulldatamodel() throws Exception {
		if (fulldbmodel != null)
			return fulldbmodel;

		DBTableModel dsdefinedbmodel = createDatasourcedbmodel();
		Enumeration<Datasource> en = this.datasources.elements();
		while (en.hasMoreElements()) {
			Datasource ds = en.nextElement();
			int row = dsdefinedbmodel.getRowCount();
			dsdefinedbmodel.appendRow();
			dsdefinedbmodel.setItemValue(row, "sql", ds.getSql());
			dsdefinedbmodel.setItemValue(row, "type", ds.getType());
			dsdefinedbmodel.setItemValue(row, "viewname", ds.getViewname());
		}
		ClientRequest req = new ClientRequest("npclient:builddatasource");
		DataCommand dcmd = new DataCommand();
		dcmd.setDbmodel(dsdefinedbmodel);
		req.addCommand(dcmd);
		SendHelper sh = new SendHelper();
		ServerResponse resp = sh.sendRequest(req);
		String cmd = resp.getCommand();
		if (!cmd.startsWith("+OK")) {
			throw new Exception(cmd);
		}
		DataCommand respdcmd = (DataCommand) resp.commandAt(1);
		DBTableModel dbmodel = respdcmd.getDbmodel();
		// ����ds��sql�������е���
		Vector<DBColumnDisplayInfo> allcols = dbmodel.getDisplaycolumninfos();
		// ������������
		Enumeration<DBColumnDisplayInfo> en1 = allcols.elements();
		while (en1.hasMoreElements()) {
			DBColumnDisplayInfo col = en1.nextElement();
			col.setTitle(col.getColname());
			String title = this.getCcolnamemap().get(
					col.getColname().toUpperCase());
			if (title != null && title.length() > 0) {
				col.setTitle(title);
			}
		}
		return fulldbmodel = new DBTableModel(allcols);
	}

	public void setInputparam(String inputparam) {
		fetched = inputparam.equals(this.inputparam);
		if (fetched)
			return;
		try {
			fetchData(inputparam);
			this.inputparam = inputparam;
			parts.setDatadirty(true);
			fetched = true;
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	/**
	 * 
	 * @return
	 */
	public DBTableModel getDbmodel() throws Exception {
		if (!fetched) {
			Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager()
					.getActiveWindow();
			if (w != null) {
				w.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
			try {
				fetchData(inputparam);
			} finally {
				if (w != null) {
					w.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
			fetched = true;
		}
		return fulldbmodel;
	}

	/**
	 * ����datamodel������ѯ����
	 * 
	 * @param inputparam
	 * @return
	 * @throws Exception
	 */
	private void fetchData(String inputparam) throws Exception {
		DBTableModel dsdefinedbmodel = createDatasourcedbmodel();
		Enumeration<Datasource> en = this.datasources.elements();
		while (en.hasMoreElements()) {
			Datasource ds = en.nextElement();
			int row = dsdefinedbmodel.getRowCount();
			dsdefinedbmodel.appendRow();
			dsdefinedbmodel.setItemValue(row, "sql", ds.getSql());
			dsdefinedbmodel.setItemValue(row, "type", ds.getType());
			dsdefinedbmodel.setItemValue(row, "viewname", ds.getViewname());
		}
		ClientRequest req = new ClientRequest("npclient:fetchdatasource");
		DataCommand dcmd = new DataCommand();
		dcmd.setDbmodel(dsdefinedbmodel);
		req.addCommand(dcmd);
		ParamCommand pcmd = new ParamCommand();
		pcmd.addParam("inputparam", inputparam);
		req.addCommand(pcmd);
		SendHelper sh = new SendHelper();
		ServerResponse resp = sh.sendRequest(req);
		String cmd = resp.getCommand();
		if (!cmd.startsWith("+OK")) {
			throw new Exception(cmd);
		}
		DataCommand respdcmd = (DataCommand) resp.commandAt(1);

		fulldbmodel = createFulldatamodel();
		fulldbmodel.clearAll();
		fulldbmodel.appendDbmodel(respdcmd.getDbmodel());

		/**
		 * ��������д���
		 */
		procData();
	}

	public void write(PrintWriter out) throws Exception {
		out.println(planname);
		out.println(plantype);
		Enumeration<Datasource> en = datasources.elements();
		while (en.hasMoreElements()) {
			en.nextElement().write(out);
		}
		out.println("<colname>");
		Iterator<String> it = (Iterator<String>) ccolnamemap.keySet()
				.iterator();
		while (it.hasNext()) {
			String colname = it.next();
			String cname = ccolnamemap.get(colname);
			out.println(colname + ":" + cname);
		}
		out.println("</colname>");

		Enumeration<DataprocRule> enr = dataprocrules.elements();
		while (enr.hasMoreElements()) {
			DataprocRule r = enr.nextElement();
			r.write(out);
		}

		parts.write(out);
		out.flush();
	}

	public void read(File f) throws Exception {
		BufferedReader rd = new BufferedReader(new FileReader(f));
		planname = rd.readLine();
		plantype = rd.readLine();
		parts.setPlantype(plantype);
		datasources.clear();
		datasources.addAll(Datasource.read(rd));
		rd.close();

		// �����ٶ�
		rd = new BufferedReader(new FileReader(f));
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<colname>")) {
				readColname(rd);
			}
		}
		rd.close();

		// �����ٶ�������parts
		rd = new BufferedReader(new FileReader(f));
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<parts>")) {
				parts.read(rd);
			}
		}
		rd.close();

		// ����һ��
		setPlantype(plantype);

		// �����ٶ�������dataproc
		rd = new BufferedReader(new FileReader(f));
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("<dataprocrule>")) {
				DataprocRule r = new DataprocRule("");
				r.read(rd);
				dataprocrules.add(r);
			}
		}
		rd.close();

	}

	void readColname(BufferedReader rd) throws Exception {
		String line;
		while ((line = rd.readLine()) != null) {
			if (line.startsWith("</colname>")) {
				break;
			}
			String ss[] = line.split(":");
			if (ss.length == 2) {
				ccolnamemap.put(ss[0], ss[1]);
			}
		}
	}

	public Parts getParts() {
		return parts;
	}

	public void setParts(Parts parts) {
		this.parts = parts;
	}

	public void defineChanged() {
		fulldbmodel = null;
		inputparam = "";
		fetched = false;
	}

	DBTableModel createDatasourcedbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("sql", "varchar", "sql");
		cols.add(col);

		col = new DBColumnDisplayInfo("type", "varchar", "����Դ����");
		cols.add(col);

		col = new DBColumnDisplayInfo("viewname", "varchar", "����������ͼ");
		cols.add(col);
		return new DBTableModel(cols);

	}

	/**
	 * ��������
	 */
	protected void procData() {
		Enumeration<DataprocRule> en = dataprocrules.elements();
		while (en.hasMoreElements()) {
			DataprocRule rule = en.nextElement();
			try {
				rule.process(fulldbmodel);
			} catch (Exception e) {
				logger.error("error", e);
			}
		}
	}

	public void clearDataprocrule() {
		dataprocrules.clear();
	}

	public void addDataprocrule(DataprocRule rule) {
		dataprocrules.add(rule);
	}

	public Vector<DataprocRule> getProcrule() {
		return dataprocrules;
	}

	public String[] getSplitcolumns() {
		Enumeration<DataprocRule> en = dataprocrules.elements();
		while (en.hasMoreElements()) {
			DataprocRule rule = en.nextElement();
			if (rule.getRuletype().equals(DataprocRule.RULETYPE_SPLITPAGE)) {
				return rule.getExpr().split(":");
			}
		}
		return new String[0];
	}

	public String getDefaultinputparam() {
		return defaultinputparam;
	}

	public void setDefaultinputparam(String defaultinputparam) {
		this.defaultinputparam = defaultinputparam;
	}

	public boolean isFetched() {
		return fetched;
	}

	public void setFetched(boolean fetched) {
		this.fetched = fetched;
	}

	/**
	 * ����������еģ�������
	 */
	public void reset() {
		datasources = new Vector<Datasource>();
		fulldbmodel = null;
		fetched = false;
		defaultinputparam = "";
		parts = new Parts(this);
		ccolnamemap = new HashMap<String, String>();
		dataprocrules = new Vector<DataprocRule>();
		inputparam = "";

	}

	public void sendPrinter(PrinterJob printerjob, PageFormat curpageformat,
			HashPrintRequestAttributeSet prats,int printstartcopy,int printendcopy) throws Exception {
		
		for(int c=printstartcopy;c<=printendcopy;c++){
			getParts().setPrintcopys(printendcopy);
			getParts().setPrintcopy(c);
			Partsprinter pp=new Partsprinter(getParts(),curpageformat);
			printerjob.setPrintable(pp);
			printerjob.setPageable(pp);
			printerjob.print(prats);
		}

/*		Partsprinter pp = new Partsprinter(getParts(), curpageformat);
		printerjob.setPrintable(pp);
		printerjob.setPageable(pp);
		printerjob.print(prats);
*/	}

	public void sendPrinter() throws Exception {
		PrinterJob printerjob = PrinterJob.getPrinterJob();
		PrintsetupDialog psetupdlg = new PrintsetupDialog((Frame) null,
				printerjob,getPlanname(),getParts().getPagecount());
		psetupdlg.pack();
		psetupdlg.setVisible(true);
		if (!psetupdlg.getOk())
			return;

		String printername = psetupdlg.getPrintername();
		PrintService printservice = null;
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
				null, null);
		for (int i = 0; i < printServices.length; i++) {
			if (printServices[i].getName().equals(printername)) {
				try {
					printerjob.setPrintService(printServices[i]);
				} catch (PrinterException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		HashPrintRequestAttributeSet prats = new HashPrintRequestAttributeSet();
		// ���ô�ӡ��
		double pw, ph;
		// ����pw ph��mm
		pw = psetupdlg.getPaperwidth();
		ph = psetupdlg.getPaperheight();

		double px = 0;
		double py = 0;

		px = 0;
		py = 0;

		pw = pw / 25.4 * 72.0;
		ph = ph / 25.4 * 72.0;
		Paper paper = new Paper();
		paper.setImageableArea(px, py, pw, ph);
		paper.setSize(pw, ph);
		PageFormat curpageformat = printerjob.defaultPage();
		curpageformat.setPaper(paper);

		// ����ҳ��Χ
		PageRanges prang = new PageRanges(psetupdlg.getStartpage(), psetupdlg
				.getEndpage());
		prats.add(prang);

		// ���÷�
		//Copies pcopies = new Copies(psetupdlg.getCopies());
		//prats.add(pcopies);
		int printstartcopy=0;
		int printendcopy=0;
		int copymode=psetupdlg.getCopymode();
		if(copymode==0){
			printstartcopy = printendcopy = psetupdlg.getCopies();
		}else{
			printstartcopy=psetupdlg.getCopies1();
			printendcopy=psetupdlg.getCopies2();
		}

		if(parts.isLandscape()){
			prats.add(OrientationRequested.LANDSCAPE);
		}
		
		sendPrinter(printerjob, curpageformat, prats,printstartcopy,printendcopy);
	}

	public static void uploadPrintplan(File planfile) throws Exception {
		int length = (int) planfile.length();
		int buflen = 102400;
		byte[] buf = new byte[buflen];
		int totalsend = 0;
		FileInputStream fin = new FileInputStream(planfile);
		try {
			while (length > 0) {
				ClientRequest req = new ClientRequest("npclient:�ϴ���ӡ����");
				ParamCommand pcmd = new ParamCommand();
				req.addCommand(pcmd);
				int rd = fin.read(buf);
				pcmd.addParam("planfilename", planfile.getName());
				pcmd.addParam("length", String.valueOf(rd));
				pcmd.addParam("startpos", String.valueOf(totalsend));
				totalsend += rd;
				length -= rd;
				pcmd.addParam("finished", length == 0 ? "true" : "false");
				BinfileCommand bincmd = new BinfileCommand(buf, 0, rd);
				req.addCommand(bincmd);
				ServerResponse resp = SendHelper.sendRequest(req);
				String resultcmd = resp.getCommand();
				if (!resultcmd.startsWith("+OK"))
					throw new Exception(resultcmd);
			}
		} finally {
			if (fin != null)
				fin.close();
		}
	}

	/**
	 * �����ļ�
	 * @param planfilename
	 * @param planfile
	 * @return
	 * @throws Exception
	 */
	public static File downloadPrintplan(File planfile) throws Exception {
		String clientmd5 = "not exists";
		if (planfile.exists()) {
			clientmd5 = MD5Helper.MD5(planfile);
		}
		planfile.getParentFile().mkdirs();
		// ����
		int startpos = 0;
		for (;;) {
			ClientRequest req = new ClientRequest("npclient:���ش�ӡ����");
			ParamCommand pcmd = new ParamCommand();
			req.addCommand(pcmd);
			pcmd.addParam("planfilename", planfile.getName());
			pcmd.addParam("clientmd5", clientmd5);
			pcmd.addParam("startpos", String.valueOf(startpos));

			ServerResponse resp = SendHelper.sendRequest(req);

			// Thread.sleep(5000);

			String respstr = resp.getCommand();
			if (respstr.startsWith("-ERROR"))
				throw new Exception(respstr);

			ParamCommand respcmd = (ParamCommand) resp.commandAt(1);
			int length = Integer.parseInt(respcmd.getValue("length"));
			String finished = respcmd.getValue("finished");

			if (length == 0) {
				break;
			}
			//int totallength = Integer.parseInt(respcmd.getValue("totallength"));
			FileOutputStream fout = null;
			try {
				planfile.getParentFile().mkdirs();
				fout = new FileOutputStream(planfile, startpos != 0);
				BinfileCommand bcmd = (BinfileCommand) resp.commandAt(2);
				fout.write(bcmd.getBindata());
			} finally {
				if (fout != null) {
					fout.close();
				}
			}
			startpos += length;

			if (finished.equals("true"))
				break;
		}
		return planfile;
	}

	public static void main(String[] args) {
		// �򿪶Ի������ô�ӡ����
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.prodcontext = "npserver";

		
		File configfile = new File("��ӡ����/����СƱ.printplan");
		Printplan plan = new Printplan("", "");
		try {
			Printplan.downloadPrintplan(configfile);
			plan.read(configfile);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		plan.setInputparam("166");
		try {
			plan.getParts().prepareData();
			plan.sendPrinter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
