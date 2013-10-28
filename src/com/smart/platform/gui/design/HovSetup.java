package com.smart.platform.gui.design;

import com.smart.client.download.DownloadManager;
import com.smart.platform.demo.communicate.RemotesqlHelper;
import com.smart.platform.gui.control.CDialog;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.HovListener;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Hovdefine;
import com.smart.platform.gui.ste.Zxhovdownloader;
import com.smart.platform.util.DefaultNPParam;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;

import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-28 Time: 17:25:29
 * HOV设置。 从DefaultNPParam中取得所有的HOV描述. 请用户选择hov，hov列和返回列次序
 */
public class HovSetup extends JPanel {
	Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo = null;
	DesignFrame frame = null;
	CSteModel stemodel;

	private CEditableTable table;
	HashMap<String, Hovinfo> hovinfocache = new HashMap<String, Hovinfo>();
	HashMap<String, CHovBase> hovinstcache = new HashMap<String, CHovBase>();

	DBTableModel editmodel = null;

	public HovSetup(DesignFrame frame, CSteModel stemodel) {
		this.frame = frame;
		this.stemodel = stemodel;
		this.formdbcolumndisplayinfo = stemodel.getFormcolumndisplayinfos();
	}

	public void createHovPane(JPanel cp) {
		cp.removeAll();
		cp.setLayout(new BorderLayout());

		editmodel = createEditdbmodel();

		// 建张表
		table = new CEditableTable(editmodel);
		table.setRowHeight(27);
		table.addMouseListener(new MouseHandle());
		JScrollPane sp = new JScrollPane(table);
		bindData(editmodel);
		table.autoSize();
		table.addHovlistener(new Hovhandle());

		cp.add(sp, BorderLayout.CENTER);

		cp.add(new JLabel("在HOV名编辑框输入按回车或按F12键选HOV"), BorderLayout.SOUTH);

	}

	class Hovinfo {
		String hovname, classname, prodname, modulename;
	}

