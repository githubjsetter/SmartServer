package com.smart.platform.demo.ste;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CLinenoDisplayinfo;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.gui.ste.Steframe;

import java.awt.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-11
 * Time: 17:26:22
 * To change this template use File | Settings | File Templates.
 */
public class Pub_company_model extends CMasterModel {
    public Pub_company_model(CFrame owner,CMdeModel mdemodel) throws HeadlessException {
        super(owner, "��λ����Ƭ",mdemodel);
/*        DBColumnDisplayInfo editor = null;
        editor = new DBColumnDisplayInfo("companyid", "number", "��λ�ɣ�", false);
        editor.setIspk(true);
        editor.setReadonly(true);
        editor.setSeqname("PUB_COMPANY_COMPANYID_SEQ");
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("companyopcode", "varchar", "������", false);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);


        editor = new DBColumnDisplayInfo("companypinyin", "varchar", "ƴ��", true);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("companyname", "varchar", "����", false);
        formcolumndisplayinfos.add(editor);

*/
    }



    public String getTablename(){
        return "pub_company";
    }

    public String getSaveCommandString() {
        return "���浥λ";
    }
}
