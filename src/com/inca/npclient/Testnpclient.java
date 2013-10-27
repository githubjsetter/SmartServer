package com.inca.npclient;

import java.io.File;
import java.util.Properties;

import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.MdeGeneralTool;
import com.inca.npworkflow.server.WfEngine;

public class Testnpclient {
	public static void main(String[] args) {
/*		Properties prop=System.getProperties();
		String userhome=(String)prop.get("user.home");
		System.out.println(userhome);
		
		if(true){
			System.exit(0);
		}
*/
		
		new File("bin").mkdirs();
		new File("logs").mkdirs();
		DefaultNPParam.debug=1;
		DefaultNPParam.develop=1;

		DefaultNPParam.debugdbip = "127.0.0.1";
		DefaultNPParam.debugdbpasswd = "xmlyerp";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "xmlyerp";
		DefaultNPParam.prodcontext = "npserver";

/*		DefaultNPParam.debugdbip = "192.9.200.63";
		DefaultNPParam.debugdbpasswd = "gtyy0526";
		DefaultNPParam.debugdbsid = "orcl";
		DefaultNPParam.debugdbusrname = "gtyy0526";
		DefaultNPParam.prodcontext = "npserver";

		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbpasswd = "skLock";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "sk";
		DefaultNPParam.prodcontext = "npserver";

*/		
		
		WfEngine.getInstance();
		//WfEngine.getInstance().startWorkflow(tablename, pkvalue)
		
		
		/*DefaultNPParam.debugdbip = "192.9.200.89";
		DefaultNPParam.debugdbpasswd = "database2";
		DefaultNPParam.debugdbsid = "pb";
		DefaultNPParam.debugdbusrname = "database2";
		DefaultNPParam.prodcontext = "npserver";
		*/
		//MdeGeneralTool mg=new MdeGeneralTool();
		//mg.pack();
		//mg.setVisible(true);
		
		//System.out.println("entryid="+ClientUserManager.getCurrentUser().getEntryid());
		//System.out.println("placepointid="+ClientUserManager.getCurrentUser().getPlacepointid());
		Startnpclient dlg=new Startnpclient();
		dlg.pack();
		dlg.setVisible(true);
		
	}

}
