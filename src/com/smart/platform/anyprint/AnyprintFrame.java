package com.smart.platform.anyprint;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.PrintJob;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Category;

import com.smart.platform.anyprint.impl.Partsprinter;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.gui.control.CToolbar;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;
import com.smart.platform.gui.tbar.TBar;
import com.smart.platform.gui.tbar.TButton;
import com.smart.platform.print.printer.PrintsetupDialog;
import com.smart.platform.util.DefaultNPParam;

/**
 * 任意打印设置功能窗口
 * 
 * @author Administrator
 * 
 */
public class AnyprintFrame extends Steframe {
	Printplan plan = new Printplan("", "");
	Datasourcepane datasourcepane = null;
	Designpane designpane = null;
	PreviewPane previewpane = null;
	DataprocPane dataprocpane = null;
	Category logger = Category.getInstance(AnyprintFrame.class);
	File printplanfile = null;
	String postfix = ".printplan";

	public AnyprintFrame() {
		super("打印设置功能");
		setDefaultCloseOperation(AnyprintFrame.DISPOSE_ON_CLOSE);
	}

	@Override
	protected CSteModel getStemodel() {
		// TODO Auto-generated method stub
		return null;
	} 

	@Override
	protected void initControl() {

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(new Toolbar(), BorderLayout.NORTH);

		Dimension scrsize = getToolkit().getScreenSize();
		setPreferredSize(new Dimension((int) scrsize.getWidth(), (int) scrsize
				.getHeight() - 25));
		setLocation(0, 0);

		JTabbedPane tabp = new JTabbedPane();
		removeHotkey(tabp);
		cp.add(tabp, BorderLayout.CENTER);

		datasourcepane = new Datasourcepane(this, plan);
		tabp.add("数据源", datasourcepane);

		dataprocpane = new DataprocPane(this, plan);
		tabp.add("数据处理", dataprocpane);

		designpane = new Designpane(this, plan);
		tabp.add("样式设计", designpane);

		previewpane = new PreviewPane(this, plan);
		tabp.add("预览", previewpane);

		tabp.addChangeListener(new Tablistener());

	}

