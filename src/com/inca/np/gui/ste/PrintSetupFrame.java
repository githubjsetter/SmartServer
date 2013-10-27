package com.inca.np.gui.ste;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Category;

import com.inca.np.env.Configer;
import com.inca.np.gui.control.CFrame;
import com.inca.np.image.IconFactory;
import com.inca.np.print.drawable.PReport;
import com.inca.np.print.printer.PrintsetupDialog;
import com.inca.np.print.report.PageHeadSetupPane;
import com.inca.np.print.report.PagebodySetupPane;
import com.inca.np.print.report.ReportStorage;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-5-13 Time: 14:09:23
 * 打印设置
 */
public class PrintSetupFrame extends CFrame implements ActionListener {
	private CSteModel stemodel = null;
	private CSteModel masterstemodel = null;
	private PReport report;
	private BufferedImage reportimg;
	private JTextField textPageno;
	private PreviewPanel previewpanl;
	private PageFormat curpageformat = null;
	private JScrollPane scrollp;
	private JLabel lbtotlpage;
	private PrinterJob job;
	private JComboBox cbname;
	private PageHeadSetupPane pageHeadSetupPane;
	private PageHeadSetupPane pageFootSetupPane;
	private PagebodySetupPane pagebodySetupPane;
	String opid;
	boolean zxmodify = false;

	Category logger = Category.getInstance(PrintSetupFrame.class);
	Configer config = new Configer(new File("conf/print.properties"));

	public PrintSetupFrame(String opid, boolean zxmodify,
			CSteModel masterstemodel, CSteModel stemodel)
			throws HeadlessException {
		super(stemodel.getTitle() + "打印设置");
		this.opid = opid;
		this.zxmodify = zxmodify;
		this.masterstemodel = masterstemodel;
		this.stemodel = stemodel;

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		job = PrinterJob.getPrinterJob();
		job.setJobName("打印" + stemodel.getTitle());

		PageFormat defaultpageformat = job.defaultPage();
		double w = defaultpageformat.getWidth();
		double h = defaultpageformat.getHeight();
		Paper paper = new Paper();
		paper.setSize(w, h);
		paper.setImageableArea(12, 12, w - 2 * 12, h - 2 * 12);
		defaultpageformat.setPaper(paper);
		curpageformat = defaultpageformat;

		// 先设pageable
		String[] names = null;
		String reportname = "默认";
		try {
			names = ReportStorage.getSavedreportNames(opid);
			if (names.length > 0) {
				reportname = names[0];
			}
		} catch (Exception e) {
			logger.error("ERROR", e); // To change body of catch statement use
			// File | Settings | File Templates.
		}

		if (!loadReport(reportname)) {
			report = stemodel.createDefaultReport();
			fireReportChanged();
		}

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		JTabbedPane tabbedpane = new JTabbedPane();
		cp.add(tabbedpane, BorderLayout.CENTER);

		Dimension scrsize = getToolkit().getScreenSize();
		tabbedpane.setPreferredSize(new Dimension(
				(int) scrsize.getWidth() - 90, 700));

		previewpanl = new PreviewPanel();
		scrollp = new JScrollPane(previewpanl);
		tabbedpane.add("打印预览", scrollp);

		pageHeadSetupPane = new PageHeadSetupPane(this, report, report
				.getPagehead());
		JScrollPane pageheadsp = new JScrollPane(pageHeadSetupPane);
		tabbedpane.add("页头设定", pageheadsp);

		pagebodySetupPane = new PagebodySetupPane(this, report, report
				.getPagehead());
		JScrollPane pagebodysp = new JScrollPane(pagebodySetupPane);
		tabbedpane.add("表身设定", pagebodysp);

		pageFootSetupPane = new PageHeadSetupPane(this, report, report
				.getPagefoot());
		JScrollPane pagefootsp = new JScrollPane(pageFootSetupPane);
		tabbedpane.add("页脚设定", pagefootsp);

		/*
		 * JPanel toppanel = createTopPanel(); cp.add(toppanel,
		 * BorderLayout.NORTH);
		 */

		this.addWindowFocusListener(new FocusHandle());

		prats.add(new Copies(1));
		prats.add(OrientationRequested.PORTRAIT);

	}

