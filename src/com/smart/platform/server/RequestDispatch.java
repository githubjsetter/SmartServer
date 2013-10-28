package com.smart.platform.server;

import com.smart.platform.auth.UserManager;
import com.smart.platform.auth.Userruninfo;
import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.CommandBase;
import com.smart.platform.communicate.ParamCommand;
import com.smart.platform.communicate.ServerResponse;
import com.smart.platform.communicate.StringCommand;
import com.smart.platform.util.DefaultNPParam;
import com.smart.platform.util.StringUtil;
import com.smart.server.server.Server;

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
