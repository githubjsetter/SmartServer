package com.inca.np.demo.ste;

import java.awt.Color;
import java.awt.HeadlessException;

import javax.swing.JList;

import com.inca.np.auth.ClientUserManager;
import com.inca.np.auth.Userruninfo;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.ste.CSteModel;
import com.inca.np.gui.ste.Steframe;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MdeGeneralTool;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2007-3-28
 * Time: 15:58:28
 * 商品管理
 */
public class Pub_goods_frame extends Steframe{
    public Pub_goods_frame() throws HeadlessException {
        super("货品管理");  
    }

    protected CSteModel getStemodel() {
        return new Pub_goods_ste(this);
    }




    public static void main(String[] argv){
        new DefaultNPParam();
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;
		DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";
        
/*        MdeGeneralTool frm=new MdeGeneralTool();
        frm.pack();
        frm.setVisible(true);
        
        if(true)return;
*/        
        Userruninfo currentuser = new Userruninfo();
        currentuser.setUserid("23");
        ClientUserManager.setCurrentUser(currentuser);

        
        Pub_goods_frame g=new Pub_goods_frame();
        g.pack();
        g.setVisible(true);
        
        while(true){
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
        }
    }

}
