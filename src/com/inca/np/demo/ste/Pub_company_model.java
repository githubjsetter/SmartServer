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
