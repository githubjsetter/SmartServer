package com.inca.np.gui.control;

import com.inca.np.image.IconFactory;
import com.inca.np.image.CIcon;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-4-18 Time: 10:13:24
 * To change this template use File | Settings | File Templates.
 */
public class CTableHeaderRender implements TableCellRenderer {

	ContentPane contentpane = new ContentPane();
	ContentPane contentpaneasc = new ContentPane(IconFactory.icup);
	ContentPane contentpanedesc = new ContentPane(IconFactory.icdown);

	public CTableHeaderRender() {
	}

	Font focusfont = null;

	public Component getTableCellRendererComponent(JTable jtable, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		CTable table = (CTable) jtable;
		if (table.getModel() instanceof DBTableModel) {
			int mindex = jtable.convertColumnIndexToModel(column);
			DBTableModel dbmodel = (DBTableModel) table.getModel();
			if(dbmodel==null){
				return new JLabel();
			}
			DBColumnDisplayInfo colinfo = dbmodel.getDisplaycolumninfos()
					.elementAt(mindex);
			value = colinfo.getTitle();
		}

		ContentPane cp = null;
		if (table.getSortcolumnindex() == column) {
			if (table.getSortmethod().equalsIgnoreCase("A")) {
				cp = contentpaneasc;
			} else {
				cp = contentpanedesc;
			}
		} else {
			cp = contentpane;
		}

		JLabel lb = cp.lb;
		lb.setText((String) value);

		if (isSelected) {
			lb.setForeground(table.getSelectionForeground());
			lb.setBackground(table.getSelectionBackground());
		} else {
			lb.setForeground(table.getForeground());
			lb.setBackground(table.getBackground());
		}

		if (table.isFocusOwner() || table.isEditing()) {
			if (focusfont == null) {
				Font f = table.getFont();
				focusfont = new Font(f.getName(), Font.BOLD, f.getSize());
			}
			lb.setFont(focusfont);
		} else {
			lb.setFont(table.getFont());
		}

		/*
		 * if (hasFocus) { Border border = null; if (isSelected) { border =
		 * UIManager.getBorder("Table.focusSelectedCellHighlightBorder"); } if
		 * (border == null) { border =
		 * UIManager.getBorder("Table.focusCellHighlightBorder"); }
		 * cp.setBorder(border);
		 * 
		 * if (!isSelected && table.isCellEditable(row, column)) { Color col;
		 * col = UIManager.getColor("Table.focusCellForeground"); if (col !=
		 * null) { lb.setForeground(col); } col =
		 * UIManager.getColor("Table.focusCellBackground"); if (col != null) {
		 * lb.setBackground(col); } } } else { cp.setBorder(getNoFocusBorder()); }
		 */

		LineBorder border = new LineBorder(Color.LIGHT_GRAY);
		cp.setBorder(border);

		TableColumn tablecolumn = table.getColumnModel().getColumn(column);
		int w = tablecolumn.getWidth();
		Dimension size = cp.getPreferredSize();
		// System.out.println("contentpane.getComponentCount()
		// "+contentpane.getComponentCount());
		size.setSize(w, 30);
		lb.setPreferredSize(size);

		return cp;
	}

	static Border getNoFocusBorder() {
		return UIManager.getBorder("TableHeader.cellBorder");
	}

	class ContentPane extends JPanel {
		JLabel lb = null;
		JLabel lbarrow = null;

		ContentPane() {
			BoxLayout boxlayout = new BoxLayout(this, BoxLayout.X_AXIS);
			this.setLayout(boxlayout);

			lb = new JLabel("");
			lb.setHorizontalAlignment(JLabel.CENTER);
			this.add(lb);
		}

		ContentPane(CIcon icon) {
			this();
			lbarrow = new JLabel(icon);
			this.add(lbarrow);
		}

		void freeMemory() {
			if (lb != null)
				lb = null;
			if (lbarrow != null)
				lbarrow = null;
		}
	}

	public void freeMemory() {
		// TODO Auto-generated method stub
		if (contentpane != null) {
			contentpane.freeMemory();
			contentpane = null;
		}
		if (contentpaneasc != null) {
			contentpaneasc.freeMemory();
			contentpaneasc = null;
		}
		if (contentpanedesc != null) {
			contentpanedesc.freeMemory();
			contentpanedesc = null;
		}
		focusfont = null;
	}
}
