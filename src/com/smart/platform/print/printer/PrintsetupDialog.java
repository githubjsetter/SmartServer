package com.smart.platform.print.printer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Vector;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.smart.platform.env.Configer;
import com.smart.platform.gui.control.CButton;
import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CFormlineBreak;
import com.smart.platform.gui.control.CNumberTextField;
import com.smart.platform.util.DefaultNPParam;

/**
 * ��ӡ������
 * 
 * ��ӡ�� ֽ�� ���� ҳ�뷶Χ
 * 
 * @author Administrator
 * 
 */
public class PrintsetupDialog extends CDialog {

	PrinterJob job = null;
	boolean ok = false;
	int pagecount;
	int maxpagecount = 100;
	String planname;
	Configer config = null;
	JRadioButton jrAllcopys = new JRadioButton("����");
	JRadioButton jrStartcopy = new JRadioButton("�ڼ���");

	public PrintsetupDialog(Frame frame, PrinterJob job, String planname,
			int maxpagecount) {
		super(frame, "��ӡ������", true);
		this.job = job;
		this.planname = planname;
		this.maxpagecount = maxpagecount;
		config = new Configer(new File("conf/" + planname + ".properties"));

		loadInitpaper();
		initDialog();
		localCenter();
		setHotkey();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
	}

	public boolean getOk() {
		return ok;
	}

	public String getPrintername() {
		return (String) cbPrinter.getSelectedItem();
	}

	public double getPaperwidth() {
		double d;
		try {
			return Double.parseDouble(this.textPaperwidth.getText());
		} catch (Exception e) {
			Paperinfo pinfo = (Paperinfo) cbPapersize.getSelectedItem();
			return pinfo.width;
		}
	}

	public double getPaperheight() {
		double d;
		try {
			return Double.parseDouble(this.textPaperheight.getText());
		} catch (Exception e) {
			Paperinfo pinfo = (Paperinfo) cbPapersize.getSelectedItem();
			return pinfo.height;
		}
	}

	public int getStartpage() {
		int p;
		try {
			p = Integer.parseInt(this.textStartpage.getText());
		} catch (Exception e) {
			p = 1;
		}
		return p;
	}

	public int getEndpage() {
		int p;
		try {
			p = Integer.parseInt(this.textEndpage.getText());
		} catch (Exception e) {
			p = 1;
		}
		return p;
	}

	public int getCopymode() {
		if (jrAllcopys.isSelected()) {
			return 0;
		}
		return 1;
	}

	public int getCopies() {
		int p;
		try {
			p = Integer.parseInt(this.textCopies.getText());
		} catch (Exception e) {
			p = 1;
		}
		return p;
	}

	public int getCopies1() {
		int p;
		try {
			p = Integer.parseInt(this.textCopies1.getText());
		} catch (Exception e) {
			p = 1;
		}
		return p;
	}

	public int getCopies2() {
		int p;
		try {
			p = Integer.parseInt(this.textCopies2.getText());
		} catch (Exception e) {
			p = 1;
		}
		return p;
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			// �Ƿ�������ӡ?
			if ("true".equals(config.get("autoprint"))) {
				onOk();
				return;
			}
		}

