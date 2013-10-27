package com.inca.npbi.client.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.communicate.BinfileCommand;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.tbar.TBar;
import com.inca.np.gui.tbar.TButton;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MD5Helper;
import com.inca.np.util.SendHelper;
import com.inca.npx.ste.Apinfo;
import com.inca.npx.ste.ApinfoDbmodel;

/**
 * 用功能形式展示报表
 * 
 * @author user
 * 
 */
public class BIReportFrame extends ReportcanvasFrame implements ReportApIF {

	private JSpinner spScale;
	protected HashMap apmap = new HashMap();
	protected boolean aploaded;

	public BIReportFrame() throws HeadlessException {
		super();
		setTitle("报表查询");
		setAutoquery(true);
		// 运行状态,不是设计制作状态
		canvas.setDeveoping(false);

		// 缺省135%
		canvas.setXyscale(1.35);
		Dimension scrsize = getToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) scrsize.getWidth(), (int) scrsize
				.getHeight() - 25));
		setLocation(0, 0);

	}

	@Override
	public void setOpid(String opid) {
		super.setOpid(opid);
		// 当前功能ID即为报表ID
		this.rptopid = opid;
		// 下载授权属性
		downloadAp(ClientUserManager.getCurrentUser().getRoleid());
		try {
			bifile = downloadBireport();
			loadFromfile(bifile);
			if (isAutoquery()) {
				Runnable r = new Runnable() {
					public void run() {
						if (isAutoquery()) {
							doRetrieve();
						}
					}
				};

				SwingUtilities.invokeLater(r);
			}
			
			//从授权属性   设置数据源的SQL  
			dsdefine.sql = getSelectSql();
		} catch (Exception e) {
			errorMessage("错误", e.getMessage());
			return;
		}

	}

	@Override
	protected void init() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		JPanel tb = createToolbar();
		cp.add(tb, BorderLayout.NORTH);

		JTabbedPane maintabbpane = new JTabbedPane();
		maintabbpane.addChangeListener(new MaintabpaneHandler());
		// cp.add(maintabbpane, BorderLayout.CENTER);

		dspane = new BIReportdsPane(this);
		// maintabbpane.add("数据源", dspane);

		designpane = new Tablevdesignpane(this);
		// maintabbpane.add("垂直表", designpane);

		Layoutpanel dp = new Layoutpanel();
		cp.add(createCanvaspane(), BorderLayout.CENTER);
	}

	protected JPanel createCanvaspane() {
		JPanel rightpane = new JPanel();
		rightpane.setLayout(new BorderLayout());

		canvasscrollp = new JScrollPane(canvas);
		// canvasscrollp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		canvasscrollp.setPreferredSize(new Dimension(700, 550));
		rightpane.add(canvasscrollp, BorderLayout.CENTER);
		return rightpane;
	}

	@Override
	protected JPanel createToolbar() {
		TBar tb = new TBar();
		TButton btn;

		SpinnerNumberModel maxrowspinner = new SpinnerNumberModel(70000, 10,
				70000, 100);
		jspinMaxrow = new JSpinner(maxrowspinner);

		btn = new TButton("查询数据");
		btn.setActionCommand("retrieve");
		btn.addActionListener(this);
		tb.add(btn);

		Dimension spinsize = new Dimension(50, 20);
		JLabel lb = new JLabel("显示比例");
		tb.add(lb);

		// 缺i
		SpinnerNumberModel spnm = new SpinnerNumberModel(135, 25, 500, 10);
		spScale = new JSpinner(spnm);
		spScale.setPreferredSize(spinsize);
		spScale.setMinimumSize(spinsize);

		tb.add(spScale);
		spScale.addChangeListener(new SpscaleHandler());
		lb = new JLabel("%　　");
		tb.add(lb);

		lbpagecount = new JLabel("共" + pagecount + "页");
		tb.add(lbpagecount);

		btn = new TButton("跳转到页");
		btn.setActionCommand("gotopage");
		btn.addActionListener(this);
		tb.add(btn);

		SpinnerNumberModel spnumbermodel = new SpinnerNumberModel(pageno, 0,
				pagecount, 1);
		spPageno = new JSpinner(spnumbermodel);
		spPageno.addChangeListener(new SppagenoHandler());
		spPageno.setPreferredSize(spinsize);
		spPageno.setMaximumSize(spinsize);
		spPageno.setMinimumSize(spinsize);
		tb.add(spPageno);

		btn = new TButton("首页");
		btn.setActionCommand("first");
		btn.addActionListener(new PageAction("first"));
		tb.add(btn);

		btn = new TButton("上一页");
		btn.setActionCommand("prior");
		btn.addActionListener(new PageAction("prior"));
		tb.add(btn);

		btn = new TButton("下一页");
		btn.setActionCommand("next");
		btn.addActionListener(new PageAction("next"));
		tb.add(btn);

		btn = new TButton("末页");
		btn.setActionCommand("last");
		btn.addActionListener(new PageAction("last"));
		tb.add(btn);

		btn = new TButton("纸张设置");
		btn.setActionCommand("setuppaper");
		btn.addActionListener(this);
		tb.add(btn);

		if (!isForbidPrint()) {
			btn = new TButton("打印");
			btn.setActionCommand("doprint");
			btn.addActionListener(this);
			tb.add(btn);
		}
		if (!isForbidExport()) {
			btn = new TButton("导出");
			btn.setActionCommand("export");
			btn.addActionListener(this);
			tb.add(btn);
		}
		btn = new TButton("关闭");
		btn.setActionCommand("exit");
		btn.addActionListener(this);
		tb.add(btn);

		// btn = new JButton("输出图");
		// btn.setActionCommand("exportpng");
		// btn.addActionListener(this);
		// tb.add(btn);

		return tb;

	}

	File downloadBireport() throws Exception {
		File reportfile = new File("BI报表", rptopid + ".npbi");

		String clientmd5 = "not exists";
		if (reportfile.exists()) {
			clientmd5 = MD5Helper.MD5(reportfile);
		}
		reportfile.getParentFile().mkdirs();
		// 下载
		int startpos = 0;
		for (;;) {
			ClientRequest req = new ClientRequest("npclient:下载BI报表");
			ParamCommand pcmd = new ParamCommand();
			req.addCommand(pcmd);
			pcmd.addParam("opid", rptopid);
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
			// int totallength =
			// Integer.parseInt(respcmd.getValue("totallength"));
			FileOutputStream fout = null;
			try {
				reportfile.getParentFile().mkdirs();
				fout = new FileOutputStream(reportfile, startpos != 0);
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
		return reportfile;

	}

	class SpscaleHandler implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			Integer ii = (Integer) spScale.getValue();
			canvas.setXyscale(((double) ii.intValue()) / 100.0);
			recalcScrollpane(null);
			// canvas.repaint();
		}

	}

	class PageAction extends AbstractAction {

		public PageAction(String name) {
			super(name);
			putValue(PageAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("first")) {
				if (canvas.getPagecount() > 0) {
					canvas.setPageno(0);
					canvas.repaint();
					spPageno.setValue(1);
				}
			} else if (e.getActionCommand().equals("next")) {
				int p = canvas.getPageno() + 1;
				if (p < canvas.getPagecount()) {
					canvas.setPageno(p);
					canvas.repaint();
					spPageno.setValue(p + 1);
				}

			} else if (e.getActionCommand().equals("prior")) {
				int p = canvas.getPageno() - 1;
				if (p < 0)
					p = 0;
				canvas.setPageno(p);
				canvas.repaint();
				spPageno.setValue(p + 1);
			} else if (e.getActionCommand().equals("last")) {
				if (canvas.getPagecount() > 0) {
					canvas.setPageno(canvas.getPagecount() - 1);
					canvas.repaint();
					spPageno.setValue(canvas.getPagecount());
				}
			}
		}

	}

	public boolean setupAp(String s) {
		if (!downloadAp(s)) {
			errorMessage("不能设置", "下载授权属性失败,不能设置");
			return false;
		}
		BIReportSetupApDialog apsetupdialog;
		(apsetupdialog = new BIReportSetupApDialog(this, getTitle() + "设置授权属性",
				this)).pack();
		apsetupdialog.setVisible(true);
		if (!apsetupdialog.getOk())
			return false;
		Vector vector = apsetupdialog.getApinfos();
		if (!saveAp(vector, s))
			return false;
		return downloadAp(s);
	}

	protected boolean downloadAp(String s) {
		ClientRequest clientrequest;
		clientrequest = new ClientRequest("np:查询授权属性");
		ParamCommand paramcommand = new ParamCommand();
		clientrequest.addCommand(paramcommand);
		paramcommand.addParam("roleid", s);
		if (getOpid() == null || getOpid().length() == 0)
			return false;
		paramcommand.addParam("opid", getOpid());
		ServerResponse serverresponse;
		aploaded = false;
		StringCommand stringcommand;
		DBTableModel dbtablemodel;
		try {
			serverresponse = SendHelper.sendRequest(clientrequest);
			stringcommand = (StringCommand) serverresponse.commandAt(0);
			if (stringcommand.getString().startsWith("+OK")) {
				DataCommand datacommand = (DataCommand) serverresponse
						.commandAt(2);
				dbtablemodel = datacommand.getDbmodel();
				aploaded = true;
				apmap.clear();
				for (int i = 0; i < dbtablemodel.getRowCount(); i++) {
					dbtablemodel.getItemValue(i, "roleopid");
					dbtablemodel.getItemValue(i, "apid");
					String apname = dbtablemodel.getItemValue(i, "apname");
					String aptype = dbtablemodel.getItemValue(i, "aptype");
					String apvalue = dbtablemodel.getItemValue(i, "apvalue");
					Apinfo apinfo = new Apinfo(apname, aptype);
					apinfo.setApvalue(apvalue);
					logger.info((new StringBuilder("download apname=")).append(
							apinfo.getApname()).append(",aptype=").append(
							apinfo.getAptype()).append(",apvalue=").append(
							apinfo.getApvalue()).toString());
					apmap.put(apname, apinfo);
				}

				return true;

			} else {
				errorMessage("下载授权属性错误", stringcommand.getString());
				return false;
			}
		} catch (Exception exception) {
			logger.error("ERROR", exception);
			errorMessage("下载授权属性错误", exception.getMessage());
			setVisible(false);
			dispose();
			return false;
		}

	}

	protected boolean saveAp(Vector vector, String s) {
		ApinfoDbmodel apinfodbmodel = new ApinfoDbmodel();
		Apinfo apinfo;
		int i;
		for (Enumeration enumeration = vector.elements(); enumeration
				.hasMoreElements(); apinfodbmodel.setItemValue(i, "apvalue",
				apinfo.getApvalue())) {
			apinfo = (Apinfo) enumeration.nextElement();
			apinfodbmodel.appendRow();
			i = apinfodbmodel.getRowCount() - 1;
			apinfodbmodel.setItemValue(i, "apname", apinfo.getApname());
			apinfodbmodel.setItemValue(i, "aptype", apinfo.getAptype());
		}

		ClientRequest clientrequest = new ClientRequest("np:保存授权属性");
		ParamCommand paramcommand = new ParamCommand();
		clientrequest.addCommand(paramcommand);
		paramcommand.addParam("opid", getOpid());
		paramcommand.addParam("roleid", s);
		DataCommand datacommand = new DataCommand();
		clientrequest.addCommand(datacommand);
		datacommand.setDbmodel(apinfodbmodel);
		try {
			ServerResponse serverresponse = SendHelper
					.sendRequest(clientrequest);
			StringCommand stringcommand = (StringCommand) serverresponse
					.commandAt(0);
			if (stringcommand.getString().startsWith("+OK")) {
				return true;
			} else {
				errorMessage("错误", stringcommand.getString());
			}
		} catch (Exception exception) {
			logger.error("ERROR", exception);
			errorMessage("错误", exception.getMessage());
			return false;
		}
		return false;
	}

	public Apinfo getApinfo(String s) {
		if (!aploaded)
			return null;
		else
			return (Apinfo) apmap.get(s);
	}

	public String getApvalue(String s) {
		Apinfo apinfo = getApinfo(s);
		if (apinfo == null)
			return "";
		else
			return apinfo.getApvalue();
	}

	public boolean isForbidExport() {
		if (!aploaded && !this.rptopid.equals("")) {
			downloadAp(ClientUserManager.getCurrentUser().getRoleid());
		}
		String s = getApvalue(FORBID_EXPORT);
		if (s != null && s.equals("true")) {
			return true;
		}
		return false;
	}

	public boolean isForbidPrint() {
		if (!aploaded && !this.rptopid.equals("")) {
			downloadAp(ClientUserManager.getCurrentUser().getRoleid());
		}
		String s = getApvalue(FORBID_PRINT);
		if (s != null && s.equals("true")) {
			return true;
		}
		return false;
	}

	public String getSelectSql() {
		if (!aploaded && !this.rptopid.equals("")) {
			downloadAp(ClientUserManager.getCurrentUser().getRoleid());
		}
		String s = getApvalue(REPORT_SQL);
		if (s != null && !"".equals(s)) {
			return s;
		}
		return dsdefine.getSql();

	}

	public void actionPerformed(ActionEvent actionevent) {
		String s1 = actionevent.getActionCommand();
		if ("doprint".equals(s1)) {
			if (!isForbidPrint()) {
				super.actionPerformed(actionevent);
			} else {
				warnMessage("提示", "您没有打印报表的权限");
			}
		} else if ("export".equals(s1)) {
			if (!isForbidExport()) {
				super.actionPerformed(actionevent);
			} else {
				warnMessage("提示", "您没有导出报表的权限");
			}
		} else {
			super.actionPerformed(actionevent);
		}
	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";
		ClientUserManager.getCurrentUser().setUserid("0");
		ClientUserManager.getCurrentUser().setRoleid("0");
		ClientUserManager.getCurrentUser().setEntryid("1");

		BIReportFrame frm = new BIReportFrame();
		frm.pack();
		frm.setOpid("7039");
		frm.setVisible(true);
	}
}
