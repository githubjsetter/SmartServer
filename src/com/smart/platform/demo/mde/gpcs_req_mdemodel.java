package com.smart.platform.demo.mde;

import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMasterModel;
import com.smart.platform.gui.mde.CMdeModel;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-18
 * Time: 15:44:07
 * To change this template use File | Settings | File Templates.
 */
public class gpcs_req_mdemodel extends CMdeModel{
    public gpcs_req_mdemodel(CFrame frame, String title) {
        super(frame, title);
    }

    protected CMasterModel createMastermodel() {
        return new gpcs_reqsupply_stemodel(frame,this);
    }

    protected CDetailModel createDetailmodel() {
        return new gpcs_reqsupplydtl_stemodel(frame,this);
    }

    public String getMasterRelatecolname() {
        return "REQSUPPLYID";
    }

    public String getDetailRelatecolname() {
        return "REQSUPPLYID";
    }


    public String getSaveCommandString() {
        return "±£¥Ê«Îªıµ•";
    }
}
