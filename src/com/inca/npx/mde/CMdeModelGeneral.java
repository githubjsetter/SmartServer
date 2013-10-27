package com.inca.npx.mde;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.inca.np.gui.control.CFrame;
import com.inca.np.gui.control.DBColumnDisplayInfo;
import com.inca.np.gui.mde.CDetailModel;
import com.inca.np.gui.mde.CMasterModel;
import com.inca.np.gui.ste.DBColumnInfoStoreHelp;
import com.inca.np.rule.define.Rulebase;
import com.inca.np.rule.enginee.Ruleenginee;
import com.inca.np.rule.setup.RuleRepository;
import com.inca.np.util.ZipHelper;
import com.inca.npx.ste.ZxmodifyUploadHelper;

public class CMdeModelGeneral extends CMdeModelAp {
	File zxzipfile = null;
	String viewname = "";
	String pkcolname = "";
	String viewname1 = "";
	String fkcolname1 = "";
	

	public CMdeModelGeneral(CFrame frame, String title,File zxzipfile,
			String opid,String viewname,String pkcolname,String viewname1,String fkcolname1) {
		super();
		
		this.frame = frame;
		this.title = title;
		setOpid(opid);
		this.viewname=viewname;
		this.pkcolname=pkcolname;
		this.viewname1=viewname1;
		this.fkcolname1=fkcolname1;
		this.zxzipfile=zxzipfile;
		this.setZxmodify(true);

		initDelegate();
		loadRuleenginee();

		mastermodel = createMastermodel();
		mastermodel.addActionListener(new MasterModelListener());
		mastermodel.setRuleeng(ruleeng);

		detailmodel = createDetailmodel();
		detailmodel.addActionListener(new DetailModelListener());
		detailmodel.setRuleeng(ruleeng);

		masterrelatecolname = getMasterRelatecolname();
		detailrelatecolname = getDetailRelatecolname();
		
		setUseap(true);
		getMasterModel().setUseap(true);
		getDetailModel().setUseap(true);
	}

	@Override
	protected CDetailModel createDetailmodel() {
		return new CDetailModelGeneral(frame, title + "细单", this, opid,viewname1,
				zxzipfile);
	}

	@Override
	protected CMasterModel createMastermodel() {
		return new CMasterModelGeneral(frame, title + "细单", this, opid,viewname,
				zxzipfile);
	}

	@Override
	public String getDetailRelatecolname() {
		return fkcolname1;
	}

	@Override
	public String getMasterRelatecolname() {
		// TODO Auto-generated method stub
		return pkcolname;
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return "mdegeneral:"+opid;
	}

	@Override
	protected void loadRuleenginee() {
		File temprulefile = null;
		BufferedReader rd = null;
		try {
			temprulefile = File.createTempFile("temp", ".rule");
			if (!ZipHelper.extractFile(zxzipfile, "mde.rule", temprulefile)) {
				return;
			}
			rd = DBColumnInfoStoreHelp.getReaderFromFile(temprulefile);
			Vector<Rulebase> rules = RuleRepository.loadRules(rd);
			ruleeng = new Ruleenginee();
			ruleeng.setRuletable(rules);
		} catch (Exception e) {
			logger.error("load rule", e);
		} finally {
			if (rd != null)
				try {
					rd.close();
				} catch (IOException e) {
				}
			if (temprulefile != null) {
				temprulefile.delete();
			}

		}
		
	}
	
	public boolean uploadUI(){
		// 上传
		ZxmodifyUploadHelper zu = new ZxmodifyUploadHelper();
		if (!zu.uploadZxfile(opid, zxzipfile)) {
			logger.error("上传错误:"+zu.getErrormessage());
			errorMessage("上传错误", zu.getErrormessage());
			return false;
		}
		return true;
	}

	
}
