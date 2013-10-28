package com.smart.platform.demo.ste;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CLinenoDisplayinfo;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;

import java.awt.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-11
 * Time: 17:33:49
 * To change this template use File | Settings | File Templates.
 */
public class Pub_factory_model  extends CDetailModel {
    public Pub_factory_model(CFrame owner,CMdeModel mdemodel) throws HeadlessException {
        super(owner, "厂家管理卡片",mdemodel);
/*        DBColumnDisplayInfo editor = null;
        editor = new DBColumnDisplayInfo("factoryid", "number", "厂家ＩＤ", false);
        editor.setIspk(true);
        editor.setReadonly(true);
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("factoryopcode", "varchar", "操作码", false);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);


        editor = new DBColumnDisplayInfo("factorypinyin", "varchar", "拼音", true);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("factoryname", "varchar", "厂家名称", false);
        formcolumndisplayinfos.add(editor);

*/
        ////////////////////

    }



    public String getTablename(){
        return "pub_factory";
    }

    public String getSaveCommandString() {
        return "保存厂家";
    }
}
