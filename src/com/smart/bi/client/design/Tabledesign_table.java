package com.smart.bi.client.design;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.TooManyListenersException;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.smart.bi.client.design.BITableV_def.Mergeinfo;
import com.smart.platform.gui.control.CEditableTable;
import com.smart.platform.gui.control.CFormatTextField;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.control.DBTableModel;

public class Tabledesign_table extends CEditableTable {
	Tablevdesignpane frm=null;
	Tablev_transferhandle tablev_transferhandle;
	BITableV_def tablevdef;
	boolean settingvalue=false;
	Color gray = new Color(222, 222, 222);
	private ListSelectionModel tablecolselectmodel;


	public Tabledesign_table(DBTableModel dm, Tablevdesignpane frm,BITableV_def tablevdef,
			Tablev_transferhandle tablev_transferhandle) {
		super(dm);
		this.frm=frm;
		this.tablevdef = tablevdef;
		this.tablev_transferhandle = tablev_transferhandle;
		setTableheadheight(10);
		setReadonly(false);
		// 双击才能编辑. 方便实现拖拽
		// setClickcounttostartedit(2);
		getColumnModel().addColumnModelListener(new TableColumnModelHandel());
		setDragEnabled(true);
		setTransferHandler(tablev_transferhandle);

		try {
			getDropTarget().addDropTargetListener(new Droptargethandle());
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setUI(new MytableUI());


		//tablecolselectmodel = getColumnModel().getSelectionModel();
		//getColumnModel().setSelectionModel(new MyTablecolumnHandler());
	}

	class Droptargethandle implements DropTargetListener {

		public void dragEnter(DropTargetDragEvent dtde) {
			Tabledesign_table.this.stopEdit();
			Tabledesign_table.this.setReadonly(true);
		}

		public void dragExit(DropTargetEvent dte) {
			Tabledesign_table.this.setReadonly(false);
		}

		public void dragOver(DropTargetDragEvent dtde) {
			Tabledesign_table.this.stopEdit();
			Tabledesign_table.this.setReadonly(true);
		}

		public void drop(DropTargetDropEvent dtde) {
			Tabledesign_table.this.setReadonly(false);
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

	}

	
	
	@Override
	public boolean editCellAt(int row, int column, EventObject e) {
		if(!super.editCellAt(row, column, e)){
			return false;
		}
		int mindx=this.convertColumnIndexToModel(column);
		DBTableModel dm=(DBTableModel) getModel();
		String colname=dm.getDisplaycolumninfos().elementAt(mindx).getColname();
		frm.editCellAt(row, colname);
		return true;
	}

	@Override
	public boolean editCellAt(int row, int column) {
		if(!super.editCellAt(row, column)){
			return false;
		}
		int mindx=this.convertColumnIndexToModel(column);
		DBTableModel dm=(DBTableModel) getModel();
		String colname=dm.getDisplaycolumninfos().elementAt(mindx).getColname();
		frm.editCellAt(row, colname);
		return true;
	}

	@Override
	public boolean editCellAt(int row, String colname) {
		if(!super.editCellAt(row, colname)){
			return false;
		}
		frm.editCellAt(row, colname);
		return true;
	}

	EditcompMousehandle editcompmousehandler=new EditcompMousehandle();
	@Override
	protected void setEditprop() {
		super.setEditprop();
		DBTableModel dbmodel = (DBTableModel) getModel();
		TableColumnModel cm = getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			TableColumn tc = cm.getColumn(i);
			int mi = tc.getModelIndex();
			DBColumnDisplayInfo colinfo = dbmodel.getDisplaycolumninfos()
					.elementAt(mi);

			JComponent editcomp = colinfo.getEditComponent();
			removeEnterkey(editcomp);
			editcomp.addMouseListener(editcompmousehandler);
			if (editcomp instanceof CFormatTextField) {
				CFormatTextField ctf = (CFormatTextField) editcomp;
				ctf.setDragEnabled(true);
				ctf.setTransferHandler(new Celleditor_transferhandler());
			}
			tc.setCellRenderer(new PlainTablecellRender());
		}

		setReadonly(false);
	}
	
	class EditcompMousehandle implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			String colname="";
			DBTableModel dm=(DBTableModel) Tabledesign_table.this.getModel();
			Enumeration<DBColumnDisplayInfo>en=dm.getDisplaycolumninfos().elements();
			while(en.hasMoreElements()){
				DBColumnDisplayInfo colinfo=en.nextElement();
				if(colinfo.getEditComponent()==e.getSource()){
					colname=colinfo.getColname();
					break;
				}
			}
			frm.doubleclickItem(Tabledesign_table.this.getRow(),colname);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
		
	}

