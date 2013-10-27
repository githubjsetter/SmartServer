package com.inca.npbi.client.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.inca.np.gui.control.CCheckBox;
import com.inca.np.gui.control.CDialog;
import com.inca.np.util.DefaultNPParam;
import com.inca.npx.ste.Apinfo;

public class BIReportSetupApDialog extends CDialog {
	ReportApIF reportApIf;
	private JTextArea sqlarea;
	private boolean isOK;
	private CCheckBox chCanExport;
	private CCheckBox chCanPrint;

	public BIReportSetupApDialog(Frame frame, String s, ReportApIF apif)
			throws HeadlessException {
		super(frame, s, true);
		isOK = false;
		reportApIf = apif;
		initdialog();
		setDefaultCloseOperation(2);
		localCenter();
	}

	protected void initdialog() {
		Container container;
		(container = getContentPane()).setLayout(new BorderLayout());
		container.add(createPanels(), "Center");
		JPanel jpanel = createBottomPanel();
		container.add(jpanel, "South");
	}

	final JPanel createPanels() {
		JPanel jpanel;
		(jpanel = new JPanel()).setLayout(new BorderLayout());
		JPanel jpanel1 = createForbidpanel();
		JPanel jpanel2 = createDataconstraintPanel();
		jpanel.add(jpanel1, "North");
		jpanel.add(jpanel2, "Center");
		return jpanel;
	}

	protected JPanel createForbidpanel() {
		JPanel jpanel = new JPanel();
		BoxLayout boxlayout = new BoxLayout(jpanel, 0);
		jpanel.setLayout(boxlayout);
		chCanExport = new CCheckBox("禁止导出");
		chCanExport.setName("cbcannew");
		jpanel.add(chCanExport);

		if (reportApIf.isForbidExport()) {
			chCanExport.setValue("1");
		}

		chCanPrint = new CCheckBox("禁止打印");
		chCanPrint.setName("cbcandelete");
		jpanel.add(chCanPrint);

		if (reportApIf.isForbidPrint()) {
			chCanPrint.setValue("1");
		}

		return jpanel;
	}

	protected JPanel createDataconstraintPanel() {
		JPanel jpanel = new JPanel();
		JLabel jlabel;
		jlabel = new JLabel("查询授权");
		jlabel.setName("lbwhere");
		jpanel.add(jlabel);
		sqlarea = new JTextArea(10, 60);
		JScrollPane jscrollpane;
		jscrollpane = new JScrollPane(sqlarea);
		jscrollpane.setName("textquerycond");
		jpanel.add(jscrollpane);
		String s = reportApIf.getSelectSql();
		sqlarea.setText(s);
		JPanel jpanel1 = createButtonpanel();
		jpanel.add(jpanel1);

		return jpanel;
	}

	protected JPanel createButtonpanel() {
		JPanel jpanel = new JPanel();
		JButton jbutton;
		jbutton = new JButton("当前部门");
		jbutton.setName("btndept");
		jbutton.setActionCommand("当前部门");
		jbutton.addActionListener(this);
		jpanel.add(jbutton);
		jbutton = new JButton("当前人员");
		jbutton.setName("btnemployee");
		jbutton.setActionCommand("当前人员");
		jbutton.addActionListener(this);
		jpanel.add(jbutton);
		jbutton = new JButton("当前角色");
		jbutton.setName("btnrole");
		jbutton.setActionCommand("当前角色");
		jbutton.addActionListener(this);
		jpanel.add(jbutton);
		jbutton = new JButton("核算单元");
		jbutton.setName("btnentryid");
		jbutton.setActionCommand("当前核算单元");
		jbutton.addActionListener(this);
		jpanel.add(jbutton);
		return jpanel;
	}

	protected JPanel createBottomPanel() {
		JPanel jpanel = new JPanel();
		JButton jbutton = null;
		jbutton = new JButton("确定");
		jbutton.setActionCommand("ok");
		jbutton.setName("btnOk");
		jbutton.addActionListener(this);
		jpanel.add(jbutton);
		jbutton = new JButton("取消");
		jbutton.setActionCommand("cancel");
		jbutton.addActionListener(this);
		jbutton.setName("btnCancel");
		jpanel.add(jbutton);
		if (DefaultNPParam.debug == 1)
			jpanel.add(createUIDesignbutton());
		return jpanel;
	}

	protected void onOk() {
		isOK = true;
		dispose();
	}

	protected void onCancel() {
		isOK = false;
		dispose();
	}

	public boolean getOk() {
		return isOK;
	}

	public Vector<Apinfo> getApinfos() {
		Vector<Apinfo> vector = new Vector<Apinfo>();
		Apinfo apinfo = null;
		if (chCanExport.isSelected()) {
			apinfo = new Apinfo(ReportApIF.FORBID_EXPORT, Apinfo.APTYPE_PARAM);
			apinfo.setApvalue("true");
			vector.add(apinfo);
		}
		if (chCanPrint.isSelected()) {
			apinfo = new Apinfo(ReportApIF.FORBID_PRINT, Apinfo.APTYPE_PARAM);
			apinfo.setApvalue("true");
			vector.add(apinfo);
		}

		if (sqlarea.getText() != null && !sqlarea.getText().equals("")) {
			apinfo = new Apinfo(ReportApIF.REPORT_SQL, Apinfo.APTYPE_PARAM);
			apinfo.setApvalue(sqlarea.getText());
		}
		vector.add(apinfo);

		return vector;
	}

	public void actionPerformed(ActionEvent actionevent) {
		String s = actionevent.getActionCommand();
		if (s.equals("ok")) {
			onOk();
			return;
		}
		if (s.equals("cancel")) {
			onCancel();
			return;
		}
		if (s.equals("当前部门")) {
			sqlarea.replaceSelection("{部门ID}");
			return;
		}
		if (s.equals("当前人员")) {
			sqlarea.replaceSelection("{人员ID}");
			return;
		}
		if (s.equals("当前角色")) {
			sqlarea.replaceSelection("{角色ID}");
			return;
		} 
		if (s.equals("当前核算单元")) {
			sqlarea.replaceSelection("{核算单元ID}");
			return;
		} 
		else {
			super.actionPerformed(actionevent);
			return;
		}
	}
}
