package com.inca.np.demo.mde;

import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.control.*;
import com.inca.np.demo.hov.Pub_goods_hov;
import com.inca.np.communicate.RemoteGoodsdetailHelper;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

import org.apache.log4j.Category;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-18
 * Time: 16:05:19
 * To change this template use File | Settings | File Templates.
 */
public class gpcs_reqsupplydtl_stemodel extends CDetailModel {
    public gpcs_reqsupplydtl_stemodel(CFrame frame, CMdeModel mdemodel) throws HeadlessException {
        super(frame, "���ϸ��", mdemodel);
/*
        DBColumnDisplayInfo editor = null;

        editor = new DBColumnDisplayInfo("GOODSID", "number", "��ƷID", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("GOODSOPCODE", "varchar", "������", false);
        editor.setUpdateable(false);
        editor.setUppercase(true);
        editor.setUsehov(new Pub_goods_hov(),"opcode");
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("GOODSNAME", "varchar", "Ʒ��", true);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("PRODAREA", "varchar", "����", false);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("GOODSTYPE", "varchar", "�ͺ�", true);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);


        editor = new DBColumnDisplayInfo("GOODSUSEQTY", "number", "ʹ�õ�λ����", false);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("GOODSUSEUNIT", "varchar", "ʹ�õ�λ", true);
        editor.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("GOODSDTLID", "number", "��ϸID", false);
        editor.setEditcomptype(DBColumnDisplayInfo.EDITCOMP_COMBOBOX);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("PACKNAME", "varchar", "��װ��", false);
        editor.setReadonly(true);
        editor.setUpdateable(false);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("PACKSIZE", "number", "��װ��С", true);
        editor.setReadonly(true);
        editor.setUpdateable(false);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("TOTALGOODSQTY", "number", "������", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("TOTALREFUSEQTY", "number", "��������", true);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("VALIDREQQTY", "number", "VALIDREQQTY", false);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("CHANGEABLE", "number", "CHANGEABLE", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("REQSPLITFLAG", "number", "REQSPLITFLAG", true);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("REQSTATUS", "number", "REQSTATUS", true);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("REQSUPPLYDTLID", "number", "���ϸ��ID", false);
        editor.setIspk(true);
        editor.setReadonly(true);
        editor.setSeqname("GPCS_REQSUPPLYDTLID_SEQ");
        tablecolumndisplayinfos.add(editor);


        editor = new DBColumnDisplayInfo("REQSUPPLYID", "number", "�����ID", true);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("BUSIDTLID", "number", "BUSIDTLID", false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("BUSIDTLNAME", "varchar", "BUSIDTLNAME", false);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("BUSIDTLUNITNAME", "varchar", "BUSIDTLUNITNAME", true);
        editor.setUpdateable(false);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("BUSIDTLQTY", "number", "BUSIDTLQTY", true);
        editor.setReadonly(true);
        tablecolumndisplayinfos.add(editor);

        this.formcolumndisplayinfos = tablecolumndisplayinfos;
*/
    }

    public String getTablename() {
        return "GPCS_REQSUPPLYDTL_V";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getSaveCommandString() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
