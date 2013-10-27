package com.inca.np.demo.mde;

import java.awt.HeadlessException;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.mde.CMdeModel;
import com.inca.np.gui.mde.MdeFrame;
import com.inca.np.util.DefaultNPParam;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-4-11
 * Time: 17:17:55
 * 单位　厂家管理
 */
public class pub_factory_mdeframe extends MdeFrame{
    public pub_factory_mdeframe() throws HeadlessException {
        super("单位-厂家管理");
    }

    protected CMdeModel getMdeModel() {
        return new Pub_factory_mdemodel(this,"单位-厂家管理");
    }


    public static void main(String[] argv){
        Userruninfo currentuser = new Userruninfo();
        currentuser.setUserid("23");
        ClientUserManager.setCurrentUser(currentuser);

        DefaultNPParam.develop=1;
        pub_factory_mdeframe w=new pub_factory_mdeframe();
        w.pack();
        w.setVisible(true);
    }

}
