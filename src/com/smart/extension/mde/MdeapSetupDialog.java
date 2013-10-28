package com.smart.extension.mde;

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

import com.smart.extension.ste.Apinfo;
import com.smart.extension.ste.EditcolApDlg;
import com.smart.extension.ste.HidecolApDlg;
import com.smart.extension.ste.ParamapSetupPanel;
import com.smart.extension.ste.PrintplanlistDlg;
import com.smart.platform.gui.control.CCheckBox;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.DefaultNPParam;

public class MdeapSetupDialog extends CDialog {

	ApdtlIF apif = null;
	private JTextArea textquerycond;
	String roleid;
	private DBTableModel dmhide;
	private DBTableModel dmeditable;
	private DBTableModel dtldmhide;
	private DBTableModel dtldmeditable;

	public MdeapSetupDialog(Frame owner, String title, ApdtlIF apif)
			throws HeadlessException {
		super(owner, title, true);
		this.apif = apif;
		initdialog();
		this.setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
		this.localCenter();
	}

	protected void initdialog() {
		createEditablepane();
		createHidepane();
		createDtlEditablepane();
		createDtlHidepane();

		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());
		
		cp.add(createProppane());
		


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
		JButton btn;
		
		btn = new JButton("��ֹ�༭");
		btn.setActionCommand("forbidedit");
		btn.setName("btnforbidedit");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("������");
		btn.setActionCommand("hidecol");
		btn.setName("btnhidecol");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("ϸ����ֹ�༭");
		btn.setActionCommand("dtlforbidedit");
		btn.setName("btndtlforbidedit");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("ϸ��������");
		btn.setActionCommand("dtlhidecol");
		btn.setName("btndtlhidecol");
		btn.addActionListener(this);
		jp.add(btn);

		btn = new JButton("ȷ��");
		btn.setActionCommand("ok");
		btn.addActionListener(this);
		btn.setName("btnok");
		jp.add(btn);

		btn = new JButton("ȡ��");
		btn.setActionCommand("cancel");
		btn.addActionListener(this);
		jp.add(btn);
		btn.setName("btncancel");
		
		if(DefaultNPParam.debug==1){
			jp.add(createUIDesignbutton());
		}
		
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

		cbcannew = new CCheckBox("����");
		cbcannew.setName("cbcannew");
		jp.add(cbcannew);
		if (!apif.isDevelopCannew()) {
			cbcannew.setSelected(false);
			cbcannew.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbidnew = apif.getApvalue(Apinfo.APNAME_FORBIDNEW);
			cbcannew.setSelected(!forbidnew.equals("true"));
		}

		cbcandelete = new CCheckBox("ɾ��");
		cbcandelete.setName("cbcandelete");
		jp.add(cbcandelete);
		if (!apif.isDevelopCandelete()) {
			cbcandelete.setSelected(false);
			cbcandelete.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbiddelete = apif.getApvalue(Apinfo.APNAME_FORBIDDELETE);
			cbcandelete.setSelected(!forbiddelete.equals("true"));
		}

		cbcanquery = new CCheckBox("��ѯ");
		cbcanquery.setName("cbcanquery");
		jp.add(cbcanquery);
		if (!apif.isDevelopCanquery()) {
			cbcanquery.setSelected(false);
			cbcanquery.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbidquery = apif.getApvalue(Apinfo.APNAME_FORBIDQUERY);
			cbcanquery.setSelected(!forbidquery.equals("true"));
		}

		cbcanmodify = new CCheckBox("�޸�");
		cbcanmodify.setName("cbcanmodify");
		jp.add(cbcanmodify);
		if (!apif.isDevelopCanquery()) {
			cbcanmodify.setSelected(false);
			cbcanmodify.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbidmodify = apif.getApvalue(Apinfo.APNAME_FORBIDMODIFY);
			cbcanmodify.setSelected(!forbidmodify.equals("true"));
		}

		cbcansave = new CCheckBox("����");
		cbcansave.setName("cbcansave");
		jp.add(cbcansave);
		if (!apif.isDevelopCanquery()) {
			cbcansave.setSelected(false);
			cbcansave.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbidsave = apif.getApvalue(Apinfo.APNAME_FORBIDSAVE);
			cbcansave.setSelected(!forbidsave.equals("true"));
		}
		cbcanexport = new CCheckBox("����");
		cbcanexport.setName("cbcanexport");
		jp.add(cbcanexport);
		// ����Ȩ��������ô�����
		String forbidexport = apif.getApvalue(Apinfo.APNAME_FORBIDEXPORT);
		cbcanexport.setSelected(!forbidexport.equals("true"));

