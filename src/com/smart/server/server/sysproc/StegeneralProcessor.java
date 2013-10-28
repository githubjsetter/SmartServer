package com.smart.server.server.sysproc;

import java.io.File;

import com.smart.extension.ste.CSteModelGeneral;
import com.smart.extension.ste.Zxconfig;
import com.smart.extension.ste.ZxzipReader;
import com.smart.platform.gui.ste.CSteModel;
import com.smart.platform.server.process.SteProcessor;

/**
 * 本处理器不可多线程使用。
 * 
 * @author Administrator
 * 
 */
public class StegeneralProcessor extends SteProcessor {

	Zxconfig zxconfig=null;
	File zxzip = null;

	public StegeneralProcessor(String opid) throws Exception {
		zxzip = new File(CurrentappHelper.getClassesdir(), "专项开发/" + opid
				+ ".zip");
		if (!zxzip.exists())
			throw new Exception("找不到专项功能ID为" + opid + "的专项文件");
		ZxzipReader zxzr = new ZxzipReader();
		zxzr.readZxzip(zxzip);
		zxconfig = zxzr.getZxconfig();
		stemodel = getSteModel();
	}

	@Override
	protected CSteModel getSteModel() {
		if(zxconfig==null){
			return null;
		}
		CSteModelGeneral ste=new CSteModelGeneral(null,zxconfig.opname,zxconfig.opid,
				zxconfig.viewname,zxzip);
		return ste;
	}

	@Override
	protected String getTablename() {
		return zxconfig.tablename;
	}

}
