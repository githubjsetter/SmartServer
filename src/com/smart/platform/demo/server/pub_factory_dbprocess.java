package com.smart.platform.demo.server;

import org.apache.log4j.Category;

import com.smart.platform.demo.mde.Pub_factory_mdemodel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;
import com.smart.server.server.sysproc.SelectProcessor;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-12
 * Time: 13:56:01
 * To change this template use File | Settings | File Templates.
 */
public class pub_factory_dbprocess extends MdeProcessor {
    protected Category logger = Category.getInstance(SelectProcessor.class);



    protected CMdeModel getMdeModel() {
        return new Pub_factory_mdemodel(null,"");
    }

    protected String getMastertablename() {
        return "pub_company";
    }

    protected String getDetailtablename() {
        return "pub_factory";
    }
}
