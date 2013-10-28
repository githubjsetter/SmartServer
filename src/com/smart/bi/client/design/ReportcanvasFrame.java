package com.smart.bi.client.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Category;

import com.smart.bi.client.design.param.BIQueryDlg;
import com.smart.bi.client.design.param.BIReportparamdefine;
import com.smart.bi.client.storer.BIReportStorage;
import com.smart.client.download.DownloadManager;
import com.smart.client.system.ExitRestartProc;
import com.smart.client.system.SystemexitThread;
import com.smart.platform.auth.ClientUserManager;
import com.smart.platform.auth.RunopManager;
import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CDefaultProgress;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CNumberTextField;
import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.JFontChooser;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.tbar.TBar;
import com.smart.platform.gui.tbar.TButton;
import com.smart.platform.print.printer.PrintsetupDialog;
import com.smart.platform.util.DBHelper;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.SendHelper;

public class ReportcanvasFrame extends COpframe implements Printable, Pageable {
	Category logger = Category.getInstance(ReportcanvasFrame.class);
	protected Reportcanvas canvas = null;
	// DBTableModel datadm = null;
	protected JTextArea textCellExpr;
	protected JComboBox cbCellAlign;
	protected JComboBox cbCellVAlign;
	protected BICell editingcell;
	protected Rectangle editingcellpos = null;

	protected BITableV_Render editingtablev = null;
	protected Rectangle editingtablevpos = null;

	protected JTabbedPane proptabbedpane;
	protected CNumberTextField textX;
	protected CNumberTextField textY;
	protected CNumberTextField textW;
	protected CNumberTextField textH;
	/**
	 * ������Դ,���ڱ���
	 */
	protected BIReportdsDefine dsdefine = new BIReportdsDefine();
	protected JComboBox cbCellrepeat;

	/**
	 * ����Դtable.��0����������Դ.
	 */
	protected Vector<BIReportdsDefine> dstable = new Vector<BIReportdsDefine>();
	protected Tablevdesignpane designpane = null;
	protected BITableV_def tablevdef = null;
	protected BITableV_Render tablevrender = null;
	/**
	 * ��ҳ��
	 */
	protected int pagecount = 0;

	/**
	 * ��ǰҳ
	 */
	protected int pageno = 0;

	protected File bifile = null;

	protected String rptopid = "";
	protected String opcode = "";
	protected String opname = "";
	protected String groupname = "";
	protected String prodname = "";
	protected String modulename = "";

	/**
	 * ����
	 */
	protected boolean landscape = false;

	PapersetupDlg papersetupdlg = null;

	private boolean autoquery = false;

	public Vector<BIReportdsDefine> getDstable() {
		return dstable;
	}

	public BIReportdsDefine getDsdefine() {
		return dsdefine;
	}

	public ReportcanvasFrame() {
		super("�������");
		dstable.add(dsdefine);
		canvas = new Reportcanvas(ReportcanvasFrame.this);
		tablevdef = new BITableV_def();
		tablevrender = new BITableV_Render(tablevdef);
		tablevdef.addChanglistener(canvas);
		tablevrender.setDsdefine(dsdefine);
		canvas.addTablev(tablevrender);
		init();
		initDm();
		setHotkey();
		setDefaultCloseOperation(CFrame.DISPOSE_ON_CLOSE);
	}

	protected void init() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		JPanel tb = createToolbar();
		cp.add(tb, BorderLayout.NORTH);

		JTabbedPane maintabbpane = new JTabbedPane();
		maintabbpane.addChangeListener(new MaintabpaneHandler());
		cp.add(maintabbpane, BorderLayout.CENTER);

		dspane = new BIReportdsPane(this);
		maintabbpane.add("����Դ", dspane);

		designpane = new Tablevdesignpane(this);
		maintabbpane.add("��ֱ��", designpane);

