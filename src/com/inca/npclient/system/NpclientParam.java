package com.inca.npclient.system;

import com.inca.np.util.DefaultNPParam;

/**
 * 参数文件
 * @author Administrator
 *
 */
public class NpclientParam {

	static{
		DefaultNPParam.debug = 0;
		DefaultNPParam.develop=0;
		DefaultNPParam.debugdbip = "192.9.200.1";
		DefaultNPParam.debugdbsid = "data";
		DefaultNPParam.debugdbusrname = "xjxty";
		DefaultNPParam.debugdbpasswd = "xjxty";
		DefaultNPParam.defaultappsvrurl = "http://127.0.0.1/npserver/clientrequest.do";
		DefaultNPParam.prodcontext="npserver";
		DefaultNPParam.depttable_use_pub_dept=false;
	}
}
