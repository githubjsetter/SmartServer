package com.inca.np.gui.runop;

import java.io.File;

import org.apache.log4j.Category;

import com.inca.np.demo.communicate.RemotesqlHelper;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.gui.ste.COpframe;
import com.inca.np.gui.ste.Zxzipdownloader;
import com.inca.np.util.DefaultNPParam;
import com.inca.npclient.download.DownloadManager;
import com.inca.npclient.system.Clientframe;
import com.inca.npx.mde.MdeframeGeneral;
import com.inca.npx.ste.SteframeGeneral;

public class Oplauncher {
	static Category logger = Category.getInstance(Oplauncher.class);

	public static COpframe loadOp(String opid) throws Exception {
		Clientframe clientfrm=Clientframe.getClientframe();
		if(clientfrm!=null){
			return clientfrm.runOp(opid, true);
		}
		
		
		String sql = "select opid,opname,classname,prodname,modulename from np_op where opid="
				+ opid;
		RemotesqlHelper sqlh = new RemotesqlHelper();
		DBTableModel dbmodel = sqlh.doSelect(sql, 0, 1);
		if (dbmodel.getRowCount() == 0)
			throw new Exception("没有功能ID" + opid);

		String prodname = dbmodel.getItemValue(0, "prodname");
		String modulename = dbmodel.getItemValue(0, "modulename");
		String classname = dbmodel.getItemValue(0, "classname");

		if (classname.equals("stegeneral") || classname.equals("mdegeneral")) {
			// 下载STE专项
			Zxzipdownloader zxzipdl = new Zxzipdownloader();
			File zxzipfile = DownloadManager.getInst().getZxfile(opid);
			if (classname.equals("stegeneral")) {
				SteframeGeneral frm = new SteframeGeneral(zxzipfile);
				frm.pack();
				return frm;
			} else if (classname.equals("mdegeneral")) {
				MdeframeGeneral frm = new MdeframeGeneral(zxzipfile);
				frm.pack();
				return frm;
			}

		} else {
			// 要加载一个新的类.需要检查模块的JAR文件
			DownloadManager dlm = DownloadManager.getInst();
			dlm.prepareModulejar(prodname, modulename);
		}
		Class aClass = Class.forName(classname, true,
				DefaultNPParam.classloader);
		Object instance = aClass.newInstance();
		if (instance instanceof COpframe) {
			COpframe frm = (COpframe) instance;
			frm.setOpid(opid);
			frm.pack();
			return frm;
		} else {
			throw new Exception(instance + "不是COpFrame");
		}

	}
}