	/*
	 * public void doPrint(PrintService printservice, String reportname) throws
	 * HeadlessException { try { job.setPrintService(printservice); } catch
	 * (PrinterException e) { logger.error("ERROR", e); } if
	 * (!loadReport(reportname)) { return; } doPrint(); }
	 */
	public boolean doPrint(String reportname) throws HeadlessException {
		/*
		 * PrintService[] printServices =
		 * PrintServiceLookup.lookupPrintServices( null, null);
		 * 
		 * String printername = config.get("printername"); if (printername ==
		 * null || printername.length() == 0) return; boolean bfind = false; for
		 * (int i = 0; i < printServices.length; i++) { if
		 * (printServices[i].getName().equals(printername)) { try {
		 * job.setPrintService(printServices[i]); bfind = true; } catch
		 * (PrinterException e) { logger.error("ERROR", e); } break; } } if
		 * (!bfind) return;
		 */
		if (!loadReport(reportname)) {
			return false;
		}

		try {
			if (!choosePrinter())
				return false;
		} catch (Exception e) {
			logger.error("error", e);
			return false;
		}

		doPrint();
		return true;
	}

	private JPanel createTopPanel() {
		JPanel bottompane = new JPanel();
		// BoxLayout boxlayout = new BoxLayout(bottompane,BoxLayout.X_AXIS);
		// bottompane.setLayout(boxlayout);
		bottompane.setLayout(new FlowLayout());

		return bottompane;
	}

	private boolean loadReport(String reportname) {
		try {
			report = ReportStorage.loadReport(opid, reportname);
			if (report == null) {
				return false;
			}
			fireReportChanged();

			return true;
		} catch (Exception e) {
			logger.error("ERROR", e); // To change body of catch statement use
			// File | Settings | File Templates.
			return false;
		}
	}

	private HashPrintRequestAttributeSet prats = new HashPrintRequestAttributeSet();

	boolean choosePrinter() throws Exception {

		/*
		 * if (!job.printDialog(prats)) return false;
		 */
		// 改为自已的dialog
		PrintsetupDialog psetupdlg = new PrintsetupDialog(this, job, "print",
				report.getPagecount());
		psetupdlg.pack();
		psetupdlg.setVisible(true);
		if (!psetupdlg.getOk())
			return false;
		String printername = psetupdlg.getPrintername();
		PrintService printservice = null;
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
				null, null);
		for (int i = 0; i < printServices.length; i++) {
			if (printServices[i].getName().equals(printername)) {
				job.setPrintService(printServices[i]);
				break;
			}
		}
		prats = new HashPrintRequestAttributeSet();
		// 设置打印区
		double px = 0;
		double py = 0;
		double pw, ph;
		// 现在pw ph是mm
		pw = psetupdlg.getPaperwidth();
		ph = psetupdlg.getPaperheight();

		// System.out.println("==================page size=" + pw + "," + ph);

		MediaPrintableArea printarea = null;
		try {
			printarea = new MediaPrintableArea((float) px, (float) py,
					(float) pw, (float) ph, MediaPrintableArea.MM);
			prats.add(printarea);
		} catch (Exception e) {
			this.errorMessage("提示", "纸张设置的太小了");
			return false;
		}

		// 转为英寸 ,再乘以72
		pw = pw / 25.4 * 72;
		ph = ph / 25.4 * 72;
		Paper paper = new Paper();
		paper.setImageableArea(px, py, pw, ph);
		paper.setSize(pw, ph);
		curpageformat.setPaper(paper);

		// 设置页范围
		PageRanges prang = new PageRanges(psetupdlg.getStartpage(), psetupdlg
				.getEndpage());
		prats.add(prang);

		// 设置份
		Copies pcopies = new Copies(psetupdlg.getCopies());
		prats.add(pcopies);

		/*
		 * // reset print area Attribute attribute = prats.get(Media.class); if
		 * (attribute != null && attribute instanceof MediaSizeName) {
		 * MediaSizeName sizenameattr = (MediaSizeName) attribute; MediaSize
		 * mediasize = MediaSize.getMediaSizeForName(sizenameattr); float[]
		 * papersize = mediasize.getSize(MediaSize.INCH);
		 * 
		 * pw = papersize[0] * 72; ph = papersize[1] * 72; } else { PageFormat
		 * defaultpage = job.defaultPage(); Paper defaultpaper =
		 * defaultpage.getPaper(); pw = (float) defaultpaper.getWidth(); ph =
		 * (float) defaultpaper.getHeight(); }
		 */

