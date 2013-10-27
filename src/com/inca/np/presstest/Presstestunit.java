package com.inca.np.presstest;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Category;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.CommandBase;
import com.inca.np.communicate.DataCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.StringCommand;
import com.inca.np.gui.control.DBTableModel;
import com.inca.np.util.SendHelper;

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
