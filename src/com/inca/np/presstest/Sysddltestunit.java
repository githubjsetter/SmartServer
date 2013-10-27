package com.inca.np.presstest;

import com.inca.np.communicate.ClientRequest;
import com.inca.np.communicate.StringCommand;

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