	int selectrow;

	class Celleditor_transferhandler extends TransferHandler {

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
			return true;
		}

		@Override
		public int getSourceActions(JComponent c) {
			int cr = Tabledesign_table.this.getRow();
			int cc = Tabledesign_table.this.getCurcol();
			int er = Tabledesign_table.this.getEditingrow();
			int ec = Tabledesign_table.this.getEditingColumn();
			if (cr != er || cc != ec) {
				return DnDConstants.ACTION_NONE;
			}
			dragrow = cr;
			dragcol = cc;
			return DnDConstants.ACTION_MOVE | DnDConstants.ACTION_COPY;
		}

		int dragrow = -1;
		int dragcol = -1;

		@Override
		protected Transferable createTransferable(JComponent c) {
			if (c instanceof CFormatTextField) {
				String s = ((CFormatTextField) c).getText();
				return new StringSelection(s);
			}
			return super.createTransferable(c);
		}

		@Override
		protected void exportDone(JComponent source, Transferable data,
				int action) {
			if (source instanceof CFormatTextField) {
				int currow = Tabledesign_table.this.getRow();
				int curcol = Tabledesign_table.this.getCurcol();
				if (action == DnDConstants.ACTION_MOVE) {
					DBTableModel dm = (DBTableModel) Tabledesign_table.this
					.getModel();
					if (Tabledesign_table.this.getCellEditor() == null) {
						dm.setItemValue(dragrow, dragcol, "");
						Tabledesign_table.this
								.tableChanged(new TableModelEvent(dm, dragrow));
					} else {
						int mindex = Tabledesign_table.this.convertColumnIndexToModel(curcol);
						JComponent editcomp = dm.getDisplaycolumninfos().elementAt(
								mindex).getEditComponent();
						if (editcomp instanceof CFormatTextField) {
							((CFormatTextField) editcomp).replaceSelection("");
						}

					}
				}
			}
			super.exportDone(source, data, action);
		}

		@Override
		public boolean importData(JComponent comp, Transferable t) {
			DataFlavor dataflavor = t.getTransferDataFlavors()[0];
			Object tranobj = null;
			try {
				tranobj = t.getTransferData(dataflavor);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			if (dataflavor
					.getMimeType()
					.equals(
							"application/x-java-serialized-object; class=java.lang.String")) {
				// String,说明是单元格移动.
				String transfervalue = (String) tranobj;
				Tabledesign_table table = Tabledesign_table.this;
				int newr = table.getRow();
				int newc = table.getCurcol(); //
				DBTableModel dm = (DBTableModel) table.getModel();
				// System.out.println("newr=" + newr + ",newc=" + newc);
				if (table.getCellEditor() != null) {
					int mindex = table.convertColumnIndexToModel(newc);
					JComponent editcomp = dm.getDisplaycolumninfos().elementAt(
							mindex).getEditComponent();
					if (editcomp instanceof CFormatTextField) {
						((CFormatTextField) editcomp).replaceSelection(transfervalue);
					}
				} else {
					dm.setItemValue(newr, newc, transfervalue);
					table.tableChanged(new TableModelEvent(dm, newr));
				}
				return true;
			}
			return super.importData(comp, t);
		}

	}

	@Override
	public void setRowSelectionInterval(int index0, int index1) {
		super.setRowSelectionInterval(index0, index1);
		selectrow = index0;
	}

