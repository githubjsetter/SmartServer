package com.inca.np.demo.server;

import org.apache.log4j.Category;

import com.inca.np.demo.mde.Pub_factory_mdemodel;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.server.process.MdeProcessor;
import com.inca.npserver.server.sysproc.SelectProcessor;

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
