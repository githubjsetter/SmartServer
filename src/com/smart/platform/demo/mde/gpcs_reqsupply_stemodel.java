package com.smart.platform.demo.mde;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-18
 * Time: 15:45:02
 * To change this template use File | Settings | File Templates.
 */
public class gpcs_reqsupply_stemodel extends CMasterModel{
    public gpcs_reqsupply_stemodel(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
        super(frame, "请货单管理", mdemodel);
/*

        DBColumnDisplayInfo editor = null;
        editor = new DBColumnDisplayInfo("REQSUPPLYID", "number", "请货单ID", false);
        editor.setIspk(true);
        editor.setReadonly(true);
        editor.setSeqname("GPCS_REQSUPPLYID_SEQ");
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("PLACEPOINTID", "number", "配送中心ID", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("PLACEPOINTNAME", "varchar", "配送中心", true);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("REQSTORAGEID", "number", "保管帐ID", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("STORAGENAME", "varchar", "保管帐", true);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("REQMANID", "number", "录入人ID", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("EMPLOYEENAME", "varchar", "录入人", true);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("REQDATE", "date", "请货日期", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("REQLASTDATE", "date", "请货日期", true);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("USESTATUS", "number", "单据状态", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("PLACECENTERID", "number", "配送中心ID", false);
        editor.setReadonly(true);
        editor.setUpdateable(false);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("PLACEPRIORITY", "number", "PLACEPRIORITY", true);
        editor.setReadonly(true);
        editor.setUpdateable(false);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("MEMO", "varchar", "备注", true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("QUONDAMID", "number", "QUONDAMID", true);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        this.formcolumndisplayinfos=tablecolumndisplayinfos;
*/

    }

    public String getTablename() {
        return "GPCS_REQSUPPLY_V";
    }

    public String getSaveCommandString() {
        return null;
    }
}