		Layoutpanel dp = new Layoutpanel();
		maintabbpane.add("������", dp);

	}

	void initDm() {
		canvas.setDatadm(dsdefine.datadm);
		// canvas.placeDebugdrawable();
		bindListplacetable();
	}

	protected class Layoutpanel extends JPanel {
		Layoutpanel() {

			JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

			add(jsp, BorderLayout.CENTER);
			proptabbedpane = new JTabbedPane();
			jsp.setLeftComponent(proptabbedpane);
			jsp.setDividerLocation(240);

			JPanel tmpp = new JPanel();
			tmpp.setLayout(new BoxLayout(tmpp, BoxLayout.Y_AXIS));
			tmpp.add(createNavpane());
			proptabbedpane.add("����", tmpp);

			JPanel cellproppane = createCellProppane();
			tmpp = new JPanel();
			tmpp.setLayout(new BoxLayout(tmpp, BoxLayout.Y_AXIS));
			tmpp.add(cellproppane);

			proptabbedpane.add("��Ԫ��", tmpp);

			tmpp = new JPanel();
			tmpp.setLayout(new BoxLayout(tmpp, BoxLayout.Y_AXIS));
			tmpp.add(createTablevPane());
			proptabbedpane.add("��λ��", tmpp);

			tmpp = new JPanel();
			tmpp.setLayout(new BoxLayout(tmpp, BoxLayout.Y_AXIS));
			tmpp.add(createPaperPane());
			proptabbedpane.add("ֽ��", tmpp);

			JPanel righpanel = createCanvaspane();
			jsp.setRightComponent(righpanel);

			// A4ֽ
			cbPapername.setSelectedIndex(5);
			bindListplacetable();

		}
	}

	protected JPanel createCanvaspane() {
		JPanel rightpane = new JPanel();
		rightpane.setLayout(new BorderLayout());
		JPanel tb = new JPanel();
		rightpane.add(tb, BorderLayout.NORTH);
		lbpagecount = new JLabel("��" + pagecount + "ҳ");
		tb.add(lbpagecount);

		JButton btn = new JButton("��ת��ҳ");
		btn.setActionCommand("gotopage");
		btn.addActionListener(this);
		tb.add(btn);

		SpinnerNumberModel spnumbermodel = new SpinnerNumberModel(pageno, 0,
				pagecount, 1);
		spPageno = new JSpinner(spnumbermodel);
		spPageno.addChangeListener(new SppagenoHandler());
		Dimension spinsize = new Dimension(50, 27);
		spPageno.setPreferredSize(spinsize);
		spPageno.setMaximumSize(spinsize);
		spPageno.setMinimumSize(spinsize);
		tb.add(spPageno);

		canvasscrollp = new JScrollPane(canvas);
		// canvasscrollp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		canvasscrollp.setPreferredSize(new Dimension(700, 550));
		rightpane.add(canvasscrollp, BorderLayout.CENTER);
		return rightpane;
	}

	/**
	 * ҳ�ż���
	 * 
	 * @author user
	 * 
	 */
	class SppagenoHandler implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			gotoPage();
		}

	}

	protected JPanel createToolbar() {
		TBar tb = new TBar();
		TButton btn;
		btn = new TButton("����");
		btn.setActionCommand("new");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new TButton("��");
		btn.setActionCommand("open");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new TButton("����");
		btn.setActionCommand("setupop");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new TButton("����");
		btn.setActionCommand("save");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new TButton("�ϴ�");
		btn.setActionCommand("upload");
		btn.addActionListener(this);
		tb.add(btn);

		JLabel lb = new JLabel("����ʱ����¼��");
		tb.add(lb);
		SpinnerNumberModel maxrowspinner = new SpinnerNumberModel(100, 2,
				70000, 100);
		jspinMaxrow = new JSpinner(maxrowspinner);
		tb.add(jspinMaxrow);

		btn = new TButton("��ѯ����");
		btn.setMargin(new Insets(1, 1, 1, 1));
		btn.setActionCommand("retrieve");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new TButton("��ӡ");
		btn.setActionCommand("doprint");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new TButton("����");
		btn.setActionCommand("export");
		btn.addActionListener(this);
		tb.add(btn);

		btn = new TButton("�˳�");
		btn.setActionCommand("exit");
		btn.addActionListener(this);
		tb.add(btn);

		return tb;
	}

	public DBTableModel createColdefdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("title", "varchar", "��������");
		cols.add(col);

		col = new DBColumnDisplayInfo("colname", "varchar", "����");
		cols.add(col);

		col = new DBColumnDisplayInfo("coltype", "varchar", "����");
		cols.add(col);
		DBTableModel coldm = new DBTableModel(cols);

		if (dsdefine.datadm != null) {
			Enumeration<DBColumnDisplayInfo> en = dsdefine.datadm
					.getDisplaycolumninfos().elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				int newrow = coldm.getRowCount();
				coldm.appendRow();
				coldm.setItemValue(newrow, "title", colinfo.getTitle());
				coldm.setItemValue(newrow, "colname", colinfo.getColname());
				coldm.setItemValue(newrow, "coltype", colinfo.getColtype());
			}
		}

		return coldm;
	}

	public ColumndragTable createColumntable() {

		DBTableModel coldm = createColdefdm();

		return new ColumndragTable(coldm);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if ("addcell".equals(cmd)) {
			addFreecell();
		} else if ("addchart".equals(cmd)) {
			addChart();
		} else if ("delcurrent".equals(cmd)) {
			delCurrent();
		} else if ("setupexpr".equals(cmd)) {
			if (editingcell == null)
				return;
			editCellexpr();
		} else if ("setupfont".equals(cmd)) {
			if (editingcell == null)
				return;
			setupFont();
		} else if ("delcell".equals(cmd)) {
			deleCell();
		} else if ("gotopage".equals(cmd)) {
			gotoPage();
		} else if ("deltablev".equals(cmd)) {
			delTablev();
		} else if ("retrieve".equals(cmd)) {
			doRetrieve();
			canvas.repaint();
		} else if ("open".equals(cmd)) {
			doOpen();
		} else if ("save".equals(cmd)) {
			doSave();
		} else if ("setuppaper".equals(cmd)) {
			setupPaper();
		} else if ("doprint".equals(cmd)) {
			doPrint();
		} else if ("new".equals(cmd)) {
			doNew();
		} else if ("setupop".equals(cmd)) {
			setupOpprop();
		} else if ("upload".equals(cmd)) {
			doUpload();
		} else if ("exit".equals(cmd)) {
			doExit();
		} else if ("exportpng".equals(cmd)) {
			exportPng();
		} else if ("export".equals(cmd)) {
			exportExcel();
		} else {
			super.actionPerformed(e);
		}
	}

	/**
	 * ����ֽ��
	 */
	void setupPaper() {
		if (papersetupdlg == null) {
			papersetupdlg = new PapersetupDlg(this);
			papersetupdlg.pack();
		}
		papersetupdlg.setPapername((String) cbPapername.getSelectedItem());
		papersetupdlg.setPaperwidth(textPaperwidth.getText());
		papersetupdlg.setPaperheight(textPaperheight.getText());
		papersetupdlg.setLeftmargin(textPaperleftmargin.getText());
		papersetupdlg.setRightmargin(textPaperrightmargin.getText());
		papersetupdlg.setTopmargin(textPapertopmargin.getText());
		papersetupdlg.setBottommargin(textPaperbottommargin.getText());
		papersetupdlg.setLandscape(cbLandscape.isSelected());

		papersetupdlg.setVisible(true);
		if (!papersetupdlg.isOk())
			return;
		settingvalue = true;
		cbPapername.setSelectedItem(papersetupdlg.getPapername());
		textPaperwidth.setText(papersetupdlg.getPaperwidth());
		textPaperheight.setText(papersetupdlg.getPaperheight());
		textPapertopmargin.setText(papersetupdlg.getTopmargin());
		textPaperbottommargin.setText(papersetupdlg.getBottommargin());
		textPaperleftmargin.setText(papersetupdlg.getLeftmargin());
		textPaperrightmargin.setText(papersetupdlg.getRightmargin());
		cbLandscape.setSelected(papersetupdlg.isLandscape());
		landscape = cbLandscape.isSelected();
		settingvalue = false;
		onPaperSizechanged();
	}

	void delCurrent() {
		int row = listplaceabletable.getRow();
		if (row < 0)
			return;
		if (row == 0) {
			warnMessage("��ʾ", "����ɾ����ֱ����.");
			return;
		}
		canvas.deletePlaceableAt(row);
		editingcell = null;
		editingtablev = null;
		bindListplacetable();
		tablevdef.fireDefinechanged();
		canvas.repaint();
	}

	BIQueryDlg querydlg = null;

	CDefaultProgress progress = null;
	int fetchedrowcount = 0;

	class Fetchthread extends Thread {
		String mainsql = "";

		public Fetchthread(String mainsql) {
			super();
			this.mainsql = mainsql;
		}

		public void run() {
			fetchedrowcount = 0;
			doRetrieveMain(mainsql);
		}
	}

	void doRetrieve() {
		// ���ɲ�ѯ����
		Vector<BIReportparamdefine> params = dsdefine.params;
		if (querydlg == null) {
			querydlg = new BIQueryDlg(this, "�����ѯ����", params);
			querydlg.pack();
		}
		querydlg.bindValue(dsdefine.params);
		querydlg.setVisible(true);
		if (!querydlg.isConfirm())
			return;
		startFetchthread();
		if (progress != null) {
			progress.show();
		}
	}

	/**
	 * ��ѯ��
	 * 
	 * @param conds
	 *            ��:�ֿ����С� ÿ�ж��ǲ���=ֵ����ʽ��
	 */
	public void doQuery(String conds) {
		setAutoquery(false);
		String lines[] = conds.split(":");
		Enumeration<BIReportparamdefine> en = dsdefine.params.elements();
		while (en.hasMoreElements()) {
			BIReportparamdefine param = en.nextElement();
			param.setInputvalue("");
		}

		for (int i = 0; lines != null && i < lines.length; i++) {
			String line = lines[i];
			int p = line.indexOf("=");
			if (p < 0)
				continue;
			String paramname = line.substring(0, p);
			String value = line.substring(p + 1);

			en = dsdefine.params.elements();
			while (en.hasMoreElements()) {
				BIReportparamdefine param = en.nextElement();
				if (!param.paramname.equalsIgnoreCase(paramname))
					continue;
				if (param.paramtype.equals("date")) {
					if (value.length() > 10) {
						value = value.substring(0, 10);
					}
				}
				param.setInputvalue(value);
			}

		}

		startFetchthread();
		if (progress != null) {
			progress.show();
		}
		
		Runnable r=new Runnable(){
			public void run(){
				if(querydlg!=null && querydlg.isVisible()){
					querydlg.setVisible(false);
				}
			}
		};
		SwingUtilities.invokeLater(r);
	}

	void startFetchthread() {
		progress = new CDefaultProgress(this);
		Fetchthread ft = new Fetchthread(dsdefine.getFullsql());
		ft.setDaemon(true);
		ft.start();
		progress.show();
	}

	void doRetrieveMain(String sql) {
		Vector<BIReportparamdefine> params = dsdefine.params;
		int wantrowcount = ((Integer) jspinMaxrow.getValue()).intValue();
		fetchedrowcount = 0;
		try {
			setWaitcursor();
			// dsdefine.params = params;
			// String sql = dsdefine.getFullsql();
			logger.debug(sql);
			RemotesqlHelper sh = new RemotesqlHelper();
			dsdefine.datadm.clearAll();
			canvas.prepareData();
			canvas.splitPage();

			if (progress != null)
				progress.appendMessage("��ʼ��ѯ....");
			int fetchcount = 0;
			for (;;) {
				int thiscount = wantrowcount >= 1000 ? 1000 : wantrowcount;
				DBTableModel dm = sh.doSelect(sql, fetchcount, thiscount);
				fetchedrowcount += dm.getRowCount();
				if (progress != null)
					progress.appendMessage("�Ѳ�ѯ��" + fetchedrowcount + "����¼");
				logger.debug("dm.getrowcount()=" + dm.getRowCount());
				dsdefine.datadm.appendDbmodel(dm);
				wantrowcount -= dm.getRowCount();
				fetchcount += dm.getRowCount();
				if (wantrowcount <= 0)
					break;
				if (!dm.hasmore())
					break;

			}

			// ��ѯ��������
			for (int i = 1; i < dstable.size(); i++) {
				dstable.elementAt(i).params = params;
				doRetrieve(dstable.elementAt(i));
			}
			// �������·����ҳ
			if (progress != null)
				progress.appendMessage("���ڼӹ�����,����ͼ��....");
			canvas.onTabledefineChanged();
			spPageno.setValue(1);
			gotoPage();
			if (progress != null)
				progress.messageBox("", "��ѯ����");
			progress = null;
		} catch (Exception e) {
			logger.error("error", e);
			if (progress != null) {
				progress.messageBox("����", e.getMessage());
			}
			return;
		} finally {
			setDefaultcursor();
		}
	}

	/**
	 * ��ѯ��������
	 * 
	 * @param dsdefine
	 */
	void doRetrieve(BIReportdsDefine dsdefine) {
		try {
			String sql = dsdefine.getFullsql();
			logger.debug(sql);
			int wantrowcount = ((Integer) jspinMaxrow.getValue()).intValue();

			dsdefine.datadm.clearAll();
			RemotesqlHelper sh = new RemotesqlHelper();
			int fetchcount = 0;
			for (;;) {
				int thiscount = wantrowcount >= 1000 ? 1000 : wantrowcount;
				DBTableModel dm = sh.doSelect(sql, fetchcount, thiscount);
				fetchedrowcount += dm.getRowCount();
				if (progress != null)
					progress.appendMessage("�Ѳ�ѯ��" + fetchedrowcount + "����¼");
				logger.debug("dm.getrowcount()=" + dm.getRowCount());
				dsdefine.datadm.appendDbmodel(dm);
				wantrowcount -= dm.getRowCount();
				fetchcount += dm.getRowCount();
				if (wantrowcount <= 0)
					break;
				if (!dm.hasmore())
					break;

			}

		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	void prepareData() {
		canvas.setDatadm(dsdefine.datadm);
		canvas.prepareData();
	}

	void gotoPage() {
		Integer ipage = (Integer) spPageno.getValue();
		pageno = ipage.intValue() - 1;
		canvas.setPageno(pageno);
		canvas.repaint();
	}

	void delTablev() {
		canvas.deleTablev(editingtablev);
		editingtablev = null;
		repaint();
		bindListplacetable();
	}

	void deleCell() {
		canvas.deleCell(editingcell);
		editingcell = null;
		repaint();
		bindListplacetable();
	}

	void setupFont() {
		JFontChooser fc = new JFontChooser();
		fc.setSelectedFont(editingcell.getFont());
		int ret = fc.showDialog(this);
		if (ret != JFontChooser.OK_OPTION)
			return;
		editingcell.setFontname(fc.getSelectedFontFamily());
		editingcell.setFontsize(fc.getSelectedFontSize());
		int style = fc.getSelectedFontStyle();
		editingcell.setBold((style & Font.BOLD) != 0);
		editingcell.setItalic((style & Font.ITALIC) != 0);

		canvas.repaint();
	}

	void editCellexpr() {
		CellExprDlg dlg = new CellExprDlg(this, createColumntable(),
				editingcell.getExpr(), dsdefine);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		editingcell.setExpr(dlg.getExpr());
		canvas.repaint();
	}

	void addFreecell() {
		BICell cell = new BICell();
		cell.setSize(new Dimension(300, 30));
		cell.setValign(BICell.ALIGN_CENTER);
		cell.setAlign(BICell.ALIGN_CENTER);
		CellExprDlg dlg = new CellExprDlg(this, createColumntable(), cell
				.getExpr(), dsdefine);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		cell.setExpr(dlg.getExpr());
		canvas.addCell(cell);
		repaint();
		bindListplacetable();
	}

	/**
	 * �ı���������
	 * 
	 * @return
	 */
	JPanel createCellProppane() {
		GridBagLayout g = new GridBagLayout();

		JPanel jp = new JPanel(g);
		int line = 0;
		textCellExpr = new JTextArea(3, 40);
		textCellExpr.setEditable(false);
		textCellExpr.setWrapStyleWord(true);
		textCellExpr.setLineWrap(true);
		JScrollPane jscrollp = new JScrollPane(textCellExpr);
		jscrollp.setMinimumSize(new Dimension(200, 64));
		jp.add(jscrollp, new GridBagConstraints(0, line, 2, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;

		JButton btn;
		btn = new JButton("���ñ��ʽ");
		btn.setMargin(new Insets(5, 5, 5, 5));
		btn.setActionCommand("setupexpr");
		btn.addActionListener(this);
		jp.add(btn, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		btn = new JButton("��������");
		btn.setMargin(new Insets(5, 5, 5, 5));
		btn.setActionCommand("setupfont");
		btn.addActionListener(this);
		jp.add(btn, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;

		JLabel lb;
		lb = new JLabel("ˮƽ����");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		String[] ss = { "����", "����", "����" };
		cbCellAlign = new JComboBox(ss);
		cbCellAlign.addItemListener(new CbcellAlignlistener());
		jp.add(cbCellAlign, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("��ֱ����");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		ss = new String[] { "����", "����", "����" };
		cbCellVAlign = new JComboBox(ss);
		cbCellVAlign.addItemListener(new CbcellVAlignlistener());
		jp.add(cbCellVAlign, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("X");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		PositiondocumentListener posdoclistener = new PositiondocumentListener();
		Dimension textfieldsize = new Dimension(40, 27);
		textX = new CNumberTextField(0);
		textX.getDocument().addDocumentListener(posdoclistener);
		textX.setMinimumSize(textfieldsize);
		jp.add(textX, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("Y");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textY = new CNumberTextField(0);
		textY.getDocument().addDocumentListener(posdoclistener);
		textY.setMinimumSize(textfieldsize);
		jp.add(textY, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("��");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textW = new CNumberTextField(0);
		textW.getDocument().addDocumentListener(posdoclistener);
		textW.setMinimumSize(textfieldsize);
		jp.add(textW, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("��");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textH = new CNumberTextField(0);
		textH.getDocument().addDocumentListener(posdoclistener);
		textH.setMinimumSize(textfieldsize);
		jp.add(textH, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		jp.setMaximumSize(new Dimension(180, 300));

		line++;
		// �����ظ�
		lb = new JLabel("�ظ�");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		ss = new String[] { "ÿҳ��ʾ", "��ҳ��ʾ" };
		cbCellrepeat = new JComboBox(ss);
		cbCellrepeat.addItemListener(new CbcellrepeatHandler());

		jp.add(cbCellrepeat, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		btn = new JButton("ɾ����Ԫ��");

		btn.setActionCommand("delcell");
		btn.addActionListener(this);

		jp.add(btn, new GridBagConstraints(0, line, 2, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		return jp;
	}

	class CbcellrepeatHandler implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			String ss[] = { BICell.REPEAT_ALWAYS, BICell.REPEAT_FIRSTPAGE };
			editingcell.setRepeat(ss[cbCellrepeat.getSelectedIndex()]);
			tablevdef.fireDefinechanged();
			canvas.repaint();
		}

	}

	/**
	 * ���ɴ�ֱ������ҳ.
	 * 
	 * @return
	 */
	JPanel createTablevPane() {
		GridBagLayout g = new GridBagLayout();

		JPanel jp = new JPanel(g);
		int line = 0;

		line++;
		JLabel lb = new JLabel("X");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		TableposDocumentListener posdoclistener = new TableposDocumentListener();
		Dimension textfieldsize = new Dimension(40, 27);
		textTableX = new CNumberTextField(0);
		textTableX.getDocument().addDocumentListener(posdoclistener);
		textTableX.setMinimumSize(textfieldsize);
		textTableX.setPreferredSize(textfieldsize);
		jp.add(textTableX, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("Y");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textTableY = new CNumberTextField(0);
		textTableY.getDocument().addDocumentListener(posdoclistener);
		textTableY.setPreferredSize(textfieldsize);
		textTableY.setMinimumSize(textfieldsize);
		jp.add(textTableY, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("��");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textTableW = new CNumberTextField(0);
		textTableW.getDocument().addDocumentListener(posdoclistener);
		textTableW.setPreferredSize(textfieldsize);
		textTableW.setMinimumSize(textfieldsize);
		jp.add(textTableW, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("��");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textTableH = new CNumberTextField(0);
		textTableH.getDocument().addDocumentListener(posdoclistener);
		textTableH.setMinimumSize(textfieldsize);
		textTableH.setPreferredSize(textfieldsize);
		jp.add(textTableH, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("λ��");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		ArrayList<String> ar = new ArrayList<String>();
		ar.add("����λ��");

		Enumeration<ReportcanvasPlaceableIF> enp = canvas.getPlaceables()
				.elements();
		while (enp.hasMoreElements()) {
			ReportcanvasPlaceableIF p = enp.nextElement();
			if (p != tablevrender) {
				ar.add(p.getType());
			}
		}
		String ss[] = new String[ar.size()];
		ar.toArray(ss);

		cbRelatepos = new JComboBox(new String[] { "����λ��" });
		cbRelatepos.addItemListener(new CbRelateposHandle());
		jp.add(cbRelatepos, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		/*
		 * line++; JButton btn = new JButton("ɾ����ֱ��");
		 * 
		 * btn.setActionCommand("deltablev"); btn.addActionListener(this);
		 * 
		 * jp.add(btn, new GridBagConstraints(0, line, 2, 1, 1, 1,
		 * GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1,
		 * 1), 0, 0));
		 */
		jp.setPreferredSize(new Dimension(180, 240));
		jp.setMaximumSize(new Dimension(180, 240));

		cbRelatepos.setSelectedIndex(0);
		return jp;
	}

	/**
	 * �������λ��,����
	 * 
	 * @author user
	 * 
	 */
	class CbRelateposHandle implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			int ii = cbRelatepos.getSelectedIndex();
			// ���ii����0,�����ع�ϵ
			canvas.clearRelatepos();
			if (ii == 0) {
				// ����λ��
				tablevrender.setLayoutstarty(5);
				try {
					tablevrender.prepareData();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				canvas.splitPage();
				canvas.repaint();
				return;
			}
			// �������ĸ�?
			Vector<ReportcanvasPlaceableIF> ps = new Vector<ReportcanvasPlaceableIF>();
			Enumeration<ReportcanvasPlaceableIF> enp = canvas.getPlaceables()
					.elements();
			while (enp.hasMoreElements()) {
				ReportcanvasPlaceableIF p = enp.nextElement();
				if (p != tablevrender) {
					ps.add(p);
				}
			}

			canvas.addRelatepos(ps.elementAt(ii - 1), tablevrender);
			canvas.splitPage();
			canvas.repaint();
		}

	}

	/**
	 * ���ɵ���ҳ
	 * 
	 * @return
	 */
	JPanel createNavpane() {
		GridBagLayout g = new GridBagLayout();
		JPanel jp = new JPanel(g);

		int line = 0;
		JButton btn;
		btn = new JButton("�����ı�");
		btn.setActionCommand("addcell");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		jp.add(btn, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		btn = new JButton("����ͼ��");
		btn.setActionCommand("addchart");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		jp.add(btn, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		btn = new JButton("ɾ��");
		btn.setActionCommand("delcurrent");
		btn.addActionListener(this);
		btn.setMargin(new Insets(1, 1, 1, 1));
		jp.add(btn, new GridBagConstraints(2, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		/*
		 * btn = new JButton("���Ӵ�ֱ��"); btn.setMargin(new Insets(1, 1, 1, 1));
		 * btn.setActionCommand("addtablev"); btn.addActionListener(this);
		 * jp.add(btn, new GridBagConstraints(1, line, 1, 1, 1, 1,
		 * GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 1, 1,
		 * 1), 0, 0));
		 */
		line++;
		listplaceabletable = createListplaceabletable();
		listplaceabletable.getSelectionModel().addListSelectionListener(
				new ListplaceableHandler());
		JScrollPane tablejsp = new JScrollPane(listplaceabletable);
		tablejsp.setPreferredSize(new Dimension(200, 450));
		tablejsp.setMinimumSize(new Dimension(200, 450));
		jp.add(tablejsp, new GridBagConstraints(0, line, 3, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		jp.setPreferredSize(new Dimension(180, 500));
		jp.setMaximumSize(new Dimension(180, 500));

		return jp;
	}

	class ListplaceableHandler implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			int row = listplaceabletable.getSelectedRow();
			if (row < 0)
				return;
			canvas.setActiveindex(row);
			canvas.repaint();
		}

	}

	/**
	 * �г�����placeable ��table��
	 */
	void bindListplacetable() {
		DBTableModel dm = (DBTableModel) listplaceabletable.getModel();
		dm.clearAll();
		Enumeration<ReportcanvasPlaceableIF> en = canvas.getPlaceables()
				.elements();
		while (en.hasMoreElements()) {
			ReportcanvasPlaceableIF p = en.nextElement();
			int row = dm.getRowCount();
			dm.appendRow();
			dm.setItemValue(row, "placeabletype", p.getType());
		}
		listplaceabletable.tableChanged(new TableModelEvent(dm));
		listplaceabletable.autoSize();

		ReportcanvasPlaceableIF priorcomp = canvas.getPriorcomp(tablevrender);
		int relateposselectedindex = 0;
		ArrayList<String> ar = new ArrayList<String>();
		ar.add("����λ��");
		Enumeration<ReportcanvasPlaceableIF> enp = canvas.getPlaceables()
				.elements();
		while (enp.hasMoreElements()) {
			ReportcanvasPlaceableIF p = enp.nextElement();
			if (p != tablevrender) {
				if (p == priorcomp) {
					relateposselectedindex = ar.size();
				}
				ar.add(p.getType());
			}
		}
		String ss[] = new String[ar.size()];
		ar.toArray(ss);

		DefaultComboBoxModel dcbm = new DefaultComboBoxModel(ss);
		cbRelatepos.setModel(dcbm);
		cbRelatepos.setSelectedIndex(relateposselectedindex);

	}

	/**
	 * ���ɵ���pane�ڵ�Ԫ���嵥
	 * 
	 * @return
	 */
	CTable createListplaceabletable() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("placeabletype", "varchar", "Ԫ��");
		cols.add(col);
		DBTableModel dm = new DBTableModel(cols);
		dm.appendRow();
		CTable table = new CTable(dm);
		table.setReadonly(true);
		return table;
	}

	/**
	 * ҳ������
	 * 
	 * @return
	 */
	JPanel createPaperPane() {
		GridBagLayout g = new GridBagLayout();

		JPanel jp = new JPanel(g);
		int line = 0;
		JLabel lb = new JLabel("ֽ��");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		String mediasizenames[] = { "�Զ���", "A0", "A1", "A2", "A3", "A4", "A5",
				"A6", "A7", "A8", "A9", "A10", "B0", "B1", "B2", "B3", "B4",
				"B5", "B6", "B7", "B8", "B9", "B10", };
		cbPapername = new JComboBox(mediasizenames);
		jp.add(cbPapername, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		cbPapername.addItemListener(new PapernameHandler(cbPapername));

		line++;
		lb = new JLabel("ֽ�ſ�(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		PapersizeDocumentListener posdoclistener = new PapersizeDocumentListener();
		Dimension textfieldsize = new Dimension(60, 27);
		textPaperwidth = new CNumberTextField(1);
		textPaperwidth.getDocument().addDocumentListener(posdoclistener);
		textPaperwidth.setMinimumSize(textfieldsize);
		textPaperwidth.setPreferredSize(textfieldsize);
		jp.add(textPaperwidth, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		line++;
		lb = new JLabel("ֽ�Ÿ�(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperheight = new CNumberTextField(1);
		textPaperheight.getDocument().addDocumentListener(posdoclistener);
		textPaperheight.setPreferredSize(textfieldsize);
		textPaperheight.setMinimumSize(textfieldsize);
		jp.add(textPaperheight, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("��߽�(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperleftmargin = new CNumberTextField(1);
		textPaperleftmargin.setText("10");
		textPaperleftmargin.getDocument().addDocumentListener(posdoclistener);
		textPaperleftmargin.setPreferredSize(textfieldsize);
		textPaperleftmargin.setMinimumSize(textfieldsize);
		jp.add(textPaperleftmargin, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("�ұ߽�(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperrightmargin = new CNumberTextField(1);
		textPaperrightmargin.setText("10");
		textPaperrightmargin.getDocument().addDocumentListener(posdoclistener);
		textPaperrightmargin.setPreferredSize(textfieldsize);
		textPaperrightmargin.setMinimumSize(textfieldsize);
		jp.add(textPaperrightmargin, new GridBagConstraints(1, line, 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("�ϱ߽�(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPapertopmargin = new CNumberTextField(1);
		textPapertopmargin.setText("10");
		textPapertopmargin.getDocument().addDocumentListener(posdoclistener);
		textPapertopmargin.setPreferredSize(textfieldsize);
		textPapertopmargin.setMinimumSize(textfieldsize);
		jp.add(textPapertopmargin, new GridBagConstraints(1, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		line++;
		lb = new JLabel("�±߽�(MM)");
		jp.add(lb, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));
		textPaperbottommargin = new CNumberTextField(0);
		textPaperbottommargin.setText("10");
		textPaperbottommargin.getDocument().addDocumentListener(posdoclistener);
		textPaperbottommargin.setPreferredSize(textfieldsize);
		textPaperbottommargin.setMinimumSize(textfieldsize);
		jp.add(textPaperbottommargin, new GridBagConstraints(1, line, 1, 1, 1,
				1, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));

		jp.setPreferredSize(new Dimension(180, 200));
		jp.setMaximumSize(new Dimension(180, 200));

		line++;
		cbLandscape = new JCheckBox("�����ӡ");
		cbLandscape.addChangeListener(new CbLandscapehandler());
		jp.add(cbLandscape, new GridBagConstraints(0, line, 1, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1,
						1, 1, 1), 0, 0));

		return jp;
	}

	class CbLandscapehandler implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			landscape = cbLandscape.isSelected();
			onPaperSizechanged();
		}

	}

	class PapernameHandler implements ItemListener {
		JComboBox cbPapername;

		PapernameHandler(JComboBox cbPapername) {
			this.cbPapername = cbPapername;
		}

		public void itemStateChanged(ItemEvent e) {
			String medianame = (String) cbPapername.getSelectedItem();
			MediaSize mediasize = MediaSize.ISO.A4;
			if (medianame.equals("A0")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A0);
			} else if (medianame.equals("A1")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A1);
			} else if (medianame.equals("A2")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A2);
			} else if (medianame.equals("A3")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A3);
			} else if (medianame.equals("A4")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A4);
			} else if (medianame.equals("A5")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A5);
			} else if (medianame.equals("A6")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A6);
			} else if (medianame.equals("A7")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A7);
			} else if (medianame.equals("A8")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A8);
			} else if (medianame.equals("A9")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_A9);
			} else if (medianame.equals("A10")) {
				mediasize = MediaSize
						.getMediaSizeForName(MediaSizeName.ISO_A10);
			} else if (medianame.equals("B0")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B0);
			} else if (medianame.equals("B1")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B1);
			} else if (medianame.equals("B2")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B2);
			} else if (medianame.equals("B3")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B3);
			} else if (medianame.equals("B4")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B4);
			} else if (medianame.equals("B5")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B5);
			} else if (medianame.equals("B6")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B6);
			} else if (medianame.equals("B7")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B7);
			} else if (medianame.equals("B8")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B8);
			} else if (medianame.equals("B9")) {
				mediasize = MediaSize.getMediaSizeForName(MediaSizeName.ISO_B9);
			} else if (medianame.equals("B10")) {
				mediasize = MediaSize
						.getMediaSizeForName(MediaSizeName.ISO_B10);
			}
			DecimalFormat dfmt = new DecimalFormat("0.0");
			float wh[] = mediasize.getSize(MediaSize.MM);
			settingvalue = true;
			textPaperwidth.setText(dfmt.format(wh[0]));
			textPaperheight.setText(dfmt.format(wh[1]));
			settingvalue = false;
			onPaperSizechanged();

		}
	}

	/**
	 * ������ֽ�Ŵ�С�������������׺�,��ɻ�����Ĵ�С
	 */
	void onPaperSizechanged() {
		try {
			setWaitcursor();
			if (settingvalue)
				return;
			if (canvas == null)
				return;
			Rectangle rect = calcPrintarea();
			if (rect == null)
				return;
			landscape = cbLandscape.isSelected();
			if (landscape) {
				canvas.setAreasize(new Dimension(rect.height, rect.width));
			} else {
				canvas.setAreasize(new Dimension(rect.width, rect.height));
			}
			tablevdef.fireDefinechanged();
			canvas.repaint();
			recalcScrollpane(null);
		} finally {
			setDefaultcursor();
		}
	}

	class CbcellAlignlistener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (editingcell == null)
				return;
			int i = cbCellAlign.getSelectedIndex();
			int[] as = { BICell.ALIGN_LEFT, BICell.ALIGN_CENTER,
					BICell.ALIGN_RIGHT };
			editingcell.setAlign(as[i]);
			canvas.repaint();
		}
	}

	class CbcellVAlignlistener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (editingcell == null)
				return;
			int i = cbCellVAlign.getSelectedIndex();
			int[] as = { BICell.ALIGN_NORTH, BICell.ALIGN_CENTER,
					BICell.ALIGN_SOUTH };
			editingcell.setValign(as[i]);
			canvas.repaint();
		}
	}

	public void editCell(BICell cell, Rectangle pos) {
		this.editingcell = cell;
		this.editingcellpos = pos;
		settingvalue = true;
		proptabbedpane.setSelectedIndex(1);
		proptabbedpane.setEnabledAt(1, true);

		textCellExpr.setText(cell.getExpr());
		int in = 0;
		if (cell.getAlign() == BICell.ALIGN_LEFT)
			in = 0;
		else if (cell.getAlign() == BICell.ALIGN_CENTER)
			in = 1;
		else if (cell.getAlign() == BICell.ALIGN_RIGHT)
			in = 2;
		cbCellAlign.setSelectedIndex(in);

		if (cell.getValign() == BICell.ALIGN_NORTH)
			in = 0;
		else if (cell.getValign() == BICell.ALIGN_CENTER)
			in = 1;
		else if (cell.getValign() == BICell.ALIGN_SOUTH)
			in = 2;
		cbCellVAlign.setSelectedIndex(in);

		textX.setText(String.valueOf(pos.x));
		textY.setText(String.valueOf(pos.y));
		textW.setText(String.valueOf(pos.width));
		textH.setText(String.valueOf(pos.height));

		if (cell.getRepeat().equals(BICell.REPEAT_FIRSTPAGE)) {
			cbCellrepeat.setSelectedIndex(1);
		} else {
			cbCellrepeat.setSelectedIndex(0);
		}

		int r = canvas.getActiveindex();
		listplaceabletable.setRowSelectionInterval(r, r);

		settingvalue = false;
	}

	public void editTablev(BITableV_Render render, Rectangle pos) {
		this.editingtablev = render;
		this.editingtablevpos = pos;
		settingvalue = true;
		proptabbedpane.setSelectedIndex(2);
		proptabbedpane.setEnabledAt(2, true);

		textTableX.setText(String.valueOf(pos.x));
		textTableY.setText(String.valueOf(pos.y));
		textTableW.setText(String.valueOf(pos.width));
		textTableH.setText(String.valueOf(pos.height));
		int r = canvas.getActiveindex();
		listplaceabletable.setRowSelectionInterval(r, r);
		settingvalue = false;
	}

	/**
	 * ���¼��������
	 */
	public void recalcScrollpane(ReportcanvasPlaceableIF placeable) {
		canvasscrollp.setViewportView(canvas);
		repaint();
	}

	/**
	 * ������������pane��ֵ
	 */
	boolean settingvalue = false;
	protected CNumberTextField textTableX;
	protected CNumberTextField textTableY;
	protected CNumberTextField textTableW;
	protected CNumberTextField textTableH;
	protected JLabel lbpagecount;
	protected JSpinner spPageno;
	protected JScrollPane canvasscrollp;
	protected CNumberTextField textPaperwidth;
	protected CNumberTextField textPaperheight;
	protected JComboBox cbPapername;
	protected CNumberTextField textPapertopmargin;
	protected CNumberTextField textPaperbottommargin;
	protected CNumberTextField textPaperleftmargin;
	protected CNumberTextField textPaperrightmargin;
	protected CTable listplaceabletable;
	protected BIReportdsPane dspane;
	protected JComboBox cbRelatepos;
	protected PageFormat curpageformat;

	class PositiondocumentListener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			setEditingpos();
		}

		public void insertUpdate(DocumentEvent e) {
			setEditingpos();
		}

		public void removeUpdate(DocumentEvent e) {
			setEditingpos();
		}
	}

	void setEditingpos() {
		if (editingcellpos == null || settingvalue)
			return;
		try {
			editingcellpos.x = Integer.parseInt(textX.getText());
		} catch (Exception e) {
		}
		try {
			editingcellpos.y = Integer.parseInt(textY.getText());
		} catch (Exception e) {
		}
		try {
			editingcellpos.width = Integer.parseInt(textW.getText());
		} catch (Exception e) {
		}
		try {
			editingcellpos.height = Integer.parseInt(textH.getText());
		} catch (Exception e) {
		}

		canvas.positionChanged(editingcellpos);

		canvas.repaint();

	}

	class PapersizeDocumentListener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			onPaperSizechanged();
		}

		public void insertUpdate(DocumentEvent e) {
			onPaperSizechanged();
		}

		public void removeUpdate(DocumentEvent e) {
			onPaperSizechanged();
		}
	}

	class TableposDocumentListener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			setEditingtablepos();
		}

		public void insertUpdate(DocumentEvent e) {
			setEditingtablepos();
		}

		public void removeUpdate(DocumentEvent e) {
			setEditingtablepos();
		}
	}

	void setEditingtablepos() {
		if (editingtablev == null || settingvalue)
			return;
		try {
			editingtablevpos.x = Integer.parseInt(textTableX.getText());
		} catch (Exception e) {
		}
		try {
			editingtablevpos.y = Integer.parseInt(textTableY.getText());
		} catch (Exception e) {
		}
		try {
			editingtablevpos.width = Integer.parseInt(textTableW.getText());
		} catch (Exception e) {
		}
		try {
			editingtablevpos.height = Integer.parseInt(textTableH.getText());
		} catch (Exception e) {
		}

		canvas.positionChanged(editingtablevpos);

		canvas.repaint();

	}

	public void onTabledefineChanged() {
		pagecount = canvas.getPagecount();
		SpinnerNumberModel spm = null;
		int min = 0, max = 0;
		if (pagecount == 0) {
			spm = new SpinnerNumberModel(0, 0, 0, 1);
		} else {
			if (pageno > pagecount - 1) {
				pageno = pagecount - 1;
			}
			min = 0;
			max = pagecount - 1;
			spm = new SpinnerNumberModel(pageno + 1, min + 1, max + 1, 1);
		}
		spPageno.setModel(spm);
		lbpagecount.setText("��" + pagecount + "ҳ");

		canvas.repaint();
		canvasscrollp.setViewportView(canvas);
	}

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;
		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";

		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";

