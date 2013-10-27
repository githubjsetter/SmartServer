package com.inca.npclient.system;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;

import com.inca.np.gui.control.CDialogOkcancel;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.CPlainTextField;
import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.CUpperTextField;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

/**
 * 功能快速调用dlg
 * 
 * @author user
 * 
 */
public class QuickopDlg extends CDialogOkcancel {
	private CUpperTextField textInput;
	DBTableModel opdbmodel = null;
	DBTableModel dbmodel = null;
	CTable table = null;

	public QuickopDlg(CFrame frm) {
		super(frm, "功能快速调用", true);
		opdbmodel = NpopManager.getInst().getOpmodel();
		init();
		bind();
		localCenter();
		setDefaultCloseOperation(CDialogOkcancel.HIDE_ON_CLOSE);
	}

	void init() {
		Container cp = getContentPane();
		cp.add(createMainpane(), BorderLayout.CENTER);
		cp.add(createOkcancelPane(),BorderLayout.SOUTH);
	}

	void bind() {
		dbmodel.clearAll();
		// 过滤
		for (int r = 0; r < opdbmodel.getRowCount(); r++) {
			String opid = opdbmodel.getItemValue(r, "opid");
			String opcode = opdbmodel.getItemValue(r, "opcode");
			String opname = opdbmodel.getItemValue(r, "opname");
			String prodname = opdbmodel.getItemValue(r, "prodname");
			String modulename = opdbmodel.getItemValue(r, "modulename");
			String classname = opdbmodel.getItemValue(r, "classname");
			int newrow = dbmodel.getRowCount();
			dbmodel.appendRow();
			dbmodel.setItemValue(newrow, "opid", opid);
			dbmodel.setItemValue(newrow, "opcode", opcode);
			dbmodel.setItemValue(newrow, "opname", opname);
			dbmodel.setItemValue(newrow, "prodname", prodname);
			dbmodel.setItemValue(newrow, "modulename", modulename);
			dbmodel.setItemValue(newrow, "classname", classname);

		}
		//最后一行
		dbmodel.appendRow();
		table.tableChanged(new TableModelEvent(dbmodel));
		table.autoSize();
		if(dbmodel.getRowCount()>0){
			table.addRowSelectionInterval(0, 0);
		}

	}

	JPanel createMainpane() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());

		JPanel tb = new JPanel();
		tb.setPreferredSize(new Dimension(600, 30));
		tb.add(new JLabel("过滤"));
		textInput = new CUpperTextField();
		textInput.addKeyListener(new Keyhandler());
		textInput.getDocument().addDocumentListener(new Dochandler());

		Dimension compsize = new Dimension(150, 25);
		textInput.setPreferredSize(compsize);
		tb.add(textInput);

		jp.add(tb, BorderLayout.NORTH);

		dbmodel = createDbmodel();
		table = new CTable(dbmodel);
		InputMap tim=table.getInputMap(JComponent.WHEN_FOCUSED);
		tim.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "not need");
		
		tim=table.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		tim.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "not need");

		tim=table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		tim.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "not need");

		table.setReadonly(true);
		table.addMouseListener(new Tablemousehandler());

		JScrollPane jsp = new JScrollPane(table);
		jsp.setPreferredSize(new Dimension(600, 400));
		jp.add(jsp, BorderLayout.CENTER);

		return jp;
	}

	class Dochandler implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			filterOp();
		}

		public void insertUpdate(DocumentEvent e) {
			filterOp();
		}

		public void removeUpdate(DocumentEvent e) {
			filterOp();
		}

	}

	void filterOp() {
		String fs = textInput.getText();
		fs = fs.toUpperCase();
		// System.out.println(fs);
		dbmodel.clearAll();
		// 过滤
		for (int r = 0; r < opdbmodel.getRowCount(); r++) {
			String opid = opdbmodel.getItemValue(r, "opid");
			String opcode = opdbmodel.getItemValue(r, "opcode");
			String opname = opdbmodel.getItemValue(r, "opname");
			if (opid.indexOf(fs) >= 0 || opcode.indexOf(fs) >= 0
					|| opname.indexOf(fs) >= 0) {
				String prodname = opdbmodel.getItemValue(r, "prodname");
				String modulename = opdbmodel.getItemValue(r, "modulename");
				String classname = opdbmodel.getItemValue(r, "classname");
				int newrow = dbmodel.getRowCount();
				dbmodel.appendRow();
				dbmodel.setItemValue(newrow, "opid", opid);
				dbmodel.setItemValue(newrow, "opcode", opcode);
				dbmodel.setItemValue(newrow, "opname", opname);
				dbmodel.setItemValue(newrow, "prodname", prodname);
				dbmodel.setItemValue(newrow, "modulename", modulename);
				dbmodel.setItemValue(newrow, "classname", classname);
			}

		}
		table.tableChanged(new TableModelEvent(dbmodel));
		table.autoSize();
		
		if(dbmodel.getRowCount()>0){
			table.addRowSelectionInterval(0, 0);
		}

	}

	DBTableModel createDbmodel() {
		Vector<DBColumnDisplayInfo> cols = new Vector<DBColumnDisplayInfo>();
		DBColumnDisplayInfo col = new DBColumnDisplayInfo("opid", "number",
				"功能ID");
		cols.add(col);

		col = new DBColumnDisplayInfo("opcode", "varchar", "操作码");
		cols.add(col);
		col = new DBColumnDisplayInfo("opname", "varchar", "功能");
		cols.add(col);
		col = new DBColumnDisplayInfo("prodname", "varchar", "产品");
		cols.add(col);
		col = new DBColumnDisplayInfo("modulename", "varchar", "模块");
		cols.add(col);
		col = new DBColumnDisplayInfo("classname", "varchar", "类");
		cols.add(col);

		return new DBTableModel(cols);
	}
	
	public String getOpid(){
		int r=table.getRow();
		if(r<0||r>dbmodel.getRowCount()-1)return null;
		return dbmodel.getItemValue(r, "opid");
		
	}

	class Keyhandler implements KeyListener{

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 0XA) {
				// 按回车了
				onOk();
			} else if (e.getKeyCode() == 27) {
				// 按ESC
				onCancel();
			}else if(e.getKeyCode() == KeyEvent.VK_UP){
				int r=table.getRow();
				if(r>0){
					r--;
					table.addRowSelectionInterval(r, r);
					table.scrollToCell(r, 1);
				}
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
				int r=table.getRow();
				if(r>=0 && r<dbmodel.getRowCount()-1){
					r++;
					table.addRowSelectionInterval(r, r);
					table.scrollToCell(r, 1);
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class Tablemousehandler implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()>1){
				if(table.getRow()>=0){
					onOk();
					return;
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	@Override
	protected void onOk() {
		int r=table.getRow();
		if(r<0 || r>dbmodel.getRowCount()-1){
			return;
		}
		ok = true;
		this.setVisible(false);
	}
	
	

	@Override
	protected void onCancel() {
		ok = false;
		this.setVisible(false);
	}

	public static void main(String[] args) {
		QuickopDlg dlg = new QuickopDlg(null);
		dlg.pack();
		dlg.setVisible(true);
	}
}