		return true;
	}

	void doPrint() {
		int index = cbname.getSelectedIndex();
		if (index >= 0) {
			String reportname = (String) cbname.getSelectedItem();
			saveUserConfig(getPrintername(), reportname);
		}

		prats.remove(Media.class);
		try {
			job.print(prats);
		} catch (PrinterException e) {
			logger.error("ERROR", e); // To change body of catch statement use
			// File | Settings | File Templates.
		}

	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("chooseprinter")) {
			try {
				if (this.choosePrinter()) {
					this.showPage(0, curpageformat);
				}
			} catch (Exception e1) {
				e1.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
			}
		} else if (cmd.equals("ok")) {
			doPrint();
			this.dispose();
		} else if (cmd.equals("cancel")) {
			this.dispose();
		} else if (cmd.equals("go")) {
			int pn = 0;
			try {
				pn = Integer.parseInt(textPageno.getText());
			} catch (NumberFormatException e1) {
			}
			showPage(pn - 1, curpageformat);
		} else if (cmd.equals("first")) {
			showPage(0, curpageformat);
		} else if (cmd.equals("prior")) {
			showPage(pageno - 1, curpageformat);
		} else if (cmd.equals("next")) {
			showPage(pageno + 1, curpageformat);
		} else if (cmd.equals("last")) {
			showPage(report.getNumberOfPages() - 1, curpageformat);
		} else if (cmd.equals("save")) {
			// 保存报表
			int index = cbname.getSelectedIndex();
			if (index < 0)
				return;

			String reportname = (String) cbname.getSelectedItem();
			try {
				ReportStorage.saveReport(opid, reportname, report);
				saveUserConfig(getPrintername(), reportname);
			} catch (Exception e1) {
				logger.error("ERROR", e1);
			}
		} else if (cmd.equals("saveas")) {
			NewnameDialog dlg = new NewnameDialog();
			dlg.pack();
			dlg.setVisible(true);
			String newname = dlg.getNewname();
			if (newname != null && newname.length() > 0) {
				try {
					ReportStorage.saveReport(opid, newname, report);
					saveUserConfig(getPrintername(), newname);

					// 设置
					DefaultComboBoxModel cbmodel = (DefaultComboBoxModel) cbname
							.getModel();
					int i;
					for (i = 0; i < cbmodel.getSize(); i++) {
						if (((String) cbmodel.getElementAt(i)).equals(newname)) {
							cbname.setSelectedIndex(i);
							break;
						}
					}
					if (i >= cbmodel.getSize()) {
						cbmodel.addElement(newname);
						cbname.setSelectedIndex(cbmodel.getSize() - 1);
					}

				} catch (Exception e1) {
					logger.error("ERROR", e1); // To change body of catch
					// statement use File | Settings
					// | File Templates.
				}
			}
		}
	}

	String getPrintername() {
		if (job == null)
			return "";
		PrintService printService = job.getPrintService();
		if (printService == null)
			return "";
		return printService.getName();
	}

	private int pageno = 0;

	void showPage(int pageno, PageFormat pageformat) {
		int pagect = 0;
		try {
			pagect = report.getNumberOfPages();
			if (pageno > pagect - 1) {
				pageno = pagect - 1;
			}
			if (pageno < 0) {
				pageno = 0;
			}
		} catch (Exception e) {
			// 未初始化时，报表不能取页数
		}
		BufferedImage img = null;
		try {
			img = report.createReportImage(pageno, pageformat);
		} catch (Exception e) {
			logger.error("ERROR", e); // To change body of catch statement use
			// File | Settings | File Templates.
			return;
		}
		reportimg = img;
		if (previewpanl != null) {
			scrollp.updateUI();
			invalidate();
			repaint();
		}

		this.pageno = pageno;
		if (textPageno != null) {
			textPageno.setText(String.valueOf(pageno + 1));
			lbtotlpage.setText(String.valueOf(report.getNumberOfPages()));
		}

		dirty = false;
		/*
		 * 
		 * try { ImageIO.write(reportimg, "png", new File("rpt.png")); } catch
		 * (IOException e) { logger.error("ERROR",e); //To change body of catch
		 * statement use File | Settings | File Templates. }
		 */

	}

	public void pack() {
		// 重新设置位置
		Dimension screensize = this.getToolkit().getScreenSize();
		Dimension size = this.getPreferredSize();

		int x = (int) ((screensize.getWidth() - size.getWidth()) / 2);
		this.setLocation(x, 0);
		size = new Dimension((int) size.getWidth(), (int) size.getHeight() - 50);
		this.setPreferredSize(size);

		super.pack();

	}

	public void setVisible(boolean b) {
		if (b) {
			try {
				/*
				 * if(! this.choosePrinter()){ this.dispose(); return; }
				 */
				showPage(0, curpageformat);
			} catch (Exception e) {
				logger.error("ERROR", e); // To change body of catch statement
				// use File | Settings | File
				// Templates.
				return;
			}
		}
		super.setVisible(b);
	}

	boolean dirty = false;

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void fireReportChanged() {
		job.setPageable(report);
		job.setPrintable(report);

		/*
		 * report.setDbmodel(stemodel.getDBtableModel());
		 * report.setMasterdbmodel(masterstemodel.getDBtableModel(),
		 * masterstemodel.getRow());
		 */
		report.setDbmodel(stemodel.getSumdbmodel());
		report.setMasterdbmodel(masterstemodel.getSumdbmodel(), masterstemodel
				.getRow());

		if (pageHeadSetupPane != null) {
			pageHeadSetupPane.fireReportchanged(report);
		}
		if (pageHeadSetupPane != null) {
			pageHeadSetupPane.setReport(report);
			pageHeadSetupPane.setPagehead(report.getPagehead());
		}

		if (pagebodySetupPane != null) {
			pagebodySetupPane.setReport(report);
			pagebodySetupPane.setPagehead(report.getPagehead());
		}

		if (pageFootSetupPane != null) {
			pageFootSetupPane.setReport(report);
			pageFootSetupPane.setPagehead(report.getPagefoot());
		}

		// 重画
		showPage(0, curpageformat);
	}

	/**
	 * 在用户配置文件中保存reportname
	 * 
	 * @param reportname
	 */
	void saveUserConfig(String printername, String reportname) {
		String classname = stemodel.getClass().getName();
		int p = classname.lastIndexOf(".");
		if (p >= 0) {
			classname = classname.substring(p + 1);
		}
		String configfilename = classname + ".properties";
		logger.info("save print user config,configfilename=" + configfilename);

		File configf = new File("conf/" + configfilename);
		logger.info("save print config filepath=" + configf.getAbsolutePath());
		Configer config = new Configer(configf);
		config.put("print.printername", printername);
		config.put("print.reportname", reportname);
		config.saveConfigfile();

	}

	class FocusHandle implements WindowFocusListener {

		public void windowGainedFocus(WindowEvent e) {
			if (dirty) {
				// 需要重新生成report
				fireReportChanged();
			}
		}

		public void windowLostFocus(WindowEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
	}

	class PreviewPanel extends JPanel {
		public PreviewPanel() {
			setLayout(new BorderLayout());

			JPanel toppane = createToppane();
			add(toppane, BorderLayout.NORTH);
			add(new Drawpanel(), BorderLayout.CENTER);
		}

		JPanel createToppane() {
			JPanel jp = new JPanel();
			jp.setLayout(new FlowLayout());
			JButton btn;
			Dimension btnsize = new Dimension(80, 27);
			btn = new JButton("打印机");
			btn.setActionCommand("chooseprinter");
			btn.setPreferredSize(btnsize);
			btn.addActionListener(PrintSetupFrame.this);
			jp.add(btn);

			JLabel lb = new JLabel("第");
			jp.add(lb);
			textPageno = new JTextField(4);
			textPageno.setText("1");
			jp.add(textPageno);
			lb = new JLabel("/");
			jp.add(lb);

			lbtotlpage = new JLabel(String.valueOf(report.getNumberOfPages()));
			jp.add(lbtotlpage);

			lb = new JLabel("页");
			jp.add(lb);

			JButton btngo = new JButton("跳　转");
			btngo.setActionCommand("go");
			btngo.setPreferredSize(btnsize);
			btngo.addActionListener(PrintSetupFrame.this);
			jp.add(btngo);

			Dimension pbtnsize = new Dimension(30, 27);
			btn = new JButton(IconFactory.icfirst);
			btn.setActionCommand("first");
			btn.setPreferredSize(pbtnsize);
			btn.addActionListener(PrintSetupFrame.this);
			jp.add(btn);

			btn = new JButton(IconFactory.icprior);
			btn.setActionCommand("prior");
			btn.setPreferredSize(pbtnsize);
			btn.addActionListener(PrintSetupFrame.this);
			jp.add(btn);

			btn = new JButton(IconFactory.icnext);
			btn.setActionCommand("next");
			btn.setPreferredSize(pbtnsize);
			btn.addActionListener(PrintSetupFrame.this);
			jp.add(btn);

			btn = new JButton(IconFactory.iclast);
			btn.setActionCommand("last");
			btn.setPreferredSize(pbtnsize);
			btn.addActionListener(PrintSetupFrame.this);
			jp.add(btn);

			// 列出报表样式
			lb = new JLabel("报表名称");
			jp.add(lb);

			String savednames[] = { "默认" };
			try {
				savednames = ReportStorage.getSavedreportNames(opid);
			} catch (Exception e) {
				logger.error("ERROR", e); // To change body of catch statement
				// use File | Settings | File
				// Templates.
			}
			cbname = new JComboBox(savednames);
			cbname.addItemListener(new CbnameListener());
			jp.add(cbname);

			JButton btnsave = new JButton("保　存");
			btnsave.setActionCommand("save");
			btnsave.setPreferredSize(btnsize);
			btnsave.addActionListener(PrintSetupFrame.this);
			jp.add(btnsave);

			JButton btnsaveas = new JButton("另存为");
			btnsaveas.setActionCommand("saveas");
			btnsaveas.setPreferredSize(btnsize);
			btnsaveas.addActionListener(PrintSetupFrame.this);
			jp.add(btnsaveas);

			JButton btnok = new JButton("打　印");
			btnok.setActionCommand("ok");
			btnok.setPreferredSize(btnsize);
			btnok.addActionListener(PrintSetupFrame.this);
			jp.add(btnok);

			JButton btncancel = new JButton("取消");
			btncancel.setActionCommand("cancel");
			btncancel.addActionListener(PrintSetupFrame.this);
			// jp.add(btncancel);

			return jp;
		}
	}

	class Drawpanel extends JPanel {
		public Dimension getPreferredSize() {
			return new Dimension(reportimg.getWidth(), reportimg.getHeight());
		}

		protected void paintComponent(Graphics g) {
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.drawImage(reportimg, 0, 0, null);
		}
	}

	class NewnameDialog extends JDialog implements ActionListener {
		private JTextField textName;
		private String newname = null;

		public NewnameDialog() throws HeadlessException {
			super(PrintSetupFrame.this, "请输入新名称", true);

			Container cp = this.getContentPane();
			cp.setLayout(new BorderLayout());

			JPanel jpcenter = new JPanel();
			jpcenter.setLayout(new FlowLayout());

			JLabel lb = new JLabel("新名称");
			jpcenter.add(lb);
			textName = new JTextField(40);
			jpcenter.add(textName);

			cp.add(jpcenter, BorderLayout.CENTER);

			JPanel jpbottom = new JPanel();
			jpbottom.setLayout(new FlowLayout());
			cp.add(jpbottom, BorderLayout.SOUTH);

			JButton btnok = new JButton("确定");
			btnok.setActionCommand("ok");
			btnok.addActionListener(this);
			jpbottom.add(btnok);

			JButton btncancel = new JButton("取消");
			btncancel.setActionCommand("cancel");
			btncancel.addActionListener(this);
			jpbottom.add(btncancel);

			// 定位中心
			Dimension scrsize = this.getToolkit().getScreenSize();
			Dimension size = this.getPreferredSize();
			double x = (scrsize.getWidth() - size.getWidth()) / 2;
			double y = (scrsize.getHeight() - size.getHeight()) / 2;

			this.setLocation((int) x, (int) y);

		}

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("ok")) {
				newname = textName.getText();
				this.dispose();
			} else if (cmd.equals("cancel")) {
				this.dispose();
			}
		}

		public String getNewname() {
			return newname;
		}
	}

	class CbnameListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			String reportname = (String) e.getItem();
			try {
				report = ReportStorage.loadReport(opid, reportname);
				if (report == null) {
					return;
				}
			} catch (Exception e1) {
				e1.printStackTrace(); // To change body of catch statement use
				// File | Settings | File Templates.
				return;
			}
			fireReportChanged();
		}
	}
}