		/*
		 * cbselfonly = new CCheckBox("ֻ�ܸı���"); jp.add(cbselfonly); //
		 * ����Ȩ��������ô����� String modifyselfonly =
		 * apif.getApvalue(Apinfo.APNAME_MODIFYSELFONLY);
		 * cbselfonly.setSelected(modifyselfonly.equals("true"));
		 */

		cbforbidreprint = new CCheckBox("��ֹ�ٴ�ӡ");
		cbforbidreprint.setName("cbforbidreprint");
		jp.add(cbforbidreprint);
		// ����Ȩ��������ô�����
		String forbidreprint = apif.getApvalue(Apinfo.APNAME_FORBIDREPRINT);
		cbforbidreprint.setSelected(forbidreprint.equals("true"));
		// /////////////////
		cbcannewdtl = new CCheckBox("����ϸ��");
		cbcannewdtl.setName("cbcannewdtl");
		jp.add(cbcannewdtl);
		if (!apif.isDevelopCannewdtl()) {
			cbcannewdtl.setSelected(false);
			cbcannewdtl.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbidnew = apif.getApvalue(Apinfo.APNAME_FORBIDNEWDTL);
			cbcannewdtl.setSelected(!forbidnew.equals("true"));
		}

		cbcandeletedtl = new CCheckBox("ɾ��ϸ��");
		cbcandeletedtl.setName("cbcandeletedtl");
		jp.add(cbcandeletedtl);
		if (!apif.isDevelopCandeletedtl()) {
			cbcandeletedtl.setSelected(false);
			cbcandeletedtl.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbiddelete = apif
					.getApvalue(Apinfo.APNAME_FORBIDDELETEDTL);
			cbcandeletedtl.setSelected(!forbiddelete.equals("true"));
		}

		cbcanmodifydtl = new CCheckBox("�޸�ϸ��");
		cbcanmodifydtl.setName("cbcanmodifydtl");
		jp.add(cbcanmodifydtl);
		if (!apif.isDevelopCanquery()) {
			cbcanmodifydtl.setSelected(false);
			cbcanmodifydtl.setEnabled(false);
		} else {
			// ����Ȩ��������ô�����
			String forbidmodify = apif
					.getApvalue(Apinfo.APNAME_FORBIDMODIFYDTL);
			cbcanmodifydtl.setSelected(!forbidmodify.equals("true"));
		}

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
		lb.setName("lbwhere");
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(lb, c);
		jp.add(lb);

		textquerycond = new JTextArea(6, 80);
		c.gridwidth = GridBagConstraints.RELATIVE;
		g.setConstraints(textquerycond, c);
		JScrollPane jspwheres=new JScrollPane(textquerycond);
		jspwheres.setName("jspwheres");
		jp.add(jspwheres);
		String wheres = apif.getApvalue(Apinfo.APNAME_WHERES);
		textquerycond.setText(wheres);

		JPanel buttonpanel = createButtonpanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(buttonpanel, c);
		jp.add(buttonpanel);

		// ///////////////// ���ô�ӡ����
		JButton btn = null;
		btn = new JButton("���õ��ݴ�ӡ����");
		btn.setName("btnprintplan");
		btn.setActionCommand("���õ��ݴ�ӡ����");
		btn.addActionListener(this);
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(btn, c);
		jp.add(btn);

		// //////////////����������Ȩ����
		lb = new JLabel("����������Ȩ����");
		lb.setName("lbparam");
		c.gridwidth = GridBagConstraints.REMAINDER;
		g.setConstraints(lb, c);
		jp.add(lb);

		Vector<Apinfo> paramapinfos = apif.getParamapinfos();
		if (paramapinfos == null) {
			paramapinfos = new Vector<Apinfo>();
		}
		Enumeration<Apinfo> en = paramapinfos.elements();
		while (en.hasMoreElements()) {
			Apinfo apinfo = en.nextElement();
			// ȡֵ
			apinfo.setApvalue(apif.getApvalue(apinfo.getApname()));
		}
		JPanel jpparam = new JPanel();
		paramapsetuppane = new ParamapSetupPanel();
		paramapsetuppane.setup(jpparam, paramapinfos);

		c.gridwidth = GridBagConstraints.REMAINDER;
		
		JScrollPane jspparam=new JScrollPane(jpparam);
		jspparam.setName("jspparam");
		g.setConstraints(jpparam, c);
		jp.add(jspparam);

