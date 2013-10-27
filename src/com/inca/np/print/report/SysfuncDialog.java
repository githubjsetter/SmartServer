package com.inca.np.print.report;

import com.inca.np.gui.control.CTable;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.DBTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.image.ColorModel;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-25
 * Time: 18:22:26
 * 报表系统函数选择
 */
public class SysfuncDialog extends JDialog implements MouseListener{
    private FuncTable table;

    public SysfuncDialog(Dialog owner) throws HeadlessException {
        super(owner, "选择系统函数",true);
        initDialog();
    }

    private void initDialog() {
        Container cp = this.getContentPane();

        Vector<DBColumnDisplayInfo> cols=new Vector<DBColumnDisplayInfo>();
        cols.add(new DBColumnDisplayInfo("func","varchar","函数"));
        cols.add(new DBColumnDisplayInfo("desc","varchar","说明"));

        DBTableModel dbmodel = new DBTableModel(cols);
        table = new FuncTable(dbmodel);

        cp.setLayout(new BorderLayout());
        JScrollPane sp = new JScrollPane(table);
        cp.add(sp,BorderLayout.CENTER);

        insertFuncvalue(dbmodel);

        table.addMouseListener(this);
        table.requestFocus();
    }



    private void insertFuncvalue(DBTableModel dbmodel) {
        String funcnamedescs[]={
            "today()","日期",
            "now()","时间",
        };

        for(int i=0;i<funcnamedescs.length;i+=2){
            dbmodel.appendRow();
            int r=dbmodel.getRowCount()-1;
            dbmodel.setItemValue(r,"func",funcnamedescs[i]);
            dbmodel.setItemValue(r,"desc",funcnamedescs[i+1]);
        }

        table.addRowSelectionInterval(0,0);
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        System.out.println("clickedrow="+row);
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    class FuncTable extends CTable{
        public FuncTable(TableModel dm) {
            super(dm);
            setReadonly(true);
        }
    }
}
