package com.inca.np.demo.ste;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.control.CLinenoDisplayinfo;
import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CMdeModel;

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
        super(owner, "单位管理卡片",mdemodel);
/*        DBColumnDisplayInfo editor = null;
        editor = new DBColumnDisplayInfo("companyid", "number", "单位ＩＤ", false);
        editor.setIspk(true);
        editor.setReadonly(true);
        editor.setSeqname("PUB_COMPANY_COMPANYID_SEQ");
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("companyopcode", "varchar", "操作码", false);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);


        editor = new DBColumnDisplayInfo("companypinyin", "varchar", "拼音", true);
        editor.setUppercase(true);
        formcolumndisplayinfos.add(editor);

        editor = new DBColumnDisplayInfo("companyname", "varchar", "名称", false);
        formcolumndisplayinfos.add(editor);

*/
    }



    public String getTablename(){
        return "pub_company";
    }

    public String getSaveCommandString() {
        return "保存单位";
    }
}
