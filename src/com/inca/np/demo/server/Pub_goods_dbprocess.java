package com.inca.np.demo.server;

import com.inca.np.server.process.SteProcessor;
import com.inca.np.demo.ste.Pub_goods_ste;
import com.inca.np.gui.ste.CSteModel;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-3
 * Time: 15:54:06
 * To change this template use File | Settings | File Templates.
 */
public class Pub_goods_dbprocess extends SteProcessor {
    protected CSteModel getSteModel() {
        return new Pub_goods_ste(null);
    }

    protected String getTablename() {
        return "pub_goods";
    }

}
