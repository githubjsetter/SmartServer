package com.smart.platform.demo.server;

import com.smart.platform.demo.ste.Pub_goods_ste;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;
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
