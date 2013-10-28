package com.smart.platform.presstest;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.DataCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.gui.control.DBTableModel;
import com.smart.platform.util.SendHelper;

/**
 * ²âÊÔµ¥Ôª
 * 
 * @author Administrator
 * 
 */
public class Presstestunit {
	Category logger = Category.getInstance(Presstestunit.class);

	protected Vector<ClientRequest> reqs = new Vector<ClientRequest>();

	public Vector<ClientRequest> getClientrequests() {
		return reqs;
	}

	public boolean test() throws Exception {
		Enumeration<ClientRequest> en = reqs.elements();
		while (en.hasMoreElements()) {
			ClientRequest req = en.nextElement();
			ServerResponse resp = SendHelper.sendRequest(req);
			CommandBase cmd0=resp.commandAt(0);
			if(cmd0 instanceof StringCommand){
				String respcmd=((StringCommand)cmd0).getString();
				if(!respcmd.startsWith("+OK")){
					throw new Exception(respcmd);
				}
			}
		}
		return true;
	}
}
