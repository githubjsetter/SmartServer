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
        super(owner, "���ҹ���Ƭ",mdemodel);
/*        DBColumnDisplayInfo editor = null;
        editor = new DBColumnDisplayInfo("factoryid", "number", "���ңɣ�", false);
        editor.setIspk(true);
        editor.setReadonly(true);
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("factoryopcode", "varchar", "������", false);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);


        editor = new DBColumnDisplayInfo("factorypinyin", "varchar", "ƴ��", true);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("factoryname", "varchar", "��������", false);
        formcolumndisplayinfos.add(editor);

*/
        ////////////////////

    }



    public String getTablename(){
        return "pub_factory";
    }

    public String getSaveCommandString() {
        return "���泧��";
    }
}
