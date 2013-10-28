package com.smart.client.updatelog;

import java.awt.HeadlessException;

import com.smart.extension.ste.CSteModelAp;
import com.smart.platform.gui.control.CFrame;
import com.smart.platform.gui.control.DBColumnDisplayInfo;
import com.smart.platform.gui.ste.Hovdefine;

public class Updatelog_ste  extends CSteModelAp{

	public Updatelog_ste(CFrame frame, String title) throws HeadlessException {
		super(frame, title);
		DBColumnDisplayInfo col;
		col=getDBColumnDisplayInfo("userid");
		Hovdefine hovdef=new Hovdefine("com.inca.npclient.updatelog.Emphov","userid");
		hovdef.getColpairmap().put("employeeid","userid");
		hovdef.getColpairmap().put("employeename","username");
		col.setHovdefine(hovdef);
	}

	@Override
	public String getTablename() {
		return "np_update_log";
	}

	@Override
	public String getSaveCommandString() {
		return "²»ÄÜ±£´æ";
	}

	
}