	class Tablistener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			JTabbedPane tb = (JTabbedPane) e.getSource();
		}

	}

	class Toolbar extends TBar {
		Toolbar() {
			TButton btn = new TButton("新建");
			btn.setActionCommand("new");
			btn.addActionListener(AnyprintFrame.this);
			btn.setFocusable(false);
			add(btn);

			btn = new TButton("打开");
			btn.setActionCommand("open");
			btn.addActionListener(AnyprintFrame.this);
			btn.setFocusable(false);
			add(btn);

			btn = new TButton("保存");
			btn.setActionCommand("save");
			btn.addActionListener(AnyprintFrame.this);
			btn.setFocusable(false);
			add(btn);

			btn = new TButton("另存为");
			btn.setActionCommand("saveas");
			btn.addActionListener(AnyprintFrame.this);
			btn.setFocusable(false);
			add(btn);

			btn = new TButton("打印");
			btn.setActionCommand("print");
			btn.addActionListener(AnyprintFrame.this);
			btn.setFocusable(false);
			add(btn);

			btn = new TButton("上传");
			btn.setActionCommand("上传");
			btn.addActionListener(AnyprintFrame.this);
			btn.setFocusable(false);
			add(btn);

			btn = new TButton("退出");
			btn.setActionCommand("exit");
			btn.addActionListener(AnyprintFrame.this);
			btn.setFocusable(false);
			add(btn);
		}
	}

	JFileChooser filechooser = new JFileChooser(new File("."));

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("new")) {
			newPlan();
		} else if (cmd.equals("save")) {
			if (printplanfile == null) {
				doSaveas();
			} else {
				doSave();
			}
		} else if (cmd.equals("saveas")) {
			doSaveas();
		} else if (cmd.equals("open")) {
			doOpen();
		} else if (cmd.equals("exit")) {
			dispose();
		} else if (cmd.equals("print")) {
			try {
				doPrint();
			} catch (Exception e1) {
				errorMessage("错误", e1.getMessage());
				logger.error("error", e1);
			}
		} else if (cmd.equals("上传")) {
			try {
				setWaitcursor();
				doUpload();
			} catch (Exception e1) {
				errorMessage("错误", e1.getMessage());
				logger.error("error", e1);
			} finally {
				setDefaultcursor();
			}
		}
	}

	void doUpload() throws Exception {
		filechooser.setFileFilter(new Testplanfilter());
		int ret = filechooser.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		File planfile = filechooser.getSelectedFile();
		Printplan.uploadPrintplan(planfile);
	}

	void doSaveas() {
		filechooser.setFileFilter(new Testplanfilter());
		int ret = filechooser.showSaveDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		printplanfile = filechooser.getSelectedFile();
		if (!printplanfile.getName().toLowerCase().endsWith(postfix)) {
			printplanfile = new File(printplanfile.getParentFile(),
					printplanfile.getName() + postfix);
		}
		doSave();
	}

	void doOpen() {
		filechooser.setFileFilter(new Testplanfilter());
		int ret = filechooser.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		File f = filechooser.getSelectedFile();
		try {
			plan.reset();
			plan.read(f);
			printplanfile = f;
			firePlanchanged();
		} catch (Exception e) {
			logger.error("error", e);
			JOptionPane.showMessageDialog(this, "读取失败" + e.getMessage());
			return;
		}
	}

	void doSave() {
		try {
			File outf = printplanfile;
			PrintWriter out = new PrintWriter(new FileWriter(outf));
			plan.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("error", e);
			JOptionPane.showMessageDialog(this, "保存失败" + e.getMessage());
			return;
		}
	}

	void newPlan() {
		NewplanDlg dlg = new NewplanDlg(this);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		plan.reset();
		plan.setPlanname(dlg.getPlanname());
		plan.setPlantype(dlg.getPlantype());

		firePlanchanged();
	}

	void loadDebugplan() {
		if (DefaultNPParam.debug == 1) {
			try {
				plan.read(new File("test.printplan"));
				firePlanchanged();
			} catch (Exception e) {
				logger.error("error", e);
				JOptionPane.showMessageDialog(this, "读取失败" + e.getMessage());
				return;
			}

		}

	}

	public void firePlanchanged() {
		designpane.bind();
		datasourcepane.bind();
		dataprocpane.bind();
		previewpane.bind();
	}

	class Testplanfilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().toLowerCase().endsWith(postfix))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "打印方案 *.printplan";
		}

	}

	void removeHotkey(JComponent jcp) {
		InputMap im = jcp.getInputMap(JComponent.WHEN_FOCUSED);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "nouse");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "nouse");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "nouse");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "nouse");
	}

	public void doPrint() throws Exception {
		plan.sendPrinter();
/*		
		PrinterJob printerjob = PrinterJob.getPrinterJob();
		PrintsetupDialog psetupdlg = new PrintsetupDialog(this, printerjob,
				plan.getPlanname(), plan.getParts().getPagecount());
		psetupdlg.pack();
		psetupdlg.setVisible(true);
		if (!psetupdlg.getOk())
			return;

		HashPrintRequestAttributeSet prats = new HashPrintRequestAttributeSet();
		// 设置页范围
		PageRanges prang = new PageRanges(psetupdlg.getStartpage(), psetupdlg
				.getEndpage());
		prats.add(prang);

		// 设置份
		// Copies pcopies = new Copies(psetupdlg.getCopies());
		// prats.add(pcopies);

		int printcopys = psetupdlg.getCopies();

		if (plan.getParts().isLandscape()) {
			prats.add(OrientationRequested.LANDSCAPE);
		}

		String printername = psetupdlg.getPrintername();
		PrintService printservice = null;
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
				null, prats);
		for (int i = 0; i < printServices.length; i++) {
			if (printServices[i].getName().equals(printername)) {
				printerjob.setPrintService(printServices[i]);
				break;
			}
		}
		// 设置打印区
		double pw, ph;
		// 现在pw ph是mm
		pw = psetupdlg.getPaperwidth();
		ph = psetupdlg.getPaperheight();

		// System.out.println("==================page size=" + pw + "," + ph);
		double px = 0;
		double py = 0;
*/
		/*
		 * MediaPrintableArea printarea = null; try { printarea = new
		 * MediaPrintableArea((float) px, (float) py, (float) pw, (float) ph,
		 * MediaPrintableArea.MM); prats.add(printarea); } catch (Exception e) {
		 * this.errorMessage("提示", "纸张设置的太小了"); return ; }
		 */
		// 转为英寸 ,再乘以72
/*		
		px = 0;
		py = 0;

		pw = pw / 25.4 * 72.0;
		ph = ph / 25.4 * 72.0;
		Paper paper = new Paper();
		paper.setImageableArea(px, py, pw, ph);
		paper.setSize(pw, ph);
		PageFormat curpageformat = printerjob.defaultPage();
		curpageformat.setPaper(paper);

		for (int c = 1; c <= printcopys; c++) {
			plan.getParts().setPrintcopys(printcopys);
			plan.getParts().setPrintcopy(c);
			Partsprinter pp = new Partsprinter(plan.getParts(), curpageformat);
			printerjob.setPrintable(pp);
			printerjob.setPageable(pp);
			printerjob.print(prats);
		}
*/
	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		/*
		 * DefaultNPParam.debugdbip = "192.9.200.47";
		 * DefaultNPParam.debugdbpasswd = "szw"; DefaultNPParam.debugdbsid =
		 * "orcl"; DefaultNPParam.debugdbusrname = "szw";
		 */
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";

/*		DefaultNPParam.debugdbip = "192.9.200.245";
		DefaultNPParam.debugdbpasswd = "ypgs";
		DefaultNPParam.debugdbsid = "wqserver";
		DefaultNPParam.debugdbusrname = "ypgs";
*/
		DefaultNPParam.prodcontext = "npserver";
		ClientUserManager.getCurrentUser().setUserid("0");

		AnyprintFrame frm = new AnyprintFrame();
		frm.pack();
		frm.setVisible(true);
		// frm.loadDebugplan();

	}

}
