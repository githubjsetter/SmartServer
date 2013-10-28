package com.smart.platform.gui.control;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.smart.platform.communicate.RecordTrunk;
import com.smart.platform.gui.ste.Hovdefine;
import com.smart.platform.image.CIcon;
import com.smart.platform.image.IconFactory;

/**
 * 可编辑表
 * 
 * @author Administrator
 * 
 */
public class CEditableTable extends CTable {

	Vector<DBColumnDisplayInfo> formcolumndisplayinfos = null;
	boolean isbindingvalue = false;

	Vector<HovListener> hovlistenertable = new Vector<HovListener>();

	Action priorrowaction = null;
	Action nextrowaction = null;
	Action cancelaction = null;
	Action nextcellaction = null;
	boolean hovshowing = false;
	
	/**
	 * 点击多少次开始编辑
	 */
	protected int clickcounttostartedit=1;


	public CEditableTable(TableModel dm) {
		super(dm);
		DBTableModel dbmodel = (DBTableModel) getModel();
		formcolumndisplayinfos = dbmodel.getDisplaycolumninfos();
		/*
		 * InputMap map = getInputMap(
		 * JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();
		 * map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		 * map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false));
		 * 
		 * KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
		 * false); map.remove(vkenter);
		 */
		// 回车往前
		InputMap map = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false), "nothing");
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0, false), "nothing");

		KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		map.put(vkenter, "selectNextColumnCell");

		KeyStroke vktab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
		map.put(vktab, "selectNextColumnCell");

		KeyStroke vkshifttab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
				Event.SHIFT_MASK, false);
		map.put(vkshifttab, "selectPreviousRowCell");

		ActionMap actionmap = getActionMap();
		actionmap.put("selectNextColumnCell", new SelectNextRowCellAction());
		actionmap.put("selectPreviousRowCell",
				new SelectPreviousRowCellAction());

		registryTableaction();

		getSelectionModel().addListSelectionListener(
				new Tableselectionlistener());
		getColumnModel().addColumnModelListener(new Tablecolumnmodellistener());

		this.setHovshowing(false);
	}

	@Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		setEditprop();

		DBTableModel dbmodel = (DBTableModel) getModel();
		formcolumndisplayinfos = dbmodel.getDisplaycolumninfos();

		Enumeration<DBColumnDisplayInfo> en = formcolumndisplayinfos.elements();
		while (en.hasMoreElements()) {
			DBColumnDisplayInfo editor = en.nextElement();
			JComponent editcomp = editor.getEditComponentwithoutKeytraver();
			if (editcomp instanceof CFormatTextField) {
				CFormatTextField textfield = (CFormatTextField) editcomp;
				textfield.setChangelistener(new FormattextHandel());
			} else if (editcomp instanceof CTextField) {
				CTextField textfield = (CTextField) editcomp;
			} else if (editcomp instanceof CComboBox) {
				CComboBox combobox = (CComboBox) editcomp;
			} else if (editcomp instanceof CCheckBox) {
				CCheckBox cb = (CCheckBox) editcomp;
			} else if (editcomp instanceof CTextArea) {
				CTextArea textarea = (CTextArea) editcomp;
			}

			if (editor.isUsehov()
					|| editor.getColtype().equalsIgnoreCase("date")
					|| editor.getColname().equalsIgnoreCase("filegroupid")) {
				setEditcompHotkey(editor);
				editor.setHovlistener(new Hovhandle());
			}
		}
		
		for(int i=0;i<getColumnCount();i++){
			TableColumn tc=getColumnModel().getColumn(i);
			int modelindex=convertColumnIndexToModel(i);
			DBColumnDisplayInfo colinfo = formcolumndisplayinfos.elementAt(modelindex);
			if (colinfo.getTablecolumnwidth() >= 0) {
				tc.setPreferredWidth(colinfo.getTablecolumnwidth());
			} else {
				tc.setPreferredWidth(65);
			}
		}
	}

	protected void registryTableaction() {
		ActionMap am = getActionMap();
		priorrowaction = am.get("selectPreviousRow");
		nextrowaction = am.get("selectNextRow");
		cancelaction = am.get("cancel");
		nextcellaction = am.get("selectNextColumnCell"); // 回车

		am.put("selectPreviousRow", new TableAction("selectPreviousRow"));
		am.put("selectNextRow", new TableAction("selectNextRow"));
		am.put("cancel", new TableAction("cancel"));
		am.put("selectNextColumnCell", new TableAction("selectNextColumnCell"));

	}

	class TableAction extends AbstractAction {
		public TableAction(String name) {
			super(name);
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
		}

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("selectPreviousRow")) {
				if (hovshowing) {
					/*
					 * DBColumnDisplayInfo colinfo =
					 * CSteModel.this.getDBColumnDisplayInfo(hovcolname);
					 * //colinfo.getHov().priorRow();
					 */} else {
					priorrowaction.actionPerformed(e);
				}
			} else if (cmd.equals("selectNextRow")) {
				if (hovshowing) {
					/*
					 * DBColumnDisplayInfo colinfo =
					 * CSteModel.this.getDBColumnDisplayInfo(hovcolname);
					 * //colinfo.getHov().nextRow();
					 */} else {
					nextrowaction.actionPerformed(e);
				}
			} else if (cmd.equals("cancel")) {
				;// donothing
				/*
				 * if(hovshowing){ DBColumnDisplayInfo colinfo =
				 * CSteModel.this.getDBColumnDisplayInfo(hovcolname);
				 * colinfo.getHov().hide(); }else{
				 * cancelaction.actionPerformed(e); }
				 */
			} else if (cmd.equals("selectNextColumnCell")) {
				if (hovshowing) {
					int m;
					m = 3;
				} else {
					nextcellaction.actionPerformed(e);
				}
			}
		}

	}

	protected void setEditprop() {
		setReadonly(false);
		/*
		 * 去掉F2键编辑 enter下一行 InputMap map = getInputMap(
		 * JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent(); if (map !=
		 * null) { map.remove(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		 * KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0,
		 * false); map.remove(vkenter); // 回车往前 map.put(vkenter,
		 * "selectNextColumnCell"); }
		 */

		DBTableModel dbmodel = (DBTableModel) getModel();
		TableColumnModel cm = getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			TableColumn tc = cm.getColumn(i);
			int mi = tc.getModelIndex();
			DBColumnDisplayInfo colinfo = dbmodel.getDisplaycolumninfos()
					.elementAt(mi);

			JComponent editcomp = colinfo.getEditComponent();
			removeEnterkey(editcomp);

			Tablecelleditor cCellEditor = null;
			if (editcomp instanceof JTextField) {
				cCellEditor = new Tablecelleditor((JTextField) editcomp,
						colinfo);
			} else if (editcomp instanceof JComboBox) {
				cCellEditor = new Tablecelleditor((JComboBox) editcomp, colinfo);
			} else if (editcomp instanceof JCheckBox) {
				cCellEditor = new Tablecelleditor((JCheckBox) editcomp, colinfo);
			}

			cCellEditor.setClickCountToStart(clickcounttostartedit);
			tc.setCellEditor(cCellEditor);

			PlainTablecellRender cellRenderer = new PlainTablecellRender(
					colinfo);
			tc.setCellRenderer(cellRenderer);
		}
	}

	public int getEditingrow() {
		return editorcurrow;
	}


	int editorcurrow = -100;
	int editorcurcol = -100;

	protected class Tablecelleditor extends DefaultCellEditor {
		protected DBColumnDisplayInfo colinfo = null;

		public int getCurrow() {
			return editorcurrow;
		}

		public int getCurcol() {
			return editorcurcol;
		}

		public Tablecelleditor(JTextField textField, DBColumnDisplayInfo colinfo) {
			super(textField);
			textField.removeActionListener(delegate);
			delegate = new MyDelegate();
			textField.addActionListener(delegate);
			this.colinfo = colinfo;
			setClickCountToStart(clickcounttostartedit);
		}

		public Tablecelleditor(JComboBox comboBox, DBColumnDisplayInfo colinfo) {
			super(comboBox);
			comboBox.removeActionListener(delegate);
			delegate = new MyDelegate();
			comboBox.addActionListener(delegate);
			this.colinfo = colinfo;
			setClickCountToStart(clickcounttostartedit);
		}

		public Tablecelleditor(JCheckBox checkBox, DBColumnDisplayInfo colinfo) {
			super(checkBox);
			checkBox.removeActionListener(delegate);
			delegate = new MyDelegate();
			checkBox.addActionListener(delegate);
			this.colinfo = colinfo;
			setClickCountToStart(clickcounttostartedit);
		}

		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) anEvent;
				int clickrow = rowAtPoint(new Point(me.getX(), me.getY()));
				if (clickrow < 0 || clickrow >= getRowCount() - 1) {
					return false;
				}
			}
			// System.out.println("isCellEditable col="+colinfo.getColname());
			if (colinfo.isReadonly())
				return false;
			if (colinfo.isHide())
				return false;
			if (colinfo.getColtype().equals("行号"))
				return false;
			JTableHeader th = getTableHeader();
			TableColumn tcr=th.getResizingColumn();
			TableColumn tcd=th.getDraggedColumn();
			//System.out.println("isCellEditable col="+colinfo.getColname());
			//System.out.println("table resize c="+tcr + ",table drag c="+tcd);
			
			if(tcr!=null || tcd!=null){
				return false;
			}

			return super.isCellEditable(anEvent); // To change body of
		}

		/**
		 * stopCellEditing()被调用有两种情况. 1
		 * 是在编辑过程中按回车键,认为做为命令处理.先调用invokehov,后调用stopCellediting()
		 * 
		 * 2 另一种是按Tab键或鼠标点击,移动了焦点. 先调用了stopCellediting(),后调用invokehov
		 * 
		 * 对于第2种情况,发现如果使用了hov,stopCelledtiing()返回false;
		 */
		boolean callfromactionperform = false;

		public boolean stopCellEditing() {
			if (callfromactionperform) {
				if (hovshowing) {
					return false;
				}
			} else {
				if (colinfo.isUsehov()) {
					JComponent comp = colinfo.getEditComponent();
					if (comp instanceof CFormatTextField) {
						/*
						 * 这里不应该触发commitEdit() try { ((CFormatTextField)
						 * comp).commitEdit(); } catch (ParseException e) {
						 * logger.error("ERROR", e); return false; }
						 */}
					if (hovshowing) {
						return false;
					}
				}
			}
			return super.stopCellEditing();
		}

		class ComboboxAction extends AbstractAction {
			Action oldaction;

			public ComboboxAction(String name, Action oldaction) {
				super();
				this.oldaction = oldaction;
				super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
			}

			public void actionPerformed(ActionEvent e) {
				CComboBox cb = (CComboBox) e.getSource();
				// System.out.println(e.getWhen()+" "+e.getActionCommand()+",cb
				// visible="+cb.isPopupVisible());
				if (cb.isPopupVisible()) {
					oldaction
							.actionPerformed(new ActionEvent(cb, e.getID(), e
									.getActionCommand(), e.getWhen(), e
									.getModifiers()));
					cb.setPopupVisible(false);
					editNext();
					return;
				}
				editNext();
			}
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			Component editcomp = this.getComponent();
			editorcurrow = row;
			editorcurcol = column;

			if (editcomp instanceof CComboBox) {
				CComboBox cb = (CComboBox) editcomp;
				if (cb.getModel() instanceof CComboBoxModel) {
					CComboBoxModel ccbmodel = (CComboBoxModel) cb.getModel();
					int sindex = ccbmodel.getKeyIndex((String) value);
					if (sindex < 0)
						sindex = 0;
					cb.setSelectedIndex(sindex);

				}
				Action oldaction = cb.getActionMap().get("enterPressed");
				if (!(oldaction instanceof ComboboxAction)) {
					// System.out.println("oldaction="+oldaction);

					cb.getActionMap().put("enterPressed",
							new ComboboxAction("enterPressed", oldaction));

				}

			} else if (editcomp instanceof CCheckBox) {
				CCheckBox cb = (CCheckBox) editcomp;
				if (value instanceof String) {
					cb.setSelected(((String) value).equals("1"));
				} else if (value instanceof Boolean) {
					cb.setSelected(((Boolean) value).booleanValue());
				}
				// BasicTableUI会多发一次mouse事件
				cb.setMousereleasecount(0);
			}

			JComponent comp = (JComponent) super.getTableCellEditorComponent(
					table, value, isSelected, row, column);
			if (comp instanceof CFormatTextField) {
				try {
					((CFormatTextField) comp).setValue(value);
					// 设置esc键失效
					InputMap im = ((CFormatTextField) comp).getInputMap()
							.getParent();
					if (im != null) {
						// esc用于关闭hov窗口,因此原来的esc的reset-field-edit不要了
						KeyStroke vesc = KeyStroke.getKeyStroke(
								KeyEvent.VK_ESCAPE, 0, false);
						im.remove(vesc);

					}

					// 删除tab向前20070827 by wwh
					KeyStroke tabkey = KeyStroke.getKeyStroke(KeyEvent.VK_TAB,
							0, false);
					Set<AWTKeyStroke> focusTraversalKeys = comp
							.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
					HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
							focusTraversalKeys);
					hasset.remove(tabkey);
					comp
							.setFocusTraversalKeys(
									KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
									hasset);

				} catch (Exception e) {
					return null;
				}
			}

			/*
			 * KeyStroke vkup = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0,
			 * false); KeyStroke vkdown =
			 * KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false); InputMap imap =
			 * comp.getInputMap();
			 */return comp;
		}

		/*
		 * public void editNext() { ((MyDelegate) delegate).editNext(); }
		 */
		protected class MyDelegate extends EditorDelegate {
			public void setValue(Object value) {
				// System.out.println("celledit,setValue
				// currow="+currow+",curcol="+curcol+",value="+value);
				if (editorComponent instanceof JTextField) {
					((JTextField) editorComponent)
							.setText((value != null) ? value.toString() : "");
				} else if (editorComponent instanceof JCheckBox) {
					boolean selected = false;
					if (value instanceof Boolean) {
						selected = ((Boolean) value).booleanValue();
					} else if (value instanceof String) {
						selected = value.equals("1");
					}
					((JCheckBox) editorComponent).setSelected(selected);

				} else {
					((JComboBox) editorComponent).setSelectedItem(value);
				}
			}

			public Object getCellEditorValue() {
				String value = (String) getEditorvalue();
				if (value == null)
					value = "";

				if (editorcurrow >= 0 && editorcurrow < getRowCount() - 1) {
					int modelindex = getColumnModel().getColumn(editorcurcol)
							.getModelIndex();
					DBColumnDisplayInfo colinfo = ((DBTableModel) getModel())
							.getDisplaycolumninfos().elementAt(modelindex);
					if(colinfo.getEditcomptype().equals(DBColumnDisplayInfo.EDITCOMP_COMBOBOX)){
						value=colinfo.getComboboxKey(value);
						if (value == null)
							value = "";
					}
					
					String dbvalue = ((DBTableModel) getModel()).getItemValue(
							editorcurrow, modelindex);
					if (value == null)
						value = "";
					if (dbvalue == null)
						dbvalue = "";
					if (!value.equals(dbvalue)) {
						((DBTableModel) getModel()).setItemValue(editorcurrow,
								modelindex, value);
						tableChanged(new TableModelEvent(getModel(),
								editorcurrow));
					}

					/*
					 * // 如果当前值和数据库的值不一样,才触发on_itemchanged String dbvalue =
					 * dbvalue.getItemValue( editorcurrow, editorcurcol); if
					 * (dbvalue == null) dbvalue = ""; if
					 * (!dbvalue.equals(value)) {
					 * dlgdbmodel.setItemValue(editorcurrow,
					 * colinfo.getColname(), value);
					 * //tableChanged(editorcurrow); }
					 */}
				onitemchanged(editorcurrow, colinfo.getColname());
				return value;
			}

			Object getEditorvalue() {
				if (editorComponent instanceof JTextField) {
					return ((JTextField) editorComponent).getText();
				} else if (editorComponent instanceof JCheckBox) {
					return ((JCheckBox) editorComponent).isSelected() ? "1"
							: "0";
				} else {
					return ((JComboBox) editorComponent).getSelectedItem();
				}
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("comboBoxChanged")) {
					CComboBox cb = (CComboBox) e.getSource();
					cb.setPopupVisible(false);
					return;
				}
				callfromactionperform = true;
				editNext();
				callfromactionperform = false;
			}

			void editNext() {
				int ecol = getEditingColumn();
				int erow = getEditingRow();
				if (!Tablecelleditor.this.stopCellEditing())
					return;

				if (editorcurrow != erow || editorcurcol != ecol) {
					return;
				}

				if (getRowCount() <= 1) {
					return;
				}
				if (erow < 0) {
					editCellAt(0, getFirstEditableColumn(0));
				} else {
					if (ecol == getLastEditableColumn(editorcurrow)) {
						erow++;
						if (erow >= getRowCount() - 1) {
							erow = 0;
						}
						ecol = getFirstEditableColumn(erow);
						setRowSelectionInterval(erow, erow);
						// System.out.println("edit cell at " + erow + "," +
						// ecol);
						editCellAt(erow, ecol);
					} else {
						// 找下一个可编辑的列
						TableColumnModel tm = getColumnModel();
						for (int c = ecol + 1; c < tm.getColumnCount(); c++) {
							TableColumn tc = tm.getColumn(c);
							DBColumnDisplayInfo cinfo = ((DBTableModel) getModel())
									.getDisplaycolumninfos().elementAt(
											tc.getModelIndex());
							if (cinfo.getColtype().equals("行号")
									|| cinfo.isHide()
									|| cinfo.isReadonly()
									|| !CEditableTable.this.isCellEditable(
											editorcurrow, cinfo.getColname())) {
								continue;
							}
							// System.out.println("edit next cell at " + erow
							// + "," + c);
							editCellAt(erow, c);
							break;
						}

					}
				}
			}

		}

	}

	public int getFirstEditableColumn(int row) {
		TableColumnModel cm = getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			TableColumn colm = cm.getColumn(i);
			int mindex = colm.getModelIndex();
			DBColumnDisplayInfo colinfo = ((DBTableModel) getModel())
					.getDisplaycolumninfos().elementAt(mindex);
			if (colinfo.getColtype().equals("行号") || colinfo.isReadonly()
					|| colinfo.isHide()
					|| !isCellEditable(row, colinfo.getColname())) {
				continue;
			}
			return i;
		}
		return -1;
	}

	public int getLastEditableColumn(int row) {
		TableColumnModel cm = getColumnModel();
		for (int i = cm.getColumnCount() - 1; i >= 0; i--) {
			TableColumn colm = cm.getColumn(i);
			int mindex = colm.getModelIndex();
			DBColumnDisplayInfo colinfo = ((DBTableModel) getModel())
					.getDisplaycolumninfos().elementAt(mindex);
			if (colinfo.getColtype().equals("行号") || colinfo.isReadonly()
					|| colinfo.isHide()
					|| !isCellEditable(row, colinfo.getColname())) {
				continue;
			}
			return i;
		}
		return -1;
	}

	protected void removeEnterkey(JComponent comp) {
		KeyStroke enterkey = KeyStroke
				.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		KeyStroke vktab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
		Set<AWTKeyStroke> focusTraversalKeys = comp
				.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		HashSet<AWTKeyStroke> hasset = new HashSet<AWTKeyStroke>(
				focusTraversalKeys);
		hasset.remove(enterkey);
		hasset.remove(vktab);
		comp.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				hasset);
	}

	protected class PlainTablecellRender extends DefaultTableCellRenderer {
		protected JLabel lbnormal = null;
		protected JLabel lbbold = null;

		protected DBColumnDisplayInfo colinfo = null;

		public PlainTablecellRender(DBColumnDisplayInfo colinfo) {
			this.colinfo = colinfo;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (lbnormal == null) {
				lbnormal = new JLabel();
				lbnormal.setOpaque(true);
				lbbold = new JLabel();
				lbbold.setOpaque(true);
				Font normalfont = null;
				Font boldfont = null;
				Font font = table.getFont();

				String name = font.getFontName();
				int size = font.getSize();
				normalfont = new Font(name, Font.PLAIN, size);
				boldfont = new Font(name, Font.BOLD, size);

				lbnormal.setFont(normalfont);
				lbbold.setFont(boldfont);
			}

			JLabel lb = null;
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			RecordTrunk rec = dbmodel.getRecordThunk(row);
			if (rec.getSumflag() == RecordTrunk.SUMFLAG_SUMMARY) {
				lb = lbbold;
			} else {
				lb = lbnormal;
			}

			int modelIndex = table.getColumnModel().getColumn(column)
					.getModelIndex();
			/*
			 * DBColumnDisplayInfo colinfo = formcolumndisplayinfos
			 * .elementAt(modelIndex);
			 */
			if (colinfo.getColtype().equals("number")) {
				lb.setHorizontalAlignment(JLabel.RIGHT);
			} else {
				lb.setHorizontalAlignment(JLabel.LEFT);
			}

			boolean islastrow = table.getRowCount() - 1 == row;

			if (isSelected && !islastrow) {
				lb.setForeground(table.getSelectionForeground());
				lb.setBackground(table.getSelectionBackground());
			} else {
				lb.setForeground(table.getForeground());
				if (row % 2 == 0) {
					lb.setBackground(table.getBackground());
				} else {
					lb.setBackground(secondbackcolor);
				}
				//if (tableeditable) {
					if (!table.isCellEditable(row, column)) {
						lb.setBackground(readonlybackcolor);
					}
				//} else {
					// 表格不能编辑
					//if (!isColumneditable(row, colinfo.getColname())) {
					//	lb.setBackground(readonlybackcolor);
					//}
				//}
			}

			// 因为这句重载了整个函数
			// setFont(table.getFont());

			if (hasFocus) {
				Border border = null;
				if (isSelected) {
					border = UIManager
							.getBorder("Table.focusSelectedCellHighlightBorder");
				}
				if (border == null) {
					border = UIManager
							.getBorder("Table.focusCellHighlightBorder");
				}
				lb.setBorder(border);

				if (!isSelected && table.isCellEditable(row, column)) {
					Color col;
					col = UIManager.getColor("Table.focusCellForeground");
					if (col != null) {
						lb.setForeground(col);
					}
					col = UIManager.getColor("Table.focusCellBackground");
					if (col != null) {
						lb.setBackground(col);
					}
				}
			} else {
				lb.setBorder(new EmptyBorder(1, 1, 1, 1));
			}

			String newvalue = (String) value;
			// 数字format
			//if (colinfo.getColtype().equals("number")) {
				newvalue = colinfo.getFormatvalue(newvalue);
			//}

			// 是否有下拉？
			if (colinfo.getEditComponent() instanceof CComboBox) {
				DBTableModel cbdbmodel = null;
				CComboBoxModel cbmodel = dbmodel.getRecordThunk(row)
						.getColumnddlmodel(colinfo.getColname());
				if (cbmodel != null) {
					cbdbmodel = cbmodel.getDbmodel();
				}

				if (cbdbmodel == null) {
					cbdbmodel = colinfo.getCbdbmodel();
				}
				if (cbdbmodel != null) {
					boolean bfind = false;
					for (int r = 0; r < cbdbmodel.getRowCount(); r++) {
						if (cbdbmodel.getItemValue(r, 0).equals(newvalue)) {
							newvalue = cbdbmodel.getItemValue(r, 1);
							bfind = true;
							break;
						}
					}
					if (!bfind) {
						CComboBox ccb = (CComboBox) colinfo.getEditComponent();
						CComboBoxModel ccbmodel = (CComboBoxModel) ccb
								.getModel();
						for (int i = 0; i < ccbmodel.getSize(); i++) {
							if (newvalue.equals(ccbmodel.getKeyvalue(i))) {
								newvalue = (String) ccbmodel.getElementAt(i);
							}
						}
					}
				}
			} else if (colinfo.getEditComponent() instanceof CCheckBox) {
				if (newvalue == null || newvalue.length() == 0) {
					newvalue = "";
				} else {
					newvalue = newvalue.equals("1") ? "是" : "否";
				}
			} else if (rec.getSumflag() != RecordTrunk.SUMFLAG_SUMMARY
					&& colinfo.getColname().equalsIgnoreCase("filegroupid")) {
				newvalue = "附件";
				if (value != null && value instanceof String) {
					String strv = (String) value;
					if (strv.length() > 0) {
						newvalue = "有附件";
					}
				}
			}

			lb.setText(newvalue);

			Color c = getCellColor(row, column);
			if (c == null) {
				lb.setForeground(Color.BLACK);
			} else {
				lb.setForeground(c);
			}

			return lb;

		}

		public void freeMemory() {
			lbnormal = null;
			lbbold = null;
			colinfo = null;
		}
	}

	class SelectPreviousRowCellAction extends AbstractAction {

		public SelectPreviousRowCellAction() {
			super();
		}

		public void actionPerformed(ActionEvent e) {
			editPrevious();
		}

	}

	class SelectNextRowCellAction extends AbstractAction {

		public SelectNextRowCellAction() {
			super();
		}

		public void actionPerformed(ActionEvent e) {
			editNext();
		}

	}

	/**
	 * 编辑表格状态下编辑下一个
	 */
	protected void editNext() {
		int erow = currow;
		int ecol = curcol;

		if (getRowCount() <= 1) {
			return;
		}

		DBColumnDisplayInfo cinfo = formcolumndisplayinfos.elementAt(ecol);
		JComponent editcomp = cinfo.getEditComponent();
		if (editcomp instanceof CFormatTextField) {
			try {
				((CFormatTextField) editcomp).commitEdit();
			} catch (ParseException e) {
				return;
			}
		}

		if (hovshowing)
			return;

		stopEdit();
		if (erow == getRowCount() - 1) {
			// 说明到了合计行,要跳到第0行.
			editCellAt(0, getFirstEditableColumn(0));
			return;
		}

		if (erow < 0) {
			editCellAt(0, getFirstEditableColumn(0));
			return;
		} else {
			if (ecol >= getLastEditableColumn(erow)) {
				erow++;
				if (erow >= getRowCount() - 1) {
					erow = 0;
				}
				ecol = getFirstEditableColumn(erow);
				if (ecol >= 0) {
					editCellAt(erow, ecol);
				}
			} else {
				// 找下一个可编辑的列
				TableColumnModel tm = getColumnModel();
				for (int c = ecol + 1; c < tm.getColumnCount(); c++) {
					TableColumn tc = tm.getColumn(c);
					cinfo = formcolumndisplayinfos
							.elementAt(tc.getModelIndex());
					if (cinfo.getColtype().equals("行号") || cinfo.isHide()
							|| cinfo.isReadonly()
							|| !isCellEditable(erow, cinfo.getColname())) {
						continue;
					}
					// System.out.println("edit next cell at " + erow
					// + "," + c);
					if (c >= 0) {
						editCellAt(erow, c);
					}
					break;
				}

			}
		}
	}

	protected void editPrevious() {
		int erow = currow;
		int ecol = curcol;

		if (getRowCount() <= 1) {
			return;
		}

		DBColumnDisplayInfo cinfo = formcolumndisplayinfos.elementAt(ecol);
		JComponent editcomp = cinfo.getEditComponent();
		if (editcomp instanceof CFormatTextField) {
			try {
				((CFormatTextField) editcomp).commitEdit();
			} catch (ParseException e) {
				return;
			}
		}

		if (hovshowing)
			return;
		stopEdit();
		if (erow < 0) {
			editCellAt(0, getFirstEditableColumn(0));
			return;
		} else {
			if (ecol <= getFirstEditableColumn(erow)) {
				erow--;
				if (erow < 0) {
					erow = getRowCount() - 1 - 1;
				}
				ecol = getLastEditableColumn(erow);
				editCellAt(erow, ecol);
			} else {
				// 找上一个可编辑的列
				TableColumnModel tm = getColumnModel();
				for (int c = ecol - 1; c >= 0; c--) {
					TableColumn tc = tm.getColumn(c);
					cinfo = formcolumndisplayinfos
							.elementAt(tc.getModelIndex());
					if (cinfo.getColtype().equals("行号") || cinfo.isHide()
							|| cinfo.isReadonly()
							|| !isCellEditable(erow, cinfo.getColname())) {
						continue;
					}
					if (c >= 0) {
						editCellAt(erow, c);
					}
					break;
				}
			}
		}
	}

	class Tablecolumnmodellistener implements TableColumnModelListener {

		public void columnAdded(TableColumnModelEvent e) {
		}

		public void columnMarginChanged(ChangeEvent e) {
		}

		public void columnMoved(TableColumnModelEvent e) {
		}

		public void columnRemoved(TableColumnModelEvent e) {
		}

		public void columnSelectionChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			curcol = dm.getAnchorSelectionIndex();
			on_Focuscell();
/*			Runnable r = new Runnable() {
				public void run() {
					on_Focuscell();
				}
			};
			SwingUtilities.invokeLater(r);
*/
			}
	}

	protected void on_Focuscell() {
		//System.out.println("on_Focuscell,currow="+currow+",curcol="+curcol);
		
		if (curcol < 0) {
			return;
		}

		if (isReadonly()) {
			return;
		}

		int modelindex = getColumnModel().getColumn(curcol).getModelIndex();
		DBColumnDisplayInfo colinfo = formcolumndisplayinfos
				.elementAt(modelindex);
		if (colinfo.isHide() || colinfo.isReadonly()
				|| colinfo.getColtype().equals("行号")) {
			// editNext();
			return;
		}
/*
 * 2008-11-08 应该由clickcounttostart来实现cell edit
		if (!isReadonly() && currow >= 0 && curcol >= 0) {
			TableCellEditor tce = getCellEditor();
			if (tce == null) {
				editCellAt(currow, curcol);
			} else if (tce instanceof Tablecelleditor) {
				Tablecelleditor ce = (Tablecelleditor) tce;
				if (currow != ce.getCurrow() || curcol != ce.getCurcol()) {
					if (getCellEditor().stopCellEditing()) {
						editCellAt(currow, curcol);
					}
				}

			}
		}
*/		
		// System.out.println("current row,col=" + currow + "," + curcol);
	}

	class FormattextHandel implements CFormatTextFieldListener {

		public void cancelHov(String editorname, String newvalue,
				String oldvalue) {
		}

		public boolean confirmHov(String editorname, String newvalue,
				String oldvalue) {
			DBTableModel dbmodel = (DBTableModel) getModel();
			DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(editorname);
			CHovBase hov = colinfo.getHov();
			if (hov.isVisible()) {
				colinfo.confirmHov();
				DBTableModel result = hov.getResult();
				if (result != null) {
					String hovcolname = colinfo.getHovdefine().getHovcolname(
							editorname);
					String hovvalue = result.getItemValue(0, hovcolname);

					JComponent editcomp = colinfo.getEditComponent();
					if (editcomp instanceof CFormatTextField) {
						CFormatTextField textcomp = (CFormatTextField) editcomp;
						// textcomp.setValue(hovvalue);

						/*
						 * Enumeration<HovListener> en =
						 * hovlistenertable.elements(); while
						 * (en.hasMoreElements()) { HovListener hl =
						 * en.nextElement(); hl.on_hov(colinfo, result); }
						 */
						setHovshowing(false);
						return true;
					}
				}
				if (newvalue.length() == 0)
					return true;
				return false;
			} else {
				colinfo.confirmHov();
				return true;
			}
		}

		public void invokeHov(String editorname, String newvalue,
				String oldvalue) {
			if (newvalue.length() == 0)
				return;
			DBTableModel dbmodel = (DBTableModel) getModel();
			DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(editorname);
			setHovshowing(true);
			colinfo.invokeHov(newvalue, oldvalue, "");
		}

		public boolean isHov(String editorname) {
			DBTableModel dbmodel = (DBTableModel) getModel();
			DBColumnDisplayInfo colinfo = dbmodel.getColumninfo(editorname);
			return colinfo.isUsehov();
		}

		public void onchanged(CFormatTextField comp, String value,
				String oldvalue) {
			int m;
			m = 3;
			// editNext();
		}

	}

	private void setEditcompHotkey(DBColumnDisplayInfo colinfo) {
		// F12键选hov
		String SHOWHOVDIALOG = "showhovdialog";
		JComponent comp = colinfo.getEditComponent();
		KeyStroke keyf12 = KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false);
		comp.getInputMap(JComponent.WHEN_FOCUSED).put(keyf12, SHOWHOVDIALOG);
		HovKeyAction hovkeyaction = new HovKeyAction(SHOWHOVDIALOG, colinfo);
		comp.getActionMap().put(SHOWHOVDIALOG, hovkeyaction);
		// hovkeyactions.add(hovkeyaction);
	}

	class HovKeyAction extends AbstractAction {
		DBColumnDisplayInfo colinfo = null;

		public HovKeyAction(String name, DBColumnDisplayInfo colinfo) {
			super.putValue(AbstractAction.ACTION_COMMAND_KEY, name);
			this.colinfo = colinfo;
		}

		public void actionPerformed(ActionEvent e) {
			// String cmd = e.getActionCommand();
			// 当前激活的元件是事件的触发者. by wwh 20070727
			if (colinfo.isUsehov()) {
				colinfo.showHovDialog();
			} else if (colinfo.getColtype().equalsIgnoreCase("date")) {
				colinfo.showDatedialog();
			}
		}
	}

	class Hovhandle implements HovListener {

		public void gainFocus(DBColumnDisplayInfo dispinfo) {
		}

		public void lostFocus(DBColumnDisplayInfo dispinfo) {
		}

		/*
		 * public void on_hov(DBColumnDisplayInfo colinfo, DBTableModel result) { //
		 * 设置对应列的值 stopEdit(); Hovdefine hovdefine = colinfo.getHovdefine(); if
		 * (hovdefine != null) { DBTableModel dbmodel = (DBTableModel)
		 * getModel(); int row = currow; Iterator it =
		 * hovdefine.getColpairmap().keySet().iterator(); while (it.hasNext()) {
		 * String hovcolname = (String) it.next(); String dbcolname =
		 * hovdefine.getColpairmap() .get(hovcolname); if (dbcolname.length() ==
		 * 0) { continue; } String hovvalue = result.getItemValue(0,
		 * hovcolname); dbmodel.setItemValue(row, dbcolname, hovvalue); } } else {
		 * String hovcolname = colinfo.getHovdefine().getHovcolname(
		 * colinfo.getColname()); String hovvalue = result.getItemValue(0,
		 * hovcolname); JComponent editcomp = colinfo.getEditComponent(); if
		 * (editcomp instanceof CFormatTextField) { CFormatTextField textcomp =
		 * (CFormatTextField) editcomp; textcomp.setValue(hovvalue); } }
		 * Enumeration<HovListener> en = hovlistenertable.elements(); while
		 * (en.hasMoreElements()) { HovListener hl = en.nextElement();
		 * hl.on_hov(colinfo, result); } setHovshowing(false);
		 * 
		 * tableChanged(new TableModelEvent(getModel(),currow)); editNext(); }
		 */

		public void on_hov(DBColumnDisplayInfo colinfo, DBTableModel hovmodel) {
			// 根据hovdefine设置值
			if (hovmodel == null || hovmodel.getRowCount() == 0)
				return;

			TableCellEditor tce = getCellEditor();

			setHovshowing(false);

			DBTableModel dbmodel = (DBTableModel) getModel();
			// DBColumnDisplayInfo colinfo =
			// this.getDBColumnDisplayInfo(colname);
			if (colinfo == null) {
				return;
			}
			Hovdefine hovdefine = colinfo.getHovdefine();
			if (hovdefine == null) {
				return;
			}

			if (tce != null) {
				if (tce instanceof Tablecelleditor) {
					Tablecelleditor mytce = (Tablecelleditor) tce;
					if (mytce.colinfo.getColname().equalsIgnoreCase(
							colinfo.getColname())) {
						tce.stopCellEditing();
					}
				}
			}

			// 设置对应列的值
			Iterator it = hovdefine.getColpairmap().keySet().iterator();
			while (it.hasNext()) {
				String hovcolname = (String) it.next();
				String dbcolname = hovdefine.getColpairmap().get(hovcolname);
				if (dbcolname.length() == 0) {
					continue;
				}
				String hovvalue = hovmodel.getItemValue(0, hovcolname);
				dbmodel.setItemValue(currow, dbcolname, hovvalue);
				// 如果非本列，通知值变化了。
				// if (!dbcolname.equals(colname)) {
				// this.on_itemvaluechange(row, dbcolname, hovvalue);
				// }
			}

			Enumeration<HovListener> en = hovlistenertable.elements();
			while (en.hasMoreElements()) {
				HovListener hl = en.nextElement();
				hl.on_hov(colinfo, hovmodel);
			}
			setHovshowing(false);

			tableChanged(new TableModelEvent(getModel(), currow));

			if (tce != null && tce instanceof Tablecelleditor) {
				editNext();
			}

			return; // 成功
		}

	}

	public void addHovlistener(HovListener hovl) {
		hovlistenertable.add(hovl);
	}

	public void setHovshowing(boolean show) {
		hovshowing = show;
		// hovcolname = colname;

		InputMap map = getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).getParent();

		/*
		 * KeyStroke keys[]=map.allKeys(); for(int i=0;i<keys.length;i++){
		 * System.out.println(keys[i]+"==>"+map.get(keys[i])); }
		 */
		KeyStroke vkenter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
		KeyStroke vktab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false);
		if (show) {
			map.put(vkenter, null);
			map.put(vktab, null);
			setDisablemouseevent(true);
		} else {
			map.put(vkenter, "selectNextColumnCell");
			map.put(vktab, "selectNextColumnCell");
			setDisablemouseevent(false);
		}

	}

	class Tableselectionlistener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			// 当前选中的行是
			if (e.getValueIsAdjusting())
				return;
			DBTableModel dbmodel = (DBTableModel) getModel();

			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			int newrow = dm.getAnchorSelectionIndex();
			// 调用tablechanged()后，dm.getAnchorSelectionIndex()返回-1,这不是本意
			if (newrow >= 0) {
				currow = dm.getAnchorSelectionIndex();
			} else {
				if (dbmodel.getRowCount() == 0) {
					currow = -1;
				}
			}

			if (dbmodel.getRowCount() == 0) {
				currow = -1;
				curcol = -1;
			}
			on_Focuscell();
			// System.out.println("currow="+currow);
/*			Runnable r = new Runnable() {
				public void run() {
					on_Focuscell();
				}
			};
			SwingUtilities.invokeLater(r);
*/
			}

	}

	/**
	 * 表格背景颜色,区别不同的行
	 */
	protected Color secondbackcolor = new Color(240, 240, 240);

	/**
	 * 表格只读字段的颜色
	 */
	protected Color readonlybackcolor = new Color(220, 220, 220);

	public int getClickcounttostartedit() {
		return clickcounttostartedit;
	}

	public void setClickcounttostartedit(int clickcounttostartedit) {
		this.clickcounttostartedit = clickcounttostartedit;
	}
	
	protected void onitemchanged(int row,String colname){
		
	}
	
	protected Color getCellColor(int row, int col) {
		return null;
	}

}
