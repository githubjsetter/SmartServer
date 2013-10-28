package com.smart.server.server.sysproc;

import java.io.File;

import com.smart.extension.mde.CMdeModelGeneral;
import com.smart.extension.ste.Zxconfig;
import com.smart.extension.ste.ZxzipReader;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.server.process.MdeProcessor;

public class MdegeneralProcessor extends MdeProcessor {
	Zxconfig zxconfig = null;
	File zxzip = null;

	public MdegeneralProcessor(String opid) throws Exception {
		zxzip = new File(CurrentappHelper.getClassesdir(), "专项开发/" + opid
				+ ".zip");
		if (!zxzip.exists())
			throw new Exception("找不到专项功能ID为" + opid + "的专项文件");
		ZxzipReader zxzr = new ZxzipReader();
		zxzr.readZxzip(zxzip);
		zxconfig = zxzr.getZxconfig();
		mdemodel = getMdeModel();
	}

	@Override
	protected String getDetailtablename() {
		return zxconfig.tablename1;
	}

	@Override
	protected String getMastertablename() {
		return zxconfig.tablename;
	}

	@Override
	protected CMdeModel getMdeModel() {
		if(zxconfig==null)return null;
		return new CMdeModelGeneral(null, zxconfig.opname, zxzip,
				zxconfig.opid, zxconfig.viewname, zxconfig.pkcolname,
				zxconfig.viewname1, zxconfig.fkcolname1);
	}
}
