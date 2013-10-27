package com.inca.sysmgr.roleopfast;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import com.inca.np.gui.control.CCheckBox;
import com.inca.np.gui.control.CDialog;
import com.inca.np.gui.control.CEditableTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.DefaultNPParam;
import com.inca.npx.ste.ApIF;
import com.inca.npx.ste.Apinfo;
import com.inca.npx.ste.ParamapSetupPanel;
import com.inca.npx.ste.PrintplanlistDlg;
import com.inca.sysmgr.employee.Employee_frame;

public class BatchapDlg extends CDialog {

	private JTextArea textquerycond;
	String roleid;

	public BatchapDlg(Frame owner, String title) throws HeadlessException {
		super(owner, title, true);
		initdialog();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		this.localCenter();
	}

	protected void initdialog() {
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		JTabbedPane tabpane = new JTabbedPane();
		cp.add(tabpane, BorderLayout.CENTER);

		tabpane.add("������Ȩ", createProppane());

		// tabpane.add("��ֹ�༭",createEditablepane());

		// tabpane.add("������",createHidepane());

		JPanel bottompane = createBottomPanel();
		cp.add(bottompane, BorderLayout.SOUTH);
	}

	JPanel createProppane() {
		JPanel jpprop = new JPanel();
		jpprop.setLayout(new BorderLayout());

		JPanel upppane = createForbidpanel();
		JPanel midpane = createDataconstraintPanel();

		jpprop.add(upppane, BorderLayout.NORTH);
		jpprop.add(midpane, BorderLayout.CENTER);
		return jpprop;
	}

	protected JPanel createBottomPanel() {
		JPanel jp = new JPanel();
		JButton btn = new JButton("ȷ��");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("ȡ��");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		jp.add(btn);
		return jp;
	}

	/**
	 * ��ʾ�ܷ���ɾ��Ĵ�
	 * 
	 * @return
	 */
	protected JPanel createForbidpanel() {
		JPanel jp = new JPanel();
		BoxLayout layout = new BoxLayout(jp, BoxLayout.X_AXIS);
		jp.setLayout(layout);

		cbcannew = new CCheckBox("��ֹ����");
		jp.add(cbcannew);
		/*
		 * if (!apif.isDevelopCannew()) { cbcannew.setSelected(false);
		 * cbcannew.setEnabled(false); } else { // ����Ȩ��������ô����� String forbidnew =
		 * apif.getApvalue(Apinfo.APNAME_FORBIDNEW);
		 * cbcannew.setSelected(!forbidnew.equals("true")); }
		 */
		cbcandelete = new CCheckBox("��ֹɾ��");
		/*
		 * jp.add(cbcandelete); if (!apif.isDevelopCandelete()) {
		 * cbcandelete.setSelected(false); cbcandelete.setEnabled(false); } else { //
		 * ����Ȩ��������ô����� String forbiddelete =
		 * apif.getApvalue(Apinfo.APNAME_FORBIDDELETE);
		 * cbcandelete.setSelected(!forbiddelete.equals("true")); }
		 */
		cbcanquery = new CCheckBox("��ֹ��ѯ");
		jp.add(cbcanquery);
		/*
		 * if (!apif.isDevelopCanquery()) { cbcanquery.setSelected(false);
		 * cbcanquery.setEnabled(false); } else { // ����Ȩ��������ô����� String
		 * forbidquery = apif.getApvalue(Apinfo.APNAME_FORBIDQUERY);
		 * cbcanquery.setSelected(!forbidquery.equals("true")); }
		 */
		cbcanmodify = new CCheckBox("��ֹ�޸�");
		jp.add(cbcanmodify);
		/*
		 * if (!apif.isDevelopCanquery()) { cbcanmodify.setSelected(false);
		 * cbcanmodify.setEnabled(false); } else { // ����Ȩ��������ô����� String
		 * forbidmodify = apif.getApvalue(Apinfo.APNAME_FORBIDMODIFY);
		 * cbcanmodify.setSelected(!forbidmodify.equals("true")); }
		 */
		cbcansave = new CCheckBox("��ֹ����");
		jp.add(cbcansave);
		/*
		 * if (!apif.isDevelopCanquery()) { cbcansave.setSelected(false);
		 * cbcansave.setEnabled(false); } else { // ����Ȩ��������ô����� String
		 * forbidsave = apif.getApvalue(Apinfo.APNAME_FORBIDSAVE);
		 * cbcansave.setSelected(!forbidsave.equals("true")); }
		 */
		cbcanexport = new CCheckBox("��ֹ����");
		jp.add(cbcanexport);
		// ����Ȩ��������ô�����
		/*
		 * String forbidexport = apif.getApvalue(Apinfo.APNAME_FORBIDEXPORT);
		 * cbcanexport.setSelected(!forbidexport.equals("true"));
		 */
		/*
		 * cbselfonly = new CCheckBox("ֻ�ܸı���"); jp.add(cbselfonly); //
		 * ����Ȩ��������ô����� String modifyselfonly =
		 * apif.getApvalue(Apinfo.APNAME_MODIFYSELFONLY);
		 * cbselfonly.setSelected(modifyselfonly.equals("true"));
		 */
		cbforbidreprint = new CCheckBox("��ֹ�ٴ�ӡ");
		jp.add(cbforbidreprint);
		// ����Ȩ��������ô�����
		// String forbidreprint = apif.getApvalue(Apinfo.APNAME_FORBIDREPRINT);
		// cbforbidreprint.setSelected(forbidreprint.equals("true"));

		return jp;
	}

