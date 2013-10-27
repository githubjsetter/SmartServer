package com.inca.np.demo.mde;

import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.gui.control.CFrame;
import com.inca.np.demo.ste.Pub_company_model;
import com.inca.np.demo.ste.Pub_factory_model;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-13
 * Time: 14:29:48
 * To change this template use File | Settings | File Templates.
 */
public class Pub_factory_mdemodel extends CMdeModel{
    public Pub_factory_mdemodel(CFrame frame, String title) {
        super(frame, title);
        setSaveimmdiate(true);
    }

    protected CMasterModel createMastermodel() {
        return new Pub_company_model(frame,this);
    }

    protected CDetailModel createDetailmodel() {
        return new Pub_factory_model(frame,this);
    }

    public String getMasterRelatecolname() {
        return "companyid";
    }

    public String getDetailRelatecolname() {
        return "factoryid";
    }


    public String getSaveCommandString() {
        return "保存单位厂家";
    }

	@Override
	protected int on_actionPerformed(String command) {
		System.out.println(command);
		return super.on_actionPerformed(command);
	}
    
}
