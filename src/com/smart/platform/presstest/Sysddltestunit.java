package com.smart.platform.presstest;

import com.smart.platform.communicate.ClientRequest;
import com.smart.platform.communicate.StringCommand;

/**
 * 查询系统选项的测试单元
 * @author Administrator
 *
 */
public class Sysddltestunit extends Presstestunit{

	public Sysddltestunit() {
		super();
		ClientRequest req=new ClientRequest("查询系统选项字典");
		req.addCommand(new StringCommand("PUB_COMPANY_TYPE"));
		
		reqs.add(req);
		
	}

}