/*		DefaultNPParam.debugdbip = "192.9.200.78";
		DefaultNPParam.debugdbpasswd = "xmzyc";
		DefaultNPParam.debugdbsid = "O92";
		DefaultNPParam.debugdbusrname = "xmzyc0903";
*/
		DefaultNPParam.prodcontext = "npserver";
		ClientUserManager.getCurrentUser().setUserid("0");

		ReportcanvasFrame frm = new ReportcanvasFrame();
		frm.pack();
		frm.setVisible(true);
	}

	public BITableV_Render getTablevrender() {
		return tablevrender;
	}

	class MaintabpaneHandler implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			JTabbedPane tp = (JTabbedPane) e.getSource();
			int ii = tp.getSelectedIndex();
			if (ii == 1) {
				dspane.reverseBind();
				designpane.refreshColumndef();
				Tabledesign_table table = designpane.getTable();
				DBTableModel dm = (DBTableModel) table.getModel();
				if (dm.getRowCount() > 0 && table.getColumnCount() > 1) {
					int r = table.getRow();
					if (r < 0)
						r = 0;
					int c = table.getCurcol();
					if (c < 0) {
						if (c < 0)
							c = 1;
					}
					table.editCellAt(r, c);
				}
			} else {
				if (designpane != null && designpane.table != null) {
					designpane.table.stopEdit();
				}
			}
		}

	}

	void addChart() {
		BiChartRender chartrender = new BiChartRender();
		BichartSetupDlg dlg = new BichartSetupDlg(this, dstable, chartrender);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		setWaitcursor();
		chartrender.prepareData();
		canvas.addChart(chartrender);
		canvas.repaint();
		bindListplacetable();
		setDefaultcursor();
	}

	public void editChart(BiChartRender chartrender, Rectangle pos) {
		BichartSetupDlg dlg = new BichartSetupDlg(this, dstable, chartrender);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;
		setWaitcursor();
		canvas.prepareData();
		canvas.repaint();
		setDefaultcursor();
	}

	void doSave() {
		dspane.reverseBind();

		if (bifile == null) {
			if (jfc == null) {
				jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File("."));
				jfc.setFileFilter(new Npbifilter());
			}
			int ret = jfc.showDialog(this, "ѡ��Ҫ����ı����ļ�");
			if (ret != JFileChooser.APPROVE_OPTION)
				return;
			bifile = jfc.getSelectedFile();
		}
		if (!bifile.getName().endsWith(".npbi")) {
			bifile = new File(bifile.getParentFile(), bifile.getName()
					+ ".npbi");
		}

		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(bifile));
			out.println("<bireport>");
			out.println("<opid>" + rptopid + "</opid>");
			out.println("<opname>" + opname + "</opname>");
			out.println("<opcode>" + opcode + "</opcode>");
			out.println("<groupname>" + groupname + "</groupname>");
			out.println("<prodname>" + prodname + "</prodname>");
			out.println("<modulename>" + modulename + "</modulename>");
			// ���ֽ��.
			out.println("<paper>");
			out.println("<papername>" + cbPapername.getSelectedItem()
					+ "</papername>");
			out.println("<paperwidth>" + textPaperwidth.getText()
					+ "</paperwidth>");
			out.println("<paperheight>" + textPaperheight.getText()
					+ "</paperheight>");
			out.println("<paperleftmargin>" + textPaperleftmargin.getText()
					+ "</paperleftmargin>");
			out.println("<paperrightmargin>" + textPaperrightmargin.getText()
					+ "</paperrightmargin>");
			out.println("<papertopmargin>" + textPapertopmargin.getText()
					+ "</papertopmargin>");
			out.println("<paperbottommargin>" + textPaperbottommargin.getText()
					+ "</paperbottommargin>");
			out.println("<landscape>" + (landscape ? "true" : "false")
					+ "</landscape>");
			out.println("</paper>");
			// д��ds
			Enumeration<BIReportdsDefine> en = dstable.elements();
			while (en.hasMoreElements()) {
				BIReportdsDefine ds = en.nextElement();
				BIReportStorage.writeDS(out, ds);
			}
			canvas.write(out);
			out.println("</bireport>");
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	void doPrint() {

		PrinterJob printerjob = PrinterJob.getPrinterJob();
		PrintsetupDialog psetupdlg = new PrintsetupDialog(this, printerjob,
				"print", pagecount);
		psetupdlg.setPagecount(canvas.getPagecount() * canvas.getHpagecount());
		psetupdlg.pack();
		psetupdlg.setVisible(true);
		if (!psetupdlg.getOk())
			return;

		HashPrintRequestAttributeSet prats = new HashPrintRequestAttributeSet();
		// ����ҳ��Χ
		PageRanges prang = new PageRanges(psetupdlg.getStartpage(), psetupdlg
				.getEndpage());
		prats.add(prang);

		// ���÷�
		Copies pcopies = new Copies(psetupdlg.getCopies());
		prats.add(pcopies);

		String printername = psetupdlg.getPrintername();
		PrintService printservice = null;
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(
				null, prats);
		for (int i = 0; i < printServices.length; i++) {
			if (printServices[i].getName().equals(printername)) {
				try {
					printerjob.setPrintService(printServices[i]);
				} catch (PrinterException e) {
					// TODO Auto-generated catch block
					logger.error("error", e);
				}
				break;
			}
		}
		curpageformat = printerjob.defaultPage();
		curpageformat.setPaper(createPaper());

		printerjob.setPrintable(this);
		printerjob.setPageable(this);
		int oldpageno = canvas.getPageno();
		boolean olddeveoping = canvas.isDeveoping();
		try {
			// Rectangle rect = calcPrintarea();
			// canvas.setAreasize(new Dimension(rect.width,rect.height));
			// canvas.prepareData();
			// canvas.splitPage();
			canvas.setDeveoping(false);
			printerjob.print(prats);
		} catch (PrinterException e) {
			logger.error("error", e);
			errorMessage("����", e.getMessage());
		} finally {
			canvas.setDeveoping(olddeveoping);
			spPageno.setValue(oldpageno + 1);
			gotoPage();
		}

	}

	Paper createPaper() {
		Paper paper = new Paper();
		Dimension psize = getPapersize();
		paper.setSize(psize.getWidth(), psize.getHeight());
		Rectangle rect = calcPrintarea();
		// paper.setImageableArea(rect.x, rect.y, rect.width, rect.height);
		paper.setImageableArea(0, 0, psize.getWidth(), psize.getHeight());
		return paper;
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		int allpagecount = canvas.getPagecount() * canvas.getHpagecount();
		if (pageIndex < 0 || pageIndex >= allpagecount)
			return Printable.NO_SUCH_PAGE;

		int realpageno = pageIndex / canvas.getHpagecount();
		canvas.setPageno(realpageno);
		// Dimension papersize = getPapersize();
		Rectangle rect = calcPrintarea();
		Graphics2D g2 = (Graphics2D) g.create(rect.x, rect.y, rect.width,
				rect.height);
		if (landscape) {
			g2.rotate(-90.0 / 360.0 * 2 * Math.PI);
			g2.translate(-rect.height, 0);
		}
		AffineTransform oldtran = g2.getTransform();
		AffineTransform newtran = new AffineTransform(oldtran);
		// ����ˮƽ��ҳ,Ҫ��ƫ��
		int hpageno = pageIndex % canvas.getHpagecount();
		double xtranslate = 0;
		if (hpageno > 0) {
			if (landscape) {
				xtranslate -= (double) hpageno * (double) rect.height;
			} else {
				xtranslate -= (double) hpageno * (double) rect.width;
			}
		}
		newtran.translate(xtranslate, 0);

		g2.setTransform(newtran);
		canvas.setPrinting(true);
		canvas.paint(g2);
		canvas.setPrinting(false);

		g2.setTransform(oldtran);
		g2.dispose();
		return Printable.PAGE_EXISTS;
	}

	public int getNumberOfPages() {
		return canvas.getPagecount() * canvas.getHpagecount();
	}

	Rectangle calcPrintarea() {
		try {
			Rectangle rect = new Rectangle();
			float wmm = Float.parseFloat(textPaperwidth.getText());
			float hmm = Float.parseFloat(textPaperheight.getText());
			float left = Float.parseFloat(textPaperleftmargin.getText());
			float right = Float.parseFloat(textPaperrightmargin.getText());
			float top = Float.parseFloat(textPapertopmargin.getText());
			float bottom = Float.parseFloat(textPaperbottommargin.getText());
			double paperw = wmm / 25.4 * 72;
			double paperh = hmm / 25.4 * 72;

			double areax = left / 25.4 * 72;
			double arear = right / 25.4 * 72;
			double areat = top / 25.4 * 72;
			double areab = bottom / 25.4 * 72;
			rect.x = (int) (areax + 0.5);
			rect.y = (int) (areat + 0.5);
			rect.width = (int) (paperw - areax - arear + 0.5);
			rect.height = (int) (paperh - areat - areab + 0.5);
			return rect;
		} catch (Exception e) {
			return null;
		}
	}

	public Dimension getPapersize() {
		float wmm = Float.parseFloat(textPaperwidth.getText());
		float hmm = Float.parseFloat(textPaperheight.getText());
		wmm = wmm / 25.4f * 72f;
		hmm = hmm / 25.4f * 72f;
		return new Dimension((int) (wmm + .5), (int) (hmm + 0.5));
	}

	public PageFormat getPageFormat(int pageIndex)
			throws IndexOutOfBoundsException {
		return curpageformat;
	}

	public Printable getPrintable(int pageIndex)
			throws IndexOutOfBoundsException {
		return this;
	}

	void loadFromfile(File f) {
		BufferedReader rd = null;
		try {
			setWaitcursor();
			settingvalue = true;
			rd = new BufferedReader(new FileReader(f));
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<opid>")) {
					rptopid = BIReportStorage.getValue(line);
				} else if (line.startsWith("<opname>")) {
					opname = BIReportStorage.getValue(line);
					setTitle("����:" + opname);
				} else if (line.startsWith("<opcode>")) {
					opcode = BIReportStorage.getValue(line);
				} else if (line.startsWith("<groupname>")) {
					groupname = BIReportStorage.getValue(line);
				} else if (line.startsWith("<prodname>")) {
					prodname = BIReportStorage.getValue(line);
				} else if (line.startsWith("<modulename>")) {
					modulename = BIReportStorage.getValue(line);
				} else if (line.startsWith("<papername>")) {
					cbPapername.setSelectedItem(BIReportStorage.getValue(line));
				} else if (line.startsWith("<paperwidth>")) {
					textPaperwidth.setText(BIReportStorage.getValue(line));
				} else if (line.startsWith("<paperheight>")) {
					textPaperheight.setText(BIReportStorage.getValue(line));
				} else if (line.startsWith("<paperleftmargin>")) {
					textPaperleftmargin.setText(BIReportStorage.getValue(line));
				} else if (line.startsWith("<paperrightmargin>")) {
					textPaperrightmargin
							.setText(BIReportStorage.getValue(line));
				} else if (line.startsWith("<papertopmargin>")) {
					textPapertopmargin.setText(BIReportStorage.getValue(line));
				} else if (line.startsWith("<paperbottommargin>")) {
					textPaperbottommargin.setText(BIReportStorage
							.getValue(line));
				} else if (line.startsWith("<landscape>")) {
					cbLandscape.setSelected(BIReportStorage.getValue(line)
							.equals("true"));
				}
			}
			rd.close();

			rd = new BufferedReader(new FileReader(f));
			dstable = BIReportStorage.readDs(rd);
			if (dstable.size() > 0) {
				dsdefine = dstable.elementAt(0);
			}
			rd.close();
			rd = new BufferedReader(new FileReader(f));
			canvas.reset();
			canvas.read(rd);
			settingvalue = false;

			// ��������
			setWaitcursor();

			tablevrender.setDsdefine(dsdefine);
			dspane.bind();
			designpane.bind();
			bindListplacetable();
			canvas.setDatadm(dsdefine.datadm);
			onPaperSizechanged();
		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			setDefaultcursor();
			if (rd != null)
				try {
					rd.close();
				} catch (IOException e) {
					logger.error("error", e);
				}
		}

	}

	/**
	 * ȡ����.
	 * 
	 * @param p
	 * @return
	 */
	public String getParameter(String p) {
		Enumeration<BIReportparamdefine> en = dsdefine.params.elements();
		while (en.hasMoreElements()) {
			BIReportparamdefine pdef = en.nextElement();
			if (pdef.paramname.equals(p)) {
				return pdef.getInputvalue();
			}
		}
		return null;
	}

	JFileChooser jfc = null;
	protected JSpinner jspinMaxrow;
	private JCheckBox cbLandscape;

	void doOpen() {
		if (jfc == null) {
			jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
		}
		jfc.setFileFilter(new Npbifilter());
		int ret = jfc.showDialog(this, "ѡ��Ҫ�򿪵ı����ļ�");
		if (ret != JFileChooser.APPROVE_OPTION)
			return;
		File f = jfc.getSelectedFile();
		setWaitcursor();
		loadFromfile(f);
		bifile = f;
		querydlg = null;
		setDefaultcursor();
	}

	void doNew() {
		ReportopDlg dlg = new ReportopDlg(this, true);
		dlg.pack();
		dlg.setVisible(true);
		if (!dlg.isOk())
			return;

		querydlg = null;
		bifile = null;
		dsdefine.reset();
		dstable.clear();
		dstable.add(dsdefine);
		tablevrender.reset();
		tablevdef.reset();
		tablevrender.setDsdefine(dsdefine);
		canvas.reset();
		canvas.addTablev(tablevrender);
		dspane.bind();
		designpane.reset();
		designpane.bind();
		bindListplacetable();
		canvas.setDatadm(dsdefine.datadm);
		tablevdef.fireDefinechanged();

	}

	class Npbifilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			return f.getName().endsWith(".npbi");
		}

		@Override
		public String getDescription() {
			return "npƽ̨BI�����ļ�(.npbi)";
		}

	}

	protected void setHotkey() {
		JComponent compcp = (JComponent) getContentPane();
		// F8 ��ѯ
		KeyStroke vkf8 = KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false);
		compcp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(vkf8,
				"retrieve");
		compcp.getActionMap().put("retrieve", new Hotkeyhandler("retrieve"));

	}

	class Hotkeyhandler extends AbstractAction {

		public Hotkeyhandler(String name) {
			super(name);
			putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			ReportcanvasFrame.this.actionPerformed(e);
		}

	}

	public void onParamsdefchanged() {
		querydlg = null;
	}

	public String getRptOpid() {
		return rptopid;
	}

	public void setRptopid(String opid) {
		this.rptopid = opid;
	}

	public void setOpid(String opid) {
		super.setOpid(opid);
		boolean needdownloadmodule = DownloadManager.getInst().isNeeddownload(
				"npserver", "npbichart");
		logger.debug("needdownloadmodule=" + needdownloadmodule);
		try {
			DownloadManager.getInst().prepareModulejar("npserver", "npbichart");
		} catch (Exception e) {
			logger.error("error", e);
			errorMessage("����", e.getMessage());
			return;
		}

		if (needdownloadmodule) {
			SystemexitThread.addExitproc(new ExitRestartProc());
			JOptionPane.showMessageDialog(this, "�ӷ��������������л���,��Ҫ��������.");
			System.exit(0);
			return;
		}

	}

	public String getOpcode() {
		return opcode;
	}

	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}

	public String getOpname() {
		return opname;
	}

	public void setOpname(String opname) {
		this.opname = opname;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getProdname() {
		return prodname;
	}

	public void setProdname(String prodname) {
		this.prodname = prodname;
	}

	public String getModulename() {
		return modulename;
	}

	public void setModulename(String modulename) {
		this.modulename = modulename;
	}

	void setupOpprop() {
		ReportopDlg dlg = new ReportopDlg(this, false);
		dlg.pack();
		dlg.setVisible(true);
	}

	void doUpload() {
		if (jfc == null) {
			jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
			jfc.setFileFilter(new Npbifilter());
		}
		jfc.setMultiSelectionEnabled(true);
		int ret = jfc.showDialog(this, "ѡ��Ҫ�ϴ��ı����ļ�");
		if (ret != JFileChooser.APPROVE_OPTION) {
			jfc.setMultiSelectionEnabled(false);
			return;
		}
		File afs[] = jfc.getSelectedFiles();
		for (int i = 0; i < afs.length; i++) {
			doUpload(afs[i]);
		}
		jfc.setMultiSelectionEnabled(false);

	}

	/**
	 * �ϴ�һ���ļ�
	 * 
	 * @param af
	 */
	void doUpload(File af) {
		BufferedReader rd = null;
		String opid = "";
		String opname = "";
		String opcode = "";
		String groupname = "";
		String prodname = "";
		String modulename = "";
		try {
			setWaitcursor();
			rd = new BufferedReader(new FileReader(af));
			String line;
			while ((line = rd.readLine()) != null) {
				if (line.startsWith("<opid>")) {
					opid = BIReportStorage.getValue(line);
				} else if (line.startsWith("<opname>")) {
					opname = BIReportStorage.getValue(line);
				} else if (line.startsWith("<opcode>")) {
					opcode = BIReportStorage.getValue(line);
				} else if (line.startsWith("<groupname>")) {
					groupname = BIReportStorage.getValue(line);
				} else if (line.startsWith("<groupname>")) {
					groupname = BIReportStorage.getValue(line);
				} else if (line.startsWith("<prodname>")) {
					prodname = BIReportStorage.getValue(line);
				} else if (line.startsWith("<modulename>")) {
					modulename = BIReportStorage.getValue(line);
				}
			}
			rd.close();

			if (opid.length() == 0 || opname.length() == 0) {
				errorMessage("����", "����һ���Ϸ���npbi�ļ�");
				return;
			}

			int length = (int) af.length();
			int buflen = 102400;
			byte[] buf = new byte[buflen];
			int totalsend = 0;
			FileInputStream fin = new FileInputStream(af);
			try {
				while (length > 0) {
					ClientRequest req = new ClientRequest("npclient:�ϴ�bi����");
					ParamCommand pcmd = new ParamCommand();
					req.addCommand(pcmd);
					int rded = fin.read(buf);
					pcmd.addParam("opid", opid);
					pcmd.addParam("opname", opname);
					pcmd.addParam("opcode", opcode);
					pcmd.addParam("groupname", groupname);
					pcmd.addParam("prodname", prodname);
					pcmd.addParam("modulename", modulename);
					pcmd.addParam("length", String.valueOf(rd));
					pcmd.addParam("startpos", String.valueOf(totalsend));
					totalsend += rded;
					length -= rded;
					pcmd.addParam("finished", length == 0 ? "true" : "false");
					BinfileCommand bincmd = new BinfileCommand(buf, 0, rded);
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

		} catch (Exception e) {
			logger.error("error", e);
			errorMessage("����", e.getMessage());
			return;
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
				}
			}
			setDefaultcursor();
		}
	}

	void doExit() {
		dispose();

	}

	void exportPng() {
		BufferedImage img = new BufferedImage(500, 700,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setColor(Color.white);
		g2.fillRect(0, 0, 500, 700);
		canvas.setPrinting(false);
		canvas.paint(g2);
		try {
			ImageIO.write(img, "png", new File("font.png"));
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	JFileChooser jfcexcel = null;

	void exportExcel() {
		if (jfcexcel == null) {
			jfcexcel = new JFileChooser(new File("."));
			jfcexcel.setFileFilter(new ExcelFilter());
		}

		jfcexcel.setDialogType(JFileChooser.SAVE_DIALOG);
		jfcexcel.setSelectedFile(new File(opname + ".xls"));
		int ret = jfcexcel.showDialog(this, "�����ļ�");
		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File outf = jfcexcel.getSelectedFile();
		try {
			//tablevrender.exportExcel(outf, opname);
			
			//�����˱����ͷ����
			canvas.exportExcel(outf,opname);
		} catch (Exception e) {
			logger.error("error", e);
			errorMessage("����", e.getMessage());
		}

	}

	class ExcelFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			if (f.getName().toLowerCase().endsWith(".xls"))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "Excel�ļ�(*.xls)";
		}

	}

	public boolean isAutoquery() {
		return autoquery;
	}

	public void setAutoquery(boolean autoquery) {
		this.autoquery = autoquery;
	}

}