	/**
	 * drag时选中的col
	 */
	int selectcol;

	@Override
	public void setColumnSelectionInterval(int index0, int index1) {
		super.setColumnSelectionInterval(index0, index1);
		selectcol = index0;
	}

	class TableColumnModelHandel implements TableColumnModelListener {

		public void columnAdded(TableColumnModelEvent e) {
		}

		public void columnRemoved(TableColumnModelEvent e) {
		}

		public void columnMoved(TableColumnModelEvent e) {
			if(settingvalue)return;
			frm.reverseBind();
		}

		public void columnMarginChanged(ChangeEvent e) {
			if(settingvalue)return;
			frm.reverseBind();
		}

		public void columnSelectionChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting())
				return;
			if (isTablechanging())
				return;
			DefaultListSelectionModel dm = (DefaultListSelectionModel) e
					.getSource();
			curcol = dm.getAnchorSelectionIndex();
			//System.out.println("curcol=" + curcol);
		}
	}

	public int getSelectrow() {
		return selectrow;
	}

	public int getSelectcol() {
		return selectcol;
	}


	/**
	 * 设计表的cellrender
	 * 
	 * @author user
	 * 
	 */
	class PlainTablecellRender extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			JLabel lb = new JLabel((String) value);
			DBTableModel dm = (DBTableModel) table.getModel();
			int mindex = table.getColumnModel().getColumn(column)
					.getModelIndex();
			// dm.getDisplaycolumninfos().elementAt(mindex);
			if (column == 0 || row < 0 || row > tablevdef.getCells().length - 1) {
				lb.setOpaque(true);
				lb.setBackground(gray);
				return lb;
			}
			BICell cell = tablevdef.getCells()[row][mindex - 1];
			lb.setFont(cell.getFont());
			
			Color background=table.getBackground();
			int mcolindex=table.convertColumnIndexToModel(column);
			int ismerge=tablevdef.isMergecell(row, mcolindex);
			if(ismerge==2){
				//background=Color.LIGHT_GRAY;
			}
			lb.setOpaque(true);
			lb.setBackground(background);

			if (isSelected) {
				lb.setBackground(table.getSelectionBackground());
			}
			// 对齐
			if (cell.getAlign() == BICell.ALIGN_LEFT) {
				lb.setHorizontalAlignment(JLabel.LEFT);
			} else if (cell.getAlign() == BICell.ALIGN_CENTER) {
				lb.setHorizontalAlignment(JLabel.CENTER);
			} else if (cell.getAlign() == BICell.ALIGN_RIGHT) {
				lb.setHorizontalAlignment(JLabel.RIGHT);
			}
			return lb;
		}
	}



	public boolean isSettingvalue() {
		return settingvalue;
	}

	public void setSettingvalue(boolean settingvalue) {
		this.settingvalue = settingvalue;
	}

	@Override
	protected void onitemchanged(int row,String colname){
		//这里一定要用线程调用 frm.reverseBind(),否则会死循环的.
		Runnable r=new Runnable(){
			public void run(){
				frm.reverseBind();
			}
		};
		SwingUtilities.invokeLater(r);
	}

	@Override
	public boolean isCellSelected(int row, int column) {
		return super.isCellSelected(row,column);
	}

	class MyTablecolumnHandler implements ListSelectionModel{

		public void addListSelectionListener(ListSelectionListener x) {
			tablecolselectmodel.addListSelectionListener(x);
		}

		public void addSelectionInterval(int index0, int index1) {
			tablecolselectmodel.addSelectionInterval(index0,index1);
			
			for(int i=0;i<3;i++){
				System.out.println("i="+i+",isselect="+isSelectedIndex(i));
			}
		}

		public void clearSelection() {
			tablecolselectmodel.clearSelection();
		}

		public int getAnchorSelectionIndex() {
			return tablecolselectmodel.getAnchorSelectionIndex();
		}

		public int getLeadSelectionIndex() {
			return tablecolselectmodel.getLeadSelectionIndex();
		}

		public int getMaxSelectionIndex() {
			return tablecolselectmodel.getMaxSelectionIndex();
		}

		public int getMinSelectionIndex() {
			return tablecolselectmodel.getMinSelectionIndex();
		}

		public int getSelectionMode() {
			return tablecolselectmodel.getSelectionMode();
		}

		public boolean getValueIsAdjusting() {
			return tablecolselectmodel.getValueIsAdjusting();
		}

		public void insertIndexInterval(int index, int length, boolean before) {
			 tablecolselectmodel.insertIndexInterval(index,length,before);
			
		}

		public boolean isSelectedIndex(int index) {
			// TODO Auto-generated method stub
			return tablecolselectmodel.isSelectedIndex(index);
		}

		public boolean isSelectionEmpty() {
			// TODO Auto-generated method stub
			return tablecolselectmodel.isSelectionEmpty();
		}

		public void removeIndexInterval(int index0, int index1) {
			tablecolselectmodel.removeIndexInterval(index0,index1);
		}

		public void removeListSelectionListener(ListSelectionListener x) {
			tablecolselectmodel.removeListSelectionListener(x);
		}

		public void removeSelectionInterval(int index0, int index1) {
			tablecolselectmodel.removeSelectionInterval(index0, index1);
		}

		public void setAnchorSelectionIndex(int index) {
			tablecolselectmodel.setAnchorSelectionIndex(index);
		}

		public void setLeadSelectionIndex(int index) {
			tablecolselectmodel.setLeadSelectionIndex(index);
		}

		public void setSelectionInterval(int index0, int index1) {
			tablecolselectmodel.setSelectionInterval(index0, index1);
			for(int i=0;i<3;i++){
				System.out.println("i="+i+",isselect="+isSelectedIndex(i));
			}
		}

		public void setSelectionMode(int selectionMode) {
			tablecolselectmodel.setSelectionMode(selectionMode);
		}

		public void setValueIsAdjusting(boolean valueIsAdjusting) {
			tablecolselectmodel.setValueIsAdjusting(valueIsAdjusting);
			for(int i=0;i<3;i++){
				System.out.println("i="+i+",isselect="+isSelectedIndex(i));
			}
		}
		
	}



	@Override
	public boolean isCellEditable(int row, int column) {
		//合并的单元格不能编辑.
		if(column==0)return false;
		int mcolindex=this.convertColumnIndexToModel(column);
		int ismerge=tablevdef.isMergecell(row, mcolindex-1);
		if(ismerge==0 || ismerge==1){
			//非合并,或首个合并,可以编辑
			return true;
		}
		return false;
	}
	
	class MytableUI extends BasicTableUI {

		@Override
		public void paint(Graphics g, JComponent c) {
			// TODO Auto-generated method stub
			super.paint(g, c);
			JTable table = (JTable) c;

			// 对于单元格.要合并
			Enumeration<Mergeinfo> en = tablevdef.getMergeinfos()
					.elements();
			while (en.hasMoreElements()) {
				Mergeinfo minfo = en.nextElement();
				// 合并
				Rectangle minrect = table.getCellRect(minfo.startrow,
						minfo.startcolumn+1, false);
				Rectangle maxrect = table.getCellRect(minfo.startrow
						+ minfo.rowcount - 1, minfo.startcolumn
						+ minfo.columncount - 1+1, false);
				Rectangle cellRect = minrect.union(maxrect);
				Color oldc = g.getColor();
				g.setColor(Color.WHITE);
				g.fillRect(cellRect.x, cellRect.y, cellRect.width,
						cellRect.height);

				// 画单元格
				TableCellRenderer renderer = table.getCellRenderer(
						minfo.startrow, minfo.startcolumn+1);
				Component component = table.prepareRenderer(renderer,
						minfo.startrow, minfo.startcolumn+1);
				rendererPane.paintComponent(g, component, table, cellRect.x,
						cellRect.y, cellRect.width, cellRect.height, true);

				g.setColor(oldc);

			}
		}

	}



	@Override
	protected void on_Focuscell() {
		frm.on_Focuscell();
	}

}
