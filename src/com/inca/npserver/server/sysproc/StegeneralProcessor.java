package com.inca.npserver.server.sysproc;

import java.io.File;

import com.inca.np.gui.ste.CSteModel;
import com.inca.np.server.process.SteProcessor;
import com.inca.npx.ste.CSteModelGeneral;
import com.inca.npx.ste.Zxconfig;
import com.inca.npx.ste.ZxzipReader;

/**
 * �����������ɶ��߳�ʹ�á�
 * 
 * @author Administrator
 * 
 */
public class StegeneralProcessor extends SteProcessor {

	Zxconfig zxconfig=null;
	File zxzip = null;

	public StegeneralProcessor(String opid) throws Exception {
		zxzip = new File(CurrentappHelper.getClassesdir(), "ר���/" + opid
				+ ".zip");
		if (!zxzip.exists())
			throw new Exception("�Ҳ���ר���IDΪ" + opid + "��ר���ļ�");
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
