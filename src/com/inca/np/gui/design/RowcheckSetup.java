package com.inca.np.gui.design;

import com.inca.np.gui.control.DBColumnDisplayInfo;

import javax.swing.*;
import javax.swing.table.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-29
 * Time: 13:29:52
 * To change this template use File | Settings | File Templates.
 */
public class RowcheckSetup extends JPanel {
    Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo = null;
    DesignFrame frame = null;

    String coltitles[] = {
        "字段名", "行检查","提示语"
    };

    TTable table = null;

    public RowcheckSetup(DesignFrame frame, Vector<DBColumnDisplayInfo> formdbcolumndisplayinfo) {
        this.frame = frame;
        this.formdbcolumndisplayinfo = formdbcolumndisplayinfo;
    }

    void autoSize(JTable table) {
        TableModel model = table.getModel();
        int size = model.getColumnCount();
        //	for all columns
        for (int c = 0; c < size; c++) {
            TableColumn column = table.getColumnModel().getColumn(c);
            //	Not displayed columns

            int width = 0;
            //	Header
            TableCellRenderer renderer = column.getHeaderRenderer();
            if (renderer == null)
                renderer = new DefaultTableCellRenderer();
            Component comp = null;
            if (renderer != null)
                comp = renderer.getTableCellRendererComponent
                        (table, column.getHeaderValue(), false, false, 0, 0);
            //
            if (comp != null) {
                width = comp.getPreferredSize().width;
                width = Math.max(width, comp.getWidth());

                //	Cells
                int col = column.getModelIndex();
                int maxRow = Math.min(20, table.getRowCount());
                try {
                    for (int row = 0; row < maxRow; row++) {
                        renderer = table.getCellRenderer(row, col);
                        comp = renderer.getTableCellRendererComponent
                                (table, table.getValueAt(row, col), false, false, row, col);
                        int rowWidth = comp.getPreferredSize().width;
                        width = Math.max(width, rowWidth);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //	Width not greater than 250
            }
            //
            column.setPreferredWidth(width + 15);
        }	//	for all columns
    }	//	autoSize

    public void createRowcheckPane(JPanel cp) {
        cp.removeAll();
        cp.setLayout(new BorderLayout());


        DefaultTableModel model = new DefaultTableModel(new Object[0][0], coltitles);
        table = new TTable(model);
        table.setRowHeight(27);
        JScrollPane sp = new JScrollPane(table);
        bindData(model);
        autoSize(table);


        TableColumnModel cm = table.getColumnModel();
        TableColumn column = cm.getColumn(1);
        String initvalues[] = {"", DBColumnDisplayInfo.ROWCHECK_NOTNULL};

        JComboBox cbrowcheck = new JComboBox(initvalues);
        //can't edit
        cbrowcheck.setEditable(false);
        column.setCellEditor(new DefaultCellEditor(cbrowcheck));
        cp.add(sp, BorderLayout.CENTER);

    }

    void bindData(DefaultTableModel model) {
        Vector data = model.getDataVector();
        data.removeAllElements();

        Enumeration<DBColumnDisplayInfo> en = formdbcolumndisplayinfo.elements();
        while (en.hasMoreElements()) {
            DBColumnDisplayInfo colinfo = en.nextElement();
            if (colinfo.getColname().equals("行号")) {
                continue;
            }
            Vector<String> record = new Vector<String>();
            record.setSize(coltitles.length);
            record.setElementAt(colinfo.getColname(), 0);
            record.setElementAt(colinfo.getRowcheck().getChecktype(),1);
            record.setElementAt(colinfo.getRowcheck().getInfomessage(),2);
            data.add(record);
        }
    }

    public void reverseBinddata() {
        for (int i = 0; i < table.getRowCount(); i++) {
            String checktype = (String) table.getValueAt(i, 1);
            String infomsg = (String) table.getValueAt(i, 2);
            DBColumnDisplayInfo colinfo = formdbcolumndisplayinfo.elementAt(i + 1);
            colinfo.getRowcheck().setChecktype(checktype);
            colinfo.getRowcheck().setInfomessage(infomsg);
        }
    }

    class TTable extends JTable {
        public TTable(TableModel dm) {
            super(dm);
        }

        public boolean isCellEditable(int row, int column) {
            if (column == 0 ) {
                return false;
            }
            return true;
        }
    }
}