		return jp;
	}

	protected JPanel createButtonpanel() {
		JPanel jp = new JPanel();
		BoxLayout layout = new BoxLayout(jp, BoxLayout.Y_AXIS);
		jp.setLayout(layout);

		JButton btn = new JButton("��ǰ����");
		btn.setActionCommand("��ǰ����");
		btn.addActionListener(this);
		btn.setName("btndept");
		jp.add(btn);

		btn = new JButton("��ǰ��Ա");
		btn.setActionCommand("��ǰ��Ա");
		btn.addActionListener(this);
		btn.setName("btnemployee");
		jp.add(btn);

		btn = new JButton("��ǰ��ɫ");
		btn.setActionCommand("��ǰ��ɫ");
		btn.addActionListener(this);
		btn.setName("btnrole");
		jp.add(btn);

		return jp;
	}

	private boolean ok = false;
	private CCheckBox cbcannew;
	private CCheckBox cbcandelete;
	private CCheckBox cbcanquery;
	private CCheckBox cbcanmodify;
	private CCheckBox cbcansave;
	private CCheckBox cbcanexport;
	private CCheckBox cbforbidreprint;
	// private CCheckBox cbselfonly;
	private CCheckBox cbcannewdtl;
	private CCheckBox cbcandeletedtl;
	private CCheckBox cbcanmodifydtl;
	private ParamapSetupPanel paramapsetuppane;

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
			setupPrintplan();
		} else if (command.equals("forbidedit")) {
			EditcolApDlg dlg=new EditcolApDlg(this,apif,dmeditable);
			dlg.pack();
			dlg.setVisible(true);
		} else if (command.equals("hidecol")) {
			HidecolApDlg dlg=new HidecolApDlg(this,apif,dmhide);
			dlg.pack();
			dlg.setVisible(true);
		} else if (command.equals("dtlforbidedit")) {
			EditcolApDlg dlg=new EditcolApDlg(this,apif,dtldmeditable);
			dlg.pack();
			dlg.setVisible(true);
		} else if (command.equals("dtlhidecol")) {
			HidecolApDlg dlg=new HidecolApDlg(this,apif,dtldmhide);
			dlg.pack();
			dlg.setVisible(true);
		}else{
			super.actionPerformed(e);
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

		if (!cbcannew.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDNEW, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcandelete.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDDELETE, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanquery.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDQUERY, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanmodify.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDMODIFY, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcansave.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDSAVE, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanexport.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDEXPORT, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
/*		if (cbselfonly.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_MODIFYSELFONLY,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
*/
		if (cbforbidreprint.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDREPRINT,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}

		// ��Ȩ����
		info = new Apinfo(Apinfo.APNAME_WHERES, Apinfo.APTYPE_DATA);
		info.setApvalue(textquerycond.getText());
		infos.add(info);

		// �Զ���ӡ
		info = new Apinfo(Apinfo.APNAME_AUTOPRINTPLAN, Apinfo.APTYPE_PARAM);
		info.setApvalue(apif.getAutoprintplan());
		infos.add(info);

		// ///ϸ��
		if (!cbcannewdtl.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDNEWDTL, Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcandeletedtl.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDDELETEDTL,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}
		if (!cbcanmodifydtl.isSelected()) {
			info = new Apinfo(Apinfo.APNAME_FORBIDMODIFYDTL,
					Apinfo.APTYPE_FORBID);
			info.setApvalue("true");
			infos.add(info);
		}

		// ��ֹ�༭
		for (int r = 0; r < dmeditable.getRowCount() - 1; r++) {
			String forbidedit = dmeditable.getItemValue(r, "forbidedit");
			if (forbidedit.equals("1")) {
				String colname = dmeditable.getItemValue(r, "colname");
				info = new Apinfo("forbidedit_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}
		// ��ֹ��ʾ
		for (int r = 0; r < dmhide.getRowCount() - 1; r++) {
			String hide = dmhide.getItemValue(r, "hide");
			if (hide.equals("1")) {
				String colname = dmhide.getItemValue(r, "colname");
				info = new Apinfo("hide_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}

		// ��ֹ�༭
		for (int r = 0; r < dtldmeditable.getRowCount() - 1; r++) {
			String forbidedit = dtldmeditable.getItemValue(r, "forbidedit");
			if (forbidedit.equals("1")) {
				String colname = dtldmeditable.getItemValue(r, "colname");
				info = new Apinfo("dtlforbidedit_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}
		// ��ֹ��ʾ
		for (int r = 0; r < dtldmhide.getRowCount() - 1; r++) {
			String hide = dtldmhide.getItemValue(r, "hide");
			if (hide.equals("1")) {
				String colname = dtldmhide.getItemValue(r, "colname");
				info = new Apinfo("dtlhide_" + colname.toLowerCase(),
						Apinfo.APTYPE_PARAM);
				info.setApvalue("true");
				infos.add(info);
			}
		}

		infos.addAll(paramapsetuppane.getApinfos());

		return infos;
	}

	/**
	 * �������д�ӡ�������ƣ��ٸ���ר���е��ļ�����ѡ��
	 */
	void setupPrintplan() {
		PrintplanlistDlg dlg = new PrintplanlistDlg(this, apif);
		dlg.pack();
		dlg.setVisible(true);
	}

	DBTableModel createColdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "����");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("title", "varchar", "������");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("forbidedit", "varchar", "��ֹ�༭");
		col.setReadonly(false);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);
		col.setTablecolumnwidth(100);

		return new DBTableModel(cols);
	}

	DBTableModel createHideColdm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "����");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("title", "varchar", "������");
		col.setReadonly(true);
		cols.add(col);
		col.setTablecolumnwidth(100);

		col = new DBColumnDisplayInfo("hide", "varchar", "����");
		col.setReadonly(false);
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_CHECKBOX);
		cols.add(col);
		col.setTablecolumnwidth(100);

		return new DBTableModel(cols);
	}

	/**
	 * ���� �Ƿ�ɱ༭
	 * 
	 * @return
	 */
	void createEditablepane() {
		dmeditable = createColdm();
		// ������
		Enumeration<DBColumnDisplayInfo> en = apif.getDBColumnDisplayInfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			if (dbcol.isReadonly())
				continue;
			if (dbcol.getColtype().equals("�к�"))
				continue;
			int r = dmeditable.getRowCount();
			dmeditable.appendRow();
			dmeditable.setItemValue(r, "colname", dbcol.getColname());
			dmeditable.setItemValue(r, "title", dbcol.getTitle());

			// �Ƿ��н�ֹ?
			String afvalue = apif.getApvalue("forbidedit_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dmeditable.setItemValue(r, "forbidedit", "1");
			} else {
				dmeditable.setItemValue(r, "forbidedit", "0");
			}
		}
		dmeditable.appendRow();

	}

	void createDtlEditablepane() {
		dtldmeditable = createColdm();
		// ������
		Enumeration<DBColumnDisplayInfo> en = apif.getDtlDBColumnDisplayInfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			if (dbcol.isReadonly())
				continue;
			if (dbcol.getColtype().equals("�к�"))
				continue;
			int r = dtldmeditable.getRowCount();
			dtldmeditable.appendRow();
			dtldmeditable.setItemValue(r, "colname", dbcol.getColname());
			dtldmeditable.setItemValue(r, "title", dbcol.getTitle());

			// �Ƿ��н�ֹ?
			String afvalue = apif.getApvalue("dtlforbidedit_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dtldmeditable.setItemValue(r, "forbidedit", "1");
			} else {
				dtldmeditable.setItemValue(r, "forbidedit", "0");
			}
		}
		dtldmeditable.appendRow();
	}

	/**
	 * ���� �Ƿ�����
	 * 
	 * @return
	 */
	void createHidepane() {
		dmhide = createHideColdm();


		// ������
		Enumeration<DBColumnDisplayInfo> en = apif.loadOrgDBmodeldefine()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			//if (dbcol.isHide())
			//	continue;
			if (dbcol.getColtype().equals("�к�"))
				continue;
			int r = dmhide.getRowCount();
			dmhide.appendRow();
			dmhide.setItemValue(r, "colname", dbcol.getColname());
			dmhide.setItemValue(r, "title", dbcol.getTitle());

			// �Ƿ�������?
			String afvalue = apif.getApvalue("hide_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dmhide.setItemValue(r, "hide", "1");
			} else {
				dmhide.setItemValue(r, "hide", "0");
			}
		}
		dmhide.appendRow();
	}

	void createDtlHidepane() {
		dtldmhide = createHideColdm();
		CEditableTable table = new CEditableTable(dtldmhide);


		// ������
		Enumeration<DBColumnDisplayInfo> en = apif.loadDtlOrgDBColumnDisplayInfos()
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo dbcol = en.nextElement();
			//if (dbcol.isHide())
			//	continue;
			if (dbcol.getColtype().equals("�к�"))
				continue;
			int r = dtldmhide.getRowCount();
			dtldmhide.appendRow();
			dtldmhide.setItemValue(r, "colname", dbcol.getColname());
			dtldmhide.setItemValue(r, "title", dbcol.getTitle());

			// �Ƿ�������?
			String afvalue = apif.getApvalue("dtlhide_"
					+ dbcol.getColname().toLowerCase());
			if (afvalue != null && afvalue.equals("true")) {
				dtldmhide.setItemValue(r, "hide", "1");
			} else {
				dtldmhide.setItemValue(r, "hide", "0");
			}
		}
		dtldmhide.appendRow();
	}

}
