package com.smart.platform.print.report;

import com.smart.platform.gui.control.CTable;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.print.drawable.PColumnCell;
import com.smart.platform.print.drawable.PDataline;
import com.smart.platform.print.drawable.PReport;
import com.smart.platform.print.drawable.PageBody;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-5-13
 * Time: 13:38:06
 * 根据表格窗口的样式生成的报表
 */
public class BasicReport extends PReport{
    public void createDefaultReport(CSteModel stemodel) throws Exception{
        /**
         * 表格数据行高
         */
        int lineheight=11;
        setDefaulttitle(stemodel.getTitle());
        ppage.setDefaultTitle(stemodel.getTitle());

        PDataline pline = new PDataline();
        pline.setWithborder(true);
        pline.setBorderwidth(1);
        pline.setHeight(lineheight);
        pline.setX(20);

        PageBody pbody = new PageBody(this);
        pbody.setDataline(pline);
        pbody.setLineTitleheight(40);
        setPbody(pbody);

        Vector<PColumnCell> columns = new Vector<PColumnCell>();
        CTable ctable = stemodel.getTable();
        TableColumnModel cm = ctable.getColumnModel();
        for(int c=0;c<cm.getColumnCount();c++){
            TableColumn tc = cm.getColumn(c);
            String columntitle=(String) tc.getHeaderValue();
            DBColumnDisplayInfo coldispinfo = stemodel.getFormcolumndisplayinfos().elementAt(tc.getModelIndex());
            String colname=coldispinfo.getColname();
            int columnwidth=tc.getWidth();

            PColumnCell pcolcell = new PColumnCell(colname,columntitle,columnwidth);
            if(c==0){
                pcolcell.setFreeze(true);
            }
            columns.add(pcolcell);

        }
        pbody.setDatalineColumns(columns);

        setDbmodel(stemodel.getDBtableModel());
        setMasterdbmodel(stemodel.getDBtableModel(),0);
    }
}