	/**
	 * ��������Լ��Panel
	 * 
	 * @return
	 */
	protected JPanel createDataconstraintPanel() {
		JPanel jp = new JPanel();
		GridBagLayout g = new GridBagLayout();
		jp.setLayout(g);

		GridBagConstraints c = new GridBagConstraints();
		JLabel lb = new JLabel("��ѯԼ��(where����)");
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(lb, c);
		jp.add(lb);

		textquerycond = new JTextArea(6, 50);
		c.gridwidth = GridBagConstraints.RELATIVE;
		g.setConstraints(textquerycond, c);
		jp.add(new JScrollPane(textquerycond));
		// String wheres = apif.getApvalue(Apinfo.APNAME_WHERES);
		// textquerycond.setText(wheres);

		JPanel buttonpanel = createButtonpanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(buttonpanel, c);
		jp.add(buttonpanel);

		// ///////////////// ���ô�ӡ����
		/*
		 * JButton btn=null; btn=new JButton("���õ��ݴ�ӡ����");
		 * btn.setActionCommand("���õ��ݴ�ӡ����"); btn.addActionListener(this);
		 * c.gridwidth = GridBagConstraints.REMAINDER; g.setConstraints(btn, c);
		 * jp.add(btn);
		 */

		/*
		 * // //////////////����������Ȩ���� lb = new JLabel("����������Ȩ����"); c.gridwidth =
		 * GridBagConstraints.REMAINDER; g.setConstraints(lb, c); jp.add(lb);
		 * 
		 * Vector<Apinfo> paramapinfos = apif.getParamapinfos(); if
		 * (paramapinfos == null) { paramapinfos = new Vector<Apinfo>(); }
		 * Enumeration<Apinfo> en = paramapinfos.elements(); while
		 * (en.hasMoreElements()) { Apinfo apinfo = en.nextElement(); // ȡֵ
		 * apinfo.setApvalue(apif.getApvalue(apinfo.getApname())); } JPanel
		 * jpparam = new JPanel(); paramapsetuppane = new ParamapSetupPanel();
		 * paramapsetuppane.setup(jpparam, paramapinfos);
		 * 
		 * c.gridwidth = GridBagConstraints.REMAINDER; g.setConstraints(jpparam,
		 * c); jp.add(jpparam);
		 */
		return jp;
	}

