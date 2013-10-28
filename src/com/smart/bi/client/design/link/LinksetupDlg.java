package com.smart.bi.client.design.link;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;

import com.smart.bi.client.design.BIReportFrame;
import com.smart.bi.client.design.BIReportdsDefine;
import com.smart.bi.client.design.param.BIReportparamdefine;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CDialogOkcancel;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.Sumdbmodel;
import com.smart.platform.gui.mde.MdeFrame;
import com.smart.platform.gui.runop.Oplauncher;
import com.smart.platform.gui.ste.COpframe;
import com.smart.platform.gui.ste.Querycond;
import com.smart.platform.gui.ste.Querycondline;
import com.smart.platform.gui.ste.Relateophov;
import com.smart.platform.gui.ste.Steframe;

/**
 * 链接定义
 * 
 * @author user
 * 
 */
public class LinksetupDlg extends CDialogOkcancel {

	private JTextField textLinkname;
	private JTextField textLinkopid;
	DBTableModel conddm = null;
	DBTableModel dbmodel = null;
	private JTextField textLinkopname;
	Vector<BIReportparamdefine> params;
	private CEditableTable condtable;

	public LinksetupDlg(Frame owner, DBTableModel dbmodel,
			Vector<BIReportparamdefine> params) throws HeadlessException {
		super(owner, "定义链接", true);
		this.dbmodel = dbmodel;
		this.params = params;
		init();
		localCenter();
		setDefaultCloseOperation(CDialogOkcancel.DISPOSE_ON_CLOSE);
	}

