package com.smart.platform.gui.ste;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.control.CCheckBox;
import com.smart.platform.gui.control.CComboBox;
import com.smart.platform.gui.control.CFieldGroup;
import com.smart.platform.gui.control.CFormatTextField;
import com.smart.platform.gui.control.CFormatTextFieldListener;
import com.smart.platform.gui.control.CFormlayout;
import com.smart.platform.gui.control.CFormlineBreak;
import com.smart.platform.gui.control.CHovBase;
import com.smart.platform.gui.control.CMdeHov;
import com.smart.platform.gui.control.CMdehovEx;
import com.smart.platform.gui.control.CMultiHov;
import com.smart.platform.gui.control.CStehovEx;
import com.smart.platform.gui.control.CTextArea;
import com.smart.platform.gui.control.CTextField;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.gui.control.HovListener;
import com.smart.platform.gui.control.HovcondIF;
import com.smart.platform.gui.panedesign.DPanedesignDlg;
import com.smart.platform.gui.panedesign.DPanel;
import com.smart.platform.util.DefaultNPParam;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-30 Time: 16:44:05
 * ����༭��Ƭ����
 */
public class Steform extends JPanel implements CFormatTextFieldListener,
		HovcondIF {
	public final static String SHOWHOVDIALOG = "showhovdialog";

	protected Vector<DBColumnDisplayInfo> editorcontrols = new Vector<DBColumnDisplayInfo>();
	protected JLabel lbstatus;
	protected String strdbstatus = "";
	protected JComponent activecomp = null;

	protected CSteModel stemodel = null;

	/**
	 * ��Ӧ�ļ�¼������
	 */
	protected int row = 0;
	protected DBTableModel model = null;
	protected JPanel centerform;
	protected ActionListener actionlistener = null;

	/**
	 * ���ڴ�����Դ��������ʱ��Ҫ�����ֶα��޸��¼���
	 */
	protected boolean isbindingvalue = false;
	private CFormlayout formlayout;

	Vector<HovKeyAction> hovkeyactions = new Vector<HovKeyAction>();
	SteformUIManager steformgr=null;

	// private JScrollPane scrollp;

	protected Steform(CSteModel stemodel) {
		super();
		this.editorcontrols = stemodel.getDBtableModel()
				.getDisplaycolumninfos();
		this.actionlistener = stemodel;
		this.stemodel = stemodel;
		steformgr=new SteformUIManager(stemodel);

		// �����ȼ�
		Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo editor = en.nextElement();
			JComponent editcomp = editor.getEditComponent();
			if (editcomp instanceof CFormatTextField) {
				CFormatTextField textfield = (CFormatTextField) editcomp;
				Doclistener dc = new Doclistener(textfield);
				textfield.addDoclistener(dc);
				textfield.setChangelistener(this);
			} else if (editcomp instanceof CTextField) {
				CTextField textfield = (CTextField) editcomp;
				Doclistener dc = new Doclistener(textfield);
				textfield.addDoclistener(dc);
			} else if (editcomp instanceof CComboBox) {
				CComboBox combobox = (CComboBox) editcomp;
				combobox.addItemListener(new CComboBoxListener());
			} else if (editcomp instanceof CCheckBox) {
				CCheckBox cb = (CCheckBox) editcomp;
				cb.addItemListener(new CheckboxListener());
			} else if (editcomp instanceof CTextArea) {
				CTextArea textarea = (CTextArea) editcomp;
				TextareaListener tal = new TextareaListener(editor);
				textarea.addDoclistener(tal);
			}
			if (editor.isUsehov()
					|| editor.getColtype().equalsIgnoreCase("date")
					|| editor.getColname().equalsIgnoreCase("filegroupid")) {
				setEditcompHotkey(editor);
			}
		}

		setLayout(new BorderLayout());

		JPanel statuspane = createStatusPanel();
		this.add(statuspane, BorderLayout.NORTH);

		centerform = createCenterForm();
		/*
		 * scrollp = new JScrollPane(centerform); KeyStroke vkup =
		 * KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false); KeyStroke vkdown =
		 * KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
		 * scrollp.getInputMap
		 * (JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( vkup, "nouse");
		 * scrollp
		 * .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
		 * vkdown, "nouse");
		 */
		// scrollp.addMouseListener(new FormMouselistener());
		this.add(centerform, BorderLayout.CENTER);

	}

	protected JPanel createCenterForm() {
		JPanel centerform = new JPanel();
		centerform.setName("stecenterform");
		formlayout = new CFormlayout(1, 1);
		
		if(steformgr.isUseformUI()){
			centerform.setLayout(steformgr);
		}else{
			centerform.setLayout(formlayout);
		}

		Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo editor = en.nextElement();

			String filegroupname = stemodel.isCreateFieldgroup(editor
					.getColname());
			if (filegroupname != null && filegroupname.length() > 0) {
				CFieldGroup fg = new CFieldGroup(filegroupname);
				centerform.add(fg);
				formlayout.addLayoutComponent(fg, new CFormlineBreak());
			}

			editor.placeOnForm(centerform, formlayout, new HovEventHandle());
			editor.setHovcondif(this);
		}
		
		if(steformgr.isUseformUI()){
			steformgr.addTitlepane(centerform);
		}
		return centerform;
	}

	public void bindDatamodel(DBTableModel model, int row,
			boolean defaultenable, String editablecols) {
		bindDatamodel(model, row, defaultenable);

		if (editablecols == null)
			editablecols = "";
		if (editablecols.length() == 0) {
			return;
		}

		String colnames[] = editablecols.split(",");
		HashMap<String, String> editcolmap = new HashMap<String, String>();
		for (int i = 0; i < colnames.length; i++) {
			String colname = colnames[i].toLowerCase();
			if (colname.length() > 0) {
				editcolmap.put(colname, colname);
			}
		}
		Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo col = en.nextElement();
			if (col.isReadonly()) {
				col.setEnable(false);
			} else {
				if (editcolmap.get(col.getColname().toLowerCase()) != null) {
					col.setEnable(true);
				} else {
					col.setEnable(false);
				}
			}
		}

	}

	private void bindDatamodel(DBTableModel model, int row, boolean editenable) {
		this.row = row;
		this.model = model;
		if (row < 0) {
			return;
		}

		// ���ڰ����ݣ���Ҫ�����¼�
		isbindingvalue = true;

		int colct = model.getColumnCount();
		for (int col = 0; col < colct; col++) {
			String colname = model.getColumnDBName(col);
			if (colname == null) {
				continue;
			}
			DBColumnDisplayInfo editorcontrol = getEditorcontrol(colname);
			if (editorcontrol == null) {
				continue;
			}

			String value = (String) model.getValueAt(row, col);
			DBColumnDisplayInfo colinfo = model.getDisplaycolumninfos()
					.elementAt(col);
			// if (colinfo.getColtype().equals("number")) {
			value = colinfo.getFormatvalue(value);
			// }
			editorcontrol.setValue(value);
		}
		isbindingvalue = false;

		setFormEnable(editenable);
		fireStatuschanged();
	}

	public void fireStatuschanged() {
		int dbstatus = -1;
		try {
			dbstatus = model.getdbStatus(row);
		} catch (Exception e) {
			row = -1;
		}
		switch (dbstatus) {
		case RecordTrunk.DBSTATUS_SAVED:
			strdbstatus = "�ѱ���";
			break;
		case RecordTrunk.DBSTATUS_NEW:
			strdbstatus = "����";
			break;
		case RecordTrunk.DBSTATUS_MODIFIED:
			strdbstatus = "�޸�";
			break;
		case RecordTrunk.DBSTATUS_DELETE:
			strdbstatus = "ɾ��";
			break;
		default:
			strdbstatus = "";
			break;
		}
		String status = "��" + (row + 1) + "/" + stemodel.getRowCount()
				+ "����¼,״̬:" + strdbstatus;
		setStatus(status);
	}

	public DBColumnDisplayInfo getEditorcontrol(String colname) {
		Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo formeditor = en.nextElement();
			if (formeditor.getColname().equalsIgnoreCase(colname)) {
				return formeditor;
			}
		}
		return null;
	}

	public void clearAll() {
		isbindingvalue = true;
		if (model == null) {
			return;
		}

		int colct = model.getColumnCount();
		for (int col = 0; col < colct; col++) {
			String colname = model.getColumnDBName(col);
			if (colname == null) {
				continue;
			}
			DBColumnDisplayInfo editorcontrol = getEditorcontrol(colname);
			if (editorcontrol == null) {
				continue;
			}

			String value = "";
			editorcontrol.setValue(value);
		}
		isbindingvalue = false;
		setFormEnable(false);
	}

	private void setEditcompHotkey(DBColumnDisplayInfo colinfo) {
		// F12��ѡhov
		JComponent comp = colinfo.getEditComponent();
		KeyStroke keyf12 = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false);
		if (comp instanceof CTextArea) {
			((CTextArea) comp).getTextareaInputMap().put(keyf12, SHOWHOVDIALOG);
		} else {

			comp.getInputMap(JComponent.WHEN_FOCUSED)
					.put(keyf12, SHOWHOVDIALOG);
		}
		HovKeyAction hovkeyaction = new HovKeyAction(SHOWHOVDIALOG, colinfo);
		if (comp instanceof CTextArea) {
			((CTextArea) comp).getTextareaActionmap().put(SHOWHOVDIALOG,
					hovkeyaction);
		} else {
			comp.getActionMap().put(SHOWHOVDIALOG, hovkeyaction);
		}
		hovkeyactions.add(hovkeyaction);

		/*
		 * ���Ǳ༭����Ҫ���º�home end��,��form�����в���ʹ����Щ�����ƶ���¼ KeyStroke vkdown =
		 * KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
		 * comp.getInputMap(JComponent
		 * .WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkdown,
		 * CSteModel.ACTION_NEXT);
		 * comp.getActionMap().put(CSteModel.ACTION_NEXT, new
		 * SteActionListener(CSteModel.ACTION_NEXT));
		 * 
		 * KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
		 * comp
		 * .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkup,
		 * CSteModel.ACTION_PRIOR);
		 * comp.getActionMap().put(CSteModel.ACTION_PRIOR, new
		 * SteActionListener(CSteModel.ACTION_PRIOR));
		 * 
		 * KeyStroke vkhome = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0,
		 * false);
		 * comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
		 * ).put(vkhome, CSteModel.ACTION_FIRST);
		 * comp.getActionMap().put(CSteModel.ACTION_FIRST, new
		 * SteActionListener(CSteModel.ACTION_FIRST));
		 * 
		 * KeyStroke vkend = KeyStroke.getKeyStroke(KeyEvent.VK_END, 0, false);
		 * comp
		 * .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(vkend
		 * , CSteModel.ACTION_LAST);
		 * comp.getActionMap().put(CSteModel.ACTION_LAST, new
		 * SteActionListener(CSteModel.ACTION_LAST));
		 */
	}

	/**
	 * ������Form��ѡ���һ���ɱ༭�ؼ�focus��
	 */
	/*
	 * public void onActive(boolean focusfirst) { 20070420
	 * �򿪷���CFormFocusTraversalPolicy,����Ҫ����focus�� if (activecomp == null ||
	 * focusfirst) { for (int i = 0; i < centerform.getComponentCount(); i++) {
	 * final JComponent comp = (JComponent) centerform.getComponent(i); if
	 * (!(comp instanceof JTextField)) { continue; } if (!comp.isVisible() ||
	 * !comp.isEnabled()) { continue; } Runnable runable = new Runnable() {
	 * public void run() { comp.requestFocusInWindow(); } };
	 * SwingUtilities.invokeLater(runable); break; } } else {
	 * //System.out.println("activecomp=" + activecomp.getClass().getName());
	 * Runnable runable = new Runnable() { public void run() {
	 * activecomp.requestFocusInWindow(); } };
	 * SwingUtilities.invokeLater(runable); } }
	 */

	public void onchanged(CFormatTextField fireeventtextfield, String value,
			String oldvalue) {
		Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo editor = en.nextElement();
			if (editor.getEditComponent() == fireeventtextfield
					&& fireeventtextfield instanceof CFormatTextField) {
				// CFormatTextField textfield = (CFormatTextField)
				// fireeventtextfield;

				if (stemodel != null) {
					if (!value.equals(oldvalue)) {
						stemodel.setFormfieldvalue(row, editor.getColname(),
								value);
					}

					break;
				}
			}
		}
	}

	public boolean isHov(String editname) {
		if (stemodel == null)
			return false;
		DBColumnDisplayInfo colinfo = stemodel.getDBColumnDisplayInfo(editname);
		if (colinfo == null)
			return false;
		return colinfo.isUsehov();
	}

	public void invokeHov(String colname, String newvalue, String oldvalue) {
		DBColumnDisplayInfo colinfo = stemodel.getDBColumnDisplayInfo(colname);
		// add by wwh
		// ���˵��CMultihov��CMde hov,����ʹ�ô�ͳ�ĶԻ���
		if (newvalue.length() == 0) {
			// clearHov();
			if (stemodel != null) {
				colinfo.getHov().hide();
				stemodel.on_clearhov(row, colname);
			}
			return;
		}

		CHovBase hov = colinfo.getHov();
		if (hov instanceof CMultiHov || hov instanceof CMdeHov
				|| hov instanceof CStehovEx || hov instanceof CMdehovEx) {
			stemodel.invokeMultimdehov(row, colname, newvalue);
			return;
		} else {
			stemodel.setHovshowing(colinfo.getColname(), true);
			// ����hov
			String otherewheres = stemodel.getHovOtherWheres(row, colinfo
					.getColname());
			String hovclassname = colinfo.getHov().getClass().getName();
			otherewheres = addWherecond(otherewheres, stemodel
					.getHovOtherWheresAp(hovclassname));

			colinfo.invokeHov(newvalue, oldvalue, otherewheres);
		}
		// editor.onEnterkeydown();
	}

	public boolean confirmHov(String editname, String newvalue, String oldvalue) {
		DBColumnDisplayInfo colinfo = stemodel.getDBColumnDisplayInfo(editname);
		boolean ret = colinfo.confirmHov();
		if (ret) {
			stemodel.setHovshowing(editname, false);
		}
		return ret;
	}

	public void cancelHov(String editorname, String newvalue, String oldvalue) {
		stemodel.setHovshowing(editorname, false);
	}

	protected String addWherecond(String where1, String where2) {
		StringBuffer sb = new StringBuffer();
		if (where1.length() > 0) {
			sb.append(where1);
		}
		if (where2.length() > 0) {
			if (sb.length() > 0)
				sb.append(" and ");
			sb.append(where2);
		}
		return sb.toString();
	}

	/*
	 * public void syncData() { Enumeration<DBColumnDisplayInfo> en =
	 * editorcontrols.elements(); while (en.hasMoreElements()) {
	 * DBColumnDisplayInfo editor = en.nextElement(); if (frame != null) {
	 * JComponent editcomp = editor.getEditComponent(); if(editcomp instanceof
	 * JTextField){ JTextField textfield=(JTextField)editcomp;
	 * frame.setFormfieldvalue(row, editor.getExpr(), textfield.getText()); } }
	 * } }
	 */

	public void setFormEnable(boolean flag) {
		Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo editor = en.nextElement();
			if (editor.isReadonly()) {
				// //////////ֻ���ģ�����������Ա༭xxxxxxxxxxxxx
				if (flag
						&& model.getRecordThunk(row).getDbstatus() == RecordTrunk.DBSTATUS_NEW) {
					editor.setEnable(false);
				} else {
					editor.setEnable(false);
				}
			} else {
				editor.setEnable(flag);
			}

		}
	}

	class SteActionListener extends AbstractAction {
		public SteActionListener(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			if (stemodel == null) {
				return;
			}
			actionlistener.actionPerformed(e);
		}
	}

	protected JPanel createStatusPanel() {
		JPanel statuspanel = new JPanel();
		BoxLayout boxlay = new BoxLayout(statuspanel, BoxLayout.X_AXIS);
		statuspanel.setLayout(boxlay);

		lbstatus = new JLabel();
		statuspanel.add(lbstatus);

		JButton btn = new JButton("ȷ�Ϲر�(Ctrl+W)");
		btn.setPreferredSize(new Dimension(100, 20));
		btn.addActionListener(new CloseAction());
		if (!stemodel.isShowformonly()) {
			statuspanel.add(btn);
		}

		btn = new JButton("����(ESC)");
		btn.setPreferredSize(new Dimension(100, 20));
		btn.addActionListener(new CancelCloseAction());
		if (!stemodel.isShowformonly()) {
			// 20080708ȡ��esc������Ϊesc���岻��
			statuspanel.add(btn);

			InputMap im = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
					"cancelform");
			getActionMap().put("cancelform", new CancelCloseAction());

		}

		if (DefaultNPParam.debug == 1) {
			btn = new JButton("��Ƭ���");
			btn.setPreferredSize(new Dimension(100, 20));
			btn.addActionListener(new FormdesignAction());
			statuspanel.add(btn);
		}
		return statuspanel;
	}

	class CloseAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			stemodel.doHideform();
		}

	}

	class CancelCloseAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			stemodel.doCancelform();
		}

	}

	class FormdesignAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			steformgr.openSetupDlg(centerform);
			if(steformgr.isUseformUI()){
				centerform.setLayout(steformgr);
				stemodel.showForm();
			}
		}

	}

	protected void setStatus(String s) {
		String title = s;
		lbstatus.setText(s);
	}

	class Doclistener implements DocumentListener {
		JTextField fireeventtextfield = null;

		public Doclistener(JTextField textfield) {
			this.fireeventtextfield = textfield;
		}

		public void freeMemory() {
			fireeventtextfield = null;
		}

		public void insertUpdate(DocumentEvent e) {
			if (isbindingvalue)
				return;
			if (stemodel == null)
				return;
			Document doc = e.getDocument();
			try {
				// String s = doc.getText(0, doc.getLength());
				String s = fireeventtextfield.getText();
				// String v="";
				/*
				 * if (fireeventtextfield instanceof CFormatTextField) { v =
				 * (String) ((CFormatTextField)
				 * fireeventtextfield).getItemValue(); }
				 */
				if (s.length() > 0) {
					// setFieldText(s);
					fireStatuschanged();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		public void removeUpdate(DocumentEvent e) {
			if (isbindingvalue)
				return;
			Document doc = e.getDocument();
			try {
				String s = fireeventtextfield.getText();
				/*
				 * if (fireeventtextfield instanceof CFormatTextField) { s =
				 * (String) ((CFormatTextField)
				 * fireeventtextfield).getItemValue(); }
				 */
				// setFieldText(s);
				fireStatuschanged();
			} catch (Exception e1) {
			}

		}

		public void changedUpdate(DocumentEvent e) {
			if (isbindingvalue)
				return;
			Document doc = e.getDocument();
			try {
				String s = doc.getText(0, doc.getLength());
				// setFieldText(s);
				fireStatuschanged();
			} catch (BadLocationException e1) {
			}
		}

	}

	public void commitEdit() {
		// System.out.println("commitEdit() activecomp="+activecomp);
		// if(activecomp!=null){
		// System.out.print("Text=");
		// System.out.println(((JFormattedTextField)activecomp).getText());
		// }
		if (activecomp != null && activecomp instanceof CFormatTextField) {
			try {
				((CFormatTextField) activecomp).commitEdit();
			} catch (ParseException e) {
			}
		}
	}

	/**
	 * �༭�Ŀ�Ƭ���ڱ�ǿ�йر�
	 */
	public boolean cancelEdit() {
		if (stemodel != null) {
			return stemodel.cancelEdit();
		}
		return true;
	}

	public Dimension getPreferredSize() {
		if(steformgr.isUseformUI()){
			return steformgr.preferredLayoutSize(this);
		}else	if (formlayout != null) {
			return formlayout.preferredLayoutSize(this);
		} else {
			return super.getPreferredSize(); 
		}
	}

	public CSteModel getStemodel() {
		return stemodel;
	}

	class CComboBoxListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (isbindingvalue)
				return;
			CComboBox combobox = (CComboBox) e.getSource();
			Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo editor = en.nextElement();
				if (editor.getEditComponent() == combobox) {
					// JTextField textfield = (JTextField) combobox;

					if (stemodel != null) {
						String cbv = combobox.getValue();
						if (!cbv.equals("1") || !cbv.equals("2")) {
							int m;
							m = 3;
						}
						stemodel.setFormfieldvalue(row, editor.getColname(),
								cbv);
						break;
					}
				}

			}
		}
	}

	class CheckboxListener implements ItemListener {

		public void itemStateChanged(ItemEvent e) {
			if (isbindingvalue)
				return;
			CCheckBox cb = (CCheckBox) e.getSource();
			Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo editor = en.nextElement();
				if (editor.getEditComponent() == cb) {
					// JTextField textfield = (JTextField) combobox;

					if (stemodel != null) {
						String cbv = cb.getValue();
						stemodel.setFormfieldvalue(row, editor.getColname(),
								cbv);
						break;
					}
				}

			}
		}
	}

	class TextareaListener implements DocumentListener {
		DBColumnDisplayInfo colinfo = null;

		public TextareaListener(DBColumnDisplayInfo colinfo) {
			this.colinfo = colinfo;
		}

		public void freeMemory() {
			this.colinfo = null;
		}

		public void changedUpdate(DocumentEvent e) {
			fireEvent(e);
		}

		public void insertUpdate(DocumentEvent e) {
			fireEvent(e);
		}

		public void removeUpdate(DocumentEvent e) {
			fireEvent(e);
		}

		void fireEvent(DocumentEvent e) {
			if (isbindingvalue)
				return;
			try {
				String textv = e.getDocument().getText(0,
						e.getDocument().getLength());
				stemodel.setFormfieldvalue(row, colinfo.getColname(), textv);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}

		}

	}

	class HovEventHandle implements HovListener {
		public void on_hov(DBColumnDisplayInfo dispinfo,
				DBTableModel resultmodel) {
			stemodel.on_hov(row, dispinfo.getColname(), resultmodel);
		}

		public void gainFocus(DBColumnDisplayInfo dispinfo) {
			activecomp = dispinfo.getEditComponent();
			// System.out.println("gainFocus
			// colname="+dispinfo.getColname()+",activecomp="+activecomp);
			// System.out.println("Text
			// ="+((JFormattedTextField)activecomp).getText());
			// �ص�������hov��

			Enumeration<DBColumnDisplayInfo> en = editorcontrols.elements();
			while (en.hasMoreElements()) {
				DBColumnDisplayInfo colinfo = en.nextElement();
				if (colinfo.isUsehov() && colinfo != dispinfo) {
					colinfo.getHov().hide();
				}
			}
		}

		public void lostFocus(DBColumnDisplayInfo dispinfo) {
			stemodel.setHovshowing(dispinfo.getColname(), false);
		}

	}

	class HovKeyAction extends AbstractAction {
		DBColumnDisplayInfo colinfo = null;

		public HovKeyAction(String name, DBColumnDisplayInfo colinfo) {
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
			this.colinfo = colinfo;
		}

		public void actionPerformed(ActionEvent e) {
			// String cmd = e.getActionCommand();
			// ��ǰ�����Ԫ�����¼��Ĵ�����. by wwh 20070727
			activecomp = (JComponent) e.getSource();
			if (colinfo.getColname().equalsIgnoreCase("filegroupid")) {
				stemodel.on_hov(row, "filegroupid", null);
			} else if (colinfo.isUsehov()) {
				colinfo.showHovDialog();
			} else if (colinfo.getColtype().equalsIgnoreCase("date")) {
				colinfo.showDatedialog();
			}
		}
	}

	public String getHovOtherWheres(String colname) {

		String otherewheres = stemodel.getHovOtherWheres(row, colname);
		DBColumnDisplayInfo colinfo = stemodel.getDBColumnDisplayInfo(colname);
		String hovclassname = colinfo.getHov().getClass().getName();
		otherewheres = addWherecond(otherewheres, stemodel
				.getHovOtherWheresAp(hovclassname));
		return otherewheres;
	}

	public void invokeMultimdehov(String colname) {
		stemodel.invokeMultimdehov(row, colname, "");

	}

	/*
	 * @Override public void setBounds(int x, int y, int width, int height) { //
	 * TODO Auto-generated method stub super.setBounds(x, y, width, height);
	 * scrollp.setSize(width, height); }
	 */
	public void freeMemory() {

		editorcontrols = null;
		lbstatus = null;
		activecomp = null;
		stemodel = null;
		model = null;
		centerform = null;
		actionlistener = null;
		formlayout = null;
		if (hovkeyactions != null) {
			Enumeration<HovKeyAction> en = hovkeyactions.elements();
			while (en.hasMoreElements()) {
				HovKeyAction ha = en.nextElement();
				ha.colinfo = null;
			}
			hovkeyactions.removeAllElements();
			hovkeyactions = null;
		}
	}

	public void onActive(boolean focusfirst) {
		final boolean finalfocusfirst = focusfirst;
		Runnable r = new Runnable() {
			public void run() {
				if (finalfocusfirst) {
					Component firstcomp = getFocusTraversalPolicy()
							.getFirstComponent((Container) Steform.this);
					if (firstcomp != null) {
						firstcomp.requestFocus();
					}
				} else {
					if (activecomp != null)
						activecomp.requestFocus();
				}

			}
		};
		SwingUtilities.invokeLater(r);

	}

	public boolean canInvokehov(String invokehovcolname) {
		if (stemodel == null)
			return true;
		return stemodel.canInvokehov(invokehovcolname);
	}

	/**
	 * �Ƿ���������ֵ��
	 * 
	 * @return true���ɳ�������ֵ����Ҫ��������¼�
	 */
	public boolean isbindingvalue() {
		return isbindingvalue;
	}

	public void setbindingvalue(boolean isbindingvalue) {
		this.isbindingvalue = isbindingvalue;
	}

	public boolean isUseformUI(){
		return steformgr.isUseformUI();
	}
}