	protected JPanel createButtonpanel() {
		JPanel jp = new JPanel();
		BoxLayout layout = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(layout);

		JButton btn = new JButton("��ǰ����");
		btn.setActionCommand("��ǰ����");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("��ǰ��Ա");
		btn.setActionCommand("��ǰ��Ա");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("��ǰ��ɫ");
		btn.setActionCommand("��ǰ��ɫ");
		btn.addActionListener(this);
		jp.add(btn);

		return jp;
	}

	private boolean ok = false;
	private CCheckBox cbcannew;
	private CCheckBox cbcandelete;
	private CCheckBox cbcanquery;
	private CCheckBox cbcanmodify;
	private CCheckBox cbcansave;
	private CCheckBox cbselfonly;
	private CCheckBox cbcanexport;
	private CCheckBox cbforbidreprint;
	private ParamapSetupPanel paramapsetuppane;
	private DBTableModel dmhide;
	private DBTableModel dmeditable;

	protected void onOk() {
		ok = true;
		dispose();
	}

	protected void onCancel() {
		ok = false;
		dispose();
	}

	public boolean getOk() {
		return ok;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("ok")) {
			onOk();
		} else if (command.equals("cancel")) {
			onCancel();
		} else if (command.equals("��ǰ����")) {
			textquerycond.replaceSelection("<��ǰ����ID>");
		} else if (command.equals("��ǰ��Ա")) {
			textquerycond.replaceSelection("<��ǰ��ԱID>");
		} else if (command.equals("��ǰ��ɫ")) {
			textquerycond.replaceSelection("<��ǰ��ɫID>");
		} else if (command.equals("���õ��ݴ�ӡ����")) {
			// setupPrintplan();
		}
	}

	/**
	 * ������Ȩ��������
	 * 
	 * @return
	 */

	public Vector<Apinfo> getApinfos() {
		Vector<Apinfo> infos = new Vector<Apinfo>();
		Apinfo info = null;

		if (cbcannew.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDNEW, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (cbcandelete.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDDELETE, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (cbcanquery.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDQUERY, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (cbcanmodify.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDMODIFY, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (cbcansave.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDSAVE, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (cbcanexport.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDEXPORT, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		/*
		 * if (cbselfonly.isSelected()) { info = new
		 * Apinfo(Apinfo.APNAME_MODIFYSELFONLY, Apinfo.APTYPE_FORBID);
		 * info.setApvalue("true"); infos.add(info); }
		 */
		if (cbforbidreprint.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDREPRINT, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}

		// ��Ȩ����
		if (textquerycond.getText().length() > 0) {
			info = new Apinfo(Apinfo.APNAME_WHERES, Apinfo.APTYPE_DATA);
			info.setApvalue(textquerycond.getText());
			infos.add(info);
		}

		// �Զ���ӡ
		// info = new Apinfo(Apinfo.APNAME_AUTOPRINTPLAN, Apinfo.APTYPE_PARAM);
		// info.setApvalue(apif.getAutoprintplan());
		// infos.add(info);

		// ��ֹ�༭
		/*
		 * for(int r=0;r<dmeditable.getRowCount()-1;r++){ String
		 * forbidedit=dmeditable.getItemValue(r, "forbidedit");
		 * if(forbidedit.equals("1")){ String colname=dmeditable.getItemValue(r,
		 * "colname"); info = new Apinfo("forbidedit_"+colname.toLowerCase(),
		 * Apinfo.APTYPE_PARAM); info.setApvalue("true"); infos.add(info); } }
		 */// ��ֹ��ʾ
		/*
		 * for(int r=0;r<dmhide.getRowCount()-1;r++){ String
		 * hide=dmhide.getItemValue(r, "hide"); if(hide.equals("1")){ String
		 * colname=dmhide.getItemValue(r, "colname"); info = new
		 * Apinfo("hide_"+colname.toLowerCase(), Apinfo.APTYPE_PARAM);
		 * info.setApvalue("true"); infos.add(info); } }
		 * infos.addAll(paramapsetuppane.getApinfos());
		 */
		return infos;
	}

	/**
	 * �������д�ӡ�������ƣ��ٸ���ר���е��ļ�����ѡ�� void setupPrintplan(){ PrintplanlistDlg dlg=new
	 * PrintplanlistDlg(this,apif); dlg.pack(); dlg.setVisible(true); }
	 */

	DBTableModel createColdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "����");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "������");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("forbidedit", "varchar", "��ֹ�༭");
		col.setReadonly(false);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);

		return new DBTableModel(cols);
	}

	DBTableModel createHideColdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "����");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "������");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("hide", "varchar", "����");
		col.setReadonly(false);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);

		return new DBTableModel(cols);
	}

	/**
	 * ���� �Ƿ�ɱ༭
	 * 
	 * @return JPanel createEditablepane(){ JPanel jp=new JPanel(); dmeditable =
	 *         createColdm(); CEditableTable table=new
	 *         CEditableTable(dmeditable);
	 * 
	 * jp.setLayout(new BorderLayout()); jp.add(new
	 * JScrollPane(table),BorderLayout.CENTER);
	 * 
	 * 
	 * //������ Enumeration<DBColumnDisplayInfo>en=apif.getDBColumnDisplayInfos().elements();
	 * while(en.hasMoreElements()){ DBColumnDisplayInfo dbcol=en.nextElement();
	 * if(dbcol.isReadonly())continue;
	 * if(dbcol.getColtype().equals("�к�"))continue; int
	 * r=dmeditable.getRowCount(); dmeditable.appendRow();
	 * dmeditable.setItemValue(r, "colname", dbcol.getColname());
	 * dmeditable.setItemValue(r, "title", dbcol.getTitle());
	 * 
	 * //�Ƿ��н�ֹ? String
	 * afvalue=apif.getApvalue("forbidedit_"+dbcol.getColname().toLowerCase());
	 * if(afvalue!=null && afvalue.equals("true")){ dmeditable.setItemValue(r,
	 * "forbidedit","1"); }else{ dmeditable.setItemValue(r, "forbidedit","0"); } }
	 * dmeditable.appendRow();
	 * 
	 * return jp; }
	 */

	/**
	 * ���� �Ƿ�����
	 * 
	 * @return JPanel createHidepane(){ JPanel jp=new JPanel(); dmhide
	 *         =createHideColdm(); CEditableTable table=new
	 *         CEditableTable(dmhide);
	 * 
	 * jp.setLayout(new BorderLayout()); jp.add(new
	 * JScrollPane(table),BorderLayout.CENTER);
	 * 
	 * 
	 * //������ Enumeration<DBColumnDisplayInfo>en=apif.getDBColumnDisplayInfos().elements();
	 * while(en.hasMoreElements()){ DBColumnDisplayInfo dbcol=en.nextElement();
	 * if(dbcol.isHide())continue; if(dbcol.getColtype().equals("�к�"))continue;
	 * int r=dmhide.getRowCount(); dmhide.appendRow(); dmhide.setItemValue(r,
	 * "colname", dbcol.getColname()); dmhide.setItemValue(r, "title",
	 * dbcol.getTitle());
	 * 
	 * //�Ƿ�������? String
	 * afvalue=apif.getApvalue("hide_"+dbcol.getColname().toLowerCase());
	 * if(afvalue!=null && afvalue.equals("true")){ dmhide.setItemValue(r,
	 * "hide","1"); }else{ dmhide.setItemValue(r, "hide","0"); } }
	 * dmhide.appendRow();
	 * 
	 * return jp; }
	 */

	public static void main(String[] args) {
		DefaultNPParam.debug = 1;
		DefaultNPParam.develop = 1;

		DefaultNPParam.debugdbip = "192.9.200.47";
		DefaultNPParam.debugdbpasswd = "npserver";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "npserver";
		DefaultNPParam.prodcontext = "npserver";

		Employee_frame frm = new Employee_frame();
		frm.setOpid("2");
		frm.pack();
		frm.setVisible(true);
		frm.setupAp("0");
	}
}