		Runnable r = new Runnable() {
			public void run() {
				textStartpage.requestFocus();
			};
		};
		SwingUtilities.invokeLater(r);
		super.setVisible(b);
	}

	protected void initDialog() {
		Container cp = getContentPane();
		CFormlayout layout = new CFormlayout(2, 2);
		cp.setLayout(layout);

		Dimension lbsize = new Dimension(80, 27);
		Dimension cbsize = new Dimension(200, 27);

		JLabel lb = null;
		// new JLabel("�� ӡ ��");
		// lb.setPreferredSize(lbsize);
		// cp.add(lb);
		// layout.addLayoutComponent(lb, null);

		CButton btn = new CButton("�� ӡ ��");
		btn.setActionCommand("setupprinter");
		btn.addActionListener(this);
		btn.setName("btnPrinter");
		cp.add(btn);
		layout.addLayoutComponent(btn, null);

		if (printServices == null) {
			printServices = PrintServiceLookup.lookupPrintServices(null, null);
		}
		String[] printernames = new String[printServices.length];
		for (int i = 0; i < printServices.length; i++) {
			printernames[i] = printServices[i].getName();
		}
		cbPrinter = new CComboBox(printernames);
		cbPrinter.setName("cbPrinter");
		cbPrinter.setPreferredSize(cbsize);
		cp.add(cbPrinter);
		layout.addLayoutComponent(cbPrinter, new CFormlineBreak());
		String printername = config.get("printername");
		if (printername != null && printername.length() > 0) {
			for (int i = 0; i < printernames.length; i++) {
				if (printername.equals(printernames[i])) {
					cbPrinter.setSelectedIndex(i);
					break;
				}
			}
		}

		// ////////////// ֽ��/////////////////
		lb = new JLabel("ֽ    ��");
		lb.setName("lbpaper");
		lb.setPreferredSize(lbsize);
		cp.add(lb);
		layout.addLayoutComponent(lb, null);

		cbPapersize = new CComboBox(paperinfos);
		cbPapersize.setName("cbPapersize");
		cbPapersize.setPreferredSize(cbsize);
		cp.add(cbPapersize);
		layout.addLayoutComponent(cbPapersize, new CFormlineBreak());
		cbPapersize.addItemListener(new PaperItemListener());

		// //////��//////// ��/////////////
		lb = new JLabel("ֽ�Ŵ�С");
		lb.setName("lbpapersize");
		lb.setPreferredSize(lbsize);
		cp.add(lb);
		layout.addLayoutComponent(lb, null);

		textPaperwidth = new CNumberTextField(1);
		textPaperwidth.setName("textPaperwidth");
		textPaperwidth.addKeyListener(new KeyListen());
		textPaperwidth.setPreferredSize(new Dimension(40, 27));
		cp.add(textPaperwidth);
		layout.addLayoutComponent(textPaperwidth, null);

		lb = new JLabel("  ");
		lb.setName("lb1");
		cp.add(lb);
		layout.addLayoutComponent(lb, null);

		textPaperheight = new CNumberTextField(1);
		textPaperheight.setName("textPaperheight");
		textPaperheight.addKeyListener(new KeyListen());
		textPaperheight.setPreferredSize(new Dimension(40, 27));
		cp.add(textPaperheight);
		layout.addLayoutComponent(textPaperheight, new CFormlineBreak());

		// ////////���ô�С//////////////////
		// ����ѡ��,�ȴ����仯
		cbPapersize.setSelectedIndex(1);
		cbPapersize.setName("cbPapersize");
		cbPapersize.setSelectedIndex(0);
		String papername = config.get("papername");
		if (papername != null && papername.length() > 0) {
			for (int i = 0; i < paperinfos.size(); i++) {
				if (paperinfos.elementAt(i).name.equals(papername)) {
					cbPapersize.setSelectedIndex(i);
					break;
				}
			}
		}

		// /////////ҳ��Χ////////////////////////
		lb = new JLabel("ҳ�뷶Χ");
		lb.setName("lbpaperpages");
		lb.setPreferredSize(lbsize);
		cp.add(lb);
		layout.addLayoutComponent(lb, null);

		textStartpage = new CNumberTextField(0);
		textStartpage.setName("textStartpage");
		textStartpage.addKeyListener(new KeyListen());
		textStartpage.setPreferredSize(new Dimension(40, 27));
		cp.add(textStartpage);
		layout.addLayoutComponent(textStartpage, null);
		textStartpage.setText("1");

		lb = new JLabel("  ");
		lb.setName("lb2");
		cp.add(lb);
		layout.addLayoutComponent(lb, null);

		textEndpage = new CNumberTextField(0);
		textEndpage.setName("textEndpage");
		textEndpage.addKeyListener(new KeyListen());
		textEndpage.setPreferredSize(new Dimension(40, 27));
		cp.add(textEndpage);
		layout.addLayoutComponent(textEndpage, new CFormlineBreak());
		textEndpage.setText(String.valueOf(maxpagecount));
		if (pagecount > 0) {
			textEndpage.setText(String.valueOf(pagecount));
		}

		// /////////����////////////////////////
		// lb = new JLabel("��ӡ����");
		// lb.setPreferredSize(lbsize);
		jrAllcopys.setPreferredSize(lbsize);
		jrAllcopys.setName("jrAllcopys");
		cp.add(jrAllcopys);
		layout.addLayoutComponent(jrAllcopys, null);

		textCopies = new CNumberTextField(0);
		textCopies.setName("textCopies");
		textCopies.addKeyListener(new KeyListen());
		textCopies.setPreferredSize(new Dimension(40, 27));
		cp.add(textCopies);
		layout.addLayoutComponent(textCopies, new CFormlineBreak());
		textCopies.setText("1");

		// /�ӵڼ��ݵ��ڼ���
		jrStartcopy.setPreferredSize(lbsize);
		jrStartcopy.setName("jrStartcopy");
		cp.add(jrStartcopy);
		layout.addLayoutComponent(jrStartcopy, null);

		textCopies1 = new CNumberTextField(0);
		textCopies1.setName("textCopies1");
		textCopies.addKeyListener(new KeyListen());
		textCopies1.setPreferredSize(new Dimension(40, 27));
		cp.add(textCopies1);
		layout.addLayoutComponent(textCopies1, null);
		textCopies1.setText("1");

		textCopies2 = new CNumberTextField(0);
		textCopies2.setName("textCopies2");
		textCopies2.addKeyListener(new KeyListen());
		textCopies2.setPreferredSize(new Dimension(40, 27));
		cp.add(textCopies2);
		layout.addLayoutComponent(textCopies2, new CFormlineBreak());
		textCopies2.setText("1");

		ButtonGroup btngroup = new ButtonGroup();
		btngroup.add(jrAllcopys);
		btngroup.add(jrStartcopy);
		jrAllcopys.setSelected(true);

		// ////�Զ���ӡ
		if ("true".equals(config.get("autoprint"))) {
			cbAuto.setSelected(true);
		}
		cp.add(cbAuto);
		cbAuto.setName("cbAuto");
		layout.addLayoutComponent(cbAuto, new CFormlineBreak());

		// /////ȷ�� ȡ��//////////////////
		JPanel jpbottom = new JPanel();
		jpbottom.setPreferredSize(new Dimension(300, 30));
		JButton btnok = new JButton("ȷ��");
		btnok.setActionCommand("ok");
		btnok.setName("btnok");
		btnok.addActionListener(this);
		jpbottom.add(btnok);

		JButton btncancel = new JButton("ȡ��");
		btncancel.setActionCommand("cancel");
		btncancel.setName("btncancel");
		btncancel.addActionListener(this);
		jpbottom.add(btncancel);

		if (DefaultNPParam.develop == 1) {
			JButton btndesign = createUIDesignbutton();
			jpbottom.add(btndesign);
		}

		cp.add(jpbottom);
		layout.addLayoutComponent(jpbottom, new CFormlineBreak());
	}

	class PaperItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			Paperinfo paperinfo = (Paperinfo) e.getItem();
			if (paperinfo == null)
				return;
			textPaperwidth.setText(String.valueOf(paperinfo.width));
			textPaperheight.setText(String.valueOf(paperinfo.height));
			boolean enable = paperinfo.name.startsWith("�Զ���");
			textPaperwidth.setEditable(enable);
			textPaperheight.setEditable(enable);
		}

	}

	public static void main(String[] argv) {
		PrintsetupDialog dlg = new PrintsetupDialog(null, null, "print", 5);
		dlg.pack();
		dlg.setVisible(true);
	}

	class Paperinfo {
		public Paperinfo(String name, double w, double h) {
			this.name = name;
			this.width = w;
			this.height = h;
		}

		String name;
		double width;
		double height;

		@Override
		public String toString() {
			return name;
		}

	}

	void loadInitpaper() {
		Paperinfo pinfo = new Paperinfo("�Զ���", 210, 297);
		double w = 0;
		try {
			w = Double.parseDouble(config.get("paper.width"));
		} catch (Exception e) {
			w = 210;
		}
		double h = 0;
		try {
			h = Double.parseDouble(config.get("paper.height"));
		} catch (Exception e) {
			h = 297;
		}
		pinfo.width = w;
		pinfo.height = h;
		paperinfos.add(pinfo);

		paperinfos.add(new Paperinfo("A0", 841, 1189));
		paperinfos.add(new Paperinfo("A1", 594, 841));
		paperinfos.add(new Paperinfo("A2", 420, 594));
		paperinfos.add(new Paperinfo("A3", 297, 420));
		paperinfos.add(new Paperinfo("A4", 210, 297));
		paperinfos.add(new Paperinfo("A5", 148, 210));
		paperinfos.add(new Paperinfo("B5", 176, 250));

	}

	void onOk() {
		saveConfig();
		ok = true;
		dispose();
	}

	void onCancel() {
		ok = false;
		dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok")) {
			onOk();
		} else if (e.getActionCommand().equals("cancel")) {
			onCancel();
		} else if (e.getActionCommand().equals("setupprinter")) {
			if (job == null) {
				job = PrinterJob.getPrinterJob();
			}
			String printername = (String) cbPrinter.getSelectedItem();
			for (int i = 0; i < printServices.length; i++) {
				if (printServices[i].getName().equals(printername)) {
					try {
						job.setPrintService(printServices[i]);
					} catch (PrinterException e1) {
					}
					break;
				}
			}

			job.printDialog();
		} else {
			super.actionPerformed(e);
		}
	}

	protected void saveConfig() {
		// ���ô�ӡ����
		String printername = (String) cbPrinter.getSelectedItem();
		if (printername == null)
			printername = "";
		config.put("printername", printername);

		// ֽ
		Paperinfo pinfo = (Paperinfo) cbPapersize.getSelectedItem();
		config.put("papername", pinfo.name);

		// ��С
		if (pinfo.name.startsWith("�Զ���")) {
			config.put("paper.width", String.valueOf(this.textPaperwidth
					.getText()));
			config.put("paper.height", String.valueOf(this.textPaperheight
					.getText()));
		}

		config.put("autoprint", cbAuto.isSelected() ? "true" : "false");
		config.saveConfigfile();

		/*
		 * //ҳ��
		 * config.put("startpage",String.valueOf(this.textStartpage.getText()));
		 * config.put("endpage",String.valueOf(this.textEndpage.getText()));
		 * 
		 * //�� config.put("copies",String.valueOf(this.textCopies.getText()));
		 */
	}

	void setHotkey() {
		KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		KeyStroke vkesc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		JComponent jcp = (JComponent) getContentPane();
		InputMap im = jcp
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		im.put(vkenter, "ok");
		jcp.getActionMap().put("ok", new DlgAction("ok"));

		im.put(vkesc, "cancel");
		jcp.getActionMap().put("cancel", new DlgAction("cancel"));

	}

	class DlgAction extends AbstractAction {
		DlgAction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("ok")) {
				onOk();
			} else if (e.getActionCommand().equals("cancel")) {
				onCancel();
			}
		}
	}

	class KeyListen implements KeyListener {

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 0XA) {
				onOk();
			} else if (e.getKeyCode() == 27) {
				onCancel();
			}
		}

		public void keyReleased(KeyEvent e) {

		}

		public void keyTyped(KeyEvent e) {

		}

	}

	Vector<Paperinfo> paperinfos = new Vector<Paperinfo>();
	private CComboBox cbPapersize;
	private CNumberTextField textPaperwidth;
	private CNumberTextField textPaperheight;
	private CNumberTextField textStartpage;
	private CNumberTextField textEndpage;
	private CNumberTextField textCopies;
	private CNumberTextField textCopies1;
	private CNumberTextField textCopies2;

	private CComboBox cbPrinter;
	private static PrintService[] printServices;
	private JCheckBox cbAuto = new JCheckBox("�Ժ󰴴˲�����ӡ,��Ҫ�ٵ����˶Ի���");

	public int getPagecount() {
		return pagecount;
	}

	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
		textEndpage.setText(String.valueOf(pagecount));
	}
}
