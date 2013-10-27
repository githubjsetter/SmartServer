package com.inca.np.demo.mde;

import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.gui.mde.CMdeModel;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-18
 * Time: 16:16:19
 * To change this template use File | Settings | File Templates.
 */
public class gpcs_req_mdeframe extends MdeFrame{
    public gpcs_req_mdeframe() throws HeadlessException {
        super("请货单管理");
    }

    protected CMdeModel getMdeModel() {
        return new gpcs_req_mdemodel(this,"请货单管理");
    }


    public static void main(String[] argv){
        gpcs_req_mdeframe w=new gpcs_req_mdeframe();
        //FocusTraversalPolicy policy = w.getFocusTraversalPolicy();
        w.pack();
        w.setVisible(true);


    }

}
