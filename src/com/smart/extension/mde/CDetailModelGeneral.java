package com.smart.extension.mde;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Vector;





import org.apache.log4j.Category;

import com.smart.extension.ste.ZxmodifyUploadHelper;
import com.smart.platform.filedb.CurrentdirHelper;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.CLinenoDisplayinfo;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.mde.CDetailModel;
import com.smart.platform.gui.mde.CMdeModel;
import com.smart.platform.gui.ste.DBColumnInfoStoreHelp;
import com.smart.platform.util.ZipHelper;

public class CDetailModelGeneral extends CDetailModel {
	Category logger=Category.getInstance(CDetailModelGeneral.class);

	File zxzipfile = null;
	String viewname = "";
	

	protected CDetailModelGeneral(CFrame frame, String title,
			CMdeModel mdemodel, String opid,String viewname, File zxzipfile)
			throws HeadlessException {
		super();
		this.mdemodel = mdemodel;
		this.frame = frame;
		this.title = title;
		this.viewname = viewname;
		this.zxzipfile = zxzipfile;
		setOpid(opid);

		/*
		 * // ¼ÓÔØ×¨Ïî initInitdelegate();
		 */
		loadDBColumnInfos();

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
			String filename="ste1.model";
			if (!ZipHelper.extractFile(zxzipfile, filename , tempmodelfile)) {
				return;
			}
			DBColumnInfoStoreHelp.readFile(this, tempmodelfile);
			orgdbmodelcols=new Vector<DBColumnDisplayInfo>();
			orgdbmodelcols.addAll(formcolumndisplayinfos);
		} catch (Exception e) {
			logger.error("load ", e);
			errorMessage("´íÎó", e.getMessage());
		} finally {
			if (tempmodelfile != null) {
				tempmodelfile.delete();
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
		return null;
	}

}
