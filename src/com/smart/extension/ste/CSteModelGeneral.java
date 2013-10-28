package com.smart.extension.ste;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.tools.zip.ZipOutputStream;

import com.smart.platform.communicate.BinfileCommand;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.filedb.CurrentdirHelper;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CLinenoDisplayinfo;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;
import com.smart.platform.rule.define.Rulebase;
import com.smart.platform.rule.enginee.Ruleenginee;
import com.smart.platform.rule.setup.RuleRepository;
import com.smart.platform.rule.setup.RulesetupMaindialog;
import com.smart.platform.util.SendHelper;
import com.smart.platform.util.ZipHelper;

public class CSteModelGeneral extends CSteModelAp {

	File zxzipfile = null;
	String viewname = "";

	public CSteModelGeneral(CFrame frame, String title, String opid,
			String viewname, File zxzipfile) throws HeadlessException {
		this.frame = frame;
		this.title = title;
		this.viewname = viewname;
		this.zxzipfile = zxzipfile;
		this.setOpid(opid);

		
		// 加载专项 
		
		initInitdelegate();
		loadDBColumnInfos();
		loadRuleenginee();

		if (ruleeng != null) {
			ruleeng.process(this, "设置下拉选择");
			ruleeng.process(this, "设置系统下拉选择");
			ruleeng.process(this, "设置SQL下拉选择");
			ruleeng.process(this, "表格可以编辑");
		}

		if (initdelegate != null) {
			initdelegate.on_init(this);
		}

		DBColumnDisplayInfo colinfo = getDBColumnDisplayInfo("filegroupid");
		useattachfile = colinfo != null;
		useap = true;
	}

	@Override
	protected void loadDBColumnInfos() {
		formcolumndisplayinfos = new Vector<DBColumnDisplayInfo>();
		formcolumndisplayinfos.add(new CLinenoDisplayinfo());
		File tempmodelfile = null;
		try {
			tempmodelfile = File.createTempFile("temp", ".model");
			String filename = "ste.model";
			if (!ZipHelper.extractFile(zxzipfile, filename, tempmodelfile)) {
				return;
			}
			DBColumnInfoStoreHelp.readFile(this, tempmodelfile);
			orgdbmodelcols=new Vector<DBColumnDisplayInfo>();
			orgdbmodelcols.addAll(formcolumndisplayinfos);

		} catch (Exception e) {
			logger.error("load ", e);
			errorMessage("错误", e.getMessage());
		} finally {
			if (tempmodelfile != null) {
				tempmodelfile.delete();
			}
		}

	}

	@Override
	protected void loadRuleenginee() {
		File temprulefile = null;
		BufferedReader rd = null;
		try {
			temprulefile = File.createTempFile("temp", ".rule");
			if (!ZipHelper.extractFile(zxzipfile, "ste.rule", temprulefile)) {
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

	@Override
	public String getTablename() {
		// TODO Auto-generated method stub
		return viewname;
	}

	@Override
	public String getSaveCommandString() {
		// TODO Auto-generated method stub
		return "stegeneral:" + opid;
	}

	@Override
	public void saveUI() {
		File outf = null;
		File zxzip = null;
		try {
			outf = File.createTempFile("temp", "model");
			DBColumnInfoStoreHelp.writeFile(this, outf);

			File dir = CurrentdirHelper.getZxdir();
			zxzip = new File(dir, opid + ".zip");
			ZipHelper.replaceZipfile(zxzip, "ste.model", outf);

		} catch (Exception e) {
			logger.error("saveUI", e);
			return;
		} finally {
			if (outf != null) {
				outf.delete();
			}
		}

		if (zxzip != null && zxzip.exists()) {
			/*
			 * int ret = JOptionPane.showConfirmDialog(frame,
			 * "已完成调整，要将专项开发包上专服务器吗？"); if (ret != JOptionPane.OK_OPTION)
			 * return;
			 */try {
				installZx(zxzip);
			} catch (Exception e) {
				logger.error("err", e);
				errorMessage("错误", e.getMessage());
			}
		}

	}

	@Override
	public void doSetuprule() {
		// Ruleenginee ruleeng, Object caller, String optype,
		// File savefile
		if (ruleeng == null) {
			ruleeng = new Ruleenginee();
		}

		/*
		 * String classname = this.getClass().getName(); int p =
		 * classname.lastIndexOf("."); if (p > 0) { classname =
		 * classname.substring(p + 1); }
		 */
		
		String optype="ste";
		if(this instanceof CReportmodelGeneral){
			optype="report";
		}
		
		RulesetupMaindialog setupdlg = new RulesetupMaindialog(this
				.getParentFrame(), ruleeng, this, optype);
		setupdlg.pack();
		setupdlg.setVisible(true);
		if (!setupdlg.getOk())
			return;

		File outf = null;
		File zxzip = null;
		try {
			outf = File.createTempFile("temp", "rule");
			RuleRepository.saveRule(outf, ruleeng.getRuletable());

			File dir = CurrentdirHelper.getZxdir();
			zxzip = new File(dir, opid + ".zip");
			ZipHelper.replaceZipfile(zxzip, "ste.rule", outf);

		} catch (Exception e) {
			logger.error("setuprule", e);
			return;
		} finally {
			if (outf != null) {
				outf.delete();
			}
		}

		if (zxzip != null && zxzip.exists()) {
			try {
				installZx(zxzip);
			} catch (Exception e) {
				logger.error("err", e);
				errorMessage("错误", e.getMessage());
			}
		}

	}

	void installZx(File f) throws Exception {
		ZxUploadHelper zu = new ZxUploadHelper();
		if (!zu.uploadZxfile(f)) {
			throw new Exception(zu.getErrormessage());
		}
	}

}