	void init() {
		Container cp = getContentPane();
		GridBagLayout g = new GridBagLayout();
		cp.setLayout(g);

		int liney = 0;
		JLabel lb = new JLabel("链接名称");
		lb.setName("lbname");
		cp.add(lb, new GridBagConstraints(0, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		Dimension compsize = new Dimension(340, 27);
		textLinkname = new JTextField();
		textLinkname.setName("linkname");
		textLinkname.setPreferredSize(compsize);
		textLinkname.setMinimumSize(compsize);
		cp.add(textLinkname, new GridBagConstraints(1, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		liney++;
		lb = new JLabel("调用功能ID");
		lb.setName("lbopid");
		cp.add(lb, new GridBagConstraints(0, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		textLinkopid = new JTextField();
		textLinkopid.setName("textLinkopid");
		textLinkopid.setEditable(false);
		textLinkopid.setPreferredSize(compsize);
		cp.add(textLinkopid, new GridBagConstraints(1, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		JButton btn = new JButton("选择功能");
		btn.setActionCommand("selectop");
		btn.setName("btnselectop");
		btn.addActionListener(this);
		cp.add(btn, new GridBagConstraints(2, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		liney++;
		lb = new JLabel("调用功能名称");
		lb.setName("lbopname");
		cp.add(lb, new GridBagConstraints(0, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		textLinkopname = new JTextField();
		textLinkopname.setName("textLinkopname");
		textLinkopname.setEditable(false);
		textLinkopname.setPreferredSize(compsize);
		cp.add(textLinkopname, new GridBagConstraints(1, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		liney++;
		lb = new JLabel("调用条件");
		lb.setName("lbcond");
		cp.add(lb, new GridBagConstraints(0, liney, 1, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		conddm = createConddm();
		Sumdbmodel sumdm = new Sumdbmodel(conddm, null);
		condtable = new CEditableTable(sumdm);
		JScrollPane jsp = new JScrollPane(condtable);
		jsp.setName("jspcondtable");
		Dimension condsize = new Dimension(340, 240);
		jsp.setPreferredSize(condsize);
		jsp.setMinimumSize(condsize);
		cp.add(jsp, new GridBagConstraints(1, liney, 2, 1, 1, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

		liney++;
		JPanel okpanel = createOkcancelPane();
		Dimension okpanelsize = new Dimension(340, 33);
		okpanel.setPreferredSize(okpanelsize);
		okpanel.setMinimumSize(okpanelsize);
		cp.add(okpanel, new GridBagConstraints(0, liney, 3, 1, 3, 1,
				GridBagConstraints.WEST, 0, new Insets(0, 0, 0, 0), 1, 1));

	}

	void selectOp() {
		Relateophov hov = new Relateophov();
		DBTableModel result = hov.showDialog(this, "选择调用功能", "", "", "");
		if (result == null) {
			return;
		}
		String callopid = result.getItemValue(0, "opid");
		String callopname = result.getItemValue(0, "opname");
		setCallopid(callopid);
		textLinkopname.setText(callopname);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("selectop")) {
			selectOp();
		} else {
			super.actionPerformed(e);
		}
	}

	@Override
	protected void onOk() {
		condtable.stopEdit();

		if (textLinkname.getText().trim().length() == 0) {
			warnMessage("提示", "请输入链接名称");
			return;
		}

		if (textLinkopid.getText().trim().length() == 0) {
			warnMessage("提示", "请选择要调用的报表功能");
			return;
		}

		super.onOk();
	}

	public String getLinkname() {
		return textLinkname.getText().trim();
	}

	public String getCallopid() {
		return textLinkopid.getText();
	}

	public String getCallopname() {
		return textLinkopname.getText();
	}

	public String getCallcond() {
		StringBuffer sb = new StringBuffer();
		for (int r = 0; r < conddm.getRowCount(); r++) {
			String cond = conddm.getItemValue(r, "cond");
			if (cond.length() == 0)
				continue;
			if (cond.equals(" "))
				continue;
			if (cond.equals("　"))
				continue;

			String paramname = conddm.getItemValue(r, "paramname");
			sb.append(paramname + "=" + cond + ":");
		}
		return sb.toString();
	}

	public void setLinkname(String linkname) {
		textLinkname.setText(linkname);
	}

	public void setCallopid(String callopid) {
		textLinkopid.setText(callopid);
		downloadCallop();
	}

	public void setCallopname(String callopname) {
		textLinkopname.setText(callopname);
	}

	public void setCallcond(String cond) {
		String lines[] = cond.split(":");
		for (int i = 0; lines != null && i < lines.length; i++) {
			String line = lines[i];
			int p = line.indexOf("=");
			if (p < 0)
				continue;
			String paramname = line.substring(0, p);
			String value = line.substring(p + 1);
			setCondvalue(paramname, value);
		}
		condtable.tableChanged(new TableModelEvent(condtable.getModel()));
		condtable.autoSize();
	}

	void setCondvalue(String paramname, String value) {
		for (int i = 0; i < conddm.getRowCount(); i++) {
			if (conddm.getItemValue(i, "paramname").equalsIgnoreCase(paramname)) {
				conddm.setItemValue(i, "cond", value);
				return;
			}
		}
	}

	DBTableModel createConddm() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("paramname",
				"varchar", "被调用参数");
		col.setReadonly(true);
		col.setTablecolumnwidth(120);
		cols.add(col);

		col = new DBColumnDisplayInfo("title", "varchar", "被调用参数名");
		col.setReadonly(true);
		col.setTablecolumnwidth(120);
		cols.add(col);

		col = new DBColumnDisplayInfo("cond", "varchar", "本表参数和列");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		cols.add(col);
		col.setTablecolumnwidth(120);
		col.setReadonly(false);
		col.addComboxBoxItem("　", "　");

		Enumeration<BIReportparamdefine> en1 = params.elements();
		while (en1.hasMoreElements()) {
			BIReportparamdefine param = en1.nextElement();
			col.addComboxBoxItem("{" + param.paramname + "}", param.title
					+ "(参数)");
		}

		for (int i = 0; i < dbmodel.getDisplaycolumninfos().size(); i++) {
			DBColumnDisplayInfo colinfo = dbmodel.getDisplaycolumninfos()
					.elementAt(i);
			col.addComboxBoxItem("{" + colinfo.getColname() + "}", colinfo
					.getTitle());
		}

		return new DBTableModel(cols);
	}

	void downloadCallop() {
		conddm.clearAll();
		String sql = "select classname from np_op where opid=" + getCallopid();
		RemotesqlHelper sh = new RemotesqlHelper();
		String classname = "";
		try {
			DBTableModel dm = sh.doSelect(sql, 0, 1);
			if (dm.getRowCount() == 0) {
				return;
			}
			classname = dm.getItemValue(0, "classname");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (classname.equals("bireport")) {
			BIReportFrame f = new BIReportFrame();
			f.setAutoquery(false);
			f.setOpid(getCallopid());
			BIReportdsDefine dsdefine = f.getDsdefine();
			Enumeration<BIReportparamdefine> en = dsdefine.params.elements();
			while (en.hasMoreElements()) {
				BIReportparamdefine param = en.nextElement();
				int r = conddm.getRowCount();
				conddm.appendRow();
				conddm.setItemValue(r, "paramname", param.paramname);
				conddm.setItemValue(r, "title", param.title);
				conddm.setItemValue(r, "cond", "　");
			}
		} else {
			Querycond querycond=null;
			String callopid=getCallopid();
			try {
				COpframe frm = Oplauncher.loadOp(callopid);
				if (frm instanceof Steframe) {
					Steframe stefrm = (Steframe) frm;
					querycond = stefrm.getCreatedStemodel().getCreatedquerycond();
				} else if (frm instanceof MdeFrame) {
					MdeFrame mdefrm = (MdeFrame) frm;
					querycond = mdefrm.getCreatedMdemodel().getMasterModel()
							.getCreatedquerycond();
				} else {
					JOptionPane.showMessageDialog(this, "被调用功能ID"
							+ callopid + "不是能处理的ste和mde类型");
					return;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "下载被调用功能ID"
						+ callopid+ "失败:" + e.getMessage());
				return;
			}
			
			//将querycond中的条件插入
			Enumeration<Querycondline> en=querycond.elements();
			while(en.hasMoreElements()){
				Querycondline ql=en.nextElement();
				int r = conddm.getRowCount();
				conddm.appendRow();
				conddm.setItemValue(r, "paramname", ql.getDbcolumndisplayinfo().getColname());
				conddm.setItemValue(r, "title", ql.getDbcolumndisplayinfo().getTitle());
				conddm.setItemValue(r, "cond", "　");

			}

		}
		condtable.tableChanged(new TableModelEvent(condtable.getModel()));
		condtable.autoSize();

	}
}