	void bindData(DBTableModel dbmodel) {
		// 找所有的hov

		dbmodel.clearAll();

		Enumeration<DBColumnDisplayInfo> en = formdbcolumndisplayinfo
				.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo colinfo = en.nextElement();
			if (colinfo.getColname().equals("行号")) {
				continue;
			}

			int row = dbmodel.getRowCount();
			dbmodel.appendRow();

			dbmodel.setItemValue(row, "colname", colinfo.getColname());
			dbmodel.setItemValue(row, "title", colinfo.getTitle());

			Hovdefine hovdefine = colinfo.getHovdefine();
			if (hovdefine != null) {
				dbmodel.setItemValue(row, "classname", hovdefine
						.getHovclassname());
				dbmodel.setItemValue(row, "usecontext", hovdefine
						.getUsecontext());
				Hovinfo hovinfo = hovinfocache.get(hovdefine.getHovclassname());
				if (hovinfo != null) {
					dbmodel.setItemValue(row, "hovname", hovinfo.hovname);
					dbmodel.setItemValue(row, "prodname", hovinfo.prodname);
					dbmodel.setItemValue(row, "modulename", hovinfo.modulename);
				} else {
					downloadHovname(dbmodel, row, hovdefine.getHovclassname());
				}
				dbmodel.setItemValue(row, "hovcolmap", hovdefine
						.getColpairString());
			}
		}
		// 加合计
		dbmodel.appendRow();
	}

	private void downloadHovname(DBTableModel dbmodel, int row, String classname) {
		try {
			String sql;
			if (classname.startsWith("hovgeneral_")) {
				String hovname = classname.substring("hovgeneral_".length());
				sql = "select * from np_hov where hovname='" + hovname + "'";
			} else {
				sql = "select * from np_hov where classname='" + classname
						+ "'";
			}
			RemotesqlHelper sqlh = new RemotesqlHelper();
			DBTableModel result = sqlh.doSelect(sql, 0, 1);
			if (result.getRowCount() == 1) {
				dbmodel.setItemValue(row, "hovname", result.getItemValue(0,
						"hovname"));
				dbmodel.setItemValue(row, "prodname", result.getItemValue(0,
						"prodname"));
				dbmodel.setItemValue(row, "modulename", result.getItemValue(0,
						"modulename"));
				Hovinfo hovinfo = new Hovinfo();
				hovinfocache.put(classname, hovinfo);
				hovinfo.hovname = result.getItemValue(0, "hovname");
				hovinfo.prodname = result.getItemValue(0, "prodname");
				hovinfo.modulename = result.getItemValue(0, "modulename");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reversebindData() {
		table.stopEdit();
		
		for (int r = 0; r < editmodel.getRowCount() - 1; r++) {
			String colname = editmodel.getItemValue(r, "colname");
			DBColumnDisplayInfo colinfo = stemodel.getDBtableModel()
					.getColumninfo(colname);
			String hovname = editmodel.getItemValue(r, "hovname");
			String hovcolmap = editmodel.getItemValue(r, "hovcolmap");
			String classname = editmodel.getItemValue(r, "classname");
			String usecontext = editmodel.getItemValue(r, "usecontext");
			if (hovname.length() == 0) {
				colinfo.setHovdefine(null);
			} else {
				if (classname.equals("hovgeneral")) {
					classname = "hovgeneral_" + hovname;
				}

				Hovdefine hovdefine = new Hovdefine(classname, colinfo
						.getColname());
				colinfo.setHovdefine(hovdefine);
				hovdefine.setUsecontext(usecontext);

				String line = editmodel.getItemValue(r, "hovcolmap");
				int p = 0, p1;
				while (true) {
					p = line.indexOf("(", p);
					if (p < 0)
						break;
					p++;
					p1 = line.indexOf(")", p);
					if (p1 < 0)
						break;
					String s = line.substring(p, p1);
					int k = s.indexOf(",");
					String hovcolname = s.substring(0, k);
					String dbcolname = s.substring(k + 1);
					hovdefine.putColpair(hovcolname, dbcolname);
					p = p1;
				}

			}
		}
	}

	class MouseHandle implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mousePressed(MouseEvent e) {
			int row = table.rowAtPoint(e.getPoint());
			if (row < 0 || row >= editmodel.getRowCount() - 1)
				return;
			int col = table.columnAtPoint(e.getPoint());

			TableColumnModel cm = table.getColumnModel();
			TableColumn tc = cm.getColumn(col);
			int modelindex = tc.getModelIndex();
			DBColumnDisplayInfo colinfo = editmodel.getDisplaycolumninfos()
					.elementAt(modelindex);

			if (colinfo.getColname().equals("hovcolmap")) {
				setupHovColumns(row, col);
			}
		}

		public void mouseReleased(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseEntered(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}

		public void mouseExited(MouseEvent e) {
			// To change body of implemented methods use File | Settings | File
			// Templates.
		}
	}

	CHovBase downloadProdhov(String hovclassname, String prodname,
			String modulename) {
		// 实例hov
		try {
			return DownloadManager.getInst().downloadProdhov(hovclassname,
					prodname, modulename);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage());
			return null;
		}
	}

	CHovBase downloadZxhov(String hovname) {
		try {
			return DownloadManager.getInst().downloadZxhov(hovname);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(frame, e1.getMessage());
			return null;
		}
	}

	void setupHovColumns(int row, int col) {
		String hovname = editmodel.getItemValue(row, "hovname");
		String hovclassname = editmodel.getItemValue(row, "classname");
		String modulename = editmodel.getItemValue(row, "modulename");
		String prodname = editmodel.getItemValue(row, "prodname");
		if (hovname.length() == 0) {
			return;
		}

		CHovBase hovinst = hovinstcache.get(hovclassname);
		if (hovinst == null) {
			if (hovclassname.startsWith("hovgeneral_")) {
				String zxhovname = hovclassname.substring("hovgeneral_"
						.length());
				hovinst = this.downloadZxhov(zxhovname);
			} else {
				hovinst = this.downloadProdhov(hovclassname, prodname,
						modulename);
			}
			if (hovinst == null) {
				return;
			}
			hovinstcache.put(hovclassname, hovinst);
		}

		String hovdesc = (String) table.getValueAt(row, 1);
		String hovcols[] = hovinst.getColumns();

		String cols[] = new String[this.formdbcolumndisplayinfo.size()];
		cols[0] = "";
		for (int i = 1; i < cols.length; i++) {
			cols[i] = formdbcolumndisplayinfo.elementAt(i).getColname();
		}

		String curcolpaires = editmodel.getItemValue(row, "hovcolmap");
		if (curcolpaires == null) {
			curcolpaires = "";
		}
		ColDialog dlg = new ColDialog(frame, hovcols, cols, curcolpaires);
		dlg.pack();
		dlg.setVisible(true);

		String colpairs = dlg.getColpairs();
		if (colpairs != null) {
			table.stopEdit();
			editmodel.setItemValue(row, "hovcolmap", colpairs);
			table.tableChanged(new TableModelEvent(editmodel, row));
		}
	}

	class ColDialog extends CDialog implements ActionListener {
		JTable table = null;
		String dbcols[] = null;

		public ColDialog(Frame owner, String hovcols[], String cols[],
				String colpairs) throws HeadlessException {
			super(owner, "定义列关系", true);
			dbcols = cols;

			Container cp = this.getContentPane();
			cp.setLayout(new BorderLayout());

			String coltitles[] = { "Hov列", "数据列" };
			DefaultTableModel model = new DefaultTableModel(new Object[0][0],
					coltitles);
			table = new JTable(model);
			table.setRowHeight(27);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane sp = new JScrollPane(table);
			cp.add(sp, BorderLayout.CENTER);

			Vector data = model.getDataVector();
			for (int i = 0; i < hovcols.length; i++) {
				Vector record = new Vector();
				record.setSize(2);
				record.setElementAt(hovcols[i], 0);
				String target = "(" + hovcols[i] + ",";
				int p = colpairs.indexOf(target);
				if (p >= 0) {
					p += target.length();
					int p1 = colpairs.indexOf(")", p);
					String v = colpairs.substring(p, p1);
					record.setElementAt(v, 1);
				} else {
					record.setElementAt("", 1);
				}
				data.add(record);
			}

			JComboBox cbcols = new JComboBox(cols);
			TableColumnModel cm = table.getColumnModel();
			cm.getColumn(1).setCellEditor(new DefaultCellEditor(cbcols));

			// autoSize(table);

			JPanel bottomPane = new JPanel();
			cp.add(bottomPane, BorderLayout.SOUTH);

			JButton btnok = new JButton("确定");
			btnok.setActionCommand("ok");
			btnok.addActionListener(this);
			bottomPane.add(btnok);

			JButton btauto = new JButton("自动设置");
			btauto.setActionCommand("auto");
			btauto.addActionListener(this);
			bottomPane.add(btauto);

			localScreenCenter();
		}

		private String colpairs = null;

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("ok")) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < table.getRowCount(); i++) {
					sb.append("(");
					sb.append(table.getValueAt(i, 0));
					sb.append(",");
					sb.append(table.getValueAt(i, 1));
					sb.append(")");
					colpairs = sb.toString();
				}
				dispose();
			} else if (e.getActionCommand().equals("auto")) {
				// 自动设置，方法是找名字一样的列
				for (int i = 0; i < table.getRowCount(); i++) {
					String hovcolname = (String) table.getValueAt(i, 0);
					// 找数据列
					for (int j = 0; j < dbcols.length; j++) {
						if (hovcolname.equalsIgnoreCase(dbcols[j])) {
							table.setValueAt(dbcols[j], i, 1);
							break;
						}
					}
				}

			}
		}

		public String getColpairs() {
			return colpairs;
		}
	}

	DBTableModel createEditdbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col;
		col = new DBColumnDisplayInfo("colname", "varchar", "字段名");
		cols.add(col);
		col.setReadonly(true);
		col.setReadonly(true);

		col = new DBColumnDisplayInfo("title", "varchar", "中文名");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("hovname", "varchar", "HOV名称");
		Hovdefine hovdefine = new Hovdefine(
				"com.inca.np.gui.design.SelecthovHov", "hovname");
		hovdefine.putColpair("hovname", "hovname");
		col.setHovdefine(hovdefine);
		cols.add(col);
		
		col = new DBColumnDisplayInfo("usecontext", "varchar", "使用场景");
		col.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
		col.addComboxBoxItem("仅编辑","仅编辑");
		col.addComboxBoxItem("仅查询","仅查询");
		col.addComboxBoxItem("编辑查询","编辑查询");
		cols.add(col);
		
		

		col = new DBColumnDisplayInfo("hovcolmap", "varchar", "对应列关系");
		col.setEnable(false);
		cols.add(col);

		col = new DBColumnDisplayInfo("classname", "varchar", "HOV类");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("prodname", "varchar", "HOV所属产品");
		col.setReadonly(true);
		cols.add(col);

		col = new DBColumnDisplayInfo("modulename", "varchar", "HOV所属模块");
		col.setReadonly(true);
		cols.add(col);

		return new DBTableModel(cols);
	}

	class Hovhandle implements HovListener {

		public void gainFocus(DBColumnDisplayInfo dispinfo) {
			// TODO Auto-generated method stub

		}

		public void lostFocus(DBColumnDisplayInfo dispinfo) {
			// TODO Auto-generated method stub

		}

		public void on_hov(DBColumnDisplayInfo dispinfo, DBTableModel result) {
			int row = table.getRow();
			String hovname = result.getItemValue(0, "hovname");
			editmodel.setItemValue(row, "hovname", hovname);
			String classname = result.getItemValue(0, "classname");
			if(classname.equals("hovgeneral")){
				classname="hovgeneral_"+hovname;
			}
			editmodel.setItemValue(row, "classname", classname);
			editmodel.setItemValue(row, "prodname", result.getItemValue(0,
					"prodname"));
			editmodel.setItemValue(row, "modulename", result.getItemValue(0,
					"modulename"));
			table.tableChanged(new TableModelEvent(editmodel, row));
			setupHovColumns(row, 0);
		}
	}
}
