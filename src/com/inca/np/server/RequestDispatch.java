package com.inca.np.server;

import com.inca.np.communicate.ParamCommand;
import com.inca.np.communicate.ServerResponse;
import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.StringCommand;
import com.inca.np.communicate.CommandBase;
import com.inca.np.auth.Userruninfo;
import com.inca.np.auth.UserManager;
import com.inca.np.util.DefaultNPParam;
import com.inca.np.util.StringUtil;
import com.inca.npserver.server.Server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.tools.zip.ZipFile;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 2007-3-26 Time: 16:37:13
 * 请求调度器
 * @deprecated 改用Server
 */
public class RequestDispatch {
	private RequestDispatch() {

	}

	private static RequestDispatch inst = null;

	public static synchronized RequestDispatch getInstance() {
		if (inst == null) {
			inst = new RequestDispatch();
			inst.loadProcess();
		}
		return inst;
	}

	void loadProcess() {
	}

	public ServerResponse process(ClientRequest req) {
		return Server.getInstance().process(req);
	}

	/**
	   @deprecated
	*/
	public static void addProcessClassname(String classname) {
	}
	
}
